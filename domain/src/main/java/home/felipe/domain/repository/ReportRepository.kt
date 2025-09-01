package home.felipe.domain.repository

import home.felipe.domain.vo.ReportContent

interface ReportRepository {
    suspend fun exportCsv(
        fileName: String, rows: List<Map<String, String>>
    ): android.net.Uri

    suspend fun exportPdf(
        fileName: String,
        content: ReportContent
    ): android.net.Uri
}