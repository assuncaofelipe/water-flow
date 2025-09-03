package home.felipe.domain.repository

import home.felipe.domain.vo.FeatureMeta

interface TFLiteRepository {
    fun loadFeatureMeta(assetName: String): FeatureMeta
    fun runBatch(
        tensorFlowLiteAssetName: String, meta: FeatureMeta,
        input: Array<FloatArray>
    ): List<Float>
}