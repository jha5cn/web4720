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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity {
	
	EditText userName;
	EditText password;
	String defaultAuthenticateURL = "http://composite-fire-350.appspot.com/user/authenticate/";
	String authenticateURL;
	String defaultRegisterURL = "http://composite-fire-350.appspot.com/user/register/";
	String registerURL;
	SignInMessage authStatus;
	RegisterMessage regStatus;
	JsonObject stardock;
	Context c = this;
	String user;
	String pass;
	double latitude;
	double longitude;
	String defaultStardockRegisterURL = "http://plato.cs.virginia.edu/~cs4720f13beet/users/register/";
	String stardockRegisterURL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		userName = (EditText) findViewById(R.id.editTextUserName);
		password = (EditText) findViewById(R.id.editTextPassword);
		userName.setText("kevin");
		password.setText("kevin");
		
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}
	
	//Authenticates solely based on GAE
	public void Authenticate(View view) {
		authenticateURL = defaultAuthenticateURL;
		authenticateURL += userName.getText() + "/" + password.getText();
		user = userName.getText().toString();
		pass = password.getText().toString();
		new Authenticate().execute(authenticateURL);
//		Intent intent = new Intent(this, MainActivity.class);
//		startActivity(intent);
	}
	
	//Registers to GAE and Stardock
	public void Register(View view) {
		registerURL = defaultRegisterURL;
		registerURL += userName.getText() + "/" + password.getText();
		stardockRegisterURL = defaultStardockRegisterURL;
		stardockRegisterURL += userName.getText()+ "/" + password.getText() + "/" + latitude + "/" + longitude;
		new Register().execute(registerURL);
		new StardockRegister().execute(stardockRegisterURL);
	}
	
	//Actual method that authenticate
	private class Authenticate extends AsyncTask<String, Integer, String> {
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
				
				authStatus = gson.fromJson(parser.parse(webJSON).getAsJsonObject(), SignInMessage.class);
				System.out.println(authStatus);
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
			TextView message = (TextView) findViewById(R.id.TextViewLoginMessage);
			authenticateURL = defaultAuthenticateURL;
			System.out.println(authStatus.getExists());
			System.out.println(authStatus.getSuccess());
			if (authStatus.getExists().equals("false")){
				message.setText("User does not exist");
			}
			else if (authStatus.getSuccess().equals("false")) {
				message.setText("User exists but password is incorrect");
			}
			else {
				Intent intent = new Intent(c, MainActivity.class);
				intent.putExtra("Username", user);
				intent.putExtra("Password", pass);
				startActivity(intent);
			}
		}
	}
	
	//Actual method that registers on GAE
	private class Register extends AsyncTask<String, Integer, String> {
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
				
				regStatus = gson.fromJson(parser.parse(webJSON).getAsJsonObject(), RegisterMessage.class);
				System.out.println(regStatus);
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
			TextView message = (TextView) findViewById(R.id.TextViewLoginMessage);
			registerURL = defaultRegisterURL;
			if (regStatus.getSuccess().equals("false")){
				message.setText("Registration unsuccessful");
			}
			else {
				message.setText("Registration successful");
			}
		}
	}
	
	//Actual method that registers to Stardock
	private class StardockRegister extends AsyncTask<String, Integer, String> {
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
				
				stardock = parser.parse(webJSON).getAsJsonObject();
				System.out.println(stardock.get("success").toString());
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
			TextView message = (TextView) findViewById(R.id.TextViewLoginMessage);
			stardockRegisterURL = defaultStardockRegisterURL;
			if (stardock.get("success").toString().equals("false")){
				message.append(", Stardock registration unsuccessful");
			}
			else {
				message.append(", Stardock registration successful");
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
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
