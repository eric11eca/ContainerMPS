package com.chen.eric.backend;

import java.sql.Date;

public abstract class TransPlan {
	public Integer planID;
	public String manager;
	public Date date;
	public String status;
	public String type;
	
	public TransPlan() {
		
	}
	
	public TransPlan(int planID, String manager, 
			Date date, String status, String type) {
		this.planID = planID;
		this.manager = manager;
		this.date = date;
		this.status = status;
		this.type = type;
	}

	public Integer getPlanID() {
		return planID;
	}

	public String getManager() {
		return manager;
	}

	public Date getDate() {
		return date;
	}

	public String getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}
}
