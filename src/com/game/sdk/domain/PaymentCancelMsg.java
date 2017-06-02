package com.game.sdk.domain;

/**
 * author janecer 2014-3-29下午4:10:41 充值失败的消息提示
 */
public class PaymentCancelMsg {
	public int code;// 状态码
	public String msg;// 失败的消息提示
	public float money;// 原本充值的金额数量
}
