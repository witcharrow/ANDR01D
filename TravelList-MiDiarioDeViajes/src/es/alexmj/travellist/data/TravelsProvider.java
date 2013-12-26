package es.alexmj.travellist.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Content Provider para la base de datos de viajes.
 * VERSION 4: Aniadimos un provider para trabajar con la base de datos.
 * 
 * @author Alejandro.Marijuan@googlemail.com
 */
public class TravelsProvider extends ContentProvider {
	
	private static final String TAG = "TravelsProvider -->";
	private TravelsDatabaseHelper mDbHelper;
	private static final String AUTHORITY = "es.alexmj.travellist";	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/travels");
	private static final int URI_TRAVELS = 1;
	private static final int URI_TRAVEL_ITEM = 2;	
	private static final UriMatcher mUriMatcher;
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITY, "travels", URI_TRAVELS);
		mUriMatcher.addURI(AUTHORITY, "travels/#", URI_TRAVEL_ITEM);
	}
	

	/**
	 * Genera la base de datos que contiene los viajes.
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		Log.d(TAG, "onCreate");
		mDbHelper = new TravelsDatabaseHelper(getContext());
		return true;
	}// onCreate()

	/**
	 * Se encarga de introducir una nueva información en la BBDD. Si tiene exito,
	 *  debe devolver la URI que representa al recurso que acaba de ser aniadido.
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "insert");
		SQLiteDatabase db = mDbHelper.getWritableDatabase();		
		long id = db.insert(TravelsDatabaseHelper.TABLE_NAME, null, values);
		Log.w(TAG,"id = "+id);
		Uri result = null;		
		if (id >= 0){
			result = ContentUris.withAppendedId(CONTENT_URI, id);
			notifyChange(result);
		}		
		return result;
	}// insert()

	/**
	 * Consulta la base de datos para obtener o todos los viajes o un item viaje. 
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {		
		Log.d(TAG, "query");
		SQLiteDatabase db = mDbHelper.getReadableDatabase();		
		int match = mUriMatcher.match(uri);
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		qBuilder.setTables(TravelsDatabaseHelper.TABLE_NAME);		
		switch (match){
		case URI_TRAVELS:
			//nada
			break;
		case URI_TRAVEL_ITEM:
			String id = uri.getPathSegments().get(1);
			qBuilder.appendWhere(Travels._ID + "=" + id);
			break;
		default:
			Log.w(TAG, "Uri didn't match: " + uri);
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}		
		Cursor c = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);		
		return c;
	}// query()
	
	/**
	 * Actualiza un elemento viaje, a partir de su URI.
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.d(TAG, "update");
		// TODO Auto-generated method stub
		
		return 0;
	}// update()

	/**
	 * Borra un elemento viaje, a partir de su URI.
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(TAG, "delete");
		// TODO Auto-generated method stub
		
		return 0;
	}

	/**
	 * Obtiene el tipo del URI, en funcion si es todos los viajes o un item viaje solo.
	 *  Para conocer el MIME Type de los datos devueltos por el provider. 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		Log.d(TAG, "getType");
		int match = mUriMatcher.match(uri);		
		switch (match){
		case URI_TRAVELS:
			return "vnd.android.cursor.dir/vnd.example.travels";
		case URI_TRAVEL_ITEM:
			return "vnd.android.cursor.item/vnd.example.travels";
		default:
			Log.w(TAG, "Uri didn't match: " + uri);
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}// getType()
	
	/**
	 * Notifica el cambio de un item.
	 * @param uri del elemento que cambia
	 */
	protected void notifyChange(Uri uri) {
		Log.d(TAG, "notifyChange");
        getContext().getContentResolver().notifyChange(uri, null);
    }// notifyChange()
	
	/**
	 * Clase de constantes con la informacion de la base de datos: ciudad,pais,anio y notas.
	 *
	 */
	public class Travels implements BaseColumns {
		/**
		 * The city of the travel
		 * <P>Type: TEXT</P>
		 */
		public static final String CITY = "city";
		
		/**
		 * The country of the travel
		 * <P>Type: TEXT</P>
		 */
		public static final String COUNTRY = "country";

		/**
		 * The year of the travel
		 * <P>Type: NUMBER</P>
		 */
		public static final String YEAR = "year";

		/**
		 * The note
		 * <P>Type: TEXT</P>
		 */
		public static final String NOTE = "notes";
	}	
}
