package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class ChargeMoneyInfo {
	
	public String id;
	
	@JSONField(name = "pay_money")
	public String chargeMoney;
	
	@JSONField(name = "real_money")
	public String realMoney;
	
}
