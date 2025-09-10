package home.felipe.domain.usecase

import home.felipe.domain.repository.LoggerRepository
import home.felipe.domain.vo.PreviewSummary
import javax.inject.Inject

class BuildPreviewSummaryUseCase @Inject constructor(
    private val loggerRepository: LoggerRepository
) : UseCase<PreviewSummary, BuildPreviewSummaryUseCase.Params> {

    data class Params(
        val fileName: String,
        val rows: Int,
        val cols: Int,
        val targets: List<home.felipe.domain.vo.TargetMapping>
    )

    override suspend fun execute(params: Params): PreviewSummary {
        loggerRepository.d(
            TAG,
            "summary rows=${params.rows} cols=${params.cols} applicable=${params.targets.size}"
        )
        return PreviewSummary(
            fileName = params.fileName,
            rows = params.rows,
            cols = params.cols,
            applicable = params.targets.size,
            targets = params.targets
        )
    }

    private companion object {
        const val TAG = "BuildPreviewSummaryUC"
    }
}