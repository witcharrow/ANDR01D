package es.alexmj.travellist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.app.DatePickerDialog;

/**
 * Muestra un formulario por pantalla para introducir los datos de un viaje.
 * VERSION 1: EditTravelActivity vacia a modo de ejemplo. Sustituye esta clase por
 *            la clase EditTravelActivity que creaste para el ejercicio de la unidad 2. 
 * VERSION 2: incluye ahora funcionalidad para pasar realizar operaciones CRUD con cada viaje. 
 * VERSION 3: Se aniade una nueva Base de datos para almacenar los viajes. Ahora en lugar de 
 * 			  borrar un viaje y reemplazarlo por uno nuevo, aniadimos funcionalidad de update. 
 * 
 * @author Alejandro.Marijuan@googlemail.com
 *
 */
public class EditTravelActivity extends FragmentActivity implements
DatePickerDialog.OnDateSetListener {

	private static final String TAG = "EditTravelActivity: --->";
	//## Variables necesarias para calcular el año	
	private static final String BUNDLE_DAY = "day";
	private static final String BUNDLE_MONTH = "month";
	private static final String BUNDLE_YEAR = "anno";
	private DatePickerFragment datePickerFragment;	
	private EditText mEdit;
	private int mYear;
	private int mMonth;
	private int mDay;	
	
	//## Variables para ciudad, pais y nota
	private static final String BUNDLE_CITY = "ciudad";
	private static final String BUNDLE_COUNTRY = "pais";	
	private static final String BUNDLE_NOTE = "nota";	
	private EditText mCity;
	private EditText mCountry;
	private EditText mNote;
	private boolean trip4edit=false;
	private Integer yearResult4edit=0;
	
	/**
	 * Operaciones para seleccionar los datos.
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_travel);		
		mEdit = (EditText) findViewById(R.id.pick_year);
		mEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.i(TAG, "onClick");
				showDatePickerDialog(view);
			}
		});		
		
		//## Parte para Ciudad, Pais y nota		
		mCity = (EditText) findViewById(R.id.city);
		mCountry = (EditText) findViewById(R.id.country);
		mNote = (EditText) findViewById(R.id.note);
		final Button saveTrip = (Button) findViewById(R.id.boton_visita);		
		
		Intent myTripEdited = getIntent();
		
		Bundle extrasFromTrip4edit = myTripEdited.getExtras(); 
		
		if (extrasFromTrip4edit != null) {
			String cityResult4edit = extrasFromTrip4edit.getString("SavedDataTripCity");
			String countryResult4edit = extrasFromTrip4edit.getString("SavedDataTripCountry");
			yearResult4edit = extrasFromTrip4edit.getInt("SavedDataTripYear");					
			String noteResult4edit = extrasFromTrip4edit.getString("SavedDataTripNote");
			Log.i(TAG, "yearResult4edit RECOGIDO: " + yearResult4edit);		
			
			mCity.setText(cityResult4edit);
			mCountry.setText(countryResult4edit);
			mEdit.setText(new StringBuilder().append(yearResult4edit).append(" "));
			mNote.setText(noteResult4edit);
			Log.i(TAG, "yearResult4edit ESTABLECIDO: " + yearResult4edit);
			trip4edit = true;
		}
		
		
		saveTrip.setOnClickListener(new OnClickListener() {					

			public void onClick(View v) {				
				if(checkInputData(saveTrip)){
					if(mYear==0 && trip4edit)
						mYear=yearResult4edit;
					String [] resultsTrip={mCity.getText().toString(),
										   mCountry.getText().toString(),
										   Integer.valueOf(mYear).toString(),
										   mNote.getText().toString()};
					Intent myTripEdited = getIntent();
					Log.i(TAG,"setOnClickListener --> "
							+ mCity.getText().toString() + "("
							+ mCountry.getText().toString() + ")" + ",Año: "
							+ Integer.valueOf(mYear).toString() + ",Nota: "
							+ mNote.getText().toString());					
					myTripEdited.putExtra("myTripEdited", resultsTrip);					
					setResult(RESULT_OK,myTripEdited);
					finish();
				}
			}
		});
				
	}// onCreate()
		
	/**
	 * Muestra el dialogo para introducir el anio.
	 * @param view dialogo
	 */
	public void showDatePickerDialog(View view) {
		Log.d(TAG, "showDatePickerDialog");
		//## inflate DatePicker, or e.g. get it from a DatePickerDialog:
		datePickerFragment = new DatePickerFragment();
		datePickerFragment.show(getFragmentManager(), "DatePicker");

	}// showDatePickerDialog()
	
	/**
	 * Guarda los datos de la fecha.
	 * @param year como anio
	 * @param monthString como mes
	 * @param day como dia
	 */
	public void populateSetDate(int year, String monthString, int day) {
		Log.d(TAG, "populateSetDate");
		mEdit = (EditText) findViewById(R.id.pick_year);
		mEdit.setText(new StringBuilder()
		//## Month is 0 based so add 1
				.append(year).append(" "));
	}// populateSetDate()
	
	/**
	 * Establece la fecha a partir de los valores recogidos. 
	 * @see android.app.DatePickerDialog.OnDateSetListener#onDateSet(android.widget.DatePicker, int, int, int)
	 */
	@Override
	public void onDateSet(DatePicker view, int yy, int mm, int dd) {		
		mDay = dd;
		mMonth = mm + 1;
		mYear = yy;
		String monthString = mMonth < 10 ? "0" + mMonth : Integer
				.toString(mMonth);
		Log.i(TAG, "onDateSet: "+dd+"/"+monthString+"/"+yy);
		populateSetDate(yy, monthString, dd);
	}// onDateSet()
		
	/**
	 * Guarda la instancia de la fecha y los datos del viaje introducidos. 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState");
		//## Guardamos el estado
		int day = mDay;
		int month = mMonth;
		int anno = mYear;
		String ciudad = mCity.getText().toString();
		String pais = mCountry.getText().toString();
		String nota = mNote.getText().toString();		
		outState.putInt(BUNDLE_DAY, day);
		outState.putInt(BUNDLE_MONTH, month);		
		outState.putInt(BUNDLE_YEAR, anno);
		outState.putString(BUNDLE_CITY, ciudad);
		outState.putString(BUNDLE_COUNTRY, pais);
		outState.putString(BUNDLE_NOTE, nota);
		
		super.onSaveInstanceState(outState);
	}// onSaveInstanceState()

	/**
	 * Restablece la instancia de la fecha y los datos del viaje introducidos.
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "onRestoreInstanceState");
		//## Restauramos el estado
		if (savedInstanceState != null){
			String ciudad = savedInstanceState.getString(BUNDLE_CITY);
			String pais = savedInstanceState.getString(BUNDLE_COUNTRY);
			String nota = savedInstanceState.getString(BUNDLE_NOTE);
			
			mCity.setText(ciudad);
			mCountry.setText(pais);
			mNote.setText(nota);
		}
		
		super.onRestoreInstanceState(savedInstanceState);
	}// onRestoreInstanceState()
	
	/**
	 * Muestra un mensaje por pantalla si se han introducido los datos de Ciudad, Pais y Anio.
	 * La nota es opcional, si los otros parámetros no están rellenos en el formulario muestra
	 *   un mensaje indicandolo.
	 * @param button para mostrar la información a guardar del viaje
	 */
	private boolean checkInputData(Button button) {
		Log.d(TAG, "checkInputData");
		boolean allDataFill = false;
		if (!mCity.getText().toString().isEmpty()
				&& !mCountry.getText().toString().isEmpty()) {			
			allDataFill = true;
		} else {
			Toast.makeText(EditTravelActivity.this,
					getResources().getString(R.string.errorInput),
					Toast.LENGTH_LONG).show();
		}
		return allDataFill;
	}// checkInputData()
}
