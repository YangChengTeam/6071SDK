package com.game.sdk.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * author janecer 2014-7-23上午9:41:45
 */
public class DialogUtil {

	private static Dialog dialog ;// 显示对话框
	private static ImageView iv_pd;// 待旋转动画
	private static TextView tv_msg;// 消息
	private static View view;

	/**
	 * 显示对话框
	 */
	private static void init(Context context) {
		if(dialog == null) {
			dialog = new Dialog(context, MResource.getIdByName(context, "style",
				"CustomSdkDialog"));
		}
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DimensionUtil.getWidth(context) - 100, DimensionUtil.getHeight(context) - 80);
		//params.setMargins(40, 40, 40, 40);
		view = LayoutInflater.from(context).inflate(
				MResource.getIdByName(context, "layout", "ttw_custom_loading"), null);
		iv_pd = (ImageView) view.findViewById(MResource.getIdByName(context,
				"id", "iv_circle"));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		tv_msg = (TextView) view.findViewById(MResource.getIdByName(context,
				"id", "tv_msg"));
		dialog.setContentView(view,params);
	}
	
	public static Dialog getdialog(){
		return dialog;
	}
	/**
	 * 显示对话框
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showDialog(Context context, String msg) {
		init(context);
		tv_msg.setText(msg); // 显示进度信息
		try{
			DialogUtil.dismissDialog();
		}catch(Exception e){}
		if (null != dialog && !dialog.isShowing()) {
			iv_pd.startAnimation(rotaAnimation());
			try{
				dialog.show();
			}catch(Exception e){}
		}
	}

	public static void showDialog(Context ctx, boolean cansable, String msg) {
		init(ctx);
		tv_msg.setText(msg);// 显示进度信息
		if (null != dialog && !dialog.isShowing()) {
			iv_pd.startAnimation(rotaAnimation());
			try{
				dialog.setCancelable(cansable);
				dialog.show();
			}catch(Exception e){
				Logger.msg("Dialog show异常" + e.getMessage());
			}
		}
	}

	/**
	 * 隐藏对话框
	 */
	public static void dismissDialog() throws Exception {
		if (null != dialog && dialog.isShowing()) {
			try{
				dialog.dismiss();
				iv_pd.clearAnimation();
				dialog = null;
			}catch(Exception e){
				Logger.msg("Dialog dismiss异常" + e.getMessage());
			}	
		}
	}

	/**
	 * 旋转动画
	 * 
	 * @return
	 */
	public static Animation rotaAnimation() {
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setInterpolator(new LinearInterpolator());
		ra.setDuration(1500);
		ra.setRepeatCount(-1);
		ra.setStartOffset(0);
		ra.setRepeatMode(Animation.RESTART);
		return ra;
	}

	/**
	 * 判断对话框是否是显示状态
	 * 
	 * @return
	 */
	public static boolean isShowing() {
		if (null != dialog) {
			return dialog.isShowing();
		}
		return false;
	}

}
