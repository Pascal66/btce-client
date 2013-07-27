package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.googlecode.BtceClient.R;
import com.googlecode.BtceClient.BTCEHelper.btce_params;
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

public class DepthActivity extends Activity {
	static final private int UPDATE_ID = Menu.FIRST;

	DepthView dp_chart;
	ListView m_depth_list;
	TextView m_statusView;
	private LayoutInflater m_inflater;

	Date temp_date = new Date();
	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");
	String statusStr;
	LimitingList<String> m_logs;

	private static final int MSG_RESIZE = 1;
	private InputHandler mHandler = new InputHandler();
	btce_params m_params;
	DecimalFormat formatter8 = new DecimalFormat();

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

		formatter8.setMaximumFractionDigits(8);
		formatter8.setGroupingUsed(false);

		setContentView(R.layout.depth_view);
		dp_chart = (DepthView) findViewById(R.id.depthchart_view);
		m_depth_list = (ListView) findViewById(R.id.user_depth_list);
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
				intent.setClass(DepthActivity.this, LogViewActivity.class);
				startActivity(intent);

			}
		});
		// update_depth();
		try {
			JSONObject fetch_result = null;
			fetch_result = new JSONObject(this.getIntent().getStringExtra(
					"depth"));
			// feedJosn_depth(fetch_result);
			dp_chart.feedJosn_depth(fetch_result);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			update_statusStr(
					System.currentTimeMillis() / 1000,
					DepthActivity.this.getResources().getString(
							R.string.depth_error)
							+ e.getMessage());
		}
		m_depth_list
				.setAdapter(new depth_list_Adapter(getApplicationContext()));
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
			update_depth();
		}
		return super.onOptionsItemSelected(item);
	}

	public void update_depth() {
		btce_params temp_param = m_params.getparams();
		temp_param.method = BTCEHelper.btce_methods.DEPTH;
		new DepthTask(temp_param).execute(null);
	}

	public void update_statusStr(long time_in_second, String info) {
		temp_date.setTime(time_in_second * 1000);
		statusStr = error_time_format.format(temp_date) + "  " + info;
		m_logs.add(statusStr);
	}

	/* Params (Integer), Progress (Integer), Result (String) */
	private class DepthTask extends AsyncTask<Integer, Integer, String> {
		btce_params param;

		DepthTask(btce_params param) {
			this.param = param;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = ProgressDialog.show(
					DepthActivity.this,
					DepthActivity.this.getResources().getString(
							R.string.Progress_title), DepthActivity.this
							.getResources()
							.getString(R.string.Progress_message), true, false);
			update_statusStr(
					System.currentTimeMillis() / 1000,
					DepthActivity.this.getResources().getString(
							R.string.depth_ing)
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
				JSONObject fetch_result = null;
				fetch_result = new JSONObject(result);
				// feedJosn_depth(fetch_result);
				dp_chart.feedJosn_depth(fetch_result);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				update_statusStr(
						System.currentTimeMillis() / 1000,
						DepthActivity.this.getResources().getString(
								R.string.depth_error)
								+ e.getMessage());
			}
			m_depth_list.setAdapter(new depth_list_Adapter(
					getApplicationContext()));
			m_statusView.setText(statusStr);
		}
	}

	private class depth_list_Adapter extends BaseAdapter {
		public depth_list_Adapter(Context c) {
		}

		@Override
		public int getCount() {
			return dp_chart.m_ask_items.size() - 1;
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
				tv = m_inflater.inflate(R.layout.depth_item, parent, false);
			else
				tv = convertView;

			t = (TextView) tv.findViewById(R.id.ask_amount);
			t.setText("" + dp_chart.m_ask_items.get(pos + 1).amount);
			t = (TextView) tv.findViewById(R.id.ask_rate);
			t.setText(formatter8.format(dp_chart.m_ask_items.get(pos + 1).price));
			t = (TextView) tv.findViewById(R.id.bid_amount);
			t.setText("" + dp_chart.m_bid_items.get(pos + 1).amount);
			t = (TextView) tv.findViewById(R.id.bid_rate);
			t.setText(formatter8.format(dp_chart.m_bid_items.get(pos + 1).price));

			return tv;
		}
	}
}
