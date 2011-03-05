package com.dissertation.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlSerializer;
import android.app.Activity;
import android.util.Log;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends Activity {

	// Member fields
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;
	public static final String TOAST = "toast";
	private static final int REQUEST_ENABLE_BT = 2;
	private Button scanButton;
	private ListView newDevicesListView;
	private Date now = null;
	private String startingTime = "";
	private String scanFinishTime = "";
	private int fileNumber = 0;
	private ProgressDialog progressDialog = null;
	private short rssiValue = 0;
	private static final String DEBUG_TAG = "Logging: ";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(DEBUG_TAG, "Starting onCreate method");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);

		// Find and set up the ListView for newly discovered devices
		newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {

				// Get the device MAC address, which is the last 17 chars in the
				// View
				String info = ((TextView) view).getText().toString();
				// String address = info.substring(info.length());
				String[] coordinate = getCoordinate(info);

				Bundle bundle = new Bundle();
				bundle.putDouble("latitude", Double.parseDouble(coordinate[0]));
				bundle.putDouble("longtitude", Double
						.parseDouble(coordinate[1]));
				Intent myIntent = new Intent(view.getContext(),
						MapsActivity.class);
				myIntent.putExtras(bundle);
				startActivityForResult(myIntent, 0);
			}
		});

		scanButton = (Button) findViewById(R.id.scan);
		scanButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// newDevicesListView.clearChoices();
//				if (mBtAdapter.isEnabled()) {
					mNewDevicesArrayAdapter.clear();
					newDevicesListView.clearChoices();
					fileNumber++;
					doDiscovery();
