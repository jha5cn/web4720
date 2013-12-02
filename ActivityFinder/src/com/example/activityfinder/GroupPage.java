package com.example.activityfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.maps.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GroupPage extends Activity {
	
	String webserviceURL = "http://composite-fire-350.appspot.com/activity";
	TextView messageDisplay;
	ArrayList<Message> activityMessages = new ArrayList<Message>();
	String activityName;
	String getMessagesURL = webserviceURL;
	String getUsersURL = "http://plato.cs.virginia.edu/~cs4720f13beet/groups/";
	public final static String SCHEDULED_ACTIVITY_NAME_MESSAGE = "com.example.activityfinder.MESSAGE";
	ArrayList<String> otherUsers = new ArrayList<String>();
	String locationName;
	double locationX;
	double locationY;
	ArrayList<String> locationInfo = new ArrayList<String>();
	String locationURL = "http://plato.cs.virginia.edu/~cs4720f13beet/find_closest_building_group/";
	String userName;
	String pass;
	String defaultSubmitMessageURL = "http://composite-fire-350.appspot.com/activity/";
	String submitMessageURL;
	String closestParkingURL = "http://plato.cs.virginia.edu/~cs4720f13turnip/parking/lots/";
	String closestParking;
	String groupID;
	String joinGroupDefaultURL = "http://plato.cs.virginia.edu/~cs4720f13beet/groups/"; 
	String joinGroupURL;
	JsonObject joinStatus;
	String checkGroupsURL = "http://plato.cs.virginia.edu/~cs4720f13beet/users/";
	ArrayList<UserGroups> groups = new ArrayList<UserGroups>();
	boolean alreadyInGroup = false;
	String removeDefaultURL = "http://plato.cs.virginia.edu/~cs4720f13beet/users/removefromgroup/";
	String removeURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_page);
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		if (b != null) {
			activityName = (String) b.get("Activity Name");
			userName = (String) b.get("Username");
			pass = (String) b.get("Password");
			groupID = (String) b.get("Group id");
		}
		System.out.println("GROUPID: " + groupID);
		locationURL += groupID;
		//new GetAllLocations().execute(allLocationsURL);
		//GETS DEFAULT LOCATION CLOSEST TO ALL USERS
		new GetLocation().execute(locationURL);
				
//		activityName = intent.getStringExtra(MainActivity.ACTIVITY_NAME_MESSAGE);
//		String locationName = intent.getStringExtra("Location Name");
//		System.out.println(locationName);
//		String locationX = intent.getStringExtra("Location X");
//		System.out.println(locationX);
//		String locationY = intent.getStringExtra("Location Y");
//		System.out.println(activityName);
		TextView name = (TextView) findViewById(R.id.textView99);
		name.setText(activityName.toUpperCase());
		getMessagesURL += "/" + activityName + "/message";
		new GetAllMessages().execute(getMessagesURL);
		getUsersURL += groupID + "/users";
		new GetAllUsers().execute(getUsersURL);
		checkGroupsURL += userName + "/groups";
		new GetGroups().execute(checkGroupsURL);
		
		System.out.println("Location info: " + locationName + locationX + locationY);		
	}
	
	//Method that gets all groups
	private class GetGroups extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			
			System.out.println(" doinbackground url: " + url);
						
			try {
				String webJSON = getJSONfromURL(url);
				
				Gson gson = new Gson();
				
				JsonParser parser = new JsonParser();
				System.out.println("This works");
				System.out.println(webJSON);
				JsonArray Jarray = parser.parse(webJSON).getAsJsonArray();
				System.out.println("Jarray: " + Jarray.toString()); 
				//result.add(webJSON);
				//return ("done");
				/*String elt = gson.fromJson(Jarray, String.class);
				result.add(elt); */
				
				for (JsonElement obj : Jarray) {
					System.out.println(obj.toString());
					UserGroups elt = gson.fromJson(obj, UserGroups.class);
					groups.add(elt);
					//System.out.println("elt: " + elt);
				} 
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 

			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Button b = (Button) findViewById(R.id.buttonScheduleActivity);
			for (UserGroups g : groups) {
				if (g.getGroup_id().toString().equals(groupID)) {
					alreadyInGroup = true;
				}
			}
			if (alreadyInGroup == true) {
				b.setText("Remove from Group");
			}
			else {
				b.setText("Join Group");
			}
		}
	}
	
	//FOOTER
	public void Home(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("Username", userName);
		intent.putExtra("Password", pass);
		startActivity(intent);
	}
	
	public void MyActivities(View view) {
		Intent intent = new Intent(this, MyActivities.class);
		intent.putExtra("Username", userName);
		intent.putExtra("Password", pass);
		startActivity(intent);
	}
	
	//Gets closest location to all signedup users
	private class GetLocation extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			
			System.out.println(" doinbackground url: " + url);
						
			try {
				String webJSON = getJSONfromURL(url);
				
				Gson gson = new Gson();
				
				JsonParser parser = new JsonParser();
				System.out.println("This works");
				System.out.println(webJSON);
				JsonArray Jarray = parser.parse(webJSON).getAsJsonArray();
				System.out.println("Jarray: " + Jarray.toString()); 
				//result.add(webJSON);
				//return ("done");
				/*String elt = gson.fromJson(Jarray, String.class);
				result.add(elt); */
				
				for (JsonElement obj : Jarray) {
					System.out.println(obj.toString());
					String elt = gson.fromJson(obj, String.class);
					//System.out.println("elt: " + elt);
					locationInfo.add(elt);  
				} 
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 

			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			locationName = locationInfo.get(locationInfo.size()-3);
			locationX = Float.parseFloat(locationInfo.get(locationInfo.size()-2));
			locationY = Float.parseFloat(locationInfo.get(locationInfo.size()-1));
			
			GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			LatLng baseAddress = new LatLng(locationX, locationY);
			map.setMyLocationEnabled(true);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(baseAddress, 17));
			map.addMarker(new MarkerOptions()
					.title(locationName)
					.snippet("Closest building to all registered users")
					.position(baseAddress));
			closestParkingURL += "?lat=" + locationX + "&lon=" + locationY + "&num=1";
			System.out.println(closestParkingURL);
			new ClosestParking().execute(closestParkingURL);
		}
	}
	
	//Joins the user to that group
	public void scheduleActivity(View view) {
		Button b = (Button) findViewById(R.id.buttonScheduleActivity);
		if (b.getText().toString().equals("Remove from Group")) {
			removeURL = removeDefaultURL;
			removeURL += userName + "/" + pass + "/" + groupID;
			new Remove().execute(removeURL);
		}
		else {
			joinGroupURL = joinGroupDefaultURL;
			joinGroupURL += groupID + "/add/" + userName;
			new JoinGroup().execute(joinGroupURL);
		}
	}
	
	//Actual method that joins the given group
	private class Remove extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			
			System.out.println(" doinbackground url: " + url);
						
			try {
				String webJSON = getJSONfromURL(url);
				
				JsonParser parser = new JsonParser();

				joinStatus = parser.parse(webJSON).getAsJsonObject();
				
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Button joinButton = (Button) findViewById(R.id.buttonScheduleActivity);
			if (joinStatus.get("success").toString().equals("true")) {
				joinButton.setText("Removed");
				joinButton.setClickable(false);
			}
			else {
				joinButton.setText("Error removing from group");
			}
			removeURL = removeDefaultURL;
		}
	}
	
	//Submits message to Google App Engine
	public void submitMessage(View view) {
		submitMessageURL = defaultSubmitMessageURL;
		submitMessageURL += activityName + "/message/" + userName + "/" + pass + "/";
		EditText submitText = (EditText) findViewById(R.id.editTextAddMessage);
		String message = submitText.getText().toString();
		message = message.replaceAll(" ", "%20");
		submitMessageURL += message;
		System.out.println("URL:::::: " + submitMessageURL);
		new SubmitMessage().execute(submitMessageURL);
	}
	
	//Actual method that joins the given group
	private class JoinGroup extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			
			System.out.println(" doinbackground url: " + url);
						
			try {
				String webJSON = getJSONfromURL(url);
				
				JsonParser parser = new JsonParser();

				joinStatus = parser.parse(webJSON).getAsJsonObject();
				
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Button joinButton = (Button) findViewById(R.id.buttonScheduleActivity);
			if (joinStatus.get("success").toString().equals("true")) {
				joinButton.setText("Joined");
				joinButton.setClickable(false);
			}
			else {
				joinButton.setText("Already Joined");
			}
			joinGroupURL = joinGroupDefaultURL;
		}
	}
	
	//Third Party web service, informs user what the closest parking spot to our suggested location is
	private class ClosestParking extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			
			System.out.println(" doinbackground url: " + url);
						
			try {
				String webJSON = getJSONfromURL(url);

				String array[] = webJSON.split(":");
				closestParking = array[0].substring(1);
//				System.out.println(webJSON);
//				JsonArray JArray = parser.parse(webJSON).getAsJsonArray();
//				//JObject.toString();
//				System.out.println("JARRAY IS NEXT");
//				System.out.println("JSONARRAY: " + JArray.get(0));
				
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			messageDisplay = (TextView) findViewById(R.id.textViewTurnip);
			messageDisplay.append(closestParking);
		}
	}

	//Actual method that submits message
	private class SubmitMessage extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			
			System.out.println(" doinbackground url: " + url);
						
			try {
				String webJSON = getJSONfromURL(url);
				
				Gson gson = new Gson();
				
				JsonParser parser = new JsonParser();
				System.out.println("This works");
				System.out.println(webJSON);
				JsonArray Jarray = parser.parse(webJSON).getAsJsonArray();
				System.out.println("Jarray: " + Jarray.toString()); 
				//result.add(webJSON);
				//return ("done");
				/*String elt = gson.fromJson(Jarray, String.class);
				result.add(elt); */
				
				for (JsonElement obj : Jarray) {
					System.out.println(obj.toString());
					//System.out.println("elt: " + elt);
				} 
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 

			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			messageDisplay = (TextView) findViewById(R.id.UsersDisplay);
			for (String s : otherUsers) {
				messageDisplay.append(s + ", ");
			}
		}
	}
	
	//Gets all users signed up for that activity
	private class GetAllUsers extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			
			System.out.println(" doinbackground url: " + url);
						
			try {
				String webJSON = getJSONfromURL(url);
				
				Gson gson = new Gson();
				
				JsonParser parser = new JsonParser();
				System.out.println("This works");
				System.out.println(webJSON);
				JsonArray Jarray = parser.parse(webJSON).getAsJsonArray();
				System.out.println("Jarray: " + Jarray.toString()); 
				//result.add(webJSON);
				//return ("done");
				/*String elt = gson.fromJson(Jarray, String.class);
				result.add(elt); */
				
				for (JsonElement obj : Jarray) {
					System.out.println(obj.toString());
					String elt = gson.fromJson(obj, String.class);
					//System.out.println("elt: " + elt);
					otherUsers.add(elt);  
				} 
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 

			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			messageDisplay = (TextView) findViewById(R.id.UsersDisplay);
			for (String s : otherUsers) {
				messageDisplay.append(s + ", ");
			}
		}
	}
	
	//Actual method that gets all GAE messages
	private class GetAllMessages extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			
			System.out.println(" doinbackground url: " + url);
						
			try {
				String webJSON = getJSONfromURL(url);
				
				Gson gson = new Gson();
				
				JsonParser parser = new JsonParser();
				System.out.println("This works");
				System.out.println(webJSON);
				JsonArray Jarray = parser.parse(webJSON).getAsJsonArray();
				System.out.println("Jarray: " + Jarray.toString()); 
				//result.add(webJSON);
				//return ("done");
				/*String elt = gson.fromJson(Jarray, String.class);
				result.add(elt); */
				
				for (JsonElement obj : Jarray) {
					System.out.println(obj.toString());
					Message elt = gson.fromJson(obj, Message.class);
					//System.out.println("elt: " + elt);
					activityMessages.add(elt);  
				} 
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 
			
			System.out.println(activityMessages);
//			webResponse = result.get(result.size()-1);
//			System.out.println(webResponse);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			messageDisplay = (TextView) findViewById(R.id.textViewMessageDisplay);
			Collections.sort(activityMessages);
			Collections.reverse(activityMessages);
			for (Message m : activityMessages) {
				messageDisplay.append(m.toString());
			}
			}
		}
		
	//Reads JSON
	public static String getJSONfromURL(String url) {
		InputStream is = null;
		String JSONresult = "";
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e){
			
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			JSONresult = sb.toString();
		} catch (Exception e) {
			Log.e("ActivityFinder", "Error converting result " + e.toString());
		}

		return JSONresult;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_page, menu);
		return true;
	}

} 
