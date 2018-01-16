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
 * Created by zhangkai on 16/9/20.
 */
public class UnBindSendCodeEngin extends BaseEngin<String> {

	public Context mContext;

	public int loginCount = 1;

	public String userName;
	
	public String mobileNumber;

	public UnBindSendCodeEngin(Context context, String userName,String mobileNumber) {
		super(context);
		this.mContext = context;
		this.userName = userName;
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String getUrl() {
		return ServerConfig.UNBIND_SEND_CODE_URL;
	}

	public ResultInfo<String> run() {
		ResultInfo<String> resultInfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_name", userName);
			params.put("mobile", mobileNumber);

			resultInfo = getResultInfo(true, String.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("解除绑定发送短信验证码----" + JSON.toJSONString(resultInfo.data));
			}

		} catch (Exception e) {
		}
		
		return resultInfo;
	}
	
}
