package com.chen.eric.backend.service;

import java.sql.ResultSet;
import java.util.Map;

public interface EntityService<T> {
	abstract Map<String, T> retriveRecords();
	abstract Map<String, T> parseResults(ResultSet rs);
	void insertRecords(T t);
}
