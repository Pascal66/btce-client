package com.googlecode.BtceClient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBTradesHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "trades.db";
	private static final int DATABASE_VERSION = 1;

	public DBTradesHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		BTCEPairs p = new BTCEPairs();
		for (String pair : p.keySet()) {
			String sql_string = "CREATE TABLE IF NOT EXISTS "
					+ pair
					+ "(_id INTEGER PRIMARY KEY, price REAL, amount REAL, date INTEGER, trade_type INTEGER)";
			//Log.e("SQL", sql_string);
			db.execSQL(sql_string);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
