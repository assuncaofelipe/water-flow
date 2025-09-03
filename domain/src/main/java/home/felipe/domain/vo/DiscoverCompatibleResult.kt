package home.felipe.domain.vo

data class DiscoverCompatibleResult(
    val targets: List<TargetMapping>,
    val headerMapsByTarget: Map<String, Map<String, String>>
)