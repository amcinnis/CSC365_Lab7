public interface DatabaseObject {
	/**
	 * Method to get all keys for attributes in database
	 * Typically used for database queries
	 * @return The keys corresponding to the database table columns
	 */
	public String getKeys();

	/**
	 * Method to get value string of values from the given object.
	 * The values should match the keys from the getKeys() method.
	 * Typically used for database queries
	 * @return A string of values corresponding to the database keys
	 */
	public String getValues();
	
	/**
	 * Method to return the name of the table the object is contained in
	 * Typically used for database queries
	 * @return The table name
	 */
	public String getTable();
	
	/**
	 * Method to get the unique key specific to this object
	 * Typically used for database queries
	 * @return the primary key to fetch this object from the database
	 */
	public String getKeyIdentifier();
	

}
