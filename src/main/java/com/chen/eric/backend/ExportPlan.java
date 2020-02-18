package com.chen.eric.backend;

import java.sql.Date;

public class ExportPlan extends TransPlan {
	private Integer containerID;
	private Integer loadTo;
	private Double totalCost;
	private boolean containerRetrived;
	private boolean loadComplete;
	private boolean servicePayed;
	
	public ExportPlan() {
		super();
	}
	
	public ExportPlan(int planID, String manager, Date date, 
			String status, String type,
			int containerID, int loadTo, double totalCost,
			boolean containerRetrived, 
			boolean loadComplete, boolean servicePayed) {
		super(planID, manager, date, status, type);
		this.containerID = containerID;
		this.loadTo = loadTo;
		this.containerRetrived = containerRetrived;
		this.loadComplete = loadComplete;
		this.servicePayed = servicePayed;
	}

	public Integer getContainerID() {
		return containerID;
	}

	public void setContainerID(Integer containerID) {
		this.containerID = containerID;
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

	public boolean isContainerRetrived() {
		return containerRetrived;
	}

	public void setContainerRetrived(boolean containerRetrived) {
		this.containerRetrived = containerRetrived;
	}

	public boolean isLoadComplete() {
		return loadComplete;
	}

	public void setLoadComplete(boolean loadComplete) {
		this.loadComplete = loadComplete;
	}

	public boolean isServicePayed() {
		return servicePayed;
	}

	public void setServicePayed(boolean servicePayed) {
		this.servicePayed = servicePayed;
	}
}
