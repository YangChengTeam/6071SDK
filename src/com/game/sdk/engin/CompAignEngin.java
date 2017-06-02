package com.game.sdk.engin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.sdk.domain.CompAign;
import com.game.sdk.domain.CompAignList;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

public class CompAignEngin extends BaseEngin<CompAignList> {

	public Context mContext;

	public String userId;

	private static CompAignEngin compAignEngin;
	
	public static CompAignEngin getImpl(Context context) {
		if (compAignEngin == null) {
			synchronized (MainModuleEngin.class) {
				compAignEngin = new CompAignEngin(context);
			}
		}
		return compAignEngin;
	}

	public CompAignEngin(Context context) {
		super(context);
	}

	public CompAignEngin(Context context, String userId) {
		super(context);
		this.mContext = context;
		this.userId = userId;
	}

	@Override
	public String getUrl() {
		return ServerConfig.COMPAIGN_LIST_URL;
	}

	public List<CompAign> run(int page) {
		List<CompAign> list = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", userId);
			params.put("page", page+"");
			ResultInfo<CompAignList> resultInfo = getResultInfo(true, CompAignList.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				list = resultInfo.data.list;
			}

		} catch (Exception e) {
			Logger.msg("CompAignEngin---获取数据错误");
		}

		return list;
	}

}
