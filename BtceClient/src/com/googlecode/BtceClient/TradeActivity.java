package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.googlecode.BtceClient.BTCEHelper.btce_params;
import com.googlecode.BtceClient.TradesView.trades_item;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;

public class TradeActivity extends Activity {
	static final String last_order = "last_order";
	private TradeView spline_chart;
	private ListView m_trades_list;
	private TextView m_statusView;
	private SeekBarWithText m_trade_num;
	private SeekBarWithText m_price_min;
	private SeekBarWithText m_price_max;
	private SeekBarWithText m_price_cur;
	private SeekBarWithText m_price_slp;
	private Button m_btn_swap;
	private Button m_issell_btn;
	private Button m_btn_go;
	private Button m_btn_add;
	private Button m_btn_save;
	private Button m_btn_rename;
	private Button m_btn_remove;
	private Spinner m_spin_orders;
	private LayoutInflater m_inflater;

	private double currency;
	private double coins;
	private String pair;

	Timer timer_trades;
	List<trades_item> orders;
	int order_index = 0;

	AlertDialog.Builder builder;
	DBManager m_dbmgr;
	btce_params m_params;
	LimitingList<String> m_logs;
	DecimalFormat formatter6 = new DecimalFormat();
	Date temp_date = new Date();
	String statusStr;
	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");

	private static final int MSG_RESIZE = 1;
	private static final int TIMER_TRADES = 257;
	private InputHandler mHandler = new InputHandler();

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

