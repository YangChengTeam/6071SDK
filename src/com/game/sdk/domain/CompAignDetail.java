package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class CompAignDetail {

	public String title;

	public String img;

	@JSONField(name = "start_time")
	public String startTime;

	@JSONField(name = "end_time")
	public String endTime;

	public String body;

	public int type;

	@JSONField(name = "type_value")
	public String typeValue;
}
