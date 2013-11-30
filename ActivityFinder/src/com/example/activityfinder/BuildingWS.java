package com.example.activityfinder;

//Template for JSON building objects 
public class BuildingWS {
	public String location;
	public double xcoor;
	public double ycoor;
	public double distance;
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public double getXcoor() {
		return xcoor;
	}
	public void setXcoor(double xcoor) {
		this.xcoor = xcoor;
	}
	public double getYcoor() {
		return ycoor;
	}
	public void setYcoor(double ycoor) {
		this.ycoor = ycoor;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
}
