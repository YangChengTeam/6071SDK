package com.game.sdk.ui.fragment;

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.BindPhoneResult;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginResult;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.engin.BindPhoneEngin;
import com.game.sdk.engin.QuickLoginEngin;
import com.game.sdk.engin.ValidateEngin;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.ui.LoginActivity;
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

public class ValidatePhoneFragment extends BaseFragment implements OnClickListener {

	private LoginActivity loginActivity;

	private ImageView backIv;

	private TextView titleTv;
	
	private EditText phoneNumberEt;

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
		return "fysdk_validate_phone_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		loginActivity = (LoginActivity) getActivity();
		sendDialog = new CustomDialog(loginActivity, "正在发送");
		validateDialog = new CustomDialog(loginActivity, "正在验证");
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		
		phoneNumberEt = findEditTextByString("phone_number_et");
		validateCodeEt = findEditTextByString("validate_code_et");
		
		getValidateBtn = findButtonByString("get_validate_btn");
		submitBtn = findButtonByString("submit_btn");
		titleTv.setText(findStringByResId("validate_phone_text"));
		
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
		//MobclickAgent.onResume(loginActivity);
		MobclickAgent.onPageStart("ValidatePhoneFragment");
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			loginActivity.changeFragment(2);
		}

		if (v.getId() == findIdByString("get_validate_btn")) {
			String phoneNumber = phoneNumberEt.getText().toString().trim();
			if (TextUtils.isEmpty(phoneNumber)) {
				Util.toast(loginActivity, "请输入手机号");
				return;
			}

			sendDialog.showDialog();
			new ValidateCodeTask(phoneNumber).execute();
		}

		if (v.getId() == findIdByString("submit_btn")) {
			if (!NetworkImpl.isNetWorkConneted(loginActivity)) {
				Util.toast(loginActivity, "网络不给力，请检查网络设置");
				return;
			}

			String phoneNumber = phoneNumberEt.getText().toString().trim();
			String validateCode = validateCodeEt.getText().toString().trim();

			if (TextUtils.isEmpty(phoneNumber)) {
				Util.toast(loginActivity, "请输入手机号");
				return;
			}
			if (TextUtils.isEmpty(validateCode)) {
				Util.toast(loginActivity, "请输入验证码");
				return;
			}

			validateDialog.show();
			//new BindPhoneTask(phoneNumber, GoagalInfo.userInfo.username, validateCode).execute();
			
			GoagalInfo.validateCode = validateCode;
			new PhoneValidateTask(phoneNumber).execute();
		}
	}

	/**
	 * 获取短信验证码
	 * 
	 * @author admin
	 *
	 */
	private class ValidateCodeTask extends AsyncTask<String, Integer, ResultInfo<String>> {

		String mobileNumber;

		public ValidateCodeTask(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ResultInfo<String> doInBackground(String... params) {
			ValidateEngin validateEngin = new ValidateEngin(loginActivity, mobileNumber);
			return validateEngin.run();
		}

		@Override
		protected void onPostExecute(ResultInfo<String> resultInfo) {
			super.onPostExecute(resultInfo);
			sendDialog.dismiss();
			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {

				Util.toast(loginActivity, "验证码已发送");
				Logger.msg("验证码发送成功----");
				codeRefresh();
				GoagalInfo.isGetValidate = 1;
				// 清空验证码输入框
				validateCodeEt.setText("");
			} else if (resultInfo != null && resultInfo.code == HttpConfig.VALIDATE_CODE_IS_SEND) {
				Util.toast(loginActivity, resultInfo.message);
				GoagalInfo.isGetValidate = 1;
				// 清空验证码输入框
				validateCodeEt.setText("");
			} else {
				Util.toast(loginActivity, resultInfo.message);
				GoagalInfo.isGetValidate = 0;
			}
			if (GoagalInfo.isGetValidate == 1) {
				// 存储是否发送过验证码
				PreferenceUtil.getImpl(loginActivity).putBoolean(mobileNumber, true);
			}
		}
	}

	/**
	 * 绑定手机
	 */
	private class BindPhoneTask extends AsyncTask<String, Integer, BindPhoneResult> {
		String phoneNumber;
		String userName;
		String code;

		public BindPhoneTask(String phoneNumber, String userName, String code) {
			this.phoneNumber = phoneNumber;
			this.userName = userName;
			this.code = code;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected BindPhoneResult doInBackground(String... params) {
			BindPhoneEngin bindPhoneEngin = new BindPhoneEngin(loginActivity, phoneNumber, userName, code);
			return bindPhoneEngin.run();
		}

		@Override
		protected void onPostExecute(BindPhoneResult bindPhoneResult) {
			super.onPostExecute(bindPhoneResult);
			
			if(validateDialog != null && validateDialog.isShowing()){
				validateDialog.dismiss();
			}
			
			if (bindPhoneResult.result) {
				GoagalInfo.userInfo.validateMobile = 1;
				//Util.toast(loginActivity, "绑定手机号成功");
				
				//存储账号，若手机号为空，则存储用户名
				String accountNumber = GoagalInfo.userInfo.username;
				
				boolean isExist = UserLoginInfodao.getInstance(loginActivity).findUserLoginInfoByName(accountNumber);
				if (!isExist) {
					UserLoginInfodao.getInstance(loginActivity).saveUserLoginInfo(phoneNumber, GoagalInfo.userInfo.password,GoagalInfo.userInfo.validateMobile,0);
				} else {
					// 先删除
					UserLoginInfodao.getInstance(loginActivity).deleteUserLoginByName(accountNumber);
					// 再保存最新的信息
					UserLoginInfodao.getInstance(loginActivity).saveUserLoginInfo(phoneNumber, GoagalInfo.userInfo.password,GoagalInfo.userInfo.validateMobile,0);
				}
				
				loginActivity.changeFragment(7);
			} else {
				Util.toast(loginActivity, StringUtils.isEmpty(bindPhoneResult.message) ? "验证手机号失败，请重试":bindPhoneResult.message);
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
							.setBackgroundResource(MResource.getIdByName(loginActivity, "drawable", "border_line_btn"));
					getValidateBtn.setTextColor(loginActivity.getResources()
							.getColor(MResource.getIdByName(loginActivity, "color", "border_line_color")));
					return;
				}
				getValidateBtn.setEnabled(false);
				getValidateBtn.setText("重新发送(" + secondes + ")");
				getValidateBtn
						.setBackgroundResource(MResource.getIdByName(loginActivity, "drawable", "border_line_gray"));
				getValidateBtn.setTextColor(loginActivity.getResources()
						.getColor(MResource.getIdByName(loginActivity, "color", "line_color")));
				handler.postDelayed(this, 1000);
			}
		};
		handler.postDelayed(runnable, 0);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//MobclickAgent.onPause(loginActivity);
		MobclickAgent.onPageEnd("ValidatePhoneFragment");
	}
	
	
	/**
	 * (手机号-验证码方式)进入游戏登录
	 * 
	 * @author admin
	 *
	 */
	private class PhoneValidateTask extends AsyncTask<String, Integer, LoginResult> {

		String mobileNumber;

		public PhoneValidateTask(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected LoginResult doInBackground(String... params) {
			QuickLoginEngin quickLoginEngin = new QuickLoginEngin(loginActivity, false, mobileNumber);
			return quickLoginEngin.run();
		}

		@Override
		protected void onPostExecute(LoginResult loginResult) {
			super.onPostExecute(loginResult);
			validateDialog.dismiss();
			if (loginResult.result) {
				loginActivity.changeFragment(7);
			} else {
				Logger.msg("手机号-验证失败----");
				Util.toast(loginActivity, StringUtils.isEmpty(loginResult.message) ? "手机号验证失败" : loginResult.message);
			}
		}
	}
}
