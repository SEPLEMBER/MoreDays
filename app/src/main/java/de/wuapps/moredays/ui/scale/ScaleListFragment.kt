package de.wuapps.moredays.ui.scale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.FragmentScaleListBinding
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.getFlexboxLayoutManager
import de.wuapps.moredays.utilities.setFabOnClickListener
import de.wuapps.moredays.utilities.showFab


class ScaleListFragment : Fragment() {

    private var _binding: FragmentScaleListBinding? = null
    private val binding get() = _binding!!
    private var _adapter: ScaleAdapter? = null
    private val adapter get() = _adapter!!
    private val viewModel: ScaleListViewModel by viewModels {
        InjectorUtils.provideScaleListViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScaleListBinding.inflate(inflater, container, false)
        binding.recyclerviewScales.layoutManager = getFlexboxLayoutManager(requireContext())
        _adapter = ScaleAdapter()
        binding.recyclerviewScales.adapter = adapter
        subscribeUi()
        setFabOnClickListener{ findNavController().navigate(R.id.navigation_scales_scale) }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    private fun subscribeUi() {
        viewModel.scales.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it as MutableList<Scale>)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }
}