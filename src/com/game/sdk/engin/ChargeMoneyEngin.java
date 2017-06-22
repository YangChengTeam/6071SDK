package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.ChargeMoneyList;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class ChargeMoneyEngin extends BaseEngin<ChargeMoneyList> {
	
	public ChargeMoneyEngin(Context context) {
		super(context);
	}
	
	@Override
	public String getUrl() {
		return ServerConfig.CHARGE_INIT_URL;
	}

	public ChargeMoneyList run() {
		ChargeMoneyList chargeMoneyList = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", GoagalInfo.userInfo != null ? GoagalInfo.userInfo.userId : "");
			ResultInfo<ChargeMoneyList> resultInfo = getResultInfo(true, ChargeMoneyList.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("payinit获取结果----" + JSON.toJSONString(resultInfo.data));

				if (resultInfo.data != null) {
					chargeMoneyList = resultInfo.data;
				}
			}
		} catch (Exception e) {
		}
		return chargeMoneyList;
	}

}
