package com.example.activityfinder;

//Template for JSON output when authenticating
public class SignInMessage {
	public String exists;
	public String success;
	
	public String toString() {
		return exists + ":" + success;
	}
	public String getExists() {
		return exists;
	}
	public void setExists(String exists) {
		this.exists = exists;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
}
