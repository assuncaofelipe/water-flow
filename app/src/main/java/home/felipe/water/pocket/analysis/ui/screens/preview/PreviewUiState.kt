package home.felipe.water.pocket.analysis.ui.screens.preview

import home.felipe.domain.vo.PreviewSummary

sealed class PreviewUiState {
    data object Loading : PreviewUiState()
    data class Error(val msg: String) : PreviewUiState()
    data class Ready(val summary: PreviewSummary) : PreviewUiState()
}