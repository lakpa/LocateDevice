package com.dissertation.project;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class MollyRenderedActivity extends Activity {
//	private WebView browser = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebView webview = new WebView(this);
		 setContentView(webview);
//		browser = (WebView) findViewById(R.id.webKit);
		webview.loadUrl("http://192.168.1.5:8000/search/?q=beginning");
//		webview.loadUrl("http://www.google.com");
	}
}
