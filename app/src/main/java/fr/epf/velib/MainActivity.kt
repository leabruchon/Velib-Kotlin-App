package fr.epf.velib

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.android.clustering.ClusterManager
import fr.epf.velib.api.ApiService
import fr.epf.velib.model.StationVelib
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
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

        val button = findViewById<Button>(R.id.favorites_button)
        button.setOnClickListener{
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
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
    @SuppressLint("MissingPermission", "WrongViewCast", "Range")
    override fun onMapReady(googleMap: GoogleMap) {

        // zoom + and - on map
        var UiSettings = googleMap.uiSettings
        UiSettings.isZoomControlsEnabled = true

        // location button
        googleMap.isMyLocationEnabled = true
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)

        // add items
        setUpClusterer(googleMap)

        // when the user click on a marker
        clusterManager.setOnClusterItemClickListener { item ->

            val nameEdittext = findViewById<TextView>(R.id.info_text)
            nameEdittext.text = item.name

            val bikesData = findViewById<TextView>(R.id.bikes_data)
            bikesData.text = item.bikes_available.toString()

            val eBikesData = findViewById<TextView>(R.id.ebikes_data)
            eBikesData.text = item.ebikes_available.toString()

            val docksData = findViewById<TextView>(R.id.docks_data)
            docksData.text = item.num_docks_available.toString()

            val popup = findViewById<CardView>(R.id.card_view)
            popup.isVisible = true

            val closeButton = findViewById<TextView>(R.id.close_button)
            closeButton.setOnClickListener(){
                popup.isVisible =false
            }

            // when user clicks on fav button
            val favButton = findViewById<Button>(R.id.fav_changing_button)
            favButton.setOnClickListener(){
                val db = DBHelper(this, null)
                val id_station = item.station_id
                val name = item.name
                val bikes_available = item.bikes_available
                val ebikes_available = item.ebikes_available
                val num_docks_available = item.num_docks_available

                db.addStation(id_station, bikes_available, ebikes_available,num_docks_available)

            }

            false
        }




        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(IDF, 9.0f))
    }


    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {

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
            val offsetItem =
                StationVelib(station.station_id, station.bikes_available, station.capacity, station.ebikes_available, station.last_reported,station.lat,station.lon,station.name,station.num_docks_available,station.stationCode)
            clusterManager.addItem(offsetItem)
        }
    }



}



