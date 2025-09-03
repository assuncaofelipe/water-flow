package home.felipe.water.pocket.analysis.ui.screens.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.repository.AnalysisRepository
import home.felipe.domain.repository.LoggerRepository
import home.felipe.domain.usecase.BuildPreviewSummaryUseCase
import home.felipe.domain.usecase.DiscoverCompatibleTargetsUseCase
import home.felipe.domain.usecase.ListMetadataTargetsUseCase
import home.felipe.domain.vo.DiscoverCompatibleTargetParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviewModel @Inject constructor(
    private val listTargets: ListMetadataTargetsUseCase,
    private val discover: DiscoverCompatibleTargetsUseCase,
    private val buildSummary: BuildPreviewSummaryUseCase,
    private val analysisRepo: AnalysisRepository,
    private val logger: LoggerRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<PreviewUiState>(PreviewUiState.Loading)
    val ui: StateFlow<PreviewUiState> = _ui
    private val COVERAGE = 0.20f

    fun load(sessionId: String) = viewModelScope.launch(Dispatchers.Default) {
        val session = analysisRepo.get(sessionId)
        if (session == null) {
            _ui.value = PreviewUiState.Error("Sessão inválida.")
            return@launch
        }

        val headers =
            session.records.asSequence().flatMap { it.values.keys.asSequence() }.toSet().toList()
        val targetNames = listTargets.execute(Unit)
        val result =
            discover.execute(DiscoverCompatibleTargetParams(targetNames, headers, COVERAGE))

        // persistir no repositório
        val updated = session.copy(
            headersUnion = headers,
            targetsAvailable = result.targets.map { it.target },
            headerMapsByTarget = result.headerMapsByTarget
        )
        analysisRepo.update(updated)
        logger.d(
            "PreviewModel",
            "session $sessionId updated with ${updated.targetsAvailable.size} targets"
        )

        val summary = buildSummary.execute(
            BuildPreviewSummaryUseCase.Params(
                fileName = session.fileName,
                rows = session.records.size,
                cols = headers.size,
                targets = result.targets
            )
        )
        _ui.value = PreviewUiState.Ready(summary)
    }
}