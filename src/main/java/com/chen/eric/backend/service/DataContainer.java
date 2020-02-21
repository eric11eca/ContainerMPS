package com.chen.eric.backend.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;

import com.chen.eric.backend.Container;
import com.chen.eric.backend.Customer;
import com.chen.eric.backend.Employee;
import com.chen.eric.backend.ExportPlan;
import com.chen.eric.backend.ImportPlan;
import com.chen.eric.backend.Location;
import com.chen.eric.backend.StorageArea;
import com.chen.eric.backend.TransPlan;
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
	
	public int countVessel() {
		VesselService vesselService = new VesselService(dbService);
		return vesselService.getVesselCount();
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
	
	public void getEmployeeRecordsByParams(String filter, String value) {
		employeeRecords = employeeService.retriveRecordsByParameters(filter, value);
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
	
	public int countContainerInArea(String type) {
		LocationService locService = new LocationService(dbService);
		int code =  locService.countContainerInArea(type);
		dbService.closeConnection();
		return code;
	}
	
	private StorageLayoutFactory storageAreaFactory = new StorageLayoutFactory(dbService);
	
	public void initStorageArea(String storageID) {
		StorageArea area = storageAreaRecords.get(storageID);
		StorageArea newArea = storageAreaFactory.generateStorageArea(area);
		storageAreaRecords.put(storageID, newArea);
	}
	
	public Map<String, TransPlan> transPlanRecords = new HashMap<>();
	public Map<String, ImportPlan> importPlanRecords = new HashMap<>();
	public Map<String, ExportPlan> exportPlanRecords = new HashMap<>();
	
	private PlanService planService = new PlanService(dbService);
	
	public void getPlanRecords() {
		transPlanRecords = planService.retriveRecords();
		importPlanRecords = planService.getImportPlans();
		exportPlanRecords = planService.getExportPlans();
		dbService.closeConnection();
	}
	
	public void getPlanRecordsByParams(String filter, String value) {
		transPlanRecords = planService.retriveRecordsByParameters(filter, value);			
		exportPlanRecords = planService.getExportPlans();
		importPlanRecords = planService.getImportPlans();
	}
	
	public int insertPlanRecords(TransPlan plan) {
		int code = planService.insertRecords(plan);
		dbService.closeConnection();
		return code;
	}
	public int deletePlanRecords(Integer planID) {
		int code = planService.deleteRecords(planID);
		dbService.closeConnection();
		return code;
	}
	
	public int updateUnLoadFromVessel(int unloadFrom, int primary, int secondary) {
		int code = planService.updateImportVessel(unloadFrom, primary, secondary);
		dbService.closeConnection();
		return code;
	}
	
	public int updateUnLoadCompleted(boolean unloaded, int primary, int secondary) {
		int code = planService.updateImportUnload(unloaded, primary, secondary);
		dbService.closeConnection();
		return code;
	}
	
	public int updateCustomPassed(boolean passed, int primary, int secondary) {
		int code = planService.updateImportCustom(passed, primary, secondary);
		dbService.closeConnection();
		return code;
	}
	
	public int updateContainerDistributed(boolean distributed, int primary, int secondary) {
		int code = planService.updateImportDistribute(distributed, primary, secondary);
		dbService.closeConnection();
		return code;
	}
	
	public int updateLoadToVessel(int loadTo, int primary, int secondary) {
		int code = planService.updateExportVessel(loadTo, primary, secondary);
		dbService.closeConnection();
		return code;
	}
	
	public int updateExportTotalCost(Double totalCost, int primary, int secondary) {
		int code = planService.updateExportTotalCost(totalCost, primary, secondary);
		dbService.closeConnection();
		return code;
	}
	
	public int updateLoadCompleted(boolean loaded, int primary, int secondary) {
		int code = planService.updateExportLoad(loaded, primary, secondary);
		dbService.closeConnection();
		return code;
	}
	
	public int updateContainerRetrived(boolean retrived, int primary, int secondary) {
		int code = planService.updateExportRetrived(retrived, primary, secondary);
		dbService.closeConnection();
		return code;
	}
	
	public int updateServicePayed(boolean payed, int primary, int secondary) {
		int code = planService.updateExportBill(payed, primary, secondary);
		dbService.closeConnection();
		return code;
	}
	
	public int uodatePlanStatus(int planID, String status) {
		int code = planService.updateStatus(status, planID);
		dbService.closeConnection();
		return code;
	}
	
	public double calculateCost(int id) {
    	try {
    		Location loc = locationRecords.get(String.valueOf(id));
    		Period noOfDaysBetween = loc.getStartDate().toLocalDate().until(LocalDate.now());
        	int numDays = noOfDaysBetween.getDays();
        	StorageArea area = storageAreaRecords.get(String.valueOf(loc.getStorageID()));
        	double fee = area.getStoragePrice();
        	double totalCost = fee * numDays;
        	return totalCost;
    	} catch(NullPointerException e) {
    		return 0;
    	}
    	
    }
	
    public static int getRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }
    
    public static final String exportDiagram = "@startuml\n" + 
			"\n" + 
			"package Retrive {\n" + 
			"    [Locate Container\\n <<$containerID>>] as location\n" + 
			"    [Retrive Container\\n <<$containerID>>] as container\n" + 
			"}\n" + 
			"\n" + 
			"package Payment {\n" + 
			"    [Bill Container\\n <<$containerID>>\\n <<$fee>>] as bill\n" + 
			"}\n" + 
			"\n" + 
			"package Load {\n" + 
			"    [Load To Vessel\\n <<$vesselID>>] as load\n" + 
			"}\n" + 
			"\n" + 
			"\n" + 
			/*"package ExportPlan {\n" + 
			"    [Plan ID\\n <<$planID>>] as id\n" + 
			"    [Plan Manager\\n <<$manager>>] as manager\n" + 
			"    [Plan Status\\n <<$status>>] as status\n" + 
			"    [Plan Date\\n <<$date>>] as date\n" + 
			"}\n" + */
			"\n" + 
			"location -> container\n" + 
			"container --> bill\n" + 
			"bill --> load\n" + 
			"\n" + 
			/*"id-down-date\n" + 
			"date-down-manager\n" + 
			"manager-down-status\n" + 
			"\n" + */
			"@enduml";
    
    public static final String importDiagram = "@startuml\n" + 
			"\n" + 
			"package Unload {\n" + 
			"    [Select Vessel\\n <<$vesselID>>] as vessel\n" + 
			"    [Create Container\\n <<$containerID>>] as container\n" + 
			"}\n" + 
			"\n" + 
			"package Custom {\n" + 
			"    [Check Container\\n <<$containerID>>] as check\n" + 
			"}\n" + 
			"\n" + 
			"package Distribute {\n" + 
			"    [Assign Loactaion\\n <<$containerID>>] as location\n" + 
			"}\n" + 
			"\n" + 
			"node Storage {\n" + 
			"    node Container <<$containerID>> as storage\n" + 
			"}\n" + 
			"\n" + 
			/*"package Plan {\n" + 
			"    [Plan ID\\n <<$planID>>] as id\n" + 
			"    [Plan Manager\\n <<$manager>>] as manager\n" + 
			"    [Plan Status\\n <<$status>>] as status\n" + 
			"    [Plan Date\\n <<$date>>] as date\n" + 
			"}\n" + */
			"\n" + 
			"vessel -> container\n" + 
			"container --> check\n" + 
			"check --> location\n" + 
			"location --> storage\n" + 
			"\n" + 
			/*"id-down-date\n" + 
			"date-down-manager\n" + 
			"manager-down-status\n" + 
			"\n" + */
			"@enduml";

	
}
