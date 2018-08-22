package com.nardah.util.database.query.options.impl;


import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.command.impl.SelectCommand;
import com.nardah.util.database.query.options.SQLOption;

import java.util.List;

/**
 * 
 * @author Telopya <telopya@gmail.com>
 *
 */
public class OrderByKeywordOption extends SQLOption {

	/**
	 * The table column name that has to be sorted
	 */
	private String name;

	/**
	 * The direction of the table column name has to be sorted
	 */
	private OrderByDirection direction;

	/**
	 * Initialize this {@link OrderByDirection}
	 * 
	 * @param name
	 *            the table column name
	 * @param direction
	 *            the direction that has to be sorted
	 */
	public OrderByKeywordOption(String name, OrderByDirection direction) {
		this.name = name;
		this.direction = direction;
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
	 * Returns the table column name of this {@link OrderByKeywordOption}
	 * 
	 * @return the table column name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the direction of the table column name that has to be sorted
	 * 
	 * @return the {@link OrderByDirection}
	 */
	public OrderByDirection getDirection() {
		return this.direction;
	}

	public enum OrderByDirection {
		ASCENDING,
		DESCENDING;
	}

}
