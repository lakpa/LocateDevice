package database;

import database.SignalStrengthDatabase.SignalStrength;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author lakpa
 * This class handles the creation and versioning of the application database.
 */

public class SignalStrengthDatabaseHelper extends SQLiteOpenHelper {

	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "dissertation.db";
	
	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Instantiates a new signal strength database helper.
	 *
	 * @param context the context
	 */
	public SignalStrengthDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
	
		// Create the ss_table table
		db.execSQL("CREATE TABLE "+ SignalStrength.SIGNAL_STRENGTH_TABLE_NAME+" ("
				+ SignalStrength._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ SignalStrength.SIGNAL_STRENGTH1+" INTEGER, "+SignalStrength.SIGNAL_STRENGTH2+" INTEGER, "
				+ SignalStrength.SIGNAL_STRENGTH3+" INTEGER, "+ SignalStrength.CLASSIFICATION+" TEXT, "
				+ SignalStrength.DATE_STAMP+" INTEGER);");
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Housekeeping here.
		// Implement how "move" your application data during an upgrade of schema versions		
		// There is no ALTER TABLE command in SQLite, so this generally involves
		// CREATING a new table, moving data if possible, or deleting the old data and starting fresh
		// Your call.
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
}
