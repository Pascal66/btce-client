package com.googlecode.BtceClient;

import java.util.ArrayList;
import java.util.LinkedList;

import com.googlecode.BtceClient.BTCEHelper.btce_params;
import com.googlecode.BtceClient.OrdersViewActivity.order_info;

import android.app.Application;
import android.os.Bundle;

public class MyApp extends Application {

	public static final String PREFS_NAME = "setting";
	public LimitingList<String> app_logs = new LimitingList<String>(200);
	public btce_params app_params = new btce_params();
	public ArrayList<order_info> app_orders = new ArrayList<order_info>();
	public Bundle app_pair_funds = new Bundle();
	public DBManager app_dbmgr = null;
	public int app_trans_num = 0;
	// BTCEPairs app_all_pairs = new BTCEPairs();
	int app_candlestick_number = 24;
	int app_candlestick_period = 1;
	int app_timer_wifi_period = 0, app_timer_mobile_period = 0;
	int app_timer_wifi_period_all = 0, app_timer_mobile_period_all = 0;
	boolean app_update_all_pair_depth_trades = false;
	String app_layout = "main";
	int app_layout_ids[] = { R.layout.main, R.layout.main_pad,
			R.layout.main_pad2 };
}