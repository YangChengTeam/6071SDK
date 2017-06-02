package com.game.sdk.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class ChargeRecordList {
	
	@JSONField(name = "list")
	public List<ChargeRecord> chargeRecordList;
}
