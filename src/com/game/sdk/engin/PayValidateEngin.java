package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.game.sdk.domain.PayValidateResult;
import com.game.sdk.domain.PointMessage;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;

import android.content.Context;

/**
 * 订单验证
 */
public class PayValidateEngin extends BaseEngin<PointMessage> {

	public Context mContext;

	public String orderId;

	public PayValidateEngin(Context context, String orderId) {
		super(context);
		this.mContext = context;
		this.orderId = orderId;
	}
	
	@Override
	public String getUrl() {
		return ServerConfig.PAY_VALIDATE_URL;
	}

	public PayValidateResult run() {
		PayValidateResult payValidateResult = new PayValidateResult();
		
		ResultInfo<PointMessage> resultInfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_sn", orderId);
			resultInfo = getResultInfo(true, PointMessage.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				payValidateResult.result = true;
				payValidateResult.pointMessage = resultInfo.data != null && resultInfo.data.pointMessage != null ? resultInfo.data.pointMessage : "";
			} else {
				payValidateResult.result = false;
			}
		} catch (Exception e) {
			payValidateResult.result = false;
		}

		return payValidateResult;
	}

}
