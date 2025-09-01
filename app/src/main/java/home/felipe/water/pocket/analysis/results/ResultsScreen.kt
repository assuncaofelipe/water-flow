package home.felipe.water.pocket.analysis.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import home.felipe.water.pocket.analysis.components.SimpleLineChart
import home.felipe.water.pocket.analysis.shared.SharedRecordsViewModel

@Composable
fun ResultsScreen(
    target: String,
    onNavigateBack: () -> Unit,
    sharedRecordsViewModel: SharedRecordsViewModel = hiltViewModel(),
    resultsViewModel: ResultsViewModel = hiltViewModel()
) {
    val recordsState by sharedRecordsViewModel.recordsState.collectAsState()
    val uiState by resultsViewModel.uiState.collectAsState()

    LaunchedEffect(target, recordsState) {
        if (recordsState.isNotEmpty()) {
            resultsViewModel.predict(target, recordsState)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Resultados — $target", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        when (uiState) {
            is ResultsViewModel.UiState.Idle -> Text("Pronto.")
            is ResultsViewModel.UiState.Loading -> Text("Calculando...")
            is ResultsViewModel.UiState.Error -> {
                val message = (uiState as ResultsViewModel.UiState.Error).message
                Text("Erro: $message")
            }

            is ResultsViewModel.UiState.Success -> {
                val success = uiState as ResultsViewModel.UiState.Success
                val result = success.result
                val dates = success.dates

                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("Média: %.3f".format(result.stats.mean))
                        Text("Mínimo: %.3f".format(result.stats.min))
                        Text("Máximo: %.3f".format(result.stats.max))
                    }
                }
                Spacer(Modifier.height(12.dp))

                SimpleLineChart(
                    points = result.predictions.mapIndexed { index, y -> index.toFloat() to y }
                )

                Spacer(Modifier.height(12.dp))
                Button(onClick = { resultsViewModel.export(result, dates) }) {
                    Text("Exportar PDF/CSV")
                }
            }

            is ResultsViewModel.UiState.Exporting -> Text("Exportando...")
            is ResultsViewModel.UiState.Exported -> {
                val exported = uiState as ResultsViewModel.UiState.Exported
                Text("Exportado!\nCSV: ${exported.csvUri}\nPDF: ${exported.pdfUri}")
            }
        }
    }
}