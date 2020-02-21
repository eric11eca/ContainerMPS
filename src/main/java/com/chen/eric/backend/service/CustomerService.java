package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.Customer;

public class CustomerService implements EntityService<Customer>{

	private DBConnectionService dbService;
	public  CustomerService (DBConnectionService dbService) {
		this.dbService = dbService;
	}
	
	@Override
	public Map<String, Customer> retriveRecords() {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.View_Customer()}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			boolean hasRs = stmt.execute();
			
			Map<String, Customer> customerTable = new HashMap<>();
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   customerTable =  parseResults(rs);
	           }
	        }	
	        return customerTable;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public Map<String, Customer> parseResults(ResultSet rs) {
		try {
			Map<String, Customer> CustomerTable = new HashMap<>();
			int CustomerID = rs.findColumn("CustomerID");
			int CompanyName = rs.findColumn("CompanyName");
			int ContactEmail = rs.findColumn("ContactEmail");
			int Country = rs.findColumn("Country");
			int State = rs.findColumn("State");
			while (rs.next()) {
				CustomerTable.put(rs.getString(CustomerID), new Customer(
						rs.getInt(CustomerID), rs.getString(CompanyName),
						rs.getString(ContactEmail),rs.getString(Country), 
						rs.getString(State)));
			}
			
			return CustomerTable;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int insertRecords(Customer t) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Insert_Customer(?,?,?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, t.getCustomerID());
			stmt.setString(3, t.getCompanyName());
			stmt.setString(4, t.getContactEmail());
			stmt.setString(5, t.getCountry());
			stmt.setString(6, t.getState());
			stmt.execute();
			return stmt.getInt(1);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public Map<String, Customer> retriveRecordsByParameters(String filter, String value) {
		if (filter.isEmpty()) {
			return new HashMap<>();
		}
		
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Search_Customer(?,?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			
			stmt.registerOutParameter(1, Types.INTEGER);
			
			if (filter.equals("CustomerID")) {
				stmt.setString(2, value);
			} else {
				stmt.setString(2, null);
			}
			
			if (filter.equals("Company Name")) {
				stmt.setString(3, value);
			} else {
				stmt.setString(3, null);
			}
			
			if (filter.equals("Contact Email")) {
				stmt.setString(4, value);
			} else {
				stmt.setString(4, null);
			}
			
			if (filter.equals("Country")) {
				stmt.setString(5, value);
			} else {
				stmt.setString(5, null);
			}

			boolean hasRs = stmt.execute();
			
			Map<String, Customer> CustomerTable = new HashMap<>();
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   while (rs.next()) {
	        		   CustomerTable.put(rs.getString("CustomerID"), new Customer(
	        				   rs.getInt("CustomerID"), rs.getString("CompanyName"),
	    					   rs.getString("ContactEmail"),rs.getString("Country"), 
	    					   rs.getString("State")));
	    			}
	            }
	         }
			
		//	System.out.println("Return Value: " + stmt.getInt(1));

			return CustomerTable;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int updateRecords(Customer t, int key) {
		
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_Customer(?,?,?,?,?,?)}";
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, key);
			
			if (t.getCustomerID() != null) {
				stmt.setInt(3, t.getCustomerID());
			} else {
				stmt.setInt(3, -1);
			}
			
			if (t.getCompanyName() != null) {
				stmt.setString(4, t.getCompanyName());
			} else {
				stmt.setString(4, null);
			}
			
			if (t.getContactEmail() != null) {
				stmt.setString(5, t.getContactEmail());
			} else {
				stmt.setString(5, null);
			}
			
			if (t.getCountry() != null) {
				stmt.setString(6, t.getCountry());
			} else {
				stmt.setString(6, null);
			}
			
			if (t.getState() != null) {
				stmt.setString(7, t.getState());
			} else {
				stmt.setString(7, null);
			}

			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public int deleteRecords(int ID) {
		try {
			dbService.connect();
			String query = "{? = call dbo.Delete_Customer(?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);	
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, ID);
			stmt.execute();
			
			return stmt.getInt(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	@Override
	public int updateTwoKeyRecords(Customer t, int primary, int secondary) {
		return 0;
	}

}
