package com.chen.eric.backend;

import java.sql.Date;

public class Location {
	private Integer containerID;
	private Integer storageID;
	private int blockIndex;
	private int bayIndex;
	private int tireIndex;
	private int rowIndex;
	private Date startDate;
	private Date endDate;
	
	public Location() {}
	
	public Location(Integer containerID, Integer storageID, int blockIndex, 
				int bayIndex, int tireIndex, int rowIndex, Date startDate, Date endDate) {
		this.containerID = containerID;
		this.storageID = storageID;
		this.blockIndex = blockIndex;
		this.bayIndex = bayIndex;
		this.tireIndex = tireIndex;
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

	public int getBlockIndex() {
		return blockIndex;
	}

	public void setBlockIndex(int blockIndex) {
		this.blockIndex = blockIndex;
	}

	public int getBayIndex() {
		return bayIndex;
	}

	public void setBayIndex(int bayIndex) {
		this.bayIndex = bayIndex;
	}

	public int getTireIndex() {
		return tireIndex;
	}

	public void setTierIndex(int tireIndex) {
		this.tireIndex = tireIndex;
	}

	public int getRowIndex() {
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
		this.tireIndex = loc.tireIndex;
		this.rowIndex = loc.rowIndex;
	}
}
