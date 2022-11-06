package com.musalatask.weatherapp.presentation.ui.cityWeather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.*
import com.google.android.gms.location.*
import com.musalatask.weatherapp.R
import com.musalatask.weatherapp.common.Constants
import com.musalatask.weatherapp.databinding.ActivityCityWeatherBinding
import com.musalatask.weatherapp.framework.utils.ActivityUtils
import com.musalatask.weatherapp.framework.utils.ConnectivityUtils
import com.musalatask.weatherapp.framework.utils.DialogsUtils
import com.musalatask.weatherapp.framework.utils.WaitForNetworkWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class CityWeatherActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var searchMenuItem: MenuItem? = null
    lateinit var selectOneCityMenuItem: MenuItem
    private lateinit var binding: ActivityCityWeatherBinding
    private lateinit var viewModel: CityWeatherViewModel

    var requestCitiesWishContainsAText: ((text: String) -> Unit)? = null
    var submitTextForSearch: ((text: String) -> Unit)? = null

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e.,
    // how often you should receive updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationListener - Called when FusedLocationProviderClient
    // has a new Location
    private lateinit var locationCallback: LocationCallback

    private lateinit var locationPermissionRequest: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityCityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        viewModel =
            ViewModelProvider(this)[CityWeatherViewModel::class.java]

        initializeLocationComponents()
        checkConnectionToStartRequestingLocation()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .filter { !it.isLoading && it.errorMessage == null && it.cityWeather != null }
                    .map { it.cityWeather?.cityName }
                    .distinctUntilChanged()
                    .collect {
                        supportActionBar?.title = it
                        collapseSearchView()
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        //Observe the result to be set by MyCitiesFragment in the stateHandle of the currentBackStackEntry
        val currentBackStackEntry = findNavController(R.id.nav_host_fragment_content_main).currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<String>(Constants.SELECTED_CITY_KEY)
            ?.observe(currentBackStackEntry, Observer { result ->
                supportActionBar?.title = result
                viewModel.getCityWeatherByName(result)
            })
    }

    private fun checkConnectionToStartRequestingLocation() {
        if (!ConnectivityUtils.hasNetworkAvailable(this)) {
            performWorkToWaitingForConnection { checkForLocationPermission() }
            notifyThereIsNotConnection()
        } else {
            checkForLocationPermission()
        }
    }

    private fun performWorkToWaitingForConnection(actionForWhenConnectionAreAvailable: () -> Unit) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val waitForNetworkWork: WorkRequest =
            OneTimeWorkRequestBuilder<WaitForNetworkWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(application)
            .enqueue(waitForNetworkWork)

        lifecycleScope.launch {
            WorkManager
                .getInstance(application)
                .getWorkInfoByIdLiveData(waitForNetworkWork.id)
                .asFlow()
                .filter {
                    it.state == WorkInfo.State.SUCCEEDED
                }.collect {
                    actionForWhenConnectionAreAvailable()
                }
        }
    }

    private fun initializeLocationComponents() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest().apply {
            // Sets the desired interval for
            // active location updates.
            // This interval is inexact.
            interval = TimeUnit.SECONDS.toMillis(60)

            // Sets the fastest rate for active location updates.
            // This interval is exact, and your application will never
            // receive updates more frequently than this value
            fastestInterval = TimeUnit.SECONDS.toMillis(30)

            // Sets the maximum time when batched location
            // updates are delivered. Updates may be
            // delivered sooner than this interval
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(var1: LocationResult) {
                var1.lastLocation?.let {
                    viewModel.getCurrentCityWeather(
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }

        locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) { // Permission is granted. Continue the action or workflow in your
                    tryToRequestLocation()
                } else {
                    DialogsUtils.createAlertDialog(
                        context = this,
                        title = "Oops!!!",
                        message = "Requesting the weather for your current city is unavailable becouse " +
                                "you don't allow us the access for your current location. You still " +
                                "can find a specific city weather by its name."
                    ).show()
                }
            }
    }

    private fun checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            tryToRequestLocation()

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            DialogsUtils.createAlertDialog(
                context = this,
                message = "Pipo, hace falta que actives el permiso!",
                positiveButtonText = "Ok",
                positiveButtonAction = { _, _ -> requestThePermission() },
                negativeButtonText = "No thanks",
                negativeButtonAction = { _, _ -> }
            ).show()
        } else {
            requestThePermission()
        }
    }

    private fun requestThePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun tryToRequestLocation() {
        requestLocation()

        val mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Checking GPS is enabled
        val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!mGPS) {
            DialogsUtils.createAlertDialog(
                context = this,
                message = "Pipo, hace falta que actives el gps!",
                positiveButtonText = "Ok",
                positiveButtonAction = { _, _ ->
                    val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                },
                negativeButtonText = "No thanks",
                negativeButtonAction = { _, _ -> }
            ).show()
        }
    }

    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun notifyThereIsNotConnection(){
        ActivityUtils.showSnackBar(message = "You don't have connection for requesting the weather.", view = binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        searchMenuItem = menu.findItem(R.id.action_search)
        selectOneCityMenuItem = menu.findItem(R.id.action_pick)
        if(!viewModel.isSelectCityMenuItemVisible) selectOneCityMenuItem.isVisible = false
        val searchView = searchMenuItem!!.actionView as SearchView
        searchView.queryHint = "Type a city name"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    submitTextForSearch?.let { it1 -> it1(it) }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                requestCitiesWishContainsAText?.let {
                    newText?.let { it1 -> it(it1) }
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_pick -> navigateToMyCitiesFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToMyCitiesFragment(){
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.action_CityWeatherFragment_to_myCitiesFragment)
        selectOneCityMenuItem.isVisible = false
        viewModel.isSelectCityMenuItemVisible = false
    }

    override fun onSupportNavigateUp(): Boolean {
        collapseSearchView()
        supportActionBar?.title = viewModel.lastSuccessCityName
        selectOneCityMenuItem.isVisible = true
        viewModel.isSelectCityMenuItemVisible = true
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun collapseSearchView(){
        searchMenuItem?.collapseActionView()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        WorkManager.getInstance(application).cancelAllWork()
    }
}