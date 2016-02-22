package com.steelsty.autorate;

import java.util.concurrent.CountDownLatch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateActivity extends FragmentActivity implements LocationListener,
		ConnectionCallbacks, OnConnectionFailedListener
{

	GoogleApiClient mGAC;
	GoogleMap map = null;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	LocationRequest mLocationRequest;
	Button bt;
	Button bt2;
	TextView e1;
	Location location;
	CountDownTimer counter;
	static double n = 0;
	String TAG = "Rate";
	static Boolean bool;
	long r1;
	Intent myIntent;
	double distance = 0;
	PowerManager pm;
	WakeLock wk;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rate);
		bool = false;
		e1 = (TextView) findViewById(R.id.textView1_ra);
		mGAC = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();

		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(30000).setFastestInterval(10000)
				.setSmallestDisplacement(5);
		bt = (Button) findViewById(R.id.button1_start);
		bt2 = (Button) findViewById(R.id.button2_stop);

		counter = new CountDownTimer(60000, 1000) {

			@Override
			public void onTick(long millisUntilFinished)
			{
				r1 = (60000 - millisUntilFinished) / 1000;
				e1.setText(r1+"");
			}

			@Override
			public void onFinish()
			{
				n = n + 1;
				counter.start();
			}
		};

		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (bool == false)
				{
					Toast.makeText(RateActivity.this, "Service Started",
							Toast.LENGTH_LONG).show();
					n = 0;
					location = LocationServices.FusedLocationApi
							.getLastLocation(mGAC);
					counter.start();
					Location loc = LocationServices.FusedLocationApi
							.getLastLocation(mGAC);
					Globals.p_lat = loc.getLatitude();
					Globals.p_lng = loc.getLongitude();
					Log.d("latlng",Globals.p_lat+" "+Globals.p_lng);
					LocationServices.FusedLocationApi.removeLocationUpdates(
							mGAC, RateActivity.this);
					pm= (PowerManager) getSystemService(POWER_SERVICE);
					wk = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Awake");
					wk.acquire();
					
					
					myIntent = new Intent(RateActivity.this, ServiceLoc.class);
					startService(myIntent);
					bool = true;
				}
			}
		});

		bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (bool == true)
				{
					double time = n * 60 + r1;
					stopService(myIntent);
					Globals.dis/=1000;
					Toast.makeText(
							RateActivity.this,
							"Distance:" + String.valueOf(Globals.dis)
									+ "\nVelocity in m/sec :"
									+ String.valueOf(Globals.dis / time)
									+ "\nTime :" + String.valueOf(time),
							Toast.LENGTH_LONG).show();
					counter.cancel();
					wk.release();

					Intent in = new Intent(RateActivity.this,
							RateResultActivity.class);
					in.putExtra("distance", String.valueOf(Globals.dis));
					in.putExtra("speed", String.valueOf(Globals.dis / time));
					in.putExtra("time", String.valueOf(time));
					LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
					Location loc = lm
							.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
					Globals.c_lat = loc.getLatitude();
					Globals.c_lng = loc.getLongitude();
					Log.d("latlng",Globals.c_lat+" "+Globals.c_lng);
					LocationServices.FusedLocationApi.removeLocationUpdates(
							mGAC, RateActivity.this);
					startActivity(in);
					map.clear();
					bool = false;
				}
			}
		});

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult)
	{
		try
		{
			// Start an Activity that tries to resolve the error
			connectionResult.startResolutionForResult(this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);
		} catch (IntentSender.SendIntentException e)
		{
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext(), "Connection Failed! Try again.", Toast.LENGTH_SHORT).show();
		finish();

	}

	@Override
	public void onConnected(Bundle arg0)
	{
		// TODO Auto-generated method stub
		if (bool == false)
		{
			LocationServices.FusedLocationApi.requestLocationUpdates(mGAC,
					mLocationRequest, this);
		}
	}

	@Override
	public void onConnectionSuspended(int arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		if (mGAC.isConnected())
		{
			LocationServices.FusedLocationApi.removeLocationUpdates(mGAC, this);
			mGAC.disconnect();
		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onPause();
		if (mGAC.isConnected())
		{
			LocationServices.FusedLocationApi.removeLocationUpdates(mGAC, this);
			mGAC.disconnect();
		}
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		setUpMapIfNeeded();
		Globals.map = map;
		mGAC.connect();
		map.moveCamera(CameraUpdateFactory.zoomTo(15));
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location loc = lm
				.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		double currentLatitude = loc.getLatitude();
		double currentLongitude = loc.getLongitude();
		LatLng latLng = new LatLng(currentLatitude, currentLongitude);
		MarkerOptions options = new MarkerOptions().position(latLng).title(
				"I am here!");
		map.addMarker(options);
		map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
	}

	private void setUpMapIfNeeded()
	{
		// TODO Auto-generated method stub
		if (map == null)
		{
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment1_rate)).getMap();
			map.setMyLocationEnabled(true);
			map.getUiSettings().setMapToolbarEnabled(false);
			map.clear();
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		double currentLatitude = location.getLatitude();
		double currentLongitude = location.getLongitude();
		LatLng latLng = new LatLng(currentLatitude, currentLongitude);
		MarkerOptions options = new MarkerOptions().position(latLng).title(
				"I am here!");
		map.addMarker(options);
		map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activityrate, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//		//Auto Rate
//		String[] str1 = {"Set Auto Rate","Set Auto Minimum Rate","Set Auto Waiting Rate","Set Taxi Rate","Set Taxi Minimum Rate","Set Taxi Waiting Rate"};
//		String[] str2 = {"Enter current Auto rate here(","Enter Auto Waiting rate here(","Enter Auto Minimum rate here(","Enter current Taxi rate here(","Enter Taxi Minimum rate here(","Enter Taxi Waiting rate here("};
//		if (item.getItemId() == R.id.menu_settings)
//		{
//			AlertDialog ad = new AlertDialog.Builder(RateActivity.this)
//					.create();
//			ad.setTitle("Set Auto Rate");
//			ad.setMessage("Enter current Auto rate here(" + Globals.autoRate
//					+ ")");
//			final EditText input = new EditText(RateActivity.this);
//			ad.setView(input);
//
//			ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							String rate = input.getText().toString();
//							Globals.autoRate = Integer.parseInt(rate);
//
//						}
//					});
//			ad.show();
//		}
//		//Auto Waiting Rate
//		if (item.getItemId() == R.id.menu_settings3)
//		{
//			AlertDialog ad = new AlertDialog.Builder(RateActivity.this)
//					.create();
//			ad.setTitle("Set Auto Waiting Rate");
//			ad.setMessage("Enter Auto Waiting rate here(" + Globals.autowaiting
//					+ ")");
//			final EditText input = new EditText(RateActivity.this);
//			ad.setView(input);
//
//			ad.setButton(ad.BUTTON_POSITIVE, "OK",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							String rate = input.getText().toString();
//							Globals.autowaiting = Integer.parseInt(rate);
//						}
//					});
//			ad.show();
//		}
//		//Auto Minimum Rate
//		if (item.getItemId() == R.id.menu_settings4)
//		{
//			AlertDialog ad = new AlertDialog.Builder(RateActivity.this)
//					.create();
//			ad.setTitle("Set Auto Minimum Rate");
//			ad.setMessage("Enter Auto Minimum rate here(" + Globals.automin
//					+ ")");
//			final EditText input = new EditText(RateActivity.this);
//			ad.setView(input);
//
//			ad.setButton(ad.BUTTON_POSITIVE, "OK",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							String rate = input.getText().toString();
//							Globals.automin = Integer.parseInt(rate);
//						}
//					});
//			ad.show();
//		}
//		//Taxi Rate
//		if (item.getItemId() == R.id.menu_settings1)
//		{
//			AlertDialog ad = new AlertDialog.Builder(RateActivity.this)
//					.create();
//			ad.setTitle("Set Taxi Rate");
//			ad.setMessage("Enter current Taxi rate here(" + Globals.taxiRate
//					+ ")");
//			final EditText input = new EditText(RateActivity.this);
//			ad.setView(input);
//
//			ad.setButton(ad.BUTTON_POSITIVE, "OK",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							String rate = input.getText().toString();
//							Globals.taxiRate = Integer.parseInt(rate);
//						}
//					});
//			ad.show();
//		}
//		//Taxi Waiting Rate
//		if (item.getItemId() == R.id.menu_settings5)
//		{
//			AlertDialog ad = new AlertDialog.Builder(RateActivity.this)
//					.create();
//			ad.setTitle("Set Taxi Waiting Rate");
//			ad.setMessage("Enter Taxi Waiting rate here(" + Globals.taxiwaiting
//					+ ")");
//			final EditText input = new EditText(RateActivity.this);
//			ad.setView(input);
//
//			ad.setButton(ad.BUTTON_POSITIVE, "OK",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							String rate = input.getText().toString();
//							Globals.taxiwaiting = Integer.parseInt(rate);
//						}
//					});
//			ad.show();
//		}
//		//Taxi Minimum Rate
//		if (item.getItemId() == R.id.menu_settings6)
//		{
//			AlertDialog ad = new AlertDialog.Builder(RateActivity.this)
//					.create();
//			ad.setTitle("Set Taxi Minimum Rate");
//			ad.setMessage("Enter Taxi Minimum rate here(" + Globals.taximin
//					+ ")");
//			final EditText input = new EditText(RateActivity.this);
//			ad.setView(input);
//
//			ad.setButton(ad.BUTTON_POSITIVE, "OK",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							String rate = input.getText().toString();
//							Globals.taximin = Integer.parseInt(rate);
//						}
//					});
//			ad.show();
//		}
//		//View History
//		if (item.getItemId() == R.id.menu_settings2)
//		{
//			Intent in = new Intent(RateActivity.this, ViewHistoryActivity.class);
//			startActivity(in);
//		}
//
//		return super.onOptionsItemSelected(item);
//	}

}
