package home.felipe.domain.vo

import android.content.ContentResolver
import android.net.Uri

data class ImportCsvParams(
    val contentResolver: ContentResolver,
    val uri: Uri
)
