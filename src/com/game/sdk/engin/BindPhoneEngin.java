package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.BindPhoneResult;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class BindPhoneEngin extends BaseEngin<String> {

	public Context mContext;
	
	public String phoneNumber;
	
	public String userName;
	
	public String validateCode;
	
	public BindPhoneEngin(Context context,String phoneNumber,String userName,String validateCode) {
		super(context);
		this.mContext = context;
		this.phoneNumber = phoneNumber;
		this.userName = userName;
		this.validateCode = validateCode;
	}

	@Override
	public String getUrl() {
		return ServerConfig.BIND_PHONE_URL;
	}

	public BindPhoneResult run() {
		BindPhoneResult bindPhoneResult = new BindPhoneResult();
		bindPhoneResult.result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("m", phoneNumber);
			params.put("n", userName);
			params.put("code", validateCode);
			ResultInfo<String> resultInfo = getResultInfo(true, String.class, params);
			
			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("绑定手机号结果----" + JSON.toJSONString(resultInfo.data));
				bindPhoneResult.result = true;
			} else {
				bindPhoneResult.result = false;
				bindPhoneResult.message = resultInfo.message;
			}
			
		} catch (Exception e) {
			bindPhoneResult.result = false;
		}
		
		Logger.msg("result---" + bindPhoneResult.result);
		return bindPhoneResult;
	}

	
}
