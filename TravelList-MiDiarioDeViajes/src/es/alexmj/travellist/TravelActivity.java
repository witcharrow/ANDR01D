package es.alexmj.travellist;

import es.alexmj.travellist.data.TravelsConstants;
import es.alexmj.travellist.data.TravelsDatabaseHelper;
import es.alexmj.travellist.data.TravelsProvider;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.TextView;

/**
 * Muestra el detalle de los viajes en una pantalla a parte. Desde esta pantalla se puede
 *  compartir el viaje con otras aplicaciones.
 * VERSION 1: TravelActivity vacia a modo de ejemplo. Sustituye esta clase por
 *            la clase TravelActivity que creaste para el ejercicio de la unidad 2. 
 * VERSION 2: incluye ahora funcionalidad para pasar realizar operaciones CRUD con cada viaje. 
 * VERSION 3: Se aniade una nueva Base de datos para almacenar los viajes. No afecta a esta Activity. 
 * VERSION 4: Aniadimos un provider para trabajar con la base de datos.
 * 
 * @author Alejandro.Marijuan@googlemail.com
 *
 */
public class TravelActivity extends Activity {

	private static final String TAG = "TravelActivity: --->";
	
	private static Integer BUNDLE_YEAR = 1970;
	private static String BUNDLE_CITY = "city";
	private static String BUNDLE_COUNTRY = "country";
	private static String BUNDLE_NOTE = "note";
	private TextView mCity;
	private TextView mCountry;
	private TextView mYear;
	private TextView mNote;
	
	/**VERSION 5: ActionBar**/
	private ShareActionProvider mShareActionProvider;	
	private static TravelsDatabaseHelper dbHelper;
	private static long BUNDLE_ID=0;
	private static final int REQUEST_CODE_EDIT_TRIP = 1;
	private static final String[] PROJECTION = {TravelsConstants._ID, TravelsConstants.CITY, 
		TravelsConstants.COUNTRY, TravelsConstants.YEAR, TravelsConstants.NOTE};

