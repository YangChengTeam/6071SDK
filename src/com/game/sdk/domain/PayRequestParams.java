package com.game.sdk.domain;

public class PayRequestParams {
	public String is_game_pay;// 0为平台币充值，1为游戏充值
	public String pay_ways;// 充值方式，多个用|隔开，如alipay|wxpay|money|gamemoney
	public String amount;// 订单总金额
	public String good_type_name;// 卡券分类名称
	public String goods_id;// 卡券商品id
	public String card_id;// 卡券ID
	public String role;// 角色
	public String server;// 区服
	public String appid;
	public String productname;// 订单商品名称
	public String attach;// 游戏充值时为：游戏订单号
	public String md5signstr;
}
