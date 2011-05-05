package com.dissertation.project;

//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import support.BluetoothDeviceModel;
import support.DataKeys;
import support.KNNModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import database.LocalKNNMain;
import database.SignalStrengthActivity;
import database.SignalStrengthDatabase.SignalStrength;

/**
 * @author lakpa
 * 
 */
public class Main extends SignalStrengthActivity {

	// Member fields
	private BluetoothAdapter mBtAdapter = null;
	private ArrayAdapter<String> mNewDevicesArrayAdapter = null;
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;
	public static final String TOAST = "toast";
	private static final int REQUEST_ENABLE_BT = 2;
	private Button scanButton = null;
	private Button findLocationButton = null;
	private Button syncButton = null;
	private ListView newDevicesListView = null;
	// private Date now = null;
	// private String startingTime = "";
	// private String scanFinishTime = "";
//	private int fileNumber = 0;
	private ProgressDialog progressDialog = null;
	private short rssiValue = 0;
	private static final String DEBUG_TAG = "Logging: ";
	private TextView locationInfoView = null;
	// private Button resetButton = null;
	private List<BluetoothDeviceModel> bluetoothDeviceInfoList = null;
	private SQLiteDatabase db = null;
	private List<KNNModel> localDBList = null;
	private KNNModel kModel = null, km = null;
	private static final String serviceUri = "http://192.168.1.5:8080/LocationService";
	private SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	private List<KNNModel> syncDBList = null;
	private RadioGroup group = null;
	private int selectedRadiobuttonId = 0;
	private Button showLocalDBButton = null;
	private Vibrator vibrate = null;
	private static long milliseconds = 1000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(DEBUG_TAG, "Starting onCreate method");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// get Device Vibrator
		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		bluetoothDeviceInfoList = new ArrayList<BluetoothDeviceModel>();

		// Remove it
//		bluetoothDeviceInfoList.add(new BluetoothDeviceModel("56",
//				"00:19:0E:08:04:EA"));
//		bluetoothDeviceInfoList.add(new BluetoothDeviceModel("51",
//				"00:19:0E:08:08:B7")); // room2
//		bluetoothDeviceInfoList.add(new BluetoothDeviceModel("56",
//				"00:19:0E:08:06:F6")); // room3

		// If the adapter is null, then Bluetooth is not supported
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		getDatabase();

		// addVectors();

		locationInfoView = (TextView) findViewById(R.id.locationInfoView);

		group = (RadioGroup) findViewById(R.id.radioGroup);
		group.check(R.id.rd_Disable);

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
				bundle.putDouble("longtitude",
						Double.parseDouble(coordinate[1]));
				callActivity(view.getContext(), bundle, MapsActivity.class);
			}
		});

		findLocationButton = (Button) findViewById(R.id.findPosition);
		findLocationButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				locationInfoView.setText("");

				// check which radio button is enable
				if (isServerRadiobuttonEnabled()) {
					callLocationFinderService(getBluetoothDeviceInfoList(),
							null, Main.this, serviceUri + "/finder", false);
				} else {
					callLocalDB(arg0);
				}
			}
		});

		syncButton = (Button) findViewById(R.id.sync);
		syncButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

