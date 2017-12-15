package com.game.sdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LoginResult;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.engin.InitEngin;
import com.game.sdk.engin.LoginEngin;
import com.game.sdk.engin.QuickLoginEngin;
import com.game.sdk.engin.UpdateMtCodeEngin;
import com.game.sdk.engin.ValidateEngin;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.ui.adapter.UserLoginListAdapter;
import com.game.sdk.ui.adapter.UserLoginListAdapter.CloseListener;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.EmulatorCheckUtil;
import com.game.sdk.utils.GameBox2SDKUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.MobileInfoUtil;
import com.game.sdk.utils.NetworkImpl;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.game.sdk.view.NoticeDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 手机号登录主界面
 * 
 * @author admin
 *
 */
public class PhoneLoginFragment extends BaseFragment implements OnClickListener, CloseListener {

	private TextView changeAccountTv;

	private TextView quickRegisterTv;

	private LinearLayout quickRegisterLayout;

	LoginActivity loginActivity;

	private PopupWindow pw_select_user;

	private UserLoginListAdapter pw_adapter;

	private List<UserInfo> userLoginInfos;

	private LinearLayout moreAccountLayout;

	protected LayoutInflater inflater;

	private EditText loginUserEt;

	private EditText validateEt;

	private Button validateBtn;

	private Button intoGameBtn;

	private ImageView titleLogo;

	private RelativeLayout bgLayout;

	private LinearLayout titleLayout;

	private LinearLayout serverLayout;

	private ImageView registerIv;

	private TextView serviceTelTv;

	private TextView serviceQQTv;

	private UserInfo userinfoSelect;// 用户选择用户的时候 赋予的用户信息

	private UserInfo currentUserInfo;

	CustomDialog sendDialog;

	CustomDialog intoGameDialog;

	NoticeDialog noticeDialog;

	private List<UserInfo> userList;

	private BroadcastReceiver smsReceiver;

	private IntentFilter smsFilter;

	private String patternCoder = "(?<!\\d)\\d{4,10}(?!\\d)";// 验证短信内容

	private String smsContent; // 验证码内容

