package home.felipe.domain.repository

import android.net.Uri
import home.felipe.domain.vo.ReportContent

interface ReportRepository {
    suspend fun exportCsv(fileName: String, rows: List<Map<String, String>>): Uri
    suspend fun exportPdf(fileName: String, content: ReportContent): Uri
}