package home.felipe.water.pocket.analysis.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.unit.dp
import dagger.hilt.android.HiltAndroidApp
import kotlin.math.max

@Composable
fun SimpleLineChart(
    points: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
) {
    if (points.isEmpty()) {
        return
    }
    val xValues = points.map { it.first }
    val yValues = points.map { it.second }

    val xMin = xValues.minOrNull() ?: 0f
    val xMax = xValues.maxOrNull() ?: 1f
    val yMin = yValues.minOrNull() ?: 0f
    val yMax = yValues.maxOrNull() ?: 1f

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        fun mapX(x: Float): Float {
            val denom = max(1e-6f, (xMax - xMin))
            return ((x - xMin) / denom) * width
        }

        fun mapY(y: Float): Float {
            val denom = max(1e-6f, (yMax - yMin))
            val normalized = (y - yMin) / denom
            return height - normalized * height
        }

        var previous: Offset? = null
        for ((x, y) in points) {
            val current = Offset(mapX(x), mapY(y))
            if (previous != null) {
                drawLine(
                    color = Blue,
                    start = previous,
                    end = current,
                    strokeWidth = 4f
                )
            }
            previous = current
        }
    }
}