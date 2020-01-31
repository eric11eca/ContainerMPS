package com.chen.eric.backend;

import java.sql.Date;

public class Vessel {
	private Integer vesselID;
	private Integer capacity;
	private String departedFromCountry;
	private String departedFromState;
	private String departedFromCity;
	private String destinationCountry;
	private String destinationState;
	private String destinationCity;
	private Date departDate;
	private Date arivalDate;
	
	public Vessel(Integer vesselID, Integer capacity, String departedFromCountry,
			String departedFromState, String departedFromCity, 
			String destinationCountry, String destinationState, 
			String destinationCity, Date departDate, Date arrivalDate) {
		this.vesselID = vesselID;
		this.capacity = capacity;
		this.departedFromCountry = departedFromCountry;
		this.departedFromState = departedFromState;
		this.departedFromCity = departedFromCity;
		this.destinationCountry = destinationCountry;
		this.destinationState = destinationState;
		this.destinationCity = destinationCity;
		this.departDate = departDate;
		this.arivalDate = arrivalDate;
	}

	public Vessel() {
		
	}

	public Integer getVesselID() {
		return vesselID;
	}
	
	public void setVesselID(Integer vesselID) {
		this.vesselID = vesselID;
	}
	
	public Integer getCapacity() {
		return capacity;
	}
	
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getDepartedFromCountry() {
		return departedFromCountry;
	}

	public void setDepartedFromCountry(String departedFromCountry) {
		this.departedFromCountry = departedFromCountry;
	}

	public String getDepartedFromState() {
		return departedFromState;
	}

	public void setDepartedFromState(String departedFromState) {
		this.departedFromState = departedFromState;
	}

	public String getDepartedFromCity() {
		return departedFromCity;
	}

	public void setDepartedFromCity(String departedFromCity) {
		this.departedFromCity = departedFromCity;
	}

	public String getDestinationCountry() {
		return destinationCountry;
	}

	public void setDestinationCountry(String destinationCountry) {
		this.destinationCountry = destinationCountry;
	}

	public String getDestinationState() {
		return destinationState;
	}

	public void setDestinationState(String destinationState) {
		this.destinationState = destinationState;
	}

	public String getDestinationCity() {
		return destinationCity;
	}

	public void setDestinationCity(String destinationCity) {
		this.destinationCity = destinationCity;
	}

	public Date getDepartDate() {
		return departDate;
	}

	public void setDepartDate(Date departDate) {
		this.departDate = departDate;
	}

	public Date getArivalDate() {
		return arivalDate;
	}

	public void setArivalDate(Date arivalDate) {
		this.arivalDate = arivalDate;
	}
}
