package com.chen.eric.backend;

import java.util.ArrayList;


@SuppressWarnings("serial")
public class Block extends ArrayList<Tier>{
	public Block() {};
	
	public void addTire(Tier tire) {
		this.add(tire);
	}
	
	public Tier getTirey(int index) {
		return this.get(index);
	}
}
