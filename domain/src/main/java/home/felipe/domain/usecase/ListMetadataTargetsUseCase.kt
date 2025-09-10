package home.felipe.domain.usecase

import home.felipe.domain.repository.AssetsRepository
import home.felipe.domain.repository.LoggerRepository
import javax.inject.Inject

class ListMetadataTargetsUseCase @Inject constructor(
    private val assets: AssetsRepository,
    private val loggerRepository: LoggerRepository
) : UseCase<List<String>, Unit> {

    override suspend fun execute(params: Unit): List<String> {
        val list = assets.listMetadataTargets()
        loggerRepository.d(TAG, "metadata targets count=${list.size}")
        return list
    }

    private companion object { const val TAG = "ListMetadataTargetsUC" }
}
