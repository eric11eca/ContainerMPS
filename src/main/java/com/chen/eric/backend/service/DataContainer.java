package com.chen.eric.backend.service;

import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.Vessel;

public class DataContainer {
	private static DataContainer dbContainer; 
	
	private DBConnectionService dbService = DBConnectionService.getInstance();
	  
	public static DataContainer getInstance() { 
		if (dbContainer == null) 
			dbContainer = new DataContainer(); 
		return dbContainer; 
	} 
	
	private EntityService<Vessel> vesselService = new VesselService(dbService);
	public Map<String, Vessel> vesselRecords = new HashMap<>();
	
	public void getVesselRecords() {
		vesselRecords = vesselService.retriveRecords();
		dbService.closeConnection();
	}
	public void insertVesselRecords (Vessel vessel) {
		vesselService.insertRecords(vessel);
	}
}
