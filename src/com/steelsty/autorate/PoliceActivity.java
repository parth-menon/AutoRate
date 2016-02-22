package com.steelsty.autorate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class PoliceActivity extends FragmentActivity implements LocationListener{

	GoogleMap mGoogleMap;
	
	MarkerOptions marker;
	LatLng loc;
	String[] mPlaceType = null;
	String[] mPlaceTypeName = null;
	
	private Set<Marker> user;
	

	double mLatitude = 0;
	double mLongitude = 0;
	LocationManager locationManager;
	String provider;
	HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
StrictMode.setThreadPolicy(policy);

//		mPlaceType = getResources().getStringArray(R.array.place_type);
//
//		// Array of place type names
//		mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

		// Creating an array adapter with an array of Place types
		// to populate the spinner
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);

		// Getting reference to the Spinner
//		mSprPlaceType = (Spinner) findViewById(R.id.spr_place_type);

		// Setting adapter on Spinner to set place types
//		mSprPlaceType.setAdapter(adapter);

		Button btnFind;

		// Getting reference to Find Button
//		btnFind = (Button) findViewById(R.id.btn_find);

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();
		} else { // Google Play Services are available

			// Getting reference to the SupportMapFragment
			SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_near);

			// Getting Google Map
			mGoogleMap = fragment.getMap();

			// Enabling MyLocation in Google Map
			mGoogleMap.setMyLocationEnabled(true);
			mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
			
			
			// Getting LocationManager object from System Service
			// LOCATION_SERVICE
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();

			// Getting the name of the best provider
			provider = locationManager.getBestProvider(criteria, true);

			// Getting Current Location From GPS
			Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

			if (location != null) {
				onLocationChanged(location);
			}

			locationManager.requestLocationUpdates(provider, 20000, 100, this);
			
//			mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
//				
//				@Override
//				public void onInfoWindowClick(Marker marker) {
//					Intent i=new Intent(MapActivity.this, MainActivity.class);
//					startActivity(i);
//					// TODO Auto-generated method stub
//					
//				}
//			});

			
			
			mGoogleMap
					.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

						@Override
						public void onInfoWindowClick(Marker arg0) {
							
							Intent intent = new Intent(getBaseContext(),
									PlaceDetailsActivity.class);
							String reference = mMarkerPlaceLink.get(arg0
									.getId());
							intent.putExtra("reference", reference);

							// Starting the Place Details Activity
							startActivity(intent);
							}
						
					});

			// Setting click event lister for the find button
			// btnFind.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {

//			int selectedPosition = mSprPlaceType.getSelectedItemPosition();
//			String type = mPlaceType[selectedPosition];
		
			// }
			// });
		}
	}
	
	

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	/** A class, to download Google Places */
	private class PlacesTask extends AsyncTask<String, Integer, String> {

		String data = null;

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result) {
			ParserTask parserTask = new ParserTask();

			// Start parsing the Google places in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a List construct */
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			} 
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			// Clears all the existing markers
//			mGoogleMap.clear();
			
		
			for (int i = 0; i < list.size(); i++) {

				// Creating a marker
				MarkerOptions markerOptions = new MarkerOptions();

				// Getting a place from the places list
				HashMap<String, String> hmPlace = list.get(i);

				// Getting latitude of the place
				double lat = Double.parseDouble(hmPlace.get("lat"));

				// Getting longitude of the place
				double lng = Double.parseDouble(hmPlace.get("lng"));

				// Getting name
				String name = hmPlace.get("place_name");

				// Getting vicinity
				String vicinity = hmPlace.get("vicinity");

				LatLng latLng = new LatLng(lat, lng);

				// Setting the position for the marker
				markerOptions.position(latLng);

				// Setting the title for the marker.
				// This will be displayed on taping the marker
				markerOptions.title(name + " : " + vicinity);

				// Placing a marker on the touched position
				Marker m = mGoogleMap.addMarker(markerOptions);
				String url=getDirectionsUrl(loc,latLng);
				DownloadTask downloadTask = new DownloadTask();
				downloadTask.execute(url);				

				// Linking Marker id and place reference
				mMarkerPlaceLink.put(m.getId(), hmPlace.get("reference"));
				
			}
		
		}

	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {
	
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
	
			// For storing data from web service
			String data = "";
	
			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}
	
		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
	
			ParserTask1 parserTask = new ParserTask1();
	
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask1 extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
	
		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {
	
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;
	
			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();
	
				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}
	
		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
		
			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
	
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
	
				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);
	
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
	
					points.add(position);
				}
	
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(9);
	
				// Changing the color polyline according to the mode
					lineOptions.color(Color.BLUE);
			}
	
			if (result.size() < 1) {
				Toast.makeText(getBaseContext(), "No Points",
						Toast.LENGTH_SHORT).show();
				return;
			}
	
			// Drawing polyline in the Google Map for the i-th route
			mGoogleMap.addPolyline(lineOptions);
		}
	}


	@Override
	public void onLocationChanged(Location location) {
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		loc = new LatLng(mLatitude, mLongitude);

		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

		
		String type="police";
		
		Log.d("lat", mLatitude+"");
		Log.d("lat", mLongitude+"");
		
		StringBuilder sb = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		sb.append("location=" + mLatitude + "," + mLongitude);
		sb.append("&radius=3000");
		sb.append("&name=" + type);
		sb.append("&sensor=true");
		sb.append("&key=key");

		// Creating a new non-ui thread task to download Google place json
		// data
		PlacesTask placesTask = new PlacesTask();
//		HospitalTask ht=new HospitalTask();
//		ht.execute("hospital");

		// Invokes the "doInBackground()" method of the class PlaceTask
		placesTask.execute(sb.toString());
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}



	private String getDirectionsUrl(LatLng origin, LatLng dest) {
	
		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;
	
		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
	
		// Sensor enabled
		String sensor = "sensor=false";
	
		// Travelling Mode
		String mode = "mode=driving";
	
		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
				+ mode;
	
		// Output format
		String output = "json";
	
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;
	
		return url;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locationManager.removeUpdates(PoliceActivity.this);
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(provider, 20000, 100, PoliceActivity.this);

	}


}
