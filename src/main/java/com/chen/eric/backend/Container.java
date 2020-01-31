package com.chen.eric.backend;

public class Container {
	private Integer containerID; 
	private String type;
	private Double length;
	private Double width;
	private Double height; 
	private Double weight;
	private String owner;
	private Double  fee;
	private boolean payed;
	
	public Container() {}
	
	public Container(Integer containerID, String type, 
			Double length, Double width, Double height, 
			Double weight, String owner, boolean payed, Double fee) {
		this.containerID = containerID;
		this.type = type;
		this.length = length;
		this.width = width;
		this.height = height;
		this.weight = weight;
		this.owner = owner;
		this.fee = fee;
		this.payed = payed;
	}

	public Integer getContainerID() {
		return containerID;
	}

	public void setContainerID(int containerID) {
		this.containerID = containerID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public boolean isPayed() {
		return payed;
	}

	public void setPayed(boolean payed) {
		this.payed = payed;
	}
}
