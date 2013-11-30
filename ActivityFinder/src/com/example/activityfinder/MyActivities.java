package com.example.activityfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

//Displays user's signed up activities
public class MyActivities extends Activity {
	
	String userName;
	String pass;
	String userGroupsURL = "http://plato.cs.virginia.edu/~cs4720f13beet/users/";
	ArrayList<UserGroups> groups = new ArrayList<UserGroups>();
	Context c = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_activities);
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		if (b != null) {
			userName = (String) b.get("Username");
			pass = (String) b.get("Password");
		}
		TextView header = (TextView) findViewById(R.id.textViewUserList);
		header.setText(userName + "'s Activities");
		
		userGroupsURL += userName + "/groups";
		new GetGroups().execute(userGroupsURL);
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
			TableLayout tl = (TableLayout) findViewById(R.id.tableLayoutMyActivities);
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			for (UserGroups g : groups) {
				TableRow tr = new TableRow(c);
				tr.setLayoutParams(lp);
				TextView tv1 = new TextView(c);
				tv1.setText(g.getActivity_name());
				tv1.setPadding(50, 0, 50, 0);
				TextView tv2 = new TextView(c);
				tv2.setText(g.getNum_registered());
				tv2.setPadding(230, 0, 0, 0);
				TextView tv3 = new TextView(c);
				tv3.setText(g.getStart_time());
				tv3.setPadding(80, 0, 0, 0);
				TextView tv4 = new TextView(c);
				tv4.setText(g.getBuilding_name());
				tv4.setPadding(90, 0, 0, 0);
				tr.addView(tv1);
				tr.addView(tv2);
				tr.addView(tv3);
				tr.addView(tv4);
				tl.addView(tr);
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
		getMenuInflater().inflate(R.menu.my_activities, menu);
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

}
