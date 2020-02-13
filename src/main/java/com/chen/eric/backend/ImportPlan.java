package com.chen.eric.backend;

import java.sql.Date;

public class ImportPlan extends TransPlan {
	private Integer containerID;
	private Date arrivalDate;
	private String departedFromCountry;
	private String departedFromState;
	private String departedFromCity;
	private Integer unloadFrom;
	
	public ImportPlan() {
		super();
	}

	public Integer getContainerID() {
		return containerID;
	}

	public void setContainerID(Integer containerID) {
		this.containerID = containerID;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
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

	public Integer getUnloadFrom() {
		return unloadFrom;
	}

	public void setUnloadFrom(Integer unloadFrom) {
		this.unloadFrom = unloadFrom;
	}

	public ImportPlan(int planID, String name, Date date, String status, String type,
			Integer containerID, Date arrivalDate, 
			String departedFromCountry, String departedFromState,
			String departedFromCity, Integer unloadFrom) {
		super(planID, name, date, status, type);
		this.containerID = containerID;
		this.arrivalDate = arrivalDate;
		this.departedFromCountry = departedFromCountry;
		this.departedFromState = departedFromState;
		this.departedFromCity = departedFromCity;
		this.unloadFrom = unloadFrom;
	}
}
