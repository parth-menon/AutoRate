package com.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbSettings extends SQLiteOpenHelper
{

	public DbSettings(Context context)
	{
		super(context, "autoratesettings.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub

		String query = "create table settings (autorate TEXT, autoratemin TEXT, autoratewait TEXT, taxirate TEXT, taxiratemin TEXT,taxiratewait TEXT,splash TEXT,area TEXT,phoneno TEXT)";
		db.execSQL(query);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		onCreate(db);
	}

	public Vector<String> getReminders()
	{
		SQLiteDatabase db = getWritableDatabase();
		Vector<String> vectObj = new Vector<String>();
		String query = "SELECT * FROM settings";
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		if (c.moveToFirst())
		{
			vectObj.add(c.getString(0));
			vectObj.add(c.getString(1));
			vectObj.add(c.getString(2));
			vectObj.add(c.getString(3));
			vectObj.add(c.getString(4));
			vectObj.add(c.getString(5));
			vectObj.add(c.getString(6));
			vectObj.add(c.getString(7));
			vectObj.add(c.getString(8));
		}
		return vectObj;
	}

	public void dropDB(SQLiteDatabase db)
	{
		db = getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS settings");
		db.close();

		onCreate(db);
	}

	public void insertReminder()
	{
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM settings", null);
		if (c.getCount() == 0)
		{
			ContentValues values = new ContentValues();
			values.put("autorate", 10);
			values.put("autoratemin", 8);
			values.put("autoratewait", 15);
			values.put("taxirate", 15);
			values.put("taxiratemin", 10);
			values.put("taxiratewait", 20);
			values.put("splash", "Yes");
			values.put("area", "KL-11-AA-1111");
			values.put("phoneno", "9999999999");
			db.insert("settings", null, values);
		}
		db.close();

	}

	public void update(String col, String newval)
	{

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(col, newval);
		db.update("settings", values, null, null);
		db.close();
	}
}
