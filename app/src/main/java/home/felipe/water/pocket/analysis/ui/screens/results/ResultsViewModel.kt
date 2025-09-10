package home.felipe.water.pocket.analysis.ui.screens.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.usecase.RunInferenceUseCase
import home.felipe.domain.vo.PredictionResult
import home.felipe.domain.vo.RunInferenceParams
import home.felipe.water.pocket.analysis.FlowState
import home.felipe.water.pocket.analysis.ui.models.PredictionRow
import home.felipe.water.pocket.analysis.ui.models.QualityStatus
import home.felipe.water.pocket.analysis.ui.models.ResultsCardUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val runInference: RunInferenceUseCase,
    private val flow: FlowState
) : ViewModel() {

    private val _ui = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val ui: StateFlow<ResultsUiState> = _ui
    private val ioDispatcher = Job() + Dispatchers.Default

    init {
        runAll()
    }

    fun runAll() = viewModelScope.launch(ioDispatcher) {
        try {
            // Validações iniciais
            if (flow.records.isEmpty()) {
                _ui.value = ResultsUiState.Error("No CSV data loaded.")
                return@launch
            }
            if (flow.selectedTargets.isEmpty()) {
                _ui.value = ResultsUiState.Error("No compatible models were selected.")
                return@launch
            }

            // Mapeia cada alvo selecionado para uma chamada de inferência
            val results = flow.selectedTargets.mapNotNull { target ->
                runCatching {
                    runInference.execute(
                        RunInferenceParams(
                            tensorFlowLiteAssetName = "tflite/$target.tflite",
                            metaAssetName = "metadata/$target.json",
                            targetName = target,
                            records = flow.records,
                            headerMap = flow.headerMapsByTarget[target]
                        )
                    )
                }.onFailure { e -> Timber.e(e, "Inference failed for target=$target") }
                    .getOrNull()
            }

            if (results.isEmpty()) {
                _ui.value = ResultsUiState.Error("Inference produced no results.")
                return@launch
            }

            val cards = results.map { it.toResultsAllCard(dates = flow.dates) }
            _ui.value = ResultsUiState.Ready(cards)

        } catch (t: Throwable) {
            Timber.e(t, "runAll failed")
            _ui.value = ResultsUiState.Error(t.message ?: "An unexpected error occurred")
        }
    }

    fun PredictionResult.toResultsAllCard(dates: List<String?>): ResultsCardUiModel {

        val quality = when {
            this.stats.mean > 8.0 -> QualityStatus.CRITICAL
            this.stats.mean < 6.0 -> QualityStatus.WARNING
            else -> QualityStatus.OK
        }

        return ResultsCardUiModel(
            target = this.target,
            unit = "mg/L",
            mean = this.stats.mean,
            min = this.stats.min,
            max = this.stats.max,
            quality = quality,

            points = this.predictions.mapIndexed { index, value ->
                index to value
            },

            table = this.predictions.mapIndexed { index, value ->
                PredictionRow(
                    date = dates.getOrNull(index) ?: "N/A",
                    station = "Estação A",
                    value = value
                )
            }
        )
    }
}