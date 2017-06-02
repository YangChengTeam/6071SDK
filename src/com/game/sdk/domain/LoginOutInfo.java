package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class LoginOutInfo {

	public String img;
	
	public int type;
	
	@JSONField(name = "type_value")
	public String typeVal;

	@JSONField(name = "package_name")
	public String packageName;
	
	@JSONField(name = "game_name")
	public String gameName;
	
	@JSONField(name = "game_icon")
	public String gameIcon;
}
