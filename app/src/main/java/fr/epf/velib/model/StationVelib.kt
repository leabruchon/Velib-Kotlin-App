package fr.epf.velib.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


data class StationVelib(
    val station_id :Long,
    val bikes_available:Int,
    val capacity:Int,
    val ebikes_available:Int,
    val last_reported:Int,
    val lat:Double,
    val lon:Double,
    val name:String,
    val num_docks_available:Int,
    val stationCode:String,
) : ClusterItem{

    private val position: LatLng = LatLng(lat, lon)

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String? {
        return name
    }

    override fun getSnippet(): String? {
        return "bikes availables : $bikes_available ebikes availables : $ebikes_available"
    }

}




