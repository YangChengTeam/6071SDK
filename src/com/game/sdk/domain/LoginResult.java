package com.game.sdk.domain;

/**
 * Created by zhangkai on 16/9/19.
 */
public class LoginResult {

	public boolean result;

	public String message;

	// 是否通过新SDK注册；0:否；1: 是
	public int newSdkReg;

	// 矫正名，如果改值不为空，则把改值作为用户民返回给CP，用来处理用户角色丢失的情况
	public String fixName;

	// CP是以用户名还是ID为唯一标记，0: 用户名；1：用户ID。该值为0，表示CP属于有问题CP
	public int cpNotice;
	
}
