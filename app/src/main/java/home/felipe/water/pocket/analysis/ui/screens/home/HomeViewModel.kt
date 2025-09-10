package home.felipe.water.pocket.analysis.ui.screens.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.usecase.DiscoverCompatibleResultUseCase
import home.felipe.domain.usecase.ImportCsvUseCase
import home.felipe.domain.usecase.ListMetadataTargetsUseCase
import home.felipe.domain.vo.DiscoverCompatibleTargetParams
import home.felipe.domain.vo.ImportCsvParams
import home.felipe.water.pocket.analysis.FlowState
import home.felipe.water.pocket.analysis.ui.models.RecentFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val importCsvUseCase: ImportCsvUseCase,
    private val listMetadataTargetsUseCase: ListMetadataTargetsUseCase,
    private val discoverCompatibleResultUseCase: DiscoverCompatibleResultUseCase,
    private val flow: FlowState
) : ViewModel() {

    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui

    fun onImportCsvUris(uris: List<Uri>, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }

            try {
                val selectedUri = uris.first()
                Timber.d("URI selected: $selectedUri")

                // 1. Usa o UseCase para importar o CSV
                val (fileName, records) = importCsvUseCase.execute(
                    ImportCsvParams(app.contentResolver, selectedUri)
                )
                if (records.isEmpty()) {
                    throw IllegalStateException("CSV file is empty or invalid.")
                }

                // 2. Usa o UseCase para listar os modelos disponíveis
                val metaTargets = listMetadataTargetsUseCase.execute(Unit)
                val csvHeaders = records.asSequence()
                    .flatMap { it.values.keys }
                    .distinct() // Usar distinct() é mais idiomático aqui
                    .toList()

                // 3. Usa o UseCase para descobrir quais modelos são compatíveis
                val compatibleResult = discoverCompatibleResultUseCase.execute(
                    DiscoverCompatibleTargetParams(
                        targetNames = metaTargets,
                        csvHeaders = csvHeaders,
                        coverage = 0.2f
                    )
                )

                // 4. Salva o estado e atualiza a UI
                flow.fileName = fileName
                flow.records = records
                flow.dates = records.map { it.date }
                flow.targetsAvailable = compatibleResult.targets.map { it.target }.sorted()
                flow.selectedTargets = flow.targetsAvailable
                flow.headerMapsByTarget.clear()
                flow.headerMapsByTarget.putAll(compatibleResult.headerMapsByTarget)

                Timber.d("Compatible targets found: ${flow.targetsAvailable.joinToString()}")

                val recentFile = RecentFile(
                    uriString = selectedUri.toString(),
                    name = fileName,
                    rows = records.size,
                    cols = csvHeaders.size
                )
                _ui.update {
                    it.copy(
                        recents = listOf(recentFile) + it.recents.filter  { r -> r.uriString != recentFile.uriString }.take(9),
                        loading = false
                    )
                }
                onDone?.invoke()

            } catch (t: Throwable) {
                Timber.e(t, "Error in onImportCsvUris")
                _ui.update { it.copy(loading = false, error = t.message ?: "Import failed") }
            }
        }
    }

    fun onOpenRecent(recentFile: RecentFile, onDone: (() -> Unit)? = null) {
        val uri = Uri.parse(recentFile.uriString)
        onImportCsvUris(listOf(uri), onDone)
    }
}
