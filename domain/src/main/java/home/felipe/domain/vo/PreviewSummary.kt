package home.felipe.domain.vo

data class PreviewSummary(
    val fileName: String,
    val rows: Int,
    val cols: Int,
    val applicable: Int,
    val targets: List<TargetMapping>
)