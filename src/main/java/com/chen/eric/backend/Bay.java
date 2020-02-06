package com.chen.eric.backend;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Bay extends ArrayList<Location>{	
	public Bay() {}
	
	public void addContainer(int index, Location location) {
		this.set(index, location);
	}
	
	public Location getContainer(int index) {
		return get(index);
	}
	
	public void removeContainer(int index) {
		this.set(index, new Location());
	}
}
