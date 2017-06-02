package com.game.sdk.ui.fragment;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.engin.UpdatePassWordEngin;
import com.game.sdk.ui.LoginActivity;
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

public class SetNewPassWordFragment extends BaseFragment implements OnClickListener {

	private LoginActivity loginActivity;

	private ImageView backIv;

	private TextView titleTv;

	private EditText newPassWordEt;

	private EditText confirmPassWordEt;

	private Button submitBtn;

	private UpdatePassWordEngin updatePassWordEngin;

	CustomDialog updateDialog;

	private String newPs;

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
		return "update_password_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		loginActivity = (LoginActivity) getActivity();
		updateDialog = new CustomDialog(loginActivity, "正在修改密码");
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		titleTv.setText(findStringByResId("set_new_ps_text"));

		newPassWordEt = findEditTextByString("new_ps_et");
		confirmPassWordEt = findEditTextByString("confirm_ps_et");
		submitBtn = findButtonByString("submit_btn");
		backIv.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();

	}

	@Override
	public void onResume() {
		super.onResume();
		//MobclickAgent.onResume(loginActivity);
		MobclickAgent.onPageStart("SetNewPassWordFragment");
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			loginActivity.changeFragment(6);
		}
		if (v.getId() == findIdByString("submit_btn")) {

			newPs = newPassWordEt.getText().toString().trim();
			String confirmPs = confirmPassWordEt.getText().toString().trim();

			if (TextUtils.isEmpty(newPs)) {
				Util.toast(loginActivity, "请输入新密码");
				return;
			}

			if (TextUtils.isEmpty(confirmPs)) {
				Util.toast(loginActivity, "请再次输入密码");
				return;
			}

			if (!newPs.equals(confirmPs)) {
				Util.toast(loginActivity, "二次密码输入不一致");
				return;
			}
			if (GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.username)) {

				if (updateDialog != null && !updateDialog.isShowing()) {
					updateDialog.show();
				}

				new UpdatePassWordTask(GoagalInfo.userInfo.username, GoagalInfo.userInfo.password, newPs).execute();

			} else {
				Util.toast(loginActivity, "获取用户登录信息失败,请重试");
			}
		}
	}

	/**
	 * 修改密码
	 * 
	 * @author admin
	 *
	 */
	private class UpdatePassWordTask extends AsyncTask<String, Integer, Boolean> {
		String userName;
		String oldPassWord;
		String newPassWord;

		public UpdatePassWordTask(String userName, String oldPassWord, String newPassWord) {
			this.userName = userName;
			this.oldPassWord = oldPassWord;
			this.newPassWord = newPassWord;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			updatePassWordEngin = new UpdatePassWordEngin(loginActivity, userName, oldPassWord, newPassWord);
			return updatePassWordEngin.run();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			updateDialog.dismiss();
			if (result) {
				Util.toast(loginActivity, "修改密码成功");
				loginActivity.changeFragment(2);
			} else {
				Util.toast(loginActivity, "修改密码失败,请稍后重试");
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//MobclickAgent.onPause(loginActivity);
		MobclickAgent.onPageEnd("SetNewPassWordFragment");
	}
}
