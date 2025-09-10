package home.felipe.domain.vo

data class FeatureMapping(
    val canonical: String,
    val matchedHeader: String,
    val isMissing: Boolean
)