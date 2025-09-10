package home.felipe.domain.usecase

import home.felipe.domain.repository.LoggerRepository
import home.felipe.domain.repository.TFLiteRepository
import home.felipe.domain.vo.MapFeaturesParams
import home.felipe.domain.vo.PredictionResult
import home.felipe.domain.vo.RunInferenceParams
import home.felipe.domain.vo.Stats
import javax.inject.Inject

class RunInferenceUseCase @Inject constructor(
    private val tensorFlowLite: TFLiteRepository,
    private val mapFeatures: MapFeaturesUseCase,
    private val loggerRepository: LoggerRepository
) : UseCase<PredictionResult, RunInferenceParams> {

    override suspend fun execute(params: RunInferenceParams): PredictionResult {
        loggerRepository.d(TAG, "run: target=${params.targetName} records=${params.records.size}")

        val meta = tensorFlowLite.loadFeatureMeta(params.metaAssetName)
        require(meta.target == params.targetName) {
            val msg = "metadata mismatch (${meta.target} != ${params.targetName})"
            loggerRepository.e(TAG, msg)
            msg
        }

        val x = mapFeatures.execute(
            MapFeaturesParams(
                records = params.records,
                meta = meta,
                headerMap = params.headerMap,
                standardizeWithMeta = params.standardizeWithMeta
            )
        )

        val predictions = tensorFlowLite.runBatch(
            tensorFlowLiteAssetName = params.tensorFlowLiteAssetName,
            meta = meta,
            input = x
        )

        val finite = predictions.filter { it.isFinite() }
        val stats = Stats(
            mean = finite.takeIf { it.isNotEmpty() }?.average()?.toFloat() ?: Float.NaN,
            min = finite.minOrNull() ?: Float.NaN,
            max = finite.maxOrNull() ?: Float.NaN
        )

        loggerRepository.d(
            TAG,
            "done: n=${predictions.size} mean=${stats.mean} min=${stats.min} max=${stats.max}"
        )
        return PredictionResult(params.targetName, predictions, stats)
    }

    private companion object {
        const val TAG = "RunInferenceUC"
    }
}