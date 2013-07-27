package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.googlecode.BtceClient.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DepthView extends View {

	Paint mPaint;
	long update_time = 0;
	List<depth_item> m_ask_items = new ArrayList<depth_item>();
	List<depth_item> m_bid_items = new ArrayList<depth_item>();
	String data = "{\"asks\":[[119.7,0.53992261],[119.96,1],[119.99,2.1305],[120,25.87303516],[120.049,0.1103],[120.05,0.06],[120.07,0.01],[120.08,0.252],[120.083,0.2206],[120.09,4.93836468],[120.091,0.014],[120.092,1.0998],[120.1,11.29695957],[120.101,0.01688904],[120.112,0.01688904],[120.113,0.122084],[120.122,0.01688476],[120.132,0.01688191],[120.133,0.1103],[120.142,0.01688049],[120.148,0.3309],[120.152,0.01687906],[120.162,0.01687764],[120.172,0.01687764],[120.183,0.12717479],[120.19,0.998],[120.193,0.01687337],[120.198,0.010978],[120.2,0.4207976],[120.203,0.01687194],[120.213,0.01687052],[120.22,0.01],[120.223,0.3477691],[120.224,0.1103],[120.23,0.01575495],[120.233,0.01686767],[120.243,0.01686625],[120.249,0.01],[120.25,0.52706774],[120.254,0.01686639],[120.264,0.01686639],[120.274,0.01686639],[120.284,0.01686639],[120.294,0.01686639],[120.298,0.257658],[120.3,0.01],[120.304,0.01686056],[120.314,0.01686056],[120.325,0.01686056],[120.33,0.20878201],[120.335,0.01686056],[120.345,0.01685204],[120.355,0.01685062],[120.36,0.1],[120.365,0.0168492],[120.366,0.05],[120.37,2.994],[120.375,0.01684778],[120.38,0.02],[120.381,0.08],[120.385,0.01684636],[120.393,0.01575442],[120.396,0.01684494],[120.4,0.342035],[120.402,0.01],[120.406,0.01684352],[120.41,0.08791632],[120.416,0.04684211],[120.42,0.05],[120.426,0.01684069],[120.43,0.05],[120.433,0.1],[120.435,0.06],[120.436,0.01683927],[120.44,0.1],[120.446,0.01684494],[120.45,0.1048588],[120.456,0.03683814],[120.46,0.04],[120.467,0.01683814],[120.468,0.01],[120.477,0.01683814],[120.487,0.01683814],[120.494,6.21912019],[120.497,0.01683814],[120.5,27.71185817],[120.507,0.01682935],[120.511,0.02],[120.515,0.03],[120.517,0.01682793],[120.52,0.01],[120.522,0.01],[120.527,0.01682652],[120.538,0.0168251],[120.54,0.010976],[120.546,0.01],[120.547,0.1],[120.548,0.01682369],[120.555,0.01572354],[120.557,0.079],[120.558,0.01682227],[120.562,0.05],[120.568,0.01682227],[120.57,0.01],[120.578,0.01682086],[120.58,0.02],[120.588,0.01682086],[120.59,0.01],[120.591,0.11],[120.598,0.01682086],[120.6,0.12],[120.604,0.05],[120.608,0.01682086],[120.619,0.01682086],[120.629,0.01681237],[120.639,0.01681096],[120.647,0.2],[120.649,0.01680955],[120.65,0.01],[120.659,0.01680814],[120.669,0.02680672],[120.67,0.02],[120.679,0.01680531],[120.69,0.0168039],[120.695,0.08],[120.696,0.10321042],[120.698,0.010978],[120.7,0.02680249],[120.702,0.01],[120.709,0.1],[120.71,0.03680108],[120.718,0.01572306],[120.72,0.03679966],[120.73,0.02680531],[120.74,0.08076646],[120.747,0.22],[120.75,5.17679868],[120.76,0.02],[120.761,0.01679402],[120.77,0.02994],[120.771,0.01679261],[120.781,0.0167912],[120.791,0.06678979],[120.798,0.06],[120.801,0.01679402],[120.806,0.01],[120.81,2.29334445],[120.811,0.01678697],[120.821,0.01678556],[120.83,0.08]]"
			+ ",\"bids\":[[119.6,0.27],[119.56,0.1321],[119.55,0.02243473],[119.522,0.01],[119.52,0.35996946],[119.515,0.05],[119.511,0.02],[119.51,0.2159],[119.5,0.39359999],[119.49,0.14612102],[119.47,0.15938308],[119.46,0.09],[119.45,0.1],[119.44,0.19163918],[119.416,0.05],[119.4,0.25729287],[119.381,0.03],[119.38,0.05],[119.36,0.2434624],[119.32,0.26815848],[119.28,0.31050202],[119.256,0.02],[119.24,0.34578398],[119.23,0.13],[119.2,0.31387702],[119.19,0.39694802],[119.16,0.44502099],[119.12,0.91412745],[119.11,0.5242017],[119.09,3.416],[119.08,10.47489684],[119.06,3.36835275],[119.051,0.01003],[119.05,4.389],[119.04,0.65122122],[119.022,0.13416991],[119.02,0.014],[119.018,0.16818491],[119.015,0.13401501],[119.011,0.12400768],[119.01,0.4805089],[119.007,0.08385013],[119.003,0.08384961],[119.002,0.85256807],[119.001,0.79761445],[119,2.63141712],[118.996,0.0840963],[118.992,0.08411859],[118.99,0.088],[118.989,0.08411173],[118.985,0.08411292],[118.981,0.0844422],[118.977,0.08422067],[118.974,0.08422017],[118.97,0.0838189],[118.966,0.22811476],[118.962,0.083813],[118.96,0.93494049],[118.959,0.08428686],[118.955,0.08414425],[118.951,0.08420092],[118.95,0.05],[118.949,0.01580728],[118.948,0.08420044],[118.946,0.06],[118.944,0.08420318],[118.94,0.08421614],[118.936,0.08421252],[118.935,0.06],[118.933,0.08419498],[118.93,0.14],[118.929,0.08419556],[118.925,0.08419783],[118.922,0.08419711],[118.918,0.08419685],[118.914,0.08419528],[118.911,0.08419924],[118.91,0.95364223],[118.907,0.08421029],[118.903,0.08419333],[118.9,0.04],[118.896,0.08417205],[118.892,0.08432254],[118.89,0.02],[118.888,0.08431592],[118.885,0.08431639],[118.881,0.08431747],[118.88,1.07084355],[118.877,0.08431637],[118.873,0.08417583],[118.87,0.08433619],[118.866,0.08420284],[118.862,0.08433859],[118.859,0.08434061],[118.855,0.08418937],[118.851,0.08418924],[118.847,0.08406513],[118.844,0.08425022],[118.84,0.08408215],[118.836,0.0840831],[118.833,1.05348949],[118.83,1.21132312],[118.829,0.0840939],[118.825,0.08411068],[118.821,0.08408785],[118.82,0.70796805],[118.818,0.08408746],[118.814,0.08402391],[118.81,1.71966657],[118.807,0.08402976],[118.803,0.08400055],[118.8,2.37367433],[118.799,0.08400327],[118.795,0.0841701],[118.792,0.08416012],[118.788,0.10013048],[118.78,1.02],[118.77,0.1],[118.76,1.55474171],[118.75,25],[118.72,1.76235976],[118.71,0.16386411],[118.7,0.1],[118.69,0.15],[118.68,2.18748553],[118.66,0.15338429],[118.65,6.2],[118.64,2.26119268],[118.627,0.19497288],[118.626,1.0563228],[118.611,0.86324475],[118.61,0.11003],[118.57,0.093],[118.53,0.15],[118.511,0.05],[118.51,0.06003],[118.502,0.011],[118.5,35.11263404],[118.484,0.05],[118.48,0.05],[118.477,0.1],[118.473,0.1],[118.469,0.05],[118.466,0.1],[118.465,0.01580858],[118.41,0.9689285],[118.4,9.2293018],[118.39,2.7],[118.38,1.76866859],[118.31,0.01003]]}";

	Path ask_path = new Path();
	Path bid_path = new Path();
	Path ask_path_open = new Path();
	Path bid_path_open = new Path();

	int bgColor = 0, frameColor = 0, gridColor = 0, textColor = 0;
	int bid_line_color = 0, bid_fill_color = 0, ask_line_color = 0,
			ask_fill_color = 0;
	int point_color = 0, focus_line_color = 0;;
	int point_size = 10, bid_line_size = 3, ask_line_size = 3;
	float textSize = 0;
	int text_infoColor = 0;
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

	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");
	// use for the touch event
	float bx = -1;
	float by = -1;
	float ex = bx;
	float ey = by;
	boolean mousedown = false;
	double amount_k = 0;
	double amount_b = 0;

	class depth_item {
		double price = 0;
		double amount = 0;
		double amount_sum = 0;
		float x_price = 0;
	};

	public int feedJosn_depth(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			return feedJosn_depth(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public int feedJosn_depth(JSONObject obj) {
		update_time = System.currentTimeMillis();
		m_ask_items.clear();
		m_bid_items.clear();
		double all_ask_amount = 0;
		double all_bid_amount = 0;
		try {
			JSONArray asks = obj.getJSONArray("asks");
			JSONArray bids = obj.getJSONArray("bids");
			int min_len = Math.min(asks.length(), bids.length());
			for (int i = 0; i < min_len; ++i) {
				JSONArray ask = asks.getJSONArray(i);
				JSONArray bid = bids.getJSONArray(i);
				depth_item a = new depth_item(), b = new depth_item();
				a.price = ask.getDouble(0);
				a.amount = ask.getDouble(1);
				all_ask_amount += a.amount;
				a.amount_sum = all_ask_amount;
				b.price = bid.getDouble(0);
				b.amount = bid.getDouble(1);
				all_bid_amount += b.amount;
				b.amount_sum = all_bid_amount;
				m_ask_items.add(a);
				m_bid_items.add(b);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int rt = m_ask_items.size();
		if (rt != 0) {
			depth_item m = new depth_item();
			m.price = (m_ask_items.get(0).price + m_bid_items.get(0).price) / 2;
			m_bid_items.add(0, m);
			m_ask_items.add(0, m);
		}
		update_path();
		this.invalidate();
		return rt;
	}

	public DepthView(Context context) {
		super(context);
	}

	public DepthView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		// TypedArray is a container of context.obtainStyledAttributes
		// after using it, call recycle
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.DepthView);

		bgColor = array.getColor(R.styleable.DepthView_dp_bgColor, 0XFF000000);
		frameColor = array.getColor(R.styleable.DepthView_dp_frameColor,
				0XFF483D8B);
		gridColor = array.getColor(R.styleable.DepthView_dp_gridColor,
				0XFF483D8B);
		textColor = array.getColor(R.styleable.DepthView_dp_textColor,
				0XFF00FF00);
		text_infoSize = array.getDimension(R.styleable.DepthView_dp_infoSize,
				12);
		text_infoColor = array.getColor(R.styleable.DepthView_dp_infoColor,
				0XFFD3D3D3);
		bid_line_color = array.getColor(R.styleable.DepthView_dp_bidlineColor,
				0XFF75B103);
		bid_line_size = array.getColor(R.styleable.DepthView_dp_bidlineSize, 3);
		bid_fill_color = array.getColor(R.styleable.DepthView_dp_bidfillColor,
				0X7F75B103);
		ask_line_color = array.getColor(R.styleable.DepthView_dp_asklineColor,
				0XFF4E8CD9);
		ask_line_size = array.getColor(R.styleable.DepthView_dp_asklineSize, 3);
		ask_fill_color = array.getColor(R.styleable.DepthView_dp_askfillColor,
				0X7F4E8CD9);
		point_color = array.getColor(R.styleable.DepthView_dp_pointColor,
				0XFF00FF00);
		point_size = array.getInteger(R.styleable.DepthView_dp_pointSize, 10);
		focus_line_color = array.getColor(
				R.styleable.DepthView_dp_focusLineColor, 0XFFFFFFFF);
		// mPaint.setColor(textColor);
		mPaint.setTextSize(textSize);
		mPaint.setAntiAlias(true);
		array.recycle();

		// feedJosn_depth(data);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		update_path();
	}

	public void set_chart_rect() {
		r_chart.set((int) (margin_left + y_text_width + margin_space),
				(int) (margin_top + text_infoSize + margin_space),
				this.getMeasuredWidth() - margin_right,
				this.getMeasuredHeight() - margin_bottom);
	}

	public void update_path() {
		ask_path.reset();
		bid_path.reset();
		// ask_path_open.reset();
		// bid_path_open.reset();
		if (m_ask_items.isEmpty())
			return;

		set_chart_rect();
		// Log.e("ttt", "update");
		double amount_V_max = Math.max(
				m_ask_items.get(m_ask_items.size() - 1).amount_sum,
				m_bid_items.get(m_bid_items.size() - 1).amount_sum);
		double price_V_min = m_bid_items.get(m_bid_items.size() - 1).price;
		double price_V_max = m_ask_items.get(m_ask_items.size() - 1).price;

		double amount_K_max = amount_V_max;
		double amount_K_min = 0;
		amount_k = (r_chart.top - r_chart.bottom)
				/ (amount_K_max - amount_K_min);
		amount_b = r_chart.top - amount_k * amount_K_max;

		double price_K_max = price_V_max;
		double price_K_min = price_V_min;
		double price_k = (r_chart.right - r_chart.left)
				/ (price_K_max - price_K_min);
		double price_b = r_chart.right - price_k * price_K_max;

		ask_path.moveTo((float) (m_ask_items.get(0).price * price_k + price_b),
				(float) (0 * amount_k + amount_b));
		bid_path.moveTo((float) (m_bid_items.get(0).price * price_k + price_b),
				(float) (0 * amount_k + amount_b));
		m_ask_items.get(0).x_price = (float) (m_ask_items.get(0).price
				* price_k + price_b);
		m_bid_items.get(0).x_price = (float) (m_bid_items.get(0).price
				* price_k + price_b);
		for (int i = 1; i < m_ask_items.size(); ++i) {
			m_ask_items.get(i).x_price = (float) (m_ask_items.get(i).price
					* price_k + price_b);
			m_bid_items.get(i).x_price = (float) (m_bid_items.get(i).price
					* price_k + price_b);
			ask_path.lineTo(
					m_ask_items.get(i).x_price,
					(float) (m_ask_items.get(i).amount_sum * amount_k + amount_b));
			bid_path.lineTo(
					m_bid_items.get(i).x_price,
					(float) (m_bid_items.get(i).amount_sum * amount_k + amount_b));
			// ask_path.rLineTo(
			// m_ask_items.get(i).x_price - m_ask_items.get(i - 1).x_price,
			// 0);
			// ask_path.rLineTo(0, (float) ((m_ask_items.get(i).amount_sum
			// * amount_k + amount_b) - (m_ask_items.get(i-1).amount_sum
			// * amount_k + amount_b)));
		}
		ask_path_open.set(ask_path);
		bid_path_open.set(bid_path);
		ask_path.lineTo(m_ask_items.get(m_ask_items.size() - 1).x_price,
				(float) (0 * amount_k + amount_b));
		bid_path.lineTo(m_bid_items.get(m_bid_items.size() - 1).x_price,
				(float) (0 * amount_k + amount_b));
		ask_path.close();
		bid_path.close();
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		// Log.e("onDraw",Integer.toString(width)+"x"+Integer.toString(height));
		mPaint.setTextSize(text_infoSize);
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(bgColor);
		canvas.drawRect(0, 0, width, height, mPaint);

		set_chart_rect();
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
		int info_text_y = (int) (margin_top + text_infoSize);
		if(0 < update_time){
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(text_infoColor);
			mPaint.setStrokeWidth(1);
			canvas.drawText(error_time_format.format(update_time), margin_left,info_text_y, mPaint);
		}
		if (m_ask_items.isEmpty() || m_bid_items.isEmpty())
			return;

		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setColor(ask_line_color);
		mPaint.setStrokeWidth(ask_line_size);
		canvas.drawPath(ask_path_open, mPaint);
		mPaint.setColor(bid_line_color);
		mPaint.setStrokeWidth(bid_line_size);
		canvas.drawPath(bid_path_open, mPaint);
		mPaint.setStrokeWidth(1);

		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(ask_fill_color);
		canvas.drawPath(ask_path, mPaint);
		mPaint.setColor(bid_fill_color);
		canvas.drawPath(bid_path, mPaint);

		int index = 1;
		String info = "Ask: ";
		List<depth_item> temp_reference = m_ask_items;
		if (m_bid_items.get(index).amount < m_ask_items.get(index).amount) {
			temp_reference = m_bid_items;
			info = "Bid: ";
		}
		// else {
		// temp_reference = m_ask_items;
		// info = "Ask: ";
		// }
		if (mousedown) {
			if (ex < m_bid_items.get(0).x_price) {
				for (int i = 1; i < m_bid_items.size(); ++i) {
					if (ex >= m_bid_items.get(i).x_price
							&& ex < m_bid_items.get(i - 1).x_price) {
						// draw the info text
						temp_reference = m_bid_items;
						info = "Bid: ";
						index = i;
						break;
					}
				}
			} else if (ex > m_ask_items.get(0).x_price) {
				for (int i = 1; i < m_ask_items.size(); ++i) {
					if (ex <= m_ask_items.get(i).x_price
							&& ex > m_ask_items.get(i - 1).x_price) {
						// draw the info text
						temp_reference = m_ask_items;
						info = "Ask: ";
						index = i;
						break;
					}
				}
			}
			mPaint.setColor(focus_line_color);
			canvas.drawLine(temp_reference.get(index).x_price, r_chart.top,
					temp_reference.get(index).x_price, r_chart.bottom, mPaint);
		}

		mPaint.setStrokeWidth(point_size);
		mPaint.setColor(point_color);
		canvas.drawPoint(
				temp_reference.get(index).x_price,
				(float) (temp_reference.get(index).amount_sum * amount_k + amount_b),
				mPaint);
		info += CandleStickView
				.my_formatter(temp_reference.get(index).price, 6)
				+ " / "
				+ CandleStickView.my_formatter(
						temp_reference.get(index).amount, 8)
				+ " / "
				+ CandleStickView.my_formatter(
						temp_reference.get(index).amount_sum, 8);
		mPaint.setColor(text_infoColor);
		canvas.drawText(info, width - mPaint.measureText(info), info_text_y,
				mPaint);
		mPaint.setStrokeWidth(1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		int action = ev.getAction();

		switch (action) {

		case MotionEvent.ACTION_DOWN:

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
			this.invalidate();
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
