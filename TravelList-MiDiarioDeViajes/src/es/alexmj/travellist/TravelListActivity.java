package es.alexmj.travellist;


import java.util.ArrayList;

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
 * VERSION 1: Este ejemplo muestra el uso de una clase ListActivity que muestra
 * una lista de paises visitados.
 * 
 * Para ello hacemos uso de una extension del ArrayAdapter que contiene una
 * lista de objetos TravelInfo. El metodo getView del adapter se encarga de
 * mostrar la informacion de cada entrada TravelInfo de la forma correcta en la
 * vista.
 * 
 * VERSION 2: Anadimos en esta version los controles necesarios para lanzar las
 * activities correspondientes a mostrar un viaje mediante un click (metodo
 * onListItemClick) y para crear un viaje usando el menu de opciones
 * (onCreateOptionsMenu y onMenuItemSelected).
 * 
 * VERSION 3: Nuevas funcionalidades para que interactuen unas activities con
 * otras. Ahora es posible añadir, borrar, editar y compartir un viaje.
 * 
 * @author Alejandro.Marijuan
 * 
 */
public class TravelListActivity extends ListActivity {

	private static final String TAG = "TravelListActivity: --->";
	private static final int REQUEST_CODE_NEW_TRIP = 1;
	private static final int REQUEST_CODE_EDIT_TRIP = 2;
	private TravelAdapter adapter;
	// ## para comunicar con la EditTravelActivity
	private String result;

	/**
	 * @author Alejandro.Marijuan
	 * 
	 */
	private class TravelAdapter extends ArrayAdapter<TravelInfo> {

		private Context context;
		private ArrayList<TravelInfo> travels;
		private static final int RESOURCE = android.R.layout.simple_list_item_2;

		public TravelAdapter(Context context, ArrayList<TravelInfo> travels) {
			super(context, RESOURCE, travels);

			this.context = context;
			this.travels = travels;
		}// TravelAdapter()

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LinearLayout view;
			ViewHolder holder;

			if (convertView == null) {
				view = new LinearLayout(context);

				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				inflater.inflate(RESOURCE, view, true);

				holder = new ViewHolder();
				holder.text1 = (TextView) view.findViewById(android.R.id.text1);
				holder.text2 = (TextView) view.findViewById(android.R.id.text2);
				view.setTag(holder);

			} else {
				view = (LinearLayout) convertView;
				holder = (ViewHolder) view.getTag();
			}

			// Rellenamos la vista con los datos
			TravelInfo info = travels.get(position);
			holder.text1.setText(info.getCity() + " (" + info.getCountry()
					+ ")");
			holder.text2.setText(getResources().getString(R.string.year) + " "
					+ info.getYear());

