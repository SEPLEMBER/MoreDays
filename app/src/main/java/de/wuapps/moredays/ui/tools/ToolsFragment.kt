package de.wuapps.moredays.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.FragmentToolsBinding
import de.wuapps.moredays.utilities.handleFab

class ToolsFragment : Fragment() {


    private var _binding: FragmentToolsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentToolsBinding.inflate(inflater, container, false)
        binding.buttonGoals
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_goals) }
        binding.buttonScales
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_scales) }
        binding.buttonHelp
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_help) }
        binding.buttonTrophies
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_trophies) }
        binding.buttonActivityEntries
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_history) }
        binding.buttonSettings
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_settings) }
        binding.buttonImportExport
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_import_export) }
        handleFab(false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}