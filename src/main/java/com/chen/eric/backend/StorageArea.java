package com.chen.eric.backend;

import java.util.ArrayList;
import java.util.List;

public class StorageArea {
	
	private Integer storageID;
	private String type;
	private Integer capacity;
	private Double storagePrice;
	private List<Block> storage;
	
	public StorageArea() {}
	
	public StorageArea(Integer storageID, String type, Integer capacity, Double storagePrice) {
		this.storageID = storageID;
		this.type=  type;
		this.capacity = capacity;
		this.storagePrice = storagePrice;
		this.storage = new ArrayList<>();
	}
	
	public List<Block> getStorage() {
		return storage;
	}
	
	public void setStorage(List<Block> storage) {
		this.storage = storage;
	}
	
	public void addBlock(int index, Block blok) {
		this.storage.add(index, blok);
	}
	
	public void setBlock(int index, Block blok) {
		this.storage.set(index, blok);
	}
	
	public Block getBlock(int index) {
		return storage.get(index);
	}

	public Integer getStorageID() {
		return storageID;
	}

	public void setStorageID(Integer storageID) {
		this.storageID = storageID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Double getStoragePrice() {
		return storagePrice;
	}

	public void setStoragePrice(Double storagePrice) {
		this.storagePrice = storagePrice;
	}

}
