package com.musalatask.weatherapp.presentation.ui.myCities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.musalatask.weatherapp.common.Constants
import com.musalatask.weatherapp.databinding.FragmentMyCitiesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyCitiesFragment : Fragment() {

    private var _binding: FragmentMyCitiesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: MyCitiesViewModel
    private lateinit var adapter: MyCitiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMyCitiesBinding.inflate(inflater, container, false)
        viewModel =
            ViewModelProvider(this)[MyCitiesViewModel::class.java]

        adapter = MyCitiesAdapter(
            deleteAction = viewModel::deleteCityByName,
            selectAction = this::selectCity
        )
        binding.cities.layoutManager = LinearLayoutManager(requireContext())
        binding.cities.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.items.collect{
                adapter.submitList(it)
            }
        }
    }

    private fun selectCity(cityName: String){
        //Setting the result in the stateHandle of the previousBackStackEntry before navigating back to CityWeatherFragment
        //will allow CityWeatherFragment to access the result in the stateHandle of its currentBackStackEntry
        val savedStateHandle = findNavController().previousBackStackEntry?.savedStateHandle
        savedStateHandle?.set(Constants.SELECTED_CITY_KEY, cityName)
        findNavController().navigateUp()
    }
}