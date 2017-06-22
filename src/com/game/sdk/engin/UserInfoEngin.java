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
public class UserInfoEngin extends BaseEngin<UserInfo> {

	public Context mContext;

	public String userId;

	public UserInfoEngin() {
	}

	public UserInfoEngin(Context context, String userId) {
		super(context);
		this.mContext = context;
		this.userId = userId;
	}

	@Override
	public String getUrl() {
		return ServerConfig.USER_INFO_URL;
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public boolean getUserInfo() {
		boolean result = true;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", userId);

			ResultInfo<UserInfo> resultInfo = getResultInfo(true, UserInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("获取用户信息结果----" + JSON.toJSONString(resultInfo.data));
				if (GoagalInfo.userInfo == null) {
					GoagalInfo.userInfo = new UserInfo();
				}

				// 保存获取到的用户信息
				saveUserInfo(resultInfo.data);
			} else {
				result = false;
			}

		} catch (Exception e) {
			result = false;
		}

		return result;
	}
	
	/**
	 * 保存用户信息
	 * 
	 * @param iusername
	 * @param ipassword
	 */
	public void saveUserInfo(UserInfo userInfo) {

		GoagalInfo.userInfo.userId = userInfo.userId;
		GoagalInfo.userInfo.username = userInfo.username;

		GoagalInfo.userInfo.mobile = userInfo.mobile;
		GoagalInfo.userInfo.nickName = userInfo.nickName;
		GoagalInfo.userInfo.face = userInfo.face;
		GoagalInfo.userInfo.sex = userInfo.sex;
		GoagalInfo.userInfo.birth = userInfo.birth;
		GoagalInfo.userInfo.areaId = userInfo.areaId;
		GoagalInfo.userInfo.email = userInfo.email;
		GoagalInfo.userInfo.qq = userInfo.qq;
		GoagalInfo.userInfo.ttb = userInfo.ttb;
		GoagalInfo.userInfo.gttb = userInfo.gttb;
		GoagalInfo.userInfo.validateMobile = userInfo.validateMobile;
		GoagalInfo.userInfo.kefuQQ = userInfo.kefuQQ;
		GoagalInfo.userInfo.vipLevel = userInfo.vipLevel;
		GoagalInfo.userInfo.shareContent = userInfo.shareContent;
		GoagalInfo.userInfo.isGameReturn = userInfo.isGameReturn;
	}

}
