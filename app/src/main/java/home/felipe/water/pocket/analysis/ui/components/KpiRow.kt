package home.felipe.water.pocket.analysis.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KpiRow(mean: String, min: String, max: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        KpiCard("MÉDIA", mean, Modifier.weight(1f))
        KpiCard("MÍNIMO", min, Modifier.weight(1f))
        KpiCard("MÁXIMO", max, Modifier.weight(1f))
    }
}