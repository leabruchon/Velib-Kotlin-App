package fr.epf.velib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import fr.epf.velib.api.ApiService
import fr.epf.velib.model.StationVelib
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callApi()
    }

    private fun callApi() : List<StationVelib> {
        Log.i("test", "Hello World")
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
}