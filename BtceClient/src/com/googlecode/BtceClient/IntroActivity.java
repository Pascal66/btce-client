package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.googlecode.BtceClient.R;
import com.googlecode.BtceClient.BTCEHelper.btce_params;
import com.googlecode.BtceClient.ResizeLayout.OnResizeListener;
import com.googlecode.BtceClient.TradesView.trades_item;

/**
 * @author spdffxyp <spdffxyp@gmail.com>
 * @version May 2013
 */
public class IntroActivity extends Activity implements OnGestureListener,
		OnDoubleTapListener {
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		for (BTCETask tsk : btce_tasks) {
			tsk.cancel(true);
		}
		btce_tasks.clear();
		if (null != timer_wifi)
			timer_wifi.cancel();
		if (null != timer_mobile)
			timer_mobile.cancel();
		if (null != timer_wifi_all)
			timer_wifi_all.cancel();
		if (null != timer_mobile_all)
			timer_mobile_all.cancel();
		savePreference();
	}

	static final private int MENU_BASE = Menu.FIRST + 100;
	static final private int EXIT_ID = MENU_BASE;
	static final private int UPDATE_ALL_CHART = MENU_BASE + 1;
	static final private int DEPTH_VIEW = MENU_BASE + 2;
	static final private int TRADES_VIEW = MENU_BASE + 3;
	static final private int SETTING_ID = MENU_BASE + 3128;
	static final private int PRICE_ID = MENU_BASE + 3129;

	static final int ALL_PAIR_PRICE = 0;
	static final int ALL_PAIR_VOLUMES = 1;
	static final int ALL_PAIR_FUNDS = 2;
	static final int ALL_PAIR_TRANS = 3;
	static final int ALL_PAIR_ORDERS = 4;
	static final int ALL_PAIR_FEES = 5;

	private InputMethodManager im_ctrl;
	private LayoutInflater m_inflater;

	private CandleStickView kchart_view;
	private DepthView dp_chart;
	private TradesView td_chart;
	// private ImageButton m_btn_ok;
	private Button m_btn_go;
	// private Button m_btn_getinfo;
	private TextView m_status_view;
	private EditText m_price;
	private EditText m_amount;
	private ToggleButton m_issell_btn;
	private ListView m_info_list;
	// private ListView m_trades_list;
	// private ListView m_depth_list;

	private ViewFlipper mViewFlipper;
	private GestureDetector mGestureDetector;

	ResizeLayout chart_layout, resize_layout;

	private int input_area_height = 0;

	DecimalFormat formatter6 = new DecimalFormat();
	DecimalFormat formatter8 = new DecimalFormat();
	List<Map<String, String>> m_info_data = new ArrayList<Map<String, String>>();
	// List<trades_item> m_trade_items = new ArrayList<trades_item>();
	// List<depth_item> m_ask_items = new ArrayList<depth_item>();
	// List<depth_item> m_bid_items = new ArrayList<depth_item>();
	Bundle m_last_price = new Bundle();
	Bundle m_pair_funds;
	static String str_last_price = "last_price";
	static String str_value = "value";
	DBManager m_dbmgr;
	int order_num = 0;
	Date temp_date = new Date();
	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");
	String statusStr;
	LimitingList<String> m_logs;
	boolean keyboard_is_shown = false;
	String str_volume, str_funds, str_trans, str_actorder, str_fee;

	String depth_str = "", trades_str = "";
	ArrayList<trades_item> m_trades_items = new ArrayList<trades_item>();

	btce_params m_params;

	Vector<BTCETask> btce_tasks = new Vector<BTCETask>();
	ticker m_ticker = new ticker();
	Double fee_level = 0.0;

	Timer timer_wifi, timer_mobile;
	Timer timer_wifi_all, timer_mobile_all;

	// BTCEPairs all_pairs;
	private class ticker {
		long server_time = 0;
		double high = 0;
		double low = 0;
		double avg = 0;
		double vol = 0;
		double vol_cur = 0;
		double last = 0;
		double buy = 0;
		double sell = 0;
	};

	private static final int MSG_RESIZE = 1;
	private static final int MSG_KCHART_DBCLK = 2;
	private static final int MSG_KCHART_DBCLK_PERIOD = 3;
	private static final int MSG_DEPTHVIEW_DBCLK = 4;
	private static final int MSG_TRADESVIEW_DBCLK = 5;
	private static final int TIMER_WIFI = 6;
	private static final int TIMER_MOBILE = 7;
	private static final int TIMER_WIFI_ALL = 8;
	private static final int TIMER_MOBILE_ALL = 9;
	private InputHandler mHandler = new InputHandler();

	boolean is_pad = true;

	@SuppressLint("HandlerLeak")
	class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE: {
				// run first time, initialize the params
				if (0 == input_area_height) {
					input_area_height = findViewById(R.id.text_area)
							.getBottom();
					int empty_height = findViewById(R.id.info_layout)
							.getHeight()
							- findViewById(R.id.user_info_list).getBottom();
					LayoutParams param = kchart_view.getLayoutParams();
					param.height += empty_height;
					kchart_view.setLayoutParams(param);
				} else {
					int oldh = msg.arg1, h = msg.arg2;
					if (oldh > h) {
						keyboard_is_shown = true;
						price_or_amount_changed_handler.afterTextChanged(null);
						// chart_layout.setOrientation(LinearLayout.HORIZONTAL);
					} else {
						keyboard_is_shown = false;
						m_status_view.setText(statusStr);
						// chart_layout.setOrientation(LinearLayout.VERTICAL);
					}
					if (h < kchart_view.getLayoutParams().height
							+ input_area_height) {
						LayoutParams param = kchart_view.getLayoutParams();
						param.height = h - input_area_height;
						kchart_view.setLayoutParams(param);
					} else {
						int empty_height = findViewById(R.id.info_layout)
								.getHeight()
								- findViewById(R.id.user_info_list).getBottom();
						LayoutParams param = kchart_view.getLayoutParams();
						param.height += empty_height;
						kchart_view.setLayoutParams(param);
					}
				}
				// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
				break;

			// candlestick chart double click, update
			case MSG_KCHART_DBCLK: {
				initial_cur_pair_mobile_timer();
				initial_cur_pair_wifi_timer();
				update_handler.onClick(null);
			}
				break;
			case MSG_KCHART_DBCLK_PERIOD: {
				final View candle_view = m_inflater.inflate(
						R.layout.setting_candlesticks, null);
				((TextView) candle_view.findViewById(R.id.number))
						.setText(""
								+ ((MyApp) getApplicationContext()).app_candlestick_number);
				((TextView) candle_view.findViewById(R.id.period))
						.setText(""
								+ ((MyApp) getApplicationContext()).app_candlestick_period);
				((CheckBox) candle_view.findViewById(R.id.vol_bar))
						.setChecked(((MyApp) getApplicationContext()).show_volume_bar);
				((CheckBox) candle_view.findViewById(R.id.price_line))
						.setChecked(((MyApp) getApplicationContext()).show_price_line);
				AlertDialog dlg = new AlertDialog.Builder(IntroActivity.this)
						.setTitle("Candlestick:")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(candle_view)
						.setPositiveButton("OK", ocl_candle)
						.setNegativeButton(
								"Cancel",
								new android.content.DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										AlertDialog dlg = (AlertDialog) dialog;
										im_ctrl.hideSoftInputFromWindow(dlg
												.findViewById(R.id.number)
												.getWindowToken(), 0);
										im_ctrl.hideSoftInputFromWindow(dlg
												.findViewById(R.id.period)
												.getWindowToken(), 0);
									}
								}).show();
			}
				break;
			case MSG_DEPTHVIEW_DBCLK: {
				onDepthItemSelected();
			}
				break;
			case MSG_TRADESVIEW_DBCLK: {
				onTradesItemSelected();
			}
				break;
			case TIMER_WIFI: {
				ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				if (activeNetInfo != null
						&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					// Log.e("wifi_timer", "update");
					update_handler.onClick(null);
				}
			}
				break;
			case TIMER_MOBILE: {
				ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				if (activeNetInfo != null
						&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					// Log.e("mobile_timer", "update");
					update_handler.onClick(null);
				}
			}
				break;
			case TIMER_WIFI_ALL: {
				ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				if (activeNetInfo != null
						&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					update_all_pair_chart();
				}
			}
				break;
			case TIMER_MOBILE_ALL: {
				ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				if (activeNetInfo != null
						&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					update_all_pair_chart();
				}
			}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	android.content.DialogInterface.OnClickListener ocl_candle = new android.content.DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			AlertDialog dlg = (AlertDialog) dialog;
			try {
				((MyApp) getApplicationContext()).app_candlestick_number = Integer
						.parseInt(((TextView) dlg.findViewById(R.id.number))
								.getText().toString());
			} catch (NumberFormatException e) {
				((MyApp) getApplicationContext()).app_candlestick_number = 48;
			}
			try {
				((MyApp) getApplicationContext()).app_candlestick_period = Integer
						.parseInt(((TextView) dlg.findViewById(R.id.period))
								.getText().toString());
			} catch (NumberFormatException e) {
				((MyApp) getApplicationContext()).app_candlestick_period = 1;
			}
			((MyApp) getApplicationContext()).show_volume_bar = ((CheckBox) dlg
					.findViewById(R.id.vol_bar)).isChecked();
			((MyApp) getApplicationContext()).show_price_line = ((CheckBox) dlg
					.findViewById(R.id.price_line)).isChecked();
			// update_list_data();
			// m_settingList.setAdapter(new setting_list_Adapter(
			// getApplicationContext()));
			im_ctrl.hideSoftInputFromWindow(dlg.findViewById(R.id.number)
					.getWindowToken(), 0);
			im_ctrl.hideSoftInputFromWindow(dlg.findViewById(R.id.period)
					.getWindowToken(), 0);
			initialView();
		}
	};

	void initial_pair_data() {
		kchart_view.m_items.clear();
		m_ticker = new ticker();
		if (m_params.pair.equals("usd_rur"))
			fee_level = 0.5;
		else
			fee_level = 0.2;
	}

	void loadPreference() {
		SharedPreferences settings = getSharedPreferences(MyApp.PREFS_NAME, 0);
		m_params.pair = settings.getString("last_pair", "btc_usd");
		m_params.key = settings.getString("Key", "");
		m_params.secret = settings.getString("secret", "");
		m_params.proxy_host = settings.getString("host", "");
		m_params.proxy_username = settings.getString("user", "");
		m_params.proxy_passwd = settings.getString("passwd", "");
		m_params.save_port = settings.getInt("port", 0);
		m_params.proxy_port = true == settings.getBoolean("proxy", false) ? m_params.save_port
				: -1;

		((MyApp) getApplicationContext()).app_candlestick_number = settings
				.getInt("candlestick_number", 48);
		((MyApp) getApplicationContext()).app_candlestick_period = settings
				.getInt("candlestick_period", 1);

		((MyApp) getApplicationContext()).app_timer_wifi_period = settings
				.getInt("timer_wifi", 1);
		((MyApp) getApplicationContext()).app_timer_mobile_period = settings
				.getInt("timer_mobile", 5);
		((MyApp) getApplicationContext()).app_timer_wifi_period_all = settings
				.getInt("timer_wifi_all", 2);
		((MyApp) getApplicationContext()).app_timer_mobile_period_all = settings
				.getInt("timer_mobile_all", 0);
		((MyApp) getApplicationContext()).app_update_all_pair_depth_trades = settings
				.getBoolean("update_all_depth_trades", false);
		((MyApp) getApplicationContext()).app_layout = settings.getString(
				"layout", getResources().getResourceEntryName(R.layout.main));
		((MyApp) getApplicationContext()).show_price_line = settings
				.getBoolean("show_price_line", false);
		((MyApp) getApplicationContext()).show_volume_bar = settings
				.getBoolean("show_volume_bar", false);
	}

	void savePreference() {
		SharedPreferences settings = getSharedPreferences(MyApp.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("last_pair", m_params.pair);
		editor.putString("Key", m_params.key);
		editor.putString("secret", m_params.secret);
		editor.putBoolean("proxy", -1 != m_params.proxy_port);
		editor.putString("host", m_params.proxy_host);
		editor.putString("user", m_params.proxy_username);
		editor.putString("passwd", m_params.proxy_passwd);
		editor.putInt("port", m_params.save_port);

		editor.putInt("candlestick_number",
				((MyApp) getApplicationContext()).app_candlestick_number);
		editor.putInt("candlestick_period",
				((MyApp) getApplicationContext()).app_candlestick_period);

		editor.putInt("timer_wifi",
				((MyApp) getApplicationContext()).app_timer_wifi_period);
		editor.putInt("timer_mobile",
				((MyApp) getApplicationContext()).app_timer_mobile_period);
		editor.putInt("timer_wifi_all",
				((MyApp) getApplicationContext()).app_timer_wifi_period_all);
		editor.putInt("timer_mobile_all",
				((MyApp) getApplicationContext()).app_timer_mobile_period_all);
		editor.putBoolean(
				"update_all_depth_trades",
				((MyApp) getApplicationContext()).app_update_all_pair_depth_trades);
		editor.putString("layout", ((MyApp) getApplicationContext()).app_layout);
		editor.putBoolean("show_price_line",
				((MyApp) getApplicationContext()).show_price_line);
		editor.putBoolean("show_volume_bar",
				((MyApp) getApplicationContext()).show_volume_bar);
		editor.commit();
	}

	void initialDB() {
		m_dbmgr = new DBManager(this, m_params.key);
		((MyApp) getApplicationContext()).app_dbmgr = m_dbmgr;
	}

	void initialData() {
		m_params = ((MyApp) getApplicationContext()).app_params;
		m_logs = ((MyApp) getApplicationContext()).app_logs;
		m_pair_funds = ((MyApp) getApplicationContext()).app_pair_funds;
		// all_pairs = ((MyApp) getApplicationContext()).app_all_pairs;
		m_params.secret = "";
		m_params.key = "";
		m_params.proxy_host = "";
		m_params.proxy_port = 0;
		m_params.proxy_username = "";
		m_params.proxy_passwd = "";
		m_params.pair = "btc_usd";
		formatter6.setMaximumFractionDigits(6);
		formatter6.setGroupingUsed(false);
		formatter8.setMaximumFractionDigits(8);
		formatter8.setGroupingUsed(false);
		for (String pair_key : new BTCEPairs().keySet()) {
			m_last_price.putDouble(pair_key, 0.0);
			m_pair_funds.putDouble(pair_key.substring(0, 3), 0.0);
			m_pair_funds.putDouble(pair_key.substring(4), 0.0);
		}
		initial_pair_data();
	}

	void initial_cur_pair_mobile_timer() {
		if (null != timer_mobile)
			timer_mobile.cancel();
		long period = ((MyApp) getApplicationContext()).app_timer_mobile_period * 60 * 1000;
		if (0 < ((MyApp) getApplicationContext()).app_timer_mobile_period) {
			timer_mobile = new Timer();
			timer_mobile.schedule(new TimerTask() {
				public void run() {
					Message msg = new Message();
					msg.what = TIMER_MOBILE;
					mHandler.sendMessage(msg);
				}
			}, period, period);
		}
	}

	void initial_cur_pair_wifi_timer() {
		if (null != timer_wifi)
			timer_wifi.cancel();
		long period = ((MyApp) getApplicationContext()).app_timer_wifi_period * 60 * 1000;
		if (0 < ((MyApp) getApplicationContext()).app_timer_wifi_period) {
			timer_wifi = new Timer();
			timer_wifi.schedule(new TimerTask() {
				public void run() {
					Message msg = new Message();
					msg.what = TIMER_WIFI;
					mHandler.sendMessage(msg);
				}
			}, period, period);
		}
	}

	void initial_all_pair_wifi_timer() {
		if (null != timer_wifi_all)
			timer_wifi_all.cancel();
		long period = ((MyApp) getApplicationContext()).app_timer_wifi_period_all * 60 * 60 * 1000;
		if (0 < ((MyApp) getApplicationContext()).app_timer_wifi_period_all) {
			timer_wifi_all = new Timer();
			timer_wifi_all.schedule(new TimerTask() {
				public void run() {
					Message msg = new Message();
					msg.what = TIMER_WIFI_ALL;
					mHandler.sendMessage(msg);
				}
			}, period, period);
		}
	}

	void initial_all_pair_mobile_timer() {
		if (null != timer_mobile_all)
			timer_mobile_all.cancel();
		long period = ((MyApp) getApplicationContext()).app_timer_mobile_period_all * 60 * 60 * 1000;
		if (0 < ((MyApp) getApplicationContext()).app_timer_mobile_period_all) {
			timer_mobile_all = new Timer();
			timer_mobile_all.schedule(new TimerTask() {
				public void run() {
					Message msg = new Message();
					msg.what = TIMER_MOBILE_ALL;
					mHandler.sendMessage(msg);
				}
			}, period, period);
		}
	}

	void initialTimer() {
		initial_cur_pair_mobile_timer();
		initial_cur_pair_wifi_timer();
		initial_all_pair_mobile_timer();
		initial_all_pair_wifi_timer();
	}

	void initialView() {
		// m_caculate_view.setText(pair.toUpperCase());
		// kchart_view.invalidate();
		kchart_view.show_price_line = ((MyApp) getApplicationContext()).show_price_line;
		kchart_view.show_volume_bar = ((MyApp) getApplicationContext()).show_volume_bar;
		kchart_view.k_times = ((MyApp) getApplicationContext()).app_candlestick_period;
		order_num = m_dbmgr.get_order_active();
		((MyApp) getApplicationContext()).app_trans_num = m_dbmgr
				.get_trans_count();
		kchart_view
				.feedJosn_chart(m_dbmgr
						.get_chart_data(
								m_params.pair,
								kchart_view.k_times
										* ((MyApp) getApplicationContext()).app_candlestick_number,
								0));

		depth_str = "";
		trades_str = "";
		try {
			dp_chart.feedJosn_depth(depth_str);
			td_chart.feedJosn_trades(trades_str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_status_view.setText(m_params.pair);
		update_list_data();
		m_info_list.setAdapter(new Info_list_Adapter(getApplicationContext()));

	}

	void update_list_data() {
		m_info_data.clear();

		Map<String, String> map = new HashMap<String, String>();
		map.put("title", m_params.pair.toUpperCase());
		map.put("info",
				"high:" + CandleStickView.my_formatter(m_ticker.high, 6)
						+ " low:"
						+ CandleStickView.my_formatter(m_ticker.low, 6)
						+ "\navge:"
						+ CandleStickView.my_formatter(m_ticker.avg, 6)
						+ " last:"
						+ CandleStickView.my_formatter(m_ticker.last, 6));
		m_info_data.add(ALL_PAIR_PRICE, map);

		map = new HashMap<String, String>();
		map.put("title", str_volume);
		map.put("info", m_ticker.vol + m_params.pair.substring(4).toUpperCase()
				+ "\n" + m_ticker.vol_cur
				+ m_params.pair.substring(0, 3).toUpperCase());
		m_info_data.add(ALL_PAIR_VOLUMES, map);

		map = new HashMap<String, String>();
		map.put("title", str_funds);
		map.put("info",
				formatter8.format(m_pair_funds.getDouble(m_params.pair
						.substring(0, 3)))
						+ m_params.pair.substring(0, 3).toUpperCase()
						+ "\n"
						+ formatter8.format(m_pair_funds
								.getDouble(m_params.pair.substring(4)))
						+ m_params.pair.substring(4).toUpperCase());
		m_info_data.add(ALL_PAIR_FUNDS, map);

		map = new HashMap<String, String>();
		map.put("title", str_trans);
		map.put("info", ((MyApp) getApplicationContext()).app_trans_num + "");
		m_info_data.add(ALL_PAIR_TRANS, map);

		map = new HashMap<String, String>();
		map.put("title", str_actorder);
		map.put("info", order_num + "");
		m_info_data.add(ALL_PAIR_ORDERS, map);

		map = new HashMap<String, String>();
		map.put("title", str_fee);
		map.put("info", fee_level + "%");
		m_info_data.add(ALL_PAIR_FEES, map);
	}

	public boolean pad_or_not() {
		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences settings = getSharedPreferences(MyApp.PREFS_NAME, 0);
		((MyApp) getApplicationContext()).app_layout = settings.getString(
				"layout", getResources().getResourceEntryName(R.layout.main));
		for (int id : ((MyApp) getApplicationContext()).app_layout_ids) {
			if (getResources().getResourceEntryName(id).equals(
					((MyApp) getApplicationContext()).app_layout)) {
				setContentView(id);
				is_pad = id == R.layout.main ? false : true;
				break;
			}
		}
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		kchart_view = (CandleStickView) findViewById(R.id.candlestick_view);
		dp_chart = (DepthView) findViewById(R.id.depthchart_view);
		td_chart = (TradesView) findViewById(R.id.tradeschart_view);
		m_status_view = (TextView) findViewById(R.id.status_view);
		m_price = (EditText) findViewById(R.id.trade_price);
		m_amount = (EditText) findViewById(R.id.trade_amount);
		LinearLayout m_textaera = (LinearLayout) findViewById(R.id.text_area);
		m_issell_btn = (ToggleButton) findViewById(R.id.is_sell);
		// m_btn_ok = (ImageButton) findViewById(R.id.btn_ok);
		m_btn_go = (Button) findViewById(R.id.trade_btn);
		// m_btn_getinfo = (Button) findViewById(R.id.get_info);

		// m_btn_ok.setOnClickListener(update_handler);
		m_issell_btn.setOnClickListener(buy_or_sell_handler);
		m_price.addTextChangedListener(price_or_amount_changed_handler);
		m_amount.addTextChangedListener(price_or_amount_changed_handler);
		m_btn_go.setOnClickListener(go_trade_handler);
		// m_btn_getinfo.setOnClickListener(temp_handler);
		m_info_list = (ListView) findViewById(R.id.user_info_list);

		// m_trades_list = (ListView) findViewById(R.id.user_trades_list);
		m_inflater = LayoutInflater.from(this);

		str_volume = this.getResources().getString(R.string.volume);
		str_funds = this.getResources().getString(R.string.funds);
		str_trans = this.getResources().getString(R.string.trans);
		str_actorder = this.getResources().getString(R.string.act_order);
		str_fee = this.getResources().getString(R.string.fee);

		m_status_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(IntroActivity.this, LogViewActivity.class);
				// intent.putStringArrayListExtra("value", m_logs);
				startActivity(intent);

			}

		});
		OnResizeListener chart_resize_listener = new ResizeLayout.OnResizeListener() {
			public void OnResize(int w, int h, int oldw, int oldh) {
				Message msg = new Message();
				msg.what = MSG_RESIZE;
				msg.arg1 = oldh;
				msg.arg2 = h;
				mHandler.sendMessage(msg);
			}
		};
		resize_layout = (ResizeLayout) findViewById(R.id.root_layout);
		// chart_layout = (ResizeLayout) findViewById(R.id.chart_layout);
		resize_layout.setOnResizeListener(chart_resize_listener);
		// chart_layout.setOnResizeListener(chart_resize_listener);
		kchart_view
				.setOnDoubleClickListener(new CandleStickView.OnDoubleClickListener() {

					@Override
					public void OnDoubleClickChart() {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.what = MSG_KCHART_DBCLK;
						mHandler.sendMessage(msg);

					}

					@Override
					public void OnDoubleClickOther() {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.what = MSG_KCHART_DBCLK_PERIOD;
						mHandler.sendMessage(msg);

					}
				});
		dp_chart.setOnDoubleClickListener(new DepthView.OnDoubleClickListener() {

			@Override
			public void OnDoubleClick() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = MSG_DEPTHVIEW_DBCLK;
				mHandler.sendMessage(msg);
			}
		});
		td_chart.setOnDoubleClickListener(new TradesView.OnDoubleClickListener() {

			@Override
			public void OnDoubleClick() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = MSG_TRADESVIEW_DBCLK;
				mHandler.sendMessage(msg);
			}
		});

		mGestureDetector = new GestureDetector(this);
		mViewFlipper = (ViewFlipper) findViewById(R.id.flipper);
		/* Get the Input Method Manager for controlling the soft keyboard */
		im_ctrl = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// kchart_view.setOnClickListener(this);
		if (!is_pad) {
			m_info_list.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					mGestureDetector.onTouchEvent(event);
					return false;
				}
			});
		}
		m_info_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				if (position == ALL_PAIR_PRICE) {
					intent.setClass(IntroActivity.this, PairFoundActivity.class);
					intent.putExtra(str_value, m_last_price);
					startActivityForResult(intent, position);
				} else if (position == ALL_PAIR_FUNDS) {
					intent.setClass(IntroActivity.this, PairFoundActivity.class);
					intent.putExtra(str_value, m_pair_funds);
					intent.putExtra(str_last_price, m_last_price);
					startActivityForResult(intent, position);
				} else if (position == ALL_PAIR_TRANS) {
					intent.setClass(IntroActivity.this, HistroyActivity.class);
					// intent.putExtra("number", trans_num);
					startActivityForResult(intent, position);
				} else if (position == ALL_PAIR_ORDERS) {
					intent.setClass(IntroActivity.this,
							OrdersViewActivity.class);
					intent.putExtra("number", order_num);
					startActivityForResult(intent, position);
				}
			}
		});

		// m_trades_list.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// mGestureDetector.onTouchEvent(event);
		// return false;
		// }
		// });

		DefaultHttpClient client = new DefaultHttpClient();
		((MyApp) getApplicationContext()).cookies = client.getCookieStore();
		initialData();
		loadPreference();
		initialDB();
		initialView();
		initialTimer();
		update_handler.onClick(null);

		update_statusStr(System.currentTimeMillis() / 1000, this.getResources()
				.getString(R.string.ready_info));
		show_status_info();
		// showDefaultNotification();
	}

	@Override
	protected void onDestroy() {
		// 应用的最后一个Activity关闭时应释放DB
		removeNotification();
		m_dbmgr.closeDB();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ALL_PAIR_PRICE) {
			if (resultCode == Activity.RESULT_OK) {
				String new_pair = data.getStringExtra("result");
				if (!m_params.pair.equals(new_pair)) {
					m_params.pair = new_pair;
					initial_pair_data();
					initialView();
					kchart_view
							.feedJosn_chart(m_dbmgr
									.get_chart_data(
											m_params.pair,
											kchart_view.k_times
													* ((MyApp) getApplicationContext()).app_candlestick_number,
											0));
					initial_cur_pair_mobile_timer();
					initial_cur_pair_wifi_timer();
					update_handler.onClick(null);
				}
			}
		} else if (requestCode == ALL_PAIR_TRANS) {
			// trans_num = data.getIntExtra("result", 0);
			update_list_data();
			m_info_list.setAdapter(new Info_list_Adapter(
					getApplicationContext()));
		} else if (requestCode == ALL_PAIR_ORDERS) {
			order_num = data.getIntExtra("result", 0);
			update_list_data();
			m_info_list.setAdapter(new Info_list_Adapter(
					getApplicationContext()));
		} else if (requestCode == SETTING_ID) {
			savePreference();
			m_dbmgr.closeDB();
			initialDB();
			initialView();
			initialTimer();
		}
	}

	public boolean onDoubleTap(MotionEvent e) {
		return true;
	}

	public boolean onDoubleTapEvent(MotionEvent e) {
		// Log.e("doubletap","onDoubleTapEvent");
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if (e1.getX() > e2.getX() + 100
				&& Math.abs(e1.getY() - e2.getY()) < Math.abs(e1.getX()
						- e2.getX()) / 2) {
			mViewFlipper.setInAnimation(getApplicationContext(),
					R.anim.push_left_in);
			mViewFlipper.setOutAnimation(getApplicationContext(),
					R.anim.push_left_out);
			mViewFlipper.showNext();
		} else if (e1.getX() < e2.getX() - 100
				&& Math.abs(e1.getY() - e2.getY()) < Math.abs(e1.getX()
						- e2.getX()) / 2) {
			mViewFlipper.setInAnimation(getApplicationContext(),
					R.anim.push_right_in);
			mViewFlipper.setOutAnimation(getApplicationContext(),
					R.anim.push_right_out);
			mViewFlipper.showPrevious();
		} else {
			return false;
		}
		return true;

	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		/* the context menu currently has only one option */
		menu.add(0, EXIT_ID, 0, R.string.exit);
		menu.add(0, UPDATE_ALL_CHART, 0, R.string.update);
		menu.add(0, PRICE_ID, 0, R.string.price);
		// menu.add(0, DEPTH_VIEW, 0, R.string.depth);
		// menu.add(0, TRADES_VIEW, 0, R.string.trades);
		menu.add(0, SETTING_ID, 0, R.string.setting);
		return true;
	}

	private boolean onDepthItemSelected() {
		Intent intent = new Intent();
		intent.setClass(IntroActivity.this, DepthActivity.class);
		intent.putExtra("depth", depth_str);
		startActivityForResult(intent, DEPTH_VIEW);
		return true;
	}

	private boolean onTradesItemSelected() {
		Intent intent = new Intent();
		intent.setClass(IntroActivity.this, TradesActivity.class);
		intent.putExtra("trades", trades_str);
		startActivityForResult(intent, TRADES_VIEW);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case EXIT_ID:
			finish(); /* terminate the application */
			return true;
		case UPDATE_ALL_CHART:
			// m_params.reset();
			// for (String pair : new BTCEPairs().keySet()) {
			// m_params.method = BTCEHelper.btce_methods.ORDERS_UPDATE;
			// new BTCETask(m_params.getparams().setpair(pair)).execute();
			// }
			initial_all_pair_mobile_timer();
			initial_all_pair_wifi_timer();
			update_all_pair_chart();
			return true;
		case SETTING_ID:
			intent.setClass(IntroActivity.this, SettingActivity.class);
			startActivityForResult(intent, SETTING_ID);
			return true;
		case DEPTH_VIEW:
			return onDepthItemSelected();
		case TRADES_VIEW:
			return onTradesItemSelected();
		case PRICE_ID:
			intent.setClass(IntroActivity.this, PriceActivity.class);
			intent.putExtra("fee", fee_level);
			startActivityForResult(intent, PRICE_ID);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// textMessage.addTextChangedListener(new TextWatcher(){
	TextWatcher price_or_amount_changed_handler = new TextWatcher() {
		public void afterTextChanged(Editable s) {
			String price = m_price.getText().toString();
			if (price.equals("") || price.equals(".")) {
				price = m_price.getHint().toString();
			} else {
				int p = price.indexOf('.');
				if (-1 != p && 6 <= price.length() - p) {
					price = price.substring(0, p + 6);
					m_price.removeTextChangedListener(price_or_amount_changed_handler);
					int editStart = m_price.getSelectionStart();
					m_price.setText(price);
					m_price.setSelection(editStart > price.length() ? price
							.length() : editStart);
					m_price.addTextChangedListener(price_or_amount_changed_handler);
				}
			}
			if (price.equals(getResources().getString(R.string.price_hint))) {
				m_status_view.setText(statusStr);
				return;
			}
			String amount = m_amount.getText().toString();
			if (amount.equals("") || amount.equals(".")) {
				m_status_view.setText(statusStr);
				return;
			}

			String info;
			double all = Double.parseDouble(amount) * Double.parseDouble(price);
			if (m_issell_btn.isChecked())
				info = "IN:"
						+ formatter6.format(all)
						+ " - "
						+ formatter6.format(all * fee_level / 100)
						+ m_params.pair.substring(4).toUpperCase()
						+ ";  OUT:"
						+ amount
						+ " / "
						+ formatter6.format(m_pair_funds
								.getDouble(m_params.pair.substring(0, 3)))
						+ m_params.pair.substring(0, 3).toUpperCase();
			else
				info = "IN:"
						+ amount
						+ " - "
						+ formatter6.format(Double.parseDouble(amount)
								* fee_level / 100)
						+ m_params.pair.substring(0, 3).toUpperCase()
						+ ";  OUT:"
						+ formatter6.format(all)
						+ " / "
						+ formatter6.format(m_pair_funds
								.getDouble(m_params.pair.substring(4)))
						+ m_params.pair.substring(4).toUpperCase();
			m_status_view.setText(info);
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};
	OnFocusChangeListener price_or_amount_focus_changed_handler = new OnFocusChangeListener() {
		public void onFocusChange(View v, boolean hasFocus) {
			// if(!hasFocus)
			// show_ticker_info();
		}
	};
	OnClickListener buy_or_sell_handler = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (m_issell_btn.isChecked()) {
				m_price.setHint(formatter6.format(m_ticker.sell));
				// m_price.setHint(KChartView.my_formatter(m_ticker.sell, 6));
			} else {
				m_price.setHint(formatter6.format(m_ticker.buy));
				// m_price.setHint(KChartView.my_formatter(m_ticker.buy, 6));
			}
			if (keyboard_is_shown)
				price_or_amount_changed_handler.afterTextChanged(null);
		}
	};

	OnClickListener go_trade_handler = new OnClickListener() {
		@Override
		public void onClick(View v) {
			im_ctrl.hideSoftInputFromWindow(m_amount.getWindowToken(), 0);
			String amount = m_amount.getText().toString();
			if (amount.equals("")) {
				Toast.makeText(
						getApplicationContext(),
						IntroActivity.this.getResources().getString(
								R.string.invalid_amount), Toast.LENGTH_LONG)
						.show();
				return;
			}
			String price = m_price.getText().toString();
			if (price.equals(""))
				price = m_price.getHint().toString();
			if (price.equals(getResources().getString(R.string.price_hint))) {
				Toast.makeText(
						getApplicationContext(),
						IntroActivity.this.getResources().getString(
								R.string.invalid_price), Toast.LENGTH_LONG)
						.show();
				return;
			}

			m_params.reset();
			m_params.method = BTCEHelper.btce_methods.TRADE;
			m_params.trade_amount = Double.parseDouble(amount);
			m_params.trade_price = Double.parseDouble(price);
			m_params.sell = m_issell_btn.isChecked();
			new BTCETask(m_params.getparams()).execute();
			// Toast.makeText(getApplicationContext(), "trade_" + m_params.pair,
			// Toast.LENGTH_LONG).show();
		}
	};

	OnClickListener temp_handler = new OnClickListener() {
		@Override
		public void onClick(View v) {
			m_params.reset();
			m_params.method = BTCEHelper.btce_methods.GET_INFO;
			new BTCETask(m_params.getparams()).execute();
			Toast.makeText(getApplicationContext(), "getinfo",
					Toast.LENGTH_LONG).show();
		}
	};

	void update_all_pair_chart() {
		m_params.reset();
		for (String pair : new BTCEPairs().keySet()) {
			m_params.method = BTCEHelper.btce_methods.ORDERS_UPDATE;
			new BTCETask(m_params.getparams().setpair(pair)).execute();
		}
	}

	OnClickListener update_handler = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (true) {
				Toast.makeText(
						getApplicationContext(),
						IntroActivity.this.getResources().getString(
								R.string.update_info_ing)
								+ m_params.pair, Toast.LENGTH_SHORT).show();
				update_statusStr(
						System.currentTimeMillis() / 1000,
						IntroActivity.this.getResources().getString(
								R.string.update_info_ing)
								+ m_params.pair);
				/* hide the soft keyboard */
				// im_ctrl.hideSoftInputFromWindow(m_search_key.getWindowToken(),0);
				for (BTCETask tsk : btce_tasks) {
					tsk.cancel(true);
				}
				btce_tasks.clear();
				m_params.reset();
				if (((MyApp) getApplicationContext()).use_bitcoincharts) {
					m_params.chart_start_time = m_dbmgr
							.get_last_chart_time(m_params.pair);
					if (m_params.pair.equals("btc_usd")) {
						// update all pairs price
						m_params.method = BTCEHelper.btce_methods.BTCE_UPDATE;
						btce_tasks.add((BTCETask) new BTCETask(m_params
								.getparams()).execute());
					}
				}
				long last_time = m_dbmgr.get_last_chart_time(m_params.pair);
				long test = System.currentTimeMillis() / 1000 / 1800 * 1800;
				if (24 * 60 * 60 <= test - last_time) {
					m_params.chart_start_time = last_time;
				}

				m_params.method = BTCEHelper.btce_methods.ORDERS_UPDATE;
				btce_tasks.add((BTCETask) new BTCETask(m_params.getparams())
						.execute());
				m_params.method = BTCEHelper.btce_methods.FEE;
				btce_tasks.add((BTCETask) new BTCETask(m_params.getparams())
						.execute());
				m_params.method = BTCEHelper.btce_methods.TICKER;
				btce_tasks.add((BTCETask) new BTCETask(m_params.getparams())
						.execute());
				m_params.method = BTCEHelper.btce_methods.GET_INFO;
				btce_tasks.add((BTCETask) new BTCETask(m_params.getparams())
						.execute());
				if (((MyApp) getApplicationContext()).app_update_all_pair_depth_trades) {
					BTCEPairs temp_pairs = new BTCEPairs();
					if (!temp_pairs.trade_depth_pairs.keySet().contains(
							m_params.pair))
						temp_pairs.trade_depth_pairs.put(m_params.pair,
								temp_pairs.get(m_params.pair));
					for (String pair : temp_pairs.trade_depth_pairs.keySet()) {
						m_params.method = BTCEHelper.btce_methods.TRADES;
						btce_tasks.add((BTCETask) new BTCETask(m_params
								.getparams().setpair(pair)).execute());
						m_params.method = BTCEHelper.btce_methods.DEPTH;
						btce_tasks.add((BTCETask) new BTCETask(m_params
								.getparams().setpair(pair)).execute());
					}
				} else {
					m_params.method = BTCEHelper.btce_methods.TRADES;
					btce_tasks
							.add((BTCETask) new BTCETask(m_params.getparams())
									.execute());
					m_params.method = BTCEHelper.btce_methods.DEPTH;
					btce_tasks
							.add((BTCETask) new BTCETask(m_params.getparams())
									.execute());
				}
				show_status_info();
			}
		}
	};

	public int feedJosn_fee(JSONObject obj) {
		int error = 0;
		try {
			if (1 != obj.getInt("success")) {
				update_statusStr(
						System.currentTimeMillis() / 1000,
						this.getResources().getString(R.string.fee_error)
								+ obj.getString("error"));
				return -1;
			}
			fee_level = obj.getDouble("trade");
			update_list_data();
			m_info_list.setAdapter(new Info_list_Adapter(
					getApplicationContext()));
			update_statusStr(System.currentTimeMillis() / 1000, this
					.getResources().getString(R.string.fee_ok));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(
					System.currentTimeMillis() / 1000,
					this.getResources().getString(R.string.fee_error)
							+ e.getMessage());
			error = -1;
		}
		return error;
	}

	public void show_status_info() {
		if (!keyboard_is_shown) {
			m_status_view.setText(statusStr);
		}
		buy_or_sell_handler.onClick(null);
	}

	public void update_statusStr(long time_in_second, String info) {
		temp_date.setTime(time_in_second * 1000);
		statusStr = error_time_format.format(temp_date) + "  " + info;
		m_logs.add(statusStr);
	}

	public int feedJosn_ticker(JSONObject obj) {
		int error = 0;
		try {
			if (1 != obj.getInt("success")) {
				update_statusStr(System.currentTimeMillis() / 1000,
						this.getResources().getString(R.string.ticker_error)
								+ obj.getString("error"));
				return -1;
			}
			obj = obj.getJSONObject("ticker");
			m_ticker.avg = obj.getDouble("avg");
			m_ticker.buy = obj.getDouble("buy");
			m_ticker.high = obj.getDouble("high");
			m_ticker.last = obj.getDouble("last");
			m_ticker.low = obj.getDouble("low");
			m_ticker.sell = obj.getDouble("sell");
			m_ticker.vol = obj.getDouble("vol");
			m_ticker.vol_cur = obj.getDouble("vol_cur");
			m_ticker.server_time = obj.getLong("server_time");
			update_list_data();
			m_info_list.setAdapter(new Info_list_Adapter(
					getApplicationContext()));
			// if (!isfront())
			showDefaultNotification(m_params.pair + ": " + m_ticker.last);
			update_statusStr(System.currentTimeMillis() / 1000, this
					.getResources().getString(R.string.ticker_ok));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			error = -1;
			update_statusStr(
					System.currentTimeMillis() / 1000,
					this.getResources().getString(R.string.ticker_error)
							+ e.getMessage());
		}
		return error;
	}

	public int feedJosn_depth(String json_str, String pair, long timestamp) {
		JSONObject obj = null;
		try {
			obj = new JSONObject(json_str);
			if (1 != obj.getInt("success")) {
				update_statusStr(System.currentTimeMillis() / 1000, pair + ":"
						+ this.getResources().getString(R.string.depth_error)
						+ obj.getString("error"));
				return -1;
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			update_statusStr(
					System.currentTimeMillis() / 1000,
					pair
							+ ":"
							+ this.getResources().getString(
									R.string.depth_error) + e1.getMessage());
			return -1;
		}
		update_statusStr(System.currentTimeMillis() / 1000, pair + ":"
				+ this.getResources().getString(R.string.depth_ok));
		m_dbmgr.update_depth(json_str, pair, timestamp);
		return 0;
	}

	public int feedJosn_trades(String json_str, String pair) {
		m_trades_items.clear();
		JSONArray obj = null;
		try {
			obj = new JSONArray(json_str);
		} catch (JSONException e) {
			try {
				JSONObject error = new JSONObject(json_str);
				// if (1 != error.getInt("success")) {
				update_statusStr(System.currentTimeMillis() / 1000, pair + ":"
						+ this.getResources().getString(R.string.trades_error)
						+ error.getString("error"));
				return 0;
				// }
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				update_statusStr(System.currentTimeMillis() / 1000, pair + ":"
						+ this.getResources().getString(R.string.trades_error)
						+ e1.getMessage());
				return 0;
			}
		}
		try {
			for (int i = 0; i < obj.length(); ++i) {
				trades_item item = new trades_item();
				JSONObject e = obj.getJSONObject(i);
				item.amount = e.getDouble("amount");
				item.date = e.getLong("date");
				item.price = e.getDouble("price");
				item.tid = e.getLong("tid");
				item.trade_type = e.getString("trade_type").equals("ask") ? 0
						: 1;
				m_trades_items.add(item);
			}
			// m_trades_list.setAdapter(new trades_list_Adapter(
			// getApplicationContext()));
			update_statusStr(System.currentTimeMillis() / 1000, pair + ":"
					+ this.getResources().getString(R.string.trades_ok));
			m_dbmgr.update_trades(m_trades_items, pair);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(
					System.currentTimeMillis() / 1000,
					pair
							+ ":"
							+ this.getResources().getString(
									R.string.trades_error) + e.getMessage());
		}
		return m_trades_items.size();
	}

	public int feedJosn_trade(JSONObject obj) {
		try {
			if (1 != obj.getInt("success")) {
				update_statusStr(
						System.currentTimeMillis() / 1000,
						getResources().getString(R.string.trade_error)
								+ obj.getString("error"));
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
				order_num += 1;
				update_statusStr(
						System.currentTimeMillis() / 1000,
						String.format(getResources().getString(
								R.string.trade_ok_order, last_order_id,
								received, remains)));
			}
			((MyApp) getApplicationContext()).app_trans_num += 1;

			rt = rt.getJSONObject("funds");
			for (String coin_str : m_pair_funds.keySet()) {
				m_pair_funds.putDouble(coin_str, rt.getDouble(coin_str));
			}

			update_list_data();
			m_info_list.setAdapter(new Info_list_Adapter(
					getApplicationContext()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(System.currentTimeMillis() / 1000, getResources()
					.getString(R.string.trade_error) + e.getMessage());
		}
		return 0;
	}

	public int feedJosn_getinfo(JSONObject obj) {
		try {
			if (1 != obj.getInt("success")) {
				update_statusStr(System.currentTimeMillis() / 1000,
						getResources().getString(R.string.user_info_error)
								+ obj.getString("error"));
				return -1;
			}
			JSONObject rt = obj.getJSONObject("return");
			long update_time = rt.getLong("server_time");

			order_num = rt.getInt("open_orders");
			((MyApp) getApplicationContext()).app_trans_num = rt
					.getInt("transaction_count");

			rt = rt.getJSONObject("funds");
			for (String coin_str : m_pair_funds.keySet()) {
				m_pair_funds.putDouble(coin_str, rt.getDouble(coin_str));
			}

			update_list_data();
			m_info_list.setAdapter(new Info_list_Adapter(
					getApplicationContext()));
			update_statusStr(System.currentTimeMillis() / 1000, getResources()
					.getString(R.string.user_info_ok));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(System.currentTimeMillis() / 1000, getResources()
					.getString(R.string.user_info_error) + e.getMessage());
		}
		return 0;
	}

	public int feedJosn_last_price(String pair, JSONObject obj) {
		JSONObject last_price;
		try {
			last_price = obj.getJSONObject("last");
			for (String pair_key : new BTCEPairs().keySet()) {
				m_last_price.putDouble(pair_key,
						last_price.getDouble(new BTCEPairs().get(pair_key)));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public int feedJosn_bitcoincharts(String pair, String json_str) {
		JSONArray obj;
		try {
			obj = new JSONArray(json_str);
			int up_num = m_dbmgr.update_chart(
					kchart_view.update_items_bitcoincharts(obj), pair);
			if (pair.equals(m_params.pair))
				kchart_view
						.feedJosn_chart(m_dbmgr
								.get_chart_data(
										pair,
										kchart_view.k_times
												* ((MyApp) getApplicationContext()).app_candlestick_number,
										0));
			update_statusStr(
					System.currentTimeMillis() / 1000,
					String.format(
							pair
									+ ":"
									+ getResources().getString(
											R.string.chart_ok), pair));
			if (49 <= up_num
					&& ((MyApp) getApplicationContext()).use_bitcoincharts
					&& m_params.pair.equals("btc_usd")) {
				m_params.reset();
				m_params.chart_start_time = m_dbmgr
						.get_last_chart_time(m_params.pair);
				m_params.method = BTCEHelper.btce_methods.ORDERS_UPDATE;
				btce_tasks.add((BTCETask) new BTCETask(m_params.getparams())
						.execute());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(System.currentTimeMillis() / 1000,
					pair + ":" + getResources().getString(R.string.chart_error)
							+ e.getMessage());
		}
		return 0;
	}

	public int feedJosn_orders_update(String pair, JSONObject obj) {
		try {
			if (1 != obj.getInt("success")) {
				update_statusStr(
						System.currentTimeMillis() / 1000,
						pair
								+ ":"
								+ getResources()
										.getString(R.string.chart_error)
								+ obj.getString("error"));
				return -1;
			}
			m_dbmgr.update_chart(kchart_view.update_items(obj), pair);
			if (pair.equals(m_params.pair))
				kchart_view
						.feedJosn_chart(m_dbmgr
								.get_chart_data(
										pair,
										kchart_view.k_times
												* ((MyApp) getApplicationContext()).app_candlestick_number,
										0));

			// -1 if update from sae
			if (0 > feedJosn_last_price(pair, obj)) {
				m_params.reset();
				long last_time = m_dbmgr.get_last_chart_time(m_params.pair);
				if (24 * 60 * 60 <= (System.currentTimeMillis() / 1000 / 1800 * 1800 - last_time)) {
					m_params.chart_start_time = last_time;
				}
				m_params.method = BTCEHelper.btce_methods.ORDERS_UPDATE;
				btce_tasks.add((BTCETask) new BTCETask(m_params.getparams())
						.execute());
			}

			update_statusStr(
					System.currentTimeMillis() / 1000,
					String.format(
							pair
									+ ":"
									+ getResources().getString(
											R.string.chart_ok), pair));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(System.currentTimeMillis() / 1000,
					pair + ":" + getResources().getString(R.string.chart_error)
							+ e.getMessage());
		}
		return 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (1 == mViewFlipper.getDisplayedChild()) {
				mViewFlipper.setInAnimation(getApplicationContext(),
						R.anim.push_right_in);
				mViewFlipper.setOutAnimation(getApplicationContext(),
						R.anim.push_right_out);
				mViewFlipper.showPrevious();
				return true;
			}
			// showDefaultNotification();
			moveTaskToBack(true);
		}

		return super.onKeyDown(keyCode, event);
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
			switch (param.method) {
			case TICKER:
				update_statusStr(
						System.currentTimeMillis() / 1000,
						IntroActivity.this.getResources().getString(
								R.string.ticker_ing)
								+ m_params.pair);
				break;
			case ORDERS_UPDATE:
				update_statusStr(
						System.currentTimeMillis() / 1000,
						IntroActivity.this.getResources().getString(
								R.string.chart_ing)
								+ param.pair);
				break;
			case BTCE_UPDATE:
				break;
			case TRADE:
				String info;
				if (m_params.sell)
					info = IntroActivity.this.getResources().getString(
							R.string.trade_sell_ing);
				else
					info = IntroActivity.this.getResources().getString(
							R.string.trade_buy_ing);
				update_statusStr(System.currentTimeMillis() / 1000,
						String.format(info, m_params.trade_amount,
								m_params.pair.substring(0, 3),
								m_params.trade_price,
								m_params.pair.substring(4)));
				break;
			case GET_INFO:
				update_statusStr(
						System.currentTimeMillis() / 1000,
						IntroActivity.this.getResources().getString(
								R.string.user_info_ing));
				break;
			case FEE:
				update_statusStr(
						System.currentTimeMillis() / 1000,
						IntroActivity.this.getResources().getString(
								R.string.fee_ing)
								+ param.pair);
				break;
			case TRADES:
				update_statusStr(
						System.currentTimeMillis() / 1000,
						IntroActivity.this.getResources().getString(
								R.string.trades_ing)
								+ param.pair);
				break;
			case DEPTH:
				create_time = System.currentTimeMillis();
				update_statusStr(
						System.currentTimeMillis() / 1000,
						IntroActivity.this.getResources().getString(
								R.string.depth_ing)
								+ param.pair);
				break;
			default:
				update_statusStr(
						System.currentTimeMillis() / 1000,
						IntroActivity.this.getResources().getString(
								R.string.unknown_task));
			}
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
			try {
				JSONObject fetch_result = null;
				switch (param.method) {
				case TICKER:
					fetch_result = new JSONObject(result);
					feedJosn_ticker(fetch_result);
					break;
				case ORDERS_UPDATE:
					if (((MyApp) getApplicationContext()).use_bitcoincharts
							&& 0 != param.chart_start_time
							&& param.pair.equals("btc_usd")) {
						feedJosn_bitcoincharts(param.pair, result);
					} else {
						fetch_result = new JSONObject(result);
						feedJosn_orders_update(param.pair, fetch_result);
					}
					break;
				case BTCE_UPDATE:
					fetch_result = new JSONObject(result);
					feedJosn_last_price(param.pair, fetch_result);
					break;
				case TRADE:
					fetch_result = new JSONObject(result);
					feedJosn_trade(fetch_result);
					break;
				case GET_INFO:
					fetch_result = new JSONObject(result);
					feedJosn_getinfo(fetch_result);
					break;
				case FEE:
					fetch_result = new JSONObject(result);
					feedJosn_fee(fetch_result);
					break;
				case TRADES:
					feedJosn_trades(result, param.pair);
					if (m_params.pair.equals(param.pair)) {
						trades_str = result;
						td_chart.feedJosn_trades(result);
					}
					break;
				case DEPTH:
					// feedJosn_depth(result, param.pair, create_time);
					if (m_params.pair.equals(param.pair)) {
						depth_str = result;
						dp_chart.feedJosn_depth(result);
					}
					break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				update_statusStr(
						System.currentTimeMillis() / 1000,
						IntroActivity.this.getResources().getString(
								R.string.task_error)
								+ e.getMessage() + "\n" + result);
			}
			show_status_info();
		}
	}

	private class Info_list_Adapter extends BaseAdapter {
		public Info_list_Adapter(Context c) {
		}

		@Override
		public int getCount() {
			return m_info_data.size();
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
				tv = m_inflater.inflate(R.layout.info_item, parent, false);
			else
				tv = convertView;

			t = (TextView) tv.findViewById(R.id.title);
			t.setText(m_info_data.get(pos).get("title"));
			t = (TextView) tv.findViewById(R.id.info);
			t.setText(m_info_data.get(pos).get("info"));

			return tv;
		}
	}

	//
	// private class trades_list_Adapter extends BaseAdapter {
	// public trades_list_Adapter(Context c) {
	// }
	//
	// @Override
	// public int getCount() {
	// return m_trade_items.size();
	// }
	//
	// @Override
	// public Object getItem(int arg0) {
	// return null;
	// }
	//
	// @Override
	// public long getItemId(int pos) {
	// return pos;
	// }
	//
	// @Override
	// public View getView(int pos, View convertView, ViewGroup parent) {
	// View tv = null;
	// TextView t;
	//
	// if (convertView == null)
	// tv = m_inflater.inflate(R.layout.trades_item, parent, false);
	// else
	// tv = convertView;
	//
	// t = (TextView) tv.findViewById(R.id.trades_amount);
	// t.setText(formatter6.format(m_trade_items.get(pos).amount));
	// t = (TextView) tv.findViewById(R.id.trades_rate);
	// t.setText(formatter6.format(m_trade_items.get(pos).price));
	// t = (TextView) tv.findViewById(R.id.trades_time);
	// t.setText(error_time_format.format(m_trade_items.get(pos).date * 1000));
	// t = (TextView) tv.findViewById(R.id.trades_type);
	// t.setText(0 == m_trade_items.get(pos).trade_type ? "Ask" : "Bid");
	//
	// return tv;
	// }
	// }

	//
	// //自定义显示的通知 ，创建RemoteView对象
	// private void showCustomizeNotification() {
	//
	// CharSequence title = "i am new";
	// int icon = R.drawable.icon;
	// long when = System.currentTimeMillis();
	// Notification noti = new Notification(icon, title, when + 10000);
	// noti.flags = Notification.FLAG_INSISTENT;
	//
	// // 1、创建一个自定义的消息布局 view.xml
	// // 2、在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段
	// RemoteViews remoteView = new
	// RemoteViews(this.getPackageName(),R.layout.notification);
	// remoteView.setImageViewResource(R.id.image, R.drawable.icon);
	// remoteView.setTextViewText(R.id.text , "通知类型为：自定义View");
	// noti.contentView = remoteView;
	// //
	// 3、为Notification的contentIntent字段定义一个Intent(注意，使用自定义View不需要setLatestEventInfo()方法)
	//
	// //这儿点击后简单启动Settings模块
	// PendingIntent contentIntent = PendingIntent.getActivity
	// (MainActivity.this, 0,new Intent("android.settings.SETTINGS"), 0);
	// noti.contentIntent = contentIntent;
	//
	// NotificationManager mnotiManager = (NotificationManager)
	// getSystemService(Context.NOTIFICATION_SERVICE);
	// mnotiManager.notify(0, noti);
	//
	// }
	//
	// 默认显示的的Notification
	private void showDefaultNotification(String info) {
		// 定义Notication的各种属性
		CharSequence title = "i am new";
		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();
		Notification noti = new Notification(icon, title, when + 10000);
		noti.flags = Notification.FLAG_INSISTENT;

		// 创建一个通知
		Notification mNotification = new Notification();

		// 设置属性值
		mNotification.icon = R.drawable.icon;
		mNotification.tickerText = info;
		mNotification.when = System.currentTimeMillis(); // 立即发生此通知

		// 带参数的构造函数,属性值如上
		// Notification mNotification = = new
		// Notification(R.drawable.icon,"NotificationTest",
		// System.currentTimeMillis()));

		// 添加声音效果
		mNotification.defaults |= Notification.DEFAULT_LIGHTS;

		// 添加震动,需要添加震动权限 : Virbate Permission
		// mNotification.defaults |= Notification.DEFAULT_VIBRATE ;

		// 添加状态标志

		// FLAG_AUTO_CANCEL 该通知能被状态栏的清除按钮给清除掉
		// FLAG_NO_CLEAR 该通知能被状态栏的清除按钮给清除掉
		// FLAG_ONGOING_EVENT 通知放置在正在运行
		// FLAG_INSISTENT 通知的音乐效果一直播放
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		// 将该通知显示为默认View
		Intent notificationIntent = new Intent(this, this.getClass());
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent contentIntent = PendingIntent.getActivity(
				IntroActivity.this, 0, notificationIntent, 0);
		mNotification.setLatestEventInfo(IntroActivity.this, "BTCE Client",
				info, contentIntent);

		// 设置setLatestEventInfo方法,如果不设置会App报错异常
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// 注册此通知
		// 如果该NOTIFICATION_ID的通知已存在，会显示最新通知的相关信息 ，比如tickerText 等
		mNotificationManager.notify(2, mNotification);

	}

	private void removeNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 取消的只是当前Context的Notification
		mNotificationManager.cancel(2);
	}

	protected boolean isfront() {
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);

		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

		// Log.d("topActivity", "CURRENT Activity ::"
		// + taskInfo.get(0).topActivity.getClassName());

		ComponentName componentInfo = taskInfo.get(0).topActivity;
		if (this.getPackageName().equals(componentInfo.getPackageName()))
			return true;
		return false;

	}
}
