package fr.epf.velib.model


data class StationVelib (
    val station_id :Long,
    val bikes_available:Int,
    val capacity:Int,
    val ebikes_available:Int,
    val last_reported:Int,
    val lat:Float,
    val lon:Float,
    val name:String,
    val num_docks_available:Int,
    val rental_methods:String,
    val stationCode:String,
)
