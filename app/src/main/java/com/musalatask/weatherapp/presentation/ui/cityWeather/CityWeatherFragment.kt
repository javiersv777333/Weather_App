package com.musalatask.weatherapp.presentation.ui.cityWeather

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
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
import com.musalatask.weatherapp.R
import com.musalatask.weatherapp.databinding.FragmentCityWeatherBinding
import com.musalatask.weatherapp.framework.utils.ActivityUtils
import com.musalatask.weatherapp.framework.utils.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * This fragment show to the user a specified city weather info.
 */
@AndroidEntryPoint
class CityWeatherFragment : Fragment() {

    private var _binding: FragmentCityWeatherBinding? = null
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
                    binding.currentDate.text = it//Display the current time.
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lastUpdate.collect {
                    if (it.isNotEmpty())
                        binding.elapseTime.text = getString(R.string.last_update_label) + it
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    //A new ui state request the invalidation of the screen.
                    setUi(it)
                }
            }
        }

        binding.refresh.setOnRefreshListener { viewModel.refreshWeather() }
    }

    /**
     * This transform the given ui state in a new visual representation and apply it.
     *
     * @param[state] the ui state to represent.
     */
    private fun setUi(state: CityWeatherUiState) {
        setUiByLoadingInfo(state)
        setUiByErrorInfo(state)
        setUiByCityWeatherInfo(state)
    }

    /**
     * This generate the view representation around loading info by given state.
     */
    private fun setUiByLoadingInfo(state: CityWeatherUiState) {
        if (state.isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            setWidgetsVisibility(View.INVISIBLE)
            binding.empty.visibility = View.INVISIBLE
            binding.weatherInfo.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.refresh.isRefreshing = false
        }
    }

    /**
     * This generate the view representation around error info by given state.
     */
    private fun setUiByErrorInfo(state: CityWeatherUiState) {
        if (state.errorResource != null) {
            if (state.cityWeather == null) {
                setWidgetsVisibility(View.INVISIBLE)
                binding.empty.visibility = View.VISIBLE
                binding.weatherInfo.visibility = View.INVISIBLE
            } else {
                setWidgetsVisibility(View.VISIBLE)
                binding.empty.visibility = View.INVISIBLE
                binding.weatherInfo.visibility = View.VISIBLE
            }
            showError(state.errorResource)
            binding.progressBar.visibility = View.INVISIBLE
            binding.refresh.isRefreshing = false
        }
    }

    /**
     * This generate the view representation around city weather model info by given state.
     *
     * @param[state] the new ui state.
     */
    private fun setUiByCityWeatherInfo(state: CityWeatherUiState) {
        if (state.cityWeather != null) {
            binding.empty.visibility = View.GONE
            setWidgetsVisibility(View.VISIBLE)
            binding.weatherInfo.visibility = View.VISIBLE

            with(state.cityWeather) {
                displayImageFromUrl(
                    url = "https://openweathermap.org/img/w/${iconId}.png",
                    imageView = binding.weatherIcon
                )
                binding.description.text = description
                binding.temp.text = "${(temp - 273.15).toInt()}"
                binding.metricsUnit.text = getString(R.string.celsius_degree)
                binding.maxTemp.text = "${(tempMax - 273.15).toInt()}${getString(R.string.celsius_degree)}"
                binding.minTemp.text = "${(tempMin - 273.15).toInt()}${getString(R.string.celsius_degree)}"
                binding.humidity.text = "${getString(R.string.humidity_label)}$humidity%"
                binding.feelsLike.text = "${getString(R.string.feels_like_label)}${(feelsLike - 273.15).toInt()}${getString(R.string.celsius_degree)}"
                if (rainVolumeLastHour != null) {
                    binding.precipitation.text = "${getString(R.string.last_hour_label)}$rainVolumeLastHour mm"
                    binding.imageView6.visibility = View.VISIBLE
                } else binding.imageView6.visibility = View.INVISIBLE
                binding.air.text = "$windSpeed m/s"
                binding.imageView5.rotation = windDirection.toFloat()
                binding.visibility.text = "${getString(R.string.visibility_label)}$visibility m"
                binding.pressure.text = "${getString(R.string.pressure_label)}$pressure hPa"
                if (snowVolumeLastHour != null) {
                    binding.snow.text = "${getString(R.string.last_hour_label)}$snowVolumeLastHour mm"
                    binding.imageView7.visibility = View.VISIBLE
                } else binding.imageView7.visibility = View.INVISIBLE
                binding.sunset.text =
                    "${getString(R.string.sunset_label)}${DateTimeUtils.formatTime((sunset + timezone) * 1000L)}"
                binding.sunrise.text =
                    "${getString(R.string.sunrise_label)}${DateTimeUtils.formatTime((sunrise + timezone) * 1000L)}"

                binding.map.setOnClickListener {
                    val url =
                        "https://openweathermap.org/weathermap?basemap=map&cities=false&layer=clouds&lat=$latitude&lon=$longitude&zoom=3"
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                }

            }
        }
    }

    private fun showError(messageResource: Int) {
        ActivityUtils.showSnackBar(messageResource = messageResource, view = binding.root)
    }

    /**
     * This is used to show or hide the widgets as consequence of a new ui state.
     */
    private fun setWidgetsVisibility(visibility: Int) {
        binding.imageView2.visibility = visibility
        binding.imageView3.visibility = visibility
        binding.imageView4.visibility = visibility
        binding.imageView5.visibility = visibility
        binding.imageView6.visibility = visibility
        binding.imageView7.visibility = visibility
        binding.imageView8.visibility = visibility
        binding.imageView9.visibility = visibility
        binding.map.visibility = visibility
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