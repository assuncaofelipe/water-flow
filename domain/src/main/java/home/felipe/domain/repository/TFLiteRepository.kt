package home.felipe.domain.repository

import home.felipe.domain.vo.FeatureMeta
import home.felipe.domain.vo.WaterRecord

interface TFLiteRepository {
    fun loadFeatureMeta(assetName: String): FeatureMeta

    fun runBatch(
        tensorFlowLiteAssetName: String,
        meta: FeatureMeta,
        records: List<WaterRecord>
    ): List<Float>
}