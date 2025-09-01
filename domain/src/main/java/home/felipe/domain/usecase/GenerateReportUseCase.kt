package home.felipe.domain.usecase

import android.net.Uri
import home.felipe.domain.repository.ReportRepository
import home.felipe.domain.vo.GenerateReportParams
import home.felipe.domain.vo.ReportContent
import javax.inject.Inject

class GenerateReportUseCase @Inject constructor(
    private val repo: ReportRepository
) : UseCase<Pair<Uri, Uri>, GenerateReportParams> {

    override suspend fun execute(params: GenerateReportParams): Pair<Uri, Uri> {
        val rows = params.result.predictions.mapIndexed { i, y ->
            mapOf(
                "index" to i.toString(),
                "date" to (params.dates.getOrNull(i) ?: ""),
                "prediction_${params.result.target}" to y.toString()
            )
        }
        val csv = repo.exportCsv("${params.fileName}.csv", rows)
        val pdf = repo.exportPdf(
            "${params.fileName}.pdf",
            ReportContent(
                title = "Previsões — ${params.result.target}",
                summary = mapOf(
                    "Média" to "%.3f".format(params.result.stats.mean),
                    "Mínimo" to "%.3f".format(params.result.stats.min),
                    "Máximo" to "%.3f".format(params.result.stats.max)
                ),
                series = listOf(params.result.target to params.result.predictions)
            )
        )
        return csv to pdf
    }
}