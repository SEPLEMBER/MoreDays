package de.wuapps.moredays.ui.tools.imexport

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.R
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.SQLiteToExcel
import de.wuapps.moredays.database.SQLiteToExcel.ExportCustomFormatter
import de.wuapps.moredays.database.converter.DateTypeConverter
import de.wuapps.moredays.database.converter.DateTypeConverter.Companion.convertDateToString
import de.wuapps.moredays.database.converter.DateTypeConverter.Companion.convertStringToDate
import de.wuapps.moredays.databinding.FragmentImportExportBinding
import de.wuapps.moredays.utilities.DATABASE_NAME
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date


class ImportExportFragment : Fragment(), ExportListener {

    private val TAG = "ImportExportFragment"
    private var _binding: FragmentImportExportBinding? = null
    private val binding get() = _binding!!
    private var hasPermissions = false

    companion object {
        private val handler = Handler(Looper.getMainLooper())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                hasPermissions = true
            } else {
                Log.i("Permission: ", "Denied")
                hasPermissions = false
            }
        }

    private var requestedMimeType = "application/octet-stream"
    private val openRestoreFileChooser =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
            if (result != null) {
                val inputstream = requireContext().contentResolver.openInputStream(result)!!
                binding.progressBarCyclic.visibility = View.VISIBLE
                Thread {
                    try {
                        doRestore(inputstream)
                        handler.post { this.onCompleted() }
                    } catch (e: Exception) {
                        if (inputstream != null) {
                            inputstream!!.close()
                        }
                        handler.post { this.onError(e) }
                    }
                }.start()
            }
        }
    private val openFileCreator = registerForActivityResult(
        ActivityResultContracts.CreateDocument(requestedMimeType)
    ) { result ->
        if (result != null) {
            Log.d(TAG, requireContext().getDatabasePath(DATABASE_NAME).toString())
            val out = requireContext().contentResolver.openOutputStream(result)!!
            binding.progressBarCyclic.visibility = View.VISIBLE
            Thread {
                try {
                    doWithCreatedFile(out)
                    handler.post { this.onCompleted() }
                } catch (e: Exception) {
                    if (out != null) {
                        out!!.close()
                    }
                    handler.post { this.onError(e) }
                }
            }.start()

        }
    }
    private var doWithCreatedFile = { out: OutputStream -> Unit }
    private var x: Unit? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportExportBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.btnExportExcel.setOnClickListener {
            exportToExcel()
        }
        binding.btnExportMd.setOnClickListener {
            exportToMd(binding.radioDesc.isChecked)
        }
        binding.btnBackup.setOnClickListener {
            backup();
        }
        binding.btnRestore.setOnClickListener {
            restore();
        }
        checkPermissions()
        return view
    }

    private fun exportToMd(isDescOrder: Boolean) {
        if (!hasPermissions) {
            actionFailed(getString(R.string.error_permssion_denied))
            return
        }
        requestedMimeType = "text/markdown"
        doWithCreatedFile = { out: OutputStream -> doExportToMd(out, isDescOrder) }
        openFileCreator.launch(getExportFileBaseName("export") + ".md")
    }

    private fun getMoodEmoji(mood: Int): String {
        when(mood) {
            1 -> return getString(R.string.mood_face_1)
            2 -> return getString(R.string.mood_face_2)
            3 -> return getString(R.string.mood_face_3)
            4 -> return getString(R.string.mood_face_4)
            5 -> return getString(R.string.mood_face_5)
            else -> return ""
        }
    }
    private fun doExportToMd(out: OutputStream, isDescOrder: Boolean){
        val db = SQLiteDatabase.openOrCreateDatabase(
            requireContext().getDatabasePath(DATABASE_NAME).absolutePath,
            null
        )
        val journalEntries = HashMap<String, Pair<String, String>>()
        val scaleEntries = ArrayList<Triple<String, String, Float>>()
        val activityEntries = ArrayList<ActivityEntry>()
        val hundredPercentValue = HashMap<String, Int>()
        //goal.name, goal.points, symbol, Activity.name, Activity.points, timestamp
        var formatter = SimpleDateFormat(binding.root.context.getString(R.string.date_ymd))
        var c = db.rawQuery("select date, points from HundredPercentValue order by date asc", null)
        try {
            if (c.moveToFirst()) {
                do {
                    val date: String = c.getString(0)
                    val points: Int = c.getInt(1)
                    hundredPercentValue.put(formatter.format(convertStringToDate(date)), points)
                } while (c.moveToNext())
            }
            c.close()

            c = db.rawQuery("SELECT date,note,mood FROM Journal", null)
            if (c.moveToFirst()) {
                do {
                    val date: String = c.getString(0)
                    val note: String = c.getString(1)
                    val mood: Int = c.getInt(2)
                    journalEntries.put(
                        formatter.format(convertStringToDate(date)),
                        Pair(note, getMoodEmoji(mood))
                    )
                } while (c.moveToNext())
            }
            c.close()
            c = db.rawQuery(
                "SELECT name, value, timestamp FROM ScaleEntry INNER JOIN Scale ON ScaleEntry.scale_id = Scale.uid",
                null
            )
            if (c.moveToFirst()) {
                do {
                    val name: String = c.getString(0)
                    val value: Float = c.getFloat(1)
                    val time: Long = c.getLong(2)
                    scaleEntries.add(
                        Triple(
                            formatter.format(Calendar.getInstance().apply { timeInMillis = time }),
                            name, value
                        )
                    )
                } while (c.moveToNext())
            }
            c.close()
            c = db.rawQuery(
                "select goal.name, goal.points, symbol, Activity.name, Activity.points, timestamp from Goal, Activity, ActivityEntry where goal.uid=Activity.goal_id AND Activity.uid=ActivityEntry.activity_id and goal.is_old = 0",
                null
            )
            if (c.moveToFirst()) {
                do {
                    val goalName: String = c.getString(0)
                    val goalPoints: Int = c.getInt(1)
                    val activityName: String = c.getString(2)
                    val activityPoints: Int = c.getInt(3)
                    val timestamp: Long = c.getLong(4)
                    activityEntries.add(
                        ActivityEntry(
                            formatter.format(
                                Calendar.getInstance().apply { timeInMillis = timestamp }),
                            goalName, goalPoints, activityName, activityPoints
                        )
                    )
                } while (c.moveToNext())
            }
            c.close()
            db.close()
            val history = StringBuffer()
            val allDates = mutableSetOf<String>()
            allDates.addAll(journalEntries.keys)
            allDates.addAll(hundredPercentValue.keys)
            activityEntries.forEach { allDates.add(it.date) }
            val sortedDays = if (isDescOrder) allDates.sortedDescending() else allDates.sorted()
            for (day in sortedDays) {//default ascending
                val journalNote = journalEntries.get(day)?.first ?: ""
                val journalMood = journalEntries.get(day)?.first ?: ""
                var scale = StringBuffer()
                scaleEntries.filter { it.first.equals(day) }
                    .forEach { scale.append("- ${it.second}: ${it.third}") }
                var sumPoints = 0
                var activity = StringBuffer()
                val hundredPercentPoints =
                    hundredPercentValue.get(hundredPercentValue.keys.filter { it <= day }.first())
                        ?: hundredPercentValue.values.first()
                activityEntries.filter { it.date.equals(day) }.forEach {
                    sumPoints += it.activityPoints
                    activity.append("- ${it.activityName} (${it.activityPoints})")
                }
                val reached = "- ${sumPoints} / ${hundredPercentPoints}"
                history.append("## ${day} ${journalMood}")
                if (sumPoints > 0) {
                    history.append(reached)
                    history.append("- ${activity.toString()}")
                }
                if (scale.isNotEmpty()) history.append("- ${scale.toString()}")
                if (journalNote.isNotEmpty()) history.append("- ${journalNote}")
                history.append(history.toString())
            }
            val bf = out.bufferedWriter(Charsets.UTF_8)
            bf.use { out -> out.write(history.toString()) }
        }
        catch(ex: Exception){
            c.close()
            if(db.isOpen) db.close()
            onError(ex)
        }
    }
    private fun backup() {
        if (!hasPermissions) {
            actionFailed(getString(R.string.error_permssion_denied))
            return
        }
        val alertDialog: AlertDialog.Builder =
            AlertDialog.Builder(requireNotNull(this.activity))
        alertDialog.setTitle(getString(R.string.title_delete))
        alertDialog.setMessage(getString(R.string.backup_dialog_text))
        alertDialog.setPositiveButton(
            getString(R.string.yes)
        ) { _, _ ->
            requestedMimeType = "application/octet-stream"
            doWithCreatedFile = { out: OutputStream -> doBackup(out) }
            openFileCreator.launch(getExportFileBaseName("backup") + ".db")
        }
        alertDialog.setNegativeButton(
            getString(R.string.no)
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()

    }

    //Thanks to https://medium.com/dwarsoft/how-to-provide-backup-restore-feature-for-room-database-in-android-d87f111d9c64
    private fun doBackup(savedb: OutputStream) {
        Log.i(TAG, "in dobackup")
        //close the db to make sure all journaling is run and in the single db file see https://www.sqlite.org/wal.html
        MoreDaysDatabase.getInstance(requireContext().applicationContext).close()
        val dbfile = requireContext().applicationContext.getDatabasePath(DATABASE_NAME)
        val buffersize = 8 * 1024
        val buffer = ByteArray(buffersize)
        var bytes_read = buffersize
        val indb: InputStream = FileInputStream(dbfile)
        while (indb.read(buffer, 0, buffersize).also { bytes_read = it } > 0) {
            savedb.write(buffer, 0, bytes_read)
        }
        savedb.flush()
        indb.close()
        savedb.close()
        //exit Application as the viewmodels need to refresh and this would be a hassle, it is not
        //easy to reopen the datbase and refresh the models automatically
        restartApp()
    }


    private fun restore() {
        if (!hasPermissions) {
            actionFailed(getString(R.string.error_permssion_denied))
            return
        }
        val alertDialog: AlertDialog.Builder =
            AlertDialog.Builder(requireNotNull(this.activity))
        alertDialog.setTitle(getString(R.string.title_delete))
        alertDialog.setMessage(getString(R.string.restore_dialog_text))
        alertDialog.setPositiveButton(
            getString(R.string.yes)
        ) { _, _ ->
            openRestoreFileChooser.launch(arrayOf("application/octet-stream"))
        }
        alertDialog.setNegativeButton(
            getString(R.string.no)
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun doRestore(source: InputStream) {
        Log.i(TAG, "in do restore")
        source.use { input ->
            val dbUri = requireContext().getDatabasePath(DATABASE_NAME).toURI()
            MoreDaysDatabase.getInstance(requireContext().applicationContext).close()
            File(dbUri).outputStream().use { output ->
                input.copyTo(output)
            }

        }
        restartApp()
    }

    private fun getExportFileBaseName(suffix: String): String {
        return "MoreDays_${suffix}_${SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(Calendar.getInstance().time)}"
    }

    private fun exportToExcel() {
        if (!hasPermissions) {
            actionFailed(getString(R.string.error_permssion_denied))
            return
        }
        requestedMimeType = "application/octet-stream"
        doWithCreatedFile = { out: OutputStream -> doExportToExcel(out) }
        openFileCreator.launch(getExportFileBaseName("export") + ".xls")
    }

    private fun doExportToExcel(out: OutputStream) {
        var database: SQLiteDatabase? = null
        try {
            val formatter = SimpleDateFormat(getString(R.string.date_time))
            val calendar = Calendar.getInstance()
            database = SQLiteDatabase.openOrCreateDatabase(
                requireContext().getDatabasePath(DATABASE_NAME).absolutePath,
                null
            )
            val sqliteToExcel = SQLiteToExcel(
                database,
                out
            )
            sqliteToExcel.setCustomFormatter(object : ExportCustomFormatter {
                override fun process(columnName: String?, value: String?): String? {
                    var value = value
                    if (columnName.equals("timestamp") && !value.isNullOrBlank()) {
                        calendar.apply { timeInMillis = value!!.toLong() }
                        value = formatter.format(calendar.time)

                    }
                    return value
                }
            })


            sqliteToExcel.exportAllTables()
        } catch (ex: Exception) {
            if (database != null && database!!.isOpen()) {
                database!!.close()
            }
            onError(ex)
        }
    }

    private fun actionFailed(msg: String = "") {
        if (msg.isEmpty())
            Toast.makeText(requireContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG)
                .show()
        else
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()

    }

    private fun checkPermissions() {
        var permission = Manifest.permission.READ_MEDIA_IMAGES
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
            hasPermissions = true
        else
            requestPermissionLauncher.launch(permission)
    }

    override fun onCompleted() {
        binding.progressBarCyclic.visibility = View.GONE
        Snackbar.make(
            requireActivity().findViewById(R.id.container),
            getString(R.string.export_success),
            Snackbar.LENGTH_LONG
        ).show()

    }

    override fun onError(e: Exception?) {
        if (e != null) {
            e!!.message?.let {
                Log.e(TAG, it)
                actionFailed(it)
            }
        }
    }

    private fun restartApp() {
//        restartIntent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(restartIntent)
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
        Runtime.getRuntime().exit(0)
    }
}

interface ExportListener {
    fun onCompleted()
    fun onError(e: Exception?)
}
data class ActivityEntry(
    val date: String,
    val goalName: String,
    val goalPoints: Int,
    val activityName: String,
    val activityPoints: Int
)