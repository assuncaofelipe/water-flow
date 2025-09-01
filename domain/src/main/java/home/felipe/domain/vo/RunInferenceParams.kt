package home.felipe.domain.vo

data class RunInferenceParams(
    val tensorFlowLiteAssetName: String,
    val metaAssetName: String,
    val targetName: String,
    val records: List<WaterRecord>
)