package es.alexmj.travellist.data;

import android.provider.BaseColumns;

/**
 * Clase de constantes con la informacion de la base de datos.
 *
 */
public class TravelsConstants implements BaseColumns {	
	/**
	 * Travel table name
	 */
	public static final String TRAVELS_TABLE_NAME = "travels";
	/**
	 * The city of the travel
	 * <P>Type: TEXT</P>
	 */
	public static final String CITY = "city";
	/**
	 * The city of the travel
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
