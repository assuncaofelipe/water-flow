package home.felipe.domain.repository

import home.felipe.domain.vo.ImportCsvParams
import home.felipe.domain.vo.WaterRecord

interface CsvRepository {
    suspend fun readCsv(uri: ImportCsvParams): List<WaterRecord>
}