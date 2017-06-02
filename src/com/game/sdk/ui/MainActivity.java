package com.game.sdk.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

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
import com.game.sdk.ui.fragment.UpdatePassWordFragment;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity {

	private FragmentManager fm;

	private FragmentTransaction transaction;

	private BaseFragment currentFragment;
	
	public interface PayResultListener{
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
		defaultFragment();
		MainActivity.this.setFinishOnTouchOutside(true);
	}

	@Override
	public void initData() {
		super.initData();
		//MobclickAgent.openActivityDurationTrack(false);
	}

	public void setOrientation() {
		
		if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 0){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 1){
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
		//MobclickAgent.onPageStart("MainActivity");
		MobclickAgent.onResume(this);
		//setOrientation();
		
		//自定义事件,统计SDK主页面打开的次数
		MobclickAgent.onEvent(MainActivity.this,"fysdk_main_activity");
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
		//MobclickAgent.onPageEnd("MainActivity");
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		payResultListener.payResult(arg0, arg1, arg2);
		Logger.msg("mainActivity---onActivityResult--->");
	}
	
}
