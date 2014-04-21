package com.googlecode.BtceClient;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.googlecode.BtceClient.R;
import com.spdffxyp.util.LimitingList;

public class LogViewActivity extends Activity {

	static final private int ID_CLEAR = Menu.FIRST;
	// static final private int ID_SAVE = Menu.FIRST + 1;

	TextView m_textview;
	LimitingList<String> m_logs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.log_view);

		m_textview = (TextView) findViewById(R.id.LogView);
		final ScrollView m_scrollview = (ScrollView) findViewById(R.id.LogscrollView);

		m_textview.setMovementMethod(ScrollingMovementMethod.getInstance());

		m_logs = ((MyApp) getApplicationContext()).app_logs;

		for (Object str : m_logs)
			m_textview.append(str + "\n");

		m_scrollview.post(new Runnable() {
			public void run() {
				m_scrollview.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		/* the context menu currently has only one option */
		menu.add(0, ID_CLEAR, 0, R.string.clear);
		// menu.add(0, ID_SAVE, 0, R.string.save);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Intent intent = new Intent();
		switch (item.getItemId()) {
		case ID_CLEAR:
			m_logs.clear();
			m_textview.setText("");
			return true;
			// case ID_SAVE:
			// return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
