package com.googlecode.BtceClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.googlecode.BtceClient.R;
import com.googlecode.BtceClient.DepthView.OnDoubleClickListener;

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

	public interface OnDoubleClickListener {
		void OnDoubleClick();
	}

	public void setOnDoubleClickListener(OnDoubleClickListener l) {
		mListener = l;
	}

	Paint mPaint;
	// String data =
	// "[{\"date\":1373786105,\"price\":88.8,\"amount\":0.01,\"tid\":6202885,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373785817,\"price\":88.8,\"amount\":0.0777038,\"tid\":6202871,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373785805,\"price\":88.8,\"amount\":0.01,\"tid\":6202870,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373785801,\"price\":88.8,\"amount\":0.012,\"tid\":6202869,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373785789,\"price\":88.8,\"amount\":0.0105388,\"tid\":6202863,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373785505,\"price\":88.8,\"amount\":0.01,\"tid\":6202821,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373785388,\"price\":88.8,\"amount\":0.283331,\"tid\":6202815,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373785388,\"price\":88.7,\"amount\":0.01,\"tid\":6202814,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373784708,\"price\":88.5,\"amount\":0.2,\"tid\":6202759,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373784613,\"price\":88.331,\"amount\":0.00313713,\"tid\":6202751,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373784613,\"price\":88.5,\"amount\":0.01,\"tid\":6202750,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373784273,\"price\":88.31,\"amount\":0.153902,\"tid\":6202705,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373784154,\"price\":88.913,\"amount\":0.162837,\"tid\":6202704,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373784116,\"price\":88.303,\"amount\":0.101,\"tid\":6202699,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373784103,\"price\":88.303,\"amount\":0.0116475,\"tid\":6202696,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783919,\"price\":88.915,\"amount\":0.0202585,\"tid\":6202667,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783918,\"price\":88.915,\"amount\":0.0106112,\"tid\":6202665,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783917,\"price\":88.915,\"amount\":0.219631,\"tid\":6202663,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783917,\"price\":88.915,\"amount\":0.356,\"tid\":6202662,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783889,\"price\":88.915,\"amount\":0.01,\"tid\":6202657,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373783709,\"price\":88.303,\"amount\":0.37,\"tid\":6202653,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783662,\"price\":88.303,\"amount\":0.100999,\"tid\":6202646,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783649,\"price\":88.303,\"amount\":0.101,\"tid\":6202644,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783538,\"price\":88.302,\"amount\":0.021,\"tid\":6202638,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783481,\"price\":88.302,\"amount\":2.27287,\"tid\":6202634,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373783476,\"price\":88.302,\"amount\":0.109,\"tid\":6202633,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373783462,\"price\":88.302,\"amount\":0.07,\"tid\":6202625,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373783460,\"price\":88.302,\"amount\":0.0128256,\"tid\":6202624,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373783456,\"price\":88.305,\"amount\":0.01003,\"tid\":6202623,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783456,\"price\":88.4,\"amount\":0.325271,\"tid\":6202622,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783456,\"price\":88.5,\"amount\":0.2,\"tid\":6202621,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783340,\"price\":88.4,\"amount\":0.0947291,\"tid\":6202616,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373783044,\"price\":88.3,\"amount\":1.10456,\"tid\":6202555,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782875,\"price\":88.316,\"amount\":0.73766,\"tid\":6202495,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782860,\"price\":88.316,\"amount\":0.167429,\"tid\":6202494,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782860,\"price\":88.316,\"amount\":0.0849112,\"tid\":6202493,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.316,\"amount\":0.0502734,\"tid\":6202486,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.497,\"amount\":0.01,\"tid\":6202485,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.497,\"amount\":0.01,\"tid\":6202484,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.497,\"amount\":0.01,\"tid\":6202483,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.497,\"amount\":0.01,\"tid\":6202482,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.497,\"amount\":0.01,\"tid\":6202481,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.497,\"amount\":0.01,\"tid\":6202480,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.497,\"amount\":0.01,\"tid\":6202479,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.497,\"amount\":0.01,\"tid\":6202478,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.498,\"amount\":0.01,\"tid\":6202477,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782826,\"price\":88.5,\"amount\":0.859727,\"tid\":6202476,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782716,\"price\":89,\"amount\":11.236,\"tid\":6202473,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782681,\"price\":89,\"amount\":0.522,\"tid\":6202464,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782668,\"price\":89,\"amount\":0.025646,\"tid\":6202463,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782613,\"price\":88.5,\"amount\":0.140273,\"tid\":6202462,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782613,\"price\":88.5,\"amount\":0.1,\"tid\":6202461,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782613,\"price\":88.5,\"amount\":0.0394636,\"tid\":6202460,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782598,\"price\":89,\"amount\":0.028426,\"tid\":6202459,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782597,\"price\":89,\"amount\":0.0589405,\"tid\":6202458,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782595,\"price\":89,\"amount\":0.0968617,\"tid\":6202457,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782588,\"price\":88.8,\"amount\":0.0619498,\"tid\":6202456,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782588,\"price\":88.8,\"amount\":0.162582,\"tid\":6202455,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782588,\"price\":88.8,\"amount\":0.0254678,\"tid\":6202454,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782379,\"price\":88.8,\"amount\":4.16262,\"tid\":6202443,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782254,\"price\":88.8,\"amount\":1.90504,\"tid\":6202431,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782254,\"price\":88.7,\"amount\":0.071532,\"tid\":6202430,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373782248,\"price\":88.7,\"amount\":0.0593924,\"tid\":6202429,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782248,\"price\":88.7,\"amount\":0.105446,\"tid\":6202428,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782214,\"price\":88.8,\"amount\":0.190556,\"tid\":6202411,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782054,\"price\":88.8,\"amount\":0.0594444,\"tid\":6202401,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373782008,\"price\":89,\"amount\":0.5,\"tid\":6202381,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373781963,\"price\":89,\"amount\":39.7895,\"tid\":6202374,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373781963,\"price\":88.96,\"amount\":0.110458,\"tid\":6202373,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373781930,\"price\":88.96,\"amount\":0.00148545,\"tid\":6202371,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781929,\"price\":88.96,\"amount\":0.147021,\"tid\":6202370,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781781,\"price\":88.95,\"amount\":0.469864,\"tid\":6202343,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781778,\"price\":88.95,\"amount\":0.0117927,\"tid\":6202333,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781775,\"price\":88.9,\"amount\":0.3099,\"tid\":6202330,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373781723,\"price\":88.81,\"amount\":0.0576622,\"tid\":6202326,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781723,\"price\":88.81,\"amount\":0.130498,\"tid\":6202324,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781506,\"price\":88.8,\"amount\":0.590551,\"tid\":6202314,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781475,\"price\":88.8,\"amount\":0.15098,\"tid\":6202310,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781465,\"price\":88.8,\"amount\":0.485419,\"tid\":6202308,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781455,\"price\":88.8,\"amount\":0.296895,\"tid\":6202306,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781360,\"price\":88.8,\"amount\":0.2794,\"tid\":6202302,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781352,\"price\":88.8,\"amount\":1.13,\"tid\":6202301,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781322,\"price\":88.9,\"amount\":0.0201,\"tid\":6202300,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373781311,\"price\":88.8,\"amount\":0.206856,\"tid\":6202299,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781303,\"price\":88.8,\"amount\":0.70653,\"tid\":6202297,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781220,\"price\":89,\"amount\":36.2,\"tid\":6202288,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373781182,\"price\":89,\"amount\":1.7,\"tid\":6202287,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373781126,\"price\":89,\"amount\":4.2359,\"tid\":6202275,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373781126,\"price\":88.998,\"amount\":1.73126,\"tid\":6202274,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373781110,\"price\":88.997,\"amount\":0.05,\"tid\":6202267,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781106,\"price\":88.997,\"amount\":0.180199,\"tid\":6202265,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781102,\"price\":88.997,\"amount\":0.110571,\"tid\":6202263,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781102,\"price\":88.997,\"amount\":0.291354,\"tid\":6202261,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781101,\"price\":88.997,\"amount\":0.228362,\"tid\":6202258,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781101,\"price\":88.997,\"amount\":0.2,\"tid\":6202254,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781101,\"price\":88.997,\"amount\":0.300365,\"tid\":6202253,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781101,\"price\":88.997,\"amount\":0.300365,\"tid\":6202252,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781101,\"price\":88.997,\"amount\":0.0252707,\"tid\":6202251,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781101,\"price\":88.997,\"amount\":1,\"tid\":6202249,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781101,\"price\":88.997,\"amount\":0.70653,\"tid\":6202247,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781100,\"price\":88.997,\"amount\":0.425561,\"tid\":6202233,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781100,\"price\":88.997,\"amount\":0.300365,\"tid\":6202230,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781100,\"price\":88.997,\"amount\":0.898902,\"tid\":6202229,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781099,\"price\":88.997,\"amount\":2.80907,\"tid\":6202226,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781087,\"price\":88.997,\"amount\":0.3,\"tid\":6202206,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781073,\"price\":88.997,\"amount\":0.3,\"tid\":6202205,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373781041,\"price\":88.997,\"amount\":0.185361,\"tid\":6202204,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"bid\"},{\"date\":1373780489,\"price\":88.316,\"amount\":2.81482,\"tid\":6202152,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.4,\"amount\":1.47983,\"tid\":6202151,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.45,\"amount\":0.04,\"tid\":6202150,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.499,\"amount\":0.01,\"tid\":6202149,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.5,\"amount\":0.01,\"tid\":6202148,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.5,\"amount\":0.01,\"tid\":6202147,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.566,\"amount\":0.01,\"tid\":6202146,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.566,\"amount\":0.01,\"tid\":6202145,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.566,\"amount\":0.01,\"tid\":6202144,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.566,\"amount\":0.01,\"tid\":6202143,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.566,\"amount\":0.01,\"tid\":6202142,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.566,\"amount\":0.01,\"tid\":6202141,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.599,\"amount\":0.01,\"tid\":6202140,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.599,\"amount\":0.01,\"tid\":6202139,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.599,\"amount\":0.01,\"tid\":6202138,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.599,\"amount\":0.01,\"tid\":6202137,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.599,\"amount\":0.01,\"tid\":6202136,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.6,\"amount\":0.01,\"tid\":6202135,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.6,\"amount\":0.01,\"tid\":6202134,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.672,\"amount\":0.01003,\"tid\":6202133,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202132,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202131,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202130,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202129,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202128,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202127,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202126,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202125,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202124,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.7,\"amount\":0.01,\"tid\":6202123,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.763,\"amount\":0.01,\"tid\":6202122,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.763,\"amount\":0.01,\"tid\":6202121,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.763,\"amount\":0.01,\"tid\":6202120,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.763,\"amount\":0.01,\"tid\":6202119,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.763,\"amount\":0.01,\"tid\":6202118,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.807,\"amount\":0.01,\"tid\":6202117,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.807,\"amount\":0.01,\"tid\":6202116,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.807,\"amount\":0.01,\"tid\":6202115,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.807,\"amount\":0.01,\"tid\":6202114,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780471,\"price\":88.807,\"amount\":0.01,\"tid\":6202113,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780470,\"price\":88.808,\"amount\":0.01,\"tid\":6202112,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780470,\"price\":88.808,\"amount\":0.01,\"tid\":6202111,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"},{\"date\":1373780470,\"price\":88.81,\"amount\":0.01,\"tid\":6202110,\"price_currency\":\"USD\",\"item\":\"BTC\",\"trade_type\":\"ask\"}]";
	List<trades_item> m_trades_items = new ArrayList<trades_item>();
	long update_time = 0;

	Path price_path = new Path();

	int bgColor = 0, frameColor = 0, gridColor = 0, textColor = 0;
	int bid_line_color = 0, bid_fill_color = 0, ask_line_color = 0,
			ask_fill_color = 0, price_line_color = 0;
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

	SimpleDateFormat error_time_format = new SimpleDateFormat("HH:mm:ss");

	public static class trades_item {
		long date = 0;
		double price = 0;
		double amount = 0;
		long tid = 0;
		int trade_type = 0;// 0 for ask, else for bid
	};

	public int feedJosn_trades(String string) throws JSONException {
		update_time = System.currentTimeMillis();
		m_trades_items.clear();
		amount_V_max = amount_V_min = price_V_max = 0;
		price_V_min = 1000000000;
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
				.getColor(R.styleable.TradesView_td_bidlineSize, 3);
		bid_fill_color = array.getColor(R.styleable.TradesView_td_bidfillColor,
				0X7F75B103);
		ask_line_color = array.getColor(R.styleable.TradesView_td_asklineColor,
				0XFF4E8CD9);
		price_line_color = array.getColor(
				R.styleable.TradesView_td_pricelineColor, 0X00FF00FF);
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

		float block_width = (r_chart.right - r_chart.left)
				/ m_trades_items.size();
		float amount_line_width = block_width / 2 * 1;
		float amount_line_space = block_width - amount_line_width;

		int size = m_trades_items.size();
		price_path
				.moveTo((float) (r_chart.left + block_width / 2 + 0 * block_width),
						(float) (m_trades_items.get(size - 0 - 1).price
								* price_k + price_b));
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
		mPaint.setColor(price_line_color);
		mPaint.setStyle(Style.STROKE);
		// mPaint.setStrokeWidth(ask_line_size);
		canvas.drawPath(price_path, mPaint);

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
