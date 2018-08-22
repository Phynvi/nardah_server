package com.nardah.util.database.query;


import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLExecution<T extends Object> {

	public T execute(ResultSet rs) throws SQLException;

}
