package com.game.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class QuickLoginInfo {

	@JSONField(name = "user_id")
	public String userId;

	@JSONField(name = "name")
	public String userName;

	@JSONField(name = "mobile")
	public String mobile;

	@JSONField(name = "pwd")
	public String passWord;

	@JSONField(name = "last_login_time")
	public long lastLoginTime;

	@JSONField(name = "reg_time")
	public long registerTime;

	@JSONField(name = "is_vali_mobile")
	public int isValiMobile;

	@JSONField(name = "sign")
	public String sign;

	@JSONField(name = "mk")
	public String memkey;

	@JSONField(name = "game_notice")
	public NoticeMsg gameNotice;
	
	@JSONField(name = "agent_id")
	public String agentId;
	
}
