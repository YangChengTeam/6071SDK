package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class CouponInfo {

	@JSONField(name = "card_id")
	public String cardId;// 卡券id

	@JSONField(name = "card_type_name")
	public String cardTypeName;// 卡券类型

	@JSONField(name = "card_type_title")
	public String cardTypeTitle;// 卡券类型说明

	@JSONField(name = "goods_id")
	public String goodsId;// 对应商品编号

	@JSONField(name = "goods_name")
	public String goodsName;// 商品名称，如：阴阳师优惠券

	@JSONField(name = "goods_worth")
	public String goodsWorth;// 商品价值（抵用金额）

	@JSONField(name = "goods_uc_start_time")
	public String goodsUcStartTime;// 使用优惠券条件：起始时间

	@JSONField(name = "goods_uc_end_time")
	public String goodsUcEndTime;// 使用优惠券条件：截止时间

	@JSONField(name = "goodsUcMoney")
	public String goods_uc_money;// 使用优惠券条件：最低金额
}
