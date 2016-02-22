package com.steelsty.autorate;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.db.DbUtils;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity1 extends ListActivity
{

	TextView addNumberTextView;
	Button addOkButton;
	DbUtils db;
	String lat, lon, add;
	ListView li;
	String dist, from, from1, to, to1, taxi, auto, rem;
	ArrayList<String> al = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		db = new DbUtils(ShareActivity1.this);
		Bundle b = getIntent().getExtras();
		rem = b.getString("rem");

		Vector<String> v = db.getReminderDetails(Integer.parseInt(rem));

		dist = v.get(0);
		from = v.get(3);
		from1 = from.split(";")[0];
		to = v.get(4);
		to1 = to.split(";")[0];
		taxi = v.get(5);
		auto = v.get(6);

		li = getListView();
		li.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		List<String> list = new ArrayList<String>();
		Cursor c = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		while (c.moveToNext())
		{

			String contactName = c
					.getString(c
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phNumber = c
					.getString(c
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			list.add(contactName + ":" + phNumber);

		}
		c.close();
		li.setTextFilterEnabled(true);
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked, list));

		addOkButton = (Button) findViewById(R.id.callAddOkBt);

		addOkButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				int i = 0;
				for (i = 0; i < al.size(); i++)
				{
					String msg = "AutoRate Info:\nFrom :" + from1 + "\nTo:"
							+ to1 + "\nAuto Rate:" + auto + "\nTaxi Rate:"
							+ taxi;
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage(al.get(i), null, msg, null, null);
					Toast.makeText(getApplicationContext(), "Message Sent",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	public void onListItemClick(ListView parent, View v, int position, long id)
	{
		CheckedTextView item = (CheckedTextView) v;
		Toast.makeText(
				this,
				parent.getItemAtPosition(position).toString() + " checked : "
						+ item.isChecked(), Toast.LENGTH_SHORT).show();
		String number = parent.getItemAtPosition(position).toString()
				.split(":")[1];
		al.add(number);

	}

}
