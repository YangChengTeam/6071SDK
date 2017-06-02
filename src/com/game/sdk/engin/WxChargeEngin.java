package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.PayInfo;
import com.game.sdk.domain.PayRequestParams;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class WxChargeEngin extends BaseEngin<PayInfo> {

	public Context mContext;

	public String userId;

	// 充值类型,0为平台币充值，1为游戏充值
	public int payType;

	// 充值方式(alipay|wxpay|money|gamemoney)
	public String payWay;

	public double chargeMoney = 0;

	public String md5signstr;

	public String orderId;

	private PayRequestParams payRequestParams;

	public WxChargeEngin() {
	}

	public WxChargeEngin(Context context, String orderId, String md5signstr) {
		this.orderId = orderId;
		this.md5signstr = md5signstr;
	}

	@Override
	public String getUrl() {
		return ServerConfig.WX_CONTINUE_PAY_URL;
	}

	/**
	 * 微信继续支付，获取到的参数
	 * 
	 * @return
	 */
	public PayInfo wxContinuePay() {
		PayInfo payinfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderid", orderId);
			params.put("md5signstr", md5signstr);

			ResultInfo<PayInfo> resultInfo = getResultInfo(true, PayInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("充值结果----" + JSON.toJSONString(resultInfo));
				payinfo = resultInfo.data;

				payinfo.code = resultInfo.code;
				payinfo.errorMsg = resultInfo.message;
			} else {
				payinfo = new PayInfo();
				payinfo.code = resultInfo.code;
				payinfo.errorMsg = resultInfo.message != null ? resultInfo.message : "支付异常，请稍后重试";
			}
		} catch (Exception e) {
		}

		return payinfo;
	}

}
