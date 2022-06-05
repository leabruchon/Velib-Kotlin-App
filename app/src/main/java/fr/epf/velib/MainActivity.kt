package fr.epf.velib

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import fr.epf.velib.api.ApiService
import fr.epf.velib.model.StationVelib
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    OnMyLocationButtonClickListener, OnMyLocationClickListener {
    private val IDF = LatLng(48.85, 2.34)
    //private var markerPerth: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun callApi() : List<StationVelib> {
        var stations: List<StationVelib>
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://94.247.183.221:8078/")
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .client(client)
            .build()
        val service = retrofit.create(ApiService::class.java)
        runBlocking {
            stations = service.getAllStations()
            Log.d("result", "synchroApi: ${stations.size}")
        }
        return stations
    }

    /** Called when the map is ready.  */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        // zoom + and - on map
        var UiSettings = googleMap.uiSettings
        UiSettings.isZoomControlsEnabled = true

        // location button
        googleMap.isMyLocationEnabled = true
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)




       // Add some markers to the map, and add a data object to each marker.
       /*markerPerth = googleMap.addMarker(
           MarkerOptions()
               .position(IDF)
               .title("Perth")
       )
        markerPerth?.tag = 0*/

        // add items
        setUpClusterer(googleMap)


        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(IDF, 9.0f))
    }

    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        val clickCount = marker.tag as? Int

            Toast.makeText(
                this,
                "${marker.title} ${marker.snippet} ",

                Toast.LENGTH_SHORT
            ).show()

        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }


    // Declare a variable for the cluster manager.
    private lateinit var clusterManager: ClusterManager<StationVelib>


    private fun setUpClusterer(map : GoogleMap) {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(this, map)

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        // Add cluster items (markers) to the cluster manager.
        addItems()
    }

    private fun addItems() {
        var stations: List<StationVelib> = callApi()


        for (station in stations) {
            Log.d("result", "list station: ${station.name}")
            val offsetItem =
                StationVelib(station.station_id, station.bikes_available, station.capacity, station.ebikes_available, station.last_reported,station.lat,station.lon,station.name,station.num_docks_available,station.stationCode)
            clusterManager.addItem(offsetItem)
        }
    }


}



