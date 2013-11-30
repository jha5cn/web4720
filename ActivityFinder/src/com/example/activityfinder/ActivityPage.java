package com.example.activityfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.location.Location;

public class ActivityPage extends Activity {
	
	String activityName;
	String userName;
	BuildingWS closestBuilding;
	double latitude;
	double longitude;
	Context c = this;
	String pass;
	String locationURL = "http://plato.cs.virginia.edu/~cs4720f13beet/activities/";
	ArrayList<GPSLocation> locations = new ArrayList<GPSLocation>();
	String getGroupsURL = "http://plato.cs.virginia.edu/~cs4720f13beet/activities/";
	ArrayList<Groups> groups = new ArrayList<Groups>();
	
	/* Displays all locations, a create group option, and list of groups already created for this
	 * activity
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_page);
		
		/* Bundles pass variables between different activities; keep track of username,
		 * activityname, and password
		 */
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		if (b != null) {
			activityName = (String) b.get("Activity Name");
			userName = (String) b.get("Username");
			pass = (String) b.get("Password");
		}
		System.out.println(userName);
		
		TextView name = (TextView) findViewById(R.id.textView1);
		name.setText(activityName);
		
		//Base address for google map fragment
		
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		LatLng baseAddress = new LatLng(38.03, -78.5);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(baseAddress, 14));
		
		
		locationURL += activityName + "/locations_new";
		new GetAllLocations().execute(locationURL);
		
		getGroupsURL += activityName + "/groups";
		new GetGroups().execute(getGroupsURL);
	}
	
	
	//Defines the view group button that takes user to GroupPage
	OnClickListener groupListener = new OnClickListener() {
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(c, GroupPage.class);
		intent.putExtra("Activity Name", activityName);
		intent.putExtra("Username", userName);
		intent.putExtra("Password", pass);
		String buttonText = ((Button) view).getText().toString();
		intent.putExtra("Group id", buttonText);
		startActivity(intent);
	}
	};
	
	//Retrieves all groups linked to the given activity
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
					Groups elt = gson.fromJson(obj, Groups.class);
					//System.out.println("elt: " + elt);
					groups.add(elt);  
				} 
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 

			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {		
			TableLayout tl = (TableLayout) findViewById(R.id.tableLayoutActivityPage);
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			Collections.sort(groups);
			for (Groups g : groups) {
				TableRow tr = new TableRow(c);
				tr.setLayoutParams(lp);
				
				TextView tv1 = new TextView(c);
				tv1.setText(g.getStart_time());
				tv1.setPadding(45, 0, 0, 0);
				TextView tv2 = new TextView(c);
				for (String s : g.getUsers()) {
					tv2.append(s + ", ");
				}
				tv2.setPadding(65, 0, 0, 0);
				TextView tv3 = new TextView(c);
				tv3.setText(g.getBuilding_name());
				tv3.setPadding(120, 0, 0, 0);
				Button tv4 = new Button(c);
				tv4.setPadding(0, 0, 0, 0);
				tv4.setText(g.getGroup_id());
				tv4.setOnClickListener(groupListener);
				tr.addView(tv1);
				tr.addView(tv2);
				tr.addView(tv3);
				tr.addView(tv4);
				tl.addView(tr);
			}
		}
	}
	
	//Gets all locations associated with this activity
	private class GetAllLocations extends AsyncTask<String, Integer, String> {
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
					GPSLocation elt = gson.fromJson(obj, GPSLocation.class);
					//System.out.println("elt: " + elt);
					locations.add(elt);  
				} 
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 

			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {		
			GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			LatLng baseAddress = new LatLng(38.03379, -78.511611);
			map.setMyLocationEnabled(true);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(baseAddress, 17));
			for (GPSLocation l : locations) {
				LatLng gps = new LatLng(l.getX(), l.getY());
				map.addMarker(new MarkerOptions()
				.title(l.getBuilding())
				.position(gps));
			}
		}
	}
	
	//Retrieves JSON from URL
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
	
	//Sends info to CreateGroup Page
	public void CreateGroup(View view) {
		Intent intent = new Intent(this, CreateGroup.class);
		intent.putExtra("Username", userName);
		intent.putExtra("Password", pass);
		intent.putExtra("Activity Name", activityName);
		startActivity(intent);
	}

}
