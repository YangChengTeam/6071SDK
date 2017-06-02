package com.game.sdk.view;

import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.MResource;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDialog extends Dialog {
	private Context mContext;

	private TextView messageTv;
	
	private ImageView loadingIv;
	
	private String message;
	
	public CustomDialog(Context context,String msg) {
		super(context, MResource.getIdByName(context, "style", "CustomSdkDialog"));
		this.mContext = context;
		this.message = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		startAnimation();
	}

	public void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(MResource.getIdByName(mContext, "layout", "custom_dialog"), null);

		messageTv = (TextView) view.findViewById(MResource.getIdByName(mContext, "id", "tv_msg"));
		loadingIv = (ImageView) view.findViewById(MResource.getIdByName(mContext, "id", "loading_icon"));
		setContentView(view);
		
		messageTv.setText(message);
		
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		params.width = (int) (DimensionUtil.getWidth(mContext) * 0.8);

	}

	public void startAnimation() {
		if (loadingIv != null) {
			loadingIv.startAnimation(rotaAnimation());
		}
	}
	
	public void showDialog(){
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

}
