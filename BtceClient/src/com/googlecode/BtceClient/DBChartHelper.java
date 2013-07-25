package com.googlecode.BtceClient;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBChartHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "chart_info.db";
	private static final int DATABASE_VERSION = 1;

	public DBChartHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		BTCEPairs p = new BTCEPairs();
		for (String pair : p.keySet()) {
			String sql_string = "CREATE TABLE IF NOT EXISTS "
					+ pair
					+ "(_id INTEGER PRIMARY KEY, open REAL, close REAL, high REAL, low REAL)";
			// Log.e("SQL", sql_string);
			db.execSQL(sql_string);
		}
	}

	// called if the version is changed
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// if(1==oldVersion && 2==newVersion) {
		// BTCEPairs p = new BTCEPairs();
		// for (String pair : p.keySet()) {
		// String query_str = "SELECT * FROM " + pair+ " ORDER BY _id ASC";
		// Cursor c = db.rawQuery(query_str, null);
		// Vector<ChartItem> return_list = new Vector<ChartItem>();
		// while (c.moveToNext()) {
		// ChartItem item = new ChartItem();
		// item.time = c.getLong(c.getColumnIndex("_id"));
		// item.open = c.getDouble(c.getColumnIndex("open"));
		// item.close = c.getDouble(c.getColumnIndex("close"));
		// item.high = c.getDouble(c.getColumnIndex("high"));
		// item.low = c.getDouble(c.getColumnIndex("low"));
		// return_list.add(item);
		// }
		//
		// for(ChartItem item : return_list) {
		// String strFilter = "_id=" + item.time;
		// ContentValues args = new ContentValues();
		// args.put("_id", item.time - 8*60*60);
		// db.update(pair, args, strFilter, null);
		// }
		// }
		//
		// }
	}
}