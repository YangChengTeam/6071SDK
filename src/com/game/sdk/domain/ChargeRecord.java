package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class ChargeRecord {
	
	@JSONField(name = "order_sn")
	public String orderSn;
	
	public int status;
	
	@JSONField(name = "status_msg")
	public String statusMsg;
	
	public String desp;
	
	public String money;
	
	@JSONField(name = "rmb_money")
	public String rmbMoney;
	
	@JSONField(name = "finish_time")
	public String finishTime;
	
	@JSONField(name = "pay_way_title")
	public String payWayTitle;
}
