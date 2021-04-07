package de.wuapps.moredays.ui.tools.imexport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import de.wuapps.moredays.databinding.FragmentImportExportBinding
import de.wuapps.moredays.utilities.InjectorUtils

class ImportExportFragment : Fragment() {

    private lateinit var binding: FragmentImportExportBinding
    private val viewModel: ImportExportViewModel by viewModels {
        InjectorUtils.provideImportExportViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImportExportBinding.inflate(inflater, container, false)
        //setHasOptionsMenu(true)
        //viewModel.path = requireContext().filesDir.absolutePath
        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.import_export_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.menu_delete -> {
//                delete()
//                true
//            }
//            R.id.menu_save -> {
//                export()
//                return true
//            }
//            R.id.menu_import -> {
//                import()
//                return true
//            }
//            else -> false
//        }
//    }
//
//    private fun delete(){}
//    private fun export(){
//        val uri = Uri.EMPTY
//        val exportFileDescriptor = requireActivity().contentResolver.openFileDescriptor(uri, "w")
//    }
//    private fun import(){}
}