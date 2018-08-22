package com.nardah.util.database.query.options.impl;


import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.command.impl.SelectCommand;
import com.nardah.util.database.query.options.SQLOption;

import java.util.List;

/**
 * The SQL Option used for defining the range of columns of an executed
 * {@link SelectCommand}
 * 
 * @author Telopya <telopya@gmail.com>
 * @version 1.0
 *
 */
public class SelectionRangeOption extends SQLOption {

	/**
	 * An array of column names that have to be selected
	 */
	private String[] columns;

	/**
	 * Initialize this {@link SelectionRangeOption}
	 * 
	 * @param column
	 *            the column name
	 */
	public SelectionRangeOption(String column) {
		this.columns = new String[] { column };
	}

	/**
	 * Intialize this {@link SelectionRangeOption}
	 * 
	 * @param columns
	 *            an array of column names
	 */
	public SelectionRangeOption(String... columns) {
		this.columns = columns;
	}

	@Override
	public int getLimit() {
		return 1;
	}

	@Override
	public List<Class<? extends SQLCommand>> getApplicableCommands() {

		APPLICABLES.add(SelectCommand.class);

		return APPLICABLES;
	}

	/**
	 * Returns an array of column names that have to be selected
	 * 
	 * @return an array of column names
	 */
	public String[] getColumns() {
		return this.columns;
	}

}
