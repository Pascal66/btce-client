package com.btce.database;

import com.btce.api.BTCEPairs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBDepthHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "depth.db";
	private static final int DATABASE_VERSION = 5;

	public DBDepthHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		BTCEPairs p = new BTCEPairs();
		for (String pair : p.keySet()) {
			String sql_string = "CREATE TABLE IF NOT EXISTS " + pair
					+ "(_id INTEGER PRIMARY KEY, depth TEXT)";
			// Log.e("SQL", sql_string);
			db.execSQL(sql_string);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (5 > oldVersion) {
			onCreate(db);
			oldVersion = 5;
		}
	}

}
