package de.wuapps.moredays.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.BuildConfig
import de.wuapps.moredays.R
import de.wuapps.moredays.utilities.DATABASE_VERSION


class HelpFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_help, container, false)
        view.findViewById<Button>(R.id.buttonCredentials)
            .setOnClickListener{showCredentials()}
        val version = String.format(
            getString(R.string.help_version_pattern),
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
            DATABASE_VERSION
        )
        view.findViewById<TextView>(R.id.texViewVersion).text = version
        view.findViewById<TextView>(R.id.textViewTips).text = HtmlCompat.fromHtml(getString(R.string.help_tips_content), HtmlCompat.FROM_HTML_MODE_LEGACY)
        hideFab()
        return view
    }

    private fun showCredentials() {
        findNavController().navigate(R.id.navigation_help_about)
    }

}