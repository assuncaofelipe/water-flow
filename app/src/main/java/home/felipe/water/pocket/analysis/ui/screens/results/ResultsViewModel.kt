package home.felipe.water.pocket.analysis.ui.screens.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import home.felipe.domain.repository.AnalysisRepository
import home.felipe.domain.repository.TFLiteRepository
import home.felipe.domain.usecase.RunInferenceUseCase
import home.felipe.domain.util.HeaderMatcher
import home.felipe.domain.vo.PredictionResult
import home.felipe.domain.vo.RunInferenceParams
import home.felipe.water.pocket.analysis.ui.shared.toResultsAllCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val runInference: RunInferenceUseCase,
    private val tflite: TFLiteRepository,
    private val analysisRepo: AnalysisRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val ui: StateFlow<ResultsUiState> = _ui

    fun runAll(sessionId: String) = viewModelScope.launch(Dispatchers.Default) {
        val session = analysisRepo.get(sessionId)
        if (session == null) {
            _ui.value = ResultsUiState.Error("Sessão inválida.")
            return@launch
        }
        val records = session.records
        if (records.isEmpty()) {
            _ui.value = ResultsUiState.Error("Nenhum dado.")
            return@launch
        }

        // Se Preview foi removida do fluxo, compute aqui também:
        val headers = if (session.headersUnion.isEmpty())
            records.asSequence().flatMap { it.values.keys.asSequence() }.toSet().toList()
        else session.headersUnion

        val metas = tfliteTargets() // ler assets/metadata/*.json (igual antes)
        val headerMaps = if (session.headerMapsByTarget.isEmpty())
            metas.associateWith { name ->
                val meta = tflite.loadFeatureMeta("metadata/$name.json")
                HeaderMatcher.buildMapping(meta.featuresOrder, headers)
            }
        else session.headerMapsByTarget

        val targets =
            (if (session.targetsAvailable.isEmpty()) headerMaps.keys.toList() else session.targetsAvailable)

        val results = mutableListOf<PredictionResult>()
        for (target in targets) {
            val metaPath = "metadata/$target.json"
            val tflitePath = "tflite/$target.tflite"
            val headerMap = headerMaps[target]

            val res = runCatching {
                runInference.execute(
                    RunInferenceParams(
                        tensorFlowLiteAssetName = tflitePath,
                        metaAssetName = metaPath,
                        targetName = target,
                        records = records,
                        headerMap = headerMap,
                        standardizeWithMeta = false
                    )
                )
            }.onFailure { Timber.e("fail $target - $it") }.getOrNull() ?: continue

            results += res
        }

        if (results.isEmpty()) {
            _ui.value = ResultsUiState.Error("Nenhum resultado gerado.")
            return@launch
        }
        val cards = results.map { it.toResultsAllCard(dates = session.dates) } // seu mapper atual
        _ui.value = ResultsUiState.Ready(cards)
    }

    private fun tfliteTargets(): List<String> {
        // ler lista de nomes em assets/metadata/*.json como você já faz
        // ou expor via ListMetadataTargetsUseCase se preferir
        // (deixei curto para foco no desacoplamento)
        return emptyList()
    }
}
