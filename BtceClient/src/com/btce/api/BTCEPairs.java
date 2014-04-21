package com.btce.api;

import java.util.HashMap;

public class BTCEPairs extends HashMap<String, pair_values> {
	public HashMap<String, String> trade_depth_pairs;

	public BTCEPairs() {
		// this.put("all pairs", "0");
		this.put("btc_usd", new pair_values("1", 3));// checked
		this.put("ltc_btc", new pair_values("10", 5));// checked
		this.put("nmc_btc", new pair_values("13", 5));// checked
		this.put("ltc_usd", new pair_values("14", 5));// checked, 6 is passed
		this.put("btc_rur", new pair_values("17", 5));// checked
		this.put("usd_rur", new pair_values("18", 5));// price 25 at least,
														// checked
		this.put("btc_eur", new pair_values("19", 5));// checked
		this.put("eur_usd", new pair_values("20", 5));// price 1 at least,
														// checked
		this.put("ltc_rur", new pair_values("21", 5));// checked
		this.put("nvc_btc", new pair_values("22", 5));// checked
		// this.put("trc_btc", new pair_values("23",6));
		this.put("ppc_btc", new pair_values("24", 5));// checked,8 is passed
		this.put("ftc_btc", new pair_values("25", 5));// checked
		// this.put("cnc_btc", new pair_values("26",6));
		this.put("ltc_eur", new pair_values("27", 3));// checked
		this.put("nmc_usd", new pair_values("28", 3));// checked
		this.put("nvc_usd", new pair_values("29", 3));// checked
		this.put("xpm_btc", new pair_values("30", 5));// checked
		this.put("ppc_usd", new pair_values("31", 3));// checked
		this.put("eur_rur", new pair_values("32", 3));// no check
		this.put("btc_cnh", new pair_values("33", 3));// no check
		this.put("ltc_cnh", new pair_values("34", 3));// no check
		this.put("usd_cnh", new pair_values("35", 3));// no check
		this.put("btc_gbp", new pair_values("36", 3));// no check
		this.put("ltc_gbp", new pair_values("37", 3));// no check
		this.put("gbp_usd", new pair_values("38", 3));// no check
		
		trade_depth_pairs = new HashMap<String, String>();
		// trade_depth_pairs.put("btc_usd", "1");
		// trade_depth_pairs.put("ltc_btc", "10");
		// trade_depth_pairs.put("ltc_usd", "14");
	}

}
