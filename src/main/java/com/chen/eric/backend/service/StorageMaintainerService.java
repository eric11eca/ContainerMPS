package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.StorageMaintainer;

public class StorageMaintainerService implements EntityService<StorageMaintainer>{
	private DBConnectionService dbService;
	
	public StorageMaintainerService (DBConnectionService dbService) {
		this.dbService = dbService;
	}
	@Override
	public Map<String, StorageMaintainer> retriveRecords() {
		try {
			this.dbService.connect();
			String query = "{call View_StorageMaintainer()}";
			CallableStatement stmt = this.dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			ResultSet result = stmt.executeQuery();
			return parseResults(result);
		}catch(SQLException e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public Map<String, StorageMaintainer> parseResults(ResultSet rs) {
		try {
			HashMap<String, StorageMaintainer> StorageMaintainerTable = new HashMap<>();
			int StorageID = rs.findColumn("StorageID");
			int MaintainerID = rs.findColumn("MaintainerID");
			while(rs.next()) {
				StorageMaintainerTable.put(rs.getString(StorageID), new StorageMaintainer(rs.getInt(StorageID), rs.getInt(MaintainerID)));
			}
			return StorageMaintainerTable;
		}catch(SQLException e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int insertRecords(StorageMaintainer t) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Insert_StorageMaintainer(?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, t.getStorageID());
			stmt.setInt(3, t.getMaintainerID());
			stmt.execute();
			
			
			return stmt.getInt(1);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public Map<String, StorageMaintainer> retriveRecordsByParameters(String filter, String value) {
		if (filter.isEmpty()) {
			return new HashMap<>();
		}
			try {
				this.dbService.connect();
				String query = "{call Search_StorageMaintainer(?,?)}";
				CallableStatement stmt = this.dbService.getConnection().prepareCall(query);
				if (filter.equals("StorageID")) {
					stmt.setString(1, value);
				}else {
					stmt.setString(1, null);
				}
				
				if (filter.equals("MaintainerID")) {
					stmt.setString(2, value);
				}else {
					stmt.setString(2, null);
				}
				boolean hasRs = stmt.execute();
				ResultSet res = stmt.getResultSet();
				if (hasRs) {
				return parseResults(res);
				}
				return new HashMap<>();
			}catch(SQLException e) {
				e.printStackTrace();
				return new HashMap<>();
			}
			
		
	}

	@Override
	public int updateRecords(StorageMaintainer t, int key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateTwoKeyRecords(StorageMaintainer t, int primary, int secondary) {
		try {
			this.dbService.connect();
			String query = "{? = call Update_StorageMaintainer(?,?,?,?)}";
			CallableStatement stmt = this.dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(4, secondary);
			
			if (t.getStorageID() != null) {
				stmt.setInt(3, t.getStorageID());
			}else {
				stmt.setInt(3, -1);
			}
			
			if (t.getMaintainerID() != null) {
				stmt.setInt(5, t.getMaintainerID());
			}else {
				stmt.setInt(5, -1);
			}
			
			stmt.execute();
			return stmt.getInt(1);
		}catch(SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int deleteRecords(int ID) {
		try {
			dbService.connect();
			String query = "{? = call dbo.Delete_StorageMaintainer(?)}";
			
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

}
