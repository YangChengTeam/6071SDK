package com.game.sdk.view;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.EmulatorCheckUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ServiceDialog extends Dialog implements android.view.View.OnClickListener {

	private Activity mContext;

	private TextView callPhoneTv;

	private RelativeLayout callLayout;

	public float scale;

	private String[] kefu = null;

	private TextView firstQQTv;

	private TextView secondQQTv;

	public ServiceDialog(Activity context, float scale) {
		super(context, MResource.getIdByName(context, "style", "CustomSdkDialog"));
		this.mContext = context;
		this.scale = scale;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		kefu = kefuQQ(GoagalInfo.userInfo.kefuQQ);
		Logger.msg("kefuqq--->" + GoagalInfo.userInfo.kefuQQ);
		Drawable drawable = mContext.getResources()
				.getDrawable(MResource.getIdByName(mContext, "drawable", "no_qq_service_num_icon"));
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

		if (kefu == null) {
			firstQQTv.setCompoundDrawables(drawable, null, null, null);
			secondQQTv.setCompoundDrawables(drawable, null, null, null);
		} else {
			if (kefu.length == 0) {
				firstQQTv.setCompoundDrawables(drawable, null, null, null);
				secondQQTv.setCompoundDrawables(drawable, null, null, null);
			}

			if (kefu.length == 1) {
				secondQQTv.setCompoundDrawables(drawable, null, null, null);
			}
			
			if(!StringUtils.isEmpty(kefu[0]) && kefu[0].length() > 16){
				firstQQTv.setText("客服QQ群1");
			}
			
			if(!StringUtils.isEmpty(kefu[1]) && kefu[1].length() > 16){
				secondQQTv.setText("客服QQ群2");
			}
		}
	}

	public void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(MResource.getIdByName(mContext, "layout", "service_dialog"), null);

		callPhoneTv = (TextView) view.findViewById(MResource.getIdByName(mContext, "id", "call_phone_tv"));

		callLayout = (RelativeLayout) view.findViewById(MResource.getIdByName(mContext, "id", "call_layout"));

		firstQQTv = (TextView) view.findViewById(MResource.getIdByName(mContext, "id", "kefu_qq_num1_tv"));

		secondQQTv = (TextView) view.findViewById(MResource.getIdByName(mContext, "id", "kefu_qq_num2_tv"));

		setContentView(view);

		if (!StringUtils.isEmpty(GoagalInfo.inItInfo.tel)) {
			callPhoneTv.setText("客服电话：" + GoagalInfo.inItInfo.tel);
		} else {
			callPhoneTv.setText("客服电话：400-796-6071");
		}

		/*
		 * NoUnderlineSpan mNoUnderlineSpan = new NoUnderlineSpan(); if
		 * (callPhoneTv.getText() instanceof Spannable) { Spannable s =
		 * (Spannable) callPhoneTv.getText(); s.setSpan(mNoUnderlineSpan, 0,
		 * s.length(), Spanned.SPAN_MARK_MARK); }
		 */

		double ratio = scale;
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 0) {
			ratio = scale;
		}

		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 1) {
			ratio = 0.8;
		}

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		// params.width = DimensionUtil.dip2px(mContext, 320);
		params.width = (int) (DimensionUtil.getWidth(mContext) * ratio);
		// params.y = DimensionUtil.dip2px(mContext, 280);
		params.gravity = Gravity.CENTER;

		callPhoneTv.setOnClickListener(this);
		firstQQTv.setOnClickListener(this);
		secondQQTv.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == MResource.getIdByName(mContext, "id", "call_phone_tv")) {

			Logger.msg("isEmulator--->" + EmulatorCheckUtil.isEmulator());

			if (!EmulatorCheckUtil.isEmulator()) {
				if (SystemUtil.isValidContext(mContext) && GoagalInfo.inItInfo != null
						&& !StringUtils.isEmpty(GoagalInfo.inItInfo.tel)) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					Uri data = Uri.parse("tel:" + GoagalInfo.inItInfo.tel);
					intent.setData(data);
					mContext.startActivity(intent);
				}
			}
		}

		if (v.getId() == MResource.getIdByName(mContext, "id", "kefu_qq_num1_tv")) {
			if (kefu != null && kefu.length > 0) {
				startQQ(kefu[0]);
			}
		}
		if (v.getId() == MResource.getIdByName(mContext, "id", "kefu_qq_num2_tv")) {
			if (kefu != null && kefu.length > 1) {
				startQQ(kefu[1]);
			}
		}
	}

	public class NoUnderlineSpan extends UnderlineSpan {

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(Color.parseColor("#333333"));
			ds.setUnderlineText(false);
		}
	}

	public String[] kefuQQ(String qqNumString) {
		if (!StringUtils.isEmpty(qqNumString)) {
			return qqNumString.split(",");
		} else {
			return null;
		}
	}

	public void startQQ(String qqNum) {
		if (StringUtils.isEmpty(qqNum)) {
			return;
		}
		
		CheckUtil.setPackageNames(mContext);
		if (!CheckUtil.isQQAvilible(mContext)) {
			Util.toast(mContext, "请安装QQ");
		} else {
			// 加入QQ群
			if (qqNum.length() > 16) {
				joinQQGroup(qqNum);
			} else {
				String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum;
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		}
	}
	
	/****************
	 * 发起添加群流程。群号：XXX 的 key 为：xxxxx 调用 joinQQGroup(xxxx) 即可发起手Q客户端申请加群XXX
	 *
	 * @param key
	 *            由官网生成的key
	 * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	 ******************/
	public boolean joinQQGroup(String key) {
		Intent intent = new Intent();
		intent.setData(Uri
				.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D"
						+ key));
		// 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
		// //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		try {
			mContext.startActivity(intent);
			return true;
		} catch (Exception e) {
			// 未安装手Q或安装的版本不支持
			return false;
		}
	}
}
