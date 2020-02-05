package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import com.chen.eric.backend.Bay;
import com.chen.eric.backend.Block;
import com.chen.eric.backend.Location;
import com.chen.eric.backend.StorageArea;
import com.chen.eric.backend.Tier;

public class StorageLayoutFactory {
	private Set<Location> locations;
	private DBConnectionService dbService;
	
	public StorageLayoutFactory(DBConnectionService dbService) {
		this.dbService = dbService;
	}
	
	private void retriveLocationByArea(int storageID) {
		try {
			dbService.connect();
			String query = "{call dbo.View_Location(?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);	
			stmt.setInt(1, storageID);
			boolean hasRs = stmt.execute();
			
	        if (hasRs) {
	           try (ResultSet rs = stmt.getResultSet()) {     	
	        	   while (rs.next()) {
	        		   locations.add(
	        			  new Location(
	        				  rs.getInt("ContainerID"), 
	        				  rs.getInt("StorageID"),
	        				  rs.getInt("BlockIndex"),
	        				  rs.getInt("TierIndex"),
	        				  rs.getInt("BayIndex"),
	        				  rs.getInt("RowIndex"), null, null));
	        	   }
	           }
	        }	
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	private Tier generateTire() {
		Tier tire = new Tier();
		for (int i = 0; i < 6; i++) {
			Bay bay = new Bay();
			tire.addBay(bay);
		}
		return tire;
	}
	
	private Block generateBlock() {
		Block block = new Block();
		for (int i = 0; i < 3; i++) {
			block.addTire(generateTire());
		}
		return block;
	}
	
	public void generateStorageArea(StorageArea storageArea) {
		for (int i = 0; i < 2; i++) {
			storageArea.addBlock(i, generateBlock());
		}
		
		retriveLocationByArea(storageArea.getStorageID());
		
		for (Location loc : locations) {
			updateStorageSlot(storageArea, loc, true);
		}
	}
	
	public void updateStorageSlot(StorageArea area, Location loc, boolean add) {
		int blockIndex = loc.getBlockIndex();
		int tireIndex = loc.getTireIndex();
		int bayIndex = loc.getBayIndex();
		int rowIndex = loc.getRowIndex();
		
		Bay bay = area.getBlock(blockIndex).getTirey(tireIndex).getBay(bayIndex);
		if (add) {
			bay.addContainer(rowIndex, loc);
		} else {
			bay.removeContainer(rowIndex);
		}
	}
}
