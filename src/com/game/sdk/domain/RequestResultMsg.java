package com.game.sdk.domain;

public class RequestResultMsg {

	public int code;// 接口请求状态码
	public String msg;// 接口请求的消息提示
	public boolean isSuccess = true; //接口请求是否成功

	public RequestResultMsg(int code, String msg, boolean isSuccess) {
		this.code = code;
		this.msg = msg;
		this.isSuccess = isSuccess;
	}
}
