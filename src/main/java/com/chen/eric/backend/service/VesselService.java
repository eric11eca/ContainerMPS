package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.Vessel;

public class VesselService implements EntityService<Vessel>{
	private DBConnectionService dbService;
	
	public  VesselService (DBConnectionService dbService) {
		this.dbService = dbService;
	}
	
	@Override
	public Map<String, Vessel> retriveRecords() {
		try {
			dbService.connect();
			String query = "SELECT Top(10) * FROM Vessel";
			
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
	public Map<String, Vessel> retriveRecordsByParameters(String filter, String value) {
		if (filter.isEmpty()) {
			return new HashMap<>();
		}
		
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Search_Vessel(?,?,?,?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			
			stmt.registerOutParameter(1, Types.INTEGER);
			
			if (filter.equals("VesselID")) {
				stmt.setString(2, value);
			} else {
				stmt.setString(2, null);
			}
			
			if (filter.equals("Capacity")) {
				stmt.setString(3, value);
			} else {
				stmt.setString(3, null);
			}
			
			if (filter.equals("DepartureDate")) {
				stmt.setString(4, value);
			} else {
				stmt.setString(4, null);
			}
			
			if (filter.equals("ArrivalDate")) {
				stmt.setString(5, value);
			} else {
				stmt.setString(5, null);
			}
			
			if (filter.equals("DepartedFrom_Country")) {
				stmt.setString(6, value);
			} else {
				stmt.setString(6, null);
			}
			
			if (filter.equals("Destination_Country")) {
				stmt.setString(7, value);
			} else {
				stmt.setString(7, null);
			}

			boolean hasRs = stmt.execute();
			
			Map<String, Vessel> vesselTable = new HashMap<>();
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   while (rs.next()) {
	        		   vesselTable.put(rs.getString("VesselID"), new Vessel(
	        				   rs.getInt("VesselID"), rs.getInt("Capacity"),
	    					   rs.getString("DepartedFrom_Country"),rs.getString("DepartedFrom_State"), 
	    					   rs.getString("DepartedFrom_City"), rs.getString("Destination_Country"),
	    					   rs.getString("Destination_State"),rs.getString("Destination_City"),
	    					   rs.getDate ("DepartureDate"), rs.getDate ("ArrivalDate")));
	    			}
	            }
	         }
			
			System.out.println("Return Value: " + stmt.getInt(1));

			return vesselTable;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}
	
	@Override
	public int updateRecords(Vessel vessel, int key) {
		dbService.connect();
		try {
			String query = "{? = CALL dbo.Update_Vessel(?,?,?,?,?,?,?)}";
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);
			
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, key);
			
			if (vessel.getVesselID() != null) {
				stmt.setInt(3, vessel.getVesselID());
			} else {
				stmt.setInt(3, 0);
			}
			
			if (vessel.getCapacity() != null) {
				stmt.setInt(4, vessel.getCapacity());
			} else {
				stmt.setInt(4, 0);
			}
			
			if (vessel.getDepartDate() != null) {
				stmt.setDate(5, vessel.getDepartDate());
			} else {
				stmt.setDate(5, null);
			}
			
			if (vessel.getArivalDate() != null) {
				stmt.setDate(6, vessel.getArivalDate());
			} else {
				stmt.setDate(6, null);
			}
			
			if (vessel.getDepartedFromCountry() != null) {
				stmt.setString(7, vessel.getDepartedFromCountry());
			} else {
				stmt.setString(7, null);
			}
			
			if (vessel.getDestinationCountry() != null) {
				stmt.setString(8, vessel.getDestinationCountry());
			} else {
				stmt.setString(8, null);
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
	public int deleteRecords(int VesselID) {
		try {
			dbService.connect();
			String query = "{? = call dbo.Delete_Vessel(?)";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);	
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, VesselID);
			stmt.execute();
			
			return stmt.getInt(1);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	@Override
	public int insertRecords(Vessel vessel) {
		try {
			dbService.connect();
			String query = "INSERT INTO Vessel Values(?,?,?,?,?,?,?,?,?,?)";
			
			PreparedStatement stmt =  dbService.getConnection().prepareStatement(query);	
			stmt.setInt(1, vessel.getVesselID());
			stmt.setInt(2, vessel.getCapacity());
			stmt.setDate(3, vessel.getArivalDate());
			stmt.setDate(4, vessel.getDepartDate());
			stmt.setString(5, vessel.getDestinationCountry());
			stmt.setString(6, vessel.getDestinationState());
			stmt.setString(7, vessel.getDestinationCity());
			stmt.setString(8, vessel.getDepartedFromCountry());
			stmt.setString(9, vessel.getDepartedFromState());
			stmt.setString(10, vessel.getDepartedFromCity());
			stmt.executeQuery();
			return 0;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public Map<String, Vessel> parseResults(ResultSet rs) {
		try {
			Map<String, Vessel> vesselTable = new HashMap<>();
			int vesselID = rs.findColumn("VesselID");
			int capacity = rs.findColumn("Capacity");
			int departureCountry = rs.findColumn("DepartedFrom_Country");
			int departureState = rs.findColumn("DepartedFrom_State");
			int departureCity = rs.findColumn("DepartedFrom_City");
			int destCountry = rs.findColumn("Destination_Country");
			int destState = rs.findColumn("Destination_State");
			int destCity = rs.findColumn("Destination_City");
			int departureDate = rs.findColumn("DepartureDate");
			int arrivalDate = rs.findColumn("ArrivalDate");
			while (rs.next()) {
				vesselTable.put(rs.getString(vesselID), new Vessel(
						rs.getInt(vesselID), rs.getInt(capacity),
						rs.getString(departureCountry),rs.getString(departureState), 
						rs.getString(departureCity), rs.getString(destCountry),
						rs.getString(destState),rs.getString(destCity),
						rs.getDate (departureDate), rs.getDate (arrivalDate)));
			}
			
			return vesselTable;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}
}
