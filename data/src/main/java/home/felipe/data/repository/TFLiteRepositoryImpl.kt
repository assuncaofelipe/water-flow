package home.felipe.data.repository

import android.app.Application
import home.felipe.domain.json.GsonProvider.shared
import home.felipe.domain.repository.TFLiteRepository
import home.felipe.domain.vo.FeatureMeta
import home.felipe.domain.vo.WaterRecord
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel
import javax.inject.Inject

class TFLiteRepositoryImpl @Inject constructor(
    private val app: Application,
) : TFLiteRepository {

    override fun loadFeatureMeta(assetName: String): FeatureMeta {
        val txt = app.assets.open(assetName).bufferedReader().use { it.readText() }
        return shared.fromJson(txt, FeatureMeta::class.java)
    }

    override fun runBatch(
        tensorFlowLiteAssetName: String,
        meta: FeatureMeta,
        records: List<WaterRecord>
    ): List<Float> {
        // quantidade de linhas (registros).
        val recordCount = records.size

        // número de features usadas.
        val featureCount = meta.featuresOrder.size

        // matriz de entrada [registros, features].
        val inputMatrix = Array(recordCount) { FloatArray(featureCount) }
        records.forEachIndexed { recordIndex, waterRecord ->
            meta.featuresOrder.forEachIndexed { featureIndex, featureName ->
                val value = waterRecord.values[featureName] ?: Float.NaN
                inputMatrix[recordIndex][featureIndex] = value
            }
        }

        // matriz de saída [registros, 1].
        val outputMatrix = Array(recordCount) { FloatArray(1) }

        val mappedModel = app.assets.openFd(tensorFlowLiteAssetName).use { fd ->
            FileInputStream(fd.fileDescriptor).channel.map(
                FileChannel.MapMode.READ_ONLY,
                fd.startOffset,
                fd.declaredLength
            )
        }

        val interpreter = Interpreter(mappedModel)
        interpreter.run(inputMatrix, outputMatrix)
        interpreter.close()

        return outputMatrix.map { it[0] }
    }
}
