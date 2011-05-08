package com.dissertation.project;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * @author lakpa
 * The Class MollyRenderedActivity.
 */
public class MollyRenderedActivity extends Activity {
	/* (non-Javadoc)
 * @see android.app.Activity#onCreate(android.os.Bundle)
 */
@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebView webview = new WebView(this);
		setContentView(webview);
		webview.loadUrl("http://192.168.1.5:8000/search/?q=beginning");

	}
}
