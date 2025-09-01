package home.felipe.domain.vo

import com.google.gson.annotations.SerializedName

data class FeatureMeta(
    @SerializedName("target") val target: String,
    @SerializedName("features_order") val featuresOrder: List<String>,
    @SerializedName("medians") val medians: List<Float>,
    @SerializedName("means") val means: List<Float>,
    @SerializedName("stds") val stds: List<Float>,
    @SerializedName("input_name") val inputName: String = "raw_features",
    @SerializedName("output_name") val outputName: String = "y"
)