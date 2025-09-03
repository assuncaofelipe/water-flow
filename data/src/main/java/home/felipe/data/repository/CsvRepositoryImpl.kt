package home.felipe.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import home.felipe.domain.repository.CsvRepository
import home.felipe.domain.vo.WaterRecord
import timber.log.Timber
import javax.inject.Inject

class CsvRepositoryImpl @Inject constructor() : CsvRepository {
    override suspend fun readCsvFromUri(
        cr: ContentResolver,
        uri: Uri
    ): Pair<String, List<WaterRecord>> {
        Timber.d("readCsvFromUri: $uri")
        val display = getDisplayName(cr, uri) ?: "arquivo.csv"

        val records = buildList {
            cr.openInputStream(uri)?.use { ips ->
                val rows: List<Map<String, String>> = csvReader {
                    delimiter = ','
                    skipEmptyLine = true
                }.readAllWithHeader(ips)

                for (row in rows) {
                    val date =
                        row.keys.firstOrNull { it.contains("date", ignoreCase = true) }?.let { k ->
                            row[k]?.takeIf { it.isNotBlank() }
                        }
                    val values: Map<String, Float> = row.mapNotNull { (k, v) ->
                        v?.replace(",", ".", false)
                            ?.trim()
                            ?.takeIf { it.isNotEmpty() }
                            ?.toFloatOrNull()
                            ?.let { k to it }
                    }.toMap()
                    add(WaterRecord(date = date, values = values))
                }
            }
        }

        return display to records
    }

    private fun getDisplayName(cr: ContentResolver, uri: Uri): String? {
        var name: String? = null
        val cursor: Cursor? = cr.query(uri, null, null, null, null)
        cursor?.use {
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && it.moveToFirst()) name = it.getString(idx)
        }
        return name
    }
}