//				} else {
//					new AlertDialog.Builder(Main.this).setTitle("Bluetooth state message").setMessage(
//							"Bluetooth is not enabled\nWould you like to enable it?").setPositiveButton("Ok", dialogOnClickListener).setNegativeButton("No", dialogOnClickListener).show();
//				}
			}
		});
	}

	private DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			switch(which) {
				case DialogInterface.BUTTON_POSITIVE:
					onStart();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
			}
		}
	};

	private String[] getCoordinate(String val) {
		String[] rtVal = new String[2];
		String[] address = val.split(" ");
		String longtitude = address[1];
		String latitude = address[2];
		rtVal[0] = longtitude;
		rtVal[1] = latitude;
		return rtVal;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(DEBUG_TAG, "Executing onStart..");
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBtAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		}
	}

	/**
	 * Start device discover with the BluetoothAdapter
	 */
	private void doDiscovery() {
		Log.i(DEBUG_TAG, "Executing doDiscovery..");
		if (D)
			Log.d(TAG, "doDiscovery()");
		// Indicate scanning in the title
		// setProgressBarIndeterminateVisibility(true);

		// If we're already discovering, stop it
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		if (mBtAdapter.startDiscovery()) {
			now = new Date();
			startingTime = calculateTime(now);
		}
		progressDialog = ProgressDialog.show(this, "Device scan",
				"Scanning device....please wait!");
	}

	// method to create log file for bluetooth activity
	private boolean createLogFile(ArrayAdapter<String> arryAdapter) {
		int count = arryAdapter.getCount();
		File newXmlFile = new File(Environment.getExternalStorageDirectory()
				+ "/bluetooth_log" + fileNumber + ".xml");
		try {
			newXmlFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream fous = null;
		try {
			fous = new FileOutputStream(newXmlFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fous, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true));
			serializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			serializer.startTag(null, "DeviceInfo");
			serializer.startTag(null, "ScanStartingTime");
			serializer.text(startingTime);
			serializer.endTag(null, "ScanStartingTime");
			serializer.startTag(null, "ScanFinishedTime");
			serializer.text(scanFinishTime);
			serializer.endTag(null, "ScanFinishedTime");
			serializer.startTag(null, "DiscoveredDevices");
			for (int i = 0; i < count; i++) {
				serializer.startTag(null, "Device");
				String val = arryAdapter.getItem(i);
				String[] val1 = val.split(" ");
				/**
				 * String domain = val1[1]; serializer.startTag(null, "domain");
				 * serializer.text(domain); serializer.endTag(null, "domain");
				 *
				 * String longtitude = val1[2]; serializer.startTag(null,
				 * "longtitude"); serializer.text(longtitude);
				 * serializer.endTag(null, "longtitude"); String latitude =
				 * val1[3]; serializer.startTag(null, "latitude");
				 * serializer.text(latitude.trim()); serializer.endTag(null,
				 * "latitude"); String macAddress = val1[4];
				 * serializer.startTag(null, "macAddress");
				 * serializer.text(macAddress.trim()); serializer.endTag(null,
				 * "macAddress");
				 */
				String longtitude = val1[1];
				serializer.startTag(null, "Longtitude");
				serializer.text(longtitude);
				serializer.endTag(null, "Longtitude");
				String latitude = val1[2];
				serializer.startTag(null, "Latitude");
				serializer.text(latitude.trim());
				serializer.endTag(null, "Latitude");
				String macAddress = val1[3];
				serializer.startTag(null, "MacAddress");
				serializer.text(macAddress.trim());
				serializer.endTag(null, "MacAddress");
				String scanTime = val1[4];
				serializer.startTag(null, "ScannedTime");
				serializer.text(scanTime.trim());
				serializer.endTag(null, "ScannedTime");
				// String rssi = val1[5];
				// serializer.startTag(null, "RssiValue");
				// serializer.text(rssi.trim());
				// serializer.endTag(null, "RssiValue");
				serializer.endTag(null, "Device");
			}
			serializer.endTag(null, "DiscoveredDevices");
			serializer.endTag(null, "DeviceInfo");
			serializer.endDocument();
			// write xml data into the FileOutputStream
			serializer.flush();
			// finally close the file stream
			fous.close();
			Toast.makeText(this, "successfully created", Toast.LENGTH_LONG)
					.show();
		} catch (IllegalArgumentException e) {
			generateErrorFile("errorLog.txt", e.getMessage());
		} catch (IllegalStateException e) {
			generateErrorFile("errorLog.txt", e.getMessage());
		} catch (IOException e) {
			generateErrorFile("errorLog.txt", e.getMessage());
		}
		return true;
	}

	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				rssiValue = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
						Short.MIN_VALUE);

				// Add the name and address to an array adapter to show in a
				// ListView
				if (requiredData(device.getName())) {
					if (!isDuplicate(mNewDevicesArrayAdapter, device
							.getAddress())) {
						mNewDevicesArrayAdapter.add(device.getName() + "\n "
								+ device.getAddress() + " "
								+ calculateTime(new Date()) + " " + rssiValue);
					}
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				// setProgressBarIndeterminateVisibility(false);
				progressDialog.dismiss();
				if (mNewDevicesArrayAdapter.getCount() != 0) {
					scanFinishTime = calculateTime(new Date());
//					createLogFile(mNewDevicesArrayAdapter);
//					scanButton.setEnabled(true);
				}
			}
		}
	};

	private void sendEmail(String emailId, File f) {

	}

	private String calculateTime(Date d) {
		String second = "";
		String minute = "";
		if (d.getSeconds() < 10) {
			second = "0" + d.getSeconds();
		} else {
			second = String.valueOf(d.getSeconds());
		}
		if (d.getMinutes() < 10) {
			minute = "0" + d.getMinutes();
		} else {
			minute = String.valueOf(d.getMinutes());
		}
		return d.getHours() + ":" + minute + ":" + second;
	}

	// check duplicate device
	private boolean isDuplicate(ArrayAdapter<String> arryString,
			String deviceAddress) {
		int len = arryString.getCount();
		for (int i = 0; i < len; i++) {
			String device = arryString.getItem(i);
			String[] deviceInfo = device.split(" ");
			if (deviceInfo[3].trim().equals(deviceAddress)) {
				return true;
			}
		}
		return false;
	}

	// check whether device is required one
	private boolean requiredData(String name) {
		if (name.startsWith("+", 0) || name.startsWith("-", 0)) {
			return true;
		}
		return false;
	}

	private static void generateErrorFile(String sFileName, String contents) {
		try {
			File root = Environment.getExternalStorageDirectory();
			File gpxfile = new File(root, sFileName);
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(contents);
			// generate whatever data you want
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}