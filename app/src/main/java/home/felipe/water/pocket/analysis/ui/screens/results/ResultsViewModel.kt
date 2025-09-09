package home.felipe.water.pocket.analysis.ui.screens.results

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.repository.TFLiteRepository
import home.felipe.domain.usecase.RunInferenceUseCase
import home.felipe.domain.vo.FeatureMeta
import home.felipe.domain.vo.PredictionResult
import home.felipe.domain.vo.RunInferenceParams
import home.felipe.domain.vo.Stats
import home.felipe.water.pocket.analysis.FlowState
import home.felipe.water.pocket.analysis.ui.shared.toResultsAllCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.Normalizer
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val app: Application,
    private val runInference: RunInferenceUseCase,
    private val tfliteRepository: TFLiteRepository,
    private val flow: FlowState
) : ViewModel() {

    private val dispatcher = Job() + Dispatchers.Default
    private val _ui = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val ui: StateFlow<ResultsUiState> = _ui

    init { runAll() }

    fun runAll() = viewModelScope.launch(dispatcher) {
        try {
            val records = flow.records
            if (records.isEmpty()) {
                _ui.value = ResultsUiState.Error("No CSV loaded.")
                return@launch
            }

            // If Home already computed, reuse. Otherwise build with the same logic here.
            val targets: List<String> =
                if (!flow.selectedTargets.isEmpty()) flow.selectedTargets
                else withContext(dispatcher) { buildMappingsFromAssets(records) }

            if (targets.isEmpty()) {
                _ui.value = ResultsUiState.Error("No model/metadata found in assets.")
                return@launch
            }

            val results = mutableListOf<PredictionResult>()

            for (target in targets) {
                val headerMap = flow.headerMapsByTarget[target]
                val res = runCatching {
                    runInference.execute(
                        RunInferenceParams(
                            tensorFlowLiteAssetName = "tflite/$target.tflite",
                            metaAssetName = "metadata/$target.json",
                            targetName = target,
                            records = records,
                            headerMap = headerMap
                        )
                    )
                }.onFailure { e -> Timber.e(e, "Inference failed for target=$target") }
                    .getOrNull() ?: continue

                val preds = res.predictions
                if (preds.isEmpty()) continue

                var minVal = Float.POSITIVE_INFINITY
                var maxVal = Float.NEGATIVE_INFINITY
                preds.forEach { v ->
                    minVal = min(minVal, v)
                    maxVal = max(maxVal, v)
                }

                results += res.copy(stats = Stats(res.stats.mean, minVal, maxVal))
            }

            if (results.isEmpty()) {
                _ui.value = ResultsUiState.Error("No results generated.")
                return@launch
            }

            val cards = results.map { it.toResultsAllCard(dates = flow.dates) }
            _ui.value = ResultsUiState.Ready(cards)
        } catch (t: Throwable) {
            Timber.e(t, "runAll failed")
            _ui.value = ResultsUiState.Error(t.message ?: "Error")
        }
    }

    /** === Helpers (repete a lógica do Home se necessário) === */
    private fun buildMappingsFromAssets(records: List<home.felipe.domain.vo.WaterRecord>): List<String> {
        val headers = records.asSequence().flatMap { it.values.keys.asSequence() }.toSet()
        val normIndex = headers.associateBy { normalizeHeaderLikePython(it) }

        val targets = mutableListOf<String>()
        val maps = mutableMapOf<String, Map<String, String>>()

        val metaTargets = app.assets.list("metadata")
            ?.filter { it.endsWith(".json", ignoreCase = true) }
            ?.map { it.removeSuffix(".json") }
            .orEmpty()

        Timber.d("Meta targets found (results): ${metaTargets.joinToString()}")

        metaTargets.forEach { name ->
            val meta = runCatching {
                tfliteRepository.loadFeatureMeta("metadata/$name.json")
            }.getOrNull() ?: return@forEach

            val (mapping, matched) = mapTarget(meta, headers, normIndex)
            val sample = mapping.entries.take(4).joinToString { "${it.key}→${it.value.ifEmpty { "(miss)" }}" }
            Timber.d("Target $name coverage=$matched/${meta.features_order.size} sample=[$sample]")

            targets += name
            maps[name] = mapping
        }

        flow.targetsAvailable = targets.sorted()
        flow.selectedTargets = flow.targetsAvailable
        flow.headerMapsByTarget.clear()
        flow.headerMapsByTarget.putAll(maps)
        Timber.d("Compatible targets (results): ${flow.targetsAvailable.joinToString()}")

        return flow.selectedTargets
    }

    private fun mapTarget(
        meta: FeatureMeta,
        csvHeaders: Set<String>,
        normIndex: Map<String, String>
    ): Pair<Map<String, String>, Int> {
        val mapping = mutableMapOf<String, String>()
        var matched = 0
        meta.features_order.forEach { canonical ->
            val pref = meta.csvHeaderMap?.get(canonical)
            var chosen: String? = if (pref != null && csvHeaders.contains(pref)) pref else null
            if (chosen == null && pref != null) {
                val key = normalizeHeaderLikePython(pref)
                chosen = normIndex[key]
            }
            if (chosen != null) matched += 1
            mapping[canonical] = chosen ?: ""
        }
        return mapping to matched
    }

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
}
