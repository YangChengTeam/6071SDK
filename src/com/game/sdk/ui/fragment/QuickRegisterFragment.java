package com.game.sdk.ui.fragment;

import java.util.regex.Pattern;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginResult;
import com.game.sdk.engin.RegisterAccountEngin;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.EmulatorCheckUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.NetworkImpl;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.ss.android.common.lib.EventUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 账号密码登录主界面
 * 
 * @author admin
 *
 */
public class QuickRegisterFragment extends BaseFragment implements OnClickListener {

	LoginActivity loginActivity;
	/**
	 * 已有账号登录
	 */
	private TextView hasAccountTv;
	/**
	 * 极速试玩
	 */
	private LinearLayout quickPlayLayout;

	private TextView quickPlayTv;

	private EditText userNameEt;

	private EditText passWordEt;

	private Button registerBtn;

	private ImageView titleLogo;

	private RelativeLayout bgLayout;

	private LinearLayout titleLayout;

	private LinearLayout serverLayout;

	private TextView serviceTelTv;

	private TextView serviceQQTv;

	private ImageView quickPlayIv;

	CustomDialog registerDialog;

	@Override
	public String getLayoutId() {
		return "quick_register_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		loginActivity = (LoginActivity) getActivity();
		registerDialog = new CustomDialog(loginActivity, "正在注册");
		hasAccountTv = findTextViewByString("has_account_tv");
		quickPlayTv = findTextViewByString("quick_play_tv");
		quickPlayLayout = (LinearLayout) findViewByString("quick_play_layout");
		userNameEt = findEditTextByString("user_name_et");
		passWordEt = findEditTextByString("password_et");
		registerBtn = findButtonByString("register_btn");

		titleLogo = findImageViewByString("register_login_logo");
		bgLayout = (RelativeLayout) findViewByString("bg_layout");
		titleLayout = (LinearLayout) findViewByString("common_title_layout");
		serverLayout = (LinearLayout) findViewByString("service_number_layout");
		quickPlayIv = findImageViewByString("quick_play_iv");

		serviceTelTv = findTextViewByString("service_tel_tv");
		serviceQQTv = findTextViewByString("service_qq_tv");

		hasAccountTv.setOnClickListener(this);
		quickPlayLayout.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		serviceTelTv.setOnClickListener(this);
		serviceQQTv.setOnClickListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		initTheme();

		if (GoagalInfo.inItInfo != null) {

			if (!StringUtils.isEmpty(GoagalInfo.inItInfo.tel)) {
				serviceTelTv.setText("客服电话：" + Html.fromHtml(GoagalInfo.inItInfo.tel));
			} else {
				serviceTelTv.setText(
						"客服电话:" + Html.fromHtml("<a href='tel://400-796-6071' style='text-decoration:none;'>400-796-6071</a>"));
			}

			/*if (!StringUtils.isEmpty(GoagalInfo.inItInfo.qq)) {
				serviceQQTv.setText("客服QQ：" + Html.fromHtml(GoagalInfo.inItInfo.qq));
			} else {
				serviceQQTv.setText("客服QQ：3453725652");
			}*/

			NoUnderlineSpan mNoUnderlineSpan = new NoUnderlineSpan();
			if (serviceTelTv.getText() instanceof Spannable) {
				Spannable s = (Spannable) serviceTelTv.getText();
				s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
			}

			if (serviceQQTv.getText() instanceof Spannable) {
				Spannable s = (Spannable) serviceQQTv.getText();
				s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
			}
		} else {
			serviceTelTv.setText("客服电话：400-796-6071");
			serviceQQTv.setText("客服QQ：3453725652");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//MobclickAgent.onResume(loginActivity);
		MobclickAgent.onPageStart("QuickRegisterFragment");
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.logoBitmp != null) {
			titleLogo.setImageBitmap(GoagalInfo.inItInfo.logoBitmp);
		}

		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.template != null) {
			String bgColor = GoagalInfo.inItInfo.template.bgColor;
			String headColor = GoagalInfo.inItInfo.template.headColor;
			String btnColor = GoagalInfo.inItInfo.template.btnColor;
			String noticeColor = GoagalInfo.inItInfo.template.noticeColor;
			String changeFontColor = GoagalInfo.inItInfo.template.fontColor;

			if (!StringUtils.isEmpty(bgColor)) {
				GradientDrawable allBg = (GradientDrawable) bgLayout.getBackground();
				allBg.setColor(Color.parseColor("#" + bgColor));

				if (!StringUtils.isEmpty(headColor)) {
					if (bgColor.equals(headColor)) {
						GradientDrawable titleBg = (GradientDrawable) titleLayout.getBackground();
						titleBg.setColor(Color.parseColor("#00000000"));
					} else {
						GradientDrawable titleBg = (GradientDrawable) titleLayout.getBackground();
						titleBg.setColor(Color.parseColor("#" + GoagalInfo.inItInfo.template.headColor));
					}
				} else {
					GradientDrawable titleBg = (GradientDrawable) titleLayout.getBackground();
					titleBg.setColor(Color.parseColor("#00000000"));
				}
			}

			if (!StringUtils.isEmpty(btnColor)) {
				GradientDrawable btnBg = (GradientDrawable) registerBtn.getBackground();
				btnBg.setColor(Color.parseColor("#" + btnColor));

				quickPlayTv.setTextColor(Color.parseColor("#" + btnColor));
			}

			if (!StringUtils.isEmpty(noticeColor)) {
				serverLayout.setBackgroundColor(Color.parseColor("#" + noticeColor));
			}

			if (!StringUtils.isEmpty(changeFontColor)) {
				hasAccountTv.setTextColor(Color.parseColor("#" + changeFontColor));
			}

			if (GoagalInfo.inItInfo.playBitmp != null) {
				quickPlayIv.setImageBitmap(GoagalInfo.inItInfo.playBitmp);
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("has_account_tv")) {
			loginActivity.changeFragment(2);
			return;
		}
		
		if (v.getId() == findIdByString("service_qq_tv")) {
			GoagalInfo.qqKefuFrom = 1;
			loginActivity.changeFragment(5);
			return;
		}
		
		if (v.getId() == findIdByString("quick_play_layout")) {

			if (registerDialog != null && !registerDialog.isShowing()) {
				registerDialog.showDialog();
			}
			GoagalInfo.isQuick = 1;
			new RegisterTask(null, null).execute();
		}

		if (v.getId() == findIdByString("register_btn")) {

			if (!NetworkImpl.isNetWorkConneted(loginActivity)) {
				Util.toast(loginActivity, "网络不给力，请检查网络设置");
				return;
			}

			String username = userNameEt.getText().toString().trim();
			String password = passWordEt.getText().toString().trim();

			if (TextUtils.isEmpty(username)) {
				Util.toast(loginActivity, "请输入用户名");
				return;
			}
			if (TextUtils.isEmpty(password)) {
				Util.toast(loginActivity, "请输入密码");
				return;
			}
			Pattern pat = Pattern.compile("[\u4e00-\u9fa5]");

			if (username.length() < 6 || username.length() > 16 || pat.matcher(username).find()) {
				Util.toast(loginActivity, "账号只能由6至16位英文或数字组成");
				return;
			}
			if (password.length() < 6 || password.length() > 16 || pat.matcher(password).find()) {
				Util.toast(loginActivity, "密码只能由6至16位英文或数字组成");
				return;
			}

			if (registerDialog != null && !registerDialog.isShowing()) {
				registerDialog.showDialog();
			}

			GoagalInfo.isQuick = 0;
			new RegisterTask(username, password).execute();
		}
		
		if (v.getId() == MResource.getIdByName(loginActivity, "id", "service_tel_tv")) {
			Logger.msg("isEmulator--->"+EmulatorCheckUtil.isEmulator());
			if (!EmulatorCheckUtil.isEmulator()) {
				if (SystemUtil.isValidContext(loginActivity) && GoagalInfo.inItInfo != null && !StringUtils.isEmpty(GoagalInfo.inItInfo.tel)) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					Uri data = Uri.parse("tel:" + GoagalInfo.inItInfo.tel);
					intent.setData(data);
					loginActivity.startActivity(intent);
				}
			}
		}
	}

	/**
	 * 账号密码注册
	 * 
	 * @author admin
	 *
	 */
	private class RegisterTask extends AsyncTask<String, Integer, LoginResult> {

		private String userName;

		private String password;

		public RegisterTask(String userName, String password) {
			this.userName = userName;
			this.password = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected LoginResult doInBackground(String... params) {
			Util.reInitChannel(loginActivity);
			RegisterAccountEngin loginEngin = new RegisterAccountEngin(loginActivity, userName, password);
			return loginEngin.run();
		}

		@Override
		protected void onPostExecute(LoginResult loginResult) {
			super.onPostExecute(loginResult);

			registerDialog.dismiss();

			if (loginResult.result) {
				if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
					EventUtils.setRegister("quick_register", true);
				}
				//将logo图及启动图恢复为渠道对应的值
				if (Util.getInitLogoFileBitmap(loginActivity, Constants.AGENT_LOGO_IMAGE) != null) {
					GoagalInfo.inItInfo.logoBitmp = Util.getInitLogoFileBitmap(loginActivity,Constants.AGENT_LOGO_IMAGE);
					Util.writeLaunchImageInSDCard(loginActivity, GoagalInfo.inItInfo.logoBitmp, Constants.LOGO_IMAGE);
				}
				
				if (Util.getInitLogoFileBitmap(loginActivity, Constants.AGENT_INIT_IMAGE) != null) {
					GoagalInfo.inItInfo.lunchBitmp = Util.getInitLogoFileBitmap(loginActivity,Constants.AGENT_INIT_IMAGE);
					Util.writeLaunchImageInSDCard(loginActivity, GoagalInfo.inItInfo.lunchBitmp, Constants.INIT_IMAGE);
				}
				
				Logger.msg("注册账号成功----");
				// Util.toast(loginActivity, "注册成功");
				
				loginActivity.changeFragment(4);

			} else {
				if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
					EventUtils.setRegister("quick_register", false);
				}
				Logger.msg("注册账号失败----");
				Util.toast(loginActivity, "注册失败");
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//MobclickAgent.onPause(loginActivity);
		MobclickAgent.onPageEnd("QuickRegisterFragment");
	}

	public class NoUnderlineSpan extends UnderlineSpan {
		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(ds.linkColor);
			ds.setUnderlineText(false);
		}
	}
}
