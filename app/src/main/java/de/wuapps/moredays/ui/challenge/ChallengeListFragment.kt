package de.wuapps.moredays.ui.challenge

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.wuapps.moredays.R

class ChallengeListFragment : Fragment() {

    companion object {
        fun newInstance() = ChallengeListFragment()
    }

    private lateinit var viewModel: ChallengeListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.challenge_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChallengeListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}