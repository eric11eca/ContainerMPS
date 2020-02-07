package com.chen.eric.backend;

public class StorageMaintainer {
	private Integer StorageID;
	private Integer MaintainerID;
	
	public StorageMaintainer() {
		
	}
	
	public StorageMaintainer(Integer StorageID, Integer MaintainerID) {
		this.StorageID = StorageID;
		this.MaintainerID = MaintainerID;
	}
	
	public Integer getStorageID() {
		return this.StorageID;
	}
	
	public void setStorageID(Integer NewStorageID) {
		this.StorageID = NewStorageID;
		
	}
	
	public Integer getMaintainerID() {
		return this.MaintainerID;
	}
	
	public void setMaintainerID(Integer MaintainerID) {
		this.MaintainerID = MaintainerID;
	}
	
}
