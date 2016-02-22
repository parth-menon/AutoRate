package com.steelsty.autorate;

import java.util.Vector;

import com.db.DbUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ViewHistoryActivity extends ListActivity {
	DbUtils db = new DbUtils(this);

	String data2;
	String[] values;
	String dist, time, speed, from, to, taxi, auto;
	String id,rem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View mview,
					final int position, long id) {
				
				rem = ((TextView) mview).getText().toString();
				rem =rem.substring(rem.indexOf(":") + 1,rem.indexOf("."));
				Intent in=new Intent(ViewHistoryActivity.this, ShareActivity1.class);
				in.putExtra("rem", rem);
				startActivity(in);
				

			}
		});

		// long click to delete code
		lv.setLongClickable(true);
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
					long id) {
				// Toast.makeText(getActivity(), Integer.toString(pos),
				// Toast.LENGTH_SHORT).show();
				rem = ((TextView) v).getText().toString();
				rem =rem.substring(rem.indexOf(":") + 1,rem.indexOf("."));
				int pos1 = Integer.parseInt(rem);
				db.deleteReminder(pos1);
				bind();
				return true;
			}
		});

	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		bind();
	}
	
	public void bind(){
		Vector<Vector<String>> d1 = db.getReminders();
		values = new String[d1.size()];
		System.out.println(d1.size());

		for (int i1 = 0; i1 < d1.size(); i1++) {
			Vector<String> data1 = d1.get(i1);
			System.out.println(data1.size());
			if (data1 != null && data1.size() > 0) {

				id = data1.get(0);
				dist = data1.get(1);
				time = data1.get(2);
				speed = data1.get(3);
				from = data1.get(4);
				to = data1.get(5);
				taxi = data1.get(6);
				auto = data1.get(7);

				StringBuilder sb = new StringBuilder();
				sb.append("ID:" + id + "." + "\n" + "Distance : " + dist + "\n"
						+ "Time : " + time + "\n" + "Speed :" + speed
						+ "\n" + "From Address : " + from + "\n" + "To Address : "
						+ to + "\n" + "Taxi Rate : " + taxi + "\n" + "Auto Rate : "
						+ auto);

				data2 = sb.toString();

				values[i1] = data2;
			}
		}

		ArrayAdapter<String> arays = new ArrayAdapter<String>(this,
				R.layout.list_item, R.id.label, values);
		// Binding Array to ListAdapter
		this.setListAdapter(arays);

	}


}
