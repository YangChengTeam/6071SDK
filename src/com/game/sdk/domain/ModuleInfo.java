package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class ModuleInfo {

	public int id;

	public String title;

	public String ico;

	public int type;

	@JSONField(name = "type_val")
	public String typeVal;

	public int sort;

	public int status;
	
	public int num;
}
