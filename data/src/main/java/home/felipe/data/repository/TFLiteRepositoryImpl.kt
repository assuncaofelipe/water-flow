package home.felipe.data.repository

import android.app.Application
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.java.TfLite
import home.felipe.domain.json.GsonProvider.shared
import home.felipe.domain.repository.TFLiteRepository
import home.felipe.domain.vo.FeatureMeta
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.InterpreterApi
import timber.log.Timber
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

class TFLiteRepositoryImpl @Inject constructor(
    private val app: Application,
) : TFLiteRepository {

    private val initializeTask by lazy {
        val opts = TfLiteInitializationOptions.builder().build()
        TfLite.initialize(app, opts)
    }

    override fun loadFeatureMeta(assetName: String): FeatureMeta {
        Timber.d("Loading metadata $assetName - $assetName")
        val json = app.assets.open(assetName).bufferedReader().use { it.readText() }
        val meta = shared.fromJson(json, FeatureMeta::class.java)
        Timber.d("Meta lida target ${meta.target} - ${meta.featuresOrder.size} features")
        return meta
    }

    override fun runBatch(
        tensorFlowLiteAssetName: String,
        meta: FeatureMeta,
        input: Array<FloatArray>
    ): List<Float> {
        if (input.isEmpty()) {
            Timber.d("runBatch called empty input - ${input.size}")
            return emptyList()
        }
        val features = input.first().size
        if (features != meta.featuresOrder.size) {
            Timber.d("The size of divergent features $features - ${meta.featuresOrder.size}")
        }

        val modelBuffer = mapModel(tensorFlowLiteAssetName)
        Timber.d("Model mapped $tensorFlowLiteAssetName - $tensorFlowLiteAssetName")

        try {
            Tasks.await(initializeTask)
            Timber.d("TfLite.initialize completed - $tensorFlowLiteAssetName")

            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)

            val output = Array(input.size) { FloatArray(1) }
            InterpreterApi.create(modelBuffer, options).use { interpreter ->
                Timber.d("InterpreterApi pronto - N ${input.size} - K $features")
                interpreter.run(input, output)
            }

            val ys = output.map { it[0] }
            Timber.d("Inference (LiteRT) ok - ${ys.size}")
            return ys
        } catch (t: Throwable) {
            Timber.d("Failed LiteRT - ${t.localizedMessage}")
        }

        return runWithBundledInterpreter(modelBuffer, input)
    }

    private fun mapModel(assetPath: String): MappedByteBuffer {
        app.assets.openFd(assetPath).use { fd ->
            FileInputStream(fd.fileDescriptor).channel.use { ch ->
                val mapped =
                    ch.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
                Timber.d("mapModel ok $assetPath - ${fd.declaredLength}")
                return mapped
            }
        }
    }

    private fun runWithBundledInterpreter(
        model: MappedByteBuffer,
        input: Array<FloatArray>
    ): List<Float> {
        val output = Array(input.size) { FloatArray(1) }
        Interpreter(model).use { interpreter ->
            Timber.d("Interpreter (bundled) pronto - N ${input.size}")
            interpreter.run(input, output)
        }
        val ys = output.map { it[0] }
        Timber.d("Inference (bundled) ok - ${ys.size}")
        return ys
    }
}