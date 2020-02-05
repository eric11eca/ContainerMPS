package com.chen.eric.backend.service;

import java.sql.ResultSet;
import java.util.Map;

import com.chen.eric.backend.Location;

public class LocationService implements EntityService<Location> {

	public LocationService(DBConnectionService dbService) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, Location> retriveRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Location> parseResults(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insertRecords(Location t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Location> retriveRecordsByParameters(String filter, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateRecords(Location t, int key) {
		// No Implementation
		return 0;
	}

	@Override
	public int deleteRecords(int ID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateTwoKeyRecords(Location t, int primary, int secondary) {
		// TODO Auto-generated method stub
		return 0;
	}

}
