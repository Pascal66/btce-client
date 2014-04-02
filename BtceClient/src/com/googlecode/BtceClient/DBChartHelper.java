package com.googlecode.BtceClient;

import java.util.Vector;

import com.googlecode.BtceClient.HistroyActivity.trans_his_item;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBChartHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "chart_info.db";
	private static final int DATABASE_VERSION = 7;

	public DBChartHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		BTCEPairs p = new BTCEPairs();
		for (String pair : p.keySet()) {
			String sql_string = "CREATE TABLE IF NOT EXISTS "
					+ pair
					+ "(_id INTEGER PRIMARY KEY, open REAL, close REAL, high REAL, low REAL, volume REAL, volume_currency REAL, w_price REAL)";
			// Log.e("SQL", sql_string);
			db.execSQL(sql_string);
		}
	}

	// called if the version is changed
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		String query_str = "SELECT name FROM sqlite_master WHERE type='table' and name != 'android_metadata'";
		Cursor c = db.rawQuery(query_str, null);
		Vector<String> tables = new Vector<String>();
		while (c.moveToNext()) {
			tables.add(c.getString(0));
			Log.e("table: ", c.getString(0));
		}

		switch (oldVersion) {
		case 1: {
			db.beginTransaction();
			try {
				for (String tb : tables) {
					query_str = "alter table " + tb + " add \"volume\" REAL";
					db.execSQL(query_str);
					query_str = "alter table " + tb
							+ " add \"volume_currency\" REAL";
					db.execSQL(query_str);
					query_str = "alter table " + tb + " add \"w_price\" REAL";
					db.execSQL(query_str);
				}
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
		case 2: {
			db.beginTransaction();
			try {
				query_str = "DELETE FROM btc_usd";
				db.execSQL(query_str);
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
		case 3:
		case 4:
		case 5:
		case 6:{
			// add pairs
			onCreate(db);
		}
		default:
			break;
		}
	}
}