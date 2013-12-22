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
 * Función para mostrar un picker de Anios. Eliminamos el día y el mes del picker.
 * @author Alejandro.Marijuan
 *
 */
public class DatePickerFragment extends DialogFragment {

	private static final String TAG = "DatePickerFragment: PruebaDatePicker--->";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.i(TAG, "onCreateDialog");
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();

		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);

		// Create a new instance of DatePickerDialog and return it
		//##Log.i(TAG, "onCreateDialog");
		
		return new CustomDatePickerDialog(day, month, year);
	}

	/**
	 * Personaliza el DatePicker, en nuestro caso nos mostrará únicamente el
	 * año.
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @return el resultado del año obtenido del DatePickerDialog
	 */
	private class CustomDatePickerDialog extends DatePickerDialog {
		
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
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			//super.onDateChanged(view, year, month, day);
			//no llamar a super, para no sobreescribir el título del encabezado del DatePickerDialog.			
		}	
		
	}

}