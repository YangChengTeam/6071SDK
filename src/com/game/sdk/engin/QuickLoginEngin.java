package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.FYGameSDK;
import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginResult;
import com.game.sdk.domain.QuickLoginInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MobileInfoUtil;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class QuickLoginEngin extends BaseEngin<QuickLoginInfo> {

	public Context mContext;

	public int loginCount = 1;

	public String mobileNumber;

	public boolean isRetry = false;

	public QuickLoginEngin() {
	}

	public QuickLoginEngin(Context context, boolean isRetry, String mobileNumber) {
		super(context);
		this.mContext = context;
		this.isRetry = isRetry;
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String getUrl() {
		return ServerConfig.QUICK_LOGIN_URL;
	}

	public LoginResult run() {
		LoginResult loginResult = new LoginResult();
		loginResult.result = false;
		try {

			Map<String, String> params = new HashMap<String, String>();
			params.put("code", GoagalInfo.validateCode);
			params.put("f", GoagalInfo.gameid);
			params.put("is_quick", GoagalInfo.isQuick + "");
			
			
			if (!StringUtils.isEmpty(mobileNumber)) {
				params.put("m", mobileNumber);
			}
			
			ResultInfo<QuickLoginInfo> resultInfo = getResultInfo(true, QuickLoginInfo.class, params);
			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("手机号-验证码方式-登录成功----" + JSON.toJSONString(resultInfo.data));
				//Util.toast(mContext, "手机号-验证码方式-登录成功---");
				if (GoagalInfo.userInfo == null) {
					GoagalInfo.userInfo = new UserInfo();
				}
				// 保存用户信息
				saveUserInfo(resultInfo.data);
				loginResult.result = true;
			}else{
				loginResult.result = false;
				loginResult.message = resultInfo.message;
			}
		} catch (Exception e) {
			loginResult.result = false;
		}
		
		Logger.msg("result---" + loginResult.result);
		return loginResult;
	}

	public LoginResult quickRun() {
		LoginResult loginResult = new LoginResult();
		loginResult.result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("code", GoagalInfo.validateCode);
			params.put("f", GoagalInfo.gameid);
			params.put("is_quick", GoagalInfo.isQuick + "");

			if (!StringUtils.isEmpty(mobileNumber)) {
				params.put("m", mobileNumber);
			}

			do {
				ResultInfo<QuickLoginInfo> resultInfo = getResultInfo(true, QuickLoginInfo.class, params);

				if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {

					Logger.msg("一键登录后结果----" + JSON.toJSONString(resultInfo.data));

					if (GoagalInfo.userInfo == null) {
						GoagalInfo.userInfo = new UserInfo();
					}
					// 保存用户信息
					saveUserInfo(resultInfo.data);
					loginResult.result = true;
					loginCount = GoagalInfo.inItInfo.mqrNumLimit != null
							? Integer.parseInt(GoagalInfo.inItInfo.mqrNumLimit) : 100;
					break;
				} else {
					if (!StringUtils.isEmpty(GoagalInfo.inItInfo.mqrNumLimit)) {
						Logger.msg("当前请求的次数----" + loginCount);
						if (loginCount < Integer.parseInt(GoagalInfo.inItInfo.mqrNumLimit)) {
							//根据后台设置的延时时间，延时处理请求
							if(GoagalInfo.inItInfo != null && !StringUtils.isEmpty(GoagalInfo.inItInfo.mqrDelay)){
								Thread.sleep(Long.parseLong(GoagalInfo.inItInfo.mqrDelay));
							}
							loginCount++;
						} else {
							break;
						}
					} else {
						break;
					}
				}
			} while (isRetry && loginCount < Integer.parseInt(GoagalInfo.inItInfo.mqrNumLimit));

		} catch (Exception e) {
			loginResult.result = false;
		}

		Logger.msg("result---" + loginResult.result);
		return loginResult;
	}

	/**
	 * 保存用户信息
	 * 
	 * @param iusername
	 * @param ipassword
	 */
	public void saveUserInfo(QuickLoginInfo quickLoginInfo) {

		// 存储账号，若手机号为空，则存储用户名
		String accountNumber = StringUtils.isEmpty(quickLoginInfo.userName) == true ? quickLoginInfo.mobile
				: quickLoginInfo.userName;

		boolean isExist = UserLoginInfodao.getInstance(mContext).findUserLoginInfoByName(accountNumber);

		if (!isExist) {
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(accountNumber, quickLoginInfo.passWord,
					quickLoginInfo.isValiMobile, 0);
		} else {
			// 先删除
			UserLoginInfodao.getInstance(mContext).deleteUserLoginByName(accountNumber);
			// 再保存最新的信息
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(accountNumber, quickLoginInfo.passWord,
					quickLoginInfo.isValiMobile, 0);
		}
		
		GoagalInfo.userInfo.username = accountNumber;
		GoagalInfo.userInfo.mobile = quickLoginInfo.mobile;
		GoagalInfo.userInfo.password = quickLoginInfo.passWord;
		GoagalInfo.userInfo.userId = quickLoginInfo.userId;
		GoagalInfo.userInfo.logintime = quickLoginInfo.lastLoginTime;
		GoagalInfo.userInfo.sign = quickLoginInfo.sign;
		GoagalInfo.userInfo.validateMobile = quickLoginInfo.isValiMobile;
		GoagalInfo.userInfo.agentId = quickLoginInfo.agentId;
		
		GoagalInfo.userInfo.newSdkReg = quickLoginInfo.newSdkReg;
		GoagalInfo.userInfo.fixName = quickLoginInfo.fixName;
		GoagalInfo.userInfo.cpNotice = quickLoginInfo.cpNotice;
		
		GoagalInfo.isLogin = true;
		GoagalInfo.loginType = 2;
		if (quickLoginInfo.gameNotice != null) {
			GoagalInfo.noticeMsg = quickLoginInfo.gameNotice.body;
		}

		// 保存登录成功的登录方式，下次直接到此页面
		PreferenceUtil.getImpl(this.context).putInt(SystemUtil.getPhoneIMEI(mContext), GoagalInfo.loginType);

		// 保存用户信息到本地
		//MobileInfoUtil.insertUserInfo(mContext, GoagalInfo.userInfo);
	}
}
