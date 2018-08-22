package com.nardah.util.database.query.options.impl;


import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.command.impl.InsertCommand;
import com.nardah.util.database.query.command.impl.UpdateCommand;
import com.nardah.util.database.query.options.SQLOption;

import java.util.List;

public class TableColumnValueOption extends SQLOption {

	/**
	 * The name of the table column
	 */
	private String name;

	/**
	 * The value of the table column
	 */
	private Object value;

	public TableColumnValueOption(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public int getLimit() {
		return -1;
	}

	@Override
	public List<Class<? extends SQLCommand>> getApplicableCommands() {

		APPLICABLES.add(UpdateCommand.class);
		APPLICABLES.add(InsertCommand.class);

		return APPLICABLES;
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}

}
