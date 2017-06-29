package com.game.sdk.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class ChargeMoneyList {

	@JSONField(name = "open")
	public boolean isOpen;

	@JSONField(name = "detail")
	public List<ChargeMoneyInfo> chargeMoneyList;

	//充值金额阈值
	@JSONField(name = "mix_money")
	public String mixMoney;
	
	//低于mix_money的返利比例
	@JSONField(name = "rate_low")
	public String rateLow;
	
	//高于mix_money的返利比例
	@JSONField(name = "rate_high")
	public String rateHigh;

	//充值返利最低限额
	@JSONField(name = "return_range_money")
	public String returnRangeMoney;
	
}
