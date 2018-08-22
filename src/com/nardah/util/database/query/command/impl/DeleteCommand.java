package com.nardah.util.database.query.command.impl;


import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.options.impl.WhereConditionOption;

import java.util.List;

public class DeleteCommand extends SQLCommand {

	public DeleteCommand(String table) {
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
		 * Get a list of where condition SQL options
		 */
		List<WhereConditionOption> conditions = (List<WhereConditionOption>) getOptionsByType(WhereConditionOption.class);

		/*
		 * Check if where conditions SQL options are found
		 */
		if (!conditions.isEmpty()) {

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

		} else {
			query = query.replace("<CONDITIONS>", "");
		}

		return query;
	}

	@Override
	public String getQuery() {
		return "DELETE FROM <TABLE> <CONDITIONS>";
	}

}
