package home.felipe.water.pocket.analysis.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import home.felipe.water.pocket.analysis.ui.models.PredictionRow

@Composable
fun PredictionsTable(rows: List<PredictionRow>) {
    Card {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Text("Data", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("Estação", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("Previsão", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            LazyColumn(modifier = Modifier.heightIn(max = 280.dp)) {
                items(rows) { r ->
                    Row(Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)) {
                        Text(r.date.orEmpty(), Modifier.weight(1f))
                        Text(r.station.orEmpty(), Modifier.weight(1f))
                        Text("%.3f".format(r.value), Modifier.weight(1f))
                    }
                }
            }
        }
    }
}