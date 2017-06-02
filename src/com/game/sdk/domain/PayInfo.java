package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class PayInfo {

	// 订单号
	@JSONField(name = "order_sn")
	public String orderSn;
	// 支付相关参数
	public PayParams params;

	@JSONField(name = "rmb_money")
	public String rmbMoney;

	public String rsmd5;

	public String starttime;

	public int code;

	public String errorMsg;

}
