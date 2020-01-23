package com.chen.eric.backend;

public class Employee {
	private int SSN;
	private String name;
	private String role;
	
	public int getSSN() {
		return SSN;
	}

	public void setSSN(int SSN) {
		this.SSN = SSN;
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

	public Employee(int SSN, String name, String role) {
		this.SSN = SSN;
		this.name = name;
		this.role = role;
	}
}
