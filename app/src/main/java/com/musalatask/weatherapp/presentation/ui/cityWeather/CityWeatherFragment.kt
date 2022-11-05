package com.musalatask.weatherapp.presentation.ui.cityWeather

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.musalatask.weatherapp.databinding.FragmentCityWeatherBinding
import com.musalatask.weatherapp.framework.utils.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Timer

@AndroidEntryPoint
class CityWeatherFragment : Fragment() {

    private var _binding: FragmentCityWeatherBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: CityWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCityWeatherBinding.inflate(inflater, container, false)
        viewModel =
            ViewModelProvider(requireActivity())[CityWeatherViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentTime.collect {
                    binding.currentDate.text = it
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lastUpdate.collect {
                    if (!it.isNullOrEmpty())
                        binding.elapseTime.text = "Last update: $it"
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    setUi(it)
                }
            }
        }
    }

    private fun setUi(state: CityWeatherUiState) {
        if (state.isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            setImagesVisibility(View.INVISIBLE)
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }

        if (state.errorMessage != null) {
            if (state.cityWeather == null) setImagesVisibility(View.INVISIBLE)
        } else if (state.cityWeather != null) {

            setImagesVisibility(View.VISIBLE)
            with(state.cityWeather) {
                displayImageFromUrl(
                    url = "https://openweathermap.org/img/w/${iconId}.png",
                    imageView = binding.weatherIcon
                )
                binding.description.text = description
                binding.temp.text = "${(temp - 273.15).toInt()}"
                binding.metricsUnit.text = "ºC"
                binding.maxTemp.text = "${(tempMax - 273.15).toInt()}ºC"
                binding.minTemp.text = "${(tempMin - 273.15).toInt()}ºC"
                binding.humidity.text = "Humidity: $humidity%"
                binding.feelsLike.text = "Feels like: ${(feelsLike - 273.15).toInt()}ºC"
                if (rainVolumeLastHour != null) {
                    binding.precipitation.text = "last hour: $rainVolumeLastHour mm"
                    binding.imageView6.visibility = View.VISIBLE
                }else binding.imageView6.visibility = View.INVISIBLE
                binding.air.text = "$windSpeed m/s"
                binding.imageView5.rotation = windDirection.toFloat()
                binding.visibility.text = "Visibility: $visibility m"
                binding.pressure.text = "Pressure: $pressure hPa"
                if (snowVolumeLastHour != null) {
                    binding.snow.text = "last hour: $snowVolumeLastHour mm"
                    binding.imageView7.visibility = View.VISIBLE
                }else binding.imageView7.visibility = View.INVISIBLE
                binding.sunset.text = "Sunset: ${DateTimeUtils.formatTime(sunset * 1000L)}"
                binding.sunrise.text = "Sunrise: ${DateTimeUtils.formatTime(sunrise * 1000L)}"
            }
        }
    }

    private fun setImagesVisibility(visibility: Int) {
        binding.imageView2.visibility = visibility
        binding.imageView3.visibility = visibility
        binding.imageView4.visibility = visibility
        binding.imageView5.visibility = visibility
        binding.imageView6.visibility = visibility
        binding.imageView7.visibility = visibility
        binding.imageView8.visibility = visibility
        binding.imageView9.visibility = visibility
    }

    /**
     * Displays an image from a URL in an ImageView.
     */
    private fun displayImageFromUrl(
        url: String?,
        imageView: ImageView,
        listener: RequestListener<Drawable>? = null
    ) {
        val myOptions: RequestOptions = RequestOptions()
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        if (listener != null) {
            Glide.with(requireActivity())
                .load(url)
                .apply(myOptions)
                .listener(listener)
                .into(imageView)
        } else {
            Glide.with(requireActivity())
                .load(url)
                .apply(myOptions)
                .into(imageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}