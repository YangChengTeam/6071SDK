package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.domain.RoleInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

/**
 * 解除绑定
 */
public class UserRoleEngin extends BaseEngin<RoleInfo> {

	public Context mContext;

	public RoleInfo mRoleInfo;
	
	public UserRoleEngin(Context context, RoleInfo roleInfo) {
		super(context);
		this.mContext = context;
		this.mRoleInfo = roleInfo;
	}

	@Override
	public String getUrl() {
		return ServerConfig.BIND_USER_ROLE;
	}

	public ResultInfo<RoleInfo> run() {
		ResultInfo<RoleInfo> resultInfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", mRoleInfo.getUserId());
			params.put("role_id", mRoleInfo.getRoleId());
			params.put("role_name", mRoleInfo.getRoleName());
			params.put("server_id", mRoleInfo.getRoleId());
			params.put("server_name", mRoleInfo.getServerName());
			params.put("role_level", mRoleInfo.getRoleLevel());
			params.put("union", mRoleInfo.getUnion());
			resultInfo = getResultInfo(true, RoleInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("设置用户的角色--->" + JSON.toJSONString(resultInfo.data));
			}

		} catch (Exception e) {
		}
		
		return resultInfo;
	}
	
}
