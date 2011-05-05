package database;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author lakpa
 *
 */
public class SignalStrengthActivity extends Activity {

	// Our application database
	protected SignalStrengthDatabaseHelper mDatabase = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDatabase = new SignalStrengthDatabaseHelper(this.getApplicationContext());
	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDatabase != null) {
			mDatabase.close();
		}
	}
}
