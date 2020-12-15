package de.wuapps.moredays.ui.goal

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.wuapps.moredays.R

class GoalListFragment : Fragment() {

    companion object {
        fun newInstance() = GoalListFragment()
    }

    private lateinit var viewModel: GoalListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.goal_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GoalListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}