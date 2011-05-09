package com.dissertation.project;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * @author lakpa The Class MollyRenderedActivity.
 */
public class MollyRenderedActivity extends Activity {

	/** The location_info. */
	private String location_info = "";

	/** The url to molly. */
	private static String urlToMolly = "http://192.168.1.5:8000/location_info_renderer/?q=";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebView webview = new WebView(this);
		setContentView(webview);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			location_info = extras.getString("location_info");
		}
		webview.loadUrl(urlToMolly + location_info);
	}
}
