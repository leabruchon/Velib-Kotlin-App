package fr.epf.velib

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import fr.epf.velib.model.StationVelib

class FavListAdapter(private val context: Context,
                     private val dataSource: ArrayList<StationVelib>
) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.station_list_row, parent, false)

        val title = rowView.findViewById(R.id.info_text) as TextView
        val bikes = rowView.findViewById(R.id.bikes_data) as TextView
        val ebikes = rowView.findViewById(R.id.ebikes_data) as TextView
        val docks = rowView.findViewById(R.id.docks_data) as TextView


        val station = getItem(position) as StationVelib



        title.text = station.name
        bikes.text = station.bikes_available.toString()
        ebikes.text = station.ebikes_available.toString()
        docks.text = station.num_docks_available.toString()


        return rowView
    }
}