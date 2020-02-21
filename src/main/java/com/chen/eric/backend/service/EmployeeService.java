package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.Employee;

public class EmployeeService implements EntityService<Employee>{
	private DBConnectionService dbService;
	
	public  EmployeeService (DBConnectionService dbService) {
		this.dbService = dbService;
	}
	
	@Override
	public Map<String, Employee> retriveRecords() {
		try {
			dbService.connect();
			String query = "{? = call dbo.Retrive_Employee()}";
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			boolean hasRs = stmt.execute();
			
			Map<String, Employee> employees = new HashMap<>();
			if (hasRs) {
				try (ResultSet rs = stmt.getResultSet()) {     	
					employees = parseResults(rs);
				}
			}
			return employees;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public Map<String, Employee> parseResults(ResultSet rs) {
		try {
			Map<String, Employee> employeeTable = new HashMap<>();
			int usernameIndex = rs.findColumn("Username");
			int ssnIndex = rs.findColumn("SSN");
			int nameIndex = rs.findColumn("Name");
			int roleIndex = rs.findColumn("Role");
			int enableIndex = rs.findColumn("Enable");

			while (rs.next()) {
				employeeTable.put(String.valueOf(rs.getInt(ssnIndex)), 
						new Employee(rs.getInt(ssnIndex), rs.getString(nameIndex), 
								rs.getString(roleIndex), rs.getString(usernameIndex),
								rs.getBoolean(enableIndex)));
			}
			
			return employeeTable;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	/** No Implementation **/
	public int insertRecords(Employee t) {
		return -1;
	}

	@Override
	public Map<String, Employee> retriveRecordsByParameters(String filter, String value) {
		if (filter.isEmpty()) {
			return new HashMap<>();
		}
		
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Search_Employee(?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			
			stmt.registerOutParameter(1, Types.INTEGER);
			
			if (filter.equals("SSN")) {
				stmt.setString(2, value);
				System.out.println(value);
			} else {
				stmt.setString(2, null);
			}
			
			if (filter.equals("Role")) {
				stmt.setString(3, value);
			} else {
				stmt.setString(3, null);
			}
			
			if (filter.equals("Name")) {
				stmt.setString(4, value);
			} else {
				stmt.setString(4, null);
			}
			

			boolean hasRs = stmt.execute();
			
			Map<String, Employee> employeeTable = new HashMap<>();
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   while (rs.next()) {
	        		   employeeTable.put(rs.getString("SSN"), new Employee(
	        				   rs.getInt("SSN"), rs.getString("Name"),
	    					   rs.getString("Role"),rs.getString("Username"), 
	    					   rs.getBoolean("Enable")));
	    			}
	            }
	         }
			
		//	System.out.println("Return Value: " + stmt.getInt(1));

			return employeeTable;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int updateRecords(Employee e, int key) {
		dbService.connect();
		
		final String activateQuery = "{? = call dbo.Activate_User(?)}";
		final String deactivateQuery = "{? = call dbo.Deactivate_User(?)}";
		final String updateRoleQuery = "{? = call dbo.Update_Role(?, ?)}";
		
		CallableStatement stmt = null;
		try {
			if (key == 1) {
				stmt =  dbService.getConnection().prepareCall(activateQuery);
				stmt.setString(2, e.getUserName());
			} else if (key == 2) {
				stmt =  dbService.getConnection().prepareCall(deactivateQuery);
				stmt.setString(2, e.getUserName());
			} else if (key == 3) {
				stmt =  dbService.getConnection().prepareCall(updateRoleQuery);
				stmt.setInt(2, e.getSSN());
				stmt.setString(3, e.getRole());
			}
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.execute();
			return stmt.getInt(1);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return -1;
		}		
	}

	@Override
	public int deleteRecords(int ID) {
		dbService.connect();
		final String query = "{? = call dbo.Delete_User(?)}";
		CallableStatement stmt;
		try {
			stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, ID);
			stmt.execute();
			return stmt.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int updateTwoKeyRecords(Employee t, int primary, int secondary) {
		return 0;
	}

}
