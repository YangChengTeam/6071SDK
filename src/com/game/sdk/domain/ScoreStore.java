package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class ScoreStore {

	public int id;

	public String name;

	@JSONField(name = "type_id")
	public int typeId;

	@JSONField(name = "type_val")
	public String typeVal;

	@JSONField(name = "game_id")
	public String gameId;

	public String img;

	public String desp;

	public String price;

	public String stock;

	@JSONField(name = "curr_stock")
	public String currStock;

	@JSONField(name = "max_buy_num")
	public String maxBuyNum;

	@JSONField(name = "is_comm")
	public String isComm;

	@JSONField(name = "uc_start_time")
	public String ucStartTime;

	@JSONField(name = "uc_end_time")
	public String ucEndTime;

	@JSONField(name = "uc_money")
	public String ucMoney;

	public String sort;

	public int status;

	@JSONField(name = "type_name")
	public String typeName;

	@JSONField(name = "game_name")
	public String gameName;

}
