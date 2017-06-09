package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.domain.UpdateInfo;
import com.game.sdk.domain.UpdateInfoResult;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.listeners.Callback;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class UpdateAvaterEngin extends BaseEngin<UpdateInfo> {

	public Context mContext;

	public String imgStr;

	private static UpdateAvaterEngin updateAvaterEngin;

	public static UpdateAvaterEngin getImpl(Context context) {
		if (updateAvaterEngin == null) {
			synchronized (MainModuleEngin.class) {
				updateAvaterEngin = new UpdateAvaterEngin(context);
			}
		}
		return updateAvaterEngin;
	}

	public UpdateAvaterEngin() {
	}

	public UpdateAvaterEngin(Context context) {
		super(context);
	}

	public UpdateAvaterEngin(Context context, String imgStr) {
		super(context);
		this.mContext = context;
		this.imgStr = imgStr;
	}

	@Override
	public String getUrl() {
		return ServerConfig.UPDATE_USER_INFO_URL;
	}

	/**
	 * 更新用户头像
	 * 
	 * @param updateUserInfo
	 * @return
	 */
	public UpdateInfoResult updateUserAvater() {
		
		final UpdateInfoResult updateInfoResult = new UpdateInfoResult();
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", GoagalInfo.userInfo.userId);
			params.put("face", imgStr);

			agetResultInfo(UpdateInfo.class, params, new Callback<UpdateInfo>() {

				@Override
				public void onSuccess(ResultInfo<UpdateInfo> resultInfo) {
					// Util.toast(mContext, "修改图像成功");
					if (resultInfo != null && resultInfo.data != null) {
						updateInfoResult.result = true;
						updateInfoResult.pointMessage = resultInfo.data != null && resultInfo.data.pointMessage != null ? resultInfo.data.pointMessage : "";
						GoagalInfo.userInfo.face = resultInfo.data.face;
					}
				}
				
				@Override
				public void onFailure(Response response) {
					// Util.toast(mContext, "修改图像失败");
					updateInfoResult.result = false;
				}
			});

		} catch (Exception e) {
			updateInfoResult.result = false;
		}

		return updateInfoResult;
	}

}
