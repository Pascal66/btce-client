package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.googlecode.BtceClient.R;
import com.googlecode.BtceClient.BTCEHelper.btce_params;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class HistroyActivity extends Activity {
	static final private int UPDATE_ID = Menu.FIRST;

	LimitingList<String> m_logs;
	btce_params m_params;
	Bundle m_pair_funds;

	ListView m_trans_his;
	ListView m_trade_his;
	TextView m_statusView;
	private LayoutInflater m_inflater;
	private ProgressDialog progressDialog;
	DBManager m_dbmgr;

	String statusStr;

	Date temp_date = new Date();
	SimpleDateFormat trade_time_format = new SimpleDateFormat(
			"dd.MM.yy HH:mm:ss");
	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");
	DecimalFormat formatter8 = new DecimalFormat();

	ArrayList<trade_his_item> m_trade_his_items = new ArrayList<trade_his_item>();
	ArrayList<trans_his_item> m_trans_his_items = new ArrayList<trans_his_item>();
	private Spinner trans_currency, trans_type, trans_status;
	private Spinner trade_pair, trade_type, trade_yours;

	// private static final String[] str_trans_types = { "types", "1", "2", "3",
	// "4", "5" };
	// private static final String[] str_trans_status = { "status", "1", "2",
	// "3",
	// "4", "5" };
	// private static final String[] str_trade_types = { "All", "Sell", "Buy" };
	// private static final String[] str_trade_yours = { "All", "Yours",
	// "Others" };
	// private List<String> str_trans_types;
	// private List<String> str_trans_status;
	// private List<String> str_trade_types;
	// private List<String> str_trade_yours;

	static class trade_his_item {
		int id;
		String pair;
		String type;
		double amount;
		double rate;
		int order_id;
		boolean is_your_order;
		long time;
	}

	static class trans_his_item {
		int id;
		int type;
		double amount;
		String currency;
		String desc;
		int status;
		long time;
	}

	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

	class SpinnerSelectedListener_trans implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String currency = trans_currency.getSelectedItem().toString();
			int type = trans_type.getSelectedItemPosition();
			int status = trans_status.getSelectedItemPosition();
			m_trans_his_items.clear();
			m_trans_his_items = m_dbmgr.get_trans_list(currency, type, status,
					50);
			m_trans_his.setAdapter(new trans_histroy_list_Adapter(
					getApplicationContext()));
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	class SpinnerSelectedListener_trade implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String pair = trade_pair.getSelectedItem().toString();
			int type = trade_type.getSelectedItemPosition();
			int yours = trade_yours.getSelectedItemPosition();
			m_trade_his_items.clear();
			m_trade_his_items = m_dbmgr.get_trade_list(pair, type, yours, 50);
			m_trade_his.setAdapter(new trade_histroy_list_Adapter(
					getApplicationContext()));
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		/* the context menu currently has only one option */
		menu.add(0, UPDATE_ID, 0, R.string.update);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case UPDATE_ID:
			update_trans_trades();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_logs = ((MyApp) getApplicationContext()).app_logs;
		m_params = ((MyApp) getApplicationContext()).app_params;
		m_pair_funds = ((MyApp) getApplicationContext()).app_pair_funds;
		formatter8.setMaximumFractionDigits(8);
		formatter8.setGroupingUsed(false);

		setContentView(R.layout.histroy_view);
		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec(this.getResources().getString(
				R.string.histroy_trans));
		spec.setContent(R.id.transhistroy);
		spec.setIndicator(this.getResources().getString(R.string.histroy_trans));
		tabs.addTab(spec);
		spec = tabs.newTabSpec(this.getResources().getString(
				R.string.histroy_trade));
		spec.setContent(R.id.tradehistroy);
		spec.setIndicator(this.getResources().getString(R.string.histroy_trade));
		tabs.addTab(spec);
		tabs.setCurrentTab(1);// show the second tab on create

		m_trans_his = (ListView) findViewById(R.id.trans_his_list);
		m_trade_his = (ListView) findViewById(R.id.trade_his_list);
		m_statusView = (TextView) findViewById(R.id.histroy_status);
		m_inflater = LayoutInflater.from(this);

		trans_currency = (Spinner) findViewById(R.id.spinner_trans_currency);
		trans_type = (Spinner) findViewById(R.id.spinner_trans_type);
		trans_status = (Spinner) findViewById(R.id.spinner_trans_status);
		trade_pair = (Spinner) findViewById(R.id.spinner_trade_pair);
		trade_type = (Spinner) findViewById(R.id.spinner_trade_type);
		trade_yours = (Spinner) findViewById(R.id.spinner_trade_yours);

		ArrayAdapter<String> temp_adapter;

		temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				asSortedList(m_pair_funds.keySet()));
		temp_adapter.insert("All", 0);
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trans_currency.setAdapter(temp_adapter);
		trans_currency
				.setOnItemSelectedListener(new SpinnerSelectedListener_trans());

		temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.trans_type_array));
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trans_type.setAdapter(temp_adapter);
		trans_type
				.setOnItemSelectedListener(new SpinnerSelectedListener_trans());

		temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.trans_status_array));
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trans_status.setAdapter(temp_adapter);
		trans_status
				.setOnItemSelectedListener(new SpinnerSelectedListener_trans());

		BTCEPairs all_pairs = new BTCEPairs();
		temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				asSortedList(all_pairs.keySet()));
		temp_adapter.insert("All", 0);
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trade_pair.setAdapter(temp_adapter);
		trade_pair
				.setOnItemSelectedListener(new SpinnerSelectedListener_trade());

		temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.trade_type_array));
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trade_type.setAdapter(temp_adapter);
		trade_type
				.setOnItemSelectedListener(new SpinnerSelectedListener_trade());

		temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.trade_owner_array));
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trade_yours.setAdapter(temp_adapter);
		trade_yours
				.setOnItemSelectedListener(new SpinnerSelectedListener_trade());

		m_dbmgr = ((MyApp) getApplicationContext()).app_dbmgr;

		m_statusView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(HistroyActivity.this, LogViewActivity.class);
				// intent.putStringArrayListExtra("value", m_logs);
				startActivity(intent);

			}

		});
		m_statusView.setText((String) m_logs.get(m_logs.size() - 1));

		if (m_dbmgr.get_trans_count() < ((MyApp) getApplicationContext()).app_trans_num)
			update_trans_trades();
		else {
			update_trans_list();
			update_trade_list();
		}

		m_trade_his.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(HistroyActivity.this, PriceActivity.class);
				intent.putExtra("is_sell", m_trade_his_items.get(position).type.toLowerCase().equals("sell"));
				intent.putExtra("price", m_trade_his_items.get(position).rate);
				if (m_trade_his_items.get(position).pair.equals("usd_rur"))
					intent.putExtra("fee", 0.5);
				else
					intent.putExtra("fee", 0.2);
				startActivity(intent);
			}
		});
	}

	void update_trans_trades() {
		new UpdateHistroyTask().execute(0);
	}

	void update_trans_list() {
		((MyApp) getApplicationContext()).app_trans_num = m_dbmgr
				.get_trans_count();
		m_trans_his_items.clear();
		m_trans_his_items = m_dbmgr.get_trans_list(50, -1);
		m_trans_his.setAdapter(new trans_histroy_list_Adapter(
				getApplicationContext()));
	}

	void update_trade_list() {
		m_trade_his_items.clear();
		m_trade_his_items = m_dbmgr.get_trade_list(50, -1);
		m_trade_his.setAdapter(new trade_histroy_list_Adapter(
				getApplicationContext()));
	}

	public void update_statusStr(long time_in_second, String info) {
		temp_date.setTime(time_in_second * 1000);
		statusStr = error_time_format.format(temp_date) + "  " + info;
		m_logs.add(statusStr);
	}

	/* Params (int), Progress (Integer), Result (String) */
	private class UpdateHistroyTask extends
			AsyncTask<Integer, Integer, String[]> {
		// btce_params param;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			update_statusStr(
					System.currentTimeMillis() / 1000,
					HistroyActivity.this.getResources().getString(
							R.string.history_ing));
			m_statusView.setText(statusStr);
			progressDialog = ProgressDialog.show(
					HistroyActivity.this,
					HistroyActivity.this.getResources().getString(
							R.string.Progress_title), HistroyActivity.this
							.getResources()
							.getString(R.string.Progress_message), true, false);
		}

		@Override
		protected String[] doInBackground(Integer... params) {
			String[] result = new String[2];

			BTCEHelper btce = new BTCEHelper();
			btce_params temp_param = m_params.getparams();
			temp_param.reset();
			temp_param.method = BTCEHelper.btce_methods.TRANS_HISTORY;
			temp_param.his_from_id = m_dbmgr.get_last_trans_id();
			result[0] = btce.do_something(temp_param);

			temp_param = m_params.getparams();
			temp_param.reset();
			temp_param.pair = "all pairs";
			temp_param.method = BTCEHelper.btce_methods.TRADE_HISTORY;
			temp_param.his_from_id = m_dbmgr.get_last_trade_id();
			result[1] = btce.do_something(temp_param);

			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			try {
				JSONObject fetch_result = null;
				fetch_result = new JSONObject(result[0]);
				feedJosn_trans_histroy(fetch_result);
				fetch_result = new JSONObject(result[1]);
				feedJosn_trade_histroy(fetch_result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				update_statusStr(
						System.currentTimeMillis() / 1000,
						HistroyActivity.this.getResources().getString(
								R.string.history_error)
								+ e.getMessage());
			}
			m_statusView.setText(statusStr);
			progressDialog.dismiss();
		}
	}

	public int feedJosn_trade_histroy(JSONObject obj) {
		try {
			if (1 != obj.getInt("success")) {
				update_statusStr(
						System.currentTimeMillis() / 1000,
						HistroyActivity.this.getResources().getString(
								R.string.trade_history_error)
								+ obj.getString("error"));
				return -1;
			}
			JSONObject rt = obj.getJSONObject("return");

			ArrayList<trade_his_item> result_list = new ArrayList<trade_his_item>();
			Iterator keys = rt.keys();
			while (keys.hasNext()) {
				// loop to get the dynamic key
				String trade_id = (String) keys.next();

				// get the value of the dynamic key
				JSONObject trade_json = rt.getJSONObject(trade_id);

				// do something here with the value...
				trade_his_item trade_item = new trade_his_item();
				trade_item.id = Integer.parseInt(trade_id);
				trade_item.pair = trade_json.getString("pair");
				trade_item.type = trade_json.getString("type");
				trade_item.amount = trade_json.getDouble("amount");
				trade_item.rate = trade_json.getDouble("rate");
				trade_item.order_id = trade_json.getInt("order_id");
				trade_item.is_your_order = 1 == trade_json
						.getInt("is_your_order") ? true : false;
				trade_item.time = trade_json.getLong("timestamp");
				result_list.add(trade_item);

			}

			m_dbmgr.update_trade(result_list);
			update_trade_list();
			update_statusStr(
					System.currentTimeMillis() / 1000,
					HistroyActivity.this.getResources().getString(
							R.string.trade_history_ok));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(
					System.currentTimeMillis() / 1000,
					HistroyActivity.this.getResources().getString(
							R.string.trade_history_error)
							+ e.getMessage());
		}
		return 0;
	}

	/*--- ListAdapter for rendering JSON data ---*/
	private class trade_histroy_list_Adapter extends BaseAdapter {
		public trade_histroy_list_Adapter(Context c) {
		}

		@Override
		public int getCount() {
			return m_trade_his_items.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			View tv = null;
			TextView t;

			if (convertView == null)
				tv = m_inflater.inflate(R.layout.trade_his_item, parent, false);
			else
				tv = convertView;

			t = (TextView) tv.findViewById(R.id.trade_pair);
			t.setText(m_trade_his_items.get(pos).pair.toUpperCase());
			t = (TextView) tv.findViewById(R.id.trade_type);
			t.setText(m_trade_his_items.get(pos).type.toUpperCase());
			t = (TextView) tv.findViewById(R.id.trade_amount);
			t.setText("" + formatter8.format(m_trade_his_items.get(pos).amount));
			t = (TextView) tv.findViewById(R.id.trade_rate);
			t.setText("" + formatter8.format(m_trade_his_items.get(pos).rate));
			t = (TextView) tv.findViewById(R.id.trade_time);
			temp_date.setTime(m_trade_his_items.get(pos).time * 1000);
			t.setText("id:" + m_trade_his_items.get(pos).id + "order_id"
					+ m_trade_his_items.get(pos).order_id + "yours:"
					+ m_trade_his_items.get(pos).is_your_order + " "
					+ trade_time_format.format(temp_date));
			return tv;
		}
	}

	public int feedJosn_trans_histroy(JSONObject obj) {
		try {
			if (1 != obj.getInt("success")) {
				update_statusStr(
						System.currentTimeMillis() / 1000,
						HistroyActivity.this.getResources().getString(
								R.string.trans_history_error)
								+ obj.getString("error"));
				return -1;
			}
			JSONObject rt = obj.getJSONObject("return");

			ArrayList<trans_his_item> result_list = new ArrayList<trans_his_item>();

			Iterator keys = rt.keys();
			while (keys.hasNext()) {
				// loop to get the dynamic key
				String trans_id = (String) keys.next();

				// get the value of the dynamic key
				JSONObject trans_json = rt.getJSONObject(trans_id);

				// do something here with the value...
				trans_his_item trans_item = new trans_his_item();
				trans_item.id = Integer.parseInt(trans_id);
				trans_item.type = trans_json.getInt("type");
				trans_item.amount = trans_json.getDouble("amount");
				trans_item.currency = trans_json.getString("currency");
				trans_item.desc = trans_json.getString("desc");
				trans_item.status = trans_json.getInt("status");
				trans_item.time = trans_json.getLong("timestamp");
				// m_trans_his_items.add(trans_item);
				result_list.add(trans_item);

			}
			m_dbmgr.update_trans(result_list);
			update_trans_list();

			update_statusStr(
					System.currentTimeMillis() / 1000,
					HistroyActivity.this.getResources().getString(
							R.string.trans_history_ok));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(
					System.currentTimeMillis() / 1000,
					HistroyActivity.this.getResources().getString(
							R.string.trans_history_error)
							+ e.getMessage());
		}
		return 0;
	}

	/*--- ListAdapter for rendering JSON data ---*/
	private class trans_histroy_list_Adapter extends BaseAdapter {
		public trans_histroy_list_Adapter(Context c) {
		}

		@Override
		public int getCount() {
			return m_trans_his_items.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			View tv = null;
			TextView t;

			if (convertView == null)
				tv = m_inflater.inflate(R.layout.trans_his_item, parent, false);
			else
				tv = convertView;

			t = (TextView) tv.findViewById(R.id.trans_type);
			t.setText("" + m_trans_his_items.get(pos).type);
			t = (TextView) tv.findViewById(R.id.trans_currency);
			t.setText(m_trans_his_items.get(pos).currency);
			t = (TextView) tv.findViewById(R.id.trans_amount);
			t.setText("" + formatter8.format(m_trans_his_items.get(pos).amount));
			t = (TextView) tv.findViewById(R.id.trans_status);
			t.setText("" + m_trans_his_items.get(pos).status);
			t = (TextView) tv.findViewById(R.id.trans_desc);
			t.setText(m_trans_his_items.get(pos).desc);

			t = (TextView) tv.findViewById(R.id.trans_time);
			temp_date.setTime(m_trans_his_items.get(pos).time * 1000);
			t.setText("id:" + m_trans_his_items.get(pos).id + " "
					+ trade_time_format.format(temp_date));

			return tv;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		super.finish();
	}
}
