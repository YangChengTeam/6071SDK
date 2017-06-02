package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class VersionInfo {
	
	@JSONField(name = "is_upd")
	public int isUpd;

	@JSONField(name = "down_url")
	public String downUrl;
	
}
