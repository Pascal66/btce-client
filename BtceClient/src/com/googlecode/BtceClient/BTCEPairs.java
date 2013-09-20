package com.googlecode.BtceClient;

import java.util.HashMap;

public class BTCEPairs extends HashMap<String, String> {
	public HashMap<String, String> trade_depth_pairs;
	BTCEPairs() {
		//this.put("all pairs", "0");
		this.put("btc_usd", "1");
		this.put("ltc_btc", "10");
		this.put("nmc_btc", "13");
		this.put("ltc_usd", "14");
		this.put("btc_rur", "17");
		this.put("btc_eur", "19");
		this.put("usd_rur", "18");
		this.put("eur_usd", "20");
		this.put("ltc_rur", "21");
		this.put("nvc_btc", "22");
		this.put("trc_btc", "23");
		this.put("ppc_btc", "24");
		this.put("ftc_btc", "25");
		//this.put("cnc_btc", "26");
		this.put("ltc_eur", "27");
		this.put("nmc_usd", "28");
		this.put("nvc_usd", "29");
		
		trade_depth_pairs = new HashMap<String, String>();
//		trade_depth_pairs.put("btc_usd", "1");
//		trade_depth_pairs.put("ltc_btc", "10");
//		trade_depth_pairs.put("ltc_usd", "14");
	}
	
}
