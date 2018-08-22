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
public class LimitClauseOption extends SQLOption {

	/**
	 * The maximum rows that have to be returned
	 */
	private int maximum;

	/**
	 * The position where the command has to start at
	 */
	private int position;

	public LimitClauseOption(int maximum, int position) {
		this.maximum = maximum;
		this.position = position;
	}

	public LimitClauseOption(int maximum) {
		this(maximum, 0);
	}

	@Override
	public int getLimit() {
		return 1;
	}

	@Override
	public List<Class<? extends SQLCommand>> getApplicableCommands() {

		/*
		 * Add the select command to the list
		 */
		APPLICABLES.add(SelectCommand.class);

		return APPLICABLES;
	}

	public int getMaxmimum() {
		return this.maximum;
	}

	public int getPosition() {
		return this.position;
	}

}
