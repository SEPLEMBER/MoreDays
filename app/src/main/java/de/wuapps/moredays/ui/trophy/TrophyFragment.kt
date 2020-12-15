package de.wuapps.moredays.ui.trophy

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.wuapps.moredays.R

class TrophyFragment : Fragment() {

    companion object {
        fun newInstance() = TrophyFragment()
    }

    private lateinit var viewModel: TrophyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trophy_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TrophyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}