	void initial_trades_timer() {
		if (null != timer_trades)
			timer_trades.cancel();
		long period = (long) (m_price_slp.getProgressValue() * 1000);
		if (0 < ((MyApp) getApplicationContext()).app_timer_wifi_period) {
			timer_trades = new Timer();
			timer_trades.schedule(new TimerTask() {
				public void run() {
					Message msg = new Message();
					msg.what = TIMER_TRADES;
					mHandler.sendMessage(msg);
				}
			}, 0, period);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		if (null != timer_trades)
			timer_trades.cancel();
	}

	@SuppressLint("HandlerLeak")
	class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE: {
			}
				break;
			case TIMER_TRADES: {
				go_order();
			}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		builder = new AlertDialog.Builder(this);
		m_params = ((MyApp) getApplicationContext()).app_params;
		m_logs = ((MyApp) getApplicationContext()).app_logs;

		formatter6.setMaximumFractionDigits(6);
		formatter6.setGroupingUsed(false);

		setContentView(R.layout.trade_view);
		spline_chart = (TradeView) findViewById(R.id.tradechart_view);
		m_trades_list = (ListView) findViewById(R.id.user_trades_list);
		m_statusView = (TextView) findViewById(R.id.status_view);
		m_trade_num = (SeekBarWithText) findViewById(R.id.trade_number);
		m_price_min = (SeekBarWithText) findViewById(R.id.price_min);
		m_price_max = (SeekBarWithText) findViewById(R.id.price_max);
		m_price_cur = (SeekBarWithText) findViewById(R.id.currcny);
		m_price_slp = (SeekBarWithText) findViewById(R.id.sleep_time);
		m_btn_swap = (Button) findViewById(R.id.swap);
		m_issell_btn = (Button) findViewById(R.id.type);
		m_btn_go = (Button) findViewById(R.id.trade);
		m_btn_add = (Button) findViewById(R.id.add);
		m_btn_save = (Button) findViewById(R.id.save);
		m_btn_rename = (Button) findViewById(R.id.rename);
		m_btn_remove = (Button) findViewById(R.id.remove);
		m_spin_orders = (Spinner) findViewById(R.id.orders);
		m_inflater = LayoutInflater.from(this);
		// ResizeLayout layout = (ResizeLayout) findViewById(R.id.root_layout);
		// layout.setOnResizeListener(new ResizeLayout.OnResizeListener() {
		// public void OnResize(int w, int h, int oldw, int oldh) {
		// Message msg = new Message();
		// msg.what = MSG_RESIZE;
		// msg.arg1 = oldh;
		// msg.arg2 = h;
		// mHandler.sendMessage(msg);
		// }
		// });
		double price = getIntent().getDoubleExtra("price", 100);
		if (0 == price)
			price = 100;
		currency = getIntent().getDoubleExtra("currency", 1000);
		if (0 == currency)
			currency = 1000;
		coins = getIntent().getDoubleExtra("coins", 1000);
		if (0 == coins)
			coins = 1000;
		pair = getIntent().getStringExtra("pair");
		double minicoin = 0.1;
		if (pair.toUpperCase().substring(0, 3).equals("BTC"))
			minicoin = 0.01;
		spline_chart.updateMinicoin(minicoin);
		double pmin = 0.1 * price, pmax = 2 * price;

		m_dbmgr = ((MyApp) getApplicationContext()).app_dbmgr;
		if (0 == m_dbmgr.order_count()) {
			initial_DB();
		}

		resetSpinOrders(null);

		m_trade_num.setOverlayText("Pieces: %P");

		m_price_min.setMax(1900 / 2);
		m_price_min.setBaseValue(pmin);
		m_price_min.setMaxValue(pmax);
		m_price_min.setOverlayText("From: %v");

		m_price_max.setMax(1900 / 2);
		m_price_max.setBaseValue(pmin);
		m_price_max.setMaxValue(pmax);
		m_price_max.setOverlayText("To: %v");

		m_price_slp.setBaseValue(1);
		m_price_slp.setMaxValue(60);
		m_price_slp.setMax(118);
		// m_price_slp.setProgress(16);
		m_price_slp.setOverlayText("Sleep Time: %v seconds");

		m_statusView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TradeActivity.this, LogViewActivity.class);
				startActivity(intent);
			}
		});
		// update_trades();

		m_trade_num
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromTouch) {
						spline_chart.updateNumber(progress);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
				});

		m_price_min
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromTouch) {
						if (m_price_max.getProgress() <= m_price_min
								.getProgress())
							m_price_min.setProgress(m_price_max.getProgress() - 1);
						spline_chart.updatePirceMin(m_price_min
								.getProgressValue());
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
				});
		m_price_max
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromTouch) {
						if (m_price_max.getProgress() <= m_price_min
								.getProgress())
							m_price_max.setProgress(m_price_min.getProgress() + 1);
						spline_chart.updatePirceMax(m_price_max
								.getProgressValue());
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
				});
		m_price_cur
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromTouch) {
						spline_chart.updateCurrency(m_price_cur
								.getProgressValue());
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
				});
		m_btn_swap.setOnClickListener(new swapBtnListener());
		m_btn_add.setOnClickListener(new addBtnListener());
		m_btn_save.setOnClickListener(new saveBtnListener());
		m_btn_rename.setOnClickListener(new renameBtnListener());
		m_btn_remove.setOnClickListener(new removeBtnListener());
		m_issell_btn.setOnClickListener(new typeBtnListener());
		m_btn_go.setOnClickListener(new tradeBtnListener());
		m_spin_orders
				.setOnItemSelectedListener(new SpinnerSelectedListener_orders());

		String last = m_dbmgr.get_value(last_order);
		if (null != last && !last.equals(""))
			m_spin_orders.setSelection(m_dbmgr.get_auto_orders_name().indexOf(
					last));
		else
			m_spin_orders.setSelection(0);
		m_statusView.setText("ready");
	}

	void resetSpinOrders(String item_name) {
		Vector<String> names = m_dbmgr.get_auto_orders_name();
		ArrayAdapter<String> temp_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				m_dbmgr.get_auto_orders_name());
		temp_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_spin_orders.setAdapter(temp_adapter);
		int i = names.indexOf(item_name);
		if (-1 != i)
			m_spin_orders.setSelection(i);
		else
			m_spin_orders.setSelection(0);
	}

	int save_current_pattern(String name) {
		auto_order_item item = new auto_order_item();
		item.name = name;
		item.x = spline_chart.spline.getX();
		item.y = spline_chart.spline.getY();
		item.piece = m_trade_num.getProgress();
		item.to = m_price_max.getProgress();
		item.from = m_price_min.getProgress();
		item.cur = m_price_cur.getProgress();
		item.sleep = m_price_slp.getProgressValue();
		item.type = m_issell_btn.getText().equals(
				getResources().getString(R.string.sell)) ? 1 : 0;
		return m_dbmgr.insert_auto_order(item);
	}

	class SpinnerSelectedListener_orders implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String order_name = m_spin_orders.getSelectedItem().toString();
			auto_order_item item = m_dbmgr.get_auto_order(order_name);
			if (null != item) {
				spline_chart.setSpline(item.x, item.y);
				m_trade_num.setProgress(item.piece);
				m_price_max.setProgress(item.to);
				m_price_min.setProgress(item.from);
				m_price_cur.setProgress(item.cur);
				m_price_slp.setProgressValue(item.sleep);
				// m_issell_btn.setChecked(0 != item.type);
				String buy = TradeActivity.this.getResources().getString(
						R.string.buy);
				String sell = TradeActivity.this.getResources().getString(
						R.string.sell);
				// 由于后面的ontypeClick会将text反转，所以此处要反着来
				// buy的时候设置成sell，sell的时候设置成buy
				if (0 == item.type) {
					m_issell_btn.setText(sell);
				} else
					m_issell_btn.setText(buy);
				ontypeClick();
				spline_chart.update_orders();
			}
			m_dbmgr.save_value(last_order, order_name);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	class swapBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			spline_chart.swapSpline();
		}
	}

	protected void ontypeClick() {
		String buy = this.getResources().getString(R.string.buy);
		String sell = this.getResources().getString(R.string.sell);
		if (m_issell_btn.getText().equals(buy)) {
			m_issell_btn.setText(sell);
			m_price_cur.setMaxValue(coins);
			m_price_cur.setOverlayText("%p of your " + pair.substring(0, 3)
					+ ":  %v / %m");
			spline_chart.updateOrderType(true);
		} else {
			m_issell_btn.setText(buy);
			m_price_cur.setMaxValue(currency);
			m_price_cur.setOverlayText("%p of your " + pair.substring(4)
					+ ":  %v / %m");
			spline_chart.updateOrderType(false);
		}
		spline_chart.updateCurrency(m_price_cur.getProgressValue());
	}

	class typeBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			TradeActivity.this.ontypeClick();
		}
	}

	class addBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			final EditText input = new EditText(TradeActivity.this);
			builder.setMessage(
					TradeActivity.this.getResources().getString(
							R.string.pattern_name))
					.setView(input)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String t = input.getText().toString();
									if (m_dbmgr.is_exist_order(t)) {
										Toast.makeText(
												getApplicationContext(),
												"already have a pattern named "
														+ t, Toast.LENGTH_LONG)
												.show();
									} else {
										save_current_pattern(t);
										resetSpinOrders(t);
									}
								}
							}).setNegativeButton("Cancel", null).show();
		}
	}

	class saveBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String info = "saved successful";
			if (0 != save_current_pattern(m_spin_orders.getSelectedItem()
					.toString()))
				info = "saved failed";
			Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG)
					.show();
			// resetSpinOrders(null);
		}
	}

	class removeBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			m_dbmgr.delele_auto_order(m_spin_orders.getSelectedItem()
					.toString());
			resetSpinOrders(null);
		}
	}

	public void startOrder() {
		order_index = 0;
		orders = spline_chart.getOrders();
		initial_trades_timer();
	}

	public void stopOrder() {
		this.timer_trades.cancel();
		order_index = 0;
		update_statusStr(System.currentTimeMillis() / 1000, "stoped");
		show_status_info();
	}

	public void go_order() {
		if (order_index < orders.size()) {
			trades_item item = orders.get(order_index);
			m_params.reset();
			m_params.method = BTCEHelper.btce_methods.TRADE;
			m_params.trade_amount = item.amount;
			m_params.trade_price = item.price;
			m_params.sell = 0 != item.trade_type;
			new BTCETask(m_params.getparams()).execute();
		}
		order_index += 1;
		if (order_index >= orders.size())
			ontradeClick();
	}

	public void show_status_info() {
		m_statusView.setText(statusStr);
	}

	protected void ontradeClick() {
		String start = this.getResources().getString(R.string.start);
		String stop = this.getResources().getString(R.string.stop);
		if (m_btn_go.getText().equals(start)) {
			m_btn_go.setText(stop);
			startOrder();
		} else {
			m_btn_go.setText(start);
			stopOrder();
		}
	}

	class tradeBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			TradeActivity.this.ontradeClick();
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
			if (m_params.sell)
				info = TradeActivity.this.getResources().getString(
						R.string.trade_sell_ing);
			else
				info = TradeActivity.this.getResources().getString(
						R.string.trade_buy_ing);
			update_statusStr(System.currentTimeMillis() / 1000, String.format(
					info, m_params.trade_amount, m_params.pair.substring(0, 3),
					m_params.trade_price, m_params.pair.substring(4)));
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
				feedJosn_trade(fetch_result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				update_statusStr(
						System.currentTimeMillis() / 1000,
						TradeActivity.this.getResources().getString(
								R.string.task_error)
								+ e.getMessage() + "\n" + result);
				show_status_info();
			}
		}
	}

	public void update_statusStr(long time_in_second, String info) {
		temp_date.setTime(time_in_second * 1000);
		statusStr = error_time_format.format(temp_date) + "  " + info;
		m_logs.add(statusStr);
	}

	public int feedJosn_trade(JSONObject obj) {
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
				double received = rt.getInt("received");
				double remains = rt.getDouble("remains");
				// order_num += 1;
				update_statusStr(
						System.currentTimeMillis() / 1000,
						String.format(getResources().getString(
								R.string.trade_ok_order, last_order_id,
								received, remains)));
			}
			((MyApp) getApplicationContext()).app_trans_num += 1;

			// rt = rt.getJSONObject("funds");
			// for (String coin_str : m_pair_funds.keySet()) {
			// m_pair_funds.putDouble(coin_str, rt.getDouble(coin_str));
			// }
			//
			// update_list_data();
			// m_info_list.setAdapter(new Info_list_Adapter(
			// getApplicationContext()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(System.currentTimeMillis() / 1000, getResources()
					.getString(R.string.trade_error) + e.getMessage());
		}
		show_status_info();
		return 0;
	}

	class startBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
		}
	}

	class renameBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			final EditText input = new EditText(TradeActivity.this);
			builder.setMessage(
					TradeActivity.this.getResources().getString(
							R.string.pattern_name))
					.setView(input)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String t = input.getText().toString();
									if (m_dbmgr.is_exist_order(t)) {
										Toast.makeText(
												getApplicationContext(),
												"already have a pattern named "
														+ t, Toast.LENGTH_LONG)
												.show();
									} else {
										m_dbmgr.rename_auto_order(m_spin_orders
												.getSelectedItem().toString(),
												t);
										resetSpinOrders(t);
									}
								}
							}).setNegativeButton("Cancel", null).show();
		}
	}

	public void initial_DB() {
		auto_order_item item = new auto_order_item();
		item.name = "Average";
		for (int i = 0; i < 5; ++i) {
			item.x[i] = 0.25F * i;
			item.y[i] = 1;
		}
		item.from = 150 / 2;
		item.to = 900 / 2;
		item.cur = 50;
		item.sleep = 10;
		item.piece = 20;
		item.type = 0;
		m_dbmgr.insert_auto_order(item);
		item.name = "Linear Down";
		for (int i = 0; i < 5; ++i) {
			item.y[i] = 1 - 0.25F * i;
		}
		m_dbmgr.insert_auto_order(item);
		item.name = "Linear Up";
		for (int i = 0; i < 5; ++i) {
			item.y[i] = 0.25F * i;
		}
		item.type = 1;
		m_dbmgr.insert_auto_order(item);
	}
}
