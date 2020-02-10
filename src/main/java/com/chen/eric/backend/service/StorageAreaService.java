package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.StorageArea;

public class StorageAreaService implements EntityService<StorageArea> {

	private DBConnectionService dbService;
	public  StorageAreaService (DBConnectionService dbService) {
		this.dbService = dbService;
	}

	@Override
	public Map<String, StorageArea> retriveRecords() {
		try {
			dbService.connect();
			String query = "{call dbo.View_Storage()}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);						
			boolean hasRs = stmt.execute();
			
			Map<String, StorageArea> StorageAreaTable = new HashMap<>();
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   StorageAreaTable =  parseResults(rs);
	           }
	        }	
	        return StorageAreaTable;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public Map<String, StorageArea> parseResults(ResultSet rs) {
		try {
			Map<String, StorageArea> StorageAreaTable = new HashMap<>();
			int StorageID = rs.findColumn("StorageID");
			int type = rs.findColumn("Type");
			int Capacity = rs.findColumn("Capacity");
			int StoragePrice = rs.findColumn("StoragePrice");
			while (rs.next()) {
				StorageAreaTable.put(rs.getString(StorageID), new StorageArea(
						rs.getInt(StorageID), rs.getString(type),
						rs.getInt(Capacity),rs.getDouble(StoragePrice)
						));
			}
			
			return StorageAreaTable;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int insertRecords(StorageArea t) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Insert_StorageArea(?,?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, t.getStorageID());
			stmt.setString(3, t.getType());
			stmt.setInt(4, t.getCapacity());
			stmt.setDouble(5, t.getStoragePrice());
			stmt.execute();
			
			return stmt.getInt(1);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public Map<String, StorageArea> retriveRecordsByParameters(String filter, String value) {
		if (filter.isEmpty()) {
			return new HashMap<>();
		}
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Search_StorageArea(?,?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			
			if (filter.equals("StorageAreaID")) {
				stmt.setString(2, value);
			} else {
				stmt.setString(2, null);
			}
			
			if (filter.equals("Type")) {
				stmt.setString(3, value);
			} else {
				stmt.setString(3, null);
			}
			
			if (filter.equals("Capacity")) {
				stmt.setString(4, value);
			} else {
				stmt.setString(4, null);
			}
			
			if (filter.equals("StoragePrice")) {
				stmt.setString(5, value);
			} else {
				stmt.setString(5, null);
			}
			
			boolean hasRs = stmt.execute();
			
			Map<String, StorageArea> StorageAreaTable = new HashMap<>();
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   StorageAreaTable = parseResults(rs);
	           }
	        }
			
			//System.out.println("Return Value: " + stmt.getInt(1));
			return StorageAreaTable;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int updateRecords(StorageArea t, int key) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_StorageArea(?,?,?,?,?)}";
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, key);
			
			if (t.getStorageID() != null) {
				stmt.setInt(3, t.getStorageID());
			} else {
				stmt.setInt(3, -1);
			}
			
			if (t.getType() != null) {
				stmt.setString(4, t.getType());
			} else {
				stmt.setString(4, null);
			}
			
			if (t.getCapacity() != null) {
				stmt.setInt(5, t.getCapacity());
			} else {
				stmt.setInt(5, -1);
			}
			
			if (t.getStoragePrice() != null) {
				stmt.setDouble(6, t.getStoragePrice());
			} else {
				stmt.setDouble(6, -1);
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
			String query = "{? = call dbo.Delete_StorageArea(?)}";
			
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
	public int updateTwoKeyRecords(StorageArea t, int primary, int secondary) {
		return 0;
	}
}
