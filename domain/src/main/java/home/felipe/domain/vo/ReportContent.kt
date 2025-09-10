package home.felipe.domain.vo

data class ReportContent(
    val title: String,
    val summary: Map<String, String>,
    val series: List<Pair<String, List<Float>>>
)