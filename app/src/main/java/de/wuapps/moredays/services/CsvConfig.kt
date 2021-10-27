package de.wuapps.moredays.services

import android.os.Environment
import java.text.DateFormat

data class CsvConfig(
    private val prefix: String = "moreDays",
    private val suffix: String = DateFormat
        .getDateTimeInstance()
        .format(System.currentTimeMillis())
        .toString()
        .replace(",","")
        .replace(" ", "_"),

    val fileName: String = "$prefix-$suffix.csv",
    @Suppress("DEPRECATION")
    val hostPath: String = Environment
        .getExternalStorageDirectory()?.absolutePath?.plus("/Documents/MoreDays") ?: ""
)