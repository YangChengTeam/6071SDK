package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.game.sdk.domain.PayContinueInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

public class PayContinueEngin extends BaseEngin<PayContinueInfo> {

	public Context mContext;

	public String orderId;

	private static PayContinueEngin payContinueEngin;

	public static PayContinueEngin getImpl(Context context) {
		if (payContinueEngin == null) {
			synchronized (MainModuleEngin.class) {
				payContinueEngin = new PayContinueEngin(context);
			}
		}
		return payContinueEngin;
	}

	public PayContinueEngin(Context context) {
		super(context);
	}

	public PayContinueEngin(Context context, String orderId) {
		super(context);
		this.mContext = context;
		this.orderId = orderId;
	}

	@Override
	public String getUrl() {
		return ServerConfig.CONTINUE_PAY_URL;
	}

	public PayContinueInfo run() {
		PayContinueInfo payContinueInfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderid", orderId);
			ResultInfo<PayContinueInfo> resultInfo = getResultInfo(true, PayContinueInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				payContinueInfo = resultInfo.data;
			}

		} catch (Exception e) {
			Logger.msg("PayContinueEngin---获取数据错误");
		}

		return payContinueInfo;
	}

}
