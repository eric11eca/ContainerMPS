package com.chen.eric.backend.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import com.chen.eric.backend.Employee;
import com.chen.eric.backend.Vessel;

public class DataContainer {
	private static DataContainer dbContainer; 
	Authentication authentication;
	
	private DBConnectionService dbService = DBConnectionService.getInstance();
	  
	public static DataContainer getInstance() { 
		if (dbContainer == null)
			dbContainer = new DataContainer(); 
		return dbContainer; 
	} 
	
	private DataContainer() {
	}
	
	private EntityService<Vessel> vesselService = new VesselService(dbService);
	public Map<String, Vessel> vesselRecords = new HashMap<>();
	
	public void getVesselRecords() {
		vesselRecords = vesselService.retriveRecords();
		dbService.closeConnection();
	}
	
	public void getVesselRecordsByParams(String filter, String value) {
		vesselRecords = vesselService.retriveRecordsByParameters(filter, value);
		dbService.closeConnection();
	}
	
	public int insertVesselRecords (Vessel vessel) {
		int code = vesselService.insertRecords(vessel);
		dbService.closeConnection();
		return code;
	}
	
	public int updateVesselRecords (Vessel vessel, int key) {
		int code = vesselService.updateRecords(vessel, key);
		dbService.closeConnection();
		return code;
	}
	
	public int deleteVesselRecords (int vesselID) {
		int code = vesselService.deleteRecords(vesselID);
		dbService.closeConnection();
		return code;
	}
	
	private EntityService<Employee> employeeService = new EmployeeService(dbService);
	public Map<String, Employee> employeeRecords = new HashMap<>();
	
	public void getEmployeeRecords() {
		employeeRecords = employeeService.retriveRecords();
		dbService.closeConnection();
	}
	
	public int updateEmployeeRecords (Employee employee, int key) {
		int code = employeeService.updateRecords(employee, key);
		dbService.closeConnection();
		return code;
	}
	
	public int deleteEmployeeRecords (int ssn) {
		int code = employeeService.deleteRecords(ssn);
		dbService.closeConnection();
		return code;
	}
}
