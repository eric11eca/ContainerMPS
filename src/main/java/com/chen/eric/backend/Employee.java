package com.chen.eric.backend;

public class Employee {
	private int SSN;
	private boolean enabled;
	private String name;
	private String role;
	private String userName;
	private String password;
	
	public int key;
	
	public int getSSN() {
		return SSN;
	}

	public void setSSN(int SSN) {
		this.SSN = SSN;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnable(boolean enabled) {
		this.enabled = enabled;
	}

	public Employee(int SSN, String name, String role, String userName, boolean enabled) {
		this.SSN = SSN;
		this.name = name;
		this.role = role;
		this.userName = userName;
		this.enabled = enabled;
	}
	
	public Employee() {}
}
