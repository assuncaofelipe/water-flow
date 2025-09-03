package home.felipe.data.repository

import android.app.Application
import home.felipe.domain.repository.AssetsRepository
import javax.inject.Inject

class AssetsRepositoryImpl @Inject constructor(
    private val app: Application
) : AssetsRepository {

    override fun listMetadataTargets(): List<String> {
        return app.assets.list("metadata")
            ?.filter { it.endsWith(".json", ignoreCase = true) }
            ?.map { it.removeSuffix(".json") }
            .orElseEmpty()
    }

    private fun <T> List<T>?.orElseEmpty(): List<T> = this ?: emptyList()
}
