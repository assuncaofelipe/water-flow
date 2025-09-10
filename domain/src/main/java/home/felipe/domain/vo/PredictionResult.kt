package home.felipe.domain.vo

data class PredictionResult(
    val target: String,
    val predictions: List<Float>,
    val stats: Stats
)