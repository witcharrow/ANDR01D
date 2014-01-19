package es.alexmj.travellist;


/**
 * Esta clase contiene la informacion de un viaje. Los datos que almacena son la ciudad, el pais,
 * el anio del viaje y una nota opcional.
 * VERSION 1: Sin cambios.
 * VERSION 2: Sin cambios.
 * VERSION 3: Se aniade la BBDD a la aplicacion. Sin cambios.
 * VERSION 4: Aniadimos un provider para trabajar con la base de datos. Se añade un campo ID al viaje.
 * 
 * @author Alejandro.Marijuan@googlemail.com
 *
 */
public class TravelInfo {

	public static final String EXTRA_CITY = "extra_city";
	public static final String EXTRA_COUNTRY = "extra_country";
	public static final String EXTRA_YEAR = "extra_year";
	public static final String EXTRA_NOTE = "extra_note";
	private int idDB;
	private String city;
	private String country;
	private int year;
	private String note;
	/**VERSION 5**/
	public static final String EXTRA_ID="extra_id";
	
	
	/**
	 * Constructor del objeto TravelInfo.
	 * @param idDB 
	 * @param city es la ciudad
	 * @param country es el pais
	 * @param year es el anio
	 * @param note es la nota
	 */
	public TravelInfo(int idDB, String city, String country, int year, String note){
		this.idDB = idDB;
		this.city = city;
		this.country = country;
		this.year = year;
		this.note = note;
	}
	
	/**
	 * Constructor del objento TravelInfo.
	 * @param city es la ciudad
	 * @param country es el pais
	 * @param year es el anio
	 */
	public TravelInfo(int idDB,String city, String country, int year){
		this(idDB, city, country, year, null);
	}
	
	/**
	 * Obtiene el id de la base de datos.
	 * @return the idDB
	 */
	public int getIdDB() {
		return idDB;
	}

	/**
	 * Estable el id de la base de datos.
	 * @param idDB the idDB to set
	 */
	public void setIdDB(int idDB) {
		this.idDB = idDB;
	}
	
	/**
	 * Obtiene el nombre de la ciudad.
	 * @return nombre de la ciudad
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Establece el nombre de la ciudad.
	 * @param city es la ciudad
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Obtiene el nombre del pais.
	 * @return nombre del pais
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Establece el nombre del pais.
	 * @param country es el pais
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Obtiene el valor del anio.
	 * @return el valor del anio
	 */
	public int getYear() {
		return year;
	}
	
	/**
	 * Establece el valor del anio.
	 * @param year es el anio
	 */
	public void setYear(int year) {
		this.year = year;
	}
	
	/**
	 * Obtiene el contenido de la nota.
	 * @return el contenido de la nota
	 */
	public String getNote() {
		return note;
	}
	
	/**
	 * Establece el valor de la nota.
	 * @param note es la nota
	 */
	public void setNote(String note) {
		this.city = note;
	}		
}
