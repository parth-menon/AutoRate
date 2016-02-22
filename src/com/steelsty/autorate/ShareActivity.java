package com.steelsty.autorate;

import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends ListActivity
{

	TextView addNumberTextView;
	Button addOkButton;

	SharedPreferences pref;
	String lat, lon, add;
	ListView li;
	String dist, from, to, taxi, auto, from1, to1;
	ArrayList<String> al = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		Bundle b = getIntent().getExtras();
		dist = b.getString("distance");
		from = b.getString("from");
		to = b.getString("to");
		taxi = b.getString("taxi");
		auto = b.getString("auto");
		from1 = from.split(";")[0];
		to1 = to.split(";")[0];

		li = getListView();
		li.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

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
					String msg = "AutoRate Info;\nFrom :" + from1 + "\nTo:"
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
