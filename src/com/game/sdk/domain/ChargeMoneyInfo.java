package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class ChargeMoneyInfo {
	
	public String id;
	
	@JSONField(name = "pay_money")
	public String chargeMoney;
	
	@JSONField(name = "real_money")
	public String realMoney;
	
	@JSONField(name = "percent")
	public String percent;
	
	//返利游戏币数量
	@JSONField(name = "return_game_money")
	public String returnGameMoney;
}
