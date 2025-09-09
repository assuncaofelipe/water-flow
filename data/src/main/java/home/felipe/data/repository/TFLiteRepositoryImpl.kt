package home.felipe.data.repository

import android.app.Application
import home.felipe.domain.json.GsonProvider.shared
import home.felipe.domain.repository.TFLiteRepository
import home.felipe.domain.vo.FeatureMeta
import org.tensorflow.lite.Interpreter
import timber.log.Timber
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

class TFLiteRepositoryImpl @Inject constructor(
    private val app: Application
) : TFLiteRepository {

    override fun loadFeatureMeta(assetName: String): FeatureMeta {
        Timber.d("Loading metadata: $assetName")
        val json = app.assets.open(assetName).bufferedReader().use { it.readText() }
        val meta = shared.fromJson(json, FeatureMeta::class.java)
        Timber.d(
            "Metadata parsed: target=${meta.target} features=${meta.features_order.size} hasCsvHeaderMap=${meta.csvHeaderMap != null}"
        )
        return meta
    }

    override fun runBatch(
        tensorFlowLiteAssetName: String,
        meta: FeatureMeta,
        input: Array<FloatArray>
    ): List<Float> {
        if (input.isEmpty()) {
            Timber.d("runBatch called with empty input")
            return emptyList()
        }
        val features = input.first().size
        if (features != meta.features_order.size) {
            Timber.w("Feature-size mismatch: input=$features meta=${meta.features_order.size}")
        }

        val modelBuffer = mapModel(tensorFlowLiteAssetName)
        val output = Array(input.size) { FloatArray(1) }

        Interpreter(modelBuffer).use { interpreter ->
            Timber.d("Interpreter ready: N=${input.size} K=$features")
            interpreter.run(input, output)
        }

        val ys = output.map { it[0] }
        Timber.d("Inference OK: outputs=${ys.size}")
        return ys
    }

    private fun mapModel(assetPath: String): MappedByteBuffer {
        app.assets.openFd(assetPath).use { fd ->
            FileInputStream(fd.fileDescriptor).channel.use { ch ->
                val mapped =
                    ch.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
                Timber.d("Model mapped: $assetPath bytes=${fd.declaredLength}")
                return mapped
            }
        }
    }
}