package com.chen.eric.backend;

public enum Role {
	SuperManager("Super Manager"),
	HumanResource("Human Resource");
	
	private String role; 
	  
    public String getRole() { 
        return this.role; 
    } 

    private Role(String role) { 
        this.role = role; 
    } 
}
