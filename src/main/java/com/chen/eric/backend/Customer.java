package com.chen.eric.backend;

public class Customer {
	private Integer customerID;
	private String companyName;
	private String contactEmail; 
	private String country;
	private String State;
	
	public Customer() {}
	
	public Customer(Integer customerID, String companyName, 
			String contactEmail, String country, String state) {
		this.customerID = customerID;
		this.companyName = companyName;
		this.contactEmail = contactEmail;
		this.country = country;
		this.State = state;
	}

	public Integer getCustomerID() {
		return customerID;
	}

	public void setCustomerID(Integer customerID) {
		this.customerID = customerID;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}
}
