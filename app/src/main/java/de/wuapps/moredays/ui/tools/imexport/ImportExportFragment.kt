package de.wuapps.moredays.ui.tools.imexport

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import de.wuapps.moredays.R
import de.wuapps.moredays.database.SQLiteToExcel
import de.wuapps.moredays.database.SQLiteToExcel.ExportCustomFormatter
import de.wuapps.moredays.databinding.FragmentImportExportBinding
import de.wuapps.moredays.utilities.DATABASE_NAME
import java.text.SimpleDateFormat
import java.util.*


class ImportExportFragment : Fragment() {

    private val TAG = "ImportExportFragment"
    private var _binding: FragmentImportExportBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportExportBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.btnExport.setOnClickListener{
            if (hasWritePermission(requireContext()))
            export()
        else
            exportFailed(getString(R.string.error_permssion_denied))}
        return view
    }

    private fun import(){}

    private fun export(){
        val formatter = SimpleDateFormat(getString(R.string.date_time))
        val calendar = Calendar.getInstance()
        val exportFileName = "MoreDaysExport" + formatter.format(calendar.time).replace(".","").replace(":", "").replace(" ", "").replace(",", "")+".xls"
        val sqliteToExcel = SQLiteToExcel(
            requireContext(),
            DATABASE_NAME,
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        )
        sqliteToExcel.setCustomFormatter(object : ExportCustomFormatter {
            override fun process(columnName: String?, value: String?): String? {
                var value = value
                if (columnName.equals("timestamp") && !value.isNullOrBlank())
                {
                        calendar.apply { timeInMillis = value!!.toLong() }
                        value = formatter.format(calendar.time)

                }
                return value
            }
        })


        sqliteToExcel.exportAllTables(exportFileName, object : SQLiteToExcel.ExportListener {
            override fun onStart() {
                binding.progressBarCyclic.visibility = View.VISIBLE
            }
            override fun onCompleted(filePath: String?) {
                binding.progressBarCyclic.visibility = View.GONE
                Snackbar.make(requireActivity().findViewById(R.id.container), getString(R.string.export_success) + "Downloads/" + exportFileName, Snackbar.LENGTH_LONG).show()

            }

            override fun onError(e: java.lang.Exception?) {
                binding.progressBarCyclic.visibility = View.GONE
                if (e != null) {
                    e!!.message?.let { Log.e(TAG, it)
                        exportFailed(it)
                    }
                }

            }
        })
    }

    private fun exportFailed(msg: String = ""){
        if (msg.isEmpty())
            Toast.makeText(requireContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show()
        else
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()

    }


    fun hasWritePermission(context: Context): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}