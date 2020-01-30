package com.chen.eric.backend.service;

import java.sql.ResultSet;
import java.util.Map;

import com.chen.eric.backend.Vessel;

public interface EntityService<T> {
	abstract Map<String, T> retriveRecords();
	abstract Map<String, T> parseResults(ResultSet rs);
	abstract int insertRecords(T t);
	abstract Map<String, Vessel> retriveRecordsByParameters(String filter, String value);
	abstract int updateRecords(T t, int key);
	abstract int deleteRecords(int ID);
}
