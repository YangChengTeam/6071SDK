package com.game.sdk.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class ChargeMoneyList {

	@JSONField(name = "open")
	public boolean isOpen;

	@JSONField(name = "detail")
	public List<ChargeMoneyInfo> chargeMoneyList;

}
