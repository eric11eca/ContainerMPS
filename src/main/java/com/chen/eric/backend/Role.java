package com.chen.eric.backend;

import java.util.Arrays;
import java.util.List;

public class Role {
	public static final String SystemAdmin = "System Admin";
	public static final String SuperManager = "Super Manager";
	public static final String HumanResource = "Human Resource";
	public static final String CustomerCommunicator = "Customer Communicator";
    
    public static final String ExportPlanManager = "Export Plan Manager";
    public static final String ImportPlanManager = "Import Plan Manager";
    
    public static final String StorageAreaMaintainer = "Storage Area Maintainer";
    public static final String ContainerDistributor = "Container Distributor";
	
    public static final String NO_ACCES = "no_acces";

	private Role() {}
	
	public static final List<String> allowedUser = Arrays.asList(new String[] {
			SystemAdmin, ExportPlanManager, ImportPlanManager,
			StorageAreaMaintainer, ContainerDistributor, 
			SuperManager, HumanResource, CustomerCommunicator});
	
	public static String[] getAllRoles() {
		return new String[] {ContainerDistributor, StorageAreaMaintainer, SystemAdmin,
				ExportPlanManager, SuperManager, ImportPlanManager, CustomerCommunicator};
	}
}
