package com.chen.eric.backend.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	public void insertRecords(Vessel vessel) {
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
		}
		catch (SQLException ex) {
			ex.printStackTrace();
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
