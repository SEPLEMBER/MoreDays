package de.wuapps.moredays.ui.scale

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.ScaleListFragmentBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import kotlinx.coroutines.InternalCoroutinesApi


class ScaleListFragment : Fragment() {

    private lateinit var binding: ScaleListFragmentBinding

    @InternalCoroutinesApi
    private lateinit var viewModel: ScaleListViewModel

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.w("home", "onCreateView ScaleListFragment")
        val application = requireNotNull(this.activity).application
        val db = MoreDaysDatabase.getInstance(application)
        viewModel = ViewModelProvider(
            this,
            ScaleListViewModelFactory(
                db.scaleDao
            )
        ).get(ScaleListViewModel::class.java)

        binding = ScaleListFragmentBinding.inflate(inflater, container, false)
        binding.recyclerviewScales.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.scale_item_width).toInt()
        )
        val adapter = ScaleAdapter()
        binding.recyclerviewScales.adapter = adapter
        subscribeUi(adapter)

        binding.fabAdd.setOnClickListener { findNavController().navigate(R.id.navigation_scales_scale) }
        return binding.root
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