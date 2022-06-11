package fr.epf.velib

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import fr.epf.velib.model.StationVelib

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                IDSTATION_COL + " LONG," +
                BIKES_COl + " INTEGER," +
                EBIKES_COL + " INTEGER," +
                DOCKS_COL + " INTEGER" + ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addStation(id: Long, bikes: Int, ebikes: Int, docks: Int) {

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(IDSTATION_COL, id)
        values.put(BIKES_COl, bikes)
        values.put(EBIKES_COL, ebikes)
        values.put(DOCKS_COL, docks)

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)

        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    @SuppressLint("Range")
    fun getInfo(): ArrayList<StationVelib> {

        /*val db: SQLiteDatabase = this.readableDatabase
        val arrayList = ArrayList<String>()
        val res: Cursor = db.rawQuery("select * from $TABLE_NAME", null)
        res.moveToFirst()
        while (!res.isAfterLast) {
            arrayList.add(res.getString(res.getColumnIndex("station_id")));
            arrayList.add(res.getString(res.getColumnIndex("bikes_available")));


            res.moveToNext();
        }*/

        var list = ArrayList<StationVelib>()
        val readableDataBase = this.readableDatabase
        val cursor = readableDataBase.rawQuery("select * from $TABLE_NAME", null)
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {

            var station = StationVelib(0,0,0,0,0,0.0,0.0,"",0,"")
            station.station_id = cursor.getLong(cursor.getColumnIndex(IDSTATION_COL))
            station.bikes_available = cursor.getInt(cursor.getColumnIndex(BIKES_COl))
            station.ebikes_available = cursor.getInt(cursor.getColumnIndex(EBIKES_COL))
            station.num_docks_available = cursor.getInt(cursor.getColumnIndex(DOCKS_COL))

            list.add(station)

            cursor.moveToNext()
        }

        return list
    }




    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "FAV_STATIONS_DATABASE"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "fav_stations"

        // below is the variable for id column
        val ID_COL = "id"

        // below is the variable for id column
        val IDSTATION_COL = "station_id"

        // below is the variable for name column
        val BIKES_COl = "bikes_available"

        // below is the variable for age column
        val EBIKES_COL = "ebikes_available"

        // below is the variable for age column
        val DOCKS_COL = "num_docks_available"


    }
}