package home.felipe.domain.vo

data class RunInferenceParams(
    val tensorFlowLiteAssetName: String,
    val metaAssetName: String,
    val targetName: String,
    val records: List<WaterRecord>,
    val headerMap: Map<String, String>? = null,
    val standardizeWithMeta: Boolean = false
)