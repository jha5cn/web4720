package com.example.activityfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CreateGroup extends Activity {
	
	String userName;
	String pass;
	String activityName;
	String addGroupDefaultURL = "http://plato.cs.virginia.edu/~cs4720f13beet/activities/";
	String addGroupURL;
	JsonObject status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		if (b != null) {
			userName = (String) b.get("Username");
			pass = (String) b.get("Password");
			activityName = (String) b.get("Activity Name");
		}
	}
	
	//Takes the start/end times/dates and creates a group
	public void Create (View view) {
		addGroupURL = addGroupDefaultURL;
		addGroupURL += activityName + "/add/";
		EditText startDate = (EditText) findViewById(R.id.editTextStartDate);
		EditText endDate = (EditText) findViewById(R.id.editTextEndDate);
		EditText startTime = (EditText) findViewById(R.id.editTextStartTime);
		EditText endTime = (EditText) findViewById(R.id.editTextEndTime);
		EditText location = (EditText) findViewById(R.id.editTextLocation);
		addGroupURL += startDate.getText() + " " + startTime.getText() + "/" + endDate.getText()
				+ " " + endTime.getText() + "/" + location.getText();
		addGroupURL = addGroupURL.replaceAll(" ", "%20");
		System.out.println("GROUPURL" + " " + addGroupURL);
		new SubmitCreateGroup().execute(addGroupURL);
	}
	
	//Actual method that creates a group
	private class SubmitCreateGroup extends AsyncTask<String, Integer, String> {
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
				
				status = parser.parse(webJSON).getAsJsonObject();

				System.out.println(status);
				//result.add(webJSON);
				//return ("done");
				/*String elt = gson.fromJson(Jarray, String.class);
				result.add(elt); */
			} catch (Exception e) {
				System.out.println(e.getStackTrace().toString());
			} 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			TextView message = (TextView) findViewById(R.id.textViewSuccess);
			addGroupURL = addGroupDefaultURL;
			message.setText("Group Creation: " + status.get("success").toString());
		}
	}
	
	//Gets JSON from URL
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
		getMenuInflater().inflate(R.menu.create_group, menu);
		return true;
	}
	
	//FOOTER
	public void Home(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("Username", userName);
		startActivity(intent);
	}
	
	public void MyActivities(View view) {
		Intent intent = new Intent(this, MyActivities.class);
		intent.putExtra("Username", userName);
		startActivity(intent);
	}

}
