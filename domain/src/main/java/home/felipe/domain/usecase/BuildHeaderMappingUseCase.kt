package home.felipe.domain.usecase

import home.felipe.domain.repository.LoggerRepository
import home.felipe.domain.util.HeaderMatcher
import home.felipe.domain.vo.BuildHeaderMappingParams
import javax.inject.Inject

class BuildHeaderMappingUseCase @Inject constructor(
    private val loggerRepository: LoggerRepository
) : UseCase<Map<String, String>, BuildHeaderMappingParams> {

    override suspend fun execute(params: BuildHeaderMappingParams): Map<String, String> {
        val mapping = HeaderMatcher.buildMapping(params.featuresOrder, params.csvHeaders)
        val matched = params.featuresOrder.count { !mapping[it].isNullOrBlank() }
        loggerRepository.d(TAG, "features=${params.featuresOrder.size} headers=${params.csvHeaders.size} matched=$matched")
        return mapping
    }

    private companion object { const val TAG = "BuildHeaderMappingUC" }
}
