package home.felipe.water.pocket.analysis.ui.routes

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Result : Route("results_all")
}
