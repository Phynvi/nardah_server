package com.nardah.util.database.result;


import com.nardah.util.database.query.SQLQuery;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Telopya
 *
 */
public class QueryResult {

	/**
	 * The {@link SQLQuery} of this {@link QueryResult}
	 */
	private SQLQuery query;

	/**
	 * The amount of affected rows after executing {@link SQLQuery}
	 */
	private int rows;

	/**
	 * The {@link ResultSet} of this {@link QueryResult}
	 */
	private ResultSet set;

	/**
	 * Construct a new {@link QueryResult} after executing an update
	 * 
	 * @param query
	 * @param result
	 */
	public QueryResult(SQLQuery query, int result) {
		this.query = query;
		this.rows = result;
	}

	/**
	 * Construct a new {@link QueryResult} afer executing a query
	 * 
	 * @param query
	 * @param result
	 */
	public QueryResult(SQLQuery query, ResultSet result) {
		this.query = query;
		this.set = result;
	}

	public ResultSet getRow() {

		try {

			/*
			 * Move pointer to the next row
			 */
			boolean state = getResultSet().next();

			/*
			 * Return ResultSet if next row is found
			 */
			return state ? getResultSet() : null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Get row count of the current {@link ResultSet}
	 */
	public int getRowCount() {

		try {

			/*
			 * Get current pointer position
			 */
			int current = getResultSet().getRow();

			/*
			 * Set pointer to the last row
			 */
			getResultSet().last();

			/*
			 * Get the amount of rows count
			 */
			int count = getResultSet().getRow();

			/*
			 * Set pointer to the previous position
			 */
			if (current != 0)
				getResultSet().absolute(current);
			else
				getResultSet().beforeFirst();

			return count;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public List<String> getColumnNames() {

		try {

			ResultSetMetaData meta = getResultSet().getMetaData();
			int count = meta.getColumnCount();

			List<String> columns = new ArrayList<String>(count);

			for (int i = 1; i <= count; i++)
				columns.add(meta.getColumnName(i));

			return columns;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Terminate the {@link QueryResult} for garbage collection
	 */
	public void terminate() {

		try {

			/*
			 * Check if a result set is set
			 */
			if (getResultSet() == null)
				return;

			/*
			 * Check if result set is already closed
			 */
			if (getResultSet().isClosed())
				return;

			/*
			 * Close the result set
			 */
			getResultSet().close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the {@link SQLQuery} of this {@link QueryResult}
	 */
	public SQLQuery getQuery() {
		return this.query;
	}

	/**
	 * Returns the amount of affected rows after executing the {@link SQLQuery}
	 */
	public int getAffectedRows() {
		return this.rows;
	}

	/**
	 * Returns the {@link ResultSet} of this {@link QueryResult}
	 */
	public ResultSet getResultSet() {
		return this.set;
	}

}
