package home.felipe.domain.repository


interface AssetsRepository {
    fun listMetadataTargets(): List<String>
}