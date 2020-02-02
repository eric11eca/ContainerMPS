package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.Container;

public class ContainerService implements EntityService<Container>{
	private DBConnectionService dbService;
	public  ContainerService (DBConnectionService dbService) {
		this.dbService = dbService;
	}
	@Override
	public Map<String, Container> retriveRecords() {
		try {
			dbService.connect();
			String query = "SELECT Top(10) * FROM Container";
			
			PreparedStatement stmt =  dbService.getConnection().prepareStatement(query);			
			ResultSet rs = stmt.executeQuery();
			
			return parseResults(rs);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public Map<String, Container> parseResults(ResultSet rs) {
		try {
			Map<String, Container> ContainerTable = new HashMap<>();
			int ContainerID = rs.findColumn("ContainerID");
			int type = rs.findColumn("Type");
			int Length = rs.findColumn("Length");
			int Width = rs.findColumn("Width");
			int Height = rs.findColumn("Height");
			int Weight = rs.findColumn("Weight");
			int Owner = rs.findColumn("Owner");
			int Payed = rs.findColumn("Payed");
			int Fee = rs.findColumn("Fee");
			while (rs.next()) {
				ContainerTable.put(rs.getString(ContainerID), new Container(
						rs.getInt(ContainerID), rs.getString(type),
						rs.getDouble(Length),rs.getDouble(Width), 
						rs.getDouble(Height), rs.getDouble(Weight), rs.getString(Owner),
						rs.getBoolean(Payed),rs.getDouble(Fee)
						));
			}
			
			return ContainerTable;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int insertRecords(Container t) {
		try {
			dbService.connect();
			String query = "? = CALL dbo.Insert_Container(?,?,?,?,?,?,?,?,?)";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, t.getContainerID());
			stmt.setString(3, t.getType());
			stmt.setDouble(4, t.getLength());
			stmt.setDouble(5, t.getWidth());
			stmt.setDouble(6, t.getHeight());
			stmt.setDouble(7, t.getWeight());
			stmt.setString(8, t.getOwner());
			stmt.setBoolean(9, t.isPayed());
			stmt.setDouble(10, t.getFee());
			stmt.executeQuery();
			return stmt.getInt(1);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public Map<String, Container> retriveRecordsByParameters(String filter, String value) {
		if (filter.isEmpty()) {
			return new HashMap<>();
		}
		
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Search_Container(?,?,?,?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			
			stmt.registerOutParameter(1, Types.INTEGER);
			
			if (filter.equals("ContainerID")) {
				stmt.setString(2, value);
			} else {
				stmt.setString(2, null);
			}
			
			if (filter.equals("type")) {
				stmt.setString(3, value);
			} else {
				stmt.setString(3, null);
			}
			
			if (filter.equals("Length")) {
				stmt.setString(4, value);
			} else {
				stmt.setString(4, null);
			}
			
			if (filter.equals("Width")) {
				stmt.setString(5, value);
			} else {
				stmt.setString(5, null);
			}
			
			if (filter.equals("Height")) {
				stmt.setString(6, value);
			} else {
				stmt.setString(6, null);
			}
			
			if (filter.equals("Weight")) {
				stmt.setString(7, value);
			} else {
				stmt.setString(7, null);
			}
			if (filter.equals("isPayed")) {
				stmt.setString(8, value);
			} else {
				stmt.setString(8, null);
			}
			if (filter.equals("Fee")) {
				stmt.setString(9, value);
			} else {
				stmt.setString(9, null);
			}

			boolean hasRs = stmt.execute();
			
			Map<String, Container> ContainerTable = new HashMap<>();
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   while (rs.next()) {
	        		   ContainerTable.put(rs.getString("VesselID"), new Container(
	        				   rs.getInt("ContainerID"), rs.getString("Type"),
	    					   rs.getDouble("Length"),rs.getDouble("Width"), 
	    					   rs.getDouble("Height"), rs.getDouble("Weight"),
	    					   rs.getString("Owner"),rs.getBoolean("isPayed"),
	    					   rs.getDouble ("Fee")));
	    			}
	            }
	         }
			
			System.out.println("Return Value: " + stmt.getInt(1));

			return ContainerTable;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int updateRecords(Container t, int key) {
		
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_Container(?,?,?,?,?,?,?,?,?,?)}";
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, key);
			
			if (t.getContainerID() != null) {
				stmt.setInt(3, t.getContainerID());
			} else {
				stmt.setInt(3, -1);
			}
			
			if (t.getType() != null) {
				stmt.setString(4, t.getType());
			} else {
				stmt.setString(4, null);
			}
			
			if (t.getLength() != null) {
				stmt.setDouble(5, t.getLength());
			} else {
				stmt.setDouble(5, -1);
			}
			
			if (t.getWidth() != null) {
				stmt.setDouble(6, t.getWidth());
			} else {
				stmt.setDouble(6, -1);
			}
			
			if (t.getHeight() != null) {
				stmt.setDouble(7, t.getHeight());
			} else {
				stmt.setDouble(7, -1);
			}
			
			if (t.getWeight() != null) {
				stmt.setDouble(8, t.getWeight());
			} else {
				stmt.setDouble(8, -1);
			}
			if (t.getOwner() != null) {
				stmt.setString(9, t.getOwner());
			} else {
				stmt.setString(9, null);
			}

				stmt.setBoolean(10, t.isPayed());
			if (t.getFee() != null) {
					stmt.setDouble(11, t.getFee());
			} else {
					stmt.setDouble(11, -1);
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
			String query = "{? = call dbo.Delete_Container(?)";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);	
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, ID);
			stmt.execute();
			
			return stmt.getInt(1);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

}
