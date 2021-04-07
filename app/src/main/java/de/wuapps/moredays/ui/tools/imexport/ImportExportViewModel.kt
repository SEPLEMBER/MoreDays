package de.wuapps.moredays.ui.tools.imexport

import androidx.lifecycle.ViewModel
import de.wuapps.moredays.database.dao.*


class ImportExportViewModel(
    private val goalDao: GoalDao,
    private val activityDao: ActivityDao,
    private val scaleDao: ScaleDao,
    private val activityEntryDao: ActivityEntryDao,
    private val scaleEntryDao: ScaleEntryDao,
    private val journalDao: JournalDao,
    private val trophyDao: TrophyDao
) : ViewModel() {
//    private val REPLACEMENT_COMMA = "<KOMMA>"
//    private val REPLACEMENT_SEMICOLON = "<SEMICOLON>"
//    private val PATH_SEPARATOR = "/"
//    private val columnHeaders = arrayOf(
//        "DataName",
//        "GoalName",
//        "GoalColor",
//        "GoalPoints",
//        "GoalSymbol",
//        "ActivityName",
//        "ActivityPoints",
//        "ActivityTimestamp",
//        "ScaleName",
//        "ScaleValue",
//        "ScaleEntryTimestamp",
//        "TrophyType",
//        "TrophyCurrentValue",
//        "TrophyTimestamp",
//        "TrophyEntryId",
//        "JournalDate",
//        "JournalAffirmation",
//        "JournalNote",
//        "JournalIcons",
//        "JournalBase64Image"
//    )
//
//    private val SEPARATOR = ';'
//    private val LIST_SEPARATOR = "::"
//    private val SEPARATOR_REPLACER = ','
//    private val LINE_END = "\n"
//    private val LINE_END_REPLACER = " "
//    private val numberFormat = NumberFormat.getInstance()
//    private val dateFormat = SimpleDateFormat("yy-MM-dd HH:mm")
//    private val goals = ArrayList<GoalAndRelatedActivities>()
//    private val activities = ArrayList<Activity>()
//    private val scales = ArrayList<Scale>()
//    private val activityEntries = ArrayList<ActivityEntryAndRelatedData>()
//    private val scaleEntries = ArrayList<ScaleData>()
//    private val trophies = ArrayList<TrophyAndRelatedGoal>()
//    private val journal = ArrayList<Journal>()
//    var goalsAreSelected = false
//    var activityEntriesAreSelected = false
//    var scalesAreSelected = false
//    var scaleEntriesAreSelected = false
//    var trophiesAreSelected = false
//    var journalIsSelected = false
//    var path = ""
//    var isBusy = false
//
//    init {
//        goalDao.getAllWithRelatedActivities().onEach { goals.addAll(it) }.launchIn(viewModelScope)
//        activityEntryDao.getAllAndRelatedData().onEach { activityEntries.addAll(it) }
//            .launchIn(viewModelScope)
//        scaleEntryDao.getAllScaleData().onEach { scaleEntries.addAll(it) }.launchIn(viewModelScope)
//        trophyDao.getAllWithGoal().onEach { trophies.addAll(it) }.launchIn(viewModelScope)
//        journalDao.getAll().onEach { journal.addAll(it) }.launchIn(viewModelScope)
//    }
//
//    private fun exportToCsv(exportFileDescriptor: ParcelFileDescriptor): String {
//        if (exportFileDescriptor == null) return "paramenter not null"
//        var printWriter: PrintWriter? = null
//        var exportFileDescriptor: ParcelFileDescriptor? = null
//        var message: String = ""
//        try {
//
//            printWriter = PrintWriter(FileWriter(exportFileDescriptor!!.fileDescriptor))
//            writeCSVRow(printWriter, columnHeaders)
//            if (goalsAreSelected) {
//                for (goal in goals) {
//                    val goalRow = arrayOf(
//                        "Goal",
//                        goal.goal!!.name,
//                        goal.goal!!.color.toString(),
//                        goal.goal!!.points.toString(),
//                        goal.goal!!.symbol.toString()
//                    )
//                    writeCSVRow(printWriter, goalRow)
//                    for (activity in goal.activities) {
//                        val activityRow = arrayOf(
//                            "Activity",
//                            goal.goal!!.name,
//                            goal.goal!!.color.toString(),
//                            goal.goal!!.points.toString(),
//                            goal.goal!!.symbol.toString(),
//                            activity.name,
//                            activity.points.toString()
//                        )
//                        writeCSVRow(printWriter, activityRow)
//                    }
//                }
//            }
//            if (scalesAreSelected) {
//                for (scale in scales) {
//                    val scaleRow = arrayOf(
//                        "Scale",
//                        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
//                        scale.name
//                    )
//                    writeCSVRow(printWriter, scaleRow)
//                }
//            }
//            if (activityEntriesAreSelected) {
//                for (entry in activityEntries) {
//                    val entryRow = arrayOf(
//                        "Entry",
//                        entry.goal.name,
//                        entry.goal.name,
//                        entry.goal.color.toString(),
//                        entry.goal.points.toString(),
//                        entry.goal.symbol.toString(),
//                        entry.activity.name,
//                        entry.activity.points.toString(),
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        dateFormat.format(entry.activityEntry.timestamp.time)
//                    )
//                    writeCSVRow(printWriter, entryRow)
//                }
//            }
//            if (scaleEntriesAreSelected) {
//                for (scaleEntry in scaleEntries) {
//                    val scaleValueRow = arrayOf(
//                        "ScaleValue",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        scaleEntry.scale!!.name,
//                        numberFormat.format(scaleEntry.scaleEntry.value),
//                        dateFormat.format(scaleEntry.scaleEntry.timestamp.time)
//                    )
//                    writeCSVRow(printWriter, scaleValueRow)
//                }
//                if (trophiesAreSelected) {
//                    for (trophy in trophies) {
//                        val trophyRow = arrayOf(
//                            "Trophy",
//                            trophy.goal.name,
//                            trophy.goal.name,
//                            trophy.goal.color.toString(),
//                            trophy.goal.points.toString(),
//                            trophy.goal.symbol.toString(),
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            trophy.trophy.value.toString(),
//                            dateFormat.format(trophy.trophy.timestamp.time),
//                            trophy.trophy.activityEntryId.toString()
//                        )
//                        writeCSVRow(printWriter, trophyRow)
//                    }
//                }
//            }
//            if (journalIsSelected) {
//                for (journalEntry in journal) {
//                    val journalRow = arrayOf(
//                        "JournalEntry",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        "",
//                        journalEntry.date,
//                        prepareStringForExport(journalEntry.affirmation),
//                        prepareStringForExport(journalEntry.note),
//                        encode(journalEntry.imageFilename),
//                        journalEntry.icons.joinToString(LIST_SEPARATOR)
//                    )
//                    writeCSVRow(printWriter, journalRow)
//                }
//            }
//        } catch (ex: IOException) {
//            message = ex.message ?: "IOException"
//        } finally {
//            if (printWriter != null) try {
//                printWriter.flush()
//                printWriter.close()
//            } catch (ignored: Exception) {
//                //ignore
//            }
//            if (exportFileDescriptor != null) try {
//                exportFileDescriptor.close()
//            } catch (ignored: Exception) {
//                //ignore
//            }
//        }
//        return message
//    }
//
//    private fun prepareStringForExport(txt: String): String {
//        return txt.replace(",", REPLACEMENT_COMMA).replace(";", REPLACEMENT_SEMICOLON)
//    }
//
//    private fun convertStringForImport(txt: String): String {
//        return txt.replace(REPLACEMENT_COMMA, ",").replace(REPLACEMENT_SEMICOLON, ";")
//    }
//
//    private fun removePathFromFilename(filename: String): String? {
//        return filename.substring(filename.lastIndexOf(PATH_SEPARATOR) + 1)
//    }
//
//    private fun isExternalStorageReadable(): Boolean {
//        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY
//    }
//
//    private fun isExternalStorageWritable(): Boolean {
//        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
//    }
//
//    @Throws(IOException::class)
//    private fun copyFile(source: File, dest: File) {
//        val `is`: InputStream = FileInputStream(source)
//        val os: OutputStream = FileOutputStream(dest)
//        copyStreams(`is`, os)
//    }
//
//    @Throws(IOException::class)
//    private fun copyStreams(`is`: InputStream?, os: OutputStream?) {
//        try {
//            val buffer = ByteArray(1024)
//            var length: Int
//            while (`is`!!.read(buffer).also { length = it } > 0) {
//                os!!.write(buffer, 0, length)
//            }
//        } finally {
//            `is`?.close()
//            os?.close()
//        }
//    }
//
//    private fun importData(parcelFileDescriptor: ParcelFileDescriptor): String {
//        //check if entry exists, if not, insert
//        var message: String = ""
//        val rows: MutableList<String> = ArrayList()
//        var bufferedReader: BufferedReader? = null
//        var inputStreamReader: InputStreamReader? = null
//        var fileInputStream: FileInputStream? = null
//        try {
//            fileInputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
//            inputStreamReader = InputStreamReader(fileInputStream, StandardCharsets.UTF_8)
//            bufferedReader = BufferedReader(inputStreamReader)
//            var row: String
//            while (bufferedReader.readLine().also { row = it } != null) {
//                rows.add(row)
//            }
//        } catch (ex: UnsupportedEncodingException) {
//            message = ex.message ?: "UnsupportedEncodingException"
//        } catch (ex: IOException) {
//            message = ex.message ?: "IOException"
//        } finally {
//            if (bufferedReader != null) {
//                try {
//                    bufferedReader.close()
//                } catch (ex: IOException) {
//                    Log.e("ImportExport", ex.message ?: "IOException")
//                }
//            }
//            if (inputStreamReader != null) {
//                try {
//                    inputStreamReader.close()
//                } catch (ex: IOException) {
//                    Log.e("ImportExport", ex.message ?: "IOException")
//                }
//            }
//            if (fileInputStream != null) {
//                try {
//                    fileInputStream.close()
//                } catch (ex: IOException) {
//                    Log.e("ImportExport", ex.message ?: "IOException")
//                }
//            }
//            if (parcelFileDescriptor != null) {
//                try {
//                    parcelFileDescriptor.close()
//                } catch (ex: IOException) {
//                    Log.e("ImportExport", ex.message ?: "IOException")
//                }
//            }
//        }
//        if (rows.size > 1) {
//            //generate map with fields and index
//            val mapFieldToIndex: MutableMap<String, Int> = HashMap()
//            val headers = rows[0]
//            val separatorString: String =
//                SEPARATOR.toString()
//            val thiscolumnHeaders =
//                headers.split(separatorString.toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray() //-1 to handle ;; in general
//            for (i in columnHeaders.indices) {
//                for (j in thiscolumnHeaders.indices) if (thiscolumnHeaders[j].trim { it <= ' ' }
//                        .toLowerCase() == columnHeaders[i].trim { it <= ' ' }
//                        .toLowerCase()) {
//                    mapFieldToIndex[columnHeaders[i]] = j
//                    break
//                }
//            }
//            for (i in 1 until rows.size) {
//                val columns =
//                    rows[i].split(separatorString.toRegex()).dropLastWhile { it.isEmpty() }
//                        .toTypedArray()
//                if (columns.size == 0) continue
//                when (columns[0]) {
//                    "Goal" -> if (goalsAreSelected) extractGoal(
//                        mapFieldToIndex,
//                        columns
//                    )
//                    "Activity" -> if (goalsAreSelected) extractActivity(
//                        mapFieldToIndex,
//                        columns
//                    )
//                    "Scale" -> if (scalesAreSelected) extractScale(mapFieldToIndex, columns)
//                    "ScaleEntry" -> if (scaleEntriesAreSelected) extractScaleEntry(
//                        mapFieldToIndex,
//                        columns
//                    )
//                    "ActivityEntry" -> if (activityEntriesAreSelected) extractActivityEntry(
//                        mapFieldToIndex,
//                        columns
//                    )
//                    "Trophy" -> if (trophiesAreSelected) extractTrophyEntry(
//                        mapFieldToIndex,
//                        columns
//                    )
//                    "Journal" -> if (journalIsSelected) extractJournalEntry(
//                        mapFieldToIndex,
//                        columns
//                    )
//                }
//            }
//        }
//
//
//        return message
//    }
//
//    private fun extractActivityEntry(
//        mapFieldToIndex: MutableMap<String, Int>,
//        columns: Array<String>
//    ) {
//
//    }
//
//    private fun extractScaleEntry(
//        mapFieldToIndex: MutableMap<String, Int>,
//        columns: Array<String>
//    ) {
//
//    }
//
//    private fun extractJournalEntry(
//        mapFieldToIndex: MutableMap<String, Int>,
//        columns: Array<String>
//    ) {
//
//    }
//
//    private fun extractTrophyEntry(
//        mapFieldToIndex: MutableMap<String, Int>,
//        columns: Array<String>
//    ) {
//
//    }
//
//    private fun extractDateFromString(dateString: String): Date {
//        var date = Date()
//        try {
//            date = dateFormat.parse(dateString)
//        } catch (ex: ParseException) {
//            Log.e("import", ex.message!!)
//        }
//        return date
//    }
//
//
//    private fun extractScale(
//        map: Map<String, Int>,
//        columns: Array<String>,
//    ): Scale? {
//        val name = extractStringValue(map, "ScaleName", columns)
//        if (name.length == 0) return null
//        var scale: Scale? = scales.firstOrNull { it.name == name }
//        if (scale != null) return scale
//        scale = Scale(name)
//        return scale
//    }
//
//    private fun extractActivity(
//        map: Map<String, Int>,
//        columns: Array<String>
//    ): Activity? {
//        val activityName = extractStringValue(map, "ActivityName", columns)
//        if (activityName.length == 0) return null
//        var activity: Activity? = activities.firstOrNull { it.name == activityName }
//        if (activity != null) return activity
//        val goal: Goal = extractGoal(map, columns) ?: return null
//        val points = extractIntegerValue(map, "ActivityPoints", columns)
//        return Activity(activityName, goal.uid, points)
//    }
//
//    private fun extractGoal(
//        map: Map<String, Int>,
//        columns: Array<String>
//    ): Goal? {
//        val goalName = extractStringValue(map, "GoalName", columns)
//        if (goalName.length == 0) return null
//        var goal: Goal? = goals.firstOrNull { it.goal!!.name == goalName }?.goal
//        if (goal != null) return null
//        val color = extractIntegerValue(map, "GoalColor", columns)
//        val points = extractIntegerValue(map, "GoalPoints", columns)
//        val symbol = extractIntegerValue(map, "GoalSymbol", columns)
//        return Goal(goalName, color, points, symbol)
//    }
//
//    private fun extractStringValue(
//        map: Map<String, Int>,
//        key: String,
//        columns: Array<String>
//    ): String {
//        val index = map[key]
//        return if (index != null && columns.size > index) {
//            columns[index]
//        } else ""
//    }
//
//    private fun extractIntegerValue(
//        map: Map<String, Int>,
//        key: String,
//        columns: Array<String>
//    ): Int {
//        val index = map[key]
//        if (index != null && columns.size > index) {
//            val tmp = columns[index].trim { it <= ' ' }
//            if (tmp.matches(Regex("[-]*[0-9]*"))) {
//                try {
//                    return tmp.toInt()
//                } catch (ex: NumberFormatException) {
//                    Log.e("import", ex.message!!)
//                }
//            }
//        }
//        return -1
//    }
//
//    private fun extractFloatValue(
//        map: Map<String, Int>,
//        key: String,
//        columns: Array<String>
//    ): Float {
//        val index = map[key]
//        if (index != null && columns.size > index) {
//            try {
//                val number = numberFormat.parse(columns[index].trim { it <= ' ' })
//                return number.toFloat()
//            } catch (ex: ParseException) {
//                Log.e("import", ex.message!!)
//            }
//        }
//        return -1f
//    }
//
//    private fun deleteData() {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (goalsAreSelected) {
//                goalDao.deleteAll()
//            }
//            if (journalIsSelected) {
//                journalDao.deleteAll()
//            }
//            if (activityEntriesAreSelected) {
//                activityEntryDao.deleteAll()
//            }
//            if (scalesAreSelected) {
//                scaleDao.deleteAll()
//            }
//            if (scaleEntriesAreSelected) {
//                scaleEntryDao.deleteAll()
//            }
//            if (trophiesAreSelected) {
//                trophyDao.deleteAll()
//            }
//        }
//    }
//
//    private fun writeCSVRow(printWriter: PrintWriter, nextLine: Array<String>?) {
//        if (nextLine == null) return
//        val sb = StringBuffer()
//        for (i in nextLine.indices) {
//            if (i != 0) {
//                sb.append(SEPARATOR)
//            }
//            val nextElement = nextLine[i]
//            nextElement.replace(
//                LINE_END,
//                LINE_END_REPLACER
//            )
//            nextElement.replace(
//                SEPARATOR,
//                SEPARATOR_REPLACER
//            )
//            sb.append(nextElement)
//        }
//        sb.append(LINE_END)
//        printWriter.write(sb.toString())
//    }
//
//
//    fun decode(date: String, base64String: String) {
//        val imgBytesData: ByteArray = Base64.decode(
//            base64String,
//            Base64.DEFAULT
//        )
//        val destFile = File(path, date + ".jpg")
//        var fileOutputStream: FileOutputStream? = null
//        var bufferedOutputStream: BufferedOutputStream? = null
//        try {
//            fileOutputStream = FileOutputStream(destFile.absolutePath)
//            bufferedOutputStream = BufferedOutputStream(fileOutputStream)
//            bufferedOutputStream.write(imgBytesData)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            bufferedOutputStream?.run {
//                flush()
//                close()
//            }
//            fileOutputStream?.run {
//                flush()
//                close()
//            }
//        }
//    }
//
//    private fun encode(filename: String): String {
//        var stream: ByteArrayOutputStream? = null
//        var encoded = ""
//        try {
//            val file = File(filename)  //File(path, date + ".jpg")
//            if (file.exists()) {
//                val options = BitmapFactory.Options()
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888
//                val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
//                stream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                encoded = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
//            } else {
//                return encoded
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            stream?.run {
//                flush()
//                close()
//            }
//        }
//        return encoded
//    }
}
