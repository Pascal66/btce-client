package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.googlecode.BtceClient.R;

import static com.googlecode.BtceClient.IntroActivity.str_last_price;
import static com.googlecode.BtceClient.IntroActivity.str_value;
import static com.googlecode.BtceClient.IntroActivity.str_active_funds;

public class PairFoundActivity extends Activity {
	private Bundle extra_values, last_price, active_funds;
	private LayoutInflater m_inflater;
	List<String> titles;
	ListView m_thisList;
	DecimalFormat formatter8 = new DecimalFormat();
	double all_usd = 0, all_btc = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pair_found_view);

		formatter8.setMaximumFractionDigits(8);
		formatter8.setGroupingUsed(false);

		extra_values = this.getIntent().getBundleExtra(str_value);
		last_price = this.getIntent().getBundleExtra(str_last_price);
		active_funds = this.getIntent().getBundleExtra(str_active_funds);
		if (null != last_price) {
			all_usd = all_btc = 0;
			String usd = "usd", btc = "btc";
			double v = 0.0;
			for (String key : extra_values.keySet()) {
				v = extra_values.getDouble(key) + active_funds.getDouble(key);
				if (key.equals(usd)) {
					all_usd += v;
					all_btc += v / last_price.getDouble("btc_usd");
				} else if (key.equals(btc)) {
					all_btc += v;
					all_usd += v * last_price.getDouble("btc_usd");
				} else {
					double key_usd = v * last_price.getDouble(key + "_" + usd);
					double key_btc = v * last_price.getDouble(key + "_" + btc);
					if ((0 == Double.compare(key_usd, 0.0))
							&& (0 < Double.compare(key_btc, 0.0))) {
						key_usd = key_btc * last_price.getDouble("btc_usd");
					}
					all_usd += key_usd;
					all_btc += key_btc;
				}
			}
			extra_values.putDouble("ALL IN USD", all_usd);
			extra_values.putDouble("ALL IN BTC", all_btc);
		}
		m_thisList = (ListView) findViewById(R.id.report_list);
		m_inflater = LayoutInflater.from(this);
		titles = asSortedList(extra_values.keySet());
		m_thisList.setAdapter(new report_list_Adapter(getApplicationContext()));
		m_thisList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("pair", titles.get(position));
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

	/*--- ListAdapter for rendering JSON data ---*/
	private class report_list_Adapter extends BaseAdapter {
		public report_list_Adapter(Context c) {
		}

		@Override
		public int getCount() {
			return extra_values.size();
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
						.inflate(R.layout.pair_found_item, parent, false);
			else
				tv = convertView;

			t = (TextView) tv.findViewById(R.id.report_title);
			t.setText(titles.get(pos).toUpperCase());
			t = (TextView) tv.findViewById(R.id.report_info);
			String info = "";
			if (null != active_funds && pos > 1)
				info += formatter8.format(extra_values.getDouble(titles
						.get(pos)) + active_funds.getDouble(titles.get(pos)))
						+ "<br><font color='#FF0000'>"
						+ formatter8.format(active_funds.getDouble(titles
								.get(pos))) + "</font>";
			else
				info += formatter8.format(extra_values.getDouble(titles
						.get(pos)));
			t.setText(Html.fromHtml(info));
			return tv;
		}
	}
}
