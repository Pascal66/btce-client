package com.googlecode.BtceClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class BTCEHelper {
	private static final String btce_host_name = "btc-e.com";
	private static final String btce_scheme = "https";
	private static final String public_api_url = btce_scheme + "://"
			+ btce_host_name + "/api/2/";
	private static final String trade_api_url = btce_scheme + "://"
			+ btce_host_name + "/tapi";
	private static final int HTTP_STATUS_OK = 200;
	private byte[] buff = new byte[1024];
	private static final BTCEPairs all_pairs = new BTCEPairs();

	enum btce_methods {
		FEE, TICKER, TRADES, DEPTH, ORDERS_UPDATE, BTCE_UPDATE, GET_INFO, TRANS_HISTORY, TRADE_HISTORY, ORDER_LIST, ACTIVE_ORDERS, TRADE, CANCEL_ORDER, UNKNOWN
	}

	private btce_params params = new btce_params();
	DecimalFormat formatter8 = new DecimalFormat();

	BTCEHelper() {
		formatter8.setMaximumFractionDigits(8);
		formatter8.setGroupingUsed(false);
	}

	static class btce_params implements Cloneable {
		String secret;
		String key;
		String proxy_host;
		int proxy_port;
		int save_port;
		String proxy_username;
		String proxy_passwd;
		private long nonce = System.currentTimeMillis() / 1000;
		String pair;
		btce_methods method;
		boolean sell;
		double trade_price;
		double trade_amount;
		int order_id;
		int his_from;
		int his_count;
		int his_from_id;
		int his_end_id;
		boolean asc;
		long his_since;
		long his_end;
		int order_active;
		long chart_start_time;

		public btce_params() {
			reset();
		}

		public btce_params getparams() {
			nonce++;
			try {
				return (btce_params) this.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return this;
		}

		public void reset() {
			method = btce_methods.UNKNOWN;
			trade_price = trade_amount = 0;
			order_id = his_from = his_count = his_from_id = his_end_id = -1;
			his_since = his_end = order_active = -1;
			asc = false;
			chart_start_time = 0;
		}

		public btce_params setpair(String pair) {
			this.pair = pair;
			return this;
		}
	}

	public String do_something(btce_params p) {
		params = p;
		String error_string = "{\"success\":0,\"error\":\"" + "params error"
				+ "\"}";
		switch (params.method) {
		case FEE:
			if (!all_pairs.containsKey(params.pair))
				return error_string;
			return this.getFee(params.pair);
		case TICKER:
			if (!all_pairs.containsKey(params.pair))
				return error_string;
			return this.getTicker(params.pair);
		case TRADES:
			if (!all_pairs.containsKey(params.pair))
				return error_string;
			return this.getTrades(params.pair);
		case DEPTH:
			if (!all_pairs.containsKey(params.pair))
				return error_string;
			return this.getDepth(params.pair);
		case ORDERS_UPDATE:
			if (!all_pairs.containsKey(params.pair))
				return error_string;
			// if (0 != params.chart_start_time &&
			// params.pair.equals("btc_usd"))
			// return this.btceUSD_bitcoincharts(params.chart_start_time);
			if (0 != params.chart_start_time)
				return orders_update_sae(params.pair, params.chart_start_time);
			return this.orders_update(params.pair);
		case BTCE_UPDATE:
			if (!all_pairs.containsKey(params.pair))
				return error_string;
			return this.orders_update(params.pair);
		case GET_INFO:
			return this.getInfo();
		case TRANS_HISTORY:
			return this.TransHistory(params.his_from, params.his_count,
					params.his_from_id, params.his_end_id, params.asc,
					params.his_since, params.his_end);
		case TRADE_HISTORY:
			if (!all_pairs.containsKey(params.pair))
				params.pair = "all pairs";
			return this.TradeHistory(params.his_from, params.his_count,
					params.his_from_id, params.his_end_id, params.asc,
					params.his_since, params.his_end, params.pair);
		case ORDER_LIST:
			if (0 != params.order_active)
				params.order_active = 1;
			if (!all_pairs.containsKey(params.pair))
				params.pair = "all pairs";
			return this.OrderList(params.his_from, params.his_count,
					params.his_from_id, params.his_end_id, params.asc,
					params.his_since, params.his_end, params.pair,
					params.order_active);
		case ACTIVE_ORDERS:
			if (!all_pairs.containsKey(params.pair))
				params.pair = "all pairs";
			return this.ActiveOrders(params.pair);
		case TRADE:
			if (!all_pairs.containsKey(params.pair))
				return error_string;
			if (0 == Double.compare(params.trade_price, 0))
				return error_string;
			if (0 == Double.compare(params.trade_amount, 0))
				return error_string;
			return this.Trade(params.pair, params.sell, params.trade_price,
					params.trade_amount);
		case CANCEL_ORDER:
			if (-1 == params.order_id)
				return error_string;
			return this.CancelOrder(params.order_id);
		default:
			return "{\"success\":0,\"error\":\"" + "not this method" + "\"}";
		}
	}

	protected String getFee(String pair) {
		assert (all_pairs.containsKey(pair));
		return downloadFromServer(public_api_url + pair + "/fee");
	}

	protected String getTicker(String pair) {
		assert (all_pairs.containsKey(pair));
		return downloadFromServer(public_api_url + pair + "/ticker");
	}

	protected String getTrades(String pair) {
		assert (all_pairs.containsKey(pair));
		return downloadFromServer(public_api_url + pair + "/trades");
	}

	protected String getDepth(String pair) {
		assert (all_pairs.containsKey(pair));
		return downloadFromServer(public_api_url + pair + "/depth");
	}

	protected String orders_update(String pair) {
		assert (all_pairs.containsKey(pair));
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("act", "orders_update"));
		parameters.add(new BasicNameValuePair("pair", all_pairs.get(pair)));
		List<NameValuePair> temp_header = new ArrayList<NameValuePair>();
		temp_header.add(new BasicNameValuePair("Referer", btce_scheme + "://"
				+ btce_host_name + "/exchange/" + pair));
		temp_header.add(new BasicNameValuePair("Origin", btce_scheme + "://"
				+ btce_host_name));
		return downloadFromServer(btce_scheme + "://" + btce_host_name
				+ "/ajax/order.php", temp_header, parameters);
	}

	protected String btceUSD_bitcoincharts(long start_time) {
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.UK);
		date_format.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
		Date temp_date = new Date();
		String priod = "30-min";
		String symbol = "btceUSD";
		temp_date.setTime(start_time * 1000);
		String url = "http://bitcoincharts.com/charts/chart.json?m=" + symbol
				+ "&SubmitButton=Draw&r=60&i=" + priod + "&c=1&s="
				+ date_format.format(temp_date);
		temp_date.setTime((start_time + 60 * 24 * 60 * 60) * 1000);
		url += "&e="
				+ date_format.format(temp_date)
				+ "&Prev=&Next=&t=S&b=&a1=&m1=10&a2=&m2=25&x=0&i1=&i2=&i3=&i4=&v=1&cv=0&ps=0&l=0&p=0&";
		// Log.e("bitcoinchart", url);
		return downloadFromServer(url);
	}

	protected String orders_update_sae(String pair, long start_time) {
		assert (all_pairs.containsKey(pair));
		Log.e("update", "update form sae: start:" + start_time);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("pair", pair));
		parameters.add(new BasicNameValuePair("start", "" + start_time));
		parameters.add(new BasicNameValuePair("num", "480"));
		return downloadFromServer("http://btcefetch.sinaapp.com/candle/pair/"
				+ pair + "/", null, parameters);
	}

	protected String getInfo() {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		List<NameValuePair> encryedHeader = encryHeader_updatePostdata(
				"getInfo", parameters);
		return downloadFromServer(trade_api_url, encryedHeader, parameters);
	}

	protected String Trade(String pair, boolean sell, double trade_price,
			double trade_amount) {
		assert (all_pairs.containsKey(pair));
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("pair", pair));
		if (sell)
			parameters.add(new BasicNameValuePair("type", "sell"));
		else
			parameters.add(new BasicNameValuePair("type", "buy"));
		parameters.add(new BasicNameValuePair("rate", formatter8
				.format(trade_price)));
		parameters.add(new BasicNameValuePair("amount", formatter8
				.format(trade_amount)));
		List<NameValuePair> encryedHeader = encryHeader_updatePostdata("Trade",
				parameters);
		return downloadFromServer(trade_api_url, encryedHeader, parameters);
	}

	protected String CancelOrder(int order_id) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("order_id", Integer
				.toString(order_id)));
		List<NameValuePair> encryedHeader = encryHeader_updatePostdata(
				"CancelOrder", parameters);
		return downloadFromServer(trade_api_url, encryedHeader, parameters);
	}

	protected String TransHistory() {
		return TransHistory(0, 1000, 0, -1, false, 0, -1);
	}

	protected String TransHistory(int from, int count, int from_id, int end_id,
			boolean asc, long since, long end) {
		return trade_trans_list("TransHistory", from, count, from_id, end_id,
				asc, since, end, null, -1);
	}

	protected String TradeHistory() {
		return TradeHistory("all pairs");
	}

	protected String TradeHistory(String pair) {
		return TradeHistory(0, 1000, 0, -1, false, 0, -1, pair);
	}

	protected String TradeHistory(int from, int count, int from_id, int end_id,
			boolean asc, long since, long end, String pair) {
		return trade_trans_list("TradeHistory", from, count, from_id, end_id,
				asc, since, end, pair, -1);
	}

	protected String OrderList() {
		return OrderList("all pairs", 1);
	}

	protected String OrderList(String pair, int active) {
		return OrderList(0, 1000, 0, -1, false, 0, -1, pair, active);
	}

	protected String OrderList(int from, int count, int from_id, int end_id,
			boolean asc, long since, long end, String pair, int active) {
		return trade_trans_list("OrderList", from, count, from_id, end_id, asc,
				since, end, pair, active);
	}

	private String trade_trans_list(String method, int from, int count,
			int from_id, int end_id, boolean asc, long since, long end,
			String pair, int active) {
		if (null != pair && pair.equals("all pairs"))
			pair = "0";
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		if (-1 != since) {
			parameters
					.add(new BasicNameValuePair("since", Long.toString(since)));
			if (-1 != end)
				parameters
						.add(new BasicNameValuePair("end", Long.toString(end)));
			else
				parameters
						.add(new BasicNameValuePair("end", Long
								.toString(Calendar.getInstance()
										.getTimeInMillis() / 1000)));
		} else if (-1 != from_id) {
			parameters.add(new BasicNameValuePair("from_id", Integer
					.toString(from_id)));
			if (-1 != end_id)
				parameters.add(new BasicNameValuePair("end_id", Integer
						.toString(end_id)));
			else
				parameters.add(new BasicNameValuePair("end_id", "2147483647"));
		} else if (-1 != from && 0 < count) {
			parameters.add(new BasicNameValuePair("from", Integer
					.toString(from)));
			parameters.add(new BasicNameValuePair("count", Integer
					.toString(count)));
		} else {
			parameters.add(new BasicNameValuePair("from", "0"));
			parameters.add(new BasicNameValuePair("count", "1000"));
		}
		if (asc)
			parameters.add(new BasicNameValuePair("order", "ASC"));
		else
			parameters.add(new BasicNameValuePair("order", "DESC"));
		if (null != pair && !pair.equals(""))
			parameters.add(new BasicNameValuePair("pair", pair));
		if (0 < active)
			parameters.add(new BasicNameValuePair("active", "1"));
		else if (0 == active)
			parameters.add(new BasicNameValuePair("active", "0"));

		List<NameValuePair> encryedHeader = encryHeader_updatePostdata(method,
				parameters);
		return downloadFromServer(trade_api_url, encryedHeader, parameters);
	}
	
	protected String ActiveOrders(String pair) {
		if (null == pair || pair.equals("all pairs"))
			pair = "0";
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("pair", pair));
		List<NameValuePair> encryedHeader = encryHeader_updatePostdata("ActiveOrders",
				parameters);
		return downloadFromServer(trade_api_url, encryedHeader, parameters);
	}

	protected List<NameValuePair> encryHeader_updatePostdata(String method,
			List<NameValuePair> parameters) {
		// attempt to change parameters as return values, so cannot be null
		assert (null != parameters);
		if (null == params.key || params.key.equals(""))
			return null;
		parameters.add(new BasicNameValuePair("nonce", "" + params.nonce));
		parameters.add(new BasicNameValuePair("method", method));

		String Sign = null;
		UrlEncodedFormEntity uef = null;
		try {
			uef = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
			OutputStream baos = new ByteArrayOutputStream();
			uef.writeTo(baos);
			Sign = baos.toString();
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		SecretKeySpec key = null;
		Mac mac = null;
		try {
			key = new SecretKeySpec(params.secret.getBytes("UTF-8"),
					"HmacSHA512");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			mac = Mac.getInstance("HmacSHA512");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		try {
			mac.init(key);
		} catch (InvalidKeyException e1) {
			e1.printStackTrace();
		}

		String hex = "";
		try {
			byte[] digest = mac.doFinal(Sign.getBytes("UTF-8"));
			for (int i = 0; i < digest.length; i++) {
				int b = digest[i] & 0xff;
				if (Integer.toHexString(b).length() == 1) {
					hex = hex + "0";
				}
				hex = hex + Integer.toHexString(b);
			}
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block"HmacSHA512");
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		List<NameValuePair> headerLines = new ArrayList<NameValuePair>();
		headerLines.add(new BasicNameValuePair("Key", params.key));
		headerLines.add(new BasicNameValuePair("Sign", hex));
		return headerLines;
	}

	protected synchronized String downloadFromServer(String url) {
		return downloadFromServer(url, null, null);
	}

	protected synchronized String downloadFromServer(String url,
			List<NameValuePair> header) {
		return downloadFromServer(url, header, null);
	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	protected synchronized String downloadFromServer(String url,
			List<NameValuePair> header, List<NameValuePair> data) {
		HttpClient client = getNewHttpClient();

		if (-1 != params.proxy_port) {
			((AbstractHttpClient) client)
					.getCredentialsProvider()
					.setCredentials(
							new AuthScope(params.proxy_host, params.proxy_port),
							new UsernamePasswordCredentials(
									params.proxy_username, params.proxy_passwd));
			// client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,new
			// HttpHost(params.proxy_host, params.proxy_port));
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					new HttpHost(params.proxy_host, params.proxy_port));
		}
		HttpHost target = new HttpHost(btce_host_name, 443, btce_scheme);
		HttpUriRequest request;
		if (null == data) {
			HttpGet get = new HttpGet(url);
			// add header
			request = get;
		} else {
			HttpPost post = new HttpPost(url);
			// add postdata
			UrlEncodedFormEntity uef = null;
			try {
				uef = new UrlEncodedFormEntity(data, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			post.setEntity(uef);
			request = post;
		}
		if (null != header) {
			for (int i = 0; i < header.size(); ++i) {
				request.setHeader(header.get(i).getName(), header.get(i)
						.getValue());
			}
		}
		request.setHeader("Accept-Encoding", "gzip");
		try {
			// HttpResponse response = client.execute(target, request);
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != HTTP_STATUS_OK) {
				return "{\"success\":0,\"error\":\"" + status.toString()
						+ "\"}";
			}
			HttpEntity entity = response.getEntity();
			InputStream instream = entity.getContent();
			Header contentEncoding = response
					.getFirstHeader("Content-Encoding");
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
			}
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			int readCount = 0;
			while ((readCount = instream.read(buff)) != -1)
				content.write(buff, 0, readCount);
			// add success json obj
			String info = new String(content.toByteArray());
			int len = info.length();
			if (0 != params.chart_start_time && params.pair.equals("btc_usd")
					&& btce_methods.ORDERS_UPDATE == params.method) {

			} else if (btce_methods.TRADES != params.method && 0 != len
					&& !info.contains("\"success\"")) {
				info = info.substring(0, len - 1) + ",\"success\":1"
						+ info.substring(len - 1);
			}
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"success\":0,\"error\":\"" + e.toString() + "\"}";
		}
	}
}
