package com.googlecode.BtceClient;

import java.util.ArrayList;
import java.util.Vector;
import java.util.zip.CRC32;

import com.googlecode.BtceClient.HistroyActivity.trade_his_item;
import com.googlecode.BtceClient.HistroyActivity.trans_his_item;
import com.googlecode.BtceClient.CandleStickView.ChartItem;
import com.googlecode.BtceClient.OrdersViewActivity.order_info;
import com.googlecode.BtceClient.TradesView.trades_item;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class DBManager {
	private DBHistroyHelper his_helper;
	private SQLiteDatabase his_db;
	private DBChartHelper chart_helper;
	private SQLiteDatabase chart_db;
	private DBDepthHelper depth_helper;
	private SQLiteDatabase depth_db;
	private DBTradesHelper trades_helper;
	private SQLiteDatabase trades_db;
	private BTCEPairs all_pairs = new BTCEPairs();
	Bundle m_pair_funds;

	public DBManager(Context context, String Key) {
		CRC32 crc = new CRC32();
		crc.update(Key.getBytes());
		String DATABASE_NAME = null;
		if (!Key.equals(""))
			DATABASE_NAME = "histroy_" + Long.toHexString(crc.getValue())
					+ ".db";
		his_helper = new DBHistroyHelper(context, DATABASE_NAME);
		his_db = his_helper.getWritableDatabase();
		chart_helper = new DBChartHelper(context);
		chart_db = chart_helper.getWritableDatabase();
		depth_helper = new DBDepthHelper(context);
		depth_db = depth_helper.getWritableDatabase();
		trades_helper = new DBTradesHelper(context);
		trades_db = trades_helper.getWritableDatabase();
		m_pair_funds = ((MyApp) context.getApplicationContext()).app_pair_funds;
	}

	// close database
	public void closeDB() {
		his_db.close();
		chart_db.close();
		depth_db.close();
		trades_db.close();
	}

	public int update_trans(ArrayList<trans_his_item> trans_list) {
		his_db.beginTransaction();
		try {
			for (trans_his_item item : trans_list) {
				his_db.execSQL("INSERT OR REPLACE INTO "
						+ DBHistroyHelper.trans_table
						+ " VALUES(?, ?, ?, ?, ?, ?, ?)", new Object[] {
						item.id, item.type, item.amount, item.currency,
						item.desc, item.status, item.time });
			}
			his_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			his_db.endTransaction();
		}
		return trans_list.size();
	}

	public int update_trade(ArrayList<trade_his_item> trade_list) {
		his_db.beginTransaction();
		try {
			for (trade_his_item item : trade_list) {
				his_db.execSQL("INSERT OR REPLACE INTO "
						+ DBHistroyHelper.trade_table
						+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?)", new Object[] {
						item.id, item.pair, item.type, item.amount, item.rate,
						item.order_id, item.is_your_order, item.time });
			}
			his_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			his_db.endTransaction();
		}
		return trade_list.size();
	}

	public int update_order(ArrayList<order_info> order_list) {
		his_db.beginTransaction();
		try {
			for (order_info item : order_list) {
				his_db.execSQL("INSERT OR REPLACE INTO "
						+ DBHistroyHelper.order_table
						+ " VALUES(?, ?, ?, ?, ?, ?, ?)", new Object[] {
						item.id, item.pair, item.type, item.amount, item.rate,
						item.status, item.time });
			}
			his_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			his_db.endTransaction();
		}
		return order_list.size();
	}

	public int update_chart(Vector<ChartItem> chart_items, String pair) {
		long last_time = get_last_chart_time(pair);
		chart_db.beginTransaction();
		try {
			for (ChartItem item : chart_items) {
				if (last_time > item.time)
					continue;
				chart_db.execSQL("INSERT OR REPLACE INTO " + pair
						+ " VALUES(?, ?, ?, ?, ?)", new Object[] { item.time,
						item.open, item.close, item.high, item.low });
			}
			chart_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			chart_db.endTransaction();
		}
		return chart_items.size();
	}

	public int update_trades(ArrayList<trades_item> trades_items, String pair) {
		long last_tid = get_last_trades_tid(pair);
		trades_db.beginTransaction();
		try {
			for (trades_item item : trades_items) {
				if (last_tid > item.tid)
					continue;
				trades_db.execSQL("INSERT OR REPLACE INTO " + pair
						+ " VALUES(?, ?, ?, ?, ?)", new Object[] { item.tid,
						item.price, item.amount, item.date, item.trade_type });
			}
			trades_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			trades_db.endTransaction();
		}
		return trades_items.size();
	}

	public int update_depth(String depth, String pair, long timestamp) {
		Cursor c = depth_db.rawQuery("SELECT * FROM " + pair
				+ " ORDER BY _id DESC LIMIT 1", null);
		if (0 != c.getCount()) {
			c.moveToNext();
			if (c.getString(c.getColumnIndex("depth")).equals(depth))
				return 0;
		}
		depth_db.beginTransaction();
		try {
			depth_db.execSQL(
					"INSERT OR REPLACE INTO " + pair + " VALUES(?, ?)",
					new Object[] { timestamp, depth });
			depth_db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			depth_db.endTransaction();
		}
		return 0;
	}

	public int get_trans_count() {
		return his_db.rawQuery(
				"SELECT _id FROM " + DBHistroyHelper.trans_table, null)
				.getCount();
	}

	public int get_last_trans_id() {
		Cursor c = his_db.rawQuery("SELECT _id FROM "
				+ DBHistroyHelper.trans_table + " ORDER BY _id DESC LIMIT 1",
				null);
		if (0 == c.getCount())
			return 0;
		c.moveToNext();
		return c.getInt(0);
	}

	public int get_last_trade_id() {
		Cursor c = his_db.rawQuery("SELECT _id FROM "
				+ DBHistroyHelper.trade_table + " ORDER BY _id DESC LIMIT 1",
				null);
		if (0 == c.getCount())
			return 0;
		c.moveToNext();
		return c.getInt(0);
	}

	public int get_first_active_order_id() {
		Cursor c = his_db.rawQuery("SELECT _id FROM "
				+ DBHistroyHelper.order_table
				+ " WHERE status = 0 ORDER BY _id ASC LIMIT 1", null);
		if (0 == c.getCount()) {
			c = his_db.rawQuery("SELECT _id FROM "
					+ DBHistroyHelper.order_table
					+ " ORDER BY _id DESC LIMIT 1", null);
			if (0 == c.getCount())
				return 0;
		}
		c.moveToNext();
		return c.getInt(0);
	}

	public long get_last_chart_time(String pair) {
		Cursor c = chart_db.rawQuery("SELECT _id FROM " + pair
				+ " ORDER BY _id DESC LIMIT 1", null);
		if (0 == c.getCount())
			return 0;
		c.moveToNext();
		return c.getLong(0);
	}

	public long get_last_trades_tid(String pair) {
		Cursor c = trades_db.rawQuery("SELECT _id FROM " + pair
				+ " ORDER BY _id DESC LIMIT 1", null);
		if (0 == c.getCount())
			return 0;
		c.moveToNext();
		return c.getLong(0);
	}

	public ArrayList<trans_his_item> get_trans_list(String currency, int type,
			int status, int limit) {
		String query_str = "SELECT * FROM " + DBHistroyHelper.trans_table;
		boolean iswhere = false;
		String str_where = "";
		if (m_pair_funds.containsKey(currency)) {
			str_where += " currency = \"" + currency.toUpperCase() + "\"";
			iswhere = true;
		}
		if (1 <= type && 5 >= type) {
			if (iswhere)
				str_where += " AND";
			str_where += " type = " + type;
			iswhere = true;
		}
		if (1 <= status && 5 >= status) {
			if (iswhere)
				str_where += " AND";
			str_where += " status = " + status;
			iswhere = true;
		}
		if (iswhere)
			query_str += " WHERE" + str_where;
		query_str += " ORDER BY _id DESC";
		if (0 < limit)
			query_str += " LIMIT " + limit;
		Cursor c = his_db.rawQuery(query_str, null);
		ArrayList<trans_his_item> return_list = new ArrayList<trans_his_item>();
		while (c.moveToNext()) {
			trans_his_item item = new trans_his_item();
			item.id = c.getInt(c.getColumnIndex("_id"));
			item.type = c.getInt(c.getColumnIndex("type"));
			item.amount = c.getDouble(c.getColumnIndex("amount"));
			item.currency = c.getString(c.getColumnIndex("currency"));
			item.desc = c.getString(c.getColumnIndex("desc"));
			item.status = c.getInt(c.getColumnIndex("status"));
			item.time = c.getLong(c.getColumnIndex("time"));
			return_list.add(item);
		}
		return return_list;

	}

	public ArrayList<trans_his_item> get_trans_list(int limit, int max_id) {
		String query_str = "SELECT * FROM " + DBHistroyHelper.trans_table;
		if (0 < max_id)
			query_str += " WHERE _id < " + max_id;
		query_str += " ORDER BY _id DESC";
		if (0 < limit)
			query_str += " LIMIT " + limit;
		Cursor c = his_db.rawQuery(query_str, null);
		ArrayList<trans_his_item> return_list = new ArrayList<trans_his_item>();
		while (c.moveToNext()) {
			trans_his_item item = new trans_his_item();
			item.id = c.getInt(c.getColumnIndex("_id"));
			item.type = c.getInt(c.getColumnIndex("type"));
			item.amount = c.getDouble(c.getColumnIndex("amount"));
			item.currency = c.getString(c.getColumnIndex("currency"));
			item.desc = c.getString(c.getColumnIndex("desc"));
			item.status = c.getInt(c.getColumnIndex("status"));
			item.time = c.getLong(c.getColumnIndex("time"));
			return_list.add(item);
		}
		return return_list;
	}

	public ArrayList<trade_his_item> get_trade_list(String pair, int type,
			int isyours, int limit) {
		String query_str = "SELECT * FROM " + DBHistroyHelper.trade_table;
		boolean iswhere = false;
		String str_where = "";
		if (all_pairs.containsKey(pair)) {
			str_where += " pair = \"" + pair + "\"";
			iswhere = true;
		}
		if (0 < type) {
			if (iswhere)
				str_where += " AND";
			if (1 == type)
				str_where += " type = \"sell\"";
			else
				str_where += " type = \"buy\"";
			iswhere = true;
		}
		if (0 < isyours) {
			if (iswhere)
				str_where += " AND";
			if (1 == isyours)
				str_where += " is_your_order = " + 1;
			else
				str_where += " is_your_order = " + 0;
			iswhere = true;
		}
		if (iswhere)
			query_str += " WHERE" + str_where;
		query_str += " ORDER BY _id DESC";
		if (0 < limit)
			query_str += " LIMIT " + limit;
		Cursor c = his_db.rawQuery(query_str, null);
		ArrayList<trade_his_item> return_list = new ArrayList<trade_his_item>();
		while (c.moveToNext()) {
			trade_his_item item = new trade_his_item();
			item.id = c.getInt(c.getColumnIndex("_id"));
			item.pair = c.getString(c.getColumnIndex("pair"));
			item.type = c.getString(c.getColumnIndex("type"));
			item.amount = c.getDouble(c.getColumnIndex("amount"));
			item.rate = c.getDouble(c.getColumnIndex("rate"));
			item.order_id = c.getInt(c.getColumnIndex("order_id"));
			item.is_your_order = 0 != c.getInt(c
					.getColumnIndex("is_your_order"));
			item.time = c.getLong(c.getColumnIndex("time"));
			return_list.add(item);
		}
		return return_list;
	}

	public ArrayList<trade_his_item> get_trade_list(int limit, int max_id) {
		String query_str = "SELECT * FROM " + DBHistroyHelper.trade_table;
		if (0 < max_id)
			query_str += " WHERE _id < " + max_id;
		query_str += " ORDER BY _id DESC";
		if (0 < limit)
			query_str += " LIMIT " + limit;
		Cursor c = his_db.rawQuery(query_str, null);
		ArrayList<trade_his_item> return_list = new ArrayList<trade_his_item>();
		while (c.moveToNext()) {
			trade_his_item item = new trade_his_item();
			item.id = c.getInt(c.getColumnIndex("_id"));
			item.pair = c.getString(c.getColumnIndex("pair"));
			item.type = c.getString(c.getColumnIndex("type"));
			item.amount = c.getDouble(c.getColumnIndex("amount"));
			item.rate = c.getDouble(c.getColumnIndex("rate"));
			item.order_id = c.getInt(c.getColumnIndex("order_id"));
			item.is_your_order = 0 != c.getInt(c
					.getColumnIndex("is_your_order"));
			item.time = c.getLong(c.getColumnIndex("time"));
			return_list.add(item);
		}
		return return_list;
	}

	public int get_order_active() {
		String query_str = "SELECT _id FROM " + DBHistroyHelper.order_table
				+ " LIMIT 1";
		if (0 == his_db.rawQuery(query_str, null).getCount())
			return 0;
		query_str = "SELECT _id FROM " + DBHistroyHelper.order_table
				+ " WHERE status = 0";
		return his_db.rawQuery(query_str, null).getCount();
	}

	public ArrayList<order_info> get_order_list(String pair, String type,
			int status) {
		String query_str = "SELECT * FROM " + DBHistroyHelper.order_table;
		boolean iswhere = false;
		String str_where = "";
		if (all_pairs.containsKey(pair)) {
			str_where += " pair = \"" + pair + "\"";
			iswhere = true;
		}
		if ("Buy".equals(type) || "Sell".equals(type)) {
			if (iswhere)
				str_where += " AND";
			str_where += " type = \"" + type.toLowerCase() + "\"";
			iswhere = true;
		}
		if (0 <= status && 3 >= status) {
			if (iswhere)
				str_where += " AND";
			str_where += " status = " + status;
			iswhere = true;
		}
		if (iswhere)
			query_str += " WHERE" + str_where;
		query_str += " ORDER BY _id DESC";
		Cursor c = his_db.rawQuery(query_str, null);
		ArrayList<order_info> return_list = new ArrayList<order_info>();
		while (c.moveToNext()) {
			order_info item = new order_info();
			item.id = c.getInt(c.getColumnIndex("_id"));
			item.pair = c.getString(c.getColumnIndex("pair"));
			item.type = c.getString(c.getColumnIndex("type"));
			item.amount = c.getDouble(c.getColumnIndex("amount"));
			item.rate = c.getDouble(c.getColumnIndex("rate"));
			item.status = c.getInt(c.getColumnIndex("status"));
			item.time = c.getLong(c.getColumnIndex("time"));
			return_list.add(item);
		}
		return return_list;
	}

	public ArrayList<order_info> get_order_list(boolean active, int limit,
			int max_id) {
		String query_str = "SELECT * FROM " + DBHistroyHelper.order_table;
		if (0 < max_id || active) {
			query_str += " WHERE";
			if (active)
				query_str += " status = 0";
			if (0 < max_id)
				query_str += " AND _id < " + max_id;
		}
		query_str += " ORDER BY _id DESC";
		if (0 < limit)
			query_str += " LIMIT " + limit;
		Cursor c = his_db.rawQuery(query_str, null);
		ArrayList<order_info> return_list = new ArrayList<order_info>();
		while (c.moveToNext()) {
			order_info item = new order_info();
			item.id = c.getInt(c.getColumnIndex("_id"));
			item.pair = c.getString(c.getColumnIndex("pair"));
			item.type = c.getString(c.getColumnIndex("type"));
			item.amount = c.getDouble(c.getColumnIndex("amount"));
			item.rate = c.getDouble(c.getColumnIndex("rate"));
			item.status = c.getInt(c.getColumnIndex("status"));
			item.time = c.getLong(c.getColumnIndex("time"));
			return_list.add(item);
		}
		return return_list;
	}

	public Vector<ChartItem> get_chart_data(String pair, int limit,
			long last_time) {
		String query_str = "SELECT * FROM " + pair;
		if (0 < last_time)
			query_str += " WHERE _id < " + last_time;
		query_str += " ORDER BY _id DESC";
		if (0 < limit)
			query_str += " LIMIT " + limit;
		Cursor c = chart_db.rawQuery(query_str, null);
		Vector<ChartItem> return_list = new Vector<ChartItem>();
		while (c.moveToNext()) {
			ChartItem item = new ChartItem();
			item.time = c.getLong(c.getColumnIndex("_id"));
			item.open = c.getDouble(c.getColumnIndex("open"));
			item.close = c.getDouble(c.getColumnIndex("close"));
			item.high = c.getDouble(c.getColumnIndex("high"));
			item.low = c.getDouble(c.getColumnIndex("low"));
			return_list.add(0, item);
		}
		return return_list;
	}

	public Vector<trades_item> get_trades_data(String pair, int limit,
			long last_tid) {
		String query_str = "SELECT * FROM " + pair;
		if (0 < last_tid)
			query_str += " WHERE _id < " + last_tid;
		query_str += " ORDER BY _id DESC";
		if (0 < limit)
			query_str += " LIMIT " + limit;
		Cursor c = trades_db.rawQuery(query_str, null);
		Vector<trades_item> return_list = new Vector<trades_item>();
		while (c.moveToNext()) {
			trades_item item = new trades_item();
			item.tid = c.getLong(c.getColumnIndex("_id"));
			item.price = c.getDouble(c.getColumnIndex("price"));
			item.amount = c.getDouble(c.getColumnIndex("amount"));
			item.date = c.getLong(c.getColumnIndex("date"));
			item.trade_type = c.getInt(c.getColumnIndex("trade_type"));
			return_list.add(0, item);
		}
		return return_list;
	}

	public Vector<String> get_depth_data(String pair, int limit, long last_time) {
		String query_str = "SELECT * FROM " + pair;
		if (0 < last_time)
			query_str += " WHERE _id < " + last_time;
		query_str += " ORDER BY _id DESC";
		if (0 < limit)
			query_str += " LIMIT " + limit;
		Cursor c = depth_db.rawQuery(query_str, null);
		Vector<String> return_list = new Vector<String>();
		while (c.moveToNext()) {
			return_list.add(0, c.getString(c.getColumnIndex("depth")));
		}
		return return_list;
	}
}