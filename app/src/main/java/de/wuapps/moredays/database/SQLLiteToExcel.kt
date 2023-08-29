package de.wuapps.moredays.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.os.Handler
import android.os.Looper
import de.wuapps.moredays.ui.tools.imexport.ExportListener
import org.apache.poi.hssf.usermodel.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


// thanks to https://github.com/androidmads/SQLite2XL

class SQLiteToExcel constructor(
    val database: SQLiteDatabase,
    val outPutStream: OutputStream
) {
    private var workbook: HSSFWorkbook? = null
    private var mCustomFormatter: ExportCustomFormatter? = null


    /**
     * Set a the custom formatter for the column value output
     * @param customFormatter
     */
    fun setCustomFormatter(customFormatter: ExportCustomFormatter?) {
        mCustomFormatter = customFormatter
    }

    private val allTables: ArrayList<String>
        private get() {
            val tables = ArrayList<String>()
            val cursor = database!!.rawQuery(
                "select name from sqlite_master where type='table' order by name",
                null
            )
            while (cursor.moveToNext()) {
                tables.add(cursor.getString(0))
            }
            cursor.close()
            return tables
        }

    private fun getColumns(table: String): ArrayList<String> {
        val columns = ArrayList<String>()
        val cursor = database!!.rawQuery("PRAGMA table_info($table)", null)
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(1))
        }
        cursor.close()
        return columns
    }

    private fun exportTables(tables: List<String>) {
        workbook = HSSFWorkbook()
        for (i in tables.indices) {
            if (tables[i] != "android_metadata") {
                val sheet: HSSFSheet = workbook!!.createSheet(tables[i])
                createSheet(tables[i], sheet)
            }
        }
        workbook!!.write(outPutStream)
        outPutStream.flush()
        outPutStream.close()
        workbook!!.close()
        database!!.close()
    }

    fun exportAllTables() {
        exportTables(allTables)
    }
    private fun createSheet(table: String, sheet: HSSFSheet) {
        val rowA: HSSFRow = sheet.createRow(0)
        val columns = getColumns(table)
        var cellIndex = 0
        for (i in columns.indices) {
            val columnName = columns[i]
            val cellA: HSSFCell = rowA.createCell(cellIndex)
            cellA.setCellValue(HSSFRichTextString(columnName))
            cellIndex++

        }
        insertItemToSheet(table, sheet, columns)
    }

    private fun insertItemToSheet(table: String, sheet: HSSFSheet, columns: ArrayList<String>) {
        val patriarch: HSSFPatriarch = sheet.createDrawingPatriarch()
        val cursor = database!!.rawQuery("select * from $table", null)
        cursor.moveToFirst()
        var n = 1
        while (!cursor.isAfterLast) {
            val rowA: HSSFRow = sheet.createRow(n)
            var cellIndex = 0
            for (j in columns.indices) {
                val columnName = "" + columns[j]
                val cellA: HSSFCell = rowA.createCell(cellIndex)
                var value = cursor.getString(j)
                if (null != mCustomFormatter) {
                    value = mCustomFormatter!!.process(columnName, value)
                }
                cellA.setCellValue(HSSFRichTextString(value))
                cellIndex++

            }
            n++
            cursor.moveToNext()
        }
        cursor.close()
    }

    interface ExportCustomFormatter {
        fun process(columnName: String?, value: String?): String?
    }


}

