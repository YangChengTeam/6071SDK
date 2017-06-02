package com.game.sdk.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class GameCoinList {
	
	@JSONField(name = "game_list")
	public List<GameCoin> gameList;
}
