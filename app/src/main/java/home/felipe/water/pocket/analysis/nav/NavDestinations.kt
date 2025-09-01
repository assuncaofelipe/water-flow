package home.felipe.water.pocket.analysis.nav

object NavDestinations {
    const val HOME = "home"
    const val RESULTS = "results/{target}"

    fun resultsRoute(target: String): String {
        return "results/$target"
    }
}