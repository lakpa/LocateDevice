package com.dissertation.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


import support.BluetoothDeviceModel;
import support.DataKeys;
import support.MIMETypeConstantsIF;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * The Class GetDataFromLocationService.
 * @author lakpa
 * @author Nazmul Idris
 *
 */
public class GetDataFromLocationService {
	
	/** The service uri. */
	private String serviceUri = "";
	
	/** The network connection timeout_ms. */
	private int networkConnectionTimeout_ms = 500000;
	
	/** The activity. */
	private Main activity;
	
	/** The data from servlet. */
	private Hashtable<DataKeys, Serializable> dataFromServlet = null;
	
	/** The bluetooth device list. */
	private List<BluetoothDeviceModel> bluetoothDeviceList = null;
	
	/** The latest updated date. */
	private String latestUpdatedDate= "";
	
	/** The ex. */
	private Exception ex;
	
	/** The is db sync. */
	private boolean isDBSync = false;
	
	/** The hash map. */
	private Hashtable<String, List<BluetoothDeviceModel>> hashMap = null;
	
	/** The hash map2. */
	private Hashtable<String, String> hashMap2 = null;
//	private Hashtable<String, List<KNNModel>> hashMap2 = null;
//	private List<KNNModel> localDBList = null;
	
	/**
 * Instantiates a new gets the data from location service.
 *
 * @param serviceUri the service uri
 * @param isDBSync the is db sync
 */
public GetDataFromLocationService(String serviceUri, boolean isDBSync) {
		this.serviceUri = serviceUri;
		this.isDBSync = isDBSync;
	}
	
	/**
	 * Execute.
	 *
	 * @param bluetoothDeviceList the bluetooth device list
	 * @param latestUpdatedDate the latest updated date
	 * @param activity the activity
	 */
	public void execute(List<BluetoothDeviceModel> bluetoothDeviceList, String latestUpdatedDate, 
			Main activity) {
//		public void execute(List<BluetoothDeviceModel> bluetoothDeviceList, List<KNNModel> dbLocalList, 
//				Main activity) {	
		
		this.activity = activity;
		
		if (bluetoothDeviceList != null) {
			this.bluetoothDeviceList = bluetoothDeviceList;
		}
		
//		if (dbLocalList != null)
//			this.localDBList = dbLocalList;
		
		this.latestUpdatedDate = latestUpdatedDate;
		
		final Handler threadCallBack = new Handler();
		final Runnable runInUIThread = new Runnable() {

			public void run() {
				displayInUI();
			}
		};

		new Thread() {
			@Override
			public void run() {
				doInBackgroundPost();
				threadCallBack.post(runInUIThread);
			}
		}.start();
		if (!isDBSync)
			Toast.makeText(activity, "Getting data from servlet",
				Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Display in ui.
	 */
	private void displayInUI() {

		if (ex != null)
			Toast.makeText(
					activity,
					ex.getMessage() == null ? "Error" : "Error - "
							+ ex.getMessage(), Toast.LENGTH_SHORT).show();
		if (!isDBSync)			
			activity.setServiceData(dataFromServlet, false);
		else
			activity.setServiceData(dataFromServlet, true);
	}

	/**
	 * Do in background post.
	 */
	private void doInBackgroundPost() {
		Log.i(getClass().getSimpleName(), "start-background task");
		
		if (!isDBSync) {
			hashMap = new Hashtable<String, List<BluetoothDeviceModel>>();
			hashMap.put("deviceList", bluetoothDeviceList);
		} else {
			hashMap2 = new Hashtable<String, String>();
			hashMap2.put("latestUpdatedDate", latestUpdatedDate);
//			hashMap2 = new Hashtable<String, List<KNNModel>>();
//			hashMap2.put("dbLocalList", localDBList);
		}
		
		try {
			HttpParams httpParams = new BasicHttpParams();

			// set parameters for connection
			HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
			HttpConnectionParams.setConnectionTimeout(httpParams,
					networkConnectionTimeout_ms);
			HttpConnectionParams.setSoTimeout(httpParams,
					networkConnectionTimeout_ms);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

			// create post method
			HttpPost postMethod = new HttpPost(serviceUri);

			// create request entity
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			if (!isDBSync) {
				oos.writeObject(hashMap);
			} else {
				oos.writeObject(hashMap2);
			}
			ByteArrayEntity requestEntity = new ByteArrayEntity(
					baos.toByteArray());
			requestEntity.setContentType(MIMETypeConstantsIF.BINARY_TYPE);

			// associating entity with method
			postMethod.setEntity(requestEntity);

			// response
			httpClient.execute(postMethod, new ResponseHandler<Void>() {

				@SuppressWarnings("unchecked")
				public Void handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					HttpEntity responseEntity = response.getEntity();

					if (responseEntity != null) {
						
						try {
							byte[] data = EntityUtils.toByteArray(responseEntity);
							ObjectInputStream ois = new ObjectInputStream(
									new ByteArrayInputStream(data));
							dataFromServlet = (Hashtable<DataKeys, Serializable>) ois
									.readObject();
						} catch (ClassNotFoundException e) {
							Log.e(getClass().getSimpleName(),
									"Processing error ", e);
						}
					} else {
						throw new IOException(new StringBuffer()
								.append("HTTP response: ")
								.append(response.getStatusLine()).toString());
					}
					return null;
				}
			});
		} catch (Exception e) {
			ex = e;
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.e(getClass().getSimpleName(), sw.getBuffer().toString(), e);
		}
	}
}
