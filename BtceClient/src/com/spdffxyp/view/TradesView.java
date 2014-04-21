package com.spdffxyp.view;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.btce.database.DBManager.trades_item;
import com.googlecode.BtceClient.R;
//import com.spdffxyp.view.DepthView.OnDoubleClickListener;

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

public class TradesView extends View {
	private OnDoubleClickListener mListener;
	private long lastTouchTime = -1;

	Paint mPaint;
	long update_time = 0;

	Path price_path = new Path();
	Path cur_path = new Path();

	int bgColor = 0, frameColor = 0, gridColor = 0, textColor = 0;
	int bid_line_color = 0, bid_fill_color = 0, ask_line_color = 0,
			ask_fill_color = 0, price_line_color = 0, cur_line_color = 0;
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
	int margin_K = 2;// space of the candle block
	int width_K = 10;// width of the candle block
	int last_k_index = 0;// last index+1 of the visible blocks
	int temp_last_k_index = last_k_index;// temp save the last index when mouse
											// is down
	float y_text_width = 0;// width of text on Y axis
	DecimalFormat formatter5 = new DecimalFormat();
	DecimalFormat formatter2 = new DecimalFormat();
	Rect r_chart = new Rect();
	Rect r_block = new Rect();
	int num_rows = 4;// grid of this view

	// use for the touch event
	float bx = -1;
	float by = -1;
	float ex = bx;
	float ey = by;
	boolean mousedown = false;
	double amount_k = 0;
	double amount_b = 0;

	double amount_V_max;
	double amount_V_min;
	double price_V_min;
	double price_V_max;
	double cur_V_min;
	double cur_V_max;

	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");

	public List<trades_item> m_trades_items = new ArrayList<trades_item>();

	public interface OnDoubleClickListener {
		void OnDoubleClick();
	}

	public void setOnDoubleClickListener(OnDoubleClickListener l) {
		mListener = l;
	}
	
	public int feedJosn_trades(String string) throws JSONException {
		update_time = System.currentTimeMillis();
		m_trades_items.clear();
		cur_V_max = amount_V_max = amount_V_min = price_V_max = 0;
		cur_V_min = price_V_min = Double.MAX_VALUE;
		JSONArray obj = null;
		try {
			obj = new JSONArray(string);
		} catch (JSONException e) {
			try {
				JSONObject error = new JSONObject(string);
				return 0;
				// }
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return 0;
			}
		}
		try {
			for (int i = 0; i < obj.length(); ++i) {
				trades_item item = new trades_item();
				JSONObject e = obj.getJSONObject(i);
				item.amount = e.getDouble("amount");
				item.date = e.getLong("date");
				item.price = e.getDouble("price");
				item.tid = e.getLong("tid");
				item.trade_type = e.getString("trade_type").equals("ask") ? 0
						: 1;
				m_trades_items.add(item);
				price_V_max = price_V_max < item.price ? item.price
						: price_V_max;
				price_V_min = price_V_min > item.price ? item.price
						: price_V_min;
				cur_V_max = cur_V_max < item.price * item.amount ? item.price
						* item.amount : cur_V_max;
				cur_V_min = cur_V_min > item.price * item.amount ? item.price
						* item.amount : cur_V_min;
				amount_V_max = amount_V_max < item.amount ? item.amount
						: amount_V_max;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (e);
		}
		this.invalidate();
		return m_trades_items.size();
	}

	public TradesView(Context context) {
		super(context);
	}

	public TradesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		// TypedArray is a container of context.obtainStyledAttributes
		// after using it, call recycle
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.TradesView);

		bgColor = array.getColor(R.styleable.TradesView_td_bgColor, 0XFF000000);
		frameColor = array.getColor(R.styleable.TradesView_td_frameColor,
				0XFF483D8B);
		gridColor = array.getColor(R.styleable.TradesView_td_gridColor,
				0XFF483D8B);
		textColor = array.getColor(R.styleable.TradesView_td_textColor,
				0XFF00FF00);
		text_infoSize = array.getDimension(R.styleable.TradesView_td_infoSize,
				12);
		text_infoColor = array.getColor(R.styleable.TradesView_td_infoColor,
				0XFFD3D3D3);
		bid_line_color = array.getColor(R.styleable.TradesView_td_bidlineColor,
				0XFF75B103);
		bid_line_size = array
				.getDimensionPixelSize(R.styleable.TradesView_td_bidlineSize, 3);
		bid_fill_color = array.getColor(R.styleable.TradesView_td_bidfillColor,
				0X7F75B103);
		ask_line_color = array.getColor(R.styleable.TradesView_td_asklineColor,
				0XFF4E8CD9);
		price_line_color = array.getColor(
				R.styleable.TradesView_td_pricelineColor, 0X00FF00FF);
		cur_line_color = array.getColor(R.styleable.TradesView_td_curlineColor,
				0XFFFF0000);
		ask_line_size = array
				.getDimensionPixelSize(R.styleable.TradesView_td_asklineSize, 3);
		ask_fill_color = array.getColor(R.styleable.TradesView_td_askfillColor,
				0X7F4E8CD9);
		point_color = array.getColor(R.styleable.TradesView_td_pointColor,
				0XFF00FF00);
		point_size = array.getDimensionPixelSize(R.styleable.TradesView_td_pointSize, 10);
		focus_line_color = array.getColor(
				R.styleable.TradesView_td_focusLineColor, 0XFFFFFFFF);
		// mPaint.setColor(textColor);
		mPaint.setTextSize(textSize);
		mPaint.setAntiAlias(true);
		array.recycle();
		//
		// try {
		// feedJosn_trades(data);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void set_chart_rect() {
		r_chart.set((int) (margin_left + y_text_width + margin_space),
				(int) (margin_top + text_infoSize + margin_space),
				this.getMeasuredWidth() - margin_right,
				this.getMeasuredHeight() - margin_bottom);
	}

	public double cacu(double v, double max) {
		double rt = Math.log(Math.E / v * max) * v;
		return rt;
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
		if (0 < update_time) {
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(text_infoColor);
			mPaint.setStrokeWidth(1);
			canvas.drawText(error_time_format.format(update_time), margin_left,
					info_text_y, mPaint);
		}
		if (m_trades_items.isEmpty())
			return;

		// draw price&amount line
		price_path.reset();
		cur_path.reset();
		// if (m_trades_items.isEmpty())
		// return;
		//
		// set_chart_rect();
		// Log.e("ttt", "update");

		double amount_K_max = amount_V_max;
		double amount_K_min = amount_V_min;
		amount_k = (r_chart.top - r_chart.bottom)
				/ (amount_K_max - amount_K_min);
		amount_b = r_chart.top - amount_k * amount_K_max;

		double price_K_max = price_V_max;
		double price_K_min = price_V_min;
		double price_k = (r_chart.top - r_chart.bottom)
				/ (price_K_max - price_K_min);
		double price_b = r_chart.top - price_k * price_K_max;

		double cur_K_max = cur_V_max;
		double cur_K_min = cur_V_min;
		double cur_k = (r_chart.top - r_chart.bottom) / (cur_K_max - cur_K_min);
		double cur_b = r_chart.top - cur_k * cur_K_max;

		float block_width = (r_chart.right - r_chart.left)
				/ m_trades_items.size();
		float amount_line_width = block_width / 2 * 1;
		float amount_line_space = block_width - amount_line_width;

		int size = m_trades_items.size();
		price_path
				.moveTo((float) (r_chart.left + block_width / 2 + 0 * block_width),
						(float) (m_trades_items.get(size - 0 - 1).price
								* price_k + price_b));
		cur_path.moveTo(
				(float) (r_chart.left + block_width / 2 + 0 * block_width),
				(float) (m_trades_items.get(size - 0 - 1).price
						* m_trades_items.get(size - 0 - 1).amount * cur_k + cur_b));
		r_block.set(
				(int) (r_chart.left + block_width / 2 + 0 * block_width - amount_line_width / 2),
				(int) (cacu(m_trades_items.get(size - 0 - 1).amount,
						amount_V_max) * amount_k + amount_b),
				(int) (r_chart.left + block_width / 2 + 0 * block_width + amount_line_width / 2),
				r_chart.bottom);
		if (0 == m_trades_items.get(0).trade_type)
			mPaint.setColor(ask_line_color);
		else
			mPaint.setColor(bid_line_color);
		// canvas.drawRect(r_block, mPaint);
		canvas.drawLine(r_block.centerX(), r_block.top, r_block.centerX(),
				r_block.bottom, mPaint);
		for (int i = size - 2; i >= 0; --i) {
			price_path.lineTo((float) (r_chart.left + block_width / 2 + (size
					- i - 1)
					* block_width), (float) (m_trades_items.get(i).price
					* price_k + price_b));
			cur_path.lineTo(
					(float) (r_chart.left + block_width / 2 + (size - i - 1)
							* block_width),
					(float) (m_trades_items.get(i).price
							* m_trades_items.get(i).amount * cur_k + cur_b));
			r_block.set((int) (r_chart.left + block_width / 2 + (size - i - 1)
					* block_width - amount_line_width / 2),
					(int) (cacu(m_trades_items.get(i).amount, amount_V_max)
							* amount_k + amount_b),
					(int) (r_chart.left + block_width / 2 + (size - i - 1)
							* block_width + amount_line_width / 2),
					r_chart.bottom);
			if (0 == m_trades_items.get(i).trade_type)
				mPaint.setColor(ask_line_color);
			else
				mPaint.setColor(bid_line_color);
			// canvas.drawRect(r_block, mPaint);
			canvas.drawLine(r_block.centerX(), r_block.top, r_block.centerX(),
					r_block.bottom, mPaint);
		}

		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(price_line_color);
		// mPaint.setStrokeWidth(ask_line_size);
		canvas.drawPath(price_path, mPaint);
		mPaint.setColor(cur_line_color);
		canvas.drawPath(cur_path, mPaint);

		float index = 0;
		if (mousedown) {
			index = size - 1 - (ex - r_chart.left - block_width / 2)
					/ block_width;
		}
		if (index < size && index >= 0) {
			int i = (int) index;
			r_block.set((int) (r_chart.left + block_width / 2 + (size - i - 1)
					* block_width - amount_line_width / 2),
					(int) (m_trades_items.get(i).amount * amount_k + amount_b),
					(int) (r_chart.left + block_width / 2 + (size - i - 1)
							* block_width + amount_line_width / 2),
					r_chart.bottom);
			if (mousedown) {
				mPaint.setColor(focus_line_color);
				canvas.drawLine(r_block.centerX(), r_chart.top,
						r_block.centerX(), r_chart.bottom, mPaint);
			}
			mPaint.setStrokeWidth(point_size);
			mPaint.setColor(point_color);
			canvas.drawPoint(r_chart.left + block_width / 2 + (size - i - 1)
					* block_width, (float) (m_trades_items.get(i).price
					* price_k + price_b), mPaint);
			mPaint.setColor(cur_line_color);
			canvas.drawPoint(r_chart.left + block_width / 2 + (size - i - 1)
					* block_width, (float) (m_trades_items.get(i).price
					* m_trades_items.get(i).amount * cur_k + cur_b), mPaint);
			mPaint.setColor(point_color ^ 0x00FFFFFF);
			canvas.drawPoint(r_chart.left + block_width / 2 + (size - i - 1)
					* block_width,
					(float) (cacu(m_trades_items.get(i).amount, amount_V_max)
							* amount_k + amount_b), mPaint);
			// draw the info text
			String info = (m_trades_items.get(i).trade_type == 0 ? "Ask:"
					: "Bid:")
					+ CandleStickView.my_formatter(m_trades_items.get(i).price,
							6)
					+ " / "
					+ CandleStickView.my_formatter(
							m_trades_items.get(i).amount, 8)
					+ " / "
					+ CandleStickView.my_formatter(m_trades_items.get(i).amount
							* m_trades_items.get(i).price, 8)
					+ " / "
					+ error_time_format
							.format(m_trades_items.get(i).date * 1000);
			// Log.e("tradesview",info);
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(text_infoColor);
			mPaint.setStrokeWidth(1);
			canvas.drawText(info, width - mPaint.measureText(info),
					info_text_y, mPaint);
		}
		mPaint.setStrokeWidth(1);
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
					mListener.OnDoubleClick();
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
