package com.steelsty.autorate;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.db.DbUtils;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;
import android.provider.Settings.Global;

public class RateResultActivity extends Activity
{

	TextView dis, speed, Arate, Trate, time, Waiting;
	Button save;
	DbUtils db;
	String dist, speedt, timet;
	String fromAddress, toAddress;
	int Arate1, Trate1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rate_result);
		save = (Button) findViewById(R.id.button1_save);
		dis = (TextView) findViewById(R.id.textView6_dis);
		time = (TextView) findViewById(R.id.textView7_tme);
		Arate = (TextView) findViewById(R.id.textView8_Arate);
		speed = (TextView) findViewById(R.id.textView9_speed);
		Trate = (TextView) findViewById(R.id.textView7_Trate);
		Waiting = (TextView) findViewById(R.id.textViewwaiting);
		db = new DbUtils(RateResultActivity.this);
		Bundle b = getIntent().getExtras();
		dist = b.getString("distance");
		speedt = b.getString("speed");
		timet = b.getString("time");
		fromAddress = null;
		toAddress = null;
		Double d = Double.valueOf(dist);
		int Aw = Globals.autowaiting * Globals.waiting;
		int Tw = Globals.taxiwaiting * Globals.waiting;
		Arate1 = Globals.automin;
		Trate1 = Globals.taximin;
		d -= 1;
		if (d > 0)
		{
			Arate1 += (int) ((d * Globals.autoRate) + Aw);
			Trate1 += (int) ((d * Globals.taxiRate) + Tw);
		}

		dis.setText(dist);
		time.setText(timet);
		speed.setText(speedt);
		this.Arate.setText(Arate1 + "");
		this.Trate.setText(Trate1 + "");
		this.Waiting.setText(Globals.waiting + "");
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (fromAddress == null)
					fromAddress = addressSet(Globals.p_lat, Globals.p_lng);
				if (toAddress == null)
					toAddress = addressSet(Globals.c_lat, Globals.c_lng);
				Vector<String> vectObj = new Vector<String>();
				vectObj.add(dist);
				vectObj.add(timet);
				vectObj.add(speedt);
				vectObj.add(fromAddress);
				vectObj.add(toAddress);
				vectObj.add(Trate1 + "");
				vectObj.add(Arate1 + "");
				db.insertReminder(vectObj);
				Toast.makeText(RateResultActivity.this, "History Saved", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_rate_result, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent in = null;
		if (item.getItemId() == R.id.menu1)
		{
			if (fromAddress == null)
				fromAddress = addressSet(Globals.p_lat, Globals.p_lng);
			if (toAddress == null)
				toAddress = addressSet(Globals.c_lat, Globals.c_lng);
			in = new Intent(RateResultActivity.this, ShareActivity.class);
			in.putExtra("distance", dist);
			in.putExtra("from", fromAddress);
			in.putExtra("to", toAddress);
			in.putExtra("taxi", Trate1 + "");
			in.putExtra("auto", Arate1 + "");
			startActivity(in);

		} else if (item.getItemId() == R.id.menu2)
		{
			if (fromAddress == null)
				fromAddress = addressSet(Globals.p_lat, Globals.p_lng);
			if (toAddress == null)
				toAddress = addressSet(Globals.c_lat, Globals.c_lng);
			in = new Intent(RateResultActivity.this, Complaints.class);
			in.putExtra("distance", dist);
			in.putExtra("from", fromAddress);
			in.putExtra("to", toAddress);
			in.putExtra("taxi", Trate1 + "");
			in.putExtra("auto", Arate1 + "");
			startActivity(in);
		}
		return super.onOptionsItemSelected(item);
	}

	public String addressSet(double latitude, double longitude)
	{
		Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
		String result = null;
		try
		{
			List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
			if (addressList != null && addressList.size() > 0)
			{
				Address address = addressList.get(0);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
				{
					sb.append(address.getAddressLine(i)).append(";");
				}
//				sb.append(address.getLocality());
				result = sb.toString();
				Log.d("result", result);
			}
		} catch (IOException e)
		{
			Log.e("ERROR", "Unable connect to Geocoder", e);
		}
		return result;
	}

}
