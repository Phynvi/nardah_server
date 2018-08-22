package com.nardah.util.database.query.command.impl;


import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.options.impl.TableColumnValueOption;

import java.util.List;

/**
 * The SQL Command used for inserting information into a SQL database table
 * 
 * @author Telopya <telopya@gmail.com>
 *
 */
public class InsertCommand extends SQLCommand {

	/**
	 * Initialize the {@link InsertCommand}
	 * 
	 * @param table
	 *            the table name
	 */
	public InsertCommand(String table) {
		super(table);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String construct() {

		String query = getQuery();

		/*
		 * Replace the table name
		 */
		query = query.replace("<TABLE>", getTable());

		/*
		 * Get a list of table column value options
		 */
		List<TableColumnValueOption> columns = (List<TableColumnValueOption>) getOptionsByType(TableColumnValueOption.class);

		/*
		 * Check if table column value options are found
		 */
		if (columns.isEmpty()) {
			return null;
		} else {

			StringBuilder keys = new StringBuilder();
			StringBuilder values = new StringBuilder();

			/*
			 * Loop through the table column value options
			 */
			for (TableColumnValueOption column : columns) {

				/*
				 * Add table column name to the keys string
				 */
				keys.append(column.getName()).append(",");

				/*
				 * Add parameter sign to the values string
				 */
				values.append("?").append(",");

				/*
				 * Add table column value to the map of parameters
				 */
				addParameter(column.getValue());

			}

			/*
			 * Replace the keys tag with the constructed string
			 */
			query = query.replace("<KEYS>", keys.substring(0, keys.length() - 1));

			/*
			 * Replace the keys tag with the constructed string
			 */
			query = query.replace("<VALUES>", values.substring(0, values.length() - 1));

		}

		return query;

	}

	@Override
	public String getQuery() {
		return "INSERT INTO <TABLE> (<KEYS>) VALUES (<VALUES>)";
	}

}
