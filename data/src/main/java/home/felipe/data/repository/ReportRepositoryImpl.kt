package home.felipe.data.repository

import android.app.Application
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import home.felipe.domain.repository.ReportRepository
import home.felipe.domain.vo.ReportContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val app: Application
) : ReportRepository {

    override suspend fun exportCsv(fileName: String, rows: List<Map<String, String>>): Uri {
        return withContext(Dispatchers.IO) {
            val file = File(app.cacheDir, fileName)
            file.printWriter().use { out ->
                val headers = rows.firstOrNull()?.keys?.toList().orEmpty()
                out.println(headers.joinToString(","))
                rows.forEach {
                    out.println(headers.joinToString(",") { h -> it[h].orEmpty() })
                }
            }
            FileProvider.getUriForFile(app, "${app.packageName}.provider", file)
        }
    }

    override suspend fun exportPdf(fileName: String, content: ReportContent): Uri {
        return withContext(Dispatchers.IO) {
            val file = File(app.cacheDir, fileName)
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val pdfPage = pdfDocument.startPage(pageInfo)
            val canvas = pdfPage.canvas
            val paint = Paint()
            paint.textSize = 16f
            canvas.drawText(content.title, 40f, 60f, paint)
            var currentY = 90f
            content.summary.forEach { (key, value) ->
                canvas.drawText("- $key: $value", 40f, currentY, paint)
                currentY += 22f
            }
            pdfDocument.finishPage(pdfPage)
            file.outputStream().use { pdfDocument.writeTo(it) }
            pdfDocument.close()
            FileProvider.getUriForFile(app, "${app.packageName}.provider", file)
        }
    }
}
