package com.game.sdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LoginResult;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.engin.InitEngin;
import com.game.sdk.engin.LoginEngin;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.ui.adapter.UserLoginListAdapter;
import com.game.sdk.ui.adapter.UserLoginListAdapter.CloseListener;
import com.game.sdk.utils.AccountInfoUtil;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.EmulatorCheckUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.NetworkImpl;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.game.sdk.view.LoginInDialog;
import com.game.sdk.view.NoticeDialog;
import com.ss.android.common.lib.EventUtils;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * 账号密码登录主界面
 * 
 * @author admin
 *
 */
public class AccountLoginFragment extends BaseFragment implements OnClickListener, CloseListener {

	LoginActivity loginActivity;

	private LayoutInflater inflater;

	private LinearLayout accountLoginLayout;

	private LinearLayout quickRegisterLayout;

	/**
	 * 切换登录方式
	 */
	private TextView changeAccountTv;
	/**
	 * 快速注册
	 */
	private TextView quickRegisterTv;

	private EditText userNameEt;

	private EditText passWordEt;

	private Button intoGameBtn;

	private ImageView titleLogo;

	private RelativeLayout bgLayout;

	private LinearLayout titleLayout;

	private LinearLayout serverLayout;

	private ImageView registerIv;

	private LinearLayout moreAccountLayout;

	private TextView forgetTv;

	private TextView serviceTelTv;

	private TextView serviceQQTv;

	private UserInfo userinfoSelect;// 用户选择用户的时候 赋予的用户信息

	private UserInfo currentUserInfo;

	CustomDialog intoGameDialog;

	private PopupWindow pw_select_user;

	private UserLoginListAdapter pw_adapter;

	private List<UserInfo> userLoginInfos;

	NoticeDialog noticeDialog;

	LoginInDialog autoLoginDialog;

	Handler handler = new Handler();

	private List<UserInfo> userList;

