package home.felipe.domain.repository

import android.content.ContentResolver
import android.net.Uri
import home.felipe.domain.vo.WaterRecord

interface CsvRepository {
    suspend fun readCsvFromUri(cr: ContentResolver, uri: Uri): Pair<String, List<WaterRecord>>
}