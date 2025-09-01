package home.felipe.data.repository

import android.app.Application
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import home.felipe.domain.repository.CsvRepository
import home.felipe.domain.vo.ImportCsvParams
import home.felipe.domain.vo.WaterRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CsvRepositoryImpl @Inject constructor(
    private val application: Application
) : CsvRepository {

    override suspend fun readCsv(uri: ImportCsvParams): List<WaterRecord> {
        return withContext(Dispatchers.IO) {
            val waterRecords: MutableList<WaterRecord> = mutableListOf()

            val contentResolver = application.contentResolver
            val inputStream = contentResolver.openInputStream(uri.uri)
                ?: return@withContext emptyList<WaterRecord>()

            inputStream.use { csvInputStream ->
                csvReader {
                    skipEmptyLine = true
                    delimiter = ';' //
                }.open(csvInputStream) {
                    readAllWithHeaderAsSequence().forEach { rowMap: Map<String, String> ->
                        val detectedDate: String? = rowMap.entries
                            .firstOrNull { entry -> entry.key.contains("date", ignoreCase = true) }
                            ?.value

                        val numericValues: Map<String, Float> =
                            rowMap.mapValues { (_, rawValue: String) ->
                                val normalizedValue = rawValue.replace(",", ".")
                                normalizedValue.toFloatOrNull() ?: Float.NaN
                            }

                        waterRecords += WaterRecord(
                            date = detectedDate,
                            values = numericValues
                        )
                    }
                }
            }

            waterRecords
        }
    }
}
