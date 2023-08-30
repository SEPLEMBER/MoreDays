package de.wuapps.moredays.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFPatriarch
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
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
            val cursor = database.rawQuery(
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
        val cursor = database.rawQuery("PRAGMA table_info($table)", null)
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(1))
        }
        cursor.close()
        return columns
    }

    private fun insertScaleEntriesForChart(){
        val sheet: HSSFSheet = workbook!!.createSheet("1-scale")
        val columns=listOf("name", "value", "timestamp")
        val cursor = database.rawQuery(
            "SELECT ${columns.joinToString(separator=", ")} FROM ScaleEntry INNER JOIN Scale ON ScaleEntry.scale_id = Scale.uid",
            null
        )
        insertQueryRows(cursor, sheet, columns)
    }

    private fun insertPlainJournal(){
        val sheet: HSSFSheet = workbook!!.createSheet("1-journal")
        val columns=listOf("date", "note", "mood", "affirmation")
        val cursor = database.rawQuery("SELECT ${columns.joinToString(separator=", ")} FROM Journal", null)
        insertQueryRows(cursor, sheet, columns)
    }
    private fun insertActivityEntriesForChart(){
        val sheet: HSSFSheet = workbook!!.createSheet("1-activities")
        val columns=listOf<String>("goal.name", "goal.points", "Activity.name", "Activity.points", "timestamp")
        val cursor = database.rawQuery(
            "select ${columns.joinToString(separator=", ")} from Goal, Activity, ActivityEntry where goal.uid=Activity.goal_id AND Activity.uid=ActivityEntry.activity_id and goal.is_old = 0",
            null
        )
        insertQueryRows(cursor, sheet, columns)
    }
    fun exportAllTables(complete: Boolean) {
        workbook = HSSFWorkbook()
        insertActivityEntriesForChart()
        insertScaleEntriesForChart()
        insertPlainJournal()
        if (complete) {
            val tables = allTables
            for (i in tables.indices) {
                if (tables[i] != "android_metadata") {
                    val sheet: HSSFSheet = workbook!!.createSheet(tables[i])
                    createSheet(tables[i], sheet)
                }
            }
        }
        workbook!!.write(outPutStream)
        outPutStream.flush()
        outPutStream.close()
        workbook!!.close()
        database.close()
    }
    private fun createSheet(table: String, sheet: HSSFSheet) {
        val columns = getColumns(table)
        val cursor = database.rawQuery("select * from $table", null)
        insertQueryRows(cursor, sheet, columns)
    }
    private fun insertQueryRows(
        cursor: Cursor,
        sheet: HSSFSheet,
        columns: List<String>
    ) {
        val headerRow: HSSFRow = sheet.createRow(0)
        var cellIndex = 0
        for (i in columns.indices) {
            val columnName = columns[i]
            val cellA: HSSFCell = headerRow.createCell(cellIndex)
            cellA.setCellValue(HSSFRichTextString(columnName))
            cellIndex++

        }

        cursor.moveToFirst()
        var n = 1
        while (!cursor.isAfterLast) {
            val rowA: HSSFRow = sheet.createRow(n)
            cellIndex = 0
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

