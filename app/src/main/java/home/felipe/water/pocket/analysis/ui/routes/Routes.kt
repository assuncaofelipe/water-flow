package home.felipe.water.pocket.analysis.ui.routes

// Rotas (já parametrizadas)
sealed class Route(val path: String) {

    data object Home : Route("home")

    data object Preview : Route("preview") {
        const val ARG_SID = "sid"
        val withArg get() = "$path/{$ARG_SID}"
        fun build(sid: String) = "$path/$sid"
    }

    data object Result : Route("results_all") {
        const val ARG_SID = "sid"
        val withArg get() = "$path/{$ARG_SID}"
        fun build(sid: String) = "$path/$sid"
    }
}
