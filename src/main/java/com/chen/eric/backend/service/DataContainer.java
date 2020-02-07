package com.chen.eric.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;

import com.chen.eric.backend.Container;
import com.chen.eric.backend.Customer;
import com.chen.eric.backend.Employee;
import com.chen.eric.backend.Location;
import com.chen.eric.backend.StorageArea;
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
	
	private EntityService<Container> containerService = new ContainerService(dbService);
	public Map<String, Container> containerRecords = new HashMap<>();
	
	
	public void getContainerRecords() {
		containerRecords = containerService.retriveRecords();
		dbService.closeConnection();
	}
	
	public void getContainerRecordsByParams(String filter, String value) {
		containerRecords = containerService.retriveRecordsByParameters(filter, value);
		dbService.closeConnection();
	}
	
	public int insertContainerRecords (Container contianer) {
		int code = containerService.insertRecords(contianer);
		dbService.closeConnection();
		return code;
	}
	
	public int updateContainerRecords (Container contianer, int key) {
		int code = containerService.updateRecords(contianer, key);
		dbService.closeConnection();
		return code;
	}
	
	public int deleteContainerRecords (int contianerID) {
		int code = containerService.deleteRecords(contianerID);
		dbService.closeConnection();
		return code;
	}
	
	public Map<String, Customer> customerRecords;
	private EntityService<Customer>customerService = new CustomerService(dbService);

	public int insertCustomerRecords(Customer customer) {
		int code = customerService.insertRecords(customer);
		dbService.closeConnection();
		return code;
	}

	public void getCustomerRecords() {
		customerRecords = customerService.retriveRecords();
		dbService.closeConnection();
	}
	
	public void getCustomerRecordsByParams(String filter, String value) {
		customerRecords = customerService.retriveRecordsByParameters(filter, value);
		dbService.closeConnection();
	}

	public int updateCustomerRecords(Customer cutomer, Integer customerID) {
		int code = customerService.updateRecords(cutomer, customerID);
		dbService.closeConnection();
		return code;
	}
	
	public int deleteCustomerRecords(Integer ID) {
		int code = customerService.deleteRecords(ID);
		dbService.closeConnection();
		return code;
	}
	
	public Map<String, StorageArea> storageAreaRecords;
	private EntityService<StorageArea>storageService = new StorageAreaService(dbService);
	
	public int insertStorageAreaRecords(StorageArea storageArea) {
		int code = storageService.insertRecords(storageArea);
		dbService.closeConnection();
		return code;
	}

	public void getStorageAreaRecords() {
		storageAreaRecords = storageService.retriveRecords();
		dbService.closeConnection();
	}
	
	public void getStorageAreaRecordsByParams(String filter, String value) {
		storageAreaRecords = storageService.retriveRecordsByParameters(filter, value);
		dbService.closeConnection();
	}

	public int updateStorageAreaRecords(StorageArea storageArea, Integer customerID) {
		int code = storageService.updateRecords(storageArea, customerID);
		dbService.closeConnection();
		return code;
	}
	
	public int deleteStorageAreaRecords(Integer ID) {
		int code = storageService.deleteRecords(ID);
		dbService.closeConnection();
		return code;
	}
	
	public Map<String, Location> locationRecords;
	private EntityService<Location>locationService = new LocationService(dbService);
	
	public int insertLocationRecords(Location location) {
		int code = locationService.insertRecords(location);
		dbService.closeConnection();
		return code;
	}

	public void getLocationRecords() {
		locationRecords = locationService.retriveRecords();
		dbService.closeConnection();
	}
	
	public void getLocationRecordsByParams(String filter, String value) {
		locationRecords = locationService.retriveRecordsByParameters(filter, value);
		dbService.closeConnection();
	}

	public int updateLocationRecords(Location location, Integer containerID) {
		int code = locationService.updateRecords(location, containerID);
		dbService.closeConnection();
		return code;
	}
	
	public int deleteLocationRecords(Integer ID) {
		int code = locationService.deleteRecords(ID);
		dbService.closeConnection();
		return code;
	}
	
	private StorageLayoutFactory storageAreaFactory = new StorageLayoutFactory(dbService);
	
	public void initStorageAreas() {
		for (StorageArea area : storageAreaRecords.values()) {
			StorageArea newArea = storageAreaFactory.generateStorageArea(area);
			storageAreaRecords.put(
					String.valueOf(newArea.getStorageID()), newArea);
		}
	}
}
