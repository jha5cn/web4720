package com.example.activityfinder;

//Template for JSON GPSLocation Objects
public class GPSLocation {
	private String building_name;
	private double x;
	private double y;
	
	public String getBuilding() {
		return building_name;
	}
	public void setBuilding(String building) {
		this.building_name = building;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
}
