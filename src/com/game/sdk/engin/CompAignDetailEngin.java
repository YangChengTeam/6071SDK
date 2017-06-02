package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.game.sdk.domain.CompAignDetail;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

public class CompAignDetailEngin extends BaseEngin<CompAignDetail> {

	public Context mContext;

	public String aid;

	private static CompAignDetailEngin compAignEngin;

	public static CompAignDetailEngin getImpl(Context context) {
		if (compAignEngin == null) {
			synchronized (MainModuleEngin.class) {
				compAignEngin = new CompAignDetailEngin(context);
			}
		}
		return compAignEngin;
	}
	
	public CompAignDetailEngin(Context context) {
		super(context);
	}

	public CompAignDetailEngin(Context context, String aid) {
		super(context);
		this.mContext = context;
		this.aid = aid;
	}

	@Override
	public String getUrl() {
		return ServerConfig.COMPAIGN_DETAIL_URL;
	}
	
	public CompAignDetail run() {
		CompAignDetail compAignDetail = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("activity_id", aid);
			ResultInfo<CompAignDetail> resultInfo = getResultInfo(true, CompAignDetail.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				compAignDetail = resultInfo.data;
			}

		} catch (Exception e) {
			Logger.msg("CompAignDetailEngin---获取数据错误");
		}

		return compAignDetail;
	}

}
