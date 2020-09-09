package com.howie.crm.workbench.domain;

public class ClueActivityRelation {
	
	private String id;
	private String clueId;
	private String activityId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClueId() {
		return clueId;
	}
	public void setClueId(String clueId) {
		this.clueId = clueId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public ClueActivityRelation(String id, String clueId, String activityId) {
		this.id = id;
		this.clueId = clueId;
		this.activityId = activityId;
	}
}
