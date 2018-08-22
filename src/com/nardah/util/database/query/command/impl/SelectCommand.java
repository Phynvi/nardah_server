package com.nardah.util.database.query.command.impl;


import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.options.impl.LimitClauseOption;
import com.nardah.util.database.query.options.impl.OrderByKeywordOption;
import com.nardah.util.database.query.options.impl.SelectionRangeOption;
import com.nardah.util.database.query.options.impl.WhereConditionOption;

import java.util.List;

/**
 * The SQL Command used for gathering information from a table in a SQL database
 * 
 * @author Telopya <telopya@gmail.com>
 *
 */
public class SelectCommand extends SQLCommand {

	/**
	 * Initialize the {@link SelectCommand}
	 * 
	 * @param table
	 *            the table name
	 */
	public SelectCommand(String table) {
		super(table);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String construct() {

		String query = getQuery();

		/*
		 * Replace the table name
		 */
		query = query.replace("<TABLE>", getTable());

		/*
		 * Get the selection range SQL option
		 */
		SelectionRangeOption range = (SelectionRangeOption) getOptionByType(SelectionRangeOption.class);

		/*
		 * Check if selection range SQL option is found
		 */
		if (range == null) {
			query = query.replace("<RANGE>", "*");
		} else {
			query = query.replace("<RANGE>", String.join(",", range.getColumns()));
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
				builder.append(condition.getName() + " " + condition.getOperator() + " " + "?");

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

		/*
		 * Get a list of order by keyword SQL options
		 */
		List<OrderByKeywordOption> ordering = (List<OrderByKeywordOption>) getOptionsByType(OrderByKeywordOption.class);

		/*
		 * Check if order by keyword SQL options are found
		 */
		if (ordering.isEmpty()) {
			query = query.replace("<ORDERING>", "");
		} else {

			StringBuilder builder = new StringBuilder();

			builder.append("ORDER BY ");

			/*
			 * Loop through the conditions
			 */
			for (int i = 0; i < ordering.size(); i++) {

				OrderByKeywordOption order = ordering.get(i);

				/*
				 * Add order by keyword to the string
				 */
				builder.append(order.getName() + " " + (order.getDirection() == OrderByKeywordOption.OrderByDirection.ASCENDING ? "ASC" : "DESC"));

				/*
				 * Check if this is the last condition
				 */
				if (i < ordering.size() - 1) {
					builder.append(",");
				}

			}

			query = query.replace("<ORDERING>", builder.toString());

		}

		/*
		 * Get the limit clause SQL option
		 */
		LimitClauseOption clause = (LimitClauseOption) getOptionByType(LimitClauseOption.class);

		/*
		 * Check if selection range SQL option is found
		 */
		if (clause == null) {
			query = query.replace("<CLAUSE>", "");
		} else {
			query = query.replace("<CLAUSE>", "LIMIT " + clause.getPosition() + ", " + clause.getMaxmimum());
		}

		return query;

	}

	@Override
	public String getQuery() {
		return "SELECT <RANGE> FROM <TABLE> <CONDITIONS> <ORDERING> <CLAUSE>";
	}

}
