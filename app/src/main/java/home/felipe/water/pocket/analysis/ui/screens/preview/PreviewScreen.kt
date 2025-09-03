package home.felipe.water.pocket.analysis.ui.screens.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import home.felipe.domain.vo.PreviewSummary
import home.felipe.domain.vo.TargetMapping

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    state: PreviewUiState,
    onBack: () -> Unit,
    onGenerateResults: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Água no Bolso") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } }
            )
        },
        bottomBar = {
            if (state is PreviewUiState.Ready) {
                Surface(tonalElevation = 2.dp) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onGenerateResults,
                            enabled = state.summary.applicable > 0
                        ) { Text("Resultados") }
                    }
                }
            }
        }
    ) { padding ->
        when (state) {
            PreviewUiState.Loading -> Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) { CircularProgressIndicator(Modifier.padding(24.dp)) }

            is PreviewUiState.Error -> Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) { Text(state.msg, color = MaterialTheme.colorScheme.error) }

            is PreviewUiState.Ready -> PreviewBody(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                summary = state.summary
            )
        }
    }
}

@Composable
private fun PreviewBody(modifier: Modifier = Modifier, summary: PreviewSummary) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Bloco-resumo (como você descreveu)
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("CSV lido: ${summary.fileName}")
                Text("colunas: ${summary.cols}")
                Text("linhas: ${summary.rows}")
                Text("colunas aplicáveis: ${summary.applicable}")
            }
        }

        // Lista de targets e seus mapeamentos (feature canônica -> header do CSV)
        summary.targets.forEach { t -> TargetMappingCard(t) }

        Spacer(Modifier.height(64.dp)) // respiro para o bottom bar
    }
}

@Composable
private fun TargetMappingCard(target: TargetMapping) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(target.target, style = MaterialTheme.typography.titleMedium)

            target.items.forEach { item ->
                val status = if (item.isMissing) "— (não mapeada)" else item.matchedHeader
                Text(
                    text = "• ${item.canonical}: $status",
                    fontWeight = if (item.isMissing) FontWeight.Normal else FontWeight.Medium,
                    color = if (item.isMissing)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
