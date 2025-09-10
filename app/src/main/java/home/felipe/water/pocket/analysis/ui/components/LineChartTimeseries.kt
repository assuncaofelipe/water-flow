package home.felipe.water.pocket.analysis.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
fun LineChartTimeseries(
    points: List<Pair<Int, Float>>,
    modifier: Modifier = Modifier
) {
    val sorted = remember(points) { points.sortedBy { it.first } }
    val ys = remember(sorted) { sorted.map { it.second } }

    // Produtor do modelo (2.x)
    val modelProducer = remember { CartesianChartModelProducer() }

    // Alimenta os dados numa transação
    LaunchedEffect(ys) {
        modelProducer.runTransaction {
            lineSeries {
                series(ys) // usa índices (0..n-1) como X
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}