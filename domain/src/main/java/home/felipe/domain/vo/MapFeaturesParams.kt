package home.felipe.domain.vo

data class MapFeaturesParams(
    val records: List<WaterRecord>,
    val meta: FeatureMeta,
    val headerMap: Map<String, String>? = null,
    val standardizeWithMeta: Boolean = false
)