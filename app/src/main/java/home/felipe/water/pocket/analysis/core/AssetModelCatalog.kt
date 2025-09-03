package home.felipe.water.pocket.analysis.core

import android.app.Application
import home.felipe.water.pocket.analysis.ui.models.ModelDescriptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetModelCatalog @Inject constructor(
    private val app: Application
) {
    /** Lista pares válidos: existe o .json e o .tflite para o MESMO nome (case-sensitive). */
    fun listAll(): List<ModelDescriptor> {
        val metas = app.assets.list("metadata").orEmpty()
            .filter { it.endsWith(".json", ignoreCase = true) }
            .map { it.removeSuffix(".json") }
            .toSet()

        val bins = app.assets.list("tflite").orEmpty()
            .filter { it.endsWith(".tflite", ignoreCase = true) }
            .map { it.removeSuffix(".tflite") }
            .toSet()

        val names = metas.intersect(bins)

        return names.map { name ->
            ModelDescriptor(
                name = name,
                "metadata/$name.json",
                "tflite/$name.tflite"
            )
        }.sortedBy { it.name.lowercase() }
    }
}
