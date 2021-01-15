package de.wuapps.moredays.ui.tools

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import de.wuapps.moredays.BuildConfig
import de.wuapps.moredays.R
import de.wuapps.moredays.utilities.DATABASE_VERSION

class HelpFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val version = String.format(
                getString(R.string.help_version_pattern),
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
                DATABASE_VERSION
            )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_help, container, false)
        view.findViewById<Button>(R.id.buttonCredentials)
            .setOnClickListener{showCredentials()}
        return view
    }

    private fun showCredentials() {
//        val fragment = LibsBuilder()
//            .withFields(R.string::class.java.fields) // in some cases it may be needed to provide the R class, if it can not be automatically resolved
//            //.withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries") // optionally apply modifications for library information
//            .supportFragment()

//        //val fragmentManager = activity?.supportFragmentManager
        //fragmentManager!!.beginTransaction().replace(R.id.fragmentCredentials, fragment).commit
        findNavController().navigate(R.id.navigation_help_about)
        //findNavController().navigate(HelpFragmentDirections.navigationHelpAbout(fragment))
        //layout.addView(fragment)

    }

}