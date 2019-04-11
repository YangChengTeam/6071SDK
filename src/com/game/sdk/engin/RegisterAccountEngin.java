package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginResult;
import com.game.sdk.domain.QuickLoginInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.AccountInfoUtil;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.GameBox2SDKUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by zhangkai on 16/9/20.
 */
public class RegisterAccountEngin extends BaseEngin<QuickLoginInfo> {

	public Context mContext;
	
	public String userName;
	
	public String passWord;
	
	public RegisterAccountEngin(Context context,String userName,String passWord) {
		super(context);
		this.mContext = context;
		this.userName = userName;
		this.passWord = passWord;
	}

	@Override
	public String getUrl() {
		return ServerConfig.REGISTER_ACCOUNT_URL;
	}

	public LoginResult run() {
		LoginResult loginResult = new LoginResult();
		loginResult.result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("n", userName);
			params.put("p", passWord);
			params.put("f", GoagalInfo.fromId);
			params.put("is_quick", GoagalInfo.isQuick+"");
			
			ResultInfo<QuickLoginInfo> resultInfo = getResultInfo(true, QuickLoginInfo.class, params);
			
			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("注册账号结果----" + JSON.toJSONString(resultInfo.data));
				if (GoagalInfo.userInfo == null) {
					GoagalInfo.userInfo = new UserInfo();
				}
				// 保存用户信息
				saveUserInfo(resultInfo.data);
				
				loginResult.result = true;
			} else {
				loginResult.result = false;
			}
			
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
		Logger.msg("register success --->" + JSON.toJSONString(quickLoginInfo));
		//存储账号
		String accountNumber = quickLoginInfo.userName;
		
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
		
		//返回是否实名认证，生日
		GoagalInfo.userInfo.isAuthenticated = quickLoginInfo.isAuthenticated;
		GoagalInfo.userInfo.birthday = quickLoginInfo.birthday;
		
		//TODO,注册成功后，不需要设置为已登录
		//1.如果点击"进入游戏"，则回调标记已登录成功，直接进入游戏
		//2.如果此时点击"返回键"，则视为用户未登录，根据需要重新跳转到登录界面
		//GoagalInfo.isLogin = true;
		
		if(quickLoginInfo.gameNotice != null){
			GoagalInfo.noticeMsg = quickLoginInfo.gameNotice.body;
		}
		
		GoagalInfo.loginType = 2;
		
		boolean isExist = UserLoginInfodao.getInstance(mContext).findUserLoginInfoByName(accountNumber);

		if (!isExist) {
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(accountNumber, quickLoginInfo.passWord,quickLoginInfo.isValiMobile,Constants.PHONE_LOGIN_TYPE);
		} else {
			// 先删除
			UserLoginInfodao.getInstance(mContext).deleteUserLoginByName(accountNumber);
			// 再保存最新的信息
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(accountNumber, quickLoginInfo.passWord,quickLoginInfo.isValiMobile,Constants.PHONE_LOGIN_TYPE);
		}
		
		//保存登录成功的登录方式，下次直接到此页面
		PreferenceUtil.getImpl(this.context).putInt(SystemUtil.getPhoneIMEI(mContext),GoagalInfo.loginType);
		
		//保存用户信息到本地
		AccountInfoUtil.insertUserInfo(mContext, GoagalInfo.userInfo);
	}

}
