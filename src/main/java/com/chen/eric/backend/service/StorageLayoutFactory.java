package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
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
			
			locations = new HashSet<>();
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
	        
	        System.out.println("Number of container: " + locations.size());
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	private Tier generateTire(int storageID, int blockID, int tierID) {
		Tier tire = new Tier();
		for (int i = 0; i < 6; i++) {
			Bay bay = new Bay();
			for (int j = 0; j < 5; j++) {
				bay.add(j, new Location(null, storageID, blockID, tierID, i,j,null,null));
			}
			tire.addBay(bay);
		}
		return tire;
	}
	
	private Block generateBlock(int storageID, int blockID) {
		Block block = new Block();
		for (int i = 0; i < 3; i++) {
			block.addTire(generateTire(storageID, blockID, i));
		}
		return block;
	}
	
	public StorageArea generateStorageArea(StorageArea storageArea) {
		for (int i = 0; i < 2; i++) {
			storageArea.addBlock(i, generateBlock(storageArea.getStorageID(), i));
		}
		
		retriveLocationByArea(storageArea.getStorageID());
		
		for (Location loc : locations) {
			updateStorageSlot(storageArea, loc, true);
		}
		
		return storageArea;
	}
	
	public void updateStorageSlot(StorageArea area, Location loc, boolean add) {
		int blockIndex = loc.getBlockIndex();
		int tireIndex = loc.getTierIndex();
		int bayIndex = loc.getBayIndex();
		int rowIndex = loc.getRowIndex();
		
		Bay bay = area.getBlock(blockIndex).getTier(tireIndex).getBay(bayIndex);
		if (add) {
			bay.addContainer(rowIndex, loc);
		} else {
			bay.removeContainer(rowIndex);
		}
	}
}
