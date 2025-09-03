package home.felipe.water.pocket.analysis.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import home.felipe.water.pocket.analysis.ui.models.RecentFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onImportCsvUris: (List<Uri>) -> Unit,
    onOpenRecent: (RecentFile) -> Unit
) {
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris -> if (uris.isNotEmpty()) onImportCsvUris(uris) }
    )

    Scaffold(topBar = { TopAppBar(title = { Text("Água no Bolso") }) }) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                enabled = !state.loading,
                onClick = {
                    picker.launch(
                        arrayOf(
                            "text/csv",
                            "text/*",
                            "application/vnd.ms-excel"
                        )
                    )
                }
            ) { Text(if (state.loading) "Processando…" else "⬆ Importar CSV") }

            if (state.loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            if (state.error != null) {
                Text(state.error, color = MaterialTheme.colorScheme.error)
            }

            Text("Recentes", style = MaterialTheme.typography.titleMedium)
            HorizontalDivider()

            state.recents.forEach { rf ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.loading) { onOpenRecent(rf) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(rf.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${rf.rows} linhas × ${rf.cols} colunas",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
