package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class GamePackage {

	@JSONField(name = "game_id")
	public String gameId;

	@JSONField(name = "game_name")
	public String gameName;

	@JSONField(name = "last_gift_name")
	public String lastGiftName;

	public String ico;

	public String num;
}
