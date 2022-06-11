package fr.epf.velib

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import fr.epf.velib.model.StationVelib

class FavoriteActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        var listView = findViewById<ListView>(R.id.listView)

        val helper = DBHelper(this, null)

        val arrayList: ArrayList<StationVelib> = helper.getInfo()



        val listItems = arrayOfNulls<String>(arrayList.size)

        for (i in arrayList.indices) {
            val recipe = arrayList[i]
            listItems[i] = recipe.toString()
        }

        val adapter = FavListAdapter(this, arrayList)
        listView.adapter = adapter
   }
}

