package com.game.sdk.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.sdk.FYGameSDK;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.ui.MainActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 账号密码登录主界面
 * 
 * @author admin
 *
 */
public class AccountSafetyFragment extends BaseFragment implements OnClickListener {

	private MainActivity mainActivity;

	private ImageView backIv;

	private TextView titleTv;

	private TextView isBindTv;

	private TextView versionCodeTv;

	private RelativeLayout updatePassWordLayout;

	private RelativeLayout bindLayout;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				break;

			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "fysdk_account_safety_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		mainActivity = (MainActivity) getActivity();
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		titleTv.setText(findStringByResId("account_safety_text"));
		updatePassWordLayout = (RelativeLayout) findViewByString("update_ps_layout");
		bindLayout = (RelativeLayout) findViewByString("bind_layout");
		isBindTv = findTextViewByString("is_bind_tv");
		versionCodeTv = findTextViewByString("version_code_tv");
		backIv.setOnClickListener(this);
		updatePassWordLayout.setOnClickListener(this);
		bindLayout.setOnClickListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();
		versionCodeTv.setText(FYGameSDK.defaultSDK().getVersion() != null ? FYGameSDK.defaultSDK().getVersion() : "");
	}

	@Override
	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(mainActivity);
		MobclickAgent.onPageStart("AccountSafetyFragment");

		if (GoagalInfo.userInfo.validateMobile == 1) {
			isBindTv.setText(findStringByResId("is_bind_text"));
		} else {
			isBindTv.setText(findStringByResId("un_bind_text"));
		}
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			mainActivity.changeFragment(1);
		}
		if (v.getId() == findIdByString("update_ps_layout")) {
			mainActivity.changeFragment(7);
		}
		if (v.getId() == findIdByString("bind_layout")) {
			if (GoagalInfo.userInfo.validateMobile == 1) {
				mainActivity.changeFragment(12);
			} else {
				GoagalInfo.bindMobileFrom = 1;
				mainActivity.changeFragment(8);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(mainActivity);
		MobclickAgent.onPageEnd("AccountSafetyFragment");
	}
}
