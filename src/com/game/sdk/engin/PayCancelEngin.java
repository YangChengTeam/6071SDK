package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class PayCancelEngin extends BaseEngin<String> {

	public Context mContext;

	public String orderId;

	public PayCancelEngin(Context context, String orderId) {
		super(context);
		this.mContext = context;
		this.orderId = orderId;
	}
	
	@Override
	public String getUrl() {
		return ServerConfig.PAY_CANCEL_URL;
	}

	public boolean run() {
		boolean flag = true;
		ResultInfo<String> resultInfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderid", orderId);
			resultInfo = getResultInfo(true, String.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

}
