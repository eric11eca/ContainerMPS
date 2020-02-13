package com.chen.eric.backend;

import java.sql.Date;

public class ExportPlan extends TransPlan {
	private Integer containerID;
	private Date departureDate;
	private String destinationCountry;
	private String destinationState;
	private String destinationCity;
	private Integer loadTo;
	private Double totalCost;
	private boolean retrived;
	private boolean loaded;
	private boolean payed;
	
	
	public ExportPlan() {
		super();
	}
	
	public ExportPlan(int planID, String manager, Date date, String status, String type,
			int containerID, Date departureDate, double totalCost,
			String destinationCountry, String destinationState,
			String destinationCity, int loadTo) {
		super(planID, manager, date, status, type);
		this.containerID = containerID;
		this.departureDate = departureDate;
		this.destinationCountry = destinationCountry;
		this.destinationState = destinationState;
		this.destinationCity = destinationCity;
		this.loadTo = loadTo;
		this.retrived = false;
		this.loaded = false;
		this.payed = false;
	}

	public Integer getContainerID() {
		return containerID;
	}

	public void setContainerID(Integer containerID) {
		this.containerID = containerID;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
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

	public Integer getLoadTo() {
		return loadTo;
	}

	public void setLoadTo(Integer loadTo) {
		this.loadTo = loadTo;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public boolean isRetrived() {
		return retrived;
	}

	public void setRetrived(boolean retrived) {
		this.retrived = retrived;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isPayed() {
		return payed;
	}

	public void setPayed(boolean payed) {
		this.payed = payed;
	}

}