	/**
	 * Recoge y establece los datos de ciudad, pais, anio y nota,
	 * que proceden del Intent.
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_travel);

		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		BUNDLE_ID = intent.getExtras().getLong(TravelInfo.EXTRA_ID);
		BUNDLE_CITY = intent.getExtras().getString(TravelInfo.EXTRA_CITY);
		BUNDLE_COUNTRY = intent.getExtras().getString(TravelInfo.EXTRA_COUNTRY);
		BUNDLE_YEAR = intent.getExtras().getInt(TravelInfo.EXTRA_YEAR);
		BUNDLE_NOTE = intent.getExtras().getString(TravelInfo.EXTRA_NOTE);

		Log.w(TAG,"("+BUNDLE_ID+")"+BUNDLE_CITY+","+BUNDLE_COUNTRY+",("+BUNDLE_YEAR+"),"+ BUNDLE_NOTE );

		mCity = (TextView) findViewById(R.id.cityResult);
		mCity.setText(BUNDLE_CITY);
		mCountry = (TextView) findViewById(R.id.countryResult);
		mCountry.setText(BUNDLE_COUNTRY);
		mYear = (TextView) findViewById(R.id.yearResult);
		mYear.setText(BUNDLE_YEAR.toString());
		mNote = (TextView) findViewById(R.id.noteResult);
		mNote.setText(BUNDLE_NOTE);
	}// onCreate()

	/**
	 * Guarda el estado de la Activity, con los datos del viaje,
	 *  a saber: Anio, ciudad, pais y nota.
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState");
		//## Guardamos el estado		
		String anno = mYear.getText().toString();
		String ciudad = mCity.getText().toString();
		String pais = mCountry.getText().toString();
		String nota = mNote.getText().toString();

		outState.putString(BUNDLE_YEAR.toString(), anno);
		outState.putString(BUNDLE_CITY, ciudad);
		outState.putString(BUNDLE_COUNTRY, pais);
		outState.putString(BUNDLE_NOTE, nota);

		super.onSaveInstanceState(outState);
	}// onSaveInstanceState()

	/**
	 * Crea el menu de opciones en la Activity que muestra los datos de un viaje guardado,
	 *  muestra unicamente la opcion de compartir dicho viaje.
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.travel_menu, menu);
		
		// Set up ShareActionProvider's default share intent
		/** Getting the actionprovider associated with the menu item whose id is share */
		mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_share_travel).getActionProvider();
	    /** Setting a share intent */
        mShareActionProvider.setShareIntent(getDefaultIntent());
 
		
	    return super.onCreateOptionsMenu(menu);
	}// onCreateOptionsMenu()

	/** Defines a default (dummy) share intent to initialize the action provider.
	  * However, as soon as the actual content to be used in the intent
	  * is known or changes, you must update the share intent by again calling
	  * mShareActionProvider.setShareIntent()
	  */
	private Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, BUNDLE_CITY + "("
				+ BUNDLE_COUNTRY + ")\n" + "Año: " + BUNDLE_YEAR + "\n"
				+ "Nota: " + BUNDLE_NOTE);
        return intent;
	}
	
	
	/**
	 * Genera las acciones necesarias para compartir un viaje con otra app.
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d(TAG, "onMenuItemSelected");
		switch (item.getItemId()) {
		case R.id.menu_share_travel:
			//## Creamos el Intent para lanzar la Activity que permita compartir el viaje
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, BUNDLE_CITY + "("
					+ BUNDLE_COUNTRY + ")\n" + "Año: " + BUNDLE_YEAR + "\n"
					+ "Nota: " + BUNDLE_NOTE);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources()
					.getText(R.string.send_to)));
			break;
/**VERSION 5**/	
		case R.id.menu_edit_travel:
			Log.i(TAG, "Etiqueta: Opcion EDITAR pulsada!");
			//## Creamos el Intent para lanzar la Activity EditTravelActivity
			Intent intent = new Intent(this, EditTravelActivity.class);	
			
			//## Obtenemos los datos para incluirlos en el Intent
			int id4edit=(int)BUNDLE_ID;
			Integer yearResult4edit = Integer.valueOf(mYear.getText().toString());
			String cityResult4edit = mCity.getText().toString();
			String countryResult4edit = mCountry.getText().toString();
			String noteResult4edit = mNote.getText().toString();
			
			if (noteResult4edit == null)
				noteResult4edit = getResources().getString(R.string.emptyNote);
			intent.putExtra("SavedDataTripID", id4edit);
			intent.putExtra("SavedDataTripCity", cityResult4edit);
			intent.putExtra("SavedDataTripCountry", countryResult4edit);
			intent.putExtra("SavedDataTripYear", yearResult4edit);
			intent.putExtra("SavedDataTripNote", noteResult4edit);
			
			Log.i(TAG, "info del viaje para editar: ("+id4edit+")" + cityResult4edit + ","
					+ countryResult4edit + "," + yearResult4edit.toString()
					+ "," + noteResult4edit);
			startActivityForResult(intent, REQUEST_CODE_EDIT_TRIP);
			return true;
		case R.id.menu_delete_travel:
				Log.i(TAG, "Etiqueta: Opcion BORRAR pulsada!");
			int idDB4delete=(int)BUNDLE_ID;
				Log.i(TAG, "---idDB4delete: "+ idDB4delete);			
			Uri uri = Uri.parse(TravelsProvider.CONTENT_URI + "/" + idDB4delete);
			getContentResolver().delete(uri, null, null);
			//## Volvemos a la lista de viajes			
			Intent intentDelete = new Intent(this, TravelListActivity.class);
			startActivity(intentDelete);
			return true;			
/**FIN VERSION 5**/			
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}// onMenuItemSelected()


	/**
	 * VERSION 5: Recoge los datos de un viaje editado.
	 *  Diferenciamos el metodo en funcion de si es un viaje nuevo o editado.
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		//**************Seccion para edicion de un viaje***********************//	
		if (requestCode == REQUEST_CODE_EDIT_TRIP && resultCode == RESULT_OK) {
			Log.i(TAG, "REQUEST_CODE_EDIT_TRIP: Viaje Editado!");
			String[] resultsTrip = data.getExtras().getStringArray("myTripCreated");
			int myNewTripID=Integer.valueOf(resultsTrip[0]);
			String myNewTripCity = resultsTrip[1];
			String myNewTripCountry = resultsTrip[2];
			int myNewTripYear = Integer.valueOf(resultsTrip[3]);
			String myNewTripNote = resultsTrip[4];
			
			Log.w(TAG, "info del Viaje Editado:"+myNewTripID+"-"+myNewTripCity+"("+myNewTripCountry+"),"+myNewTripYear+"--"+myNewTripNote);				
			ContentValues valuesTripEdited = new ContentValues();
			valuesTripEdited.put(TravelsConstants.CITY, myNewTripCity);
			valuesTripEdited.put(TravelsConstants.COUNTRY, myNewTripCountry);
			valuesTripEdited.put(TravelsConstants.YEAR, myNewTripYear);
			valuesTripEdited.put(TravelsConstants.NOTE, myNewTripNote);
			//## Update viaje en el Provider
			getContentResolver().update(TravelsProvider.CONTENT_URI, valuesTripEdited, TravelsConstants._ID+"="+myNewTripID, null);
			//## Volvemos a la lista de viajes
			Intent intent = new Intent(this, TravelListActivity.class);
			startActivity(intent);
		}
		else {
			Log.i(TAG, "ACCIÓN CANCELADA " + resultCode + "-" + requestCode);
		}

	}// onActivityResult()
	
}
