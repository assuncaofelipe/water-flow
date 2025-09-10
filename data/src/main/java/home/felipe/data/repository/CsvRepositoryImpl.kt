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
        contentResolver: ContentResolver,
        uri: Uri
    ): Pair<String, List<WaterRecord>> {
        Timber.d("readCsvFromUri uri=$uri")
        val displayName = getDisplayName(contentResolver, uri) ?: "arquivo.csv"

        val records: List<WaterRecord> = buildList {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val rows: List<Map<String, String>> = csvReader {
                    delimiter = ','
                    skipEmptyLine = true
                }.readAllWithHeader(inputStream)

                for (row in rows) {
                    val dateKey =
                        row.keys.firstOrNull { key -> key.contains("date", ignoreCase = true) }
                    val dateValue =
                        dateKey?.let { key -> row[key] }?.takeIf { value -> !value.isNullOrBlank() }

                    val numericValues: Map<String, Float> = row.mapNotNull { (key, value) ->
                        value
                            ?.replace(",", ".", ignoreCase = false)
                            ?.trim()
                            ?.takeIf { cleaned -> cleaned.isNotEmpty() }
                            ?.toFloatOrNull()
                            ?.let { number -> key to number }
                    }.toMap()

                    add(WaterRecord(date = dateValue, values = numericValues))
                }
            }
        }

        Timber.d("CSV lido: nome=$displayName linhas=${records.size}")
        return displayName to records
    }

    private fun getDisplayName(contentResolver: ContentResolver, uri: Uri): String? {
        var name: String? = null
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use { c ->
            val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && c.moveToFirst()) name = c.getString(idx)
        }
        return name
    }
}