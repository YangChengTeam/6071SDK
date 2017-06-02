package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 活动
 * 
 * @author admin
 *
 */
public class CompAign {

	public int id;

	public String title;

	public String img;

	@JSONField(name = "start_time")
	public String startTime;

	@JSONField(name = "end_time")
	public String endTime;

	public String body;

	@JSONField(name = "game_id")
	public String gameId;

	@JSONField(name = "agent_id")
	public String agentId;

	@JSONField(name = "agent_pid")
	public String agentPid;

	public int status;

	public String flag;

	public int sort;

	@JSONField(name = "add_time")
	public String addTime;

	public int type;
	
	@JSONField(name = "type_value")
	public String typeValue;
}
