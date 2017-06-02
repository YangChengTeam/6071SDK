package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.game.sdk.FYGameSDK;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ModuleList;
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
public class MainModuleEngin extends BaseEngin<String> {

	public Context mContext;

	private static MainModuleEngin mainModuleEngin;

	public MainModuleEngin(Context context) {
		super(context);
	}

	public static MainModuleEngin getImpl(Context context) {
		if (mainModuleEngin == null) {
			synchronized (MainModuleEngin.class) {
				mainModuleEngin = new MainModuleEngin(context);
			}
		}
		return mainModuleEngin;
	}

	@Override
	public String getUrl() {
		return ServerConfig.MAIN_MODULE_URL;
	}

	public static class ParamsInfo {
		public int page = 1;
		public String user_id = "";
		public String version = "";
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
	public void getModuleInfoList(int page, Callback<ModuleList> callback) {
		ParamsInfo paramsInfo = new ParamsInfo();

		paramsInfo.page = page;
		paramsInfo.user_id = GoagalInfo.userInfo.userId != null ? GoagalInfo.userInfo.userId : "";
		paramsInfo.version = FYGameSDK.defaultSDK().getVersion() != null ? FYGameSDK.defaultSDK().getVersion() : "";//增加参数-->sdk当前版本号
		Map<String, String> params = getParams(paramsInfo);
		this.agetResultInfo(true, params, callback);
	}
	
	public void agetResultInfo(boolean encodeResponse, Map<String, String> params,
			final Callback<ModuleList> callback) {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		try {
			OKHttpRequest.getImpl().apost2(getUrl(), params, new OnHttpResonseListener() {
				@Override
				public void onSuccess(Response response) {
					ResultInfo<ModuleList> resultInfo = null;
					try {
						resultInfo = JSON.parseObject(response.body, new TypeReference<ResultInfo<ModuleList>>() {
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
