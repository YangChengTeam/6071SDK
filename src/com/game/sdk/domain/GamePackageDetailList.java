package com.game.sdk.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class GamePackageDetailList {

	@JSONField(name = "list")
	public List<GamePackageDetail> list;
}
