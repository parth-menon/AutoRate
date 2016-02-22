package com.steelsty.autorate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class ServiceLoc extends Service implements LocationListener,
		ConnectionCallbacks, OnConnectionFailedListener
{

	GoogleApiClient mGAC;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	LocationRequest mLocationRequest;
	double dis;
	Location first,second;
	int i;
	CountDownTimer count;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mGAC = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(3000).setFastestInterval(3000);
//				.setSmallestDisplacement(3);
		count=new CountDownTimer(60000,1000) {
			
			@Override
			public void onTick(long millisUntilFinished)
			{
				if(second==first)
				{
					i++;
				}
				else
				{
					second=first;
					i=0;
				}
				if(i==60)
				{
					Globals.waiting++;
					Toast.makeText(getApplicationContext(), "Waiting: "+Globals.waiting, Toast.LENGTH_SHORT).show();
					i=0;
				}
			}
			
			@Override
			public void onFinish()
			{
				count.start();
			}
		};
		

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{

		dis = 0;
		first = null;
		Globals.dis = 0;
		i=0;
		Globals.waiting=0;
		mGAC.connect();
		count.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult)
	{
		try
		{
			connectionResult.startResolutionForResult(null,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);
		} catch (IntentSender.SendIntentException e)
		{
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext(), "Connection Failed! Try again.",Toast.LENGTH_SHORT).show();
		onDestroy();
	}

	@Override
	public void onConnected(Bundle arg0)
	{
		
		LocationServices.FusedLocationApi.requestLocationUpdates(mGAC,
				mLocationRequest, this);
	}

	@Override
	public void onLocationChanged(Location location)
	{
		dis = 0;
		if (first == null)
		{
			first = location;
			second=location;
			MarkerOptions options = new MarkerOptions()
					.position(
							new LatLng(location.getLatitude(), location
									.getLongitude()))
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			Globals.map.addMarker(options);

		} else
			dis = getDistance(first.getLatitude(), location.getLatitude(),
					first.getLongitude(), location.getLongitude());
		if (dis >= 2.5)
		{
			Globals.dis += dis;
			Toast.makeText(getApplicationContext(), "Distance: " + Globals.dis/1000,
					Toast.LENGTH_LONG).show();
			MarkerOptions options = new MarkerOptions()
					.position(
							new LatLng(location.getLatitude(), location
									.getLongitude()))
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			Globals.map.addMarker(options);
			first = location;
		}
//		else
//		{
//			i++;
//			if(i>20)
//			{
//				Globals.waiting++;
//				Toast.makeText(getApplicationContext(), "Waiting: "+Globals.waiting, Toast.LENGTH_SHORT).show();
//				i=0;
//			}
//		}
	}

	@Override
	public void onConnectionSuspended(int arg0)
	{

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
		return d * 1000;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		if (mGAC.isConnected())
		{
			LocationServices.FusedLocationApi.removeLocationUpdates(mGAC, this);
			mGAC.disconnect();
		}
		count.cancel();
		super.onDestroy();

	}

}
