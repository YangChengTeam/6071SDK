package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

/**
 * 解除绑定
 */
public class UnBindPhoneEngin extends BaseEngin<String> {

	public Context mContext;

	public String userName;
	
	public String mobileNumber;
	
	public String validateCode;
	
	public UnBindPhoneEngin(Context context, String userName,String mobileNumber,String code) {
		super(context);
		this.mContext = context;
		this.userName = userName;
		this.mobileNumber = mobileNumber;
		this.validateCode = code;
	}

	@Override
	public String getUrl() {
		return ServerConfig.UNBIND_PHONE_NUMBER_URL;
	}

	public ResultInfo<String> run() {
		ResultInfo<String> resultInfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_name", userName);
			params.put("mobile", mobileNumber);
			params.put("code", validateCode);
			resultInfo = getResultInfo(true, String.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("解除绑定结果----" + JSON.toJSONString(resultInfo.data));
			}

		} catch (Exception e) {
		}
		
		return resultInfo;
	}
	
}
