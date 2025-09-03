package home.felipe.domain.usecase

import home.felipe.domain.repository.LoggerRepository
import home.felipe.domain.repository.TFLiteRepository
import home.felipe.domain.vo.BuildHeaderMappingParams
import home.felipe.domain.vo.DiscoverCompatibleResult
import home.felipe.domain.vo.DiscoverCompatibleTargetParams
import home.felipe.domain.vo.FeatureMapping
import home.felipe.domain.vo.TargetMapping
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

class DiscoverCompatibleTargetsUseCase @Inject constructor(
    private val tflite: TFLiteRepository,
    private val buildMapping: BuildHeaderMappingUseCase,
    private val loggerRepository: LoggerRepository
) : UseCase<DiscoverCompatibleResult, DiscoverCompatibleTargetParams> {

    override suspend fun execute(params: DiscoverCompatibleTargetParams): DiscoverCompatibleResult {
        loggerRepository.d(
            TAG,
            "discover: targets=${params.targetNames.size} headers=${params.csvHeaders.size} coverage=${params.coverage}"
        )

        val compatible = mutableListOf<TargetMapping>()
        val mapByTarget = mutableMapOf<String, Map<String, String>>()
        val coverage = params.coverage.coerceIn(0f, 1f)

        for (name in params.targetNames) {
            val meta = runCatching { tflite.loadFeatureMeta("metadata/$name.json") }
                .onFailure { loggerRepository.w(TAG, "meta load fail: $name") }
                .getOrNull() ?: continue

            val mapping = buildMapping.execute(
                BuildHeaderMappingParams(meta.featuresOrder, params.csvHeaders)
            )

            val matched = meta.featuresOrder.count { !mapping[it].isNullOrBlank() }
            val needed = max(1, ceil(meta.featuresOrder.size * coverage).toInt())
            val missing = meta.featuresOrder.filter { mapping[it].isNullOrBlank() }

            loggerRepository.d(
                TAG,
                "target=$name matched=$matched needed=$needed total=${meta.featuresOrder.size} missing=${missing.joinToString()}"
            )

            if (matched >= needed) {
                val items = meta.featuresOrder.map { canonical ->
                    val h = mapping[canonical].orEmpty()
                    FeatureMapping(canonical, h, h.isBlank())
                }
                compatible += TargetMapping(meta.target, items)
                mapByTarget[meta.target] = mapping
            }
        }

        loggerRepository.d(TAG, "compatible=${compatible.size}")
        return DiscoverCompatibleResult(
            targets = compatible.sortedBy { it.target },
            headerMapsByTarget = mapByTarget
        )
    }

    private companion object {
        const val TAG = "DiscoverCompatUC"
    }
}