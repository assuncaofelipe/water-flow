package home.felipe.water.pocket.analysis.ui.screens.results

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import home.felipe.water.pocket.analysis.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    state: ResultsUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } }
            )
        }
    ) { padding ->
        when (state) {
            ResultsUiState.Loading -> Box(Modifier.padding(padding).fillMaxSize()) {
                CircularProgressIndicator()
            }
            is ResultsUiState.Error -> Column(
                Modifier.padding(padding).padding(16.dp)
            ) { Text(state.msg, color = MaterialTheme.colorScheme.error) }

            is ResultsUiState.Ready -> Column(
                Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.cards.forEach { card ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(card.target, style = MaterialTheme.typography.titleMedium)

                            val meanTxt = card.unit?.let { "%.3f $it".format(card.mean) } ?: "%.3f".format(card.mean)
                            val minTxt  = card.unit?.let { "%.3f $it".format(card.min) }  ?: "%.3f".format(card.min)
                            val maxTxt  = card.unit?.let { "%.3f $it".format(card.max) }  ?: "%.3f".format(card.max)

                            KpiRow(mean = meanTxt, min = minTxt, max = maxTxt)
                            QualityBadge(status = card.quality)
                            LineChartTimeseries(points = card.points, modifier = Modifier.fillMaxWidth().height(220.dp))
                            PredictionsTable(rows = card.table)
                        }
                    }
                }
            }
        }
    }
}
