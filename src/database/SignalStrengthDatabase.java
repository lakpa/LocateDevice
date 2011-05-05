package database;

import android.provider.BaseColumns;

public final class SignalStrengthDatabase {

	private SignalStrengthDatabase() {}
	
	public static final class SignalStrength implements BaseColumns {
		private SignalStrength() {}
		public static final String SIGNAL_STRENGTH_TABLE_NAME = "ss_table";
//		public static final String SIGNAL_STRENGTH_ID = "ss_id";
		public static final String SIGNAL_STRENGTH1 = "signal_strength1";
		public static final String SIGNAL_STRENGTH2 = "signal_strength2";
		public static final String SIGNAL_STRENGTH3 = "signal_strength3";
		public static final String DATE_STAMP = "datestamp";
		public static final String CLASSIFICATION = "classification";
	}
	
}
