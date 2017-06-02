package com.game.sdk.ui;

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.BindPhoneResult;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.engin.BindPhoneEngin;
import com.game.sdk.engin.BindPhoneValidateEngin;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.NetworkImpl;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
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

public class UserInfoPhoneUpdateActivity extends BaseActivity implements OnClickListener {

	private TextView titleTv;

	private ImageView backIv;

	private EditText phoneNumberEt;

	private EditText validateCodeEt;

	private Button getValidateBtn;

	private Button updateBtn;

	private CustomDialog sendDialog;

	private CustomDialog bindDialog;

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
		return "fysdk_activity_update_phone";
	}

	@Override
	public void initVars() {
		super.initVars();
		titleTv = findTextViewByString("title_tv");
		backIv = findImageViewByString("back_iv");

		if (GoagalInfo.userInfo.validateMobile == 1) {
			titleTv.setText(findStringByResId("again_bind_text"));
		} else {
			titleTv.setText(findStringByResId("bind_text"));
		}

		phoneNumberEt = findEditTextByString("phone_number_et");
		validateCodeEt = findEditTextByString("validate_et");
		getValidateBtn = findButtonByString("get_validate_btn");
		updateBtn = findButtonByString("update_btn");

		backIv.setOnClickListener(this);
		getValidateBtn.setOnClickListener(this);
		updateBtn.setOnClickListener(this);
	}

	@Override
	public void initViews() {
		super.initViews();
		sendDialog = new CustomDialog(this, "正在发送");
		bindDialog = new CustomDialog(this, "正在加载");
	}

	@Override
	public void initData() {
		super.initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("UserInfoPhoneUpdateActivity");
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			finish();
		}

		if (v.getId() == findIdByString("get_validate_btn")) {
			String phoneNumber = phoneNumberEt.getText().toString().trim();
			if (TextUtils.isEmpty(phoneNumber)) {
				Util.toast(this, "请输入手机号");
				return;
			}

			sendDialog.showDialog();
			new ValidateCodeTask(GoagalInfo.userInfo.username, phoneNumber).execute();
		}

		if (v.getId() == findIdByString("update_btn")) {
			if (!NetworkImpl.isNetWorkConneted(this)) {
				Util.toast(this, "网络不给力，请检查网络设置");
				return;
			}

			String phoneNumber = phoneNumberEt.getText().toString().trim();
			String validateCode = validateCodeEt.getText().toString().trim();

			if (TextUtils.isEmpty(phoneNumber)) {
				Util.toast(this, "请输入手机号");
				return;
			}
			if (TextUtils.isEmpty(validateCode)) {
				Util.toast(this, "请输入验证码");
				return;
			}

			bindDialog.show();
			new BindPhoneTask(phoneNumber, GoagalInfo.userInfo.username, validateCode).execute();
		}

	}

	/**
	 * 绑定手机发送-验证码
	 * 
	 * @author admin
	 *
	 */
	private class ValidateCodeTask extends AsyncTask<String, Integer, ResultInfo<String>> {

		String userName;
		String phoneNumber;

		public ValidateCodeTask(String userName, String phoneNumber) {
			this.userName = userName;
			this.phoneNumber = phoneNumber;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ResultInfo<String> doInBackground(String... params) {
			BindPhoneValidateEngin validateEngin = new BindPhoneValidateEngin(UserInfoPhoneUpdateActivity.this,
					userName, phoneNumber);
			return validateEngin.run();
		}

		@Override
		protected void onPostExecute(ResultInfo<String> resultInfo) {
			super.onPostExecute(resultInfo);
			sendDialog.dismiss();
			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Util.toast(UserInfoPhoneUpdateActivity.this, "验证码已发送");
				Logger.msg("验证码发送成功----");
				codeRefresh();
				GoagalInfo.isGetValidate = 1;
				// 清空验证码输入框
				validateCodeEt.setText("");
			} else if (resultInfo != null && resultInfo.code == HttpConfig.VALIDATE_CODE_IS_SEND) {
				Util.toast(UserInfoPhoneUpdateActivity.this, resultInfo.message);
				GoagalInfo.isGetValidate = 1;
				// 清空验证码输入框
				validateCodeEt.setText("");
			} else {
				Util.toast(UserInfoPhoneUpdateActivity.this, resultInfo.message);
				GoagalInfo.isGetValidate = 0;
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
			BindPhoneEngin bindPhoneEngin = new BindPhoneEngin(UserInfoPhoneUpdateActivity.this, phoneNumber, userName,
					code);
			return bindPhoneEngin.run();
		}

		@Override
		protected void onPostExecute(BindPhoneResult bindPhoneResult) {
			super.onPostExecute(bindPhoneResult);
			bindDialog.dismiss();
			if (bindPhoneResult.result) {
				GoagalInfo.userInfo.validateMobile = 1;
				Util.toast(UserInfoPhoneUpdateActivity.this, "绑定手机号成功");

				// 存储账号，若手机号为空，则存储用户名
				String accountNumber = phoneNumber != null ? phoneNumber : userName;
				
				boolean isExist = UserLoginInfodao.getInstance(UserInfoPhoneUpdateActivity.this)
						.findUserLoginInfoByName(accountNumber);
				if (!isExist) {
					UserLoginInfodao.getInstance(UserInfoPhoneUpdateActivity.this).saveUserLoginInfo(phoneNumber,
							GoagalInfo.userInfo.password, GoagalInfo.userInfo.validateMobile, 0);
				} else {
					// 先删除
					UserLoginInfodao.getInstance(UserInfoPhoneUpdateActivity.this).deleteUserLoginByName(accountNumber);
					// 再保存最新的信息
					UserLoginInfodao.getInstance(UserInfoPhoneUpdateActivity.this).saveUserLoginInfo(phoneNumber,
							GoagalInfo.userInfo.password, GoagalInfo.userInfo.validateMobile, 0);
				}
				
				GoagalInfo.loginType = 2;
				// 保存登录成功的登录方式，下次直接到此页面
				PreferenceUtil.getImpl(UserInfoPhoneUpdateActivity.this).putInt(SystemUtil.getPhoneIMEI(UserInfoPhoneUpdateActivity.this),
						GoagalInfo.loginType);
				setResult(Constants.UPDATE_SUCCESS);
				finish();
			} else {
				Util.toast(UserInfoPhoneUpdateActivity.this, StringUtils.isEmpty(bindPhoneResult.message) ? "绑定失败，请重试":bindPhoneResult.message);
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
					getValidateBtn.setBackgroundResource(
							MResource.getIdByName(UserInfoPhoneUpdateActivity.this, "drawable", "border_line_btn"));
					getValidateBtn.setTextColor(UserInfoPhoneUpdateActivity.this.getResources().getColor(
							MResource.getIdByName(UserInfoPhoneUpdateActivity.this, "color", "border_line_color")));
					return;
				}
				getValidateBtn.setEnabled(false);
				getValidateBtn.setText("重新发送(" + secondes + ")");
				getValidateBtn.setBackgroundResource(
						MResource.getIdByName(UserInfoPhoneUpdateActivity.this, "drawable", "border_line_gray"));
				getValidateBtn.setTextColor(UserInfoPhoneUpdateActivity.this.getResources()
						.getColor(MResource.getIdByName(UserInfoPhoneUpdateActivity.this, "color", "line_color")));
				handler.postDelayed(this, 1000);
			}
		};
		handler.postDelayed(runnable, 0);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("UserInfoPhoneUpdateActivity");
		MobclickAgent.onPause(this);
	}
}
