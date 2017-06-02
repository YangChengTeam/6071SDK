package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class GamePackageDetail {
	
	public int id;
	
	public String name;

	public String content;

	@JSONField(name = "change_note")
	public String changeNote;

	public String desp;

	public String img;

	@JSONField(name = "is_pay")
	public String isPay;

	@JSONField(name = "start_time")
	public String startTime;

	@JSONField(name = "end_time")
	public String endTime;

	@JSONField(name = "total_num")
	public String totalNum;

	@JSONField(name = "remain_num")
	public String remainNum;

	@JSONField(name = "goods_id")
	public String goodsId;

	@JSONField(name = "goods_type_id")
	public String goodsTypeId;

	@JSONField(name = "access_date")
	public String accessDate;
	
}
