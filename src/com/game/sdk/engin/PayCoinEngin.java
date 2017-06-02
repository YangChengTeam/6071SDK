package com.game.sdk.engin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.CoinInfo;
import com.game.sdk.domain.CouponInfo;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.PreferenceUtil;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class PayCoinEngin extends BaseEngin<CoinInfo> {

	public Context mContext;

	public String userId;

	public PayCoinEngin(Context context, String userId) {
		super(context);
		this.mContext = context;
		this.userId = userId;
	}

	@Override
	public String getUrl() {
		return ServerConfig.PAY_INIT_URL;
	}

	public boolean run() {
		boolean result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", userId);
			ResultInfo<CoinInfo> resultInfo = getResultInfo(true, CoinInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("payinit获取结果----" + JSON.toJSONString(resultInfo.data));

				if (resultInfo.data != null) {
					GoagalInfo.userInfo.ttb = resultInfo.data.money;
					GoagalInfo.userInfo.gttb = resultInfo.data.gameMoney;
					if (resultInfo.data.couponList != null) {
						GoagalInfo.couponList = resultInfo.data.couponList;
						GoagalInfo.couponCount = resultInfo.data.couponCount;
						//saveCouponInfoList(getUrl(), resultInfo.data.couponList);
					}else{
						GoagalInfo.couponList = null;
					}
				}

				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * 存储优惠券信息到SP
	 * 
	 * @param key
	 * @param couponInfoList
	 */
	private void saveCouponInfoList(String key, List<CouponInfo> couponInfoList) {
		if (couponInfoList != null && couponInfoList.size() > 0) {
			String moduleStr = JSON.toJSONString(couponInfoList);
			Logger.msg("save--couponInfoList---" + couponInfoList);
			try {
				PreferenceUtil.getImpl(mContext).putString(key, moduleStr);
			} catch (Exception e) {
				Logger.msg(e.getMessage());
			}
		}
	}

}
