package de.wuapps.moredays.ui.scale

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.FragmentScaleBinding
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.handleFab
import de.wuapps.moredays.utilities.hideBottomBar
import de.wuapps.moredays.utilities.showBottomBar
import java.lang.Integer.parseInt

class ScaleFragment : Fragment(), MenuProvider {
    private val args: ScaleFragmentArgs by navArgs()
    private val viewModel: ScaleViewModel by viewModels {
        InjectorUtils.provideScaleViewModelFactory(requireActivity(), args.scale)
    }
    private var _binding: FragmentScaleBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScaleBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.scaleViewModel = viewModel
        handleFab(viewModel.hasFab)
        hideBottomBar()
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return view

    }

    private fun delete() {
        if (!viewModel.isNew) {
            if (!viewModel.isUsed) {
                viewModel.delete()
                view?.post {
                    findNavController().popBackStack()
                }
            }
            val alertDialog: AlertDialog.Builder =
                AlertDialog.Builder(requireNotNull(this.activity))
            alertDialog.setTitle(getString(R.string.title_delete))
            alertDialog.setMessage(getString(R.string.question_delete_scale))
            alertDialog.setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                viewModel.delete()
                view?.post {
                    findNavController().popBackStack()
                }
            }
            alertDialog.setNegativeButton(
                getString(R.string.no)
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun save() {
        var isValid = true
        val name = binding.editTextName.text.toString().trim()
        if (name.isBlank()) {
            binding.editTextName.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        val minValueAsString = binding.editTextMinValue.text.toString().trim()
        val maxValueAsString = binding.editTextMaxValue.text.toString().trim()
        var minValue = 0
        var maxValue = 100
        if (name.isBlank()) {
            binding.editTextName.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        if (minValueAsString.isBlank()) {
            binding.editTextMinValue.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        if (maxValueAsString.isBlank()) {
            binding.editTextMaxValue.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        try {
            minValue = parseInt(minValueAsString)
        } catch (e: NumberFormatException) {
            binding.editTextMinValue.error = getString(R.string.warning_not_an_int)
            isValid = false
        }
        try {
            maxValue = parseInt(maxValueAsString)
        } catch (e: NumberFormatException) {
            binding.editTextMaxValue.error = getString(R.string.warning_not_an_int)
            isValid = false
        }
        if (!isValid)
            return
        viewModel.scale.minValue = minValue
        viewModel.scale.maxValue = maxValue
        viewModel.save()
        view?.post {
            findNavController().popBackStack()
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_help -> {
                showHelp()
                true
            }
            R.id.menu_delete -> {
                delete()
                true
            }
            R.id.menu_save -> {
                save()
                return true
            }
            else -> false
        }
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.help_delete_save_menu, menu)
        if (viewModel.isNew)
            menu.findItem(R.id.menu_delete).isVisible = false
    }

    private fun showHelp(){
        TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(binding.textViewName, getString(R.string.title_scales), getString(R.string.help_scale))
                    .cancelable(false).transparentTarget(true).targetRadius(30)
            ).start()
    }
    override fun onResume() {
        super.onResume()
        hideBottomBar()
    }

    override fun onDestroyView() {
        showBottomBar()
        super.onDestroyView()
        _binding = null
    }
}