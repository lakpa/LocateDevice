package database;

import android.provider.BaseColumns;

/**
 * @author lakpa
 * The Class SignalStrengthDatabase.
 */
public final class SignalStrengthDatabase {

	/**
	 * Instantiates a new signal strength database.
	 */
	private SignalStrengthDatabase() {}
	
	/**
	 * The Class SignalStrength.
	 */
	public static final class SignalStrength implements BaseColumns {
		
		/**
		 * Instantiates a new signal strength.
		 */
		private SignalStrength() {}
		
		/** The Constant SIGNAL_STRENGTH_TABLE_NAME. */
		public static final String SIGNAL_STRENGTH_TABLE_NAME = "ss_table";
//		public static final String SIGNAL_STRENGTH_ID = "ss_id";
		/** The Constant SIGNAL_STRENGTH1. */
public static final String SIGNAL_STRENGTH1 = "signal_strength1";
		
		/** The Constant SIGNAL_STRENGTH2. */
		public static final String SIGNAL_STRENGTH2 = "signal_strength2";
		
		/** The Constant SIGNAL_STRENGTH3. */
		public static final String SIGNAL_STRENGTH3 = "signal_strength3";
		
		/** The Constant DATE_STAMP. */
		public static final String DATE_STAMP = "datestamp";
		
		/** The Constant CLASSIFICATION. */
		public static final String CLASSIFICATION = "classification";
	}
	
}
