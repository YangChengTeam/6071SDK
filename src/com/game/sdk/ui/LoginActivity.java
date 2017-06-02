package com.game.sdk.ui;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.ui.fragment.AccountLoginFragment;
import com.game.sdk.ui.fragment.BaseFragment;
import com.game.sdk.ui.fragment.QQServiceFragment;
import com.game.sdk.ui.fragment.QuickPlayFragment;
import com.game.sdk.ui.fragment.QuickRegisterFragment;
import com.game.sdk.ui.fragment.SetNewPassWordFragment;
import com.game.sdk.ui.fragment.ValidatePhoneFragment;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.MResource;
import com.umeng.analytics.MobclickAgent;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class LoginActivity extends BaseActivity {

	private FragmentManager fm;

	private FragmentTransaction transaction;

	private BaseFragment currentFragment;

	@Override
	public String getLayoutId() {
		return "fysdk_activity_login";
	}

	@Override
	public void initVars() {
		super.initVars();
		setOrientation();
	}

	@Override
	public void initViews() {
		super.initViews();
		GoagalInfo.loginType = 2;
		defaultFragment();
	}

	@Override
	public void initData() {
		super.initData();
		//MobclickAgent.openActivityDurationTrack(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//MobclickAgent.onPageStart("LoginActivity");
		MobclickAgent.onResume(this);
		LoginActivity.this.setFinishOnTouchOutside(false);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setOrientation();
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
		// params.width = DimensionUtil.dip2px(this, 420);
		params.width = (int) (DimensionUtil.getWidth(LoginActivity.this) * 0.9);
		params.gravity = Gravity.CENTER;
	}
	
	public void defaultFragment() {
		fm = getSupportFragmentManager();
		transaction = fm.beginTransaction();
		GoagalInfo.isQuick = 0;// 非快速登录
		
		//手机/账号合并为一种登录方式
		//if (GoagalInfo.loginType == 2) {
		currentFragment = new AccountLoginFragment();
		//}

		transaction.replace(MResource.getIdByName(this, "id", "login_content"), currentFragment);
		transaction.commit();
	}

	public void changeFragment(int type) {
		if (fm == null) {
			fm = getSupportFragmentManager();
		}

		transaction = fm.beginTransaction();
		switch (type) {
		/*case 1:
			currentFragment = new PhoneLoginFragment();
			break;*/
		case 2:
			currentFragment = new AccountLoginFragment();
			break;
		case 3:
			currentFragment = new QuickRegisterFragment();
			break;
		case 4:
			currentFragment = new QuickPlayFragment();
			break;
		case 5:
			currentFragment = new QQServiceFragment();
			break;
		case 6:
			currentFragment = new ValidatePhoneFragment();
			break;
		case 7:
			currentFragment = new SetNewPassWordFragment();
			break;
		default:
			break;
		}

		transaction.replace(MResource.getIdByName(this, "id", "login_content"), currentFragment);
		transaction.commit();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//MobclickAgent.onPageEnd("LoginActivity");
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
