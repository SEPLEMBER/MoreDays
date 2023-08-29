package de.wuapps.moredays.ui.tools.imexport

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import com.google.android.material.snackbar.Snackbar
import de.wuapps.moredays.R
import de.wuapps.moredays.database.SQLiteToExcel
import de.wuapps.moredays.database.SQLiteToExcel.ExportCustomFormatter
import de.wuapps.moredays.databinding.FragmentImportExportBinding
import de.wuapps.moredays.utilities.DATABASE_NAME
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import com.google.common.io.Files.copy
import de.wuapps.moredays.database.MoreDaysDatabase
import java.io.FileOutputStream
import java.time.LocalDateTime


class ImportExportFragment : Fragment(), ExportListener {

    private val TAG = "ImportExportFragment"
    private val BACKUP_LOCATION_KEY = "backupLocation"
    private var _binding: FragmentImportExportBinding? = null
    private val binding get() = _binding!!
    private var hasPermissions = false
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
                doRestore(inputstream)
            }
        }
    private val openFileCreator = registerForActivityResult(
        ActivityResultContracts.CreateDocument(requestedMimeType)
    ) { result ->
        if (result != null) {
            Log.d(TAG, requireContext().getDatabasePath(DATABASE_NAME).toString())
            val out = requireContext().contentResolver.openOutputStream(result)!!
            binding.progressBarCyclic.visibility = View.VISIBLE
            val handler = Handler(Looper.getMainLooper())
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

    }

    private fun backup() {
        if (!hasPermissions) {
            actionFailed(getString(R.string.error_permssion_denied))
            return
        }
        requestedMimeType = "application/octet-stream"
        doWithCreatedFile = { out: OutputStream -> doBackup(out) }
        openFileCreator.launch(getExportFileBaseName("backup") + ".db")
    }

    private fun doBackup(out: OutputStream) {
        Log.i(TAG, "in dobackup")
        copy(File(requireContext().getDatabasePath(DATABASE_NAME).toString()), out)
        out.close()
    }

    private fun restore() {
        if (!hasPermissions) {
            actionFailed(getString(R.string.error_permssion_denied))
            return
        }
        openRestoreFileChooser.launch(arrayOf("application/octet-stream"))
    }

    private fun doRestore(source: InputStream) {
        Log.i(TAG, "in do restore")
        val alertDialog: AlertDialog.Builder =
            AlertDialog.Builder(requireNotNull(this.activity))
        alertDialog.setTitle(getString(R.string.title_delete))
        alertDialog.setMessage(getString(R.string.question_delete_goal))
        alertDialog.setPositiveButton(
            getString(R.string.yes)
        ) { _, _ ->
            source.use { input ->
                val dbUri = requireContext().getDatabasePath(DATABASE_NAME).toURI()
                MoreDaysDatabase.getInstance(requireContext().applicationContext).close()
                File(dbUri).outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        alertDialog.setNegativeButton(
            getString(R.string.no)
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
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

}

interface ExportListener {
    fun onCompleted()
    fun onError(e: Exception?)
}