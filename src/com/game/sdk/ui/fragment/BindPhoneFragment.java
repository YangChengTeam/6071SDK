package com.game.sdk.ui.fragment;

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

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.BindPhoneResult;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.engin.BindPhoneEngin;
import com.game.sdk.engin.BindPhoneValidateEngin;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.ui.MainActivity;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.NetworkImpl;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

public class BindPhoneFragment extends BaseFragment implements OnClickListener {

	private MainActivity mainActivity;

	private ImageView backIv;

	private TextView titleTv;
	
	private TextView userNameTv;
	
	private EditText phoneNumberEt;

	private EditText validateCodeEt;

	private Button getValidateBtn;
	
	private Button submitBtn;
	
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
		return "fysdk_bind_phone_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		mainActivity = (MainActivity) getActivity();
		sendDialog = new CustomDialog(mainActivity, "正在发送");
		bindDialog = new CustomDialog(mainActivity, "正在绑定");
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		userNameTv = findTextViewByString("user_name_tv");
		phoneNumberEt = findEditTextByString("phone_number_et");
		validateCodeEt = findEditTextByString("validate_code_et");
		
		getValidateBtn = findButtonByString("get_validate_btn");
		submitBtn = findButtonByString("submit_btn");
		titleTv.setText(findStringByResId("account_safety_text"));
		backIv.setOnClickListener(this);
		getValidateBtn.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();
		userNameTv.setText(GoagalInfo.userInfo.username);
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
		MobclickAgent.onPageStart("BindPhoneFragment");
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			if(GoagalInfo.bindMobileFrom == 0){
				mainActivity.changeFragment(1);
			}
			if(GoagalInfo.bindMobileFrom == 1){
				mainActivity.changeFragment(6);
			}
		}

		if (v.getId() == findIdByString("get_validate_btn")) {
			String phoneNumber = phoneNumberEt.getText().toString().trim();
			if (TextUtils.isEmpty(phoneNumber)) {
				Util.toast(mainActivity, "请输入手机号");
				return;
			}

			sendDialog.showDialog();
			new ValidateCodeTask(GoagalInfo.userInfo.username,phoneNumber).execute();
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
			BindPhoneValidateEngin validateEngin = new BindPhoneValidateEngin(mainActivity, userName, phoneNumber);
			return validateEngin.run();
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
			} else if (resultInfo != null && resultInfo.code == HttpConfig.VALIDATE_CODE_IS_SEND) {
				Util.toast(mainActivity, resultInfo.message);
				GoagalInfo.isGetValidate = 1;
				// 清空验证码输入框
				validateCodeEt.setText("");
			} else {
				Util.toast(mainActivity, resultInfo.message);
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
			BindPhoneEngin bindPhoneEngin = new BindPhoneEngin(mainActivity, phoneNumber, userName, code);
			return bindPhoneEngin.run();
		}

		@Override
		protected void onPostExecute(BindPhoneResult bindPhoneResult) {
			super.onPostExecute(bindPhoneResult);
			
			if(bindDialog != null && bindDialog.isShowing()){
				bindDialog.dismiss();
			}
			
			if (bindPhoneResult.result) {
				GoagalInfo.userInfo.validateMobile = 1;
				Util.toast(mainActivity, !StringUtils.isEmpty(bindPhoneResult.pointMessage)?bindPhoneResult.pointMessage:"绑定手机号成功");
				
				//存储账号，若手机号为空，则存储用户名
				String accountNumber = GoagalInfo.userInfo.username;
				
				boolean isExist = UserLoginInfodao.getInstance(mainActivity).findUserLoginInfoByName(accountNumber);
				if (!isExist) {
					UserLoginInfodao.getInstance(mainActivity).saveUserLoginInfo(phoneNumber, GoagalInfo.userInfo.password,GoagalInfo.userInfo.validateMobile,0);
				} else {
					// 先删除
					UserLoginInfodao.getInstance(mainActivity).deleteUserLoginByName(accountNumber);
					// 再保存最新的信息
					UserLoginInfodao.getInstance(mainActivity).saveUserLoginInfo(phoneNumber, GoagalInfo.userInfo.password,GoagalInfo.userInfo.validateMobile,0);
				}
				
				mainActivity.changeFragment(6);
			} else {
				Util.toast(mainActivity, StringUtils.isEmpty(bindPhoneResult.message) ? "绑定失败，请重试":bindPhoneResult.message);
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
		MobclickAgent.onPageEnd("BindPhoneFragment");
	}
}
