package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.BtceClient.TradesView.OnDoubleClickListener;
import com.googlecode.BtceClient.TradesView.trades_item;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TradeView extends View {
	Spline spline;
	double price_max, price_min, amount_max, currency_total;
	int trade_num;
	List<trades_item> m_orders_items = new ArrayList<trades_item>();
	double fector;
	double min_coin = 0.1;
	boolean is_sell = false;

	Paint mPaint;
	Path amount_path = new Path();

	int bgColor = 0, frameColor = 0, gridColor = 0, textColor = 0;
	int bid_line_color = 0, bid_fill_color = 0, ask_line_color = 0,
			ask_fill_color = 0, price_line_color = 0, cur_line_color = 0;
	int point_color = 0, focus_line_color = 0;;
	int point_size = 10, bid_line_size = 3, ask_line_size = 3;
	// float textSize = 0;
	int text_infoColor = 0;
	float text_infoSize = 0;
	int margin_left = 5;
	int margin_right = 5;
	int margin_top = 5;
	int margin_bottom = 5;
	int margin_space = 5;// margin of items in this view
	float y_text_width = 0;// width of text on Y axis
	int num_rows = 4;

	Rect r_chart = new Rect();

	long update_time = 0;
	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");

	// use for the touch event
	float bx = -1;
	float by = -1;
	float ex = bx;
	float ey = by;
	boolean mousedown = false;
	int control_point = -1;
	DecimalFormat formatter5 = new DecimalFormat();

	public TradeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TradeView(Context context, AttributeSet attrs) {
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
				.getColor(R.styleable.TradesView_td_bidlineSize, 3);
		bid_fill_color = array.getColor(R.styleable.TradesView_td_bidfillColor,
				0X7F75B103);
		ask_line_color = array.getColor(R.styleable.TradesView_td_asklineColor,
				0XFF4E8CD9);
		price_line_color = array.getColor(
				R.styleable.TradesView_td_pricelineColor, 0X00FF00FF);
		cur_line_color = array.getColor(R.styleable.TradesView_td_curlineColor,
				0XFFFF0000);
		ask_line_size = array
				.getColor(R.styleable.TradesView_td_asklineSize, 3);
		ask_fill_color = array.getColor(R.styleable.TradesView_td_askfillColor,
				0X7F4E8CD9);
		point_color = array.getColor(R.styleable.TradesView_td_pointColor,
				0XFF00FF00);
		point_size = array.getInteger(R.styleable.TradesView_td_pointSize, 10);
		focus_line_color = array.getColor(
				R.styleable.TradesView_td_focusLineColor, 0XFFFFFFFF);
		// mPaint.setColor(textColor);
		mPaint.setTextSize(text_infoSize);
		mPaint.setAntiAlias(true);
		array.recycle();
		//
		// try {
		// feedJosn_trades(data);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		y_text_width = mPaint.measureText("0.00000");
		formatter5.setMaximumFractionDigits(5);// according to the "0.00000"
		// above
		formatter5.setGroupingUsed(false);
	}

	public void setSpline(float x[], float y[]) {
		spline = new Spline(x, y);
	}

	public void swapSpline() {
		float t;
		for (int i = 1; i < spline.getX().length / 2; ++i) {
			int j = spline.getX().length - 1 - i;
			t = spline.getX()[i];
			spline.getX()[i] = 1 - spline.getX()[j];
			spline.getX()[j] = 1 - t;
			t = spline.getY()[i];
			spline.getY()[i] = spline.getY()[j];
			spline.getY()[j] = t;
		}
		if (1 == spline.getX().length % 2) {
			int j = (spline.getX().length - 1) / 2;
			spline.getX()[j] = 1 - spline.getX()[j];
		}
		t = spline.getY()[0];
		spline.getY()[0] = spline.getY()[spline.getX().length - 1];
		spline.getY()[spline.getX().length - 1] = t;
		spline.reset();
		update_orders();
	}

	public void setParam(double priceMax, double priceMin,
			double currencyTotal, int tradeNum, double minicoin) {
		price_max = priceMax;
		price_min = priceMin;
		currency_total = currencyTotal;
		trade_num = tradeNum;
		min_coin = minicoin;
		update_orders();
	}

	public void updateNumber(int tradeNum) {
		trade_num = tradeNum;
		update_orders();
	}

	public void updatePirceMin(double priceMin) {
		price_min = priceMin;
		update_orders();
	}

	public void updatePirceMax(double priceMax) {
		price_max = priceMax;
		update_orders();
	}

	public void updateCurrency(double currencyTotal) {
		currency_total = currencyTotal;
		update_orders();
	}

	public void updateMinicoin(double minicoin) {
		min_coin = minicoin;
	}

	public void updateOrderType(boolean sell) {
		is_sell = sell;
	}

	public void update_orders() {
		m_orders_items.clear();
		double all_currency = 0;
		for (int i = 0; i < trade_num + 1; ++i) {
			trades_item item = new trades_item();
			item.currency = spline.interpolate(1.0F * i / trade_num);
			item.price = (price_max - price_min) * i / trade_num + price_min;
			all_currency += item.currency;
			m_orders_items.add(item);
		}
		fector = currency_total / all_currency;
		all_currency = 0;
		for (int i = 0; i < m_orders_items.size(); ++i) {
			trades_item item = m_orders_items.get(i);
			if ((is_sell && fector * item.currency < min_coin)
					|| (!is_sell && fector * item.currency / item.price < min_coin))
				item.currency = 0;
			all_currency += item.currency;
		}
		fector = currency_total / all_currency;
		for (int i = 0; i < m_orders_items.size(); ++i) {
			trades_item item = m_orders_items.get(i);
			if (is_sell) {
				item.amount = fector * item.currency * item.price;
			} else
				item.amount = fector * item.currency / item.price;
		}
		this.invalidate();
	}

	public List<trades_item> getOrders() {
		List<trades_item> orders = new ArrayList<trades_item>();
		if (is_sell) {
			for (trades_item item : m_orders_items) {
				if (item.currency > 0) {
					trades_item i = new trades_item();
					i.price = item.price;
					i.amount = fector * item.currency;
					i.trade_type = 1;
					orders.add(i);
				}
			}
		} else {
			for (trades_item item : m_orders_items) {
				if (item.currency > 0) {
					trades_item i = new trades_item();
					i.price = item.price;
					i.amount = item.amount;
					i.trade_type = 0;
					orders.add(i);
				}
			}
		}
		return orders;
	}

	public static String my_formatter(double d, int bits) {
		DecimalFormat formatter = new DecimalFormat();
		formatter.setMaximumFractionDigits(bits);
		formatter.setGroupingUsed(false);
		String s = formatter.format(d);
		int len = s.length();
		int i = s.indexOf('.');
		if (-1 == i) {
			if (len <= bits)
				s = s + ".";
			for (int t = len; t < bits; ++t)
				s = s + "0";
		} else if (bits == i) {
			s = s.substring(0, i + 1);
		} else if (bits < i) {
			s = s.substring(0, i);
		} else {
			s = s.substring(0, s.length() > bits ? bits + 1 : s.length());
			for (int t = s.length(); t <= bits; ++t)
				s = s + "0";
		}
		return s;
	}

	public void set_chart_rect() {
		r_chart.set(
				(int) (margin_left + y_text_width + 2 * margin_space),
				(int) (margin_top + 2 * text_infoSize + margin_space),
				this.getMeasuredWidth() - margin_right,
				(int) (this.getMeasuredHeight() - margin_bottom - text_infoSize - margin_space));
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();

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
		for (int i = 1; i < num_rows; ++i) {
			canvas.drawLine(r_chart.left, r_chart.top + i * r_chart.height()
					/ num_rows, r_chart.right,
					r_chart.top + i * r_chart.height() / num_rows, mPaint);
		}

		int info_text_y = (int) (margin_top + text_infoSize);
		// if (0 < update_time) {
		// mPaint.setStyle(Style.FILL);
		// mPaint.setColor(text_infoColor);
		// mPaint.setStrokeWidth(1);
		// canvas.drawText(error_time_format.format(update_time), margin_left,
		// info_text_y, mPaint);
		// }
		if (null == spline)
			return;

		r_chart.height();
		r_chart.width();

		float x_k = (spline.right - spline.left) / r_chart.width();
		float x_b = spline.left - x_k * r_chart.left;
		float y_k = -r_chart.height();// / (spline.bottom - (spline.top < 0 ?
										// spline.top : 0));
		float y_b = r_chart.top - y_k;// * spline.bottom;
		if (mousedown) {
			if (-1 == control_point) {
				for (int i = 0; i < spline.getX().length; ++i) {
					if (25 > Math.hypot(bx - (spline.getX()[i] - x_b) / x_k, by
							- (y_b + y_k * spline.getY()[i]))) {
						control_point = i;
						break;
					}
				}
			}
			if (-1 != control_point) {
				ex = ex < r_chart.left ? r_chart.left : ex;
				ex = ex > r_chart.right ? r_chart.right : ex;
				ey = ey < r_chart.top ? r_chart.top : ey;
				ey = ey > r_chart.bottom ? r_chart.bottom : ey;
				if (0 != control_point
						&& spline.getX().length - 1 != control_point) {
					float temp = x_k * ex + x_b;
					if (temp > spline.getX()[control_point - 1] + 0.0001
							&& temp < spline.getX()[control_point + 1] - 0.0001)
						spline.getX()[control_point] = temp;
				}
				spline.getY()[control_point] = (ey - y_b) / y_k;
				spline.reset();
				x_k = (spline.right - spline.left) / r_chart.width();
				x_b = spline.left - x_k * r_chart.left;
				y_k = -r_chart.height();// / (spline.bottom - (spline.top < 0 ?
										// spline.top : 0));
				y_b = r_chart.top - y_k;// * spline.bottom;
				update_orders();
			}

		}

		amount_path.reset();
		amount_path.moveTo(r_chart.left,
				y_b + y_k * spline.interpolate(x_k * r_chart.left + x_b));
		for (int x = r_chart.left + 1; x <= r_chart.right; ++x) {
			amount_path
					.lineTo(x, y_b + y_k * spline.interpolate(x_k * x + x_b));
		}
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(price_line_color);
		// mPaint.setStrokeWidth(ask_line_size);
		canvas.drawPath(amount_path, mPaint);

		// draw control points
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(point_size);
		mPaint.setColor(point_color);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStyle(Style.STROKE);
		int size = 3;
		for (int i = 0; i < spline.getX().length; ++i) {
			float px = (spline.getX()[i] - x_b) / x_k;
			float py = y_b + y_k * spline.getY()[i];
			RectF f = new RectF(px - size / 2, py - size / 2, px + size / 2, py
					+ size / 2);
			canvas.drawArc(f, 360F, 360F, true, mPaint);
		}

		// draw orders lines
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(1);
		mPaint.setColor(bid_line_color);
		int aviable_orders = 0;
		double total_amount = 0;
		double total_cur = 0;
		int touch_index = -1;
		for (int i = 0; i < m_orders_items.size(); ++i) {
			trades_item item = m_orders_items.get(i);
			float x = (float) (((item.price - price_min)
					/ (price_max - price_min) - x_b) / x_k);
			if (item.currency > 0) {
				canvas.drawLine(x, (float) (y_b + y_k * 0), x,
						(float) (y_b + y_k * item.currency), mPaint);
				total_amount += item.amount;
				total_cur += this.fector * item.currency;
				aviable_orders += 1;
			}
			if (0 != trade_num && -1 == touch_index
					&& Math.abs(ex - x) < r_chart.width() / trade_num / 2) {
				touch_index = i;
				if (mousedown && -1 == control_point) {
					mPaint.setColor(focus_line_color);
					canvas.drawLine(x, (float) (y_b + y_k * 0), x, r_chart.top,
							mPaint);
					mPaint.setColor(bid_line_color);
				}
			}
		}

		// draw information text
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(text_infoColor);
		String information = "";
		if (-1 != control_point) {
			information = my_formatter(spline.getX()[control_point], 4) + "/"
					+ my_formatter(spline.getY()[control_point], 4);
			canvas.drawText(information, margin_left, info_text_y
					+ text_infoSize - 3, mPaint);
		}
		if (mousedown && -1 != touch_index && -1 == control_point) {
			if (is_sell)
				information = my_formatter(
						m_orders_items.get(touch_index).price, 6)
						+ "/"
						+ my_formatter(
								this.fector
										* m_orders_items.get(touch_index).currency,
								6)
						+ "/"
						+ my_formatter(m_orders_items.get(touch_index).amount,
								6);
			else
				information = my_formatter(
						m_orders_items.get(touch_index).price, 6)
						+ "/"
						+ my_formatter(m_orders_items.get(touch_index).amount,
								6)
						+ "/"
						+ my_formatter(
								this.fector
										* m_orders_items.get(touch_index).currency,
								6);
			canvas.drawText(information, margin_left, info_text_y
					+ text_infoSize - 3, mPaint);
			int num = 0;
			double all_cur = 0, all_amount = 0;
			String left_info, right_info;
			for (int i = 0; i <= touch_index; ++i) {
				trades_item item = m_orders_items.get(i);
				if (item.currency > 0) {
					num += 1;
					all_cur += item.currency;
					all_amount += item.amount;
				}
			}
			all_cur = this.fector * all_cur;
			if (is_sell)
				left_info = num + "/" + my_formatter(all_amount / all_cur, 6)
						+ "/" + my_formatter(all_cur, 6) + "/"
						+ my_formatter(all_amount, 6);
			else
				left_info = num + "/" + my_formatter(all_cur / all_amount, 6)
						+ "/" + my_formatter(all_amount, 6) + "/"
						+ my_formatter(all_cur, 6);

			num = 0;
			all_cur = all_amount = 0;
			for (int i = touch_index; i < m_orders_items.size(); ++i) {
				trades_item item = m_orders_items.get(i);
				if (item.currency > 0) {
					num += 1;
					all_cur += item.currency;
					all_amount += item.amount;
				}
			}
			all_cur = this.fector * all_cur;
			if (is_sell)
				right_info = num + "/"
						+ my_formatter(all_amount / all_cur, 6) + "/"
						+ my_formatter(all_cur, 6) + "/"
						+ my_formatter(all_amount, 6);
			else
				right_info = num + "/"
						+ my_formatter(all_cur / all_amount, 6) + "/"
						+ my_formatter(all_amount, 6) + "/"
						+ my_formatter(all_cur, 6);
			canvas.drawText(left_info, margin_left, info_text_y - 3, mPaint);
			canvas.drawText(right_info, width - mPaint.measureText(right_info),
					info_text_y - 3, mPaint);
		} else {
		}
		if (is_sell)
			information = aviable_orders + "/"
					+ my_formatter(total_amount / total_cur, 6) + "/"
					+ my_formatter(total_cur, 6) + "/"
					+ my_formatter(total_amount, 6);
		else
			information = aviable_orders + "/"
					+ my_formatter(total_cur / total_amount, 6) + "/"
					+ my_formatter(total_amount, 6) + "/"
					+ my_formatter(total_cur, 6);
		canvas.drawText(information, width - mPaint.measureText(information),
				info_text_y + text_infoSize - 3, mPaint);

		// draw text of the Y axis
		mPaint.setStyle(Style.FILL);
		// mPaint.setColor(text_yColor);
		mPaint.setStrokeWidth(1);
		for (int i = 0; i < num_rows; ++i) {
			canvas.drawText(my_formatter(fector * i / num_rows, 6),
					margin_left,
					r_chart.top + (num_rows - i) * r_chart.height() / num_rows,
					mPaint);
		}
		canvas.drawText(my_formatter(fector * num_rows / num_rows, 6),
				margin_left,
				r_chart.top + (num_rows - num_rows) * r_chart.height()
						/ num_rows + text_infoSize - 5, mPaint);

		// draw text of the X axis
		float twd = mPaint.measureText(my_formatter(price_max, 5));
		int last_column = -100;
		for (int i = 0; i < trade_num; ++i) {
			if ((i - last_column) * r_chart.width() / trade_num > 1.2 * twd
					&& r_chart.width() - i * r_chart.width() / trade_num > 2.2 * twd) {
				canvas.drawText(
						my_formatter(price_min + i * (price_max - price_min)
								/ trade_num, 5),
						r_chart.left + i * r_chart.width() / trade_num,
						r_chart.bottom + text_infoSize + margin_space, mPaint);
				last_column = i;
			}
		}
		canvas.drawText(my_formatter(price_max, 5),
				r_chart.right - mPaint.measureText(my_formatter(price_max, 5)),
				r_chart.bottom + text_infoSize + margin_space, mPaint);
	}

	private OnDoubleClickListener mListener;
	private long lastTouchTime = -1;

	public interface OnDoubleClickListener {
		void OnDoubleClick();
	}

	public void setOnDoubleClickListener(OnDoubleClickListener l) {
		mListener = l;
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
			control_point = -1;
			ex = bx = ev.getX();
			ey = by = ev.getY();
			// temp_last_k_index = last_k_index;// save the last index when
			// mouse
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
			control_point = -1;
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
