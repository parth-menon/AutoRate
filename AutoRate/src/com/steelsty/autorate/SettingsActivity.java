package com.steelsty.autorate;

import java.util.Vector;

import com.db.DbSettings;
import com.db.DbUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SettingsActivity extends ListActivity
{
	DbSettings db = new DbSettings(SettingsActivity.this);
	String[] values;
	String[] column = { "autorate", "autoratemin", "autoratewait", "taxirate", "taxiratemin", "taxiratewait", "splash", "area", "phoneno" };
	String[] str1 = { "Set Auto Rate", "Set Auto Minimum Rate", "Set Auto Waiting Rate", "Set Taxi Rate", "Set Taxi Minimum Rate", "Set Taxi Waiting Rate", "Set Splash", "Set State License Format", "Set User's PhoneNo" };
	String[] str2 = { "Enter current Auto rate here: ", "Enter Auto Minimum rate here: ", "Enter Auto Waiting rate here: ", "Enter current Taxi rate here: ", "Enter Taxi Minimum rate here: ", "Enter Taxi Waiting rate here: ", "Splash On/Off(Yes/No): ", "Enter State License Format: ", "Enter User's PhoneNo: " };
	int i;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View mview, final int position, long id)
			{
				i = (int) id;
				AlertDialog ad = new AlertDialog.Builder(SettingsActivity.this).create();
				ad.setTitle(str1[i]);
				ad.setMessage(str2[i]);
				final EditText input = new EditText(SettingsActivity.this);
				ad.setView(input);
				ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						String find = input.getText().toString();
						String newval;
						if (i == 6)
						{
							if (find.equals("No"))
								newval = "No";
							else
								newval = "Yes";
						} else
							newval = find;
						if(i>=0 && i<=5)
							newval= (Integer.parseInt(newval))+"";
						db.update(column[i], newval);
						bind();
					}
				});
				ad.show();
			}
		});
	}

	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		bind();
	}

	public void bind()
	{
		Vector<String> d1 = db.getReminders();
		values = new String[d1.size()];
		String[] names = { "Auto Rate: ", "Auto Rate Minimum: ", "Auto Rate Waiting: ", "Taxi Rate: ", "Taxi Rate Minimum: ", "Taxi Rate Waiting: ", "Splash On/Off: ", "State License Format: ", "PhoneNo: " };
		for (int i = 0; i < d1.size(); i++)
		{
			values[i] = names[i] + d1.get(i);
		}
		ArrayAdapter<String> arays = new ArrayAdapter<String>(this, R.layout.settings_list, R.id.label, values);
		// Binding Array to ListAdapter
		this.setListAdapter(arays);

	}

	@Override
	protected void onDestroy()
	{
		Globals.setGlobals(SettingsActivity.this);
		super.onDestroy();
	}

}
