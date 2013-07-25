package com.googlecode.BtceClient;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.BtceClient.R;
import com.googlecode.BtceClient.BTCEHelper.btce_params;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SettingActivity extends Activity {
	String dlg_title[] = { "API Key:", "API Secret:", "", };
	ListView m_settingList;
	boolean enable_proxy;
	List<setting_item> m_setting_data = new ArrayList<setting_item>();
	private LayoutInflater m_inflater;
	AlertDialog dlg;
	btce_params m_params;

	class setting_item {
		String key;
		String value;
		int img_id;
		boolean enable = true;
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_view);

		m_settingList = (ListView) findViewById(R.id.list_setting);
		m_inflater = LayoutInflater.from(this);
		m_params = ((MyApp) getApplicationContext()).app_params;
		enable_proxy = m_params.save_port == m_params.proxy_port;

		update_list_data();
		m_settingList.setAdapter(new setting_list_Adapter(
				getApplicationContext()));
		m_settingList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					final View key_view = m_inflater.inflate(
							R.layout.setting_key_view, null);
					((TextView) key_view.findViewById(R.id.value))
							.setText(m_params.key);
					dlg = new AlertDialog.Builder(SettingActivity.this)
							.setTitle("API Key:")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(key_view).setPositiveButton("OK", ocl_key)
							.setNegativeButton("Cancel", null).show();
				} else if (position == 1) {
					final View sec_view = m_inflater.inflate(
							R.layout.setting_secret_view, null);
					((TextView) sec_view.findViewById(R.id.value))
							.setText(m_params.secret);
					dlg = new AlertDialog.Builder(SettingActivity.this)
							.setTitle("API Secret:")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(sec_view).setPositiveButton("OK", ocl_sec)
							.setNegativeButton("Cancel", null).show();
				} else if (position == 2) {
					enable_proxy = !enable_proxy;
					update_list_data();
					m_settingList.setAdapter(new setting_list_Adapter(
							getApplicationContext()));
				} else if (position == 3) {
					final View proxy_view = m_inflater.inflate(
							R.layout.setting_proxy_sever, null);
					((TextView) proxy_view.findViewById(R.id.host))
							.setText(m_params.proxy_host);
					if (0 != m_params.save_port)
						((TextView) proxy_view.findViewById(R.id.port))
								.setText("" + m_params.save_port);
					((TextView) proxy_view.findViewById(R.id.user))
							.setText(m_params.proxy_username);
					((TextView) proxy_view.findViewById(R.id.pswd))
							.setText(m_params.proxy_passwd);
					dlg = new AlertDialog.Builder(SettingActivity.this)
							.setTitle("Proxy:")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(proxy_view)
							.setPositiveButton("OK", ocl_proxy)
							.setNegativeButton("Cancel", null).show();

				} else if (position == 4) {
					final View timer_view = m_inflater.inflate(
							R.layout.setting_timer, null);
					((TextView) timer_view
							.findViewById(R.id.timer_wifi_current_pair))
							.setText(""
									+ ((MyApp) getApplicationContext()).app_timer_wifi_period);
					((TextView) timer_view
							.findViewById(R.id.timer_mobile_current_pair))
							.setText(""
									+ ((MyApp) getApplicationContext()).app_timer_mobile_period);
					((TextView) timer_view
							.findViewById(R.id.timer_wifi_all_pair))
							.setText(""
									+ ((MyApp) getApplicationContext()).app_timer_wifi_period_all);
					((TextView) timer_view
							.findViewById(R.id.timer_mobile_all_pair))
							.setText(""
									+ ((MyApp) getApplicationContext()).app_timer_mobile_period_all);
					dlg = new AlertDialog.Builder(SettingActivity.this)
							.setTitle("Update Timer:")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(timer_view)
							.setPositiveButton("OK", ocl_timer)
							.setNegativeButton("Cancel", null).show();

				} else if (position == 5) {
					((MyApp) getApplicationContext()).app_update_all_pair_depth_trades = !((MyApp) getApplicationContext()).app_update_all_pair_depth_trades;
					update_list_data();
					m_settingList.setAdapter(new setting_list_Adapter(
							getApplicationContext()));
				}
				// else if (position == 4) {
				// final View candle_view = m_inflater.inflate(
				// R.layout.setting_candlesticks, null);
				// ((TextView) candle_view.findViewById(R.id.number))
				// .setText(""
				// + ((MyApp) getApplicationContext()).app_candlestick_number);
				// ((TextView) candle_view.findViewById(R.id.period))
				// .setText(""
				// + ((MyApp) getApplicationContext()).app_candlestick_period);
				// dlg = new AlertDialog.Builder(SettingActivity.this)
				// .setTitle("Candlestick:")
				// .setIcon(android.R.drawable.ic_dialog_info)
				// .setView(candle_view)
				// .setPositiveButton("OK", ocl_candle)
				// .setNegativeButton("Cancel", null).show();
				// }
			}
		});

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		m_params.proxy_port = true == this.enable_proxy ? m_params.save_port
				: -1;
		super.finish();
	}

	OnClickListener ocl_key = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			AlertDialog dlg = (AlertDialog) dialog;
			m_params.key = ((TextView) dlg.findViewById(R.id.value)).getText()
					.toString();
			update_list_data();
			m_settingList.setAdapter(new setting_list_Adapter(
					getApplicationContext()));
		}
	};
	OnClickListener ocl_sec = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			AlertDialog dlg = (AlertDialog) dialog;
			m_params.secret = ((TextView) dlg.findViewById(R.id.value))
					.getText().toString();
			update_list_data();
			m_settingList.setAdapter(new setting_list_Adapter(
					getApplicationContext()));
		}
	};
	OnClickListener ocl_proxy = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			AlertDialog dlg = (AlertDialog) dialog;
			m_params.proxy_host = ((TextView) dlg.findViewById(R.id.host))
					.getText().toString();
			try {
				m_params.save_port = Integer.parseInt(((TextView) dlg
						.findViewById(R.id.port)).getText().toString());
			} catch (NumberFormatException e) {
				m_params.save_port = -1;
			}
			m_params.proxy_username = ((TextView) dlg.findViewById(R.id.user))
					.getText().toString();
			m_params.proxy_passwd = ((TextView) dlg.findViewById(R.id.pswd))
					.getText().toString();
			update_list_data();
			m_settingList.setAdapter(new setting_list_Adapter(
					getApplicationContext()));
		}
	};

	OnClickListener ocl_timer = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			AlertDialog dlg = (AlertDialog) dialog;
			try {
				((MyApp) getApplicationContext()).app_timer_wifi_period = Integer
						.parseInt(((TextView) dlg
								.findViewById(R.id.timer_wifi_current_pair))
								.getText().toString());
			} catch (NumberFormatException e) {
				((MyApp) getApplicationContext()).app_timer_wifi_period = 1;
			}
			try {
				((MyApp) getApplicationContext()).app_timer_mobile_period = Integer
						.parseInt(((TextView) dlg
								.findViewById(R.id.timer_mobile_current_pair))
								.getText().toString());
			} catch (NumberFormatException e) {
				((MyApp) getApplicationContext()).app_timer_mobile_period = 5;
			}
			try {
				((MyApp) getApplicationContext()).app_timer_wifi_period_all = Integer
						.parseInt(((TextView) dlg
								.findViewById(R.id.timer_wifi_all_pair))
								.getText().toString());
			} catch (NumberFormatException e) {
				((MyApp) getApplicationContext()).app_timer_wifi_period_all = 2;
			}
			try {
				((MyApp) getApplicationContext()).app_timer_mobile_period_all = Integer
						.parseInt(((TextView) dlg
								.findViewById(R.id.timer_mobile_all_pair))
								.getText().toString());
			} catch (NumberFormatException e) {
				((MyApp) getApplicationContext()).app_timer_mobile_period_all = 0;
			}
			update_list_data();
			m_settingList.setAdapter(new setting_list_Adapter(
					getApplicationContext()));
		}
	};

	// OnClickListener ocl_candle = new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// AlertDialog dlg = (AlertDialog) dialog;
	// try {
	// ((MyApp) getApplicationContext()).app_candlestick_number = Integer
	// .parseInt(((TextView) dlg.findViewById(R.id.number))
	// .getText().toString());
	// } catch (NumberFormatException e) {
	// ((MyApp) getApplicationContext()).app_candlestick_number = 48;
	// }
	// try {
	// ((MyApp) getApplicationContext()).app_candlestick_period = Integer
	// .parseInt(((TextView) dlg.findViewById(R.id.period))
	// .getText().toString());
	// } catch (NumberFormatException e) {
	// ((MyApp) getApplicationContext()).app_candlestick_period = 1;
	// }
	// update_list_data();
	// m_settingList.setAdapter(new setting_list_Adapter(
	// getApplicationContext()));
	// }
	// };

	void update_list_data() {
		m_setting_data.clear();

		setting_item item = new setting_item();
		item.key = "Key";
		item.value = m_params.key.equals("") ? "null" : m_params.key;
		item.img_id = android.R.drawable.ic_menu_more;
		m_setting_data.add(item);

		item = new setting_item();
		item.key = "Secret";
		item.value = m_params.secret.equals("") ? "null" : m_params.secret;
		item.img_id = android.R.drawable.ic_menu_more;
		m_setting_data.add(item);

		item = new setting_item();
		item.key = "Proxy";
		item.value = "";
		if (enable_proxy)
			item.img_id = android.R.drawable.checkbox_on_background;
		else
			item.img_id = android.R.drawable.checkbox_off_background;
		m_setting_data.add(item);

		item = new setting_item();
		item.key = "Proxy Server";
		if (0 == m_params.save_port)
			item.value = "null";
		else
			item.value = m_params.proxy_host + ":" + m_params.save_port;
		item.img_id = android.R.drawable.ic_menu_more;
		item.enable = enable_proxy;
		m_setting_data.add(item);

		item = new setting_item();
		item.key = "Update Timer";
		item.value = ((MyApp) getApplicationContext()).app_timer_wifi_period
				+ " / "
				+ ((MyApp) getApplicationContext()).app_timer_mobile_period
				+ " / "
				+ ((MyApp) getApplicationContext()).app_timer_wifi_period_all
				+ " / "
				+ ((MyApp) getApplicationContext()).app_timer_mobile_period_all;
		item.img_id = android.R.drawable.ic_menu_more;
		m_setting_data.add(item);

		item = new setting_item();
		item.key = "Update all Pair";
		item.value = "";
		if (((MyApp) getApplicationContext()).app_update_all_pair_depth_trades)
			item.img_id = android.R.drawable.checkbox_on_background;
		else
			item.img_id = android.R.drawable.checkbox_off_background;
		m_setting_data.add(item);

		// item = new setting_item();
		// item.key = "Candlestick";
		// item.value = ((MyApp) getApplicationContext()).app_candlestick_number
		// + " * "
		// + ((MyApp) getApplicationContext()).app_candlestick_period
		// + "*30 minutes";
		// item.img_id = android.R.drawable.ic_menu_more;
		// m_setting_data.add(item);

	}

	/*--- ListAdapter for rendering JSON data ---*/
	private class setting_list_Adapter extends BaseAdapter {
		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return m_setting_data.get(position).enable;
		}

		public setting_list_Adapter(Context c) {
		}

		@Override
		public int getCount() {
			return m_setting_data.size();
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
			ImageView I;

			if (convertView == null)
				tv = m_inflater.inflate(R.layout.setting_item, parent, false);
			else
				tv = convertView;

			t = (TextView) tv.findViewById(R.id.set_key);
			t.setText(m_setting_data.get(pos).key);
			t = (TextView) tv.findViewById(R.id.set_value);
			t.setText(m_setting_data.get(pos).value);
			I = (ImageView) tv.findViewById(R.id.set_img);
			I.setImageResource(m_setting_data.get(pos).img_id);

			return tv;
		}

	}

}
