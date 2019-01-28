package com.game.sdk.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.sdk.utils.Logger;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by zhangkai on 16/9/20.
 */
public class InItInfo {
	
	@JSONField(name = "color")
	public String themeColor;

	@JSONField(name = "name")
	public String title;

	@JSONField(name = "login_logo")
	public String logo;

	@JSONField(name = "launch_img")
	public String launchImg;
	
	public String tel;

	public String qq;

	@JSONField(name = "qq_qun")
	public String qqQun;

	public String debug;

	@JSONField(name = "bug_url")
	public String bugUrl;

	@JSONField(name = "mt_code")
	public String mtCode;

	@JSONField(name = "mqr_num_limit")
	public String mqrNumLimit;

	public String style;

	@JSONField(name = "is_mqr")
	public int isMqr;
	
	@JSONField(name = "version_upd")
	public VersionInfo versionInfo;
	
	@JSONField(name = "mqr_delay")
	public String mqrDelay;
	
	public int color;

	public Bitmap logoBitmp;

	public Bitmap lunchBitmp;

	public Bitmap floatBitmp;

	public Bitmap registerBitmp;

	public Bitmap playBitmp;

	public List<PayWay> payway;

	public String tip;

	public String publickey;

	@JSONField(name = "template")
	public StyleInfo template;
	
	@JSONField(name = "box_info")
	public BoxInfo boxInfo;
	
	public LoginOutInfo logout;// 退出时包含的信息

	public String weixin;// 微信客服

	public int vertical;// 0横 1竖

	@JSONField(name = "floatico_info")
	public FloatInfo floatInfo;//浮动图标信息
	
	@JSONField(name = "share_content")
	public String shareContent;//分享内容
	
	@JSONField(name = "is_speed_up")
	public int isSpeedUp;//(1:加速，0：不加速) -->现在变更字段意思为 ：0(默认)：有切换账号功能，1：无切换账号功能
	
	@JSONField(name = "is_auto_click")
	public int isAutoClick;//(1：自动点击，0：不自动点击)
	
	@JSONField(name = "game_kefu_qq")
	public String gameKefuQQ;//客服QQ号码组
	
	@JSONField(name = "sms_mobile_list")
	public String smsMobileList;//后台接受短信的号码组
	
	public int isPostToToutiaoSdk;//1：提交，0：不提交
	
	public void setThemeColor() {
		try {
			color = Color.parseColor(GoagalInfo.inItInfo.themeColor);
		} catch (Exception e) {
			color = Color.parseColor("#2AB1F2");
			themeColor = "#2AB1F2";
			Logger.msg("初始化信息有误->" + GoagalInfo.inItInfo.themeColor);
		}
	}

}
