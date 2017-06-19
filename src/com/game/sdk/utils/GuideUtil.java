package com.game.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GuideUtil {
	private Context context;
	private ImageView imgView;
	private WindowManager windowManager;
	private static GuideUtil instance = null;
	/** 是否第一次进入该程序 **/
	private boolean isFirst = true;
	private int i = 0;
	private int gWidth = 0;
	private int gHeight = 0;
	int[] imgs;
	private GuideUtil() {
	}

	public static GuideUtil getInstance() {
		synchronized (GuideUtil.class) {
			if (null == instance) {
				instance = new GuideUtil();
			}
		}
		return instance;
	}

	private Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// 设置LayoutParams参数
				final LayoutParams params = new WindowManager.LayoutParams();
				// 设置显示的类型，TYPE_PHONE指的是来电话的时候会被覆盖，其他时候会在最前端，显示位置在stateBar下面，其他更多的值请查阅文档
				params.type = WindowManager.LayoutParams.TYPE_TOAST;
				// 设置显示格式
				params.format = PixelFormat.RGBA_8888;
				// 设置对齐方式
				params.gravity = Gravity.CENTER;
				// 设置宽高
				params.width = gWidth;
				params.height = gHeight;
				// 设置动画
				//params.windowAnimations = R.style.view_anim;

				// 添加到当前的窗口上
				windowManager.addView(imgView, params);
				break;
			}
		};
	};

	public void initGuide(Activity context, final int[] imgs,int gwidth,int gheight) {
		if (!isFirst) {
			return;
		}
		this.context = context;
		this.imgs = imgs;
		this.gWidth = gwidth;
		this.gHeight = gheight;
		
		Logger.msg("w--->" + gWidth + "---h--->" + gHeight);
		
		windowManager = context.getWindowManager();

		// 动态初始化图层
		imgView = new ImageView(context);
		imgView.setLayoutParams(new LayoutParams(gWidth,gHeight));
		imgView.setScaleType(ScaleType.FIT_XY);
		imgView.setImageResource(imgs[0]);
		handler.sendEmptyMessage(1);
		
		// 点击图层之后，将图层移除
		imgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PreferenceUtil.getImpl(GuideUtil.this.context).putBoolean(Constants.IS_SHOW_GUIDE, true);
				i++;
				if (i > 1) {
					i = 0;
					windowManager.removeView(imgView);
				}else{					
					imgView.setImageResource(imgs[i]);
				}
			}
		});
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

}