//				progressDialog = ProgressDialog.show(arg0.getContext(),
//						"Database Syncronization",
//						"Database Synchronizing....please wait");
				callLocationFinderService(null,
						String.valueOf(getLatestUpdateDate()), Main.this,
						serviceUri + "/databaseSync", true);
			}
		});

		showLocalDBButton = (Button) findViewById(R.id.showLocalDB);
		showLocalDBButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				callActivity(v.getContext(), null, LocalDBResultActivity.class);
			}
		});

		scanButton = (Button) findViewById(R.id.scan);
		scanButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// newDevicesListView.clearChoices();
				if (mBtAdapter.isEnabled()) {
					locationInfoView.setText("");
					mNewDevicesArrayAdapter.clear();
					newDevicesListView.clearChoices();
					// fileNumber++;
					doDiscovery();
				} else {
					new AlertDialog.Builder(Main.this)
							.setTitle("Bluetooth state message")
							.setMessage(
									"Bluetooth is not enabled\nWould you like to enable it?")
							.setPositiveButton("Ok", dialogOnClickListner)
							.setNegativeButton("No", dialogOnClickListner)
							.show();
				}
			}
		});
	}

	private DialogInterface.OnClickListener dialogOnClickListner = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			if (DialogInterface.BUTTON_POSITIVE == which)
				enableBluetooth();
		}
	};

	private void callActivity(Context ct, Bundle bundle, Class<?> c) {
		Intent myIntent = new Intent(ct, c);
		if (bundle != null)
			myIntent.putExtras(bundle);

		startActivityForResult(myIntent, 0);
	}

	private boolean isServerRadiobuttonEnabled() {
		selectedRadiobuttonId = group.getCheckedRadioButtonId();
		if (selectedRadiobuttonId == R.id.rd_enable)
			return true;
		else
			return false;
	}

	private boolean callLocationFinderService(
			List<BluetoothDeviceModel> bluetoothDeviceList, String largest,
			Activity activity, String uri, boolean isDBSync) {
		new GetDataFromLocationService(uri, isDBSync).execute(
				bluetoothDeviceList, largest, (Main) activity);
		return true;
	}

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
		
		enableBluetooth();
	}

	private boolean enableBluetooth() {
		if (!mBtAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			return true;
		}
		return false;
	}

	public void setServiceData(Hashtable<DataKeys, Serializable> info,
			boolean isDBSyncData) {
		if (!isDBSyncData) {
			String data = getStringValue(info, DataKeys.message,
					"No Location Info");
			if (!data.equals("")) {
				vibrate.vibrate(milliseconds);
//				callActivity(this.getApplicationContext(), null, MollyRenderedActivity.class);
				locationInfoView.setText(data);
			}
			data = "";
		} else {
			int rec_count = dbSynchronized(info);
			if (rec_count > 0) {
//				progressDialog.dismiss();
				Toast.makeText(this, rec_count + " record updated",
						Toast.LENGTH_LONG).show();
			} else {
//				progressDialog.dismiss();
				Toast.makeText(this, "Data is up to date", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	private void callLocalDB(View v) {
		LocalKNNMain lkm = new LocalKNNMain();
		KNNModel km = new KNNModel();

		if (bluetoothDeviceInfoList.size() == 3) {
			for (int i = 0; i < bluetoothDeviceInfoList.size(); i++) {
				if (bluetoothDeviceInfoList.get(i).getMacAddress()
						.equals("00:19:0E:08:04:EA")) {
					km.setRoom2(Integer.parseInt(bluetoothDeviceInfoList.get(i)
							.getSignalStrength()));
				} else if (bluetoothDeviceInfoList.get(i).getMacAddress()
						.equals("00:19:0E:08:08:B7")) {
					km.setRoom1(Integer.parseInt(bluetoothDeviceInfoList.get(i)
							.getSignalStrength()));
				} else if (bluetoothDeviceInfoList.get(i).getMacAddress()
						.equals("00:19:0E:08:06:F6")) {
					km.setRoom3(Integer.parseInt(bluetoothDeviceInfoList.get(i)
							.getSignalStrength()));
				}
			}
		}

		bluetoothDeviceInfoList.clear();
		String category = lkm.classifiedQueryInstance(query(), km, 2);

		if (!category.equals("")) {
			vibrate.vibrate(milliseconds);
//			callActivity(v.getContext(), null, MollyRenderedActivity.class);
			locationInfoView.setText(category);
		} else {
			locationInfoView.setText("No Location Info");
		}
		category = "";
	}

	private int dbSynchronized(Hashtable<DataKeys, Serializable> info) {
		int count = 0;
		KNNModel kModel = null;
		kModel = getKNNModelValue(info, DataKeys.dataList, "message");
		db.beginTransaction();
		for (int i = 0; i < kModel.getKnnList().size(); i++) {
			KNNModel km1 = kModel.getKnnList().get(i);
			insertToLocalDB(km1);
			count++;
		}
		kModel.setKnnList(null);
		db.setTransactionSuccessful();
		db.endTransaction();
		return count;
	}

	private String getStringValue(Hashtable<DataKeys, Serializable> up,
			DataKeys key, String defaultValue) {
		try {
			return up.get(key).toString();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private KNNModel getKNNModelValue(Hashtable<DataKeys, Serializable> up,
			DataKeys key, String defaultValue) {
		try {
			return (KNNModel) up.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	private int getLatestUpdateDate() {

		// SQL Query
		queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(SignalStrength.SIGNAL_STRENGTH_TABLE_NAME);
		Cursor c = queryBuilder.query(db, null, null, null, null, null, null);

		c.moveToFirst();
		int recordCount = c.getCount();
		if (recordCount != 0) {
			int[] iArray = new int[c.getCount()];

			int index = 0;
			while (!c.isAfterLast()) {
				int d = c.getInt(5);
				iArray[index] = d;
				c.moveToNext();
				index++;
			}
			int j = findLatest(iArray);
			return iArray[j];
		} else {
			return 0;
		}
	}

	public int findLatest(int... val) {
		int largest = val[0];
		int index = 0;
		for (int i = 0; i < val.length; i++) {
			if (i != 0) {
				if (val[i] > largest) {
					largest = val[i];
					index = i;
				}
			}
		}
		return index;
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
			// now = new Date();
			// startingTime = calculateTime(now);
//			progressDialog = ProgressDialog.show(this, "Device scan",
//					"Scanning device....please wait!");
			scanButton.setEnabled(false);
		}
	}

	// method to create log file for bluetooth activity
	/**
	 * private boolean createLogFile(ArrayAdapter<String> arryAdapter) { int
	 * count = arryAdapter.getCount(); File newXmlFile = new
	 * File(Environment.getExternalStorageDirectory() + "/bluetooth_log" +
	 * fileNumber + ".xml"); try { newXmlFile.createNewFile(); } catch
	 * (IOException e) { e.printStackTrace(); } FileOutputStream fous = null;
	 * try { fous = new FileOutputStream(newXmlFile); } catch
	 * (FileNotFoundException e) { e.printStackTrace(); }
	 * 
	 * XmlSerializer serializer = Xml.newSerializer(); try {
	 * serializer.setOutput(fous, "UTF-8"); serializer.startDocument(null,
	 * Boolean.valueOf(true)); serializer.setFeature(
	 * "http://xmlpull.org/v1/doc/features.html#indent-output", true);
	 * serializer.startTag(null, "DeviceInfo"); serializer.startTag(null,
	 * "ScanStartingTime"); serializer.text(startingTime);
	 * serializer.endTag(null, "ScanStartingTime"); serializer.startTag(null,
	 * "ScanFinishedTime"); serializer.text(scanFinishTime);
	 * serializer.endTag(null, "ScanFinishedTime"); serializer.startTag(null,
	 * "DiscoveredDevices"); for (int i = 0; i < count; i++) {
	 * serializer.startTag(null, "Device"); String val = arryAdapter.getItem(i);
	 * String[] val1 = val.split(" ");
	 * 
	 * String domain = val1[1]; serializer.startTag(null, "domain");
	 * serializer.text(domain); serializer.endTag(null, "domain");
	 * 
	 * String longtitude = val1[2]; serializer.startTag(null, "longtitude");
	 * serializer.text(longtitude); serializer.endTag(null, "longtitude");
	 * String latitude = val1[3]; serializer.startTag(null, "latitude");
	 * serializer.text(latitude.trim()); serializer.endTag(null, "latitude");
	 * String macAddress = val1[4]; serializer.startTag(null, "macAddress");
	 * serializer.text(macAddress.trim()); serializer.endTag(null,
	 * "macAddress");
	 * 
	 * String longtitude = val1[1]; serializer.startTag(null, "Longtitude");
	 * serializer.text(longtitude); serializer.endTag(null, "Longtitude");
	 * String latitude = val1[2]; serializer.startTag(null, "Latitude");
	 * serializer.text(latitude.trim()); serializer.endTag(null, "Latitude");
	 * String macAddress = val1[3]; serializer.startTag(null, "MacAddress");
	 * serializer.text(macAddress.trim()); serializer.endTag(null,
	 * "MacAddress"); String scanTime = val1[4]; serializer.startTag(null,
	 * "ScannedTime"); serializer.text(scanTime.trim()); serializer.endTag(null,
	 * "ScannedTime"); // String rssi = val1[5]; // serializer.startTag(null,
	 * "RssiValue"); // serializer.text(rssi.trim()); // serializer.endTag(null,
	 * "RssiValue"); serializer.endTag(null, "Device"); }
	 * serializer.endTag(null, "DiscoveredDevices"); serializer.endTag(null,
	 * "DeviceInfo"); serializer.endDocument(); // write xml data into the
	 * FileOutputStream serializer.flush(); // finally close the file stream
	 * fous.close(); Toast.makeText(this, "successfully created",
	 * Toast.LENGTH_LONG) .show(); } catch (IllegalArgumentException e) {
	 * generateErrorFile("errorLog.txt", e.getMessage()); } catch
	 * (IllegalStateException e) { generateErrorFile("errorLog.txt",
	 * e.getMessage()); } catch (IOException e) {
	 * generateErrorFile("errorLog.txt", e.getMessage()); } return true; }
	 **/

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
					if (!isDuplicate(mNewDevicesArrayAdapter,
							device.getAddress())) {
						mNewDevicesArrayAdapter.add(device.getName() + "\n "
								+ device.getAddress() + " "
								+ calculateTime(new Date()) + " " + rssiValue);
						String rssiVal = String.valueOf(rssiValue);
						// rssiVal = rssiVal.substring(1, rssiVal.length());
						rssiVal = String.valueOf(Math.abs(Integer
								.parseInt(rssiVal)));
						 bluetoothDeviceInfoList.add(new BluetoothDeviceModel(
						 rssiVal, device.getAddress()));
					}
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
//				progressDialog.dismiss();
				if (mNewDevicesArrayAdapter.getCount() != 0) {
					// scanFinishTime = calculateTime(new Date());
					// createLogFile(mNewDevicesArrayAdapter);
				}
				scanButton.setEnabled(true);
			}
		}
	};

	public List<BluetoothDeviceModel> getBluetoothDeviceInfoList() {
		return bluetoothDeviceInfoList;
	}

	public void setBluetoothDeviceInfoList(
			List<BluetoothDeviceModel> bluetoothDeviceInfoList) {
		this.bluetoothDeviceInfoList = bluetoothDeviceInfoList;
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

	public void addVectors() {
		localDBList = new ArrayList<KNNModel>();
		db.beginTransaction();

		localDBList.add(addSSVector(new SSVector("58", "62", "43", "In TC364",
				"20110416")));
		localDBList.add(addSSVector(new SSVector("63", "49", "56",
				"Near TC357", "20110416")));
		localDBList.add(addSSVector(new SSVector("47", "57", "60",
				"Near TC360", "20110416")));
		localDBList.add(addSSVector(new SSVector("56", "65", "64",
				"Near TC354", "20110415")));
		localDBList.add(addSSVector(new SSVector("60", "34", "60", "In TC357",
				"20110416")));

		try {
			for (int i = 0; i < localDBList.size(); i++) {
				KNNModel km = localDBList.get(i);

				// check if species type exists already
				int strDate = km.getDate();
				String clasf = km.getClassification();

				// SQL Query
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder
						.setTables(SignalStrength.SIGNAL_STRENGTH_TABLE_NAME);
				queryBuilder.appendWhere(SignalStrength.DATE_STAMP + "="
						+ strDate + " and " + SignalStrength.CLASSIFICATION
						+ "='" + clasf + "'");

				// run the query
				Cursor c = queryBuilder.query(db, null, null, null, null, null,
						null);
				if (c.getCount() == 0) {
					insertToLocalDB(km);
				}
				c.close();
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	private void insertToLocalDB(KNNModel km) {
		ContentValues typeRecordToAdd = new ContentValues();
		typeRecordToAdd.put(SignalStrength.SIGNAL_STRENGTH1, km.getRoom1());
		typeRecordToAdd.put(SignalStrength.SIGNAL_STRENGTH2, km.getRoom2());
		typeRecordToAdd.put(SignalStrength.SIGNAL_STRENGTH3, km.getRoom3());
		typeRecordToAdd.put(SignalStrength.CLASSIFICATION,
				km.getClassification());
		typeRecordToAdd.put(SignalStrength.DATE_STAMP, km.getDate());
		db.insert(SignalStrength.SIGNAL_STRENGTH_TABLE_NAME, null,
				typeRecordToAdd);
	}

	private void getDatabase() {
		db = mDatabase.getWritableDatabase();
	}

	public List<KNNModel> query() {
		List<KNNModel> returnList = new ArrayList<KNNModel>();
		queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(SignalStrength.SIGNAL_STRENGTH_TABLE_NAME);
		Cursor c = queryBuilder.query(db, null, null, null, null, null, null);

		// move cursor to first row
		c.moveToFirst();

		while (!c.isAfterLast()) {
			km = new KNNModel();
			km.setRoom1(c.getInt(1));
			km.setRoom2(c.getInt(2));
			km.setRoom3(c.getInt(3));
			km.setClassification(c.getString(4));
			km.setDate(c.getInt(5));
			returnList.add(km);
			c.moveToNext();
		}
		// Log.i(DEBUG_TAG, "*** Cursor End ***");
		return returnList;
	}

	private KNNModel addSSVector(SSVector ssv) {
		kModel = new KNNModel();

		// adding local DB contents to list to be sent to remote DB for cross
		// checking
		kModel.setRoom1(Integer.parseInt(ssv.signalStrength1));
		kModel.setRoom2(Integer.parseInt(ssv.signalStrength2));
		kModel.setRoom3(Integer.parseInt(ssv.signalStrength3));
		kModel.setClassification(ssv.classification);
		kModel.setDate(Integer.parseInt(ssv.date));
		return kModel;
	}

	public void setSyncDBList(List<KNNModel> syncDBList) {
		this.syncDBList = syncDBList;
	}

	public List<KNNModel> getSyncDBList() {
		return syncDBList;
	}

	// Helper class to encapsulate SSVector information programmatically
	class SSVector {
		String signalStrength1;
		String signalStrength2;
		String signalStrength3;
		String classification;
		String date;

		public SSVector(String signalStrength1, String signalStrength2,
				String signalStrength3, String classification, String date) {
			this.signalStrength1 = signalStrength1;
			this.signalStrength2 = signalStrength2;
			this.signalStrength3 = signalStrength3;
			this.classification = classification;
			this.date = date;
		}
	}
}