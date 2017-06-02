package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.game.sdk.domain.ChargeRecordList;
import com.game.sdk.domain.GameCoin;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.DescConstans;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.impls.OKHttpRequest;
import com.game.sdk.net.listeners.Callback;
import com.game.sdk.net.listeners.OnHttpResonseListener;
import com.game.sdk.utils.Logger;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class ChargeRecordEngin extends BaseEngin<GameCoin> {

	public Context mContext;

	private static ChargeRecordEngin chargeRecordEngin;

	public ChargeRecordEngin(Context context) {
		super(context);
	}

	public static ChargeRecordEngin getImpl(Context context) {
		if (chargeRecordEngin == null) {
			synchronized (MainModuleEngin.class) {
				chargeRecordEngin = new ChargeRecordEngin(context);
			}
		}
		return chargeRecordEngin;
	}

	@Override
	public String getUrl() {
		return ServerConfig.CHARGE_LIST_URL;
	}

	public static class ParamsInfo {
		public String userId;
	}

	public HashMap<String, String> getParams(ParamsInfo paramsInfo) {
		return JSON.parseObject(JSON.toJSONString(paramsInfo), new TypeReference<HashMap<String, String>>() {
		});
	}
	
	public void getChargeRecordList(int isAllGame,int orderState,int page,String userId, String startTime,String endTime,Callback<ChargeRecordList> callback) {
		ParamsInfo paramsInfo = new ParamsInfo();
		paramsInfo.userId = userId;
		Map<String, String> params = new HashMap<String, String>();
		params.put("page", page+"");
		params.put("user_id", userId);
		params.put("start_time", startTime);
		params.put("end_time", endTime);
		
		params.put("is_all_game", isAllGame + "");//0：当前游戏订单列表 1：所有游戏订单列表 
		params.put("order_status", orderState + "");//订单状态: -1:全部, 0(待支付)，1(支付失败)，2(支付成功)，3(充值失败)，4(充值完成)
		
		this.agetResultInfo(true, params, callback);
	}

	public void agetResultInfo(boolean encodeResponse, Map<String, String> params,
			final Callback<ChargeRecordList> callback) {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		try {
			OKHttpRequest.getImpl().apost2(getUrl(), params, new OnHttpResonseListener() {
				@Override
				public void onSuccess(Response response) {
					ResultInfo<ChargeRecordList> resultInfo = null;
					try {
						resultInfo = JSON.parseObject(response.body, new TypeReference<ResultInfo<ChargeRecordList>>() {
						});

					} catch (Exception e) {
						response.body = DescConstans.SERVICE_ERROR;
						callback.onFailure(response);
						e.printStackTrace();
						Logger.msg("agetResultInfo异常->JSON解析错误（服务器返回数据格式不正确）");
					}

					if (callback != null) {
						if (resultInfo != null) {
							callback.onSuccess(resultInfo);
						} else {
							callback.onFailure(response);
						}
					}
				}

				@Override
				public void onFailure(Response response) {
					if (callback != null) {
						callback.onFailure(response);
					}
				}
			}, encodeResponse);
		} catch (Exception e) {
			Logger.msg("agetResultInfo异常->" + e.getMessage());
		}
	}

}
