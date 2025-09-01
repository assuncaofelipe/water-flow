package home.felipe.domain.usecase

import home.felipe.domain.repository.TFLiteRepository
import home.felipe.domain.vo.PredictionResult
import home.felipe.domain.vo.RunInferenceParams
import home.felipe.domain.vo.Stats
import javax.inject.Inject

class RunInferenceUseCase @Inject constructor(
    private val tensorFlowLite: TFLiteRepository
) : UseCase<PredictionResult, RunInferenceParams> {

    override suspend fun execute(params: RunInferenceParams): PredictionResult {
        val meta = tensorFlowLite.loadFeatureMeta(params.metaAssetName)

        require(meta.target == params.targetName) {
            "metadata não corresponde ao alvo."
        }

        val preds = tensorFlowLite.runBatch(params.tensorFlowLiteAssetName, meta, params.records)

        val stats = Stats(
            mean = preds.average().toFloat(),
            min = preds.minOrNull() ?: Float.NaN,
            max = preds.maxOrNull() ?: Float.NaN
        )

        return PredictionResult(
            target = params.targetName,
            predictions = preds,
            stats = stats
        )
    }
}