package com.game.sdk.ui;

import java.util.List;
import java.util.Random;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LoginResult;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.engin.InitEngin;
import com.game.sdk.engin.QuickLoginEngin;
import com.game.sdk.engin.RegisterAccountEngin;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.game.sdk.view.NoticeDialog;
import com.ss.android.common.lib.EventUtils;
import com.umeng.analytics.MobclickAgent;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QuickLoginActivity extends BaseActivity implements OnClickListener {

	public static int SEND_TIME = 1000;

	private RelativeLayout quickLoginLayout;

	private TextView otherLoginTv;

	MyServiceReceiver mReceiver;

	CustomDialog loginDialog;

	NoticeDialog noticeDialog;

	private RelativeLayout bgLayout;

	private LinearLayout titleLayout;

	private ImageView titleLogo;

	private Handler handler = new Handler();

	private boolean isSendSms = false;

	private int timeNumber = 1;

	@Override
	public String getLayoutId() {
		return "fysdk_activity_quick_login";
	}

	@Override
	public void initVars() {
		super.initVars();

		setOrientation();
	}

	@Override
	public void initViews() {
		super.initViews();

		Logger.msg("init quick login ------");

		loginDialog = new CustomDialog(QuickLoginActivity.this, "极速注册中");

		otherLoginTv = findTextViewByString("other_login_tv");
		quickLoginLayout = (RelativeLayout) findViewByString("quick_login_layout");
		bgLayout = (RelativeLayout) findViewByString("bg_layout");
		titleLayout = (LinearLayout) findViewByString("common_title_layout");
		titleLogo = findImageViewByString("message_title_logo");
		otherLoginTv.setOnClickListener(this);
		quickLoginLayout.setOnClickListener(this);

		IntentFilter mFilter;
		mFilter = new IntentFilter(Constants.SMS_SEND_ACTIOIN);
		mReceiver = new MyServiceReceiver();
		registerReceiver(mReceiver, mFilter);
	}

	@Override
	public void initData() {
		super.initData();
		if (GoagalInfo.inItInfo != null) {

			if (GoagalInfo.inItInfo.logoBitmp != null) {
				titleLogo.setImageBitmap(GoagalInfo.inItInfo.logoBitmp);
			}

			if (GoagalInfo.inItInfo.template != null) {
				String bgColor = GoagalInfo.inItInfo.template.bgColor;
				String headColor = GoagalInfo.inItInfo.template.headColor;

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
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//MobclickAgent.onPageStart("QuickLoginActivity");
		MobclickAgent.onResume(this);
		QuickLoginActivity.this.setFinishOnTouchOutside(false);
		// params.alpha = 0.95f; // 设置本身透明度
		// params.dimAmount = 1.0f; // 设置黑暗度
		// params.format = PixelFormat.RGBA_8888;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setOrientation();
	}

	public void setOrientation() {
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 0) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 1) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		LayoutParams params = getWindow().getAttributes();
		params.width = (int) (DimensionUtil.getWidth(QuickLoginActivity.this) * 0.9);
		params.height = (int) (DimensionUtil.getHeight(QuickLoginActivity.this) * 0.6);
		getWindow().setAttributes(params);
	}

	@Override
	public void onClick(View v) {
		// 其他登录方式
		if (v.getId() == findIdByString("other_login_tv")) {
			GoagalInfo.loginType = 2;
			GoagalInfo.isQuick = 0;// 非快速登录
			toLoginActivity();
		}

		// 一键登录
		if (v.getId() == findIdByString("quick_login_layout")) {

			/*
			 * if (SystemUtil.hasSimCard(this)) { loginDialog.showDialog();
			 * 
			 * handler.postDelayed(runnable, SEND_TIME);
			 * 
			 * Intent itSend = new Intent(Constants.SMS_SEND_ACTIOIN);
			 * PendingIntent mSendPI = PendingIntent.getBroadcast(this, 0,
			 * itSend, 0); // 发送短信 SmsManager smsManager =
			 * SmsManager.getDefault();
			 * 
			 * Logger.msg("接受号码---" + GoagalInfo.inItInfo.mtCode + "---上行验证码---"
			 * + GoagalInfo.validateCode);
			 * 
			 * List<String> divideContents =
			 * smsManager.divideMessage(GoagalInfo.validateCode); for (String
			 * text : divideContents) {
			 * smsManager.sendTextMessage(GoagalInfo.inItInfo.mtCode, null,
			 * text, mSendPI, null); } } else { toLoginActivity(); }
			 */

			if (loginDialog != null && !loginDialog.isShowing()) {
				loginDialog.showDialog();
			}

			GoagalInfo.isQuick = 1;
			new RegisterTask(null, null).execute();
		}
	}

	public class MyServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				if (intent.getAction() != null && intent.getAction().equals(Constants.SMS_SEND_ACTIOIN)) {
					Logger.msg("MyServiceReceiver---" + intent.getAction());
					isSendSms = true;
				}

				switch (getResultCode()) {
				case RESULT_OK:
					// 发送短信成功
					Logger.msg("----发送短信成功------");
					// Util.toast(QuickLoginActivity.this, "发送短信已成功");
					// 异步登录
					// new LoginTask().execute();
					break;
				// 发送短信失败
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				case SmsManager.RESULT_ERROR_RADIO_OFF:
				case SmsManager.RESULT_ERROR_NULL_PDU:
				default:
					Logger.msg("----发送短信失败------");
					// closeDialog();
					// toLoginActivity();
					break;
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	}

	private class LoginTask extends AsyncTask<String, Integer, LoginResult> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected LoginResult doInBackground(String... params) {
			QuickLoginEngin quickLoginEngin = new QuickLoginEngin(QuickLoginActivity.this, true, null);
			return quickLoginEngin.quickRun();
		}

		@Override
		protected void onPostExecute(LoginResult loginResult) {
			super.onPostExecute(loginResult);
			closeDialog();
			if (loginResult.result) {
				Logger.msg("短信一键登录成功----");

				if (GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.agentId)) {
					if (!GoagalInfo.userInfo.agentId.equals(GoagalInfo.agentid)) {
						GoagalInfo.agentid = GoagalInfo.userInfo.agentId;
						new ReInitInfoTaskByUserId().execute();
					} else {
						// 将logo图及启动图恢复为渠道对应的值
						if (Util.getInitLogoFileBitmap(QuickLoginActivity.this, Constants.AGENT_LOGO_IMAGE) != null) {
							GoagalInfo.inItInfo.logoBitmp = Util.getInitLogoFileBitmap(QuickLoginActivity.this,
									Constants.AGENT_LOGO_IMAGE);
							Util.writeLaunchImageInSDCard(QuickLoginActivity.this, GoagalInfo.inItInfo.logoBitmp,
									Constants.LOGO_IMAGE);
						}

						if (Util.getInitLogoFileBitmap(QuickLoginActivity.this, Constants.AGENT_INIT_IMAGE) != null) {
							GoagalInfo.inItInfo.lunchBitmp = Util.getInitLogoFileBitmap(QuickLoginActivity.this,
									Constants.AGENT_INIT_IMAGE);
							Util.writeLaunchImageInSDCard(QuickLoginActivity.this, GoagalInfo.inItInfo.lunchBitmp,
									Constants.INIT_IMAGE);
						}
					}
				}

				if (GoagalInfo.userInfo != null) {
					LogincallBack logincallBack = new LogincallBack();
					
					if(loginResult.newSdkReg == 0){
						logincallBack.username = GoagalInfo.userInfo.username;
						if(loginResult.cpNotice == 0 && !StringUtils.isEmpty(loginResult.fixName)){
							logincallBack.username = loginResult.fixName;
						}
					} else {
						logincallBack.username = GoagalInfo.userInfo.userId;
					}
					
					logincallBack.userId = GoagalInfo.userInfo.userId;
					logincallBack.isBindPhone = GoagalInfo.userInfo.validateMobile == 1 ? true : false;
					logincallBack.logintime = GoagalInfo.userInfo.logintime;
					logincallBack.sign = GoagalInfo.userInfo.sign;

					GoagalInfo.loginlistener.loginSuccess(logincallBack);
				}

				if (!StringUtils.isEmpty(GoagalInfo.noticeMsg)) {
					noticeDialog = new NoticeDialog(QuickLoginActivity.this, GoagalInfo.noticeMsg);
					noticeDialog.show();

					noticeDialog.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							QuickLoginActivity.this.finish();
						}
					});
				} else {
					QuickLoginActivity.this.finish();
				}

			} else {

				Logger.msg("短信一键登录失败----");
				LoginErrorMsg errorMsg = new LoginErrorMsg(-1,
						StringUtils.isEmpty(loginResult.message) ? "短信一键登录失败" : loginResult.message);
				GoagalInfo.loginlistener.loginError(errorMsg);

				toLoginActivity();
			}
		}
	}

	private class ReInitInfoTaskByUserId extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			InitEngin initEngin = new InitEngin(QuickLoginActivity.this);
			return initEngin.run();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

	/**
	 * 倒计时4秒钟，如果在4秒内， 不管是否允许发送短信，短信发送的广播中未收到"已发送"的广播
	 */
	Runnable runnable = new Runnable() {
		public void run() {
			try {
				/*
				 * if (timeNumber < 4) { if (!isSendSms) {
				 * handler.postDelayed(this, SEND_TIME); timeNumber++; } } else
				 * { if (!isSendSms) { if (loginDialog != null &&
				 * loginDialog.isShowing()) { loginDialog.dismiss(); }
				 * toLoginActivity(); } }
				 */

				// 极速注册成功后发送短信发服务器,短信内容为:FYSDK_+注册的账号
				Intent itSend = new Intent(Constants.SMS_SEND_ACTIOIN);
				PendingIntent mSendPI = PendingIntent.getBroadcast(QuickLoginActivity.this, 0, itSend, 0);
				// 发送短信
				SmsManager smsManager = SmsManager.getDefault();

				String upCode = "";

				if (GoagalInfo.userInfo != null && GoagalInfo.userInfo.username != null) {
					upCode = "user:" + GoagalInfo.userInfo.username;
				}
				
				String smsMobile = getSmsMobile(GoagalInfo.inItInfo.smsMobileList);
				Logger.msg("接受号码---" + smsMobile + "---上行注册码(即极速注册的账号)---" + upCode);
				
				List<String> divideContents = smsManager.divideMessage(upCode);
				for (String text : divideContents) {
					smsManager.sendTextMessage(smsMobile, null, text, mSendPI, null);
				}

			} catch (Exception e) {
				Logger.msg("runnable---error");
			}
		}
	};

	public String getSmsMobile(String smsString) {
		if (!StringUtils.isEmpty(smsString)) {
			String[] sms = smsString.split(",");
			if (sms != null && sms.length > 0) {
				Random rand = new Random();
				int position = rand.nextInt(sms.length);
				return sms[position];
			} else {
				return "17386034522";
			}
		} else {
			return "17386034522";
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
			Util.reInitChannel(QuickLoginActivity.this);
			RegisterAccountEngin loginEngin = new RegisterAccountEngin(QuickLoginActivity.this, userName, password);
			return loginEngin.run();
		}

		@Override
		protected void onPostExecute(LoginResult loginResult) {
			super.onPostExecute(loginResult);

			if (loginDialog != null && loginDialog.isShowing()) {
				loginDialog.dismiss();
			}

			if (loginResult.result) {
				if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
					EventUtils.setRegister("quick_sms_play_register", true);
				}
				// 将logo图及启动图恢复为渠道对应的值
				if (Util.getInitLogoFileBitmap(QuickLoginActivity.this, Constants.AGENT_LOGO_IMAGE) != null) {
					GoagalInfo.inItInfo.logoBitmp = Util.getInitLogoFileBitmap(QuickLoginActivity.this,
							Constants.AGENT_LOGO_IMAGE);
					Util.writeLaunchImageInSDCard(QuickLoginActivity.this, GoagalInfo.inItInfo.logoBitmp,
							Constants.LOGO_IMAGE);
				}

				if (Util.getInitLogoFileBitmap(QuickLoginActivity.this, Constants.AGENT_INIT_IMAGE) != null) {
					GoagalInfo.inItInfo.lunchBitmp = Util.getInitLogoFileBitmap(QuickLoginActivity.this,
							Constants.AGENT_INIT_IMAGE);
					Util.writeLaunchImageInSDCard(QuickLoginActivity.this, GoagalInfo.inItInfo.lunchBitmp,
							Constants.INIT_IMAGE);
				}

				Logger.msg("极速注册账号成功----");

				handler.postDelayed(runnable, SEND_TIME);

				if (GoagalInfo.userInfo != null) {

					GoagalInfo.isLogin = true;

					LogincallBack logincallBack = new LogincallBack();
					
					logincallBack.username = GoagalInfo.userInfo.userId;
					logincallBack.userId = GoagalInfo.userInfo.userId;
					logincallBack.isBindPhone = GoagalInfo.userInfo.validateMobile == 1 ? true : false;
					logincallBack.logintime = GoagalInfo.userInfo.logintime;
					logincallBack.sign = GoagalInfo.userInfo.sign;

					GoagalInfo.loginlistener.loginSuccess(logincallBack);
				}

				if (!StringUtils.isEmpty(GoagalInfo.noticeMsg)) {
					noticeDialog = new NoticeDialog(QuickLoginActivity.this, GoagalInfo.noticeMsg);
					noticeDialog.show();

					noticeDialog.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							QuickLoginActivity.this.finish();
						}
					});
				} else {
					QuickLoginActivity.this.finish();
				}

			} else {
				if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
					EventUtils.setRegister("quick_sms_play_register", false);
				}
				GoagalInfo.isLogin = false;
				Logger.msg("极速试玩注册失败----");
				Util.toast(QuickLoginActivity.this, "极速试玩注册失败");

				LoginErrorMsg errorMsg = new LoginErrorMsg(-1, "极速试玩注册失败");
				GoagalInfo.loginlistener.loginError(errorMsg);

				// 跳转到账号密码登录
				toLoginActivity();
			}
		}
	}

	public void toLoginActivity() {
		GoagalInfo.loginType = 2;
		Intent intent = new Intent(QuickLoginActivity.this, LoginActivity.class);
		startActivity(intent);
		QuickLoginActivity.this.finish();
	}

	/**
	 * 关闭加载框
	 */
	public void closeDialog() {
		if (loginDialog != null && loginDialog.isShowing()) {
			loginDialog.dismiss();
		}

		if (noticeDialog != null && noticeDialog.isShowing()) {
			noticeDialog.dismiss();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//MobclickAgent.onPageEnd("QuickLoginActivity");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		closeDialog();
	}

}
