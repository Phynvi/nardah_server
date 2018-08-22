package com.nardah.util.database.query.options.impl;


import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.command.impl.DeleteCommand;
import com.nardah.util.database.query.command.impl.SelectCommand;
import com.nardah.util.database.query.command.impl.UpdateCommand;
import com.nardah.util.database.query.options.SQLOption;

import java.util.List;

/**
 * 
 * @author Telopya <telopya@gmail.com>
 *
 */
public class WhereConditionOption extends SQLOption {

	/**
	 * The name of the table column for this condition
	 */
	private String name;

	/**
	 * The value of the table column for this condition
	 */
	private Object value;

	/**
	 * The operator used for the condition
	 */
	private String operator;

	/**
	 * Initialize this {@link WhereConditionOption}
	 * 
	 * @param name
	 *            the table column name
	 * @param value
	 *            the table column value
	 * @param operator
	 *            the operator used for this condition
	 */
	public WhereConditionOption(String name, Object value, String operator) {
		this.name = name;
		this.value = value;
		this.operator = operator;
	}

	/**
	 * Initialize this {@link WhereConditionOption}
	 * 
	 * @param name
	 *            the table column name
	 * @param value
	 *            the table column value
	 */
	public WhereConditionOption(String name, Object value) {
		this(name, value, "=");
	}

	@Override
	public int getLimit() {
		return -1;
	}

	@Override
	public List<Class<? extends SQLCommand>> getApplicableCommands() {

		APPLICABLES.add(SelectCommand.class);
		APPLICABLES.add(UpdateCommand.class);
		APPLICABLES.add(DeleteCommand.class);

		return APPLICABLES;
	}

	/**
	 * Returns the name of the table column of this condition
	 * 
	 * @return the table column name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the value of the table column of this condition
	 * 
	 * @return the table column value
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Returns the operator used for this condition
	 * 
	 * @return the operator for comparing
	 */
	public String getOperator() {
		return this.operator;
	}

}
