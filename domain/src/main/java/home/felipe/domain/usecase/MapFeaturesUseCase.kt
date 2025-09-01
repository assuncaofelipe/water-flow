package home.felipe.domain.usecase

import home.felipe.domain.vo.MapFeaturesParams
import javax.inject.Inject

/**
 * Builds the input matrix \[N, K\] in the SAME feature order declared in FeatureMeta.featuresOrder.
 * - N = number of records
 * - K = number of features
 * Missing values become Float.NaN (the TFLite model imputes internally).
 */
class MapFeaturesUseCase @Inject constructor(
) : UseCase<Array<FloatArray>, MapFeaturesParams> {

    override suspend fun execute(params: MapFeaturesParams): Array<FloatArray> {
        val featureCount: Int = params.meta.featuresOrder.size
        val recordCount: Int = params.records.size

        val inputMatrix: Array<FloatArray> = Array(recordCount) { FloatArray(featureCount) }

        params.records.forEachIndexed { recordIndex: Int, waterRecord ->
            params.meta.featuresOrder.forEachIndexed { featureIndex: Int, featureName: String ->
                val value: Float = waterRecord.values[featureName] ?: Float.NaN
                inputMatrix[recordIndex][featureIndex] = value
            }
        }

        return inputMatrix
    }
}