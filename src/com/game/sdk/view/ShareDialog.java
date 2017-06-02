package com.game.sdk.view;

import java.util.ArrayList;
import java.util.List;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.ShareUtil;
import com.game.sdk.utils.StringUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ShareDialog extends Dialog implements android.view.View.OnClickListener {

	private Context mContext;

	private RelativeLayout wechatLayout;

	private RelativeLayout friendsLayout;

	private RelativeLayout qqLayout;

	private RelativeLayout weiboLayout;

	private LinearLayout cancelLayout;

	public ShareDialog(Context context) {
		super(context, MResource.getIdByName(context, "style", "CustomSdkDialog"));
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	public void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(MResource.getIdByName(mContext, "layout", "share_dialog"), null);
		wechatLayout = (RelativeLayout) view.findViewById(MResource.getIdByName(mContext, "id", "wechat_layout"));
		friendsLayout = (RelativeLayout) view.findViewById(MResource.getIdByName(mContext, "id", "friends_layout"));
		qqLayout = (RelativeLayout) view.findViewById(MResource.getIdByName(mContext, "id", "qq_layout"));
		weiboLayout = (RelativeLayout) view.findViewById(MResource.getIdByName(mContext, "id", "weibo_layout"));
		cancelLayout = (LinearLayout) view.findViewById(MResource.getIdByName(mContext, "id", "cancel_layout"));
		setContentView(view);

		double ratio = 0.6;
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 0) {
			ratio = 0.6;
		}

		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 1) {
			ratio = 0.9;
		}

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		// params.width = DimensionUtil.dip2px(mContext, 320);
		params.width = (int) (DimensionUtil.getWidth(mContext) * ratio);
		// params.y = DimensionUtil.dip2px(mContext, 280);
		params.gravity = Gravity.CENTER;

		wechatLayout.setOnClickListener(this);
		friendsLayout.setOnClickListener(this);
		qqLayout.setOnClickListener(this);
		weiboLayout.setOnClickListener(this);
		cancelLayout.setOnClickListener(this);
	}

	List<String> imgs = new ArrayList<String>();

	@Override
	public void onClick(View v) {
		
		String shareContent = !StringUtils.isBlank(GoagalInfo.userInfo.shareContent) ? GoagalInfo.userInfo.shareContent : "游戏SDK分享";
		
		if (v.getId() == MResource.getIdByName(mContext, "id", "wechat_layout")) {
			CheckUtil.setPackageNames(mContext);

			/*
			 * imgs.add("http://www.qqtn.com/skin/new2013/images/logo.png");
			 * imgs.add("http://www.qqtn.com/file/2013/2015-3/2015324994.png");
			 * imgs.add("http://www.qqtn.com/file/2011/2011-4/20114199556.jpg");
			 * 
			 * ShareUtil.openWXShareWithImage(mContext, "游戏SDK分享", imgs, 0);
			 */

			ShareUtil.OpenWxShareText(mContext, shareContent);

		}
		if (v.getId() == MResource.getIdByName(mContext, "id", "friends_layout")) {
			CheckUtil.setPackageNames(mContext);
			ShareUtil.openWXShareWithImage(mContext, shareContent);
		}
		if (v.getId() == MResource.getIdByName(mContext, "id", "qq_layout")) {
			CheckUtil.setPackageNames(mContext);
			ShareUtil.openQQShareWithText(mContext, shareContent);
		}
		if (v.getId() == MResource.getIdByName(mContext, "id", "weibo_layout")) {
			CheckUtil.setPackageNames(mContext);
			ShareUtil.openWeiboShareWithImage(mContext, shareContent);
		}

		if (v.getId() == MResource.getIdByName(mContext, "id", "cancel_layout")) {
			this.dismiss();
		}
	}

}
