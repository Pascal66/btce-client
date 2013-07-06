package com.googlecode.BtceClient;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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

	static final int item_number = 21;
	static final double item_step = 0.5;
	boolean is_sell = false;
	double price = 0;
	double fee = 0;
	double fee_percent = 0;

	DecimalFormat formatter6 = new DecimalFormat();

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
		fee = getIntent().getDoubleExtra("fee", 0.2);
		fee_percent = (1 - fee / 100) * (1 - fee / 100);

		formatter6.setMaximumFractionDigits(6);
		formatter6.setGroupingUsed(false);
		m_buy_sell.setChecked(is_sell);
		m_price.setText(formatter6.format(price));

		m_buy_sell.setOnClickListener(buy_or_sell_handler);
		m_price.addTextChangedListener(price_changed_handler);
		m_price_list.setAdapter(new price_list_Adapter(getApplicationContext()));
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
			if (str_price.equals("") ||  str_price.equals(".")) {
				str_price = "0";
			}
			price = Double.parseDouble(str_price);
			m_price_list.setAdapter(new price_list_Adapter(getApplicationContext()));
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
				tv = m_inflater
						.inflate(R.layout.price_item, parent, false);
			else
				tv = convertView;

//			t = (TextView) tv.findViewById(R.id.positive_percent);
//			t.setText("+" + pos * item_step + "%");
//			t = (TextView) tv.findViewById(R.id.negative_percent);
//			t.setText("-" + pos * item_step + "%");
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
