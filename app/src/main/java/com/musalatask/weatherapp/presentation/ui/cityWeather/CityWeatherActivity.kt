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

    //This lambda has the logic of the search view of the appbar when its text be changed.
    // If the weather screen is visible, its logic will be null, else, if the cities
    // screen is visible, its logic will be receive the text and filter the cities by it.
    var requestCitiesWhichContainsAText: ((text: String) -> Unit)? = null

    //This lambda has the logic of the search view of the appbar when its text be submitted.
    // If the weather screen is visible, its logic will be receive a city name and
    // request its weather, else, if the cities screen is visible, its logic will be receive
    // the text and filter the cities by it.
    var submitTextForSearch: ((text: String) -> Unit)? = null

    // FusedLocationProviderClient - Main class for receiving location updates.
    private var fusedLocationClient: FusedLocationProviderClient? = null

    // LocationRequest - Requirements for the location updates, i.e.,
    // how often you should receive updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallBack - Called when FusedLocationProviderClient
    // has a new Location
    private lateinit var locationCallback: LocationCallback

    //This variable is use to determine which permissions the system has granted to
    // your app.
    private lateinit var locationPermissionRequest: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {

        //This is the new way to create a Splash Screen.
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityCityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        viewModel = ViewModelProvider(this)[CityWeatherViewModel::class.java]

        if (viewModel.lastSuccessCityName == null){
            initializeLocationComponents()
            checkConnectionToStartRequestingLocation()
        }//This means that a city weather request doesn't been done, in other case, a
        // configuration was changed and there is no need to automatically request
        // the weather.

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .filter { !it.isLoading && it.errorResource == null && it.cityWeather != null }
                    .map { it.cityWeather?.cityName }
                    .distinctUntilChanged()
                    .collect {

                        //When the user received a success response about the weather that he
                        //requested, the name of the city in the response will appear as a
                        //title of the screen, and the search view will be collapse.
                        supportActionBar?.title = it
                        collapseSearchView()
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        //Observe the result to be set by MyCitiesFragment in the stateHandle of the currentBackStackEntry.
        //This is for get the city which the user chose in MyCitiesFragment and request its weather.
        val currentBackStackEntry = findNavController(R.id.nav_host_fragment_content_main).currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<String>(Constants.SELECTED_CITY_KEY)
            ?.observe(currentBackStackEntry, Observer { result ->
                viewModel.getCityWeatherByName(result)
            })
    }

    /**
     * This function is used when the app wants to get the weather of the current
     * coordinates location of the user, for doing that, internet connection is
     * needed.
     */
    private fun checkConnectionToStartRequestingLocation() {
        if (!ConnectivityUtils.hasNetworkAvailable(this)) {//There is no internet,
            //then a work what wait for incoming internet connection will be executed.
            performWorkToWaitingForConnection { checkForLocationPermission() }
            notifyThereIsNotConnection()
        } else {
            checkForLocationPermission()
        }
    }

    /**
     * This function execute a WaitForNetworkWorker work that is used to notify when a internet
     * connection is available.
     *
     * @param[actionForWhenConnectionAreAvailable] the logic to execute when a internet connection
     * is available.
     */
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

        //Observe the work state for know when an internet connection is
        //available.
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

    /**
     * This initialize all the locations components used for obtain the current coordinates of
     * the devices.
     */
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

                    //The current weather was requested, for that, there is no need to
                    //keep tracking the locations changes.
                    fusedLocationClient?.removeLocationUpdates(locationCallback)
                }
            }
        }

        locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) { // Permission is granted. Continue the action.
                    tryToRequestLocation()
                } else {//The user denied the location permission, for that, an educational ui
                    //will be show, to explain the user why that permission is needed.
                    DialogsUtils.createAlertDialog(
                        context = this,
                        title = getString(R.string.dialog_permission_denied_title),
                        message = getString(R.string.dialog_need_for_permission_explanation)
                    ).show()
                }
            }
    }

    /**
     * This check for ACCESS_COARSE_LOCATION permission in order to request the current coordinates.
     * If the permission is granted, it can continue with the requesting if the user has the GPS
     * activate too. If the permission is not granted yet, an ui must show to the user telling
     * him that he has to make a decision about the permission (grant or denied).
     */
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
                message = getString(R.string.should_show_request_permission_rationale_message),
                positiveButtonText = getString(R.string.should_show_request_permission_rationale_message_positive_label),
                positiveButtonAction = { _, _ -> requestThePermission() },
                negativeButtonText = getString(R.string.should_show_request_permission_rationale_message_negative_label),
                negativeButtonAction = { _, _ -> }
            ).show()
        } else {
            requestThePermission()
        }
    }

    /**
     * This tells the system that handle the permission requesting.
     */
    private fun requestThePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    /**
     * This starts the location tracking but, the GPS must be activated for obtain the
     * coordinates. This function checks that. If GPS is not activate an ui must show
     * to the user telling him that the GPS is needed if he wants to know the weather
     * at his position.
     */
    private fun tryToRequestLocation() {
        requestLocation()

        val mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Checking GPS is enabled
        val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!mGPS) {
            DialogsUtils.createAlertDialog(
                context = this,
                message = getString(R.string.get_gps_dialog_message),
                positiveButtonText = getString(R.string.get_gps_dialog_positive_label),
                positiveButtonAction = { _, _ ->
                    val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                },
                negativeButtonText = getString(R.string.get_gps_dialog_negative_label),
                negativeButtonAction = { _, _ -> }
            ).show()
        }
    }

    /**
     * This starts the location tracking.
     */
    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun notifyThereIsNotConnection(){
        ActivityUtils.showSnackBar(messageResource = R.string.no_internet_error, view = binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        searchMenuItem = menu.findItem(R.id.action_search)

        selectOneCityMenuItem = menu.findItem(R.id.action_pick)//This menu item must be
        //visible when city weather screen is showing but must be invisible if it's not.

        selectOneCityMenuItem.isVisible = viewModel.isSelectCityMenuItemVisible

        val searchView = searchMenuItem!!.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)

        setOnQueryTextListener(searchView)

        return super.onCreateOptionsMenu(menu)
    }

    private fun setOnQueryTextListener(searchView: SearchView){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    submitTextForSearch?.let { it1 -> it1(it) }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                requestCitiesWhichContainsAText?.let {
                    newText?.let { it1 -> it(it1) }
                }
                return true
            }
        })
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

        //When the user goes to cities screen selectOneCityMenuItem must be invisible.
        selectOneCityMenuItem.isVisible = false
        setSelectCityMenuItemVisibleState(false)
    }

    /**
     * This save the current state about selectOneCityMenuItem visibility in a viewModel,
     * in order that than info survival to changes configuration.
     */
    fun setSelectCityMenuItemVisibleState(state: Boolean){
        viewModel.isSelectCityMenuItemVisible = state
    }

    override fun onSupportNavigateUp(): Boolean {
        collapseSearchView()
        supportActionBar?.title = viewModel.lastSuccessCityName
        setSelectCityMenuItemVisibleState(true)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun collapseSearchView(){
        searchMenuItem?.collapseActionView()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient?.removeLocationUpdates(locationCallback)//Stop location tracking
        //if the user close the app.
        WorkManager.getInstance(application).cancelAllWork()//WorkManager storage its works, but,
        //when the app is closed there is no need that for some scenario WorkManage save a WaitForNetworkWorker
        //instance.
    }
}