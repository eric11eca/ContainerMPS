package com.chen.eric.backend;

import java.util.ArrayList;


@SuppressWarnings("serial")
public class Tier extends ArrayList<Bay>{
	public Tier() {};
	
	public void addBay(Bay bay) {
		this.add(bay);
	}
	
	public Bay getBay(int index) {
		return this.get(index);
	}
}
