package com.nardah.util.database.query;


import java.util.HashMap;
import java.util.Map;

/**
 * The SQL query with parameter binding support and SQL Command buildling
 * support
 * 
 * @author Telopya
 *
 */
public abstract class SQLQuery {

	/**
	 * An map of parameters of this {@link SQLQuery}
	 */
	private Map<Integer, Object> parameters = new HashMap<Integer, Object>();

	/**
	 * Constructs the {@link SQLQuery} into an executable
	 * 
	 * @return the executable query
	 */
	public abstract String construct();

	/**
	 * Adds a parameter to the mapping
	 */
	public SQLQuery addParameter(Object value) {
		parameters.put(parameters.size() + 1, value);
		return this;
	}

	/**
	 * Returns map of parameters of this {@link SQLQuery}
	 */
	public Map<Integer, Object> getParameters() {
		return this.parameters;
	}

}
