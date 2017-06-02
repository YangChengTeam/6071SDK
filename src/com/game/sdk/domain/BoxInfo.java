package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class BoxInfo {

	@JSONField(name = "box_down_url")
	public String boxDownUrl;

	@JSONField(name = "box_package_name")
	public String boxPackageName;
	
}
