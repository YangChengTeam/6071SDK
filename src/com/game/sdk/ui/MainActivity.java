package com.game.sdk.ui;

import com.game.sdk.FYGameSDK;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.ui.fragment.AccountSafetyFragment;
import com.game.sdk.ui.fragment.BaseFragment;
import com.game.sdk.ui.fragment.BindPhoneFragment;
import com.game.sdk.ui.fragment.ChargeRecordFragment;
import com.game.sdk.ui.fragment.CompAignDetailFragment;
import com.game.sdk.ui.fragment.CompAignFragment;
import com.game.sdk.ui.fragment.GameCoinListFragment;
import com.game.sdk.ui.fragment.GamePackageFragment;
import com.game.sdk.ui.fragment.MainFragment;
import com.game.sdk.ui.fragment.QuickPlayFragment;
import com.game.sdk.ui.fragment.QuickRegisterFragment;
import com.game.sdk.ui.fragment.ScoreStoreFragment;
import com.game.sdk.ui.fragment.UnBindValidatePhoneFragment;
import com.game.sdk.ui.fragment.UpdatePassWordFragment;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.umeng.analytics.MobclickAgent;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends BaseActivity {

	private FragmentManager fm;

	private FragmentTransaction transaction;

	private BaseFragment currentFragment;

	public interface PayResultListener {
		void payResult(int arg0, int arg1, Intent arg2);
	}

	private PayResultListener payResultListener;

	public PayResultListener getPayResultListener() {
		return payResultListener;
	}

	public void setPayResultListener(PayResultListener payResultListener) {
		this.payResultListener = payResultListener;
	}

	@Override
	public String getLayoutId() {
		return "fysdk_activity_main";
	}

	@Override
	public void initVars() {
		super.initVars();

		setOrientation();
	}

	@Override
	public void initViews() {
		super.initViews();
		hideBottomUIMenu();
		defaultFragment();
		MainActivity.this.setFinishOnTouchOutside(true);
	}

	/**  
	 * 隐藏虚拟按键，并且设置成全屏  
	 */  
	@SuppressLint("InlinedApi")
	protected void hideBottomUIMenu(){  
	    if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api  
	        View v = this.getWindow().getDecorView();  
	        v.setSystemUiVisibility(View.GONE);  
	    } else if (Build.VERSION.SDK_INT >= 19) {
	        //for new api versions.  
	        View decorView = getWindow().getDecorView();
	        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar  
	                  | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar  
	                | View.SYSTEM_UI_FLAG_IMMERSIVE;  
	        decorView.setSystemUiVisibility(uiOptions);  
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }  
	}  
	
	@Override
	public void initData() {
		super.initData();
		// MobclickAgent.openActivityDurationTrack(false);

		// 自定义事件,统计SDK主页面打开的次数
		MobclickAgent.onEvent(MainActivity.this, "fysdk_main_activity",
				FYGameSDK.defaultSDK().getVersion() != null ? FYGameSDK.defaultSDK().getVersion() : "");
	}

	public void setOrientation() {
		
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 0) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 1) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		params.width = (int) (DimensionUtil.getWidth(MainActivity.this) * 0.9);
		params.gravity = Gravity.CENTER;
		dialogWindow.setAttributes(params);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// MobclickAgent.onPageStart("MainActivity");
		MobclickAgent.onResume(this);
		// setOrientation();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setOrientation();
	}

	public void defaultFragment() {
		fm = getSupportFragmentManager();
		transaction = fm.beginTransaction();
		currentFragment = new MainFragment();
		transaction.replace(MResource.getIdByName(this, "id", "main_content"), currentFragment);
		transaction.commit();
	}

	public void changeFragment(int type) {
		if (fm == null) {
			fm = getSupportFragmentManager();
		}

		transaction = fm.beginTransaction();
		switch (type) {
		case 1:
			currentFragment = new MainFragment();
			break;
		case 2:
			currentFragment = new CompAignFragment();
			break;
		case 3:
			currentFragment = new QuickRegisterFragment();
			break;
		case 4:
			currentFragment = new QuickPlayFragment();
			break;
		case 5:
			currentFragment = new GameCoinListFragment();
			break;
		case 6:
			currentFragment = new AccountSafetyFragment();
			break;
		case 7:
			currentFragment = new UpdatePassWordFragment();
			break;
		case 8:
			currentFragment = new BindPhoneFragment();
			break;
		case 9:
			currentFragment = new ChargeRecordFragment();
			break;
		case 10:
			currentFragment = new ScoreStoreFragment();
			break;
		case 11:
			currentFragment = new GamePackageFragment();
			Bundle bundle = new Bundle();
			bundle.putString("gameId", GoagalInfo.gameid);
			currentFragment.setArguments(bundle);
			break;
		case 12:
			currentFragment = new UnBindValidatePhoneFragment();
			break;
		default:
			break;
		}

		transaction.replace(MResource.getIdByName(this, "id", "main_content"), currentFragment);
		transaction.commit();
	}

	public void detailFragment(String aid, int actionType) {
		if (fm == null) {
			fm = getSupportFragmentManager();
		}

		transaction = fm.beginTransaction();

		Bundle bundle = new Bundle();
		bundle.putString("aid", aid);
		bundle.putInt("type", actionType);
		currentFragment = new CompAignDetailFragment();
		currentFragment.setArguments(bundle);
		transaction.replace(MResource.getIdByName(this, "id", "main_content"), currentFragment);
		transaction.commit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd("MainActivity");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		payResultListener.payResult(arg0, arg1, arg2);
		Logger.msg("mainActivity---onActivityResult--->");
	}

}
