package home.felipe.water.pocket.analysis.ui.screens.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import home.felipe.water.pocket.analysis.ui.components.KpiRow
import home.felipe.water.pocket.analysis.ui.components.LineChartTimeseries
import home.felipe.water.pocket.analysis.ui.components.PredictionsTable
import home.felipe.water.pocket.analysis.ui.components.QualityBadge
import home.felipe.water.pocket.analysis.ui.models.ResultsCardUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    state: ResultsUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados da Análise") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } }
            )
        }
    ) { padding ->
        when (state) {
            ResultsUiState.Loading -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator()
            }

            is ResultsUiState.Error -> Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(state.msg, color = MaterialTheme.colorScheme.error)
            }

            is ResultsUiState.Ready ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = padding.calculateTopPadding() + 16.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(state.cards) { card ->
                        ResultCard(card = card)
                    }
                }
        }
    }
}


@Composable
private fun ResultCard(
    card: ResultsCardUiModel,
    modifier: Modifier = Modifier
) {
    Card(modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(card.target, style = MaterialTheme.typography.titleMedium)

            val unitSuffix = card.unit?.let { " $it" } ?: ""
            val meanTxt = "%.3f%s".format(card.mean, unitSuffix)
            val minTxt = "%.3f%s".format(card.min, unitSuffix)
            val maxTxt = "%.3f%s".format(card.max, unitSuffix)

            KpiRow(mean = meanTxt, min = minTxt, max = maxTxt)
            QualityBadge(status = card.quality)
            LineChartTimeseries(
                points = card.points,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            PredictionsTable(rows = card.table)
        }
    }
}