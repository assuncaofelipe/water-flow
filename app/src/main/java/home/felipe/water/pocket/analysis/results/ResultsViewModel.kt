package home.felipe.water.pocket.analysis.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.usecase.GenerateReportUseCase
import home.felipe.domain.usecase.RunInferenceUseCase
import home.felipe.domain.vo.GenerateReportParams
import home.felipe.domain.vo.PredictionResult
import home.felipe.domain.vo.RunInferenceParams
import home.felipe.domain.vo.WaterRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val runInferenceUseCase: RunInferenceUseCase,
    private val generateReportUseCase: GenerateReportUseCase
) : ViewModel() {

    sealed class UiState {
        data object Idle : UiState()
        data object Loading : UiState()
        data class Success(val result: PredictionResult, val dates: List<String?>) : UiState()
        data class Error(val message: String) : UiState()
        data object Exporting : UiState()
        data class Exported(val csvUri: String, val pdfUri: String) : UiState()
    }

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun predict(target: String, records: List<WaterRecord>) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // nomes de assets esperados: "<target>.tflite" e "<target>.json"
                val tensorFlowLiteAssetName = "$target.tflite"
                val metaAssetName = "$target.json"
                val params = RunInferenceParams(
                    tensorFlowLiteAssetName = tensorFlowLiteAssetName,
                    metaAssetName = metaAssetName,
                    targetName = target,
                    records = records
                )
                val predictionResult = runInferenceUseCase.execute(params)
                val dates = records.map { it.date }
                _uiState.value = UiState.Success(predictionResult, dates)
            } catch (exception: Exception) {
                _uiState.value = UiState.Error(exception.message ?: "Falha na inferência")
            }
        }
    }

    fun export(result: PredictionResult, dates: List<String?>) {
        viewModelScope.launch {
            _uiState.value = UiState.Exporting
            try {
                val exportName = "pred_${result.target.lowercase()}"
                val (csvUri, pdfUri) = generateReportUseCase.execute(
                    GenerateReportParams(
                        fileName = exportName,
                        result = result,
                        dates = dates
                    )
                )
                _uiState.value = UiState.Exported(
                    csvUri = csvUri.toString(),
                    pdfUri = pdfUri.toString()
                )
            } catch (exception: Exception) {
                _uiState.value = UiState.Error(exception.message ?: "Falha ao exportar")
            }
        }
    }
}