package de.wuapps.moredays.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R

class ToolsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tools, container, false)

        view.findViewById<Button>(R.id.buttonGoals)
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_goals) }
        view.findViewById<Button>(R.id.buttonChallenges)
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_challenges) }
        view.findViewById<Button>(R.id.buttonScales)
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_scales) }
        view.findViewById<Button>(R.id.buttonHelp)
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_help) }
        view.findViewById<Button>(R.id.buttonTrophies)
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_trophies) }
        view.findViewById<Button>(R.id.buttonActivityEntries)
            .setOnClickListener { findNavController().navigate(R.id.navigation_tools_history) }
        return view
    }
}