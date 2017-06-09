package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.domain.UpdateInfo;
import com.game.sdk.domain.UpdateInfoResult;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.StringUtils;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class UpdateUserInfoEngin extends BaseEngin<UpdateInfo> {

	public Context mContext;

	public UserInfo updateUserInfo;

	public UpdateUserInfoEngin() {
	}

	public UpdateUserInfoEngin(Context context, UserInfo uUserInfo) {
		super(context);
		this.mContext = context;
		this.updateUserInfo = uUserInfo;
	}

	@Override
	public String getUrl() {
		return ServerConfig.UPDATE_USER_INFO_URL;
	}

	/**
	 * 更新用户信息
	 * 
	 * @param updateUserInfo
	 * @return
	 */
	public UpdateInfoResult updateUserInfo() {
		UpdateInfoResult updateInfoResult = new UpdateInfoResult();
		
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", GoagalInfo.userInfo.userId);
			
			if(updateUserInfo != null){
				if (!StringUtils.isEmpty(updateUserInfo.face)) {
					params.put("face", updateUserInfo.face);
				}

				if (!StringUtils.isEmpty(updateUserInfo.nickName)) {
					params.put("nick_name", updateUserInfo.nickName);
				}

				if (updateUserInfo.sex > 0) {
					params.put("sex", updateUserInfo.sex + "");
				}

				if (!StringUtils.isEmpty(updateUserInfo.birth)) {
					params.put("birth", updateUserInfo.birth);
				}

				if (!StringUtils.isEmpty(updateUserInfo.email)) {
					params.put("email", updateUserInfo.email);
				}

				if (!StringUtils.isEmpty(updateUserInfo.qq)) {
					params.put("qq", updateUserInfo.qq);
				}
			}
			
			ResultInfo<UpdateInfo> resultInfo = getResultInfo(true, UpdateInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				updateInfoResult.result = true;
				updateInfoResult.pointMessage = resultInfo.data != null && resultInfo.data.pointMessage != null ? resultInfo.data.pointMessage : "";
				Logger.msg("修改用户信息成功!");
			} else {
				updateInfoResult.result = false;
			}
		} catch (Exception e) {
			updateInfoResult.result = false;
		}
		
		return updateInfoResult;
	}

}
