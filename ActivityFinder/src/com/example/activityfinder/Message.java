package com.example.activityfinder;

//Template class for JSON message output objects; ordered by date
public class Message implements Comparable {
	public String date;
	public String username;
	public String message;
	
	public String toString() {
		return "Date: " + date + "\nUsername: " + username + "\nMessage: " + message + "\n-----------------------\n";   
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		Message arg = (Message) arg0;
		return this.getDate().compareTo(arg.getDate());
	}
}
