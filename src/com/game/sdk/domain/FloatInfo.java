package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class FloatInfo {

	@JSONField(name = "float_drag")
	public String floatDrag;

	@JSONField(name = "float_holder")
	public String floatLeft;

	@JSONField(name = "float_holder2")
	public String floatRight;
}
