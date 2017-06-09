package com.game.sdk.ui;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.UpdateInfoResult;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.engin.UpdateUserInfoEngin;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommonInfoActivity extends BaseActivity implements OnClickListener {

	private LinearLayout commonLayout;

	private LinearLayout sexLayout;

	private TextView titleTv;

	private ImageView backIv;

	private EditText updateInfoEt;

	private Button updateBtn;

	private RelativeLayout boyLayout;

	private RelativeLayout girlLayout;

	private ImageView boySelectedIcon;

	private ImageView girlSelectedIcon;

	private UpdateUserInfoEngin updateUserInfoEngin;

	CustomDialog updateDialog;

	private String updateTypeText;

	private String infoType = null;

	private int sex = 1;

	@Override
	public String getLayoutId() {
		return "fysdk_activity_common_info";
	}

	@Override
	public void initVars() {
		super.initVars();

		titleTv = findTextViewByString("title_tv");
		backIv = findImageViewByString("back_iv");

		commonLayout = (LinearLayout) findViewByString("common_layout");
		sexLayout = (LinearLayout) findViewByString("sex_layout");

		updateInfoEt = findEditTextByString("update_info_et");
		updateBtn = findButtonByString("update_btn");

		boyLayout = (RelativeLayout) findViewByString("boy_layout");
		girlLayout = (RelativeLayout) findViewByString("girl_layout");

		boySelectedIcon = findImageViewByString("boy_selected_icon");
		girlSelectedIcon = findImageViewByString("girl_selected_icon");

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			infoType = bundle.getString("infoType");
		}

		if (infoType.equals("nickname")) {
			updateTypeText = findStringByResId("nick_name_update_text");
			commonLayout.setVisibility(View.VISIBLE);
			sexLayout.setVisibility(View.GONE);
			if (GoagalInfo.userInfo != null) {
				updateInfoEt.setText(StringUtils.isEmpty(GoagalInfo.userInfo.nickName) ? GoagalInfo.userInfo.username
						: GoagalInfo.userInfo.nickName);
			}
		}
		if (infoType.equals("email")) {
			updateTypeText = findStringByResId("email_update_text");
			commonLayout.setVisibility(View.VISIBLE);
			sexLayout.setVisibility(View.GONE);
			if (GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.email)) {
				updateInfoEt.setText(GoagalInfo.userInfo.email);
			}
		}
		if (infoType.equals("qq")) {
			updateTypeText = findStringByResId("qq_update_text");
			commonLayout.setVisibility(View.VISIBLE);
			sexLayout.setVisibility(View.GONE);
			if (GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.qq)) {
				updateInfoEt.setText(GoagalInfo.userInfo.qq);
			}
		}
		if (infoType.equals("sex")) {
			updateTypeText = findStringByResId("sex_update_text");
			commonLayout.setVisibility(View.GONE);
			sexLayout.setVisibility(View.VISIBLE);
		}

		titleTv.setText(updateTypeText);
		backIv.setOnClickListener(this);
		boyLayout.setOnClickListener(this);
		girlLayout.setOnClickListener(this);
		updateBtn.setOnClickListener(this);
	}

	@Override
	public void initViews() {
		super.initViews();
		updateDialog = new CustomDialog(this, "正在修改");
	}

	@Override
	public void initData() {
		super.initData();

		if (GoagalInfo.userInfo.sex == 1) {
			boySelectedIcon.setVisibility(View.VISIBLE);
			girlSelectedIcon.setVisibility(View.INVISIBLE);
			sex = 1;
		}else if (GoagalInfo.userInfo.sex == 2) {
			boySelectedIcon.setVisibility(View.INVISIBLE);
			girlSelectedIcon.setVisibility(View.VISIBLE);
			sex = 2;
		}else{
			boySelectedIcon.setVisibility(View.INVISIBLE);
			girlSelectedIcon.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("CommonInfoActivity");
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			finish();
		}

		if (v.getId() == findIdByString("boy_layout")) {
			boySelectedIcon.setVisibility(View.VISIBLE);
			girlSelectedIcon.setVisibility(View.INVISIBLE);
			sex = 1;
		}

		if (v.getId() == findIdByString("girl_layout")) {
			boySelectedIcon.setVisibility(View.INVISIBLE);
			girlSelectedIcon.setVisibility(View.VISIBLE);
			sex = 2;
		}

		if (v.getId() == findIdByString("update_btn")) {

			UserInfo uinfo = new UserInfo();

			if (commonLayout.getVisibility() == View.VISIBLE) {
				if (TextUtils.isEmpty(updateInfoEt.getText())) {
					Util.toast(this, findStringByResId("update_info_text"));
					return;
				}

				if (infoType.equals("nickname")) {
					uinfo.nickName = updateInfoEt.getText().toString();
				}
				if (infoType.equals("email")) {
					uinfo.email = updateInfoEt.getText().toString();
				}
				if (infoType.equals("qq")) {
					uinfo.qq = updateInfoEt.getText().toString();
				}
			}

			if (sexLayout.getVisibility() == View.VISIBLE) {
				if (infoType.equals("sex")) {
					uinfo.qq = updateInfoEt.getText().toString();
				}
				uinfo.sex = sex;

			}

			updateDialog.show();

			new UpdateInfoTask(uinfo).execute();
		}
	}

	/**
	 * 修改用户信息
	 * 
	 * @author admin
	 *
	 */
	private class UpdateInfoTask extends AsyncTask<String, Integer, UpdateInfoResult> {
		UserInfo uInfo;

		public UpdateInfoTask(UserInfo uInfo) {
			this.uInfo = uInfo;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected UpdateInfoResult doInBackground(String... params) {
			updateUserInfoEngin = new UpdateUserInfoEngin(CommonInfoActivity.this, uInfo);
			return updateUserInfoEngin.updateUserInfo();
		}

		@Override
		protected void onPostExecute(UpdateInfoResult updateInfoResult) {
			super.onPostExecute(updateInfoResult);
			updateDialog.dismiss();
			if (updateInfoResult!= null && updateInfoResult.result) {
				Util.toast(CommonInfoActivity.this, !StringUtils.isEmpty(updateInfoResult.pointMessage)?updateInfoResult.pointMessage:"修改成功");
				setResult(Constants.UPDATE_SUCCESS);
				finish();
			} else {
				Util.toast(CommonInfoActivity.this, "修改失败");
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("CommonInfoActivity");
		MobclickAgent.onPause(this);
	}
	
}
