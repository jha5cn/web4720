package com.example.activityfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.*;

//Screen that displays list of activities
public class MainActivity extends Activity {
	
	String webserviceURL = "http://plato.cs.virginia.edu/~cs4720f13beet/find_closest_building/";
	String categoryURL = "http://plato.cs.virginia.edu/~cs4720f13beet/categories/";
	TextView locationResult;
	ArrayList<String> result = new ArrayList<String>();
	TextView activity1name;
	String webResponse = "";
	ArrayList<String> firstCategory = new ArrayList<String>();
	ArrayList<String> secondCategory = new ArrayList<String>();
	public final static String ACTIVITY_NAME_MESSAGE = "com.example.activityfinder.MESSAGE";
	String scheduledActivity1 = "Default activity";
	ArrayList<String> scheduledActivities = new ArrayList<String>();
	Context c = this;
	String userName;
	String pass;

	
	OnClickListener activityListener = new OnClickListener() {
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		String activityName = ((Button) view).getText().toString();
		ChangePage(activityName);
	}
	};
	
	public void ChangePage(String activityName) {
		activityName = activityName.toLowerCase();
		//Intent intent = new Intent(this, GroupPage.class);
		Intent intent = new Intent(this, ActivityPage.class);
		intent.putExtra("Activity Name", activityName);
		intent.putExtra("Username", userName);
		intent.putExtra("Password", pass);
		startActivity(intent);
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
	
	//Reads JSON
	public static String getJSONfromURL(String url) {
		InputStream is = null;
		String JSONresult = "";
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d("onCreate", "start up");
		locationResult = (TextView) findViewById(R.id.textView99);
		
		new PopulateCategory1().execute(categoryURL + "academic");
		new PopulateCategory2().execute(categoryURL + "athletic");
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		if (b != null) {
			userName= (String) b.get("Username");
			pass = (String) b.get("Password");
		}
		System.out.println(userName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//Actual method that populates categories
	private class PopulateCategory1 extends AsyncTask<String, Integer, String> {
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
				JsonArray Jarray = parser.parse(webJSON).getAsJsonArray();
				System.out.println("Jarray: " + Jarray.toString());
				//result.add(webJSON);
				//return ("done");
				/*String elt = gson.fromJson(Jarray, String.class);
				result.add(elt); */
				
				for (JsonElement obj : Jarray) {
					String elt = gson.fromJson(obj, String.class);
					System.out.println("elt: " + elt);
					firstCategory.add(elt);
				} 
			} catch (Exception e) {
				System.out.println(e.getStackTrace());
			} 
			
			System.out.println(firstCategory);
			System.out.println(secondCategory);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			TableRow dynamicRow1 = (TableRow) findViewById(R.id.tableRow3);
			for (String activity : firstCategory) {
				Button btn = new Button(c);
				btn.setText(activity);
				btn.setWidth(400);
				btn.setHeight(200);
				btn.setPadding(100, 20, 100, 20);
				btn.setOnClickListener(activityListener);
				dynamicRow1.addView(btn);
			}
		}
		
	}
	
	private class PopulateCategory2 extends AsyncTask<String, Integer, String> {
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
				JsonArray Jarray = parser.parse(webJSON).getAsJsonArray();
				System.out.println("Jarray: " + Jarray.toString());
				//result.add(webJSON);
				//return ("done");
				/*String elt = gson.fromJson(Jarray, String.class);
				result.add(elt); */
				
				for (JsonElement obj : Jarray) {
					String elt = gson.fromJson(obj, String.class);
					System.out.println("elt: " + elt);
					secondCategory.add(elt);
				} 
			} catch (Exception e) {
				System.out.println(e.getStackTrace());
			} 
			
			System.out.println(firstCategory);
			System.out.println(secondCategory);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			TableRow dynamicRow2 = (TableRow) findViewById(R.id.tableRow9);
			for (String activity : secondCategory) {
				Button btn = new Button(c);
				btn.setText(activity);
				btn.setWidth(400);
				btn.setHeight(200);
				btn.setPadding(100, 20, 100, 20);
				btn.setOnClickListener(activityListener);
				dynamicRow2.addView(btn);
			}
		}
		
	}

}