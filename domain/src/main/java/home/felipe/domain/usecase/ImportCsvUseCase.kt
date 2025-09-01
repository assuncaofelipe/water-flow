package home.felipe.domain.usecase

import home.felipe.domain.repository.CsvRepository
import home.felipe.domain.vo.ImportCsvParams
import home.felipe.domain.vo.WaterRecord
import javax.inject.Inject

class ImportCsvUseCase @Inject constructor(
    private val repo: CsvRepository
) : UseCase<List<WaterRecord>, ImportCsvParams> {
    override suspend fun execute(params: ImportCsvParams): List<WaterRecord> {
        return repo.readCsv(params)
    }
}