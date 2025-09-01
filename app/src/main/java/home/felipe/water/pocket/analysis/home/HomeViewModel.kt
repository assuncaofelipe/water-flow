package home.felipe.water.pocket.analysis.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.usecase.ImportCsvUseCase
import home.felipe.domain.vo.ImportCsvParams
import home.felipe.domain.vo.WaterRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val importCsvUseCase: ImportCsvUseCase
) : ViewModel() {

    sealed class UiState {
        data object Idle : UiState()
        data object Loading : UiState()
        data class Loaded(val records: List<WaterRecord>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun importCsv(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result: List<WaterRecord> = importCsvUseCase.execute(ImportCsvParams(uri))
                _uiState.value = UiState.Loaded(result)
            } catch (exception: Exception) {
                _uiState.value = UiState.Error(exception.message ?: "Erro ao importar CSV")
            }
        }
    }
}