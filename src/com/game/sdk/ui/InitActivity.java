package com.game.sdk.ui;

import java.io.File;
import java.io.IOException;

import com.game.sdk.FYGameSDK;
import com.game.sdk.FYGameSDK.InitCloseListener;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.HttpResponseCache;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

public class InitActivity extends BaseActivity implements InitCloseListener {

	private RelativeLayout initLayout;

	private Handler handler = new Handler();
	
	int i = 0;

	public FYGameSDK fyGmaeSDk;

	@Override
	public String getLayoutId() {
		return "fysdk_activity_init";
	}

	@Override
	public void initVars() {
		super.initVars();
		fyGmaeSDk = FYGameSDK.defaultSDK();
	};

	@SuppressLint("NewApi")
	@Override
	public void initViews() {
		super.initViews();
		initLayout = (RelativeLayout) findViewById(MResource.getIdByName(this, "id", "init_bg"));
		installCache(this);
		
		Bitmap initBitmap = Util.getInitLogoFileBitmap(this, Constants.INIT_IMAGE);

		if (initBitmap != null) {
			Drawable initDrawable = new BitmapDrawable(getResources(), initBitmap);
			initLayout.setBackground(initDrawable);
		} else {
			Configuration cf = getResources().getConfiguration();
			int ori = cf.orientation;
			
			if (ori == Configuration.ORIENTATION_LANDSCAPE) {
				initLayout.setBackgroundResource(MResource.getIdByName(this, "drawable", "launcher_h_default_icon"));
			} else if (ori == Configuration.ORIENTATION_PORTRAIT) {
				initLayout.setBackgroundResource(MResource.getIdByName(this, "drawable", "launcher_v_default_icon"));
			}
		}
	};

	/**
	 * 旋转动画
	 * 
	 * @return
	 */
	public Animation rotaAnimation() {
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setInterpolator(new LinearInterpolator());
		ra.setDuration(1200);
		ra.setRepeatCount(-1);
		ra.setStartOffset(0);
		ra.setRepeatMode(Animation.RESTART);
		return ra;
	}

	@Override
	public void initData() {

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				fyGmaeSDk.init();
			}
		}, 800);

		fyGmaeSDk.setCloseListener(this);
	}

	@SuppressLint("NewApi")
	public void installCache(Context context) {
		try {
			File httpCacheDir = new File(context.getCacheDir(), "http");
			long httpCacheSize = 100 * 1024 * 1024; // 100 MiB
			HttpResponseCache.install(httpCacheDir, httpCacheSize);
		} catch (IOException e) {

			Util.toast(this, "HTTP response cache installation failed:" + e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("InitActivity");
		MobclickAgent.onResume(this);

		/*
		 * if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 0)
		 * { setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 * } if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical ==
		 * 1) {
		 * setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); }
		 */
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("InitActivity");
		MobclickAgent.onPause(this);
	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void initClose() {
		if (SystemUtil.isValidContext(InitActivity.this)) {
			this.finish();
		}
	}
}
