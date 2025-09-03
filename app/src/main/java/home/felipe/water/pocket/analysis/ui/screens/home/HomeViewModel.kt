package home.felipe.water.pocket.analysis.ui.screens.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.repository.AnalysisRepository
import home.felipe.domain.repository.CsvRepository
import home.felipe.domain.vo.AnalysisSession
import home.felipe.water.pocket.analysis.ui.models.RecentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val csvRepository: CsvRepository,
    private val analysisRepo: AnalysisRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui

    fun onImportCsvUris(
        uris: List<Uri>,
        onNavigateToPreview: (String) -> Unit
    ) {
        // Inicie no Main; faça trabalhos pesados com withContext(IO/Default)
        viewModelScope.launch {
            try {
                _ui.emit(_ui.value.copy(loading = true, error = null))

                val selected = uris.first()
                Timber.d("URI selecionado: $selected")

                // I/O: ler CSV
                val (fileName, records) = withContext(Dispatchers.IO) {
                    csvRepository.readCsvFromUri(app.contentResolver, selected)
                }
                Timber.d("CSV lido: $fileName - ${records.size} linhas")

                val dates = records.map { it.date }
                val sid = UUID.randomUUID().toString()

                // Persistência leve (pode ser Default)
                withContext(Dispatchers.Default) {
                    analysisRepo.create(
                        AnalysisSession(
                            id = sid,
                            fileName = fileName,
                            records = records,
                            dates = dates
                        )
                    )
                }
                Timber.d("Sessão criada: $sid")

                // Atualiza 'Recentes' (estamos no Main)
                val headersUnion =
                    records.asSequence().flatMap { it.values.keys.asSequence() }.toSet().toList()
                val rf = RecentFile(name = fileName, rows = records.size, cols = headersUnion.size)
                _ui.emit(
                    _ui.value.copy(
                        recents = listOf(rf) + _ui.value.recents.take(9),
                        loading = false
                    )
                )

                withContext(Dispatchers.Main) {
                    Timber.d("Navigate -> Preview sid=$sid")
                    onNavigateToPreview(sid)
                }
            } catch (t: Throwable) {
                Timber.d("Falha onImportCsvUris: ${t.message}")
                _ui.emit(_ui.value.copy(loading = false, error = t.message ?: "Falha ao importar"))
            }
        }
    }

    fun onOpenRecent(
        rf: RecentFile,
        onNavigateToPreview: (String) -> Unit
    ) {
        viewModelScope.launch {
            // gere/recupere o sid (faça I/O/Default se necessário com withContext)
            val sid = UUID.randomUUID().toString()
            Timber.d("Open recent '${rf.name}' -> sid=$sid")

            withContext(Dispatchers.Main) {
                onNavigateToPreview(sid)
            }
        }
    }
}
