package com.game.sdk.domain;

/**
 * author janecer 2014-3-29下午4:08:05 成功登录后 信息回调信息
 */
public class LogincallBack {

	public String username;// 用户登录成功后，返回的用户信息
	
	public String userId;// 用户登录成功后，返回的用户信息

	public long logintime;// 时间戳

	public String sign;
	
	public boolean isBindPhone;
}
