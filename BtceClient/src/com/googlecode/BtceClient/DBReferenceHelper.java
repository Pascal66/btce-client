package com.googlecode.BtceClient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBReferenceHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "reference.db";
	private static final int DATABASE_VERSION = 1;

	static String atorder_table = "auto_orders";
	static String setting_table = "setting";

	public DBReferenceHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ atorder_table
				+ "(name TEXT PRIMARY KEY, p1x REAL, p1y REAL, p2x REAL, p2y REAL, p3x REAL, "
				+ "p3y REAL, p4x REAL, p4y REAL, p5x REAL, p5y REAL, type INTEGER, "
				+ "piece INTEGER, ffrom INTEGER, tto INTEGER, cur INTEGER, sleep REAL)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + setting_table
				+ "(name TEXT PRIMARY KEY, value TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
