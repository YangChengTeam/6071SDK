package com.game.sdk.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class ScoreStoreList {
	
	@JSONField(name = "list")
	public List<ScoreStore> list;
}
