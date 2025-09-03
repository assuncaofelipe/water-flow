package home.felipe.water.pocket.analysis.ui.screens.results

import home.felipe.water.pocket.analysis.ui.models.ResultData

sealed interface ResultsUiState {
    data object Loading : ResultsUiState
    data class Error(val msg: String) : ResultsUiState
    data class Ready(val cards: List<ResultData>) : ResultsUiState
}
