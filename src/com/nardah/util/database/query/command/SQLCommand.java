package com.nardah.util.database.query.command;


import com.nardah.util.database.query.SQLQuery;
import com.nardah.util.database.query.options.SQLOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class of a SQL Command used for constructing a SQL query with
 * different types of SQL options
 * 
 * @author Telopya <telopya@gmail.com>
 *
 */
public abstract class SQLCommand extends SQLQuery {

	/**
	 * The table name that is used for this {@link SQLCommand}
	 */
	private String table;

	/**
	 * A list of {@link SQLOption}
	 */
	private List<SQLOption> options = new ArrayList<SQLOption>();

	/**
	 * Initialize the {@link SQLCommand}
	 * 
	 * @param table
	 *            the table name
	 */
	public SQLCommand(String table) {
		this.table = table;
	}

	/**
	 * Adds a {@link SQLOption} to the {@link SQLCommand}
	 * 
	 * @param option
	 *            the SQL option
	 */
	public void addOption(SQLOption option) {

		/*
		 * Check if a SQL option is set
		 */
		if (option == null)
			return;

		/*
		 * Check if SQL option is applicable on this SQL command
		 */
		if (!option.getApplicableCommands().contains(this.getClass()))
			return;

		/*
		 * Check if SQL option usage is unlimited
		 */
		if (option.getLimit() != -1) {

			int count = 0;

			for (SQLOption other : getOptions()) {

				/*
				 * Check if both SQL options are equal types
				 */
				if (!other.getClass().isInstance(option))
					continue;

				count++;

			}

			/*
			 * Check if SQL option passed the maximum usage limit
			 */
			if (count >= option.getLimit())
				return;

		}

		/*
		 * Add SQL option to the list
		 */
		getOptions().add(option);

	}

	/**
	 * 
	 * @param options
	 * @return
	 */
	public SQLCommand addOptions(SQLOption... options) {

		/*
		 * Loop through the options and verify each SQL option
		 */
		for (SQLOption option : options) {

			/*
			 * Add SQL Option to the list
			 */
			addOption(option);

		}

		return this;

	}

	/**
	 * Returns list of {@link SQLOption} by type
	 * 
	 * @param type
	 *            the SQL command type
	 * @return a list of {@link SQLOption}
	 */
	public List<? extends SQLOption> getOptionsByType(Class<? extends SQLOption> type) {

		List<SQLOption> options = new ArrayList<SQLOption>();

		/*
		 * Loop through all available options
		 */
		for (SQLOption option : getOptions()) {

			/*
			 * Check if option equals the type
			 */
			if (!type.isInstance(option))
				continue;

			/*
			 * Add option to the list
			 */
			options.add(option);

		}

		return options;
	}

	/**
	 * Returns the first {@link SQLOption} by type
	 * 
	 * @param type
	 *            the SQL command type
	 * @return a {@link SQLOption}
	 */
	public SQLOption getOptionByType(Class<? extends SQLOption> type) {

		/*
		 * Loop through all available options
		 */
		for (SQLOption option : getOptions()) {

			/*
			 * Check if option equals the type
			 */
			if (!type.isInstance(option))
				continue;

			/*
			 * Return the found SQL option
			 */
			return option;

		}

		return null;

	}

	/**
	 * The query of this {@link SQLCommand}
	 * 
	 * @return the SQL query
	 */
	public abstract String getQuery();

	/**
	 * Returns the table name that is used for this {@link SQLCommand}
	 * 
	 * @return the table name
	 */
	public String getTable() {
		return this.table;
	}

	/**
	 * Returns a list of {@link SQLOption}
	 * 
	 * @return the options
	 */
	public List<SQLOption> getOptions() {
		return this.options;
	}

}
