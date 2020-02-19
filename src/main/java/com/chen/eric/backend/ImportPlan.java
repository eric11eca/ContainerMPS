package com.chen.eric.backend;

import java.sql.Date;

public class ImportPlan extends TransPlan {
	private Integer containerID;
	private Date arrivalDate;
	private Integer unloadFrom;
	private boolean unLoadCompleted;
	private boolean customPassed;
	private boolean containerDistributed;
	
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

	public Integer getUnloadFrom() {
		return unloadFrom;
	}

	public void setUnloadFrom(Integer unloadFrom) {
		this.unloadFrom = unloadFrom;
	}
	
	public boolean isUnLoadCompleted() {
		return unLoadCompleted;
	}

	public void setUnLoadCompleted(boolean unLoadCompleted) {
		this.unLoadCompleted = unLoadCompleted;
	}

	public boolean isCustomPassed() {
		return customPassed;
	}

	public void setCustomPassed(boolean customPassed) {
		this.customPassed = customPassed;
	}

	public boolean isContainerDistributed() {
		return containerDistributed;
	}

	public void setContainerDistributed(boolean containerDistributed) {
		this.containerDistributed = containerDistributed;
	}

	public ImportPlan(int planID, String manager, Date date, String status, String type,
			Integer containerID, Integer unloadFrom, boolean unLoadCompleted, 
			boolean customPassed, boolean containerDistributed) {
		super(planID, containerID, manager, date, status, type);
		this.containerID = containerID;
		this.unloadFrom = unloadFrom;
		this.unLoadCompleted = unLoadCompleted;
		this.customPassed = customPassed;
		this.containerDistributed = containerDistributed;
	}
}
