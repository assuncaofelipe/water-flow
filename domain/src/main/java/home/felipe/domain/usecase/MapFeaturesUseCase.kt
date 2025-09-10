package home.felipe.domain.usecase

import home.felipe.domain.repository.LoggerRepository
import home.felipe.domain.vo.MapFeaturesParams
import javax.inject.Inject
import kotlin.math.min

class MapFeaturesUseCase @Inject constructor(
    private val loggerRepository: LoggerRepository
) : UseCase<Array<FloatArray>, MapFeaturesParams> {

    override suspend fun execute(params: MapFeaturesParams): Array<FloatArray> {
        val featureCount = params.meta.featuresOrder.size
        val recordCount = params.records.size
        val canStd = params.standardizeWithMeta &&
          params.meta.means.size == featureCount &&
          params.meta.stds.size == featureCount

        loggerRepository.d(
            TAG,
            "map: records=$recordCount features=$featureCount standardize=$canStd"
        )

        val medians = if (params.meta.medians.size == featureCount)
            params.meta.medians else List(featureCount) { Float.NaN }

        val matrix = Array(recordCount) { FloatArray(featureCount) }
        var filledByMedian = 0

        params.records.forEachIndexed { r, rec ->
            params.meta.featuresOrder.forEachIndexed { f, canonical ->
                val key = params.headerMap?.get(canonical) ?: canonical
                val raw = rec.values[key] ?: rec.values[canonical]
                var value = raw ?: medians[min(f, medians.size - 1)].also { filledByMedian++ }

                if (canStd) {
                    val sd = params.meta.stds[f]
                    if (sd > 0f && value.isFinite()) value = (value - params.meta.means[f]) / sd
                }
                matrix[r][f] = value
            }
        }

        loggerRepository.d(TAG, "map done: filledByMedian=$filledByMedian")
        return matrix
    }

    private companion object {
        const val TAG = "MapFeaturesUC"
    }
}
