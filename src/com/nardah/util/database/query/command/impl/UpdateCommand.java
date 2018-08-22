package com.nardah.util.database.query.command.impl;


import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.options.impl.TableColumnValueOption;
import com.nardah.util.database.query.options.impl.WhereConditionOption;

import java.util.List;

/**
 * The SQL Command used for updating information in a table in a SQL database
 * 
 * @author Telopya <telopya@gmail.com>
 *
 */
public class UpdateCommand extends SQLCommand {

	/**
	 * Initialize the {@link UpdateCommand}
	 * 
	 * @param table
	 *            the table name
	 */
	public UpdateCommand(String table) {
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

			StringBuilder builder = new StringBuilder();

			/*
			 * Loop through the table column value options
			 */
			for (TableColumnValueOption column : columns) {

				/*
				 * Add table column name to the string
				 */
				builder.append(column.getName() + "=?").append(",");

				/*
				 * Add table column value to the map of parameters
				 */
				addParameter(column.getValue());

			}

			/*
			 * Replace the values tag with the constructed string
			 */
			query = query.replace("<VALUES>", builder.substring(0, builder.length() - 1));

		}

		/*
		 * Get a list of where condition SQL options
		 */
		List<WhereConditionOption> conditions = (List<WhereConditionOption>) getOptionsByType(WhereConditionOption.class);

		/*
		 * Check if where conditions SQL options are found
		 */
		if (conditions.isEmpty()) {
			query = query.replace("<CONDITIONS>", "");
		} else {

			StringBuilder builder = new StringBuilder();

			builder.append("WHERE ");

			/*
			 * Loop through the conditions
			 */
			for (int i = 0; i < conditions.size(); i++) {

				WhereConditionOption condition = conditions.get(i);

				/*
				 * Add a where condition to the string
				 */
				builder.append(condition.getName() + condition.getOperator() + "?");

				/*
				 * Check if this is the last condition
				 */
				if (i < conditions.size() - 1) {
					builder.append(" AND ");
				}

				/*
				 * Add condition value to the map of parameters
				 */
				addParameter(condition.getValue());

			}

			query = query.replace("<CONDITIONS>", builder.toString());

		}

		return query;
	}

	@Override
	public String getQuery() {
		return "UPDATE <TABLE> SET <VALUES> <CONDITIONS>";
	}

}
