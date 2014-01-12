package es.alexmj.travellist.data;

import java.util.ArrayList;

import es.alexmj.travellist.R;
import es.alexmj.travellist.TravelInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Operaciones CRUD para la base de datos de viajes. Generamos una base de datos,
 *  en la cual creamos una tabla con distintos viajes, almacenando cada uno con:
 *  	- ID
 *  	- CIUDAD
 *  	- PAIS
 *  	- ANIO
 *  	- NOTA
 *  VERSION 4: Aniadimos un provider para trabajar con la base de datos.
 * @author Alejandro.Marijuan@googlemail.com
 *
 */
public class TravelsDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "TravelsDatabaseHelper -->";	
	
	//## VERSION 4: esqueleto del provider
    private static final String DATABASE_NAME = "travels.db";
    private static final int DATABASE_VERSION = 2;
	public static final String TABLE_NAME = "travels";
	private Resources res;
	
	/**
	 * Constructor de la BBDD de viajes.
	 * @param context
	 */
	public TravelsDatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		res = context.getResources();
	}

	/**
	 * Crea la tabla en la base de datos de viajes con ID, Ciudad, Anio y Nota.
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate '" + TABLE_NAME +"' on DDBB '"+DATABASE_NAME+"', v"+DATABASE_VERSION);
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
				TravelsConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				TravelsConstants.CITY + " TEXT NOT NULL, " +
				TravelsConstants.COUNTRY + " TEXT NOT NULL, " +
				TravelsConstants.YEAR + " INTEGER NOT NULL, " +
				TravelsConstants.NOTE + " TEXT " +
			");");		
		//## Initial data
		initialData(db);
	}// onCreate()

	/**
	 * Comprueba la versión de la base de datos, si es antigua borra la tabla.
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade '" + TABLE_NAME +"' on DDBB '"+DATABASE_NAME+"', v"+DATABASE_VERSION);
		if (oldVersion < newVersion){
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
			onCreate(db);
		}
	}// onUpgrade()

	/**
	 * Inicializamos los valores de los viajes en la BBDD.
	 * @param db es la BBDD
	 */
	private void initialData(SQLiteDatabase db){
		Log.d(TAG, "initialData");
		insertTravel(db, "Londres", "UK", 2012, "¡Juegos Olimpicos!");
		insertTravel(db, "Paris", "Francia", 2007, "Roquefort");
		insertTravel(db, "Gotham City", "EEUU", 2011, "¡¡Batman!!");
		insertTravel(db, "Hamburgo", "Alemania", 2009, "Wunderbar!");
		insertTravel(db, "Pekin", "China", 2011, null);
		
		//## getResources() para viaje a "Burgos"
		String city=res.getString(R.string.cityResult);
		String country=res.getString(R.string.countryResult);
		Integer year=Integer.valueOf(res.getString(R.string.yearResult));
		String note=res.getString(R.string.noteResult);
		insertTravel(db, city, country, year, note);
	}// initialData()
	
	/**
     * Obtiene los datos de la tabla de la BBDD y los almacena en una lista.
     *  Lo utilizamos en la VERSION 3 de la app.
     * @return lista de viajes
     */
    public ArrayList<TravelInfo> getTravelsList(){ 
    	Log.d(TAG, "getTravelsList");
    	ArrayList<TravelInfo> travels = new ArrayList<TravelInfo>();    	
    	SQLiteDatabase db = getReadableDatabase();    	
    	Cursor c = db.query(TravelsConstants.TRAVELS_TABLE_NAME, null, null, null, null, null, null);    	
    	if (c.moveToFirst()){
    		int idDBIndex = c.getColumnIndex(TravelsConstants._ID);
    		int cityIndex = c.getColumnIndex(TravelsConstants.CITY);
    		int countryIndex = c.getColumnIndex(TravelsConstants.COUNTRY);
    		int yearIndex = c.getColumnIndex(TravelsConstants.YEAR);
    		int noteIndex = c.getColumnIndex(TravelsConstants.NOTE);    		
    		do {
    			int idDB = c.getInt(idDBIndex);
    			String city = c.getString(cityIndex);
    			String country = c.getString(countryIndex);
    			int year = c.getInt(yearIndex);
    			String note = c.getString(noteIndex);
//    				Log.i(TAG,"%%%%%%%%%%%%%%%%%%%%% idDB="+idDB);
    			TravelInfo travel = new TravelInfo(idDB, city, country, year, note);    			
    			travels.add(travel);    			
    		} while (c.moveToNext());    		
    		c.close();
    	}
    	Log.i(TAG, "Nro de viajes en DB: "+ travels.size());
        return travels;
    }// getTravelsList()
        
    /**
     * Anyadimos un viaje a nuestra BBDD.
	 * @param db es la BBDD
	 * @param city es la ciudad a introducir
	 * @param country es el país a introducir
	 * @param year es el anyo a introducir
	 * @param note es la nota a introducir
	 */
	public void insertTravel(SQLiteDatabase db, String city, String country, int year, String note){
		Log.d(TAG, "insertTravel");
		ContentValues values = new ContentValues();
		values.put(TravelsConstants.CITY, city);
		values.put(TravelsConstants.COUNTRY, country);
		values.put(TravelsConstants.YEAR, year);
		values.put(TravelsConstants.NOTE, note);		
		db.insert(TABLE_NAME, null, values);		
	}// insertTravel()
	
    /**
     * Actualizamos un viaje que ya se encuentra en la BBDD
     * @param myTrip informacion del viaje a ser modificado
     * @return valor de la fila a ser actualizada
     */
    public int updateTravel(TravelInfo myTrip, int positionId) {
    	Log.d(TAG, "updateTravel IN "+DATABASE_NAME+": "+myTrip.getCity()+"("+myTrip.getCountry()+") Año "+myTrip.getYear()+" - '"+myTrip.getNote()+"'");
        SQLiteDatabase db = this.getWritableDatabase();     
        ContentValues values = new ContentValues();
        values.put(TravelsConstants.CITY, myTrip.getCity());
		values.put(TravelsConstants.COUNTRY, myTrip.getCountry());
		values.put(TravelsConstants.YEAR, myTrip.getYear());
		values.put(TravelsConstants.NOTE, myTrip.getNote());     
        // ##updating row
        return db.update(TABLE_NAME, values, TravelsConstants._ID + " = ?",
                new String[] { String.valueOf(positionId) });        
    }// updateTravel()
    
    /**
     * Borramos un viaje. El id se obtiene del valor que esta almacenado en la base de datos. 
     * @param positionId es la posicion del viaje en la BBDD
     */
    public void deleteTravel(int positionId) {
    	Log.d(TAG, "deleteTravel");
    	Log.i(TAG, "positionId: "+ String.valueOf(positionId) +"--"+"Table ID" );    	
        SQLiteDatabase db = this.getWritableDatabase();        
        int result=db.delete(TABLE_NAME, TravelsConstants._ID + " = ?",
                new String[] { String.valueOf(positionId) });
        Log.i(TAG, "##number of rows afected: "+ result); 
    }// deleteTravel()
    
	/**
	 * Obtiene el id con mayor valor de la tabla de nuestra BD.
	 * @return el valor del id mas grande dentro de la tabla
	 */
	public int getLastId() {
		Log.d(TAG, "getLastId");
	    //## openDB();
	    SQLiteDatabase sqlDB = this.getReadableDatabase();
	    int id = 0;
	    final String MY_QUERY = "SELECT MAX(_id) AS _id FROM "+TABLE_NAME;
	    Cursor mCursor = sqlDB.rawQuery(MY_QUERY, null);  
	    try {
	          if (mCursor.getCount() > 0) {
	            mCursor.moveToFirst();
	            id = mCursor.getInt(0);//there's only 1 column in cursor since you only get MAX, not dataset
	          }
	        } catch (Exception e) {
	          System.out.println(e.getMessage());
	        } finally {
	            //## closeDB();
	        	sqlDB.close();
	        }
	    return id;
	}// getLastId()
}