	@Override
	public String getLayoutId() {
		return "fysdk_account_login_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		loginActivity = (LoginActivity) getActivity();
		intoGameDialog = new CustomDialog(loginActivity, "正在进入游戏");

		changeAccountTv = findTextViewByString("change_account_tv");
		quickRegisterTv = findTextViewByString("quick_register_tv");
		accountLoginLayout = (LinearLayout) findViewByString("account_login_layout");
		quickRegisterLayout = (LinearLayout) findViewByString("quick_register_layout");
		userNameEt = findEditTextByString("user_name_et");
		passWordEt = findEditTextByString("password_et");
		intoGameBtn = findButtonByString("into_game_btn");

		titleLogo = findImageViewByString("account_login_logo");
		bgLayout = (RelativeLayout) findViewByString("bg_layout");
		titleLayout = (LinearLayout) findViewByString("common_title_layout");
		serverLayout = (LinearLayout) findViewByString("service_number_layout");
		registerIv = findImageViewByString("register_icon");

		forgetTv = findTextViewByString("forget_tv");
		moreAccountLayout = (LinearLayout) findViewByString("more_account_layout");

		serviceTelTv = findTextViewByString("service_tel_tv");
		serviceQQTv = findTextViewByString("service_qq_tv");

		changeAccountTv.setOnClickListener(this);
		quickRegisterLayout.setOnClickListener(this);
		intoGameBtn.setOnClickListener(this);
		moreAccountLayout.setOnClickListener(this);
		forgetTv.setOnClickListener(this);
		serviceTelTv.setOnClickListener(this);
		serviceQQTv.setOnClickListener(this);
		userNameEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (userNameEt.getText().toString().length() == 0) {
					passWordEt.setText("");
				}
			}
		});

	}

	/**
	 * 读取用户信息
	 */
	public List<UserInfo> getCommonUserInfosByType() {

		List<UserInfo> list = AccountInfoUtil.loadAllUserInfo(loginActivity);
		
		if (list == null || list.size() == 0) {
			list = UserLoginInfodao.getInstance(loginActivity).getUserLoginInfoByType();

			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i) != null) {
						AccountInfoUtil.insertUserInfo(loginActivity, list.get(i));
					}
				}
			}
		}
		return list;
	}

	public UserInfo getLastUserInfo() {
		UserInfo lastUserInfo = null;

		List<UserInfo> list = userList;

		if (list != null && list.size() > 0) {
			lastUserInfo = list.get(0);
		} else {
			lastUserInfo = UserLoginInfodao.getInstance(loginActivity).getUserInfoLastByType();
		}
		return lastUserInfo;
	}

	@Override
	public void initData() {
		super.initData();

		initTheme();

		inflater = (LayoutInflater) loginActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		userList = getCommonUserInfosByType();

		// 读取用户信息
		currentUserInfo = getLastUserInfo();

		if (currentUserInfo != null) {

			if (AccountInfoUtil.getUserInfoByName(loginActivity, currentUserInfo.username) != null) {
				GoagalInfo.userInfo = AccountInfoUtil.getUserInfoByName(loginActivity, currentUserInfo.username);
			} else {
				GoagalInfo.userInfo = currentUserInfo;
			}

			userNameEt.setText(GoagalInfo.userInfo.username);
			passWordEt.setText(GoagalInfo.userInfo.password);
		} else {
			GoagalInfo.userInfo = null;
		}

		if (GoagalInfo.inItInfo != null) {

			if (!StringUtils.isEmpty(GoagalInfo.inItInfo.tel)) {
				serviceTelTv.setText("客服电话：" + GoagalInfo.inItInfo.tel);
			} else {
				serviceTelTv.setText("客服电话：400-796-6071");
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
		MobclickAgent.onPageStart("AccountLoginFragment");
		//MobclickAgent.onResume(loginActivity);
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {

		if (GoagalInfo.inItInfo != null) {

			if (GoagalInfo.inItInfo.logoBitmp != null) {
				titleLogo.setImageBitmap(GoagalInfo.inItInfo.logoBitmp);
			}

			if (GoagalInfo.inItInfo.template != null) {

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
					
					// 快速注册按钮颜色
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
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("change_account_tv")) {
			GoagalInfo.loginType = 2;
			loginActivity.changeFragment(1);
			return;
		}
		
		if (v.getId() == findIdByString("service_qq_tv")) {
			GoagalInfo.qqKefuFrom = 0;
			loginActivity.changeFragment(5);
			return;
		}
		
		if (v.getId() == findIdByString("quick_register_layout")) {
			loginActivity.changeFragment(3);
			return;
		}
		if (v.getId() == findIdByString("into_game_btn")) {
			if (!NetworkImpl.isNetWorkConneted(loginActivity)) {
				Util.toast(loginActivity, "网络不给力，请检查网络设置");
				return;
			}

			String username = userNameEt.getText().toString().trim();
			String password = passWordEt.getText().toString().trim();

			if (TextUtils.isEmpty(username)) {
				Util.toast(loginActivity, "请输入账号");
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

			// 账号+密码方式登录
			new LoginTask().execute();
		}
		if (v.getId() == findIdByString("more_account_layout")) {
			if (pw_select_user != null && pw_select_user.isShowing()) {
				pw_select_user.dismiss();
			} else {

				userList = getCommonUserInfosByType();// 重新查询一次

				userLoginInfos = new ArrayList<UserInfo>();
				userLoginInfos.addAll(userList);

				if (null == userLoginInfos) {
					return;
				}
				if (null == pw_adapter) {
					pw_adapter = new UserLoginListAdapter(loginActivity, userLoginInfos, 1);
				}
				pw_adapter.setCloseListener(this);

				int pwidth = (int) (accountLoginLayout.getWidth() * 0.85);

				// int pwidth = DimensionUtil.dip2px(loginActivity, 340);

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
							userNameEt.setText(userinfoSelect.username);
							passWordEt.setText(userinfoSelect.password);
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
		if (v.getId() == findIdByString("forget_tv")) {
			loginActivity.changeFragment(6);
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
	 * (账号+密码方式)登录
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

			if (autoLoginDialog != null && autoLoginDialog.isShowing()) {
				autoLoginDialog.dismiss();
			}

			if (intoGameDialog != null && intoGameDialog.isShowing()) {
				intoGameDialog.dismiss();
			}

			if (loginResult.result) {
				if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
					EventUtils.setLogin("normal_login",true);
				}
				
				Logger.msg("登录成功----");

				if(GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.agentId)){
					if(!GoagalInfo.userInfo.agentId.equals(GoagalInfo.agentid)){
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
				
				if (GoagalInfo.userInfo != null) {
					GoagalInfo.isLogin = true;
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
				} else {
					GoagalInfo.isLogin = false;
				}

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
				if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
					EventUtils.setLogin("normal_login",false);
				}
				Logger.msg("登录失败----");

				Util.toast(loginActivity,
						StringUtils.isEmpty(loginResult.message) ? "服务器异常，请重试" : loginResult.message);

				LoginErrorMsg loginErrorMsg = new LoginErrorMsg(-1,
						StringUtils.isEmpty(loginResult.message) ? "服务器异常，请重试" : loginResult.message);
				GoagalInfo.loginlistener.loginError(loginErrorMsg);

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
	
	@Override
	public void popWindowClose() {
		if (pw_select_user != null && pw_select_user.isShowing()) {
			pw_select_user.dismiss();

			currentUserInfo = null;
			userNameEt.setText("");
			passWordEt.setText("");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("AccountLoginFragment");
		//MobclickAgent.onPause(loginActivity);
	}

	public class NoUnderlineSpan extends UnderlineSpan {

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(ds.linkColor);
			ds.setUnderlineText(false);
		}
	}
}
