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
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class LoginEngin extends BaseEngin<QuickLoginInfo> {

	public Context mContext;

	public int loginCount = 1;
	
	public int accountType = 0;
	
	public LoginEngin(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	public String getUrl() {
		return ServerConfig.LOGIN_URL;
	}

	public LoginResult run() {
		LoginResult loginResult = new LoginResult();
		loginResult.result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("n", GoagalInfo.userInfo.username);
			params.put("p", GoagalInfo.userInfo.password);
			
			ResultInfo<QuickLoginInfo> resultInfo = getResultInfo(true, QuickLoginInfo.class, params);
			
			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("无需验证码登录结果----" + JSON.toJSONString(resultInfo.data));
				if (GoagalInfo.userInfo == null) {
					GoagalInfo.userInfo = new UserInfo();
				}
				// 保存用户信息
				saveUserInfo(resultInfo.data);
				
				loginResult.result = true;
			} else {
				loginResult.result = false;
				loginResult.message = resultInfo.message;
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
		String accountNumber = "";
		//手机号/账号合并
		if(GoagalInfo.loginType == 2){
			accountType = 0;
			
			if(!StringUtils.isEmpty(quickLoginInfo.mobile) && GoagalInfo.userInfo.username.equals(quickLoginInfo.mobile)){
				accountNumber = quickLoginInfo.mobile;
			}
			
			if(!StringUtils.isEmpty(quickLoginInfo.userName) && GoagalInfo.userInfo.username.equals(quickLoginInfo.userName)){
				accountNumber = quickLoginInfo.userName;
			}
		}
		
		GoagalInfo.userInfo.username = accountNumber;
		GoagalInfo.userInfo.mobile = quickLoginInfo.mobile;
		GoagalInfo.userInfo.password = quickLoginInfo.passWord;
		GoagalInfo.userInfo.userId = quickLoginInfo.userId;
		GoagalInfo.userInfo.logintime = quickLoginInfo.lastLoginTime;
		GoagalInfo.userInfo.sign = quickLoginInfo.sign;
		GoagalInfo.userInfo.validateMobile = quickLoginInfo.isValiMobile;
		GoagalInfo.userInfo.agentId = quickLoginInfo.agentId;
		PreferenceUtil.getImpl(context).putString(Constants.LAST_AGENT_ID, GoagalInfo.userInfo.agentId);
		
		GoagalInfo.isLogin = true;
		if(quickLoginInfo.gameNotice != null){
			GoagalInfo.noticeMsg = quickLoginInfo.gameNotice.body;
		}
		
		boolean isExist = UserLoginInfodao.getInstance(mContext).findUserLoginInfoByName(accountNumber);
		if (!isExist) {
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(accountNumber, quickLoginInfo.passWord,quickLoginInfo.isValiMobile,accountType);
		} else {
			// 先删除
			UserLoginInfodao.getInstance(mContext).deleteUserLoginByName(accountNumber);
			// 再保存最新的信息
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(accountNumber, quickLoginInfo.passWord,quickLoginInfo.isValiMobile,accountType);
		}
		
		if(GoagalInfo.loginType == 2){
			AccountInfoUtil.insertUserInfo(mContext, GoagalInfo.userInfo);
		}
		
	}
}
