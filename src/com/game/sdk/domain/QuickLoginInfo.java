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
	
	//是否通过新SDK注册；0:否；1: 是
	@JSONField(name = "new_sdk_reg")
	public int newSdkReg;
	
	//矫正名，如果改值不为空，则把改值作为用户民返回给CP，用来处理用户角色丢失的情况
	@JSONField(name = "fix_name")
	public String fixName;
	
	//CP是以用户名还是ID为唯一标记，0: 用户名；1：用户ID。该值为0，表示CP属于有问题CP
	@JSONField(name = "cp_notice")
	public int cpNotice;
	
}
