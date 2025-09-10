package home.felipe.water.pocket.analysis.ui.models

data class ResultsCardUiModel(
    val target: String,
    val unit: String?,
    val mean: Float,
    val min: Float,
    val max: Float,
    val quality: QualityStatus,
    val points: List<Pair<Int, Float>>,
    val table: List<PredictionRow>
)