package home.felipe.domain.usecase

import home.felipe.domain.repository.CsvRepository
import home.felipe.domain.vo.ImportCsvParams
import home.felipe.domain.vo.WaterRecord
import javax.inject.Inject

class ImportCsvUseCase @Inject constructor(
    private val repo: CsvRepository
) : UseCase<Pair<String, List<WaterRecord>>, ImportCsvParams> {
    override suspend fun execute(params: ImportCsvParams): Pair<String, List<WaterRecord>> {
        return repo.readCsvFromUri(params.contentResolver, params.uri)
    }
}