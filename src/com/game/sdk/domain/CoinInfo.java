package com.game.sdk.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class CoinInfo {

	@JSONField(name = "user_id")
	public String userId;

	public String money;
	
	@JSONField(name = "game_money")
	public String gameMoney;

	@JSONField(name = "coupon_list")
	public List<CouponInfo> couponList;
	
	@JSONField(name = "coupon_count")
	public int couponCount;
	
}
