package com.game.sdk.domain;

import java.io.Serializable;

/**
 * author janecer 2014年7月1日上午10:16:18
 */
public interface OnChargerListener extends Serializable {

	/**
	 * 充值成功，充值的金额数
	 * 
	 * @param money
	 */
	void chargerSuccess(double money);

	/**
	 * 支付失败错误信息提示
	 * 
	 * @param msg
	 *            充值错误信息提示
	 * @param money
	 *            预充值金额
	 */
	void chargerFail(String msg, double money);

}
