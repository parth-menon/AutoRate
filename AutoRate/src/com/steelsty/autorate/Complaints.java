package com.steelsty.autorate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Complaints extends Activity
{

	WebView mW;
	String from, to, from1, to1, taxi, auto, dist;
	int auto1;

	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_details);
		Bundle b = getIntent().getExtras();
		dist = b.getString("distance");
		from = b.getString("from");
		to = b.getString("to");
		taxi = b.getString("taxi");
		auto = b.getString("auto");
		auto1 = Integer.parseInt(auto);
		auto1 += Globals.autoRate;
//		from1 = from.split("\n")[0]+from.split("\n")[1].split(",")[0];
//		to1 = to.split("\n")[0]+to.split("\n")[1].split(",")[0];
		Log.d("dist", dist);
		Log.d("from", from);
		Log.d("to", to);
		Log.d("taxi", taxi);
		Log.d("auto", auto);

		// Getting reference to WebView ( wv_place_details ) of the layout
		// activity_place_details

		mW = (WebView) findViewById(R.id.wv_place_details);

		mW.getSettings().setUseWideViewPort(false);
		mW.getSettings().setDomStorageEnabled(true);
		String str = "https://docs.google.com/forms/d/1MxkIYkJtOubKlJvLaP8qG3alfCrrBNPEPhmTF-PknCQ/viewform?c=0&w=1&usp=mail_form_link";
		mW.loadUrl(str);
		mW.getSettings().setJavaScriptEnabled(true);
		mW.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url)
			{
				view.loadUrl("javascript:document.getElementById('entry_245853538').value = '" + from + "';" + "document.getElementById('entry_245853538').readOnly=true;"); // from address
				view.loadUrl("javascript:document.getElementById('entry_1757432097').value = '" + to + "';" + "document.getElementById('entry_1757432097').readOnly=true;"); // to address
				view.loadUrl("javascript:document.getElementById('entry_102442068').value = '" + dist + "';" + "document.getElementById('entry_102442068').readOnly=true;"); // distance
				view.loadUrl("javascript:var x= document.getElementById('entry_1026082941').value = '" + auto + "';" + "document.getElementById('entry_1026082941').readOnly=true;"); // rate from app
																																														// auto
				view.loadUrl("javascript:document.getElementById('entry_1483188071').value = '" + taxi + "';var x= document.getElementById('entry_1483188071').readOnly=true;"); // rate from app
																																													// taxi
				view.loadUrl("javascript:document.getElementById('entry_8625394').value='" + auto1 + "';"); // rate asked
				view.loadUrl("javascript:document.getElementById('entry_805347808').value= '" + Globals.area + "';"); // license plate
				view.loadUrl("javascript:document.getElementById('entry_728435400').value='" + Globals.phoneno + "';"); // phone no
				view.loadUrl("javascript:document.getElementById('navigation-buttons').style.visibility='hidden';");
				view.loadUrl("javascript:document.getElementById('ss-submit').style.visibility='visible';");
				view.loadUrl("javascript:document.getElementsByClassName('ss-footer')[0].style.display='none';");
				view.loadUrl("javascript:document.getElementsByClassName('ss-footer-content')[0].style.display='none';");

			}
		});

	}
}
