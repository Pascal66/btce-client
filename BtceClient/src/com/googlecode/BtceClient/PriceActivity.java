package com.googlecode.BtceClient;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PriceActivity extends Activity {

	ToggleButton m_buy_sell;
	EditText m_price;
	ListView m_price_list;
	private LayoutInflater m_inflater;
	private InputMethodManager im_ctrl;

	int item_number = 41;
	double item_step = 0.5;
	boolean is_sell = false;
	double price = 0;
	double item_fee = 0;
	double fee_percent = 0;

	DecimalFormat formatter6 = new DecimalFormat();

	static final private int SETTING_ID = Menu.FIRST;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		/* the context menu currently has only one option */
		menu.add(0, SETTING_ID, 0, R.string.setting);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case SETTING_ID:
			// intent.setClass(PriceActivity.this, SettingActivity.class);
			// startActivityForResult(intent, SETTING_ID);
			final View candle_view = m_inflater.inflate(R.layout.setting_price,
					null);
			((TextView) candle_view.findViewById(R.id.number)).setText(""
					+ item_number);
			((TextView) candle_view.findViewById(R.id.step)).setText(""
					+ item_step);
			((TextView) candle_view.findViewById(R.id.fee)).setText(""
					+ item_fee);
			AlertDialog dlg = new AlertDialog.Builder(PriceActivity.this)
					.setTitle("Number & Step:")
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
											.findViewById(R.id.step)
											.getWindowToken(), 0);
									im_ctrl.hideSoftInputFromWindow(dlg
											.findViewById(R.id.fee)
											.getWindowToken(), 0);
								}
							}).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	android.content.DialogInterface.OnClickListener ocl_candle = new android.content.DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			AlertDialog dlg = (AlertDialog) dialog;
			try {
				item_number = Integer.parseInt(((TextView) dlg
						.findViewById(R.id.number)).getText().toString());
			} catch (NumberFormatException e) {
				item_number = 41;
			}
			try {
				item_step = Double.parseDouble(((TextView) dlg
						.findViewById(R.id.step)).getText().toString());
			} catch (NumberFormatException e) {
				item_step = 0.5;
			}
			try {
				item_fee = Double.parseDouble(((TextView) dlg
						.findViewById(R.id.fee)).getText().toString());
			} catch (NumberFormatException e) {
				item_fee = 0.2;
			}
			im_ctrl.hideSoftInputFromWindow(dlg.findViewById(R.id.number)
					.getWindowToken(), 0);
			im_ctrl.hideSoftInputFromWindow(dlg.findViewById(R.id.step)
					.getWindowToken(), 0);
			im_ctrl.hideSoftInputFromWindow(dlg.findViewById(R.id.fee)
					.getWindowToken(), 0);
			m_price_list.setAdapter(new price_list_Adapter(
					getApplicationContext()));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.price_view);

		m_buy_sell = (ToggleButton) findViewById(R.id.btn_buy_sell);
		m_price = (EditText) findViewById(R.id.price_buy_sell);
		m_price_list = (ListView) findViewById(R.id.price_list);
		m_inflater = LayoutInflater.from(this);

		is_sell = getIntent().getBooleanExtra("is_sell", false);
		price = getIntent().getDoubleExtra("price", 0);
		item_fee = getIntent().getDoubleExtra("fee", 0.2);

		formatter6.setMaximumFractionDigits(6);
		formatter6.setGroupingUsed(false);
		m_buy_sell.setChecked(is_sell);
		m_price.setText(formatter6.format(price));

		m_buy_sell.setOnClickListener(buy_or_sell_handler);
		m_price.addTextChangedListener(price_changed_handler);
		m_price_list
				.setAdapter(new price_list_Adapter(getApplicationContext()));

		im_ctrl = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	OnClickListener buy_or_sell_handler = new OnClickListener() {
		@Override
		public void onClick(View v) {
			is_sell = m_buy_sell.isChecked();
			price_changed_handler.afterTextChanged(null);
		}
	};

	// textMessage.addTextChangedListener(new TextWatcher(){
	TextWatcher price_changed_handler = new TextWatcher() {
		public void afterTextChanged(Editable s) {
			String str_price = m_price.getText().toString();
			if (str_price.equals("") || str_price.equals(".")) {
				str_price = "0";
			}
			price = Double.parseDouble(str_price);
			m_price_list.setAdapter(new price_list_Adapter(
					getApplicationContext()));
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};

	/*--- ListAdapter for rendering JSON data ---*/
	private class price_list_Adapter extends BaseAdapter {
		public price_list_Adapter(Context c) {
			fee_percent = (1 - item_fee / 100) * (1 - item_fee / 100);
		}

		@Override
		public int getCount() {
			return item_number;
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
				tv = m_inflater.inflate(R.layout.price_item, parent, false);
			else
				tv = convertView;

			// t = (TextView) tv.findViewById(R.id.positive_percent);
			// t.setText("+" + pos * item_step + "%");
			// t = (TextView) tv.findViewById(R.id.negative_percent);
			// t.setText("-" + pos * item_step + "%");
			t = (TextView) tv.findViewById(R.id.percent);
			t.setText(pos * item_step + "%");

			if (is_sell) {
				t = (TextView) tv.findViewById(R.id.positive_price);
				t.setText(formatter6.format(price * fee_percent
						* (1 - pos * item_step / 100)));
				t = (TextView) tv.findViewById(R.id.negative_price);
				t.setText(formatter6.format(price * fee_percent
						* (1 + pos * item_step / 100)));
			} else {
				t = (TextView) tv.findViewById(R.id.positive_price);
				t.setText(formatter6.format(price / fee_percent
						* (1 + pos * item_step / 100)));
				t = (TextView) tv.findViewById(R.id.negative_price);
				t.setText(formatter6.format(price / fee_percent
						* (1 - pos * item_step / 100)));
			}
			return tv;
		}

	}
}
