package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;

import com.googlecode.BtceClient.R;

public class CandleStickView extends View {

	private OnDoubleClickListener mListener;
	private long lastTouchTime = -1;

	public interface OnDoubleClickListener {
		void OnDoubleClickChart();

		void OnDoubleClickOther();
	}

	public void setOnDoubleClickListener(OnDoubleClickListener l) {
		mListener = l;
	}

	Paint mPaint;
	Vector<ChartItem> m_items = new Vector<ChartItem>();
	// Vector<pairs> m_asks = new Vector<pairs>();
	// Vector<pairs> m_bids = new Vector<pairs>();
	// String test_data =
	// "{\"chart_data\":[[\"16.05.13 21:30\", 2.62091, 2.649, 2.63, 2.64999],[\"16.05.13 22:00\", 2.6301, 2.63641, 2.649, 2.65011],[\"16.05.13 22:30\", 2.63641, 2.64, 2.63641, 2.6558],[\"16.05.13 23:00\", 2.6357, 2.64, 2.65, 2.65324],[\"16.05.13 23:30\", 2.63, 2.635, 2.64, 2.6558],[\"17.05.13 00:00\", 2.635, 2.65, 2.635, 2.65599],[\"17.05.13 00:30\", 2.64491, 2.64499, 2.65, 2.65],[\"17.05.13 01:00\", 2.63013, 2.64781, 2.64499, 2.65],[\"17.05.13 01:30\", 2.64791, 2.6499, 2.64791, 2.65],[\"17.05.13 02:00\", 2.6478, 2.657, 2.6499, 2.657],[\"17.05.13 02:30\", 2.63665, 2.65021, 2.6478, 2.657],[\"17.05.13 03:00\", 2.6, 2.64694, 2.65021, 2.66989],[\"17.05.13 03:30\", 2.62914, 2.6643, 2.64403, 2.6897],[\"17.05.13 04:00\", 2.65425, 2.66016, 2.66434, 2.67996],[\"17.05.13 04:30\", 2.65388, 2.65402, 2.6799, 2.6799],[\"17.05.13 05:00\", 2.65402, 2.668, 2.65402, 2.6839],[\"17.05.13 05:30\", 2.63156, 2.6316, 2.65406, 2.668],[\"17.05.13 06:00\", 2.6316, 2.684, 2.6316, 2.684],[\"17.05.13 06:30\", 2.669, 2.67, 2.6838, 2.684],[\"17.05.13 07:00\", 2.65407, 2.683, 2.669, 2.683],[\"17.05.13 07:30\", 2.66, 2.67, 2.68101, 2.684],[\"17.05.13 08:00\", 2.6621, 2.68, 2.67, 2.68],[\"17.05.13 08:30\", 2.67002, 2.687, 2.68, 2.69],[\"17.05.13 09:00\", 2.6702, 2.68, 2.686, 2.686],[\"17.05.13 09:30\", 2.67002, 2.68801, 2.67025, 2.69],[\"17.05.13 10:00\", 2.68799, 2.689, 2.68801, 2.712],[\"17.05.13 10:30\", 2.67503, 2.68896, 2.6881, 2.68996],[\"17.05.13 11:00\", 2.67503, 2.7, 2.68895, 2.7],[\"17.05.13 11:30\", 2.67006, 2.694, 2.6757, 2.69898],[\"17.05.13 12:00\", 2.67011, 2.6702, 2.69, 2.69],[\"17.05.13 12:30\", 2.67006, 2.67293, 2.67565, 2.67565],[\"17.05.13 13:00\", 2.669, 2.669, 2.67293, 2.67293],[\"17.05.13 13:30\", 2.66, 2.6688, 2.669, 2.669],[\"17.05.13 14:00\", 2.66, 2.669, 2.66499, 2.669],[\"17.05.13 14:30\", 2.661, 2.667, 2.668, 2.669],[\"17.05.13 15:00\", 2.66, 2.67296, 2.661, 2.67296],[\"17.05.13 15:30\", 2.66001, 2.67956, 2.67307, 2.6999],[\"17.05.13 16:00\", 2.69, 2.72, 2.69, 2.74],[\"17.05.13 16:30\", 2.704, 2.7201, 2.709, 2.73726],[\"17.05.13 17:00\", 2.70533, 2.70534, 2.73, 2.73],[\"17.05.13 17:30\", 2.70502, 2.71409, 2.7138, 2.72993],[\"17.05.13 18:00\", 2.714, 2.72736, 2.714, 2.74],[\"17.05.13 18:30\", 2.71502, 2.76702, 2.72737, 2.76702],[\"17.05.13 19:00\", 2.7501, 2.795, 2.76, 2.89],[\"17.05.13 19:30\", 2.78, 2.845, 2.8, 2.8789],[\"17.05.13 20:00\", 2.845, 2.98, 2.845, 3.19001],[\"17.05.13 20:30\", 2.88, 3.02, 2.98, 3.0537],[\"17.05.13 21:00\", 2.88, 2.98011, 3.02, 3.05],[\"17.05.13 21:30\", 2.9815, 3.05, 2.9815, 3.05]]}";

