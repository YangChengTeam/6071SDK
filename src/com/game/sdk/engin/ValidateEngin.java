package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class ValidateEngin extends BaseEngin<String> {

	public Context mContext;

	public int loginCount = 1;

	public String mobileNumber;

	public ValidateEngin(Context context, String mobileNumber) {
		super(context);
		this.mContext = context;
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String getUrl() {
		return ServerConfig.GET_VALIDATE_URL;
	}

	public ResultInfo<String> run() {
		ResultInfo<String> resultInfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("m", mobileNumber);

			resultInfo = getResultInfo(true, String.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("获取短信验证码结果----" + JSON.toJSONString(resultInfo.data));
				if (GoagalInfo.userInfo == null) {
					GoagalInfo.userInfo = new UserInfo();
				}
			}

		} catch (Exception e) {
		}
		
		return resultInfo;
	}
	
}
