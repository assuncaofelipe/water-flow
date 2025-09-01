package home.felipe.domain.vo

data class GenerateReportParams(
    val fileName: String,
    val result: PredictionResult,
    val dates: List<String?>
)
