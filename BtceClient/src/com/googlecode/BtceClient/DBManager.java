package com.googlecode.BtceClient;

import java.util.Vector;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class DBManager extends com.btce.database.DBManager{
	private DBReferenceHelper reference_helper;
	private SQLiteDatabase reference_db;
	Bundle m_pair_funds;

	public static class auto_order_item {
		long id;
		String name;
		float x[] = new float[5];
		float y[] = new float[5];
		int type;
		int piece;
		int from;
		int to;
		int cur;
		double sleep;
	}

	public DBManager(Context context, String Key) {
		super(context,Key);
		reference_helper = new DBReferenceHelper(context);
		reference_db = reference_helper.getWritableDatabase();
		m_pair_funds = ((MyApp) context.getApplicationContext()).app_pair_funds;
	}

	// close database
	public void closeDB() {
		reference_db.close();
		super.closeDB();
	}

	public Vector<String> get_auto_orders_name() {
		Cursor c = reference_db.rawQuery("SELECT name FROM "
				+ DBReferenceHelper.atorder_table + " ORDER BY name", null);
		Vector<String> return_list = new Vector<String>();
		while (c.moveToNext()) {
			String name = c.getString(0);
			return_list.add(name);
		}
		return return_list;
	}

	public auto_order_item get_auto_order(String name) {
		String sql_str = "SELECT * FROM " + DBReferenceHelper.atorder_table;
		if (null != name && !name.equals(""))
			sql_str += " WHERE name = \"" + name + "\"";
		else
			sql_str += " ORDER by name LIMIT 1";
		Cursor c = reference_db.rawQuery(sql_str, null);
		if (c.moveToNext()) {
			auto_order_item item = new auto_order_item();
			item.name = c.getString(c.getColumnIndex("name"));
			item.x[0] = c.getFloat(c.getColumnIndex("p1x"));
			item.x[1] = c.getFloat(c.getColumnIndex("p2x"));
			item.x[2] = c.getFloat(c.getColumnIndex("p3x"));
			item.x[3] = c.getFloat(c.getColumnIndex("p4x"));
			item.x[4] = c.getFloat(c.getColumnIndex("p5x"));
			item.y[0] = c.getFloat(c.getColumnIndex("p1y"));
			item.y[1] = c.getFloat(c.getColumnIndex("p2y"));
			item.y[2] = c.getFloat(c.getColumnIndex("p3y"));
			item.y[3] = c.getFloat(c.getColumnIndex("p4y"));
			item.y[4] = c.getFloat(c.getColumnIndex("p5y"));
			item.from = c.getInt(c.getColumnIndex("ffrom"));
			item.to = c.getInt(c.getColumnIndex("tto"));
			item.cur = c.getInt(c.getColumnIndex("cur"));
			item.sleep = c.getDouble(c.getColumnIndex("sleep"));
			item.piece = c.getInt(c.getColumnIndex("piece"));
			item.type = c.getInt(c.getColumnIndex("type"));
			return item;
		} else {
			return null;
		}
	}

	public int insert_auto_order(auto_order_item item) {
		int rt = 0;
		reference_db.beginTransaction();
		try {
			reference_db
					.execSQL(
							"INSERT OR REPLACE INTO "
									+ DBReferenceHelper.atorder_table
									+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
							new Object[] { item.name, item.x[0], item.y[0],
									item.x[1], item.y[1], item.x[2], item.y[2],
									item.x[3], item.y[3], item.x[4], item.y[4],
									item.type, item.piece, item.from, item.to,
									item.cur, item.sleep });
			reference_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			rt = -1;
		} finally {
			reference_db.endTransaction();
		}
		return rt;
	}

	public int delele_auto_order(String name) {
		int rt = 0;
		reference_db.beginTransaction();
		try {
			reference_db.delete(DBReferenceHelper.atorder_table, "name = \""
					+ name + "\"", null);
			// reference_db.execSQL("DELETE FROM "
			// + DBReferenceHelper.atorder_table + " WHERE name = \""
			// + name + "\"", null);
			reference_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			rt = -1;
		} finally {
			reference_db.endTransaction();
		}
		return rt;
	}

	public int rename_auto_order(String oldname, String newname) {
		int rt = 0;
		reference_db.beginTransaction();
		try {
			ContentValues v = new ContentValues();
			v.put("name", newname);
			reference_db.update(DBReferenceHelper.atorder_table, v, "name = \""
					+ oldname + "\"", null);
			// reference_db.execSQL("UPDATE " + DBReferenceHelper.atorder_table
			// + " SET name = \"" + newname + "\" where  name = \""
			// + oldname + "\"", null);
			reference_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			rt = -1;
		} finally {
			reference_db.endTransaction();
		}
		return rt;
	}

	public boolean is_exist_order(String name) {
		Cursor c = reference_db.rawQuery("SELECT name FROM "
				+ DBReferenceHelper.atorder_table + " WHERE name = \"" + name
				+ "\"", null);
		return c.getCount() > 0;
	}

	public int order_count() {
		Cursor c = reference_db.rawQuery("SELECT name FROM "
				+ DBReferenceHelper.atorder_table, null);
		return c.getCount();
	}

	public int save_value(String key, String value) {
		int rt = 0;
		reference_db.beginTransaction();
		try {
			reference_db.execSQL("INSERT OR REPLACE INTO "
					+ DBReferenceHelper.setting_table + " VALUES(?, ?)",
					new Object[] { key, value });
			reference_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			rt = -1;
		} finally {
			reference_db.endTransaction();
		}
		return rt;
	}

	public String get_value(String key) {
		Cursor c = reference_db.rawQuery("SELECT value FROM "
				+ DBReferenceHelper.setting_table + " WHERE name = \"" + key
				+ "\"", null);
		if (c.moveToNext()) {
			String value = c.getString(0);
			return value;
		}
		return null;
	}

}