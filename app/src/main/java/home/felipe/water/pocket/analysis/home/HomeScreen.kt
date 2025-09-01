package home.felipe.water.pocket.analysis.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import home.felipe.water.pocket.analysis.shared.SharedRecordsViewModel

@Composable
fun HomeScreen(
    sharedRecordsViewModel: SharedRecordsViewModel,
    onNavigateToResults: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState: HomeViewModel.UiState by viewModel.uiState.collectAsState()

    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                viewModel.importCsv(uri)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Água no Bolso", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            csvPickerLauncher.launch(arrayOf("text/*", "text/csv", "application/csv"))
        }) {
            Text("Importar CSV tratado")
        }

        Spacer(Modifier.height(16.dp))

        when (uiState) {
            is HomeViewModel.UiState.Idle -> {
                Text("Nenhum arquivo importado ainda.")
            }

            is HomeViewModel.UiState.Loading -> {
                Text("Lendo arquivo...")
            }

            is HomeViewModel.UiState.Error -> {
                val message = (uiState as HomeViewModel.UiState.Error).message
                Text("Erro: $message")
            }

            is HomeViewModel.UiState.Loaded -> {
                val records = (uiState as HomeViewModel.UiState.Loaded).records
                LaunchedEffect(records) {
                    sharedRecordsViewModel.setImportedRecords(records)
                }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Registros importados: ${records.size}")
                        Spacer(Modifier.height(8.dp))
                        Text("Escolha o alvo para prever:")
                        Spacer(Modifier.height(8.dp))
                        TargetButtons(onNavigateToResults)
                    }
                }
            }
        }
    }
}

@Composable
private fun TargetButtons(
    onNavigateToResults: (String) -> Unit
) {
    Column {
        listOf("DO", "pH", "Turbidity", "Conductivity", "E_coli").forEach { targetName ->
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onNavigateToResults(targetName) }) {
                Text("Prever $targetName")
            }
        }
    }
}