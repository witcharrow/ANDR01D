package es.alexmj.travellist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

		Intent intent = getIntent();
		BUNDLE_CITY = intent.getExtras().getString(TravelInfo.EXTRA_CITY);
		BUNDLE_COUNTRY = intent.getExtras().getString(TravelInfo.EXTRA_COUNTRY);
		BUNDLE_YEAR = intent.getExtras().getInt(TravelInfo.EXTRA_YEAR);
		BUNDLE_NOTE = intent.getExtras().getString(TravelInfo.EXTRA_NOTE);
		// ##Toast.makeText(this, "TravelActivity: " +
		// city+","+country+",("+year+"),"+ note , Toast.LENGTH_LONG).show();

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
		return true;
	}// onCreateOptionsMenu()

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
		default:
			Toast.makeText(this, "Invalid option", Toast.LENGTH_LONG).show();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}// onMenuItemSelected()

}
