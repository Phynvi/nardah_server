package com.nardah.util.database.query.command.impl;


import com.nardah.util.database.query.command.SQLCommand;

public class RawQueryCommand extends SQLCommand {

	private final String query;

	public RawQueryCommand(final String query) {
		super(null);
		this.query = query;
	}

	@Override
	public String getQuery() {
		return this.query;
	}

	@Override
	public String construct() {
		return this.query;
	}

}
