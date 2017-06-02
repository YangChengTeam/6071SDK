package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class UpdateMtCodeEngin extends BaseEngin<String> {

	public Context mContext;

	public String mtype;
	
	public String mtCode;

	public UpdateMtCodeEngin(Context context, String mtype,String mtCode) {
		super(context);
		this.mContext = context;
		this.mtype = mtype;
		this.mtCode = mtCode;
	}
	
	@Override
	public String getUrl() {
		return ServerConfig.UPDATE_MT_CODE_URL;
	}

	public boolean run() {
		boolean flag = true;
		ResultInfo<String> resultInfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("mt", mtype);
			params.put("mt_code", mtCode);
			resultInfo = getResultInfo(true, String.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

}
