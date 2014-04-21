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
import com.spdffxyp.util.LimitingList;
import com.btce.api.BTCEHelper;
import com.btce.api.BTCEHelper.btce_params;
import com.btce.api.BTCEPairs;
import com.btce.database.DBManager.order_info;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class OrdersViewActivity extends Activity {
	static final private int UPDATE_ID = Menu.FIRST;
	LimitingList<String> m_logs;
	btce_params m_params;
	ArrayList<order_info> m_orders = new ArrayList<order_info>();
	private LayoutInflater m_inflater;
	List<String> titles;
	ListView m_orderList;
	private String[] order_status_string = {};
	SimpleDateFormat order_time_format = new SimpleDateFormat(
			"dd.MM.yy HH:mm:ss");
	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");
	Date temp_date = new Date();
	DecimalFormat formatter8 = new DecimalFormat();
	String statusStr;
	// private ProgressDialog progressDialog;
	TextView m_statusView;
	AlertDialog.Builder builder;
	boolean updated = false;
	// int item_pos = -1;
	private Spinner s_pair, s_type, s_status;

	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	int active_orders_num = -1;

	DBManager m_dbmgr;

	enum Direction {
		LEFT, RIGHT;
	}

	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String pair = s_pair.getSelectedItem().toString();
			String type = s_type.getSelectedItem().toString();
			int status = s_status.getSelectedItemPosition() - 1;
			m_orders = m_dbmgr.get_order_list(pair, type, status);
			m_orderList.setAdapter(new order_list_Adapter(
					getApplicationContext()));
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, UPDATE_ID, 0, R.string.update);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case UPDATE_ID:
			// update_orders();
			get_active_orders();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.orders_view);
		m_logs = ((MyApp) getApplicationContext()).app_logs;
		m_params = ((MyApp) getApplicationContext()).app_params;

		formatter8.setMaximumFractionDigits(8);
		formatter8.setGroupingUsed(false);

		m_statusView = (TextView) findViewById(R.id.order_status);
		m_orderList = (ListView) findViewById(R.id.orders_list);
		s_pair = (Spinner) findViewById(R.id.spinner_pair);
		s_type = (Spinner) findViewById(R.id.spinner_type);
		s_status = (Spinner) findViewById(R.id.spinner_status);
		m_inflater = LayoutInflater.from(this);

		ArrayAdapter<String> temp_adapter;

		temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				asSortedList(new BTCEPairs().keySet()));
		temp_adapter.insert("Pairs", 0);
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_pair.setAdapter(temp_adapter);
		s_pair.setOnItemSelectedListener(new SpinnerSelectedListener());

		temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.trade_type_array));
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_type.setAdapter(temp_adapter);
		s_type.setOnItemSelectedListener(new SpinnerSelectedListener());

		order_status_string = getResources().getStringArray(
				R.array.order_status_array);
		temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, order_status_string);
		// temp_adapter.insert("All", 0);
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_status.setAdapter(temp_adapter);
		// s_status.setSelection(1);
		s_status.setOnItemSelectedListener(new SpinnerSelectedListener());

		m_statusView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(OrdersViewActivity.this, LogViewActivity.class);
				startActivity(intent);

			}

		});

		builder = new AlertDialog.Builder(this);
		m_orderList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (0 == m_orders.get(position).status) {
					builder.setMessage(
							OrdersViewActivity.this.getResources().getString(
									R.string.cancel_order_question))
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											if (position >= m_orders.size())
												return;
											order_info order_item = m_orders
													.get(position);
											update_statusStr(
													System.currentTimeMillis() / 1000,
													OrdersViewActivity.this
															.getResources()
															.getString(
																	R.string.cancel_order_ing)
															+ order_item.id);
											m_statusView.setText(statusStr);
											btce_params temp_param = m_params
													.getparams();
											temp_param.reset();
											temp_param.method = BTCEHelper.btce_methods.CANCEL_ORDER;
											temp_param.order_id = order_item.id;
											new UpdateOrderTask()
													.execute(temp_param);
										}
									}).setNegativeButton("No", null).show();
					// item_pos = position;
				}
			}
			//
			// DialogInterface.OnClickListener dialogClickListener = new
			// DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// if (-1 == item_pos)
			// return;
			// switch (which) {
			// case DialogInterface.BUTTON_POSITIVE:
			// order_info order_item = m_orders.get(item_pos);
			// update_statusStr(System.currentTimeMillis() / 1000,
			// OrdersViewActivity.this.getResources()
			// .getString(R.string.cancel_order_ing)
			// + order_item.id);
			// m_statusView.setText(statusStr);
			// btce_params temp_param = m_params.getparams();
			// temp_param.reset();
			// temp_param.method = BTCEHelper.btce_methods.CANCEL_ORDER;
			// temp_param.order_id = order_item.id;
			// new UpdateOrderTask().execute(temp_param);
			// break;
			//
			// case DialogInterface.BUTTON_NEGATIVE:
			// // No button clicked
			// break;
			// }
			// }
			// };
		});

		m_orderList.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				// Log.v("long clicked","pos: " + pos);
				new AlertDialog.Builder(OrdersViewActivity.this)
						.setMessage("ReOrder?")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										m_params.reset();
										btce_params tt = m_params.getparams();
										tt.method = BTCEHelper.btce_methods.TRADE;
										order_info order_item = m_orders
												.get(position);
										tt.pair = order_item.pair;
										tt.trade_amount = order_item.amount;
										tt.trade_price = order_item.rate;
										tt.sell = order_item.type
												.equalsIgnoreCase("sell");
										new BTCETask(tt).execute();
									}
								}).setNegativeButton("Cancel", null).show();
				return true;
			}
		});
		m_dbmgr = ((MyApp) getApplicationContext()).app_dbmgr;
		active_orders_num = m_dbmgr.get_order_active();
		// if (0 == active_orders_num)
		// s_status.setSelection(0);
		// else
		s_status.setSelection(1);
		int intent_num = this.getIntent().getIntExtra("OrdersNUM", -1);
		if (-1 != intent_num && active_orders_num != intent_num)
			// update_orders();
			get_active_orders();
		else if (0 == active_orders_num) {
			m_statusView.setText("there are no active orders");
		}
	}

	public void update_orders() {
		int act_id = m_dbmgr.get_first_active_order_id();
		update_statusStr(System.currentTimeMillis() / 1000, this.getResources()
				.getString(R.string.order_list_ing));
		m_statusView.setText(statusStr);
		btce_params temp_param = m_params.getparams();
		temp_param.reset();
		temp_param.method = BTCEHelper.btce_methods.ORDER_LIST;
		temp_param.order_active = 0;
		temp_param.his_from_id = act_id;
		temp_param.his_end_id = -1;
		temp_param.pair = "all pairs";
		new UpdateOrderTask().execute(temp_param);
	}

	public void get_active_orders() {
		update_statusStr(System.currentTimeMillis() / 1000, this.getResources()
				.getString(R.string.active_orders_ing));
		m_statusView.setText(statusStr);
		btce_params temp_param = m_params.getparams();
		temp_param.reset();
		temp_param.method = BTCEHelper.btce_methods.ACTIVE_ORDERS;
		temp_param.pair = "all pairs";
		new UpdateOrderTask().execute(temp_param);
	}

	/* Params (btce_params), Progress (Integer), Result (String) */
	private class UpdateOrderTask extends
			AsyncTask<btce_params, Integer, String> {
		btce_params param;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// progressDialog = ProgressDialog.show(
			// OrdersViewActivity.this,
			// OrdersViewActivity.this.getResources().getString(
			// R.string.Progress_title), OrdersViewActivity.this
			// .getResources()
			// .getString(R.string.Progress_message), true, false);
		}

		@Override
		protected String doInBackground(btce_params... params) {
			String result = "";
			param = params[0];
			BTCEHelper btce = new BTCEHelper(
					((MyApp) getApplicationContext()).cookies);
			result = btce.do_something(param);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// progressDialog.dismiss();
			try {
				JSONObject fetch_result = null;
				fetch_result = new JSONObject(result);
				// m_statusView.setText(result);
				switch (param.method) {
				case CANCEL_ORDER:
					feedJosn_cancelorder(fetch_result);
					break;
				case ORDER_LIST:
				case ACTIVE_ORDERS:
					feedJosn_getorders(fetch_result);
					break;
				default:
					update_statusStr(
							System.currentTimeMillis() / 1000,
							OrdersViewActivity.this.getResources().getString(
									R.string.unknown_task));

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				update_statusStr(
						System.currentTimeMillis() / 1000,
						OrdersViewActivity.this.getResources().getString(
								R.string.task_error)
								+ e.getMessage());
			}
			m_orderList.setAdapter(new order_list_Adapter(
					getApplicationContext()));
			m_statusView.setText(statusStr);
		}
	}

	/* Params (Integer), Progress (Integer), Result (String) */
	private class BTCETask extends AsyncTask<Integer, Integer, String> {
		btce_params param;
		long create_time = 0;

		public BTCETask(btce_params p) {
			super();
			param = p;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			String info;
			if (param.sell)
				info = OrdersViewActivity.this.getResources().getString(
						R.string.trade_sell_ing);
			else
				info = OrdersViewActivity.this.getResources().getString(
						R.string.trade_buy_ing);
			update_statusStr(System.currentTimeMillis() / 1000, String.format(
					info, param.trade_amount, param.pair.substring(0, 3),
					param.trade_price, param.pair.substring(4)));
			show_status_info();
		}

		@Override
		protected String doInBackground(Integer... params) {
			String result = "";
			BTCEHelper btce = new BTCEHelper(
					((MyApp) getApplicationContext()).cookies);
			result = btce.do_something(param);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject fetch_result;
			try {
				fetch_result = new JSONObject(result);
				feedJosn_trade(fetch_result, param);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				update_statusStr(
						System.currentTimeMillis() / 1000,
						OrdersViewActivity.this.getResources().getString(
								R.string.task_error)
								+ e.getMessage() + "\n" + result);
				show_status_info();
			}
		}
	}

	public void show_status_info() {
		m_statusView.setText(statusStr);
	}

	public int feedJosn_trade(JSONObject obj, btce_params param) {
		try {
			if (1 != obj.getInt("success")) {
				update_statusStr(
						System.currentTimeMillis() / 1000,
						getResources().getString(R.string.trade_error)
								+ obj.getString("error"));
				show_status_info();
				return -1;
			}
			JSONObject rt = obj.getJSONObject("return");

			long last_order_id = rt.getLong("order_id");
			if (0 == last_order_id) {
				update_statusStr(System.currentTimeMillis() / 1000,
						getResources().getString(R.string.trade_ok));
			} else {
				double received = rt.getDouble("received");
				double remains = rt.getDouble("remains");

				order_info order_item = new order_info();
				order_item.id = (int) last_order_id;
				order_item.pair = param.pair;
				order_item.type = param.sell ? "Sell" : "Buy";
				order_item.amount = remains;
				order_item.rate = param.trade_price;
				order_item.time = System.currentTimeMillis() / 1000;
				order_item.status = 0;
				ArrayList<order_info> result_orders = new ArrayList<order_info>();
				result_orders.add(order_item);
				// m_dbmgr.reset_order_status(0, 1);
				m_dbmgr.update_order(result_orders);
				active_orders_num = m_dbmgr.get_order_active();
				if (0 != active_orders_num)
					s_status.setSelection(1);
				// update the list
				new SpinnerSelectedListener().onItemSelected(null, null, 0, 0);
				update_statusStr(
						System.currentTimeMillis() / 1000,
						String.format(getResources().getString(
								R.string.trade_ok_order, last_order_id,
								received, remains)));
			}
			((MyApp) getApplicationContext()).app_trans_num += 1;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(System.currentTimeMillis() / 1000, getResources()
					.getString(R.string.trade_error) + e.getMessage());
		}
		show_status_info();
		return 0;
	}

	public void update_statusStr(long time_in_second, String info) {
		temp_date.setTime(time_in_second * 1000);
		statusStr = error_time_format.format(temp_date) + "  " + info;
		m_logs.add(statusStr);
	}

	public int feedJosn_cancelorder(JSONObject obj) {
		try {
			if (1 != obj.getInt("success")) {
				update_statusStr(System.currentTimeMillis() / 1000, this
						.getResources().getString(R.string.cancel_order_error)
						+ obj.getString("error"));
				return -1;
			}
			JSONObject rt = obj.getJSONObject("return");
			int order_id = rt.getInt("order_id");

			m_dbmgr.set_order_status(order_id, 1);
			// m_orders.remove(item_pos);
			for (int i = 0; i < m_orders.size(); ++i) {
				if (m_orders.get(i).id == order_id) {
					m_orders.remove(i);
					break;
				}
			}

			// update funds
			rt = rt.getJSONObject("funds");
			Bundle m_pair_funds = ((MyApp) getApplicationContext()).app_pair_funds;
			for (String coin_str : m_pair_funds.keySet()) {
				m_pair_funds.putDouble(coin_str, rt.getDouble(coin_str));
			}
			update_statusStr(System.currentTimeMillis() / 1000, this
					.getResources().getString(R.string.cancel_order_ok)
					+ order_id);
			// update_orders();
			// get_active_orders();
			updated = true;
			((MyApp) getApplicationContext()).app_trans_num += 1;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(System.currentTimeMillis() / 1000,
					this.getResources().getString(R.string.cancel_order_error)
							+ e.toString());
		}
		return 0;
	}

	public int feedJosn_getorders(JSONObject obj) {
		try {
			if (1 != obj.getInt("success")) {
				String error_info = obj.getString("error");
				if ("no orders".equalsIgnoreCase(error_info)
						&& 0 != m_dbmgr.get_order_active()) {
					m_dbmgr.reset_order_status(0, 1);
					// update the list
					new SpinnerSelectedListener().onItemSelected(null, null, 0,
							0);
				}
				update_statusStr(System.currentTimeMillis() / 1000, this
						.getResources().getString(R.string.order_list_error)
						+ error_info);
				return -1;
			}
			JSONObject rt = obj.getJSONObject("return");
			// m_orders.clear();
			ArrayList<order_info> result_orders = new ArrayList<order_info>();
			Iterator keys = rt.keys();
			while (keys.hasNext()) {
				// loop to get the dynamic key
				String order_id = (String) keys.next();

				// get the value of the dynamic key
				JSONObject order_json = rt.getJSONObject(order_id);

				// do something here with the value...
				order_info order_item = new order_info();
				order_item.id = Integer.parseInt(order_id);
				order_item.pair = order_json.getString("pair");
				order_item.type = order_json.getString("type");
				order_item.amount = order_json.getDouble("amount");
				order_item.rate = order_json.getDouble("rate");
				order_item.time = order_json.getLong("timestamp_created");
				order_item.status = order_json.getInt("status");
				result_orders.add(order_item);
			}

			m_dbmgr.reset_order_status(0, 1);
			m_dbmgr.update_order(result_orders);
			active_orders_num = m_dbmgr.get_order_active();
			if (0 != active_orders_num)
				s_status.setSelection(1);
			// update the list
			new SpinnerSelectedListener().onItemSelected(null, null, 0, 0);

			update_statusStr(System.currentTimeMillis() / 1000, this
					.getResources().getString(R.string.order_list_ok));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(System.currentTimeMillis() / 1000,
					this.getResources().getString(R.string.order_list_error)
							+ e.toString());
		}
		return 0;
	}

	/*--- ListAdapter for rendering JSON data ---*/
	private class order_list_Adapter extends BaseAdapter {
		public order_list_Adapter(Context c) {
		}

		@Override
		public int getCount() {
			return m_orders.size();
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
				tv = m_inflater.inflate(R.layout.order_item, parent, false);
			else
				tv = convertView;

			t = (TextView) tv.findViewById(R.id.order_pair);
			t.setText(m_orders.get(pos).pair.toUpperCase());
			t = (TextView) tv.findViewById(R.id.order_type);
			t.setText(m_orders.get(pos).type.toUpperCase());
			t = (TextView) tv.findViewById(R.id.order_amount);
			t.setText("" + formatter8.format(m_orders.get(pos).amount));
			t = (TextView) tv.findViewById(R.id.order_rate);
			t.setText("" + formatter8.format(m_orders.get(pos).rate));
			t = (TextView) tv.findViewById(R.id.order_status);
			t.setText(order_status_string[m_orders.get(pos).status + 1]);
			t = (TextView) tv.findViewById(R.id.order_time);
			temp_date.setTime(m_orders.get(pos).time * 1000);
			t.setText("id:" + m_orders.get(pos).id + " "
					+ order_time_format.format(temp_date));

			return tv;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("OrdersNUM", m_dbmgr.get_order_active());
		setResult(RESULT_OK, intent);
		super.finish();
	}
}
