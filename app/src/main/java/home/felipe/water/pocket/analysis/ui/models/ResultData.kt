package home.felipe.water.pocket.analysis.ui.models

data class ResultData(
    val target: String,
    val mean: Float,
    val min: Float,
    val max: Float,
    val unit: String?,
    val points: List<Pair<Int, Float>>,
    val table: List<PredictionRow>,
    val quality: QualityStatus
)