package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.BtceClient.BTCEHelper.btce_params;

public class TradesActivity extends Activity {
	static final private int UPDATE_ID = Menu.FIRST;

	TradesView td_chart;
	ListView m_trades_list;
	TextView m_statusView;
	private LayoutInflater m_inflater;

	Date temp_date = new Date();
	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");
	String statusStr;
	LimitingList<String> m_logs;

	private static final int MSG_RESIZE = 1;
	private InputHandler mHandler = new InputHandler();
	btce_params m_params;
	DecimalFormat formatter6 = new DecimalFormat();

	private class depth_item {
		double price;
		double amount;
	};

	private ProgressDialog progressDialog;

	@SuppressLint("HandlerLeak")
	class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE: {
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

		m_params = ((MyApp) getApplicationContext()).app_params;
		m_logs = ((MyApp) getApplicationContext()).app_logs;

		formatter6.setMaximumFractionDigits(6);
		formatter6.setGroupingUsed(false);
		
		setContentView(R.layout.trades_view);
		td_chart = (TradesView) findViewById(R.id.tradeschart_view);
		m_trades_list = (ListView) findViewById(R.id.user_trades_list);
		m_statusView = (TextView) findViewById(R.id.status_view);
		m_inflater = LayoutInflater.from(this);
		ResizeLayout layout = (ResizeLayout) findViewById(R.id.root_layout);
		layout.setOnResizeListener(new ResizeLayout.OnResizeListener() {
			public void OnResize(int w, int h, int oldw, int oldh) {
				Message msg = new Message();
				msg.what = MSG_RESIZE;
				msg.arg1 = oldh;
				msg.arg2 = h;
				mHandler.sendMessage(msg);
			}
		});
		m_statusView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TradesActivity.this, LogViewActivity.class);
				startActivity(intent);

			}

		});
		update_trades();
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
			update_trades();
		}
		return super.onOptionsItemSelected(item);
	}

	public void update_trades() {
		btce_params temp_param = m_params.getparams();
		temp_param.method = BTCEHelper.btce_methods.TRADES;
		new TradesTask(temp_param).execute(null);
	}

	public void update_statusStr(long time_in_second, String info) {
		temp_date.setTime(time_in_second * 1000);
		statusStr = error_time_format.format(temp_date) + "  " + info;
		m_logs.add(statusStr);
	}

	/* Params (Integer), Progress (Integer), Result (String) */
	private class TradesTask extends AsyncTask<Integer, Integer, String> {
		btce_params param;

		TradesTask(btce_params param) {
			this.param = param;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = ProgressDialog.show(
					TradesActivity.this,
					TradesActivity.this.getResources().getString(
							R.string.Progress_title), TradesActivity.this
							.getResources()
							.getString(R.string.Progress_message), true, false);
			update_statusStr(
					System.currentTimeMillis() / 1000,
					TradesActivity.this.getResources().getString(
							R.string.trades_ing)
							+ param.pair);
			m_statusView.setText(statusStr);
		}

		@Override
		protected String doInBackground(Integer... params) {
			String result = "";
			// param = params[0];
			BTCEHelper btce = new BTCEHelper();
			result = btce.do_something(param);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				//JSONObject fetch_result = null;
				//fetch_result = new JSONObject(result);
				//feedJosn_trades(result);
				td_chart.feedJosn_trades(result);
				update_statusStr(System.currentTimeMillis() / 1000, TradesActivity.this
						.getResources().getString(R.string.trades_ok));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				update_statusStr(
						System.currentTimeMillis() / 1000,
						TradesActivity.this.getResources().getString(
								R.string.trades_error)
								+ e.getMessage());
			}
			m_trades_list.setAdapter(new trades_list_Adapter(
					getApplicationContext()));
			m_statusView.setText(statusStr);
		}
	}

	private class trades_list_Adapter extends BaseAdapter {
		public trades_list_Adapter(Context c) {
		}

		@Override
		public int getCount() {
			return td_chart.m_trades_items.size();
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
				tv = m_inflater.inflate(R.layout.trades_item, parent, false);
			else
				tv = convertView;

			t = (TextView) tv.findViewById(R.id.trades_amount);
			t.setText(formatter6.format(td_chart.m_trades_items.get(pos).amount));
			t = (TextView) tv.findViewById(R.id.trades_rate);
			t.setText(formatter6.format(td_chart.m_trades_items.get(pos).price));
			t = (TextView) tv.findViewById(R.id.trades_time);
			t.setText(error_time_format.format(td_chart.m_trades_items.get(pos).date * 1000));
			t = (TextView) tv.findViewById(R.id.trades_type);
			t.setText(0 == td_chart.m_trades_items.get(pos).trade_type ? "Ask" : "Bid");

			return tv;
		}
	}
}
