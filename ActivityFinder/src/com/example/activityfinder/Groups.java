package com.example.activityfinder;

//Template for JSON output of Group information; ordered by start_time
public class Groups implements Comparable {
	
	private String group_id;
	private String start_time;
	private String end_Time;
	private String building_name;
	private String recurrence;
	private String[] users;
	
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_Time() {
		return end_Time;
	}
	public void setEnd_Time(String end_Time) {
		this.end_Time = end_Time;
	}
	public String getBuilding_name() {
		return building_name;
	}
	public void setBuilding_name(String building_name) {
		this.building_name = building_name;
	}
	public String getRecurrence() {
		return recurrence;
	}
	public void setRecurrence(String recurrence) {
		this.recurrence = recurrence;
	}
	public String[] getUsers() {
		return users;
	}
	public void setUsers(String[] users) {
		this.users = users;
	}
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		
		Groups g = (Groups) arg0;
		return this.getStart_time().compareTo(g.getStart_time());
	}
}
