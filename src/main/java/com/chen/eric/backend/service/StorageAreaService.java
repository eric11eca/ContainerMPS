package com.chen.eric.backend.service;

import java.sql.ResultSet;
import java.util.Map;

import com.chen.eric.backend.StorageArea;

public class StorageAreaService implements EntityService<StorageArea> {

	public StorageAreaService(DBConnectionService dbService) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, StorageArea> retriveRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, StorageArea> parseResults(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insertRecords(StorageArea t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, StorageArea> retriveRecordsByParameters(String filter, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateRecords(StorageArea t, int key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteRecords(int ID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateTwoKeyRecords(StorageArea t, int primary, int secondary) {
		return 0;
	}

}
