package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.game.sdk.domain.GameCoin;
import com.game.sdk.domain.GameCoinList;
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
public class GameCoinEngin extends BaseEngin<GameCoin> {

	public Context mContext;

	private static GameCoinEngin gameCoinEngin;

	public GameCoinEngin(Context context) {
		super(context);
	}

	public static GameCoinEngin getImpl(Context context) {
		if (gameCoinEngin == null) {
			synchronized (MainModuleEngin.class) {
				gameCoinEngin = new GameCoinEngin(context);
			}
		}
		return gameCoinEngin;
	}

	@Override
	public String getUrl() {
		return ServerConfig.GAME_COIN_LIST_URL;
	}

	public static class ParamsInfo {
		public String userId;
	}

	public HashMap<String, String> getParams(ParamsInfo paramsInfo) {
		return JSON.parseObject(JSON.toJSONString(paramsInfo), new TypeReference<HashMap<String, String>>() {
		});
	}

	/**
	 * 获取主页面模块信息
	 * 
	 * @param page
	 * @param callback
	 */
	public void getGameCoinList(String userId, Callback<GameCoinList> callback) {
		ParamsInfo paramsInfo = new ParamsInfo();
		paramsInfo.userId = userId;
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", userId);
		this.agetResultInfo(true, params, callback);
	}

	public void agetResultInfo(boolean encodeResponse, Map<String, String> params,
			final Callback<GameCoinList> callback) {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		try {
			OKHttpRequest.getImpl().apost2(getUrl(), params, new OnHttpResonseListener() {
				@Override
				public void onSuccess(Response response) {
					ResultInfo<GameCoinList> resultInfo = null;
					try {
						resultInfo = JSON.parseObject(response.body, new TypeReference<ResultInfo<GameCoinList>>() {
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
