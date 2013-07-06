package com.googlecode.BtceClient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHistroyHelper extends SQLiteOpenHelper {

	// private static String DATABASE_NAME = null;
	private static final int DATABASE_VERSION = 1;
	static String trans_table = "trans_histroy";
	static String trade_table = "trade_histroy";
	static String order_table = "order_histroy";

	public DBHistroyHelper(Context context, String DATABASE_NAME) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ trans_table
				+ "(_id INTEGER PRIMARY KEY, type INTEGER, amount REAL, currency TEXT, desc TEXT, status INTEGER, time INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ trade_table
				+ "(_id INTEGER PRIMARY KEY, pair TEXT, type TEXT, amount REAL, rate REAL, order_id INTEGER,is_your_order NUMERIC, time INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ order_table
				+ "(_id INTEGER PRIMARY KEY, pair TEXT, type TEXT, amount REAL, rate REAL, status INTEGER, time INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}