package database;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String[] columns;
	private final String[][] result;
	
	public QueryResult(ResultSet r) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		ArrayList<String> row = new ArrayList<String>();
		ArrayList<String> columns = new ArrayList<String>();
		ResultSetMetaData meta = null;
		try {
			meta = r.getMetaData();
		} catch (SQLException e1) {}
		try {
			//Fetch column names
			for(int i=1;i<=meta.getColumnCount();i++) {
				columns.add(meta.getColumnLabel(i));
			}
			//Fetch data
			while(r.next()) {
				try {
					for(int i=1;i<=meta.getColumnCount();i++) {
						row.add(r.getString(i));
					}
					result.add(row.toArray(new String[1]));
					row.clear();
				} catch(Exception ex){}
			}
		} catch(Exception e){}
		this.columns = columns.toArray(new String[1]);
		this.result = result.toArray(new String[1][1]);
	}
	
	public String[] getColumnNames() {
		return columns;
	}
	
	public String getColumnName(int i) {
		return columns[i];
	}
	
	public String getResult(int row, int column) {
		try {
			return result[row][column];
		} catch(Exception e){}
		return null;
	}
	
	public String getResult(int row, String columnName) {
		try {
			for(int i=0;i<=getColumns();i++) {
				if(columns[i].equalsIgnoreCase(columnName))
					return result[row][i];
			}
		} catch(Exception e){}
		return null;
	}
	
	public String[] getRow(int row) {
		try {
			return result[row];
		} catch(Exception e){}
		return null;
	}
	
	public int getColumns() {
		return columns.length;
	}
	
	public int getRows() {
		return result.length;
	}
	
	public String toString() {
		String s = "";
		for(int i = 0 ; i < getRows() ; i++) {
			for(int j = 0 ; j < getColumns() ; j++) {
				s += "\t|\t" + getResult(i,j);
			}
			s += "\t|\n";
		}
		return s;
	}
	
}
