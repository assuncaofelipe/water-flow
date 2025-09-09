package home.felipe.water.pocket.analysis.ui.screens.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.repository.CsvRepository
import home.felipe.domain.repository.TFLiteRepository
import home.felipe.domain.vo.FeatureMeta
import home.felipe.water.pocket.analysis.FlowState
import home.felipe.water.pocket.analysis.ui.models.RecentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.Normalizer
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val csvRepository: CsvRepository,
    private val tflite: TFLiteRepository,
    private val flow: FlowState
) : ViewModel() {

    private val ioDispatchers = Job() + Dispatchers.Default
    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui

    fun onImportCsvUris(uris: List<Uri>, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            _ui.emit(_ui.value.copy(loading = true, error = null))
            try {
                val selectedUri = uris.first()
                Timber.d("URI selected: $selectedUri")

                // 1) Read CSV (IO)
                val (fileName, records) = withContext(ioDispatchers) {
                    csvRepository.readCsvFromUri(app.contentResolver, selectedUri)
                }
                Timber.d("CSV read: name=$fileName rows=${records.size}")

                flow.fileName = fileName
                flow.records = records
                flow.dates = records.map { it.date }

                // Union of numeric headers across all rows
                val csvHeaders: Set<String> = records.asSequence()
                    .flatMap { it.values.keys.asSequence() }
                    .toSet()
                Timber.d("Headers(${csvHeaders.size}): ${csvHeaders.joinToString()}")

                // Build normalized index (matches Python cleaner)
                val normIndex: Map<String, String> =
                    csvHeaders.associateBy { normalizeHeaderLikePython(it) }

                // 2) List metadata targets available in assets
                val metaTargets: List<String> = withContext(ioDispatchers) {
                    app.assets.list("metadata")
                        ?.filter { it.endsWith(".json", ignoreCase = true) }
                        ?.map { it.removeSuffix(".json") }
                        .orEmpty()
                }
                Timber.d("Meta targets found: ${metaTargets.joinToString()}")

                // 3) Build mappings for every target (NO gate)
                val builtTargets = mutableListOf<String>()
                val builtMaps = mutableMapOf<String, Map<String, String>>()

                withContext(ioDispatchers) {
                    metaTargets.forEach { targetName ->
                        val meta = runCatching {
                            tflite.loadFeatureMeta("metadata/$targetName.json")
                        }.onFailure { e ->
                            Timber.e(e, "Failed to load metadata for target=$targetName")
                        }.getOrNull() ?: return@forEach

                        val (mapping, matched) = mapTarget(meta, csvHeaders, normIndex)

                        // Log coverage + small sample of mapping
                        val sample = mapping.entries.take(4)
                            .joinToString { "${it.key}→${it.value.ifEmpty { "(miss)" }}" }
                        Timber.d("Target $targetName coverage=$matched/${meta.features_order.size} sample=[$sample]")

                        builtTargets += targetName // always include
                        builtMaps[targetName] = mapping
                    }
                }

                flow.targetsAvailable = builtTargets.sorted()
                flow.selectedTargets = flow.targetsAvailable
                flow.headerMapsByTarget.clear()
                flow.headerMapsByTarget.putAll(builtMaps)

                Timber.d("Compatible targets (no gate): ${flow.targetsAvailable.joinToString()}")

                // 4) Update recents + finish
                val rf = RecentFile(name = fileName, rows = records.size, cols = csvHeaders.size)
                _ui.emit(
                    HomeUiState(
                        recents = listOf(rf) + _ui.value.recents.take(9),
                        loading = false,
                        error = null
                    )
                )
                onDone?.invoke()
            } catch (t: Throwable) {
                Timber.e(t, "Error in onImportCsvUris")
                _ui.emit(_ui.value.copy(loading = false, error = t.message ?: "Import failed"))
            }
        }
    }

    fun onOpenRecent(recentFile: RecentFile, onDone: (() -> Unit)? = null) {
        viewModelScope.launch { onDone?.invoke() }
    }

    /** === Helpers === */

    // Same normalization as the Python cleaner (µ→u, NFKD, E.coli, collapse spaces).
    private fun normalizeHeaderLikePython(text: String): String {
        var x = text
        x = x.replace("µ", "u")
            .replace("μ", "u")
            .replace("°", "")
            .replace("º", "")
            .replace("–", "-")
            .replace("—", "-")
            .replace("×", "x")
        x = Normalizer.normalize(x, Normalizer.Form.NFKD)
        x = x.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        x = x.replace(Regex("\\bE\\s*\\.?\\s*coli\\b", RegexOption.IGNORE_CASE), "E.coli")
        x = x.replace("\\s+".toRegex(), " ").trim()
        return x
    }

    // Build a mapping for a single target using exact match + normalized fallback
    private fun mapTarget(
        meta: FeatureMeta,
        csvHeaders: Set<String>,
        normIndex: Map<String, String>
    ): Pair<Map<String, String>, Int> {
        val mapping = mutableMapOf<String, String>()
        var matched = 0

        meta.features_order.forEach { canonical ->
            val pref = meta.csvHeaderMap?.get(canonical)

            // (1) exact
            var chosen: String? = if (pref != null && csvHeaders.contains(pref)) pref else null

            // (2) normalized fallback (if exact not found)
            if (chosen == null && pref != null) {
                val key = normalizeHeaderLikePython(pref)
                chosen = normIndex[key]
            }

            if (chosen != null) matched += 1
            mapping[canonical] = chosen ?: ""
        }
        return mapping to matched
    }
}
