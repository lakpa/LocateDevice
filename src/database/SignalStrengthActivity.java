package database;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author lakpa
 * The Class SignalStrengthActivity.
 */
public class SignalStrengthActivity extends Activity {

	// Our application database
	/** The m database. */
	protected SignalStrengthDatabaseHelper mDatabase = null;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDatabase = new SignalStrengthDatabaseHelper(this.getApplicationContext());
	
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDatabase != null) {
			mDatabase.close();
		}
	}
}
