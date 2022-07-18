package de.wuapps.moredays.ui.tools.imexport

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.FragmentImportExportBinding
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.generateFile
import de.wuapps.moredays.utilities.getFileIntent

class ImportExportFragment : Fragment(), MenuProvider {

    private val CSV_FILENAME = "MoreDaysExport.txt" //.csv might cause trouble to open it, if there is no appropriate app
    private lateinit var binding: FragmentImportExportBinding
    private var goals: MutableList<Goal>? = null
    private val viewModel: ImportExportViewModel by viewModels {
        InjectorUtils.provideImportExportViewModelFactory(requireActivity())
    }
        private val columnHeaders = listOf(
        "DataName",
        "GoalName",
        "GoalColor",
        "GoalPoints",
        "GoalSymbol",
        "ActivityName",
        "ActivityPoints",
        "ActivityTimestamp",
        "ScaleName",
        "ScaleValue",
        "ScaleEntryTimestamp",
        "TrophyType",
        "TrophyCurrentValue",
        "TrophyTimestamp",
        "TrophyEntryId",
        "JournalDate",
        "JournalAffirmation",
        "JournalNote",
        "JournalIcons",
        "JournalBase64Image"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImportExportBinding.inflate(inflater, container, false)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        viewModel.goals.observe(viewLifecycleOwner) {
            it?.let { goals = it as MutableList<Goal> }
        }
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.import_export_menu, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_export -> {
                export()
                return true
            }
            R.id.menu_import -> {
                import()
                return true
            }
            else -> false
        }
    }
    private fun import(){}

    private fun export(){
            val csvFile = generateFile(requireContext(), CSV_FILENAME)
            if (csvFile == null) {
                Toast.makeText(context, getString(R.string.create_export_file_error), Toast.LENGTH_LONG).show()
                return
            }
        csvWriter().open(csvFile, append = false) {
                // Header
                writeRow(columnHeaders)
                goals?.let {
                    goals!!.forEach {
                        writeRow(listOf(it.uid, it.name))
                    }
                }
                }

            Toast.makeText(context, getString(R.string.export_success), Toast.LENGTH_LONG).show()
            val intent = getFileIntent(requireContext(), csvFile)
            startActivity(intent)
    }
}