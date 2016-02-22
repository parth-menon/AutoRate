package com.steelsty.autorate;

import com.db.DbSettings;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends Activity
{

	Button path, nearby, taxirate, police;
	LocationManager location;
	Location loc;
	DbSettings db = new DbSettings(HomeActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		db.insertReminder();
		db.close();
		Globals.setGlobals(HomeActivity.this);
		if (Globals.splash.equals("Yes"))
			startActivity(new Intent(HomeActivity.this, Splash.class));
		path = (Button) findViewById(R.id.button3_path);
		nearby = (Button) findViewById(R.id.button1_nearby);
		taxirate = (Button) findViewById(R.id.button2_rate);
		police = (Button) findViewById(R.id.button4_police);
		path.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				Intent in = new Intent(HomeActivity.this, PathActivity.class);
				startActivity(in);
			}
		});

		nearby.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				Intent in = new Intent(HomeActivity.this, NearbyActivity.class);
				startActivity(in);
			}
		});

		taxirate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				Intent in = new Intent(HomeActivity.this, RateActivity.class);
				startActivity(in);
			}
		});
		police.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				Intent in = new Intent(HomeActivity.this, PoliceActivity.class);
				startActivity(in);

			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		int i = 10;
		location = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!location.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			AlertDialog ad = new AlertDialog.Builder(HomeActivity.this).create();
			ad.setMessage("Switch GPS on");
			ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					onDestroy();
					finish();

				}
			});
			ad.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog)
				{
					onDestroy();
					finish();

				}
			});
			ad.show();

		} else
		{
			do
			{
				loc = location.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				Location loc1 = location.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (loc1 != null)
					loc = loc1;
				loc1 = location.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
				if (loc1 != null)
					loc = loc1;
				if (loc != null)
				{
					loc.getLatitude();
					loc.getLongitude();
				}
				i--;
			} while (loc == null && i > 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.settings)
		{
			startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
		}
		if (item.getItemId() == R.id.history)
		{
			startActivity(new Intent(HomeActivity.this, ViewHistoryActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

	}

}
