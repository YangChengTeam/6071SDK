package com.game.sdk.engin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.TTWAppService;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.InItInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by zhangkai on 16/9/20.
 */
public class InitEngin extends BaseEngin<InItInfo> {

	public Context mContext;
	
	public InitEngin(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	public String getUrl() {
		return ServerConfig.INIT_URL;
	}

	public boolean run() {
		Logger.msg("init run ---");
		boolean result = true;
		try {

			Map<String, String> params = new HashMap<String, String>();
			params.put("mt", SystemUtil.getOperator(mContext));
			params.put("user_id", GoagalInfo.userInfo != null ? GoagalInfo.userInfo.userId : "");
			ResultInfo<InItInfo> resultInfo = getResultInfo(true, InItInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				GoagalInfo.inItInfo = resultInfo.data;
				// GoagalInfo.inItInfo.publickey = GoagalInfo.publicKey;
				result = save(GoagalInfo.inItInfo);
				// saveResult(resultInfo);
				//GoagalInfo.inItInfo.vertical = 0;
			} else if (resultInfo != null && resultInfo.code == HttpConfig.PUBLIC_KEY_ERROR) {
				GoagalInfo.publicKey = resultInfo.data.publickey;
				run();
			} else {
				Logger.msg("init run other error and get init data from cache ---" + resultInfo.toString());

				/*
				 * GoagalInfo.inItInfo = get(); if(GoagalInfo.inItInfo != null){
				 * GoagalInfo.publicKey = GoagalInfo.inItInfo.publickey; }
				 */

				GoagalInfo.inItInfo = null;
				result = false;
			}
		} catch (Exception e) {
			Logger.msg("init catch error ---" + e.getMessage());
			GoagalInfo.inItInfo = null;
			result = false;
		}

		return result;
	}

	private void saveResult(ResultInfo<InItInfo> resultInfo) {
		TTWAppService.code = resultInfo.code;
		TTWAppService.tips = resultInfo.data.tip;
		TTWAppService.logo = resultInfo.data.logo;
		TTWAppService.channels = resultInfo.data.payway;
		// TTWAppService.debug = resultInfo.data.debug;
	}

	private boolean save(InItInfo inItInfo) {
		boolean flag = true;
		if (StringUtils.isEmpty(inItInfo.logo)) {
			return false;
		}

		if (inItInfo.floatInfo == null) {
			return false;
		} else {
			if (StringUtils.isEmpty(inItInfo.floatInfo.floatDrag)) {
				return false;
			}
			if (StringUtils.isEmpty(inItInfo.floatInfo.floatLeft)) {
				return false;
			}
			if (StringUtils.isEmpty(inItInfo.floatInfo.floatRight)) {
				return false;
			}
		}
		
		try {
			saveInitInfo(inItInfo);

			Picasso.with(mContext).load(inItInfo.logo).resize(DimensionUtil.dip2px(mContext, 120), DimensionUtil.dip2px(mContext, 30));
			
			if(!StringUtils.isEmpty(inItInfo.launchImg)){
				Picasso.with(mContext).load(inItInfo.launchImg);
			}
			
			Picasso.with(mContext).load(inItInfo.floatInfo.floatDrag);
			Picasso.with(mContext).load(inItInfo.floatInfo.floatLeft);
			Picasso.with(mContext).load(inItInfo.floatInfo.floatRight);
			
			if (inItInfo.template != null) {
				Picasso.with(mContext).load(inItInfo.template.regImage);
				Picasso.with(mContext).load(inItInfo.template.playImage);
			}

			getBitmap(inItInfo);
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	@SuppressWarnings("unused")
	private InItInfo get() {
		InItInfo inItInfo = getInitInfo();
		getBitmap(inItInfo);
		return inItInfo;
	}

	private void saveInitInfo(InItInfo inItInfo) {
		if (inItInfo != null) {
			String initInfoStr = JSON.toJSONString(inItInfo);
			try {
				PreferenceUtil.getImpl(this.context).putString(getUrl(), initInfoStr);
			} catch (Exception e) {
				Logger.msg(e.getMessage());
			}
		}
	}

	private InItInfo getInitInfo() {
		InItInfo inItInfo = null;
		try {
			String initInfoStr = PreferenceUtil.getImpl(this.context).getString(getUrl(), "");
			inItInfo = JSON.parseObject(initInfoStr, InItInfo.class);
		} catch (Exception e) {
			Logger.msg(e.getMessage());
		}
		return inItInfo;
	}

	private void getBitmap(InItInfo inItInfo) {
		if (inItInfo != null) {
			try {
				//顶部LOGO启动图
				inItInfo.logoBitmp = Picasso.with(mContext).load(inItInfo.logo).resize(DimensionUtil.dip2px(mContext, 120), DimensionUtil.dip2px(mContext, 30)).get();
				if(GoagalInfo.userInfo != null && inItInfo.logoBitmp != null) {
					Util.writeLaunchImageInSDCard(mContext, inItInfo.logoBitmp, Constants.LOGO_IMAGE);
				}else{
					if(inItInfo.logoBitmp != null){
						Util.writeLaunchImageInSDCard(mContext, inItInfo.logoBitmp, Constants.LOGO_IMAGE);
						Util.writeLaunchImageInSDCard(mContext, inItInfo.logoBitmp, Constants.AGENT_LOGO_IMAGE);
					}else{
						if(Util.getInitLogoFileBitmap(mContext, Constants.LOGO_IMAGE) != null){
							inItInfo.logoBitmp = Util.getInitLogoFileBitmap(mContext, Constants.LOGO_IMAGE);
						}
					}
				}
				
				//启动页
				if(!StringUtils.isEmpty(inItInfo.launchImg)){
					try{
						inItInfo.lunchBitmp = Picasso.with(mContext).load(inItInfo.launchImg).get();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
				if(GoagalInfo.userInfo != null && inItInfo.lunchBitmp != null) {
					Util.writeLaunchImageInSDCard(mContext, inItInfo.lunchBitmp, Constants.INIT_IMAGE);
				}else{
					if(inItInfo.lunchBitmp != null){
						Util.writeLaunchImageInSDCard(mContext, inItInfo.lunchBitmp, Constants.INIT_IMAGE);
						Util.writeLaunchImageInSDCard(mContext, inItInfo.lunchBitmp, Constants.AGENT_INIT_IMAGE);
					}else{
						if(Util.getInitLogoFileBitmap(mContext, Constants.INIT_IMAGE) != null){
							inItInfo.lunchBitmp = Util.getInitLogoFileBitmap(mContext, Constants.INIT_IMAGE);
						}
					}
				}
				
				inItInfo.floatBitmp = Picasso.with(mContext).load(inItInfo.floatInfo.floatDrag).get();

				if (inItInfo.template != null) {
					inItInfo.registerBitmp = Picasso.with(mContext).load(inItInfo.template.regImage).get();
					inItInfo.playBitmp = Picasso.with(mContext).load(inItInfo.template.playImage).get();
				}
				
				if (inItInfo.floatInfo != null) {
					Bitmap dragBitmap = Picasso.with(mContext).load(inItInfo.floatInfo.floatDrag).get();
					Bitmap dragLeftBitmap = Picasso.with(mContext).load(inItInfo.floatInfo.floatLeft).get();
					Bitmap dragRightBitmap = Picasso.with(mContext).load(inItInfo.floatInfo.floatRight).get();

					if (dragBitmap != null) {
						Util.writeImageInSDCard(mContext, dragBitmap, Constants.DRAG_IMAGE);
					}

					if (dragLeftBitmap != null) {
						Util.writeImageInSDCard(mContext, dragLeftBitmap, Constants.DRAG_LEFT_IMAGE);
					}

					if (dragRightBitmap != null) {
						Util.writeImageInSDCard(mContext, dragRightBitmap, Constants.DRAG_RIGHT_IMAGE);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			Logger.msg("lunch bitmap1 in cache ->" + inItInfo.logoBitmp);
			Logger.msg("lunch bitmap2 in cache ->" + inItInfo.lunchBitmp);
			Logger.msg("lunch bitmap3 in cache ->" + inItInfo.floatBitmp);
			Logger.msg("lunch bitmap4 in cache ->" + inItInfo.registerBitmp);
			Logger.msg("lunch bitmap5 in cache ->" + inItInfo.playBitmp);
		}
	}
}
