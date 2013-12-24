package es.alexmj.travellist;


import java.util.ArrayList;
import es.alexmj.travellist.data.TravelsDatabaseHelper;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Muestra las opciones para editar,crear,borrar y compartir un viaje. Pantalla principal
 *  de la aplicacion. 
 * VERSION 1: Este ejemplo muestra el uso de una clase ListActivity que muestra
 * 			una lista de paises visitados.
 * 			Para ello hacemos uso de una extension del ArrayAdapter que contiene una
 * 			lista de objetos TravelInfo. El metodo getView del adapter se encarga de
 * 			mostrar la informacion de cada entrada TravelInfo de la forma correcta en la
 * 			vista. 
 * VERSION 2: Anadimos en esta version los controles necesarios para lanzar las
 * 			activities correspondientes a mostrar un viaje mediante un click (metodo
 * 			onListItemClick) y para crear un viaje usando el menu de opciones
 * 			(onCreateOptionsMenu y onMenuItemSelected).
 * VERSION 3: Nuevas funcionalidades para que interactuen unas activities con
 * 			otras. Ahora es posible añadir, borrar, editar y compartir un viaje. Se
 * 			aniade una base de datos que gestiona el almacenamiento de viajes.
 * VERSION 4: Aniadimos un provider para trabajar con la base de datos. Se aniade control por
 * 			  id de cada viaje.
 * 
 * @author Alejandro.Marijuan@googlemail.com
 * 
 */
public class TravelListActivity extends ListActivity {

	private static final String TAG = "TravelListActivity: --->";
	private static final int REQUEST_CODE_NEW_TRIP = 1;
	private static final int REQUEST_CODE_EDIT_TRIP = 2;	
	// ##Para comunicar con la EditTravelActivity
	private String result;
	
	// ##VERSION 3: Database
	private static TravelsDatabaseHelper dbHelper;
	private TravelAdapter adapter;

