package com.nardah.util.database.query.options;


import com.nardah.util.database.query.command.SQLCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Telopya <telopya@gmail.com>
 *
 */
public abstract class SQLOption {

	/**
	 * An list of applicable {@link SQLCommand}
	 */
	protected List<Class<? extends SQLCommand>> APPLICABLES = new ArrayList<>();

	/**
	 * Returns the limit of a {@link SQLOption} to be used by a
	 * {@link SQLCommand}
	 * 
	 * @return the limit
	 */
	public abstract int getLimit();

	/**
	 * Returns a list of applicable {@link SQLCommand} for this
	 * {@link SQLOption}
	 * 
	 * @return a list of applicable SQL commands
	 */
	public abstract List<Class<? extends SQLCommand>> getApplicableCommands();

}
