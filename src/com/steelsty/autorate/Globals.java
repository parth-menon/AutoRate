package com.steelsty.autorate;

import java.util.Vector;
import java.util.regex.Pattern;

import android.content.Context;

import com.db.DbSettings;
import com.google.android.gms.maps.GoogleMap;

public class Globals
{

	public static int i = 0;
	public static int count = 0;
	public static int autoRate;
	public static int autowaiting;
	public static int automin;
	public static int taxiRate;
	public static int taxiwaiting;
	public static int taximin;
	public static double dis;
	public static GoogleMap map;
	public static double c_lat;
	public static double c_lng;
	public static double p_lat;
	public static double p_lng;
	public static String splash = "Yes";
	public static String area;
	public static String phoneno;
	public static int waiting = 0;

	public static void setGlobals(Context context)
	{
		DbSettings db = new DbSettings(context);
		Vector<String> d1 = db.getReminders();
		autoRate = Integer.parseInt(d1.get(0));
		automin = Integer.parseInt(d1.get(1));
		autowaiting = Integer.parseInt(d1.get(2));
		taxiRate = Integer.parseInt(d1.get(3));
		taximin = Integer.parseInt(d1.get(4));
		taxiwaiting = Integer.parseInt(d1.get(5));
		splash = d1.get(6);
		area = d1.get(7);
		phoneno=d1.get(8);		
		db.close();
	}

}