			return view;
		}// getView()

	}

	static class ViewHolder {
		TextView text1;
		TextView text2;
	}// ViewHolder()

	/*
	 * (non-Javadoc) Clase original, sin modificar.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		// ## Generamos los datos
		ArrayList<TravelInfo> values = getData();
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
	 * Generamos datos a mostrar. En una aplicacion funcional se tomarian de
	 * base de datos o algun otro medio.
	 * 
	 * @return viajes creados
	 */
	private ArrayList<TravelInfo> getData() {
		Log.i(TAG, "getData");
		ArrayList<TravelInfo> travels = new ArrayList<TravelInfo>();

		TravelInfo info = new TravelInfo("Londres", "UK", 2012,
				"¡Juegos Olimpicos!");
		TravelInfo info2 = new TravelInfo("Paris", "Francia", 2007, "Roquefort");
		TravelInfo info3 = new TravelInfo("Gotham City", "EEUU", 2011,
				"¡¡Batman!!");
		TravelInfo info4 = new TravelInfo("Hamburgo", "Alemania", 2009,
				"Wunderbar!");
		TravelInfo info5 = new TravelInfo("Pekin", "China", 2011);
		TravelInfo info6 = new TravelInfo(getResources().getString(
				R.string.cityResult), getResources().getString(
				R.string.countryResult), Integer.valueOf(getResources()
				.getString(R.string.yearResult)), getResources().getString(
				R.string.noteResult));

		travels.add(info);
		travels.add(info2);
		travels.add(info3);
		travels.add(info4);
		travels.add(info5);
		travels.add(info6);
		return travels;
	}// getData()

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.travel_list_menu, menu);
		return true;
	}// onCreateOptionsMenu

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i(TAG, "onMenuItemSelected");
		switch (item.getItemId()) {
		case R.id.menu_new_travel:
			// Creamos el Intent para lanzar la Activity EditTravelActivity
			Intent intent = new Intent(this, EditTravelActivity.class);
			startActivityForResult(intent, REQUEST_CODE_NEW_TRIP);
			break;
		// case R.id.menu_share_travel:
		// Creamos el Intent para lanzar la Activity que permita compartir
		// el viaje
		// Intent sendIntent = new Intent();
		// sendIntent.setAction(Intent.ACTION_SEND);
		// sendIntent.putExtra(
		// Intent.EXTRA_TEXT,
		// getResources().getString(R.string.cityResult) + "("
		// + getResources().getString(R.string.countryResult)
		// + ")\n" + "Año: "
		// + getResources().getString(R.string.yearResult)
		// + "\n" + "Nota: "
		// + getResources().getString(R.string.noteResult));
		// sendIntent.setType("text/plain");
		// startActivity(Intent.createChooser(sendIntent, getResources()
		// .getText(R.string.send_to)));
		// break;

		// default:
		// Toast.makeText(this, "Invalid option", Toast.LENGTH_LONG).show();
		// break;
		}

		return super.onMenuItemSelected(featureId, item);
	}// onMenuItemSelected()

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(TAG, "onListItemClick");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK
				&& data.getExtras().containsKey("myTripEdited")) {
			String[] resultsTrip = data.getExtras().getStringArray(
					"myTripEdited");
			String myNewTripCity = resultsTrip[0];
			String myNewTripCountry = resultsTrip[1];
			int myNewTripYear = Integer.valueOf(resultsTrip[2]);
			String myNewTripNote = resultsTrip[3];
			TravelInfo myNewTripInfo = new TravelInfo(myNewTripCity,
					myNewTripCountry, myNewTripYear, myNewTripNote);
			adapter.add(myNewTripInfo);
			
			if (requestCode == REQUEST_CODE_EDIT_TRIP) {
				Log.i(TAG, "BORRAR VIAJE ANTIGUO " + resultCode + "-"
						+ requestCode);
				Log.i(TAG, "ID del viaje ="+data.getExtras().getInt("TripId"));
				int id=data.getExtras().getInt("TripId");
				adapter.remove(adapter.getItem((int) id));
			}
			
		} else {
			Log.i(TAG, "ACCIÓN CANCELADA " + resultCode + "-" + requestCode);
		}

	}// onActivityResult

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.i(TAG, "onCreateContextMenu");
		super.onCreateContextMenu(menu, v, menuInfo);
		Log.i(TAG, "pulsación larga...");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		menu.setHeaderTitle(R.string.menuContx);
		
		// if(v.getId() == R.id.LblMensaje)
		// inflater.inflate(R.menu.menu_ctx_etiqueta, menu);
		// else if(v.getId() == R.id.LstLista){
		// AdapterView.AdapterContextMenuInfo info =
		// (AdapterView.AdapterContextMenuInfo)menuInfo;
		//
		// menu.setHeaderTitle(
		// lstLista.getAdapter().getItem(info.position).toString());
		//
		// inflater.inflate(R.menu.menu_ctx_lista, menu);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(TAG, "onContextItemSelected");
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			return false;
		}
		long id = getListAdapter().getItemId(info.position);
		Log.i(TAG, "id = " + id + "---" + item.getTitle());

		switch (item.getItemId()) {
		case R.id.CtxLblOpc1:
			Log.i(TAG, "Etiqueta: Opcion 1 pulsada!--COMPARTIR");
			// Creamos el Intent para lanzar la Activity que permita compartir
			// el viaje
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			TravelInfo myTrip = adapter.getItem((int) id);
			String cityResult = myTrip.getCity();
			String countryResult = myTrip.getCountry();
			Integer yearResult = myTrip.getYear();
			String noteResult = myTrip.getNote();
			if (noteResult == null)
				noteResult = getResources().getString(R.string.emptyNote);
			sendIntent.putExtra(Intent.EXTRA_TEXT,
			// getResources().getString(R.string.cityResult) + "("
			// + getResources().getString(R.string.countryResult)
			// + ")\n" + "Año: "
			// + getResources().getString(R.string.yearResult)
			// + "\n" + "Nota: "
			// + getResources().getString(R.string.noteResult)
					cityResult + "(" + countryResult + ")\n" + "Año: "
							+ yearResult.toString() + "\n" + "Nota: "
							+ noteResult);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources()
					.getText(R.string.send_to)));
			return true;

		case R.id.CtxLblOpc2:
			Log.i(TAG, "Etiqueta: Opcion 2 pulsada!--EDITAR");
			// Creamos el Intent para lanzar la Activity EditTravelActivity
			Intent intent = new Intent(this, EditTravelActivity.class);
			TravelInfo myTrip4Edit = adapter.getItem((int) id);
			String cityResult4edit = myTrip4Edit.getCity();
			String countryResult4edit = myTrip4Edit.getCountry();
			Integer yearResult4edit = myTrip4Edit.getYear();
			String noteResult4edit = myTrip4Edit.getNote();
			Integer originalTripPosition = (int) id;
			
			if (noteResult4edit == null)
				noteResult4edit = getResources().getString(R.string.emptyNote);
			
			intent.putExtra("SavedDataTripCity", cityResult4edit);
			intent.putExtra("SavedDataTripCountry", countryResult4edit);
			intent.putExtra("SavedDataTripYear", yearResult4edit);
			intent.putExtra("SavedDataTripNote", noteResult4edit);
			intent.putExtra("TripId",originalTripPosition);
			
			Log.i(TAG, "info del viaje para editar: " + cityResult4edit + ","
					+ countryResult4edit + "," + yearResult4edit.toString()
					+ "," + noteResult4edit + "," + originalTripPosition);
			
			
			startActivityForResult(intent, REQUEST_CODE_EDIT_TRIP);
			return true;

		case R.id.CtxLblOpc3:
			Log.i(TAG, "Etiqueta: Opcion 3 pulsada!--BORRAR");
			adapter.remove(adapter.getItem((int) id));
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

}
