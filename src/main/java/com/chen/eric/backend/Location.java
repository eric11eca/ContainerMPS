package com.chen.eric.backend;

import java.sql.Date;

public class Location {
	private Integer containerID;
	private Integer storageID;
	private Integer blockIndex;
	private Integer bayIndex;
	private Integer tierIndex;
	private Integer rowIndex;
	private Date startDate;
	private Date endDate;
	
	public Location() {}
	
	public Location(Integer containerID, Integer storageID, Integer blockIndex, 
			Integer tierIndex, Integer bayIndex, Integer rowIndex, Date startDate, Date endDate) {
		this.containerID = containerID;
		this.storageID = storageID;
		this.blockIndex = blockIndex;
		this.bayIndex = bayIndex;
		this.tierIndex = tierIndex;
		this.rowIndex = rowIndex;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Integer getContainerID() {
		return containerID;
	}

	public void setContainerID(Integer containerID) {
		this.containerID = containerID;
	}

	public Integer getStorageID() {
		return storageID;
	}

	public void setStorageID(Integer storageID) {
		this.storageID = storageID;
	}

	public Integer getBlockIndex() {
		return blockIndex;
	}

	public void setBlockIndex(int blockIndex) {
		this.blockIndex = blockIndex;
	}

	public Integer getBayIndex() {
		return bayIndex;
	}

	public void setBayIndex(int bayIndex) {
		this.bayIndex = bayIndex;
	}

	public Integer getTierIndex() {
		return tierIndex;
	}

	public void setTierIndex(int tierIndex) {
		this.tierIndex = tierIndex;
	}

	public Integer getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int slotIndex) {
		this.rowIndex = slotIndex;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void copyIndices(Location loc) {
		this.bayIndex = loc.bayIndex;
		this.blockIndex = loc.blockIndex;
		this.storageID = loc.storageID;
		this.tierIndex = loc.tierIndex;
		this.rowIndex = loc.rowIndex;
	}
}
