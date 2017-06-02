package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.AccountInfoUtil;
import com.game.sdk.utils.StringUtils;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class UpdatePassWordEngin extends BaseEngin<String> {

	public Context mContext;

	public String userName;

	public String oldPassWord;

	public String newPassWord;

	public int accountType = 0;

	public UpdatePassWordEngin(Context context, String userName, String oldPassWord, String newPassWord) {
		super(context);
		this.mContext = context;
		this.userName = userName;
		this.oldPassWord = oldPassWord;
		this.newPassWord = newPassWord;
	}

	@Override
	public String getUrl() {
		return ServerConfig.UPDATE_PASS_WORD_URL;
	}

	public boolean run() {
		boolean flag = true;
		ResultInfo<String> resultInfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("n", userName);
			params.put("old_pwd", oldPassWord);
			params.put("new_pwd", newPassWord);
			resultInfo = getResultInfo(true, String.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				flag = true;
				saveUserInfo(newPassWord);
			} else {
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	/**
	 * 保存用户信息
	 * 
	 * @param iusername
	 * @param ipassword
	 */
	public void saveUserInfo(String newPwd) {
		String accountName = !StringUtils.isEmpty(GoagalInfo.userInfo.username) ? GoagalInfo.userInfo.username : GoagalInfo.userInfo.mobile;
		
		String mobileNumber = !StringUtils.isEmpty(GoagalInfo.userInfo.mobile) ? GoagalInfo.userInfo.mobile : "";
				
		accountType = 0;
		
		//首先设置新密码
		if(GoagalInfo.userInfo != null){
			GoagalInfo.userInfo.password = newPwd;
		}
		
		//一，首先判断手机号是否存在，存在的话，首先修改与手机号对应的密码
		boolean isExist = UserLoginInfodao.getInstance(mContext).findUserLoginInfoByName(mobileNumber);
		if (!isExist) {
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(mobileNumber, GoagalInfo.userInfo.password,
					GoagalInfo.userInfo.validateMobile, accountType);
		} else {
			// 先删除
			UserLoginInfodao.getInstance(mContext).deleteUserLoginByName(mobileNumber);
			// 再保存最新的信息
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(mobileNumber, GoagalInfo.userInfo.password,
					GoagalInfo.userInfo.validateMobile, accountType);
		}
		
		//三，首先判断账号是否存在，存在的话，首先修改与账号对应的密码
		boolean isExistAccount = UserLoginInfodao.getInstance(mContext).findUserLoginInfoByName(accountName);
		if (!isExistAccount) {
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(accountName, GoagalInfo.userInfo.password,
					GoagalInfo.userInfo.validateMobile, accountType);
		} else {
			// 先删除
			UserLoginInfodao.getInstance(mContext).deleteUserLoginByName(accountName);
			// 再保存最新的信息
			UserLoginInfodao.getInstance(mContext).saveUserLoginInfo(accountName, GoagalInfo.userInfo.password,
					GoagalInfo.userInfo.validateMobile, accountType);
		}
		
		if (GoagalInfo.loginType == 2) {
			AccountInfoUtil.updateUsersInfo(mContext, GoagalInfo.userInfo);
		}
		
	}

}
