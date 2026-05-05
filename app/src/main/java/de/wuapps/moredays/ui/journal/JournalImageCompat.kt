package de.wuapps.moredays.ui.journal

import android.content.Context
import android.net.Uri
import java.io.File

internal object JournalImageCompat {

    private const val IMPORT_DIRECTORY = "journal-images"

    fun glideModel(path: String): Any {
        return if (path.startsWith("content://") || path.startsWith("file://")) {
            Uri.parse(path)
        } else {
            path
        }
    }

    fun importedImageDirectory(context: Context): File {
        return File(context.filesDir, IMPORT_DIRECTORY)
    }

    fun isImportedImage(context: Context, path: String): Boolean {
        if (path.isBlank()) {
            return false
        }

        return runCatching {
            val imageFile = File(path)
            imageFile.parentFile?.canonicalFile == importedImageDirectory(context).canonicalFile
        }.getOrDefault(false)
    }

    fun toImportUri(path: String): Uri? {
        if (path.isBlank()) {
            return null
        }
        if (path.startsWith("content://") || path.startsWith("file://")) {
            return Uri.parse(path)
        }

        val file = File(path)
        return if (file.exists()) Uri.fromFile(file) else null
    }
}