	// margin of this view
	int margin_left = 5;
	int margin_right = 5;
	int margin_top = 5;
	int margin_bottom = 5;
	float text_infoSize = 0;
	int margin_space = 5;// margin of items in this view
	int margin_K = 4;// space of the candle block
	int width_K = 10;// width of the candle block
	int last_k_index = 0;// last index+1 of the visible blocks
	int temp_last_k_index = last_k_index;// temp save the last index when mouse
											// is down
	float y_text_width = 0;// width of text on Y axis
	DecimalFormat formatter5 = new DecimalFormat();
	DecimalFormat formatter2 = new DecimalFormat();
	Rect r_chart = new Rect();
	int num_rows = 4;// grid of this view
	int k_times = 12;// candlestick period, k_times*30 minutes
	boolean show_price_line = false;
	boolean show_volume_bar = false;
	Rect r_block = new Rect();
	PathEffect dash_effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1); // dash
																					// line

	float textSize = 0;
	int textColor = 0;

	int bgColor = 0;
	int frameColor = 0;
	int gridColor = 0;
	int block_upColor = 0;
	int block_downColor = 0;
	int block_lineColor = 0;
	int text_xColor = 0;
	int text_yColor = 0;
	int text_infoColor = 0;

	// use for the touch event
	float bx = -1;
	float by = -1;
	float ex = bx;
	float ey = by;
	boolean mousedown = false;
	SimpleDateFormat chart_date_format = new SimpleDateFormat("dd.MM.yy HH:mm",
			Locale.UK);
	SimpleDateFormat print_date_format = new SimpleDateFormat("dd.MM.yy HH:mm",
			Locale.UK);
	Date temp_date = new Date();
	Path price_path = new Path();

	public static class ChartItem {
		long time = 0;
		double open = 0;
		double close = 0;
		double high = 0;
		double low = 0;
		double volume = 0;
		double volume_currency = 0;
		double w_price = 0;
	}

	// private class pairs {
	// double price = 0;
	// double amount = 0;
	// }

	public CandleStickView(Context context) {
		super(context);
	}

	public CandleStickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		// TypedArray is a container of context.obtainStyledAttributes
		// after using it, call recycle
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.CandleStickView);
		textColor = array.getColor(R.styleable.CandleStickView_textColor,
				0XFF00FF00);
		textSize = array.getDimension(R.styleable.CandleStickView_textSize, 36);
		// mPaint.setColor(textColor);
		mPaint.setTextSize(textSize);
		mPaint.setAntiAlias(true);

		bgColor = array.getColor(R.styleable.CandleStickView_bgColor,
				0XFF000000);
		frameColor = array.getColor(R.styleable.CandleStickView_frameColor,
				0XFF483D8B);
		gridColor = array.getColor(R.styleable.CandleStickView_gridColor,
				0XFF483D8B);
		block_upColor = array.getColor(
				R.styleable.CandleStickView_block_upColor, 0XFF008000);
		block_downColor = array.getColor(
				R.styleable.CandleStickView_block_downColor, 0XFFFF0000);
		block_lineColor = array.getColor(
				R.styleable.CandleStickView_block_lineColor, 0XFFFF00FF);
		text_xColor = array.getColor(R.styleable.CandleStickView_text_xColor,
				0XFF483D8B);
		text_yColor = array.getColor(R.styleable.CandleStickView_text_yColor,
				0XFF483D8B);
		text_infoColor = array.getColor(
				R.styleable.CandleStickView_text_infoColor, 0XFFD3D3D3);
		text_infoSize = array.getDimension(
				R.styleable.CandleStickView_text_infoSize, 12);

		array.recycle();
		initData();
	}

	private void initData() {
		y_text_width = mPaint.measureText("0.00000");
		formatter5.setMaximumFractionDigits(5);// according to the "0.00000"
												// above
		formatter5.setGroupingUsed(false);
		formatter2.setMaximumFractionDigits(2);
		formatter2.setGroupingUsed(false);
		chart_date_format.setTimeZone(TimeZone.getTimeZone("GMT+4:00"));
		print_date_format.setTimeZone(TimeZone.getDefault());
	}

	public static String my_formatter(double d, int bits) {
		DecimalFormat formatter = new DecimalFormat();
		formatter.setMaximumFractionDigits(bits);
		formatter.setGroupingUsed(false);
		String s = formatter.format(d);
		int len = s.length();
		int i = s.indexOf('.');
		if (-1 == i) {
			if (len < bits)
				s = s + ".";
			for (int t = len; t < bits; ++t)
				s = s + "0";
		} else if (bits <= i) {
			s = s.substring(0, i);
		} else {
			s = s.substring(0, s.length() > bits ? bits + 1 : s.length());
			for (int t = s.length(); t <= bits; ++t)
				s = s + "0";
		}
		return s;
	}

	public int feedJosn_chart(Vector<ChartItem> items) {
		m_items.clear();
		if (1 < k_times)
			m_items = translate_items(items);
		else
			m_items = items;
		last_k_index = m_items.size();
		this.invalidate();
		return m_items.size();
	}

	public Vector<ChartItem> update_items(String json_string) {
		JSONObject obj = new JSONObject();
		try {
			obj = new JSONObject(json_string);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return update_items(obj);
	}

	public Vector<ChartItem> update_items(JSONObject obj) {
		JSONArray chart_datas = new JSONArray();
		try {
			chart_datas = obj.getJSONArray("chart_data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return update_items(chart_datas);

	}

	public Vector<ChartItem> update_items_bitcoincharts(JSONArray chart_datas) {
		Vector<ChartItem> items = new Vector<ChartItem>();
		try {
			for (int i = 0; i < chart_datas.length(); ++i) {
				JSONArray jitem = chart_datas.getJSONArray(i);
				ChartItem item = new ChartItem();
				item.time = jitem.getLong(0);
				item.open = jitem.getDouble(1);
				item.high = jitem.getDouble(2);
				item.low = jitem.getDouble(3);
				item.close = jitem.getDouble(4);
				item.volume = jitem.getDouble(5);
				item.volume_currency = jitem.getDouble(6);
				item.w_price = jitem.getDouble(7);
				items.add(item);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return items;
	}

	public Vector<ChartItem> update_items(JSONArray chart_datas) {
		Vector<ChartItem> items = new Vector<ChartItem>();
		try {
			for (int i = 0; i < chart_datas.length(); ++i) {
				JSONArray jitem = chart_datas.getJSONArray(i);
				ChartItem item = new ChartItem();

				String chart_time = jitem.getString(0);
				try {
					item.time = chart_date_format.parse(chart_time).getTime() / 1000;
					// Log.e("time",""+item.time+" vs "+System.currentTimeMillis()
					// / 1000);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				item.low = jitem.getDouble(1);
				item.close = jitem.getDouble(2);
				item.open = jitem.getDouble(3);
				item.high = jitem.getDouble(4);
				item.w_price = item.close;
				items.add(item);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return items;
	}

	public Vector<ChartItem> translate_items(Vector<ChartItem> original) {
		if (original.isEmpty())
			return original;
		Vector<ChartItem> rtvalue = new Vector<ChartItem>();
		ChartItem new_item = new ChartItem();
		for (int i = 0; i < original.size() - 1; ++i) {
			if (0 == new_item.time) {
				new_item.time = original.get(i).time / (k_times * 1800)
						* (k_times * 1800);
				new_item.open = original.get(i).open;
				new_item.high = original.get(i).high;
				new_item.low = original.get(i).low;
				new_item.close = original.get(i).close;
				new_item.volume = original.get(i).volume;
				new_item.volume_currency = original.get(i).volume_currency;
			} else {
				new_item.high = Math.max(original.get(i).high, new_item.high);
				new_item.low = Math.min(original.get(i).low, new_item.low);
				new_item.volume += original.get(i).volume;
				new_item.volume_currency += original.get(i).volume_currency;
			}
			// plus 1800(30 minutes)
			if ((original.get(i + 1).time - new_item.time) >= k_times * 1800) {
				// if (0 == (original.get(i).time + 1800) % (k_times * 1800)) {
				new_item.close = original.get(i).close;
				new_item.w_price = 0 == Double.compare(new_item.volume, 0.0) ? new_item.close
						: new_item.volume_currency / new_item.volume;
				rtvalue.add(new_item);
				new_item = new ChartItem();
				continue;
			}
		}
		int j = original.size() - 1;
		if (0 != new_item.time) {
			new_item.high = Math.max(original.get(j).high, new_item.high);
			new_item.low = Math.min(original.get(j).low, new_item.low);
		} else {
			new_item.time = original.get(j).time / (k_times * 1800)
					* (k_times * 1800);
			new_item.open = original.get(j).open;
			new_item.high = original.get(j).high;
			new_item.low = original.get(j).low;
		}
		new_item.close = original.get(j).close;
		new_item.volume += original.get(j).volume;
		new_item.volume_currency += original.get(j).volume_currency;
		new_item.w_price = 0 == Double.compare(new_item.volume, 0.0) ? new_item.close
				: new_item.volume_currency / new_item.volume;
		rtvalue.add(new_item);
		return rtvalue;
	}

	public int feedJosn_chart(JSONArray chart_datas) {
		return feedJosn_chart(update_items(chart_datas));
	}

	public int feedJosn_chart(JSONObject obj) {
		return feedJosn_chart(update_items(obj));
	}

	public int feedJosn_chart(String json_string) {
		return feedJosn_chart(update_items(json_string));
	}

	//
	// public int feedJosn_depth(JSONObject obj) {
	// m_asks.clear();
	// m_bids.clear();
	// try {
	// JSONArray asks = obj.getJSONArray("asks");
	// JSONArray bids = obj.getJSONArray("bids");
	// for (int i = 0; i < asks.length(); ++i) {
	// JSONArray ask_item = asks.getJSONArray(i);
	// pairs item = new pairs();
	// item.price = ask_item.getDouble(0);
	// item.amount = ask_item.getDouble(1);
	// m_asks.add(item);
	// }
	// for (int i = 0; i < bids.length(); ++i) {
	// JSONArray bid_item = asks.getJSONArray(i);
	// pairs item = new pairs();
	// item.price = bid_item.getDouble(0);
	// item.amount = bid_item.getDouble(1);
	// m_bids.add(item);
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return m_asks.size();
	//
	// }

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		// Log.e("onDraw",Integer.toString(width)+"x"+Integer.toString(height));
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(bgColor);
		canvas.drawRect(0, 0, width, height, mPaint);

		int text_lines = 1;
		if (width < mPaint
				.measureText("H:0.00000 L:0.00000 O:0.00000 C:0.00000 T:00.00.00 00:00 V:0.00000 C:0.00000 P:0.00000"))
			text_lines = 2;
		r_chart.set((int) (margin_left + y_text_width + margin_space),
				(int) (margin_top + text_lines * text_infoSize + margin_space),
				width - margin_right, height - margin_bottom);
		// draw the axis
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(1);
		mPaint.setColor(frameColor);
		canvas.drawRect(r_chart, mPaint);
		mPaint.setColor(gridColor);
		// draw grid line
		for (int i = 1; i < num_rows; ++i) {
			canvas.drawLine(r_chart.left, r_chart.top + i * r_chart.height()
					/ num_rows, r_chart.right,
					r_chart.top + i * r_chart.height() / num_rows, mPaint);
		}
		if (m_items.isEmpty())
			return;

		// build candle blocks
		int number_k = (int) ((width - margin_left - margin_right - margin_K - y_text_width) / (margin_K + width_K));
		number_k = number_k > m_items.size() ? m_items.size() : number_k;
		last_k_index = last_k_index < number_k ? number_k : last_k_index;
		int start_k_index = 0 > last_k_index - number_k ? 0 : last_k_index
				- number_k;
		double V_min = m_items.get(start_k_index).low;
		double V_max = m_items.get(start_k_index).high;
		double V_volume_max = m_items.get(start_k_index).volume;
		for (int i = start_k_index + 1; i < last_k_index; ++i) {
			V_min = Math.min(V_min, m_items.get(i).low);
			V_max = Math.max(V_max, m_items.get(i).high);
			V_volume_max = Math.max(V_volume_max, m_items.get(i).volume);
		}
		double V_y_space = (V_max - V_min) / num_rows;
		V_min -= V_y_space / 50;
		V_max += V_y_space / 50;
		int num_zero = 0;
		for (; num_zero < 5; ++num_zero) {
			if (0 != Math.floor(Math.pow(10, num_zero) * V_y_space))
				break;
		}
		double K_min = Math.floor(Math.pow(10, num_zero) * V_min)
				/ (Math.pow(10, num_zero));
		// double K_max = Math.ceil(Math.pow(10, num_zero) * V_max)
		// / (Math.pow(10, num_zero));
		// double K_y_space = (K_max - K_min) / num_rows;
		V_y_space = (V_max - K_min) / num_rows;
		V_y_space = V_y_space < 0.00001 ? 0.00001 : V_y_space;
		double K_y_space = 0;
		if (5 <= num_zero)
			K_y_space = Math.ceil(Math.pow(10, num_zero) * V_y_space)
					/ (Math.pow(10, num_zero));
		else
			K_y_space = Math.ceil(2 * Math.pow(10, num_zero) * V_y_space)
					/ (2 * Math.pow(10, num_zero));
		double K_max = K_min + num_rows * K_y_space;
		double K_volume_max = Math.ceil(V_volume_max);
		K_volume_max = 0 == Double.compare(K_volume_max, 0.0) ? 1
				: K_volume_max;
		// Log.e("draw","V_min"+V_min+"V_max"+V_max+"V_y_space"+V_y_space+"K_min"+K_min+"K_max"+K_max+"K_y_space"+K_y_space);

		// draw candlestick blocks
		int focus_k_index = -1;
		double k = (r_chart.top - r_chart.bottom) / (K_max - K_min);
		double b = r_chart.top - k * K_max;
		double k_volume = (r_chart.top - r_chart.bottom) / (K_volume_max - 0);
		double b_volume = r_chart.top - k_volume * K_volume_max;
		// Log.d("t",
		// "V_max"+Double.toString(V_max)+",V_min:"+Double.toString(V_min)+",V_y_space:"+Double.toString(V_y_space));
		// Log.d("t",
		// "K_max"+Double.toString(K_max)+",K_min:"+Doublreturn_list.add(0,
		// item);e.toString(K_min)+",K_y_space:"+Double.toString(K_y_space));
		// Log.d("t", "k"+Double.toString(k)+",b:"+Double.toString(b));
		// from left to right
		price_path.reset();
		for (int i = start_k_index, px = r_chart.left + margin_K; i < last_k_index; ++i, px += width_K
				+ margin_K) {
			// from right to left
			// for (int i = last_k_index -1 , px = r_chart.right - margin_K -
			// width_K; px > r_chart.left + margin_K ; --i, px -= width_K
			// + margin_K) {
			if (i > start_k_index
					&& k_times * 1800 < m_items.get(i).time
							- m_items.get(i - 1).time) {
				mPaint.setPathEffect(dash_effects);
				mPaint.setColor(text_infoColor);
				canvas.drawLine(px - margin_K / 2, r_chart.top, px - margin_K
						/ 2, r_chart.bottom, mPaint);
				mPaint.setPathEffect(null);
			}
			r_block.set(px, (int) (k * m_items.get(i).open + b), px + width_K,
					(int) (k * m_items.get(i).close + b));
			mPaint.setColor(block_lineColor);
			canvas.drawLine(r_block.centerX(),
					(int) (k * m_items.get(i).high + b), r_block.centerX(),
					(int) (k * m_items.get(i).low + b), mPaint);

			mPaint.setStyle(Style.FILL_AND_STROKE);
			if (r_block.top < r_block.bottom) {
				mPaint.setColor(block_downColor);
			} else {// swap left top and bottom
				int a = r_block.top;
				r_block.top = r_block.bottom;
				r_block.bottom = a;
				mPaint.setColor(block_upColor);
			}
			canvas.drawRect(r_block, mPaint);

			// draw volume block
			if (show_volume_bar) {
				mPaint.setColor(mPaint.getColor() & 0X7FFFFFFF);
				r_block.set(px + 1,
						(int) (k_volume * m_items.get(i).volume + b_volume), px
								+ width_K - 1, (int) (k_volume * 0 + b_volume));
				canvas.drawRect(r_block, mPaint);
			}
			// update price line
			if (show_price_line) {
				if (i == start_k_index) {
					price_path.moveTo(r_block.centerX(),
							(int) (k * m_items.get(i).w_price + b));
				} else {
					price_path.lineTo(r_block.centerX(),
							(int) (k * m_items.get(i).w_price + b));
				}
			}
			// draw line when mouse down
			if (mousedown
					&& -1 == focus_k_index
					&& Math.abs(r_block.centerX() - ex) <= (width_K + margin_K) / 2) {
				focus_k_index = i;
				mPaint.setColor(text_infoColor);
				canvas.drawLine(r_block.centerX(), r_chart.top,
						r_block.centerX(), r_chart.bottom, mPaint);
			}
		}
		if (show_price_line) {
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(1);
			mPaint.setColor(0XFF00FF00);
			canvas.drawPath(price_path, mPaint);
		}

		// draw text of the Y axis
		mPaint.setStyle(Style.FILL_AND_STROKE);
		mPaint.setColor(text_yColor);
		mPaint.setStrokeWidth(1);
		canvas.drawText(formatter5.format(K_min + 0 * K_y_space), margin_left,
				r_chart.top + (num_rows - 0) * r_chart.height() / num_rows
						- textSize, mPaint);
		canvas.drawText(formatter5.format(K_min + num_rows * K_y_space),
				margin_left,
				r_chart.top + (num_rows - num_rows) * r_chart.height()
						/ num_rows + textSize - 5, mPaint);
		for (int i = 1; i < num_rows; ++i) {
			canvas.drawText(formatter5.format(K_min + i * K_y_space),
					margin_left,
					r_chart.top + (num_rows - i) * r_chart.height() / num_rows,
					mPaint);
		}

		mPaint.setColor(0XFF008000);
		canvas.drawText(formatter5.format(0 * K_volume_max / num_rows),
				margin_left, r_chart.top + (num_rows - 0) * r_chart.height()
						/ num_rows, mPaint);
		canvas.drawText(formatter5.format(num_rows * K_volume_max / num_rows),
				margin_left,
				r_chart.top + (num_rows - num_rows) * r_chart.height()
						/ num_rows + 2 * textSize - 5, mPaint);
		for (int i = 1; i < num_rows; ++i) {
			canvas.drawText(formatter5.format(i * K_volume_max / num_rows),
					margin_left,
					r_chart.top + (num_rows - i) * r_chart.height() / num_rows
							+ textSize, mPaint);
		}

		// draw the info text
		int info_text_y = (int) (margin_top + text_infoSize);
		if (-1 == focus_k_index)
			focus_k_index = m_items.size() - 1;
		ChartItem item = m_items.get(focus_k_index);
		temp_date.setTime(item.time * 1000);
		mPaint.setColor(text_infoColor);
		if (1 == text_lines) {
			String info = "T:" + print_date_format.format(temp_date) + " O:"
					+ my_formatter(item.open, 6) + " H:"
					+ my_formatter(item.high, 6) + " L:"
					+ my_formatter(item.low, 6) + " C:"
					+ my_formatter(item.close, 6) + " V:"
					+ my_formatter(item.volume, 6) + " C:"
					+ my_formatter(item.volume_currency, 6) + " P:"
					+ my_formatter(item.w_price, 6);
			canvas.drawText(info, width - mPaint.measureText(info),
					info_text_y - 3, mPaint);
		} else {
			String info = " O:" + my_formatter(item.open, 6) + " H:"
					+ my_formatter(item.high, 6) + " L:"
					+ my_formatter(item.low, 6) + " C:"
					+ my_formatter(item.close, 6);
			canvas.drawText(info, width - mPaint.measureText(info),
					info_text_y, mPaint);
			info = "T:" + print_date_format.format(temp_date) + " V:"
					+ my_formatter(item.volume, 6) + " C:"
					+ my_formatter(item.volume_currency, 6) + " P:"
					+ my_formatter(item.w_price, 6);
			canvas.drawText(info, width - mPaint.measureText(info),
					2 * info_text_y - 3, mPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		int action = ev.getAction();

		switch (action) {

		case MotionEvent.ACTION_DOWN:

			// Log.d(TAG, "onTouchEvent2 action:ACTION_DOWN");
			long thisTime = System.currentTimeMillis();
			if (thisTime - lastTouchTime < 250 && Math.abs(ev.getX() - bx) < 50
					&& Math.abs(ev.getY() - by) < 50) {
				// Double click
				if (mListener != null) {
					if (r_chart.contains((int) ev.getX(), (int) ev.getY()))
						mListener.OnDoubleClickChart(); // post double click
														// message
					// to update the pair
					// information
					else
						mListener.OnDoubleClickOther();
				}
				lastTouchTime = -1;
			} else {
				// too slow
				lastTouchTime = thisTime;
			}

			mousedown = true;
			ex = bx = ev.getX();
			ey = by = ev.getY();
			temp_last_k_index = last_k_index;// save the last index when mouse
												// down
			this.invalidate();

			break;

		case MotionEvent.ACTION_MOVE:

			ex = ev.getX();
			ey = ev.getY();
			int t = (int) ((ex - bx) / (margin_K + width_K));
			if (0 != t) {
				t = temp_last_k_index - t;
				if (m_items.size() < t)
					t = m_items.size();
				else if (1 > t)
					t = 1;
				last_k_index = t;// update the last index
				this.invalidate();

			}
			// Log.d(TAG, Float.toString(bx) +"\tto\t"+ Float.toString(ex) + " "
			// +Integer.toString(t));
			// Log.d(TAG, "onTouchEvent2 action:ACTION_MOVE" + " "
			// +Integer.toString(last_k_index));

			break;

		case MotionEvent.ACTION_UP:

			// Log.d(TAG, "onTouchEvent2 action:ACTION_UP");

			mousedown = false;
			this.invalidate();
			break;

		case MotionEvent.ACTION_CANCEL:

			// Log.d(TAG, "onTouchEvent2 action:ACTION_CANCEL");

			break;

		}
		boolean b = true;
		// Log.d(TAG, "onTouchEvent2 return:"+b);
		return b;
	}

}
