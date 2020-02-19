package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.Location;

public class LocationService implements EntityService<Location> {
	private DBConnectionService dbService;
	
	public  LocationService (DBConnectionService dbService) {
		this.dbService = dbService;
	}
	
	public int countContainerInArea(String area) {
		try {
			dbService.connect();
			String query = "{? = call dbo.Num_Container_Area(?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);		
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, area);
			stmt.execute();
			int count = stmt.getInt(1);
			return count;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public Map<String, Location> retriveRecords() {
		try {
			dbService.connect();
			String query = "{call dbo.View_StoredAt()}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);						
			boolean hasRs = stmt.execute();
			
			Map<String, Location> LocationTable = new HashMap<>();
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   LocationTable =  parseResults(rs);
	           }
	        }	
	        return LocationTable;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public Map<String, Location> parseResults(ResultSet rs) {
		try {
			Map<String, Location> LocationTable = new HashMap<>();
			int Location_ContainerID = rs.findColumn("ContainerID");
			int Location_StorageID = rs.findColumn("StorageID");
			int BlockIndex = rs.findColumn("BlockIndex");
			int RowIndex = rs.findColumn("RowIndex");
			int TierIndex = rs.findColumn("TierIndex");
			int BayIndex = rs.findColumn("BayIndex");
			int StartDate = rs.findColumn("StartDate");
			int EndDate = rs.findColumn("EndDate");
			while (rs.next()) {
				LocationTable.put(rs.getString(Location_ContainerID), new Location(
						rs.getInt(Location_ContainerID), rs.getInt(Location_StorageID), rs.getInt(BlockIndex),
						rs.getInt(RowIndex),rs.getInt(TierIndex), rs.getInt(BayIndex),rs.getDate(StartDate),rs.getDate(EndDate)
						));
			}
			
			return LocationTable;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int insertRecords(Location t) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Insert_StoredAt(?,?,?,?,?,?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, t.getContainerID());
			stmt.setInt(3, t.getStorageID());
			stmt.setInt(4, t.getBlockIndex());
			stmt.setInt(5, t.getRowIndex());
			stmt.setInt(6, t.getTierIndex());
			stmt.setInt(7, t.getBayIndex());
			stmt.setDate(8, t.getStartDate());
			stmt.setDate(9, t.getEndDate());
			stmt.execute();
			
			return stmt.getInt(1);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public Map<String, Location> retriveRecordsByParameters(String filter, String value) {
		if (filter.isEmpty()) {
			return new HashMap<>();
		}
		
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Search_StoredAt(?,?,?,?,?,?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			
			if (filter.equals("ContainerID")) {
				stmt.setString(2, value);
			} else {
				stmt.setString(2, null);
			}
			
			if (filter.equals("StorageID")) {
				stmt.setString(3, value);
			} else {
				stmt.setString(3, null);
			}
			
			if (filter.equals("BlokIndex")) {
				stmt.setString(4, value);
			} else {
				stmt.setString(4, null);
			}
			
			if (filter.equals("RowIndex")) {
				stmt.setString(5, value);
			} else {
				stmt.setString(5, null);
			}
			
			if (filter.equals("TierIndex")) {
				stmt.setString(6, value);
			} else {
				stmt.setString(6, null);
			}
			if (filter.equals("BayIndex")) {
				stmt.setString(7, value);
			} else {
				stmt.setString(7, null);
			}
			if (filter.equals("StartDate")) {
				stmt.setString(8, value);
			} else {
				stmt.setString(8, null);
			}
			if (filter.equals("EndDate")) {
				stmt.setString(9, value);
			} else {
				stmt.setString(9, null);
			}

			boolean hasRs = stmt.execute();
			
			Map<String, Location> LocationTable = new HashMap<>();
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   LocationTable = parseResults(rs);
	           }
	        }
			
			System.out.println("Return Value: " + stmt.getInt(1));
			return LocationTable;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int updateRecords(Location t, int key) {
		// No Implementation
		return 0;
	}

	@Override
	public int deleteRecords(int ID) {
		try {
			dbService.connect();
			String query = "{? = call dbo.Delete_StoredAt(?)}";
			
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

	@Override
	public int updateTwoKeyRecords(Location t, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_StoredAt(?,?,?,?,?,?,?,?,?)}";
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(4, secondary);
			
			if (t.getContainerID() != null) {
				stmt.setInt(3, t.getContainerID());
			} else {
				stmt.setInt(3, -1);
			}
			
			if (t.getStorageID() != null) {
				stmt.setInt(5, t.getStorageID());
			} else {
				stmt.setInt(5, -1);
			}
			
			if (t.getBlockIndex() != null) {
				stmt.setInt(5, t.getBlockIndex());
			} else {
				stmt.setInt(5, -1);
			}
			
			if (t.getRowIndex() != null) {
				stmt.setInt(6, t.getRowIndex());
			} else {
				stmt.setInt(6, -1);
			}
			
			if (t.getTierIndex() != null) {
				stmt.setInt(7, t.getTierIndex());
			} else {
				stmt.setInt(7, -1);
			}
			
			if (t.getBayIndex() != null) {
				stmt.setInt(8, t.getBayIndex());
			} else {
				stmt.setInt(8, -1);
			}
			if (t.getStartDate() != null) {
				stmt.setDate(9, t.getStartDate());
			} else {
				stmt.setDate(9, null);
			}

			if (t.getEndDate() != null) {
					stmt.setDate(10, t.getEndDate());
			} else {
					stmt.setDate(10, null);
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

}
