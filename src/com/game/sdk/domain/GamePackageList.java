package com.game.sdk.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class GamePackageList {
	
	@JSONField(name = "list")
	public List<GamePackage> list;
}
