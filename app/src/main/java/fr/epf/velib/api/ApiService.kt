package fr.epf.velib.api

import fr.epf.velib.model.StationVelib
import retrofit2.http.GET

interface ApiService {
    @GET("get-all-stations")
    suspend fun getAllStations(): List<StationVelib>
}