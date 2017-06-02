package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class GameCoin {
	
	@JSONField(name = "game_id")
	public String gameId;
	
	public String name;
	
	public String ico;
	
	public String money;
}
