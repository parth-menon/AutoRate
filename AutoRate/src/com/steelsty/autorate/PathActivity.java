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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class PathActivity extends FragmentActivity implements LocationListener
{

	GoogleMap map;
	ArrayList<LatLng> markerPoints;
	RadioButton rbDriving;
	RadioButton rbBiCycling;

	RadioGroup rgModes;
	int mMode = 0;
	final int MODE_DRIVING = 0;
	final int MODE_BICYCLING = 1;
	final int MODE_WALKING = 2;
	private String locationFrom, locationTo;
	LatLng FromLocation;
	LatLng ToLocation;
	LatLng points;
	LatLng myPosition;
	EditText add;
	Button search;

	static double mLatitude = 0;
	static double mLongitude = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path);

		rbDriving = (RadioButton) findViewById(R.id.rb_driving);

		add = (EditText) findViewById(R.id.editText1_ser);
		search = (Button) findViewById(R.id.button1_ser);
		// Getting reference to rb_bicylcing
		rbBiCycling = (RadioButton) findViewById(R.id.rb_bicycling);
		// Getting Reference to rg_modes
		rgModes = (RadioGroup) findViewById(R.id.rg_modes);

		rgModes.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{

				// Checks, whether start and end locations are captured
				if (markerPoints.size() >= 2)
				{
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);

					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);

					DownloadTask downloadTask = new DownloadTask();

					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
			}
		});

		// Initializing
		markerPoints = new ArrayList<LatLng>();

		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				String location = add.getText().toString();
				if (markerPoints.size() > 1)
				{
					markerPoints.clear();
					map.clear();
					markerPoints.add(myPosition);
				}
				if (location != null && !location.equals(""))
				{
					new GeocoderTask().execute(location);
				}
			}
		});

		// Getting reference to SupportMapFragment of the activity_main
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment1_path);
		map = fm.getMap();
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//
		Criteria criteria = new Criteria();
		//
		String provider = locationManager.getBestProvider(criteria, true);
		//
		Location location1 = locationManager
				.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		//

		// Getting Map for the SupportMapFragment

		// Enable MyLocation Button in the Map
		map.setMyLocationEnabled(true);
		map.getUiSettings().setMapToolbarEnabled(false);
		if (location1 != null)
		{
			onLocationChanged(location1);
		}

	}

	// Drawing Start and Stop locations
	private void drawStartStopMarkers()
	{

		for (int i = 0; i < markerPoints.size(); i++)
		{

			// Creating MarkerOptions
			MarkerOptions options = new MarkerOptions();

			// Setting the position of the marker
			options.position(markerPoints.get(i));

			/**
			 * For the start location, the color of marker is GREEN and for the
			 * end location, the color of marker is RED.
			 */
			if (i == 0)
			{
				options.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			} else if (i == 1)
			{
				options.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			}

			// Add new marker to the Google Map Android API V2
			map.addMarker(options);
		}
	}

	private String getDirectionsUrl(LatLng origin, LatLng dest)
	{

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Travelling Mode
		String mode = "mode=driving";

		if (rbDriving.isChecked())
		{
			mode = "mode=driving";
			mMode = 0;
		} else if (rbBiCycling.isChecked())
		{
			mode = "mode=walking";
			mMode = 2;
		}

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

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException
	{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try
		{
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
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e)
		{
			Log.d("Exception while downloading url", e.toString());
		} finally
		{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>
	{

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url)
		{

			// For storing data from web service
			String data = "";

			try
			{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e)
			{
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
	{

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData)
		{

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try
			{
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result)
		{
			ArrayList<LatLng> points = null;
			LatLng prev = null;
			Double distance = 0.0;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++)
			{
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++)
				{
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					if (j == 0)
						prev = position;
					else
					{
						distance = distance
								+ getDistance(prev.latitude, position.latitude,
										prev.longitude, position.longitude);
						prev = position;
					}
					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(4);

				// Changing the color polyline according to the mode
				if (mMode == MODE_DRIVING)
					lineOptions.color(Color.RED);
				else if (mMode == MODE_BICYCLING)
					lineOptions.color(Color.GREEN);
			}

			if (result.size() < 1)
			{
				Toast.makeText(getBaseContext(), "No Points",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions);
			float dist = distance.floatValue();
			int autorate = Globals.automin;
			int taxirate = Globals.taximin;
			if (dist - 1 > 0)
			{
				autorate += (int) (dist - 1) * autorate;
				taxirate += (int) (dist - 1) * taxirate;
			}
			Toast.makeText(
					getApplicationContext(),
					"Distance: " + dist + "\nAuto Rate: " + autorate + "\nTaxi Rate: "
							+ taxirate, Toast.LENGTH_LONG).show();
		}
	}

	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>
	{

		@Override
		protected List<Address> doInBackground(String... locationName)
		{
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;

			try
			{
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(locationName[0], 3);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses)
		{

			if (addresses == null || addresses.size() == 0)
			{
				Toast.makeText(getBaseContext(), "No Location found",
						Toast.LENGTH_SHORT).show();
			}

			// Adding Markers on Google Map for each matching address
			for (int i = 0; i < addresses.size(); i++)
			{

				Address address = (Address) addresses.get(i);

				// Creating an instance of GeoPoint, to display in Google Map
				LatLng latLng = new LatLng(address.getLatitude(),
						address.getLongitude());

				String addressText = String.format(
						"%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address
								.getCountryName());

				MarkerOptions markerOptions;
				markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title(addressText);
				map.addMarker(markerOptions);

				markerPoints.add(latLng);

				// // Draws Start and Stop markers on the Google Map
				drawStartStopMarkers();
				//
				// // Checks, whether start and end locations are captured
				if (markerPoints.size() >= 2)
				{
					LatLng origin = myPosition;
					LatLng dest = markerPoints.get(1);
					//
					// // Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);

					DownloadTask downloadTask = new DownloadTask();

					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
					// Locate the first location
					if (i == 0)
					{
						map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
					}
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{

		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		LatLng latLng = new LatLng(mLatitude, mLongitude);
		myPosition = latLng;
		markerPoints.add(latLng);
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(location.getLatitude(), location
						.getLongitude())).zoom(12).build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub

	}

	private double getDistance(double lat1, double lat2, double lon1,
			double lon2)
	{
		double R = 6371;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double dLat1 = Math.toRadians(lat1);
		double dLat2 = Math.toRadians(lat2);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(dLat1)
				* Math.cos(dLat1) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		Log.d("Result", d + "");
		return d;
	}

	private double deg2rad(double deg)
	{
		return (deg * Math.PI / 180.0);
	}

}
