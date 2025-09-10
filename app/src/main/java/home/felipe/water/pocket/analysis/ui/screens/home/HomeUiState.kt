package home.felipe.water.pocket.analysis.ui.screens.home

import home.felipe.water.pocket.analysis.ui.models.RecentFile

data class HomeUiState(
    val recents: List<RecentFile> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)