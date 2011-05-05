package com.dissertation.project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import database.SignalStrengthActivity;
import database.SignalStrengthDatabase.SignalStrength;

public class LocalDBResultActivity extends SignalStrengthActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_local_db_data);
		fillSSList();
		
	}
	
	public void fillSSList() {
		// TableLayout where we want to Display List
		final TableLayout ssTable = (TableLayout) findViewById(R.id.TableLayout_SSList);
		
		//SQL Query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(SignalStrength.SIGNAL_STRENGTH_TABLE_NAME);
		
		// Get the database and run the query
		SQLiteDatabase db = mDatabase.getReadableDatabase();
		Cursor c = queryBuilder.query(db, null, null, null, null, null, null);
		
		// Display the results by adding some TableRows to the existing table layout
		if (c.moveToFirst()) {
			for(int i=0; i < c.getCount(); i++) {
				TableRow newRow = new TableRow(this);
				TextView sequenceCol = new TextView(this);
				TextView ss1Col = new TextView(this);
				TextView ss2Col = new TextView(this);
				TextView ss3Col = new TextView(this);
				TextView classificationCol = new TextView(this);
				TextView addedDateCol = new TextView(this);
				Button deleteButton = new Button(this);
				newRow.setTag(c.getInt(c.getColumnIndex(SignalStrength._ID)));
				sequenceCol.setText(String.valueOf(c.getInt(0)));
				ss1Col.setText(String.valueOf(c.getInt(1)));
				ss2Col.setText(String.valueOf(c.getInt(2)));
				ss3Col.setText(String.valueOf(c.getInt(3)));
				classificationCol.setText(c.getString(4));
				String date = String.valueOf(c.getInt(5));
				String year = "", month="", day="";
				char[] dateAry = date.toCharArray();
				for(int j=0; j<date.length(); j++) {
					
					if (j < 4) {
						year +=dateAry[j];
					} else if (j>3 && j<6) {
						month +=dateAry[j];
					} else if (j > 5){
						day+=dateAry[j];
					}
				}
				addedDateCol.setText(year+"/"+month+"/"+day);
				
				deleteButton.setText("Delete");
				deleteButton.setTag(c.getInt(c.getColumnIndex(SignalStrength._ID)));		// set the tag field on the button so we know which ID to delete
				deleteButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						
						Integer id = (Integer) v.getTag();
						deleteSSRecord(id);
						
						// Find and destroy the row tagged with the deleted pet id in the Table 
						final TableLayout ssTable = (TableLayout) findViewById(R.id.TableLayout_SSList);
						// This should return the TableRow as the first tagged view in the layout but it would be nice if it returned an array of views with that tag...
						View viewToDelete = ssTable.findViewWithTag(id);
						ssTable.removeView(viewToDelete);
					}
				});
				newRow.addView(ss1Col);
				newRow.addView(ss2Col);
				newRow.addView(ss3Col);
				newRow.addView(classificationCol);
				newRow.addView(addedDateCol);
				newRow.addView(deleteButton);
				ssTable.addView(newRow);
				c.moveToNext();
			}
		} else {
			TableRow newRow = new TableRow(this);
			TextView noResults = new TextView(this);
			noResults.setText("No results to show.");
			newRow.addView(noResults);
			ssTable.addView(newRow);
		}
		c.close();
		db.close();
	}
	
	
	public void deleteSSRecord(Integer id)
	{
        SQLiteDatabase db = mDatabase.getWritableDatabase();
		String astrArgs[] = { id.toString() };
        db.delete(SignalStrength.SIGNAL_STRENGTH_TABLE_NAME, SignalStrength._ID + "=?",astrArgs );
        db.close();
	}

}
