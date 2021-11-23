package de.wuapps.moredays.utilities

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import java.io.File

fun generateFile(context: Context, fileName: String): File? {
    val csvFile = File(context.filesDir, fileName)
    csvFile.createNewFile()

    return if (csvFile.exists()) {
        csvFile
    } else {
        null
    }
}

fun getFileIntent(context: Context, file: File): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    val contentUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
    val mimeType = context.contentResolver.getType(contentUri)
    intent.setDataAndType(contentUri, mimeType)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    return intent
}
