package com.game.sdk.ui.fragment;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginResult;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.engin.QuickLoginEngin;
import com.game.sdk.engin.UnBindPhoneEngin;
import com.game.sdk.engin.UnBindSendCodeEngin;
import com.game.sdk.engin.ValidateEngin;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.ui.MainActivity;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.NetworkImpl;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class UnBindValidatePhoneFragment extends BaseFragment implements OnClickListener {

	private MainActivity mainActivity;

	private ImageView backIv;

	private TextView titleTv;
	
	private TextView phoneNumberEt;

	private EditText validateCodeEt;

	private Button getValidateBtn;
	
	private Button submitBtn;
	
	private CustomDialog sendDialog;

	private CustomDialog validateDialog;

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
		return "fysdk_unbind_validate_phone_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		mainActivity = (MainActivity) getActivity();
		sendDialog = new CustomDialog(mainActivity, "正在发送");
		validateDialog = new CustomDialog(mainActivity, "正在验证");
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		
		phoneNumberEt = findTextViewByString("phone_number_et");
		validateCodeEt = findEditTextByString("validate_code_et");
		
		getValidateBtn = findButtonByString("get_validate_btn");
		submitBtn = findButtonByString("submit_btn");
		titleTv.setText(findStringByResId("unbind_validate_phone_text"));
		
		phoneNumberEt.setText(GoagalInfo.userInfo != null ? GoagalInfo.userInfo.mobile :"");
		
		backIv.setOnClickListener(this);
		getValidateBtn.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
		
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}

	@Override
	public void onResume() {
		super.onResume();
		//MobclickAgent.onResume(mainActivity);
		MobclickAgent.onPageStart("UnBindValidatePhoneFragment");
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			mainActivity.changeFragment(1);
		}

		if (v.getId() == findIdByString("get_validate_btn")) {
			String phoneNumber = phoneNumberEt.getText().toString().trim();
			if (TextUtils.isEmpty(phoneNumber)) {
				Util.toast(mainActivity, "手机号为空，不能操作");
				return;
			}

			sendDialog.showDialog();
			new SendCodeTask(GoagalInfo.userInfo != null ? GoagalInfo.userInfo.username :"",phoneNumber).execute();
		}

		if (v.getId() == findIdByString("submit_btn")) {
			if (!NetworkImpl.isNetWorkConneted(mainActivity)) {
				Util.toast(mainActivity, "网络不给力，请检查网络设置");
				return;
			}

			String phoneNumber = phoneNumberEt.getText().toString().trim();
			String validateCode = validateCodeEt.getText().toString().trim();

			if (TextUtils.isEmpty(phoneNumber)) {
				Util.toast(mainActivity, "请输入手机号");
				return;
			}
			if (TextUtils.isEmpty(validateCode)) {
				Util.toast(mainActivity, "请输入验证码");
				return;
			}

			validateDialog.show();
			
			GoagalInfo.validateCode = validateCode;
			new UnBindPhoneTask(GoagalInfo.userInfo != null ? GoagalInfo.userInfo.username :"",phoneNumber,validateCode).execute();
		}
	}

	/**
	 * 获取短信验证码
	 * 
	 * @author admin
	 *
	 */
	private class SendCodeTask extends AsyncTask<String, Integer, ResultInfo<String>> {
		
		String userName;
		
		String mobileNumber;

		public SendCodeTask(String userName,String mobileNumber) {
			this.userName = userName;
			this.mobileNumber = mobileNumber;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ResultInfo<String> doInBackground(String... params) {
			UnBindSendCodeEngin unBindSendCodeEngin = new UnBindSendCodeEngin(mainActivity, userName,mobileNumber);
			return unBindSendCodeEngin.run();
		}

		@Override
		protected void onPostExecute(ResultInfo<String> resultInfo) {
			super.onPostExecute(resultInfo);
			sendDialog.dismiss();
			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {

				Util.toast(mainActivity, "验证码已发送");
				Logger.msg("验证码发送成功----");
				codeRefresh();
				GoagalInfo.isGetValidate = 1;
				// 清空验证码输入框
				validateCodeEt.setText("");
			} else {
				Util.toast(mainActivity, resultInfo.message);
				GoagalInfo.isGetValidate = 0;
			}
			if (GoagalInfo.isGetValidate == 1) {
				// 存储是否发送过验证码
				PreferenceUtil.getImpl(mainActivity).putBoolean(mobileNumber, true);
			}
		}
	}

	
	private int secondes = 60;

	/**
	 * 刷新验证码
	 */
	private void codeRefresh() {
		secondes = 60;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (secondes-- <= 0) {
					getValidateBtn.setEnabled(true);
					getValidateBtn.setText("获取验证码");
					getValidateBtn
							.setBackgroundResource(MResource.getIdByName(mainActivity, "drawable", "border_line_btn"));
					getValidateBtn.setTextColor(mainActivity.getResources()
							.getColor(MResource.getIdByName(mainActivity, "color", "border_line_color")));
					return;
				}
				getValidateBtn.setEnabled(false);
				getValidateBtn.setText("重新发送(" + secondes + ")");
				getValidateBtn
						.setBackgroundResource(MResource.getIdByName(mainActivity, "drawable", "border_line_gray"));
				getValidateBtn.setTextColor(mainActivity.getResources()
						.getColor(MResource.getIdByName(mainActivity, "color", "line_color")));
				handler.postDelayed(this, 1000);
			}
		};
		handler.postDelayed(runnable, 0);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//MobclickAgent.onPause(mainActivity);
		MobclickAgent.onPageEnd("UnBindValidatePhoneFragment");
	}
	
	
	/**
	 * 解绑手机号
	 * 
	 * @author admin
	 *
	 */
	private class UnBindPhoneTask extends AsyncTask<String, Integer, ResultInfo<String>> {
		
		String userName;
		
		String mobileNumber;

		String code;
		
		public UnBindPhoneTask(String userName,String mobileNumber,String code) {
			this.userName = userName;
			this.mobileNumber = mobileNumber;
			this.code = code;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ResultInfo<String> doInBackground(String... params) {
			UnBindPhoneEngin unBindPhoneEngin = new UnBindPhoneEngin(mainActivity,userName,mobileNumber,code);
			return unBindPhoneEngin.run();
		}

		@Override
		protected void onPostExecute(ResultInfo<String> resultInfo) {
			super.onPostExecute(resultInfo);
			validateDialog.dismiss();
			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				GoagalInfo.bindMobileFrom = 1;
				mainActivity.changeFragment(8);
			} else {
				Logger.msg("解绑失败----");
				Util.toast(mainActivity, "解绑失败，请稍后重试");
			}
		}
	}
}
