package de.wuapps.moredays.ui.scale

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.ScaleListFragmentBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.journal.JournalListViewModel
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi


class ScaleListFragment : Fragment() {

    private lateinit var binding: ScaleListFragmentBinding

    private val viewModel: ScaleListViewModel by viewModels {
        InjectorUtils.provideScaleListViewModelFactory(requireActivity())
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.w("home", "onCreateView ScaleListFragment")
        binding = ScaleListFragmentBinding.inflate(inflater, container, false)
        binding.recyclerviewScales.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.scale_item_width).toInt()
        )
        val adapter = ScaleAdapter()
        binding.recyclerviewScales.adapter = adapter
        subscribeUi(adapter)

        setFabOnClickListener{ findNavController().navigate(R.id.navigation_scales_scale) }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    @InternalCoroutinesApi
    private fun subscribeUi(adapter: ScaleAdapter) {
        Log.w("home", "subscribeUi ScaleListFragment")
        viewModel.scales.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Scale>)
            }
        })
    }
}