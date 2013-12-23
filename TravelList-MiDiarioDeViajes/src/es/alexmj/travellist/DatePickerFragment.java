package es.alexmj.travellist;

import java.lang.reflect.Field;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

/**
 * Funcion para mostrar un picker de Anios. Eliminamos el dia y el mes del picker.
 * VERSION 1: Creacion del DatePickerFragment.
 * VERSION 2: Sin cambios.
 * VERSION 3: Se aniade la BBDD a la aplicacion. Sin cambios.
 * 
 * @author Alejandro.Marijuan@googlemail.com
 *
 */
public class DatePickerFragment extends DialogFragment {

	private static final String TAG = "DatePickerFragment: EditTravelActiviy--->";

	/**
	 * Crea una instancia dialogo para obtener el anio y devuelve la fecha.
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG, "onCreateDialog");
		//## Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		//## Create a new instance of DatePickerDialog and return it		
		return new CustomDatePickerDialog(day, month, year);
	}// onCreateDialog()

	/**
	 * Personaliza el DatePicker, en nuestro caso nos mostrará únicamente el
	 * anio.
	 * 
	 * @param day es el dia
	 * @param month es el mes
	 * @param year es el anio
	 * @return el resultado del año obtenido del DatePickerDialog
	 */
	private class CustomDatePickerDialog extends DatePickerDialog {
		
		/**
		 * Personaliza la vista para recoger de una fecha, unicamente el valor del anio.
		 * @param day como dia
		 * @param month como mes
		 * @param year como anio
		 */
		public CustomDatePickerDialog(int day, int month, int year) {
			super(getActivity(), (OnDateSetListener) getActivity(), year, month, day);
			setTitle(R.string.year);
			
			try {
				Field[] datePickerDialogFields = this.getDatePicker()
						.getClass().getDeclaredFields();
				for (Field datePickerDialogField : datePickerDialogFields) {
					//##Log.i(TAG,"Pillo: "+ datePickerDialogField.getName());
					if (datePickerDialogField.getName().equals("mDaySpinner") ||
							datePickerDialogField.getName().equals("mMonthSpinner")) {
						//##Log.i(TAG,"Cojo el dia: "+ datePickerDialogField.getName());
						datePickerDialogField.setAccessible(true);
						Object dayPicker = new Object();
						dayPicker = datePickerDialogField.get(getDatePicker());
						((View) dayPicker).setVisibility(View.GONE);;
					}
				}					
			} catch (Exception ex) {
			}
		}// CustomDatePickerDialog()

		/**
		 * Se deja en blanco, no se llama al superconstructor para que no sobreescriba el 
		 *  encabezado del dialogo cada vez que se modifique la fecha.
		 * @see android.app.DatePickerDialog#onDateChanged(android.widget.DatePicker, int, int, int)
		 */
		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			//super.onDateChanged(view, year, month, day);
			//no llamar a super, para no sobreescribir el título del encabezado del DatePickerDialog.			
		}//onDateChanged()	
	}
}