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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.googlecode.BtceClient.R;

public class PairFoundActivity extends Activity {
	private Bundle extra_values = new Bundle();
	private LayoutInflater m_inflater;
	List<String> titles;
	ListView m_thisList;
	DecimalFormat formatter7 = new DecimalFormat();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pair_found_view);

		formatter7.setMaximumFractionDigits(7);
		formatter7.setGroupingUsed(false);
		
		extra_values = this.getIntent().getExtras();
		m_thisList = (ListView) findViewById(R.id.report_list);
		m_inflater = LayoutInflater.from(this);
		titles =  asSortedList(extra_values.keySet());
		m_thisList.setAdapter(new report_list_Adapter(getApplicationContext()));
		m_thisList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("result", titles.get(position));
				setResult(RESULT_OK, intent);
				finish();
				
			}});
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
				tv = m_inflater.inflate(R.layout.pair_found_item, parent, false);
			else
				tv = convertView;

			t = (TextView) tv.findViewById(R.id.report_title);
			t.setText(titles.get(pos).toUpperCase());
			t = (TextView) tv.findViewById(R.id.report_info);
			t.setText(""+formatter7.format(extra_values.getDouble(titles.get(pos))));

			return tv;
		}

	}
}
