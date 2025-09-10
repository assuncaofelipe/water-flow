package home.felipe.water.pocket.analysis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import home.felipe.water.pocket.analysis.ui.models.QualityStatus

@Composable
fun QualityBadge(status: QualityStatus) {
    val (bg, fg, label) = when (status) {
        QualityStatus.OK -> Triple(Color(0xFFD1FADF), Color(0xFF065F46), "OK")
        QualityStatus.WARNING -> Triple(Color(0xFFFFF6E5), Color(0xFF92400E), "Atenção")
        QualityStatus.CRITICAL -> Triple(Color(0xFFFEE2E2), Color(0xFF7F1D1D), "Crítico")
    }
    Box(
        Modifier
            .background(bg, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) { Text(label, color = fg, style = MaterialTheme.typography.labelLarge) }
}