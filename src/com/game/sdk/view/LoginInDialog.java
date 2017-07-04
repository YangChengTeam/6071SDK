package com.game.sdk.view;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginInDialog extends Dialog implements android.view.View.OnClickListener {
	private Context mContext;

	private TextView loginUserTv;

	private ImageView loadingIv;

	private String userName;

	private TextView changeAccountTv;

	public LoginInDialog(Context context, String userName) {
		super(context, MResource.getIdByName(context, "style", "CustomSdkDialog"));
		this.mContext = context;
		this.userName = userName;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		startAnimation();
	}

	public void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(MResource.getIdByName(mContext, "layout", "login_in_dialog"), null);
		loginUserTv = (TextView) view.findViewById(MResource.getIdByName(mContext, "id", "welcome_user"));
		loadingIv = (ImageView) view.findViewById(MResource.getIdByName(mContext, "id", "logining_icon"));
		changeAccountTv = (TextView) view.findViewById(MResource.getIdByName(mContext, "id", "change_account_tv"));

		setContentView(view);
		if (!StringUtils.isEmpty(userName)) {
			loginUserTv.setText(userName);
		}

		changeAccountTv.setOnClickListener(this);

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		params.width = (int) (DimensionUtil.getWidth(mContext) * 0.8);
		params.gravity = Gravity.TOP | Gravity.CENTER;
		params.y = DimensionUtil.dip2px(mContext, 30);

	}

	public void startAnimation() {
		if (loadingIv != null) {
			loadingIv.startAnimation(rotaAnimation());
		}
	}

	public void showDialog() {
		startAnimation();
		this.show();
	}

	/**
	 * 旋转动画
	 * 
	 * @return
	 */
	public Animation rotaAnimation() {
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setInterpolator(new LinearInterpolator());
		ra.setDuration(1500);
		ra.setRepeatCount(-1);
		ra.setStartOffset(0);
		ra.setRepeatMode(Animation.RESTART);
		return ra;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == MResource.getIdByName(mContext, "id", "change_account_tv")) {
			this.dismiss();
			GoagalInfo.isChangeAccount = true;
			Intent intent = new Intent(mContext, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}
	}
}
