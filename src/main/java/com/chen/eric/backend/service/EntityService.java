package com.chen.eric.backend.service;

import java.sql.ResultSet;
import java.util.Map;

public interface EntityService<T> {
	abstract Map<String, T> retriveRecords();
	abstract Map<String, T> parseResults(ResultSet rs);
	abstract int insertRecords(T t);
	abstract Map<String, T> retriveRecordsByParameters(String filter, String value);
	abstract int updateRecords(T t, int key);
	int updateTwoKeyRecords(T t, int primary, int secondary);
	abstract int deleteRecords(int ID);
}
