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


public class TravelsDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "TravelsDatabaseHelper: --->";
	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "travels.db";
	public static final String TABLE_NAME = TravelsConstants.TRAVELS_TABLE_NAME;
    private Resources res;
	
	/**
	 * @param context
	 */
	public TravelsDatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		res = context.getResources();
	}
	


	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate");
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
				TravelsConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				TravelsConstants.CITY + " TEXT NOT NULL, " +
				TravelsConstants.COUNTRY + " TEXT NOT NULL, " +
				TravelsConstants.YEAR + " INTEGER NOT NULL, " +
				TravelsConstants.NOTE + " TEXT " +
			");");
		
		
		//Initial data
		initialData(db);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {		
		Log.d(TAG, "onUpgrade");
		if (oldVersion < newVersion){
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
			onCreate(db);
		}
	}

	
	/**
	 * Inicializamos los valores de los viajes en la BBDD.
	 * @param db es la BBDD
	 */
	private void initialData(SQLiteDatabase db){
		Log.i(TAG, "initialData");
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
	}
	
	 
    

	/**
     * Generamos datos a mostrar.En una aplicacion funcional se 
     *  tomarian de base de datos o algun otro medio
     * @return lista de viajes
     */
    public ArrayList<TravelInfo> getTravelsList(){ 
    	Log.i(TAG, "getTravelsList");
    	ArrayList<TravelInfo> travels = new ArrayList<TravelInfo>();    	
    	SQLiteDatabase db = getReadableDatabase();    	
    	Cursor c = db.query(TravelsConstants.TRAVELS_TABLE_NAME, null, null, null, null, null, null);    	
    	if (c.moveToFirst()){
    		int cityIndex = c.getColumnIndex(TravelsConstants.CITY);
    		int countryIndex = c.getColumnIndex(TravelsConstants.COUNTRY);
    		int yearIndex = c.getColumnIndex(TravelsConstants.YEAR);
    		int noteIndex = c.getColumnIndex(TravelsConstants.NOTE);    		
    		do {
    			String city = c.getString(cityIndex);
    			String country = c.getString(countryIndex);
    			int year = c.getInt(yearIndex);
    			String note = c.getString(noteIndex);    			
    			TravelInfo travel = new TravelInfo(city, country, year, note);    			
    			travels.add(travel);    			
    		} while (c.moveToNext());    		
    		c.close();
    	}
    	Log.i(TAG, "Nro de viajes en DB: "+ travels.size());
        return travels;
    }
    
    
    /**
     * Anyadimos un viaje a nuestra BBDD.
	 * @param db es la BBDD
	 * @param city es la ciudad a introducir
	 * @param country es el país a introducir
	 * @param year es el anyo a introducir
	 * @param note es la nota a introducir
	 */
	public void insertTravel(SQLiteDatabase db, String city, String country, int year, String note){
		Log.i(TAG, "insertTravel");
		ContentValues values = new ContentValues();
		values.put(TravelsConstants.CITY, city);
		values.put(TravelsConstants.COUNTRY, country);
		values.put(TravelsConstants.YEAR, year);
		values.put(TravelsConstants.NOTE, note);
		
		db.insert(TABLE_NAME, null, values);		
	}
    
    /**
     * Actualizamos un viaje que ya se encuentra en la BBDD
     * @param myTrip informacion del viaje a ser modificado
     * @return valor de la fila a ser actualizada
     */
    public int updateTravel(TravelInfo myTrip, int positionId) {
    	Log.i(TAG, "updateTravel");
        SQLiteDatabase db = this.getWritableDatabase();     
        ContentValues values = new ContentValues();
        values.put(TravelsConstants.CITY, myTrip.getCity());
		values.put(TravelsConstants.COUNTRY, myTrip.getCountry());
		values.put(TravelsConstants.YEAR, myTrip.getYear());
		values.put(TravelsConstants.NOTE, myTrip.getNote());
     
        // updating row
        return db.update(TABLE_NAME, values, TravelsConstants._ID + " = ?",
                new String[] { String.valueOf(positionId) });
        
    }
    
    /**
     * Borramos un viaje.
     * TODO: corregir cuando borramos un id ya usado.
     * @param positionId es la posicion del viaje en la BBDD
     */
    public void deleteTravel(int positionId) {
    	Log.i(TAG, "deleteTravel");
    	Log.i(TAG, "positionId: "+ positionId +"--"+String.valueOf(positionId) );    	
        SQLiteDatabase db = this.getWritableDatabase();        
        int result=db.delete(TABLE_NAME, TravelsConstants._ID + " = ?",
                new String[] { String.valueOf(positionId) });
        Log.i(TAG, "##number of rows afected: "+ result); 
    }
}