	@Override
	public String getLayoutId() {
		return "phone_login_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		loginActivity = (LoginActivity) getActivity();
		sendDialog = new CustomDialog(loginActivity, "正在发送");
		intoGameDialog = new CustomDialog(loginActivity, "正在进入游戏");
		changeAccountTv = findTextViewByString("change_account_tv");
		quickRegisterTv = findTextViewByString("quick_register_tv");
		quickRegisterLayout = (LinearLayout) findViewByString("quick_register_layout");
		moreAccountLayout = (LinearLayout) findViewByString("more_account_layout");
		loginUserEt = findEditTextByString("login_user_et");
		validateEt = findEditTextByString("validate_et");
		validateBtn = findButtonByString("get_validate_btn");
		intoGameBtn = findButtonByString("into_game_btn");
		titleLogo = findImageViewByString("phone_login_logo");

		bgLayout = (RelativeLayout) findViewByString("bg_layout");
		titleLayout = (LinearLayout) findViewByString("common_title_layout");
		serverLayout = (LinearLayout) findViewByString("service_number_layout");
		registerIv = findImageViewByString("register_icon");
		serviceTelTv = findTextViewByString("service_tel_tv");
		serviceQQTv = findTextViewByString("service_qq_tv");

		changeAccountTv.setOnClickListener(this);
		quickRegisterLayout.setOnClickListener(this);
		moreAccountLayout.setOnClickListener(this);
		validateBtn.setOnClickListener(this);
		intoGameBtn.setOnClickListener(this);

		smsFilter = new IntentFilter();// 创建意图过滤器
		smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED");// 创建意图动作
		smsFilter.setPriority(Integer.MAX_VALUE);// 设置等级最大

		smsReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				getSmsInfo(context, intent);
			}
		};

		loginActivity.registerReceiver(smsReceiver, smsFilter);// 注册广播监听器
	}

	@Override
	public void initData() {
		super.initData();

		initTheme();
		inflater = (LayoutInflater) loginActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		userList = getCommonUserInfosByType(0);

		// 读取用户信息
		currentUserInfo = getLastUserInfo(0);

		if (currentUserInfo != null) {
			if (MobileInfoUtil.getUserInfoByName(loginActivity, currentUserInfo.username) != null) {
				GoagalInfo.userInfo = MobileInfoUtil.getUserInfoByName(loginActivity, currentUserInfo.username);
			} else {
				GoagalInfo.userInfo = currentUserInfo;
			}

			loginUserEt.setText(GoagalInfo.userInfo.username);
			validateEt.setText(GoagalInfo.userInfo.password);
		} else {
			GoagalInfo.userInfo = null;
		}

		if (GoagalInfo.inItInfo != null) {

			if (!StringUtils.isEmpty(GoagalInfo.inItInfo.tel)) {
				serviceTelTv.setText("客服电话：" + Html.fromHtml(GoagalInfo.inItInfo.tel));
			} else {
				serviceTelTv.setText(
						"客服电话：" + Html.fromHtml("<a href='tel://400-796-6071' style='text-decoration:none;'>400-796-6071</a>"));
			}

			if (!StringUtils.isEmpty(GoagalInfo.inItInfo.qq)) {
				serviceQQTv.setText("客服QQ：" + Html.fromHtml(GoagalInfo.inItInfo.qq));
			} else {
				serviceQQTv.setText("客服QQ：3453725652");
			}

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
		MobclickAgent.onResume(loginActivity);
		// MobclickAgent.onPageStart("PhoneLoginFragment");
	}

	/**
	 * 初始化主题颜色
	 */
	@SuppressWarnings("deprecation")
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
				// allBg.setAlpha(150);

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
				
			    int roundRadius = DimensionUtil.dip2px(getActivity(), 3); // 8dp 圆角半径
			    //默认颜色
			    int fillColor = Color.parseColor("#" + btnColor);//内部填充颜色
			    //按压后颜色
			    int fillColorPressed = Color.parseColor("#979696");
			    
			    //默认
			    GradientDrawable gdNormal = new GradientDrawable();
			    gdNormal.setColor(fillColor);
			    gdNormal.setCornerRadius(roundRadius);
			    
				//按压后
			    GradientDrawable gdPressed = new GradientDrawable();
			    gdPressed.setColor(fillColorPressed);
			    gdPressed.setCornerRadius(roundRadius);
			    
				StateListDrawable stateDrawable = new StateListDrawable();
				
				//获取对应的属性值 Android框架自带的属性 attr
				int pressed = android.R.attr.state_pressed;  
				int window_focused = android.R.attr.state_window_focused;  
				int focused = android.R.attr.state_focused;  
				int selected = android.R.attr.state_selected;
				
				stateDrawable.addState(new int []{pressed , window_focused}, gdPressed);
				stateDrawable.addState(new int []{pressed , - focused}, gdPressed);
				stateDrawable.addState(new int []{selected }, gdPressed);
				stateDrawable.addState(new int []{focused }, gdPressed);
				stateDrawable.addState(new int []{-selected,- focused,- pressed}, gdNormal);
				intoGameBtn.setBackgroundDrawable(stateDrawable);
				
				
				GradientDrawable validateBg = (GradientDrawable) validateBtn.getBackground();
				validateBg.setStroke(DimensionUtil.dip2px(loginActivity, 1), Color.parseColor("#" + btnColor));
				validateBtn.setTextColor(Color.parseColor("#" + btnColor));
				quickRegisterTv.setTextColor(Color.parseColor("#" + btnColor));
			}

			if (!StringUtils.isEmpty(noticeColor)) {
				serverLayout.setBackgroundColor(Color.parseColor("#" + noticeColor));
			}

			if (!StringUtils.isEmpty(changeFontColor)) {
				changeAccountTv.setTextColor(Color.parseColor("#" + changeFontColor));
			}

			if (GoagalInfo.inItInfo.registerBitmp != null) {
				registerIv.setImageBitmap(GoagalInfo.inItInfo.registerBitmp);
			}
		}

	}

	/**
	 * 广播中处理接收到的短信消息
	 * 
	 * @param context
	 * @param intent
	 */
	public void getSmsInfo(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");// 获取收到的消息
		for (Object obj : objs) {
			byte[] pdu = (byte[]) obj;
			SmsMessage sms = SmsMessage.createFromPdu(pdu);
			// 短信的内容
			String message = sms.getMessageBody();// 获取手机短信的内容
			Logger.msg("message---" + message);
			String from = sms.getOriginatingAddress();// 获取手机号码
			Logger.msg("from---" + from);

			if (!TextUtils.isEmpty(from) && !TextUtils.isEmpty(message) && message.contains("游戏合作中心")) {// 判断号码是否为空

				String code = patternCode(message);
				if (!TextUtils.isEmpty(code)) {
					smsContent = code;

					Message smsMessage = new Message();
					smsMessage.what = 2;
					handler.sendMessage(smsMessage);
				}

				// 如果发件人号码与初始化时获取的短信通道号码不一致，更新服务器上的通道号码
				if (GoagalInfo.inItInfo != null && !from.equals(GoagalInfo.inItInfo.mtCode)) {
					new UpdateMtCodeTask(SystemUtil.getOperator(loginActivity), from).execute();
				}
			}
		}
	}

	/**
	 * 注销广播
	 */
	public void unRegisterReceiver() {
		if (smsReceiver != null) {
			loginActivity.unregisterReceiver(smsReceiver);
		}
	}

	/**
	 * 匹配短信中间的6个数字（验证码等）
	 * 
	 * @param patternContent
	 * @return
	 */
	private String patternCode(String patternContent) {
		if (TextUtils.isEmpty(patternContent)) {
			return null;
		}
		Pattern p = Pattern.compile(patternCoder);
		Matcher matcher = p.matcher(patternContent);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("get_validate_btn")) {
			String username = loginUserEt.getText().toString().trim();
			if (TextUtils.isEmpty(username)) {
				Util.toast(loginActivity, "请输入用户名/手机号");
				return;
			}

			sendDialog.showDialog();

			new ValidateCodeTask(username).execute();

		}
		if (v.getId() == findIdByString("into_game_btn")) {
			if (!NetworkImpl.isNetWorkConneted(loginActivity)) {
				Util.toast(loginActivity, "网络不给力，请检查网络设置");
				return;
			}

			String username = loginUserEt.getText().toString().trim();
			String password = validateEt.getText().toString().trim();

			if (TextUtils.isEmpty(username)) {
				Util.toast(loginActivity, "请输入用户名/手机号");
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
				Util.toast(loginActivity, "密码只能由6至16位16位英文或数字组成");
				return;
			}

			if (GoagalInfo.userInfo == null) {
				GoagalInfo.userInfo = new UserInfo();
			}

			GoagalInfo.userInfo.username = username;
			GoagalInfo.userInfo.password = password;

			intoGameDialog.showDialog();

			boolean isSend = PreferenceUtil.getImpl(loginActivity).getBoolean(username, false);

			// 非一键快速登录且获取过验证码
			if (isSend) {
				GoagalInfo.validateCode = password;
				new PhoneValidateTask(username).execute();
			} else {
				GoagalInfo.loginType = 2;
				new LoginTask().execute();
			}
		}

		if (v.getId() == findIdByString("change_account_tv")) {
			GoagalInfo.loginType = 3;
			loginActivity.changeFragment(2);
			return;
		}

		if (v.getId() == findIdByString("quick_register_layout")) {
			loginActivity.changeFragment(3);
			return;
		}
		if (v.getId() == findIdByString("more_account_layout")) {
			if (pw_select_user != null && pw_select_user.isShowing()) {
				pw_select_user.dismiss();
			} else {

				userList = getCommonUserInfosByType(0);// 重新查询一次

				userLoginInfos = new ArrayList<UserInfo>();
				userLoginInfos.addAll(userList);

				if (null == userLoginInfos) {
					return;
				}

				if (null == pw_adapter) {
					pw_adapter = new UserLoginListAdapter(loginActivity, userLoginInfos, 0);
				}
				
				pw_adapter.setCloseListener(this);
				
				int pwidth = DimensionUtil.dip2px(loginActivity, 340);
				if (pw_select_user == null) {
					View view = inflater.inflate(MResource.getIdByName(loginActivity, "layout", "login_user_list"),
							null);
					ListView lv_pw = (ListView) view.findViewById(MResource.getIdByName(loginActivity, "id", "lv_pw"));
					lv_pw.setCacheColorHint(0x000000);
					lv_pw.setAdapter(pw_adapter);
					lv_pw.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> adapterview, View view, int position, long row) {
							pw_select_user.dismiss();

							userinfoSelect = userLoginInfos.get(position);
							currentUserInfo = userinfoSelect;
							loginUserEt.setText(userinfoSelect.username);
							validateEt.setText(userinfoSelect.password);
						}
					});
					pw_select_user = new PopupWindow(view, pwidth, LinearLayout.LayoutParams.WRAP_CONTENT, true);
					pw_select_user.setBackgroundDrawable(new ColorDrawable(0x00000000));
					pw_select_user.setContentView(view);
				} else {
					pw_adapter.initDataList(userLoginInfos);
					pw_adapter.notifyDataSetChanged();
				}
				pw_select_user.showAsDropDown(moreAccountLayout,
						-pwidth + (int) DimensionUtil.dip2px(getActivity(), 48), 0);
			}
		}
		if (v.getId() == findIdByString("quick_register_layout")) {
			if (SystemUtil.hasSimCard(loginActivity) && !EmulatorCheckUtil.isEmulator()) {
				if (SystemUtil.isValidContext(loginActivity) && GoagalInfo.inItInfo != null
						&& !StringUtils.isEmpty(GoagalInfo.inItInfo.tel)) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					Uri data = Uri.parse("tel:" + GoagalInfo.inItInfo.tel);
					intent.setData(data);
					loginActivity.startActivity(intent);
				}
			}
		}
	}

	/**
	 * 读取用户信息
	 */
	public List<UserInfo> getCommonUserInfosByType(int type) {
		List<UserInfo> list = new ArrayList<UserInfo>();

		boolean isFirst = PreferenceUtil.getImpl(loginActivity).getBoolean(Constants.isFirstMobile, true);
		if (isFirst) {
			// 先从公共文件读取用户
			List<GameBox2SDKUtil.UserInfo> users = GameBox2SDKUtil.loadAllUserInfo(loginActivity);
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).type == type) {
					UserInfo temp = new UserInfo();
					temp.username = users.get(i).name;
					temp.password = users.get(i).pwd;
					list.add(temp);
					// 第一次读取到公共文件中用户信息时，存储到本地
					MobileInfoUtil.insertUserInfoFromPublic(loginActivity, temp);
				}
			}

			PreferenceUtil.getImpl(loginActivity).putBoolean(Constants.isFirstMobile, false);
		}

		if (list == null || list.size() == 0) {
			list = MobileInfoUtil.loadAllUserInfo(loginActivity);
		}
		
		if (list == null || list.size() == 0) {
			list = UserLoginInfodao.getInstance(loginActivity).getUserLoginInfoByType();

			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i) != null) {
						GameBox2SDKUtil.UserInfo gUserInfo = GameBox2SDKUtil.exchangeUserInfo(list.get(i).username,
								list.get(i).password, 0);
						GameBox2SDKUtil.insertUserInfo(loginActivity, gUserInfo);
						MobileInfoUtil.insertUserInfo(loginActivity, list.get(i));
					}
				}
			}

		}
		return list;
	}

	public UserInfo getLastUserInfo(int type) {
		UserInfo lastUserInfo = null;

		List<UserInfo> list = userList;

		if (list != null && list.size() > 0) {
			lastUserInfo = list.get(0);
		} else {
			lastUserInfo = UserLoginInfodao.getInstance(loginActivity).getUserInfoLastByType();
		}
		return lastUserInfo;
	}

	/**
	 * (手机号,默认密码方式)登录
	 * 
	 * @author admin
	 *
	 */
	private class LoginTask extends AsyncTask<String, Integer, LoginResult> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected LoginResult doInBackground(String... params) {
			LoginEngin loginEngin = new LoginEngin(loginActivity);
			return loginEngin.run();
		}

		@Override
		protected void onPostExecute(LoginResult loginResult) {
			super.onPostExecute(loginResult);
			intoGameDialog.dismiss();
			if (loginResult.result) {
				Logger.msg("手机号+默认密码方式登录成功----");

				if (GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.agentId)) {
					if (!GoagalInfo.userInfo.agentId.equals(GoagalInfo.agentid)) {
						GoagalInfo.agentid = GoagalInfo.userInfo.agentId;
						new ReInitInfoTaskByUserId().execute();
					}else{
						//将logo图及启动图恢复为渠道对应的值
						if (Util.getInitLogoFileBitmap(loginActivity, Constants.AGENT_LOGO_IMAGE) != null) {
							GoagalInfo.inItInfo.logoBitmp = Util.getInitLogoFileBitmap(loginActivity,Constants.AGENT_LOGO_IMAGE);
							Util.writeLaunchImageInSDCard(loginActivity, GoagalInfo.inItInfo.logoBitmp, Constants.LOGO_IMAGE);
						}
						
						if (Util.getInitLogoFileBitmap(loginActivity, Constants.AGENT_INIT_IMAGE) != null) {
							GoagalInfo.inItInfo.lunchBitmp = Util.getInitLogoFileBitmap(loginActivity,Constants.AGENT_INIT_IMAGE);
							Util.writeLaunchImageInSDCard(loginActivity, GoagalInfo.inItInfo.lunchBitmp, Constants.INIT_IMAGE);
						}
					}
				}

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
				GoagalInfo.loginType = 2;

				// 保存登录成功的登录方式，下次直接到此页面
				PreferenceUtil.getImpl(loginActivity).putInt(SystemUtil.getPhoneIMEI(loginActivity),
						GoagalInfo.loginType);

				if (!StringUtils.isEmpty(GoagalInfo.noticeMsg)) {
					noticeDialog = new NoticeDialog(loginActivity, GoagalInfo.noticeMsg);
					noticeDialog.show();

					noticeDialog.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							((Activity) loginActivity).finish();
						}
					});
				} else {
					((Activity) loginActivity).finish();
				}

			} else {
				Logger.msg("登录失败----");
				Util.toast(loginActivity,
						StringUtils.isEmpty(loginResult.message) ? "手机号或密码错误，请重试" : loginResult.message);
				LoginErrorMsg loginErrorMsg = new LoginErrorMsg(-1,
						StringUtils.isEmpty(loginResult.message) ? "手机号或密码错误，请重试" : loginResult.message);
				GoagalInfo.loginlistener.loginError(loginErrorMsg);
			}
		}
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
			intoGameDialog.dismiss();
			if (loginResult.result) {

				if (GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.agentId)) {
					if (!GoagalInfo.userInfo.agentId.equals(GoagalInfo.agentid)) {
						GoagalInfo.agentid = GoagalInfo.userInfo.agentId;
						new ReInitInfoTaskByUserId().execute();
					}else{
						//将logo图及启动图恢复为渠道对应的值
						if (Util.getInitLogoFileBitmap(loginActivity, Constants.AGENT_LOGO_IMAGE) != null) {
							GoagalInfo.inItInfo.logoBitmp = Util.getInitLogoFileBitmap(loginActivity,Constants.AGENT_LOGO_IMAGE);
							Util.writeLaunchImageInSDCard(loginActivity, GoagalInfo.inItInfo.logoBitmp, Constants.LOGO_IMAGE);
						}
						
						if (Util.getInitLogoFileBitmap(loginActivity, Constants.AGENT_INIT_IMAGE) != null) {
							GoagalInfo.inItInfo.lunchBitmp = Util.getInitLogoFileBitmap(loginActivity,Constants.AGENT_INIT_IMAGE);
							Util.writeLaunchImageInSDCard(loginActivity, GoagalInfo.inItInfo.lunchBitmp, Constants.INIT_IMAGE);
						}
					}
				}

				GoagalInfo.isLogin = true;
				// Logger.msg("手机号-验证码方式-登录成功----");
				PreferenceUtil.getImpl(loginActivity).putBoolean(mobileNumber, false);

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

				GoagalInfo.loginType = 2;
				// 保存登录成功的登录方式，下次直接到此页面
				PreferenceUtil.getImpl(loginActivity).putInt(SystemUtil.getPhoneIMEI(loginActivity),
						GoagalInfo.loginType);

				if (!StringUtils.isEmpty(GoagalInfo.noticeMsg)) {
					noticeDialog = new NoticeDialog(loginActivity, GoagalInfo.noticeMsg);
					noticeDialog.show();

					noticeDialog.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							((Activity) loginActivity).finish();
						}
					});
				} else {
					((Activity) loginActivity).finish();
				}

			} else {

				GoagalInfo.isLogin = false;

				Logger.msg("手机号-验证码方式-登录失败----");
				Util.toast(loginActivity,
						StringUtils.isEmpty(loginResult.message) ? "手机号或密码错误，请重试" : loginResult.message);
				LoginErrorMsg errorMsg = new LoginErrorMsg(-1,
						StringUtils.isEmpty(loginResult.message) ? "手机号或密码错误，请重试" : loginResult.message);
				GoagalInfo.loginlistener.loginError(errorMsg);
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
			InitEngin initEngin = new InitEngin(loginActivity);
			return initEngin.run();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
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
				validateEt.setText("");
			} else if (resultInfo != null && resultInfo.code == HttpConfig.VALIDATE_CODE_IS_SEND) {
				Util.toast(loginActivity, resultInfo.message);
				GoagalInfo.isGetValidate = 1;
				// 清空验证码输入框
				validateEt.setText("");
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
	 * 更新短信通道号码
	 * 
	 * @author admin
	 *
	 */
	private class UpdateMtCodeTask extends AsyncTask<String, Integer, Boolean> {

		String mtype;
		String mtCode;

		public UpdateMtCodeTask(String mtype, String mtCode) {
			this.mtype = mtype;
			this.mtCode = mtCode;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			UpdateMtCodeEngin updateMtCodeEngin = new UpdateMtCodeEngin(loginActivity, mtype, mtCode);
			return updateMtCodeEngin.run();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				handler.removeCallbacks(runnable);
				break;
			case 2:
				// 设置短信验证码
				if (!StringUtils.isEmpty(smsContent)) {
					validateEt.setText(smsContent);
				}

				break;
			}
			super.handleMessage(msg);
		};
	};

	private int secondes = 60;

	private Runnable runnable;

	/**
	 * 刷新验证码
	 */
	private void codeRefresh() {

		final int borderLineBtn = MResource.getIdByName(getActivity(), "drawable", "border_line_btn");
		final int borderLineColor = MResource.getIdByName(getActivity(), "color", "border_line_color");
		final int borderLineGray = MResource.getIdByName(getActivity(), "drawable", "border_line_gray");
		final int lineColor = MResource.getIdByName(getActivity(), "color", "line_color");
		secondes = 60;
		runnable = new Runnable() {
			@Override
			public void run() {
				if (secondes-- <= 0) {
					validateBtn.setEnabled(true);
					validateBtn.setText("获取验证码");
					validateBtn.setBackgroundResource(borderLineBtn);
					validateBtn.setTextColor(getActivity().getResources().getColor(borderLineColor));
					return;
				}
				validateBtn.setEnabled(false);
				validateBtn.setText("重新发送(" + secondes + ")");
				validateBtn.setBackgroundResource(borderLineGray);
				validateBtn.setTextColor(getActivity().getResources().getColor(lineColor));
				handler.postDelayed(this, 1000);
			}
		};
		handler.postDelayed(runnable, 0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 页面销毁时，移除runnable
		Message message = new Message();
		message.what = 1;
		handler.sendMessage(message);

		// 注销广播
		unRegisterReceiver();
	}

	@Override
	public void popWindowClose() {
		if (pw_select_user != null && pw_select_user.isShowing()) {
			pw_select_user.dismiss();

			currentUserInfo = null;
			loginUserEt.setText("");
			validateEt.setText("");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(loginActivity);
		// MobclickAgent.onPageEnd("PhoneLoginFragment");
	}

	public class NoUnderlineSpan extends UnderlineSpan {

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(ds.linkColor);
			ds.setUnderlineText(false);
		}
	}
}