	/**
	 * Adapter que contiene la lista con los viajes del diario.
	 * @author Alejandro.Marijuan
	 *
	 */
	private class TravelAdapter extends ArrayAdapter<TravelInfo>{
		private Context context;
		private ArrayList<TravelInfo> travels;
		private static final int RESOURCE = android.R.layout.simple_list_item_2;
		public TravelAdapter(Context context, ArrayList<TravelInfo> travels) {
			super(context, RESOURCE, travels);
			this.context = context;
			this.travels = travels;
		}// TravelAdapter()

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout view;
			ViewHolder holder;
			if (convertView == null){
				view = new LinearLayout(context);
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				inflater.inflate(RESOURCE, view, true);
				holder = new ViewHolder();
				holder.text1 = (TextView) view.findViewById(android.R.id.text1);
				holder.text2 = (TextView) view.findViewById(android.R.id.text2);
				view.setTag(holder);				
			} else {
				view = (LinearLayout) convertView;
				holder = (ViewHolder) view.getTag();
			}			
			//## Rellenamos la vista con los datos
			TravelInfo info = travels.get(position);
			holder.text1.setText(info.getCity() + " (" + info.getCountry() + ")");
			holder.text2.setText(getResources().getString(R.string.year) + " " + info.getYear());			
			return view;
		}// getView()		
	}
	
	/**
	 * Vista para cada elemento de la lista.
	 * @author Alejandro.Marijuan
	 *
	 */
	static class ViewHolder {
		TextView text1;
		TextView text2;
	}// ViewHolder()

    /**
     * Genera la lista de viajes.
     * Genera un Intent para almacenar un viaje nuevo.
     * Asocia los menus contextuales a los controles.
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);        
        dbHelper = new TravelsDatabaseHelper(this);
        // ## Obtenemos los datos de la base de datos
        ArrayList<TravelInfo> values = dbHelper.getTravelsList();
        // ## Creamos el adapter y lo asociamos a la activity
        adapter = new TravelAdapter(this, values);        
        setListAdapter(adapter);        
        // ## Para devolver el resultado a la EditTravelActivity
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", result);
		setResult(RESULT_OK, returnIntent);
		// ## Asociamos los menús contextuales a los controles
		registerForContextMenu(getListView());
    }// onCreate()
    
    /**
     * Crea un menu de opciones, contiene la opcion generar un nuevo viaje.
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.travel_list_menu, menu);
		return true;
	}// onCreateOptionsMenu()

	/**
	 * Lanza un Intent para crear un nuevo viaje.
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d(TAG, "onMenuItemSelected");
		switch (item.getItemId()) {
		case R.id.menu_new_travel:
			//## Creamos el Intent para lanzar la Activity EditTravelActivity
			Intent intent = new Intent(this, EditTravelActivity.class);		
			startActivityForResult(intent, REQUEST_CODE_NEW_TRIP);
			break;
		}		
		return super.onMenuItemSelected(featureId, item);
	}// onMenuItemSelected()

	/**
	 * Crea el Intent con los datos necesarios para mostrar un viaje por pantalla.
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "onListItemClick");
		// ##Tomamos la informacion del viaje seleccionado
		TravelInfo info = adapter.getItem(position);
		// ##Creamos el Intent para lanzar la Activity TravelActivity
		Intent intent = new Intent(this, TravelActivity.class);
		// ##Aniadimos como extras los datos que consideremos necesarios para la
		// Activity a lanzar
		intent.putExtra(TravelInfo.EXTRA_CITY, info.getCity());
		intent.putExtra(TravelInfo.EXTRA_COUNTRY, info.getCountry());
		intent.putExtra(TravelInfo.EXTRA_YEAR, info.getYear());
		intent.putExtra(TravelInfo.EXTRA_NOTE, info.getNote());
		// ##Lanzamos la Activity con el Intent creado a TravelActivity
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}// onListItemClick()

	/**
	 * Recoge los datos de un viaje editado.
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		boolean newTripToAdd=false;
		//**************Seccion para un nuevo viaje***********************//		
		if (resultCode == RESULT_OK	&& data.getExtras().containsKey("myTripEdited")) {
			Log.i(TAG, "RESULT_OK");
			String[] resultsTrip = data.getExtras().getStringArray("myTripEdited");
			int myNewTripID=Integer.valueOf(resultsTrip[0]);
			if(myNewTripID==0){
				myNewTripID=dbHelper.getTravelsList().size()+1;
//					Log.i(TAG,"%%%%%%%%%%%%%%%%%%%%% CHANGE idTrip with:"+myNewTripID);
				newTripToAdd=true;
			}
			String myNewTripCity = resultsTrip[1];
			String myNewTripCountry = resultsTrip[2];
			int myNewTripYear = Integer.valueOf(resultsTrip[3]);
			String myNewTripNote = resultsTrip[4];
			TravelInfo myNewTripInfo = new TravelInfo(myNewTripID,myNewTripCity,
					myNewTripCountry, myNewTripYear, myNewTripNote);			
			if(newTripToAdd){
				Log.i(TAG, "Añadimos al adapter la info del viaje");
				adapter.add(myNewTripInfo);
				dbHelper.insertTravel(dbHelper.getWritableDatabase(), myNewTripCity, myNewTripCountry, myNewTripYear, myNewTripNote);
			}			
		//**************Seccion para edicion de un viaje***********************//	
			if (requestCode == REQUEST_CODE_EDIT_TRIP) {
				Log.i(TAG, "REQUEST_CODE_EDIT_TRIP");
//					Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%% ID del viaje ="+myNewTripInfo.getIdDB());
				int id=myNewTripInfo.getIdDB();
				dbHelper.updateTravel(myNewTripInfo, id);
				// ## Obtenemos los datos de la base de datos
		        ArrayList<TravelInfo> values = dbHelper.getTravelsList();
		        // ## Creamos el adapter y lo asociamos a la activity
		        adapter = new TravelAdapter(this, values);        
		        setListAdapter(adapter);
			}			
		} else {
			Log.i(TAG, "ACCIÓN CANCELADA " + resultCode + "-" + requestCode);
		}

	}// onActivityResult()

	/**
	 * Crea un menu con opciones para pulsacion larga sobre un item viaje.
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.d(TAG, "onCreateContextMenu");
		super.onCreateContextMenu(menu, v, menuInfo);
		Log.i(TAG, "pulsación larga...");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		menu.setHeaderTitle(R.string.menuContx);
	}// onCreateContextMenu()

	/**
	 * DEPRECATED!!-->Generamos datos a mostrar. En una aplicacion funcional se tomarian de base
	 *  de datos o algun otro medio
	 * @return
	 *
	public ArrayList<TravelInfo> getTravelsList() {
		Log.d(TAG, "getTravelsList");
		ArrayList<TravelInfo> travels = new ArrayList<TravelInfo>();
		Cursor c = getContentResolver().query(TravelsProvider.CONTENT_URI,
				null, null, null, Travels.YEAR);
		if (c != null && c.moveToFirst()) {
			int cityIndex = c.getColumnIndex(Travels.CITY);
			int countryIndex = c.getColumnIndex(Travels.COUNTRY);
			int yearIndex = c.getColumnIndex(Travels.YEAR);
			int noteIndex = c.getColumnIndex(Travels.NOTE);
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
		return travels;
	}// getTravelsList()*/
    
	/**
	 * Genera las opciones de COMPARTIR,EDITAR,BORRAR.
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.d(TAG, "onContextItemSelected");
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			return false;
		}
		long row = getListAdapter().getItemId(info.position);
		Log.i(TAG, "ListAdapter id = " + row + "---" + item.getTitle());

		switch (item.getItemId()) {
		case R.id.CtxLblOpc1:
			Log.i(TAG, "Etiqueta: Opcion 1 pulsada!--COMPARTIR");
			//## Creamos el Intent para lanzar la Activity que permita compartir el viaje
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
//DEL//			TravelInfo myTrip = adapter.getItem((int) id);
			
			TravelInfo myTrip = dbHelper.getTravelsList().get((int)row);
			// ## Obtenemos los datos para incluirlos en el intent.			
			String cityResult = myTrip.getCity();
			String countryResult = myTrip.getCountry();
			Integer yearResult = myTrip.getYear();
			String noteResult = myTrip.getNote();
			// ## Creamos la la forma en que quedará representado el viaje al
			// compartirlo
			if (noteResult == null)
				noteResult = getResources().getString(R.string.emptyNote);
			sendIntent.putExtra(Intent.EXTRA_TEXT,cityResult + "("
							+ countryResult + ")\n" + "Año: "
							+ yearResult.toString() + "\n" + "Nota: "
							+ noteResult);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources()
					.getText(R.string.send_to)));
			return true;
		case R.id.CtxLblOpc2:
			Log.i(TAG, "Etiqueta: Opcion 2 pulsada!--EDITAR");
			//## Creamos el Intent para lanzar la Activity EditTravelActivity
			Intent intent = new Intent(this, EditTravelActivity.class);
//DEL//			TravelInfo myTrip4Edit = adapter.getItem((int) id);
			TravelInfo myTrip4Edit = dbHelper.getTravelsList().get((int)row);			
			// ## Obtenemos los datos para incluirlos en el Intent
			int idDB4edit = myTrip4Edit.getIdDB();
			String cityResult4edit = myTrip4Edit.getCity();
			String countryResult4edit = myTrip4Edit.getCountry();
			Integer yearResult4edit = myTrip4Edit.getYear();
			String noteResult4edit = myTrip4Edit.getNote();
			Integer originalTripPosition = idDB4edit;
			if (noteResult4edit == null)
				noteResult4edit = getResources().getString(R.string.emptyNote);			
			intent.putExtra("SavedDataTripCity", cityResult4edit);
			intent.putExtra("SavedDataTripCountry", countryResult4edit);
			intent.putExtra("SavedDataTripYear", yearResult4edit);
			intent.putExtra("SavedDataTripNote", noteResult4edit);
			intent.putExtra("TripId", originalTripPosition);
			Log.i(TAG, "info del viaje para editar: " + cityResult4edit + ","
					+ countryResult4edit + "," + yearResult4edit.toString()
					+ "," + noteResult4edit + "," + originalTripPosition);
			startActivityForResult(intent, REQUEST_CODE_EDIT_TRIP);
			return true;
		case R.id.CtxLblOpc3:
				Log.i(TAG, "Etiqueta: Opcion 3 pulsada!--BORRAR");
				Log.i(TAG, "---Nro de viajes antes de BORRAR (DB): "+ dbHelper.getTravelsList().size());
			//DEL//Log.w(TAG, "Recogemos el id para la BD:"+dbHelper.getTravelsList().indexOf(id));
			TravelInfo myTrip4Delete = dbHelper.getTravelsList().get((int)row);
			int idDB4delete=myTrip4Delete.getIdDB();
			dbHelper.deleteTravel((int) idDB4delete);//## hay que incrementar la posicion, empieza desde 1 y no 0			
			adapter.remove(adapter.getItem((int) row));
			setListAdapter(adapter);
				Log.i(TAG, "---ADAPTER despues de BORRAR en adapter: "+ adapter.getCount());			
				Log.i(TAG, "---Numero de viajes despues de BORRAR (DB): "+ dbHelper.getTravelsList().size());
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}// onContextItemSelected()	
}
