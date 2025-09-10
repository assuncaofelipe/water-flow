package home.felipe.domain.vo

data class DiscoverCompatibleTargetParams(
    val targetNames: List<String>,
    val csvHeaders: List<String>,
    val coverage: Float = 0.20f
)