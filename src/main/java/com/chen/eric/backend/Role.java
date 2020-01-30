package com.chen.eric.backend;

import java.util.Arrays;
import java.util.List;

public class Role {
	public static final String SystemAdmin = "System Admin";
	public static final String SuperManager = "Super Manager";
	public static final String HumanResource = "Human Resource";
    
    public static final String PlanManager = "Plan Manager";
	public static final String NO_ACCES = "no_acces";

	private Role() {}
	
	public static final List<String> allowedUser = Arrays.asList(new String[] {
			SystemAdmin, PlanManager, SuperManager, HumanResource});
	
	public static String[] getAllRoles() {
		return new String[] {PlanManager, SuperManager};
	}
}
