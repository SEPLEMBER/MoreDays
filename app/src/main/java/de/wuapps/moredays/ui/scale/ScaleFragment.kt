package de.wuapps.moredays.ui.scale

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.wuapps.moredays.R

class ScaleFragment : Fragment() {

    companion object {
        fun newInstance() = ScaleFragment()
    }

    private lateinit var viewModel: ScaleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scale_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScaleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}