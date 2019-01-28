package com.game.sdk;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LoginResult;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.engin.InitEngin;
import com.game.sdk.engin.LoginEngin;
import com.game.sdk.floatwindow.FloatViewImpl;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.net.impls.OKHttpRequest;
import com.game.sdk.ui.InitActivity;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.ui.LoginoutActivity;
import com.game.sdk.ui.PayActivity;
import com.game.sdk.ui.QuickLoginActivity;
import com.game.sdk.utils.AccountInfoUtil;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.CrashHandler;
import com.game.sdk.utils.EmulatorCheckUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.MobileInfoUtil;
import com.game.sdk.utils.NetworkImpl;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.ThreadPoolManager;
import com.game.sdk.utils.Util;
import com.game.sdk.view.AutoNoticeDialog;
import com.game.sdk.view.CustomDialog;
import com.game.sdk.view.LoginInDialog;
import com.ss.android.common.applog.TeaAgent;
import com.ss.android.common.applog.TeaConfigBuilder;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * author zhangkai 2016年7月22日上午9:45:18
 */
public class FYGameSDK {

	private static FYGameSDK instance;
	private Activity acontext;
	private Runnable switchCallBack;
	private Runnable alogout;
	private boolean isOpenLogout = false;
	private boolean isInitOk;

	private String initMsg = "正在初始化";

	OnSDKInitListener initCallback;

	private InitCloseListener closeListener;

	private LoginInDialog autoLoginDialog;

	Handler handler = new Handler();

	public interface InitCloseListener {
		public void initClose();
	}

	public void setCloseListener(InitCloseListener closeListener) {
		this.closeListener = closeListener;
	}

	private FYGameSDK() {

	}

	/**
	 * 预初始化相关数据
	 */
	private void preInit() {

		Logger.msg("preInit -----");

		GoagalInfo.isLogin = false;// 默认设置用户未登录
		GoagalInfo.userInfo = null;

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(acontext, "");

		GoagalInfo.imei = SystemUtil.getPhoneIMEI(acontext);
		GoagalInfo.validateCode = SystemUtil.getUpValidateCode(acontext);

		Util.getGameInfo(acontext);
		// GoagalInfo.setGoagalInfo(acontext, PathUtil.getGolgalDir());
		OKHttpRequest.initPicasso(acontext);// 初始化picasso
		
		//暂时去掉游戏“加速”功能
		//Speed.load(acontext);
		
		//动态设置渠道信息
		String appName = SystemUtil.getAppName(acontext) != null ? SystemUtil.getAppName(acontext) : GoagalInfo.getPackageInfo(acontext).packageName;
		
		Logger.msg("appName--->" + appName);
		
		// 友盟统计初始化
		UMAnalyticsConfig umConfig = new UMAnalyticsConfig(acontext, "5879b43ca325113edf0010f8", appName + "---" + GoagalInfo.agentid,
				EScenarioType.E_UM_NORMAL, true);
		MobclickAgent.startWithConfigure(umConfig);
		MobclickAgent.setScenarioType(acontext, EScenarioType.E_UM_NORMAL);
		MobclickAgent.openActivityDurationTrack(false);//禁止默认的页面统计方式，这样将不会再自动统计Activity
		
		TeaAgent.setUserUniqueID(SystemUtil.getPhoneIMEI(acontext));
        TeaAgent.init(TeaConfigBuilder.create(acontext)
                .setAppName(GoagalInfo.teaAppName)
                .setChannel(GoagalInfo.agentid)
                .setAid(Integer.valueOf(GoagalInfo.teaAppId))
                .createTeaConfig());
        Logger.msg("TeaAgent Init");
	}

	/**
	 * 初始化相关数据
	 */
	public void init() {

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				
				InitEngin initEngin = new InitEngin(acontext);

				boolean result = initEngin.run();

				if (result) {
					acontext.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeListener.initClose();
							if (GoagalInfo.inItInfo != null) {
								isInitOk = true;
								initCallback.initSuccess();
							} else {
								isInitOk = false;
								initCallback.initFailure();
							}
						}
					});
				} else {
					acontext.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeListener.initClose();
							isInitOk = false;
							initCallback.initFailure();
						}
					});
				}

				// 是否初始化成功
				Logger.msg("fy game sdk result---" + isInitOk);
			}
		});
	}

	private void reinit(final OnSDKInitListener initCallback, final Context context, final boolean isShowQuikLogin,
			final OnLoginListener loginlistener) {

		final CustomDialog reinitDialog = new CustomDialog(context, "重新初始化");
		reinitDialog.show();

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				// 预初始化
				// preInit();

				InitEngin initEngin = new InitEngin(acontext);

				boolean result = initEngin.run();

				reinitDialog.dismiss();

				if (result) {
					if (GoagalInfo.inItInfo != null) {
						isInitOk = true;
					} else {
						isInitOk = false;
					}

					if (isInitOk) {
						acontext.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								login(context, isShowQuikLogin, loginlistener);
							}
						});
					}
				} else {
					Util.toast(acontext, "重新初始化失败，请稍后重试!");
				}
			}
		});
	}

	/**
	 * 初始化相关数据（不带初始化回调）
	 * 
	 * @param context
	 *            Activity
	 * @param logoutCallback
	 *            游戏切换或退出回调
	 */
	public void initSDK(final Activity context, Runnable logoutCallback) {
		acontext = context;
		alogout = logoutCallback;
		init();
	}
	
	/**
	 * 初始化相关数据（带初始化回调）
	 * 
	 * @param context
	 *            Activity
	 * @param initCallback
	 *            初始化结束回调
	 * @param logoutCallback
	 *            退出游戏回调
	 */
	public void initSDK(final Activity context, OnSDKInitListener initCallback, Runnable logoutCallback) {
		initSDK(context,initCallback,null,logoutCallback);
	}
	
	/**
	 * 初始化相关数据（带初始化回调）
	 * 
	 * @param context
	 *            Activity
	 * @param initCallback
	 *            初始化结束回调
	 * @param switchAccountCallback
	 *            游戏切换账号回调
	 * @param logoutCallback
	 *            游戏退出回调
	 */
	public void initSDK(final Activity context, OnSDKInitListener initCallback,Runnable switchAccountCallback, Runnable logoutCallback) {
		if(isInitOk){
			return;
		}
		
		GoagalInfo.tempActivity = context;
		acontext = context;
		switchCallBack = switchAccountCallback;
		alogout = logoutCallback;
		GoagalInfo.initActivity = context;
		GoagalInfo.loginoutRunnable = logoutCallback;
		this.initCallback = initCallback;
		
		//预初始化(移到此处预初始化，2017.5.12修改)
		preInit();
		
		/*Intent intent = new Intent(context, InitActivity.class);
		context.startActivity(intent);*/
		
		Intent intent = new Intent(context, InitActivity.class);
		context.startActivity(intent);
		context.overridePendingTransition(MResource.getIdByName(context, "anim", "fysdk_init_enter"), MResource.getIdByName(context, "anim", "fysdk_init_exit"));
	}
	
	
	/**
	 * 获取游戏SDK单例
	 * 
	 * @return 返回游戏SDK单例
	 */
	public static FYGameSDK defaultSDK() {
		if (null == instance) {
			instance = new FYGameSDK();
			Logger.msg("6071GameSDK版本号：" + instance.getVersion() + " " + (ServerConfig.DEBUG ? "测试版" : "正式版"));
		}
		return instance;
	}

	/**
	 * 登录
	 * 
	 * @param context
	 *            Activity
	 * @param isShowQuikLogin
	 *            是否快速登录
	 * @param loginlist
	 *            登录回调监听器
	 *
	 */
	public void login(Context context, boolean isShowQuikLogin, OnLoginListener loginlistener) {
		if (GoagalInfo.isLogin) {
			// recycle();
			// 如果登录过了，就不需要点击，直接返回
			return;
		}

		if (!isInitOk()) {
			initMsg = "重新初始化...";
			reinit(null, context, isShowQuikLogin, loginlistener);
			return;
		}

		if (!NetworkImpl.isNetWorkConneted(context)) {
			Util.toast(acontext, "网络不给力，请检查网络设置");
			return;
		}
		
		//第一步:如果安装过游戏盒子,从游戏盒子/或者本地的数据库文件中获取当前登录用户信息
		/*boolean isFirst = PreferenceUtil.getImpl(context).getBoolean(Constants.isFirstAccount, true);
		if(isFirst){
	        try{
	        	ContentResolver contentResolver = context.getContentResolver();
		        Uri uri = Uri.parse("content://com.sdk.rpc.provide/gamesdk");
		        
	        	 Bundle gameBoxBundle = contentResolver.call(uri, "search_login", null, null);
	             if(gameBoxBundle != null){
	             	
	             	GoagalInfo.userInfo = new UserInfo();
	             	
	             	String userName = !StringUtils.isEmpty(gameBoxBundle.getString("phone")) ? gameBoxBundle.getString("phone") : gameBoxBundle.getString("username");
	             	
	             	if(!StringUtils.isEmpty(userName)){
	             		GoagalInfo.userInfo.username = userName;
	             		GoagalInfo.userInfo.password = gameBoxBundle.getString("password");
	             		GoagalInfo.userInfo.accountType = 0;//账号合并，此处已经不需要
	             		GoagalInfo.loginType = 2;
	             		
	             		AccountInfoUtil.insertUserInfoFromPublic(context, GoagalInfo.userInfo);
	             		
	             	}else{
	             		GoagalInfo.userInfo = null;
	             	}
	             	
	             }
	        }catch(Exception e){
	        	Logger.msg("ContentResolver error--->");
	        	e.printStackTrace();
	        }
	        
	        PreferenceUtil.getImpl(context).putBoolean(Constants.isFirstAccount, false);
		}*/
		
        //第二步:从本地保存的账户中获取一个账号
        //if(GoagalInfo.userInfo == null){
    	GoagalInfo.loginType = PreferenceUtil.getImpl(context).getInt(SystemUtil.getPhoneIMEI(context), 0);
    	
		UserInfo currentUserInfo = getLastUserInfo();
		if (currentUserInfo != null) {
			GoagalInfo.userInfo = currentUserInfo;
			GoagalInfo.loginType = 2;
		}
        //}
        
		// 设置登录监听
		GoagalInfo.loginlistener = loginlistener;

		boolean isAutoLogin = PreferenceUtil.getImpl(acontext).getBoolean(Constants.isAutoLogin, true);

		if (GoagalInfo.userInfo != null && isAutoLogin) {
			PreferenceUtil.getImpl(context).putBoolean(Constants.isAutoLogin, true);
			
			GoagalInfo.isChangeAccount = false;
			autoLoginDialog = new LoginInDialog(acontext, GoagalInfo.userInfo.username);
			autoLoginDialog.setCanceledOnTouchOutside(false);
			autoLoginDialog.show();

			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					new LoginTask().execute();
				}
			}, 2000);// 延时3秒执行，便于用户切换账号

		} else {
			Intent login_int = null;

			if (GoagalInfo.loginType == 0) {
				GoagalInfo.isEmulator = EmulatorCheckUtil.isEmulator();
				if (GoagalInfo.isEmulator) {
					GoagalInfo.isQuick = 0;
					login_int = new Intent(context, LoginActivity.class);
				} else {
					
					if(GoagalInfo.userInfo != null){
						GoagalInfo.isQuick = 0;
						login_int = new Intent(context, LoginActivity.class);
					}else{
						// is_mqr : 1：开，0：关
						if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isMqr == 1) {
							GoagalInfo.isQuick = 1;
							login_int = new Intent(context, QuickLoginActivity.class);
						} else {
							GoagalInfo.isQuick = 0;
							login_int = new Intent(context, LoginActivity.class);
						}
					}
				}
			}
			
			if (GoagalInfo.loginType == 2) {
				GoagalInfo.isQuick = 0;
				login_int = new Intent(context, LoginActivity.class);
			}
			
			login_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(login_int);
		}
	}

	/**
	 * 自动登录时会调用
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
			LoginEngin loginEngin = new LoginEngin(acontext);
			return loginEngin.run();
		}

		@Override
		protected void onPostExecute(LoginResult loginResult) {
			super.onPostExecute(loginResult);

			if (autoLoginDialog != null && autoLoginDialog.isShowing()) {
				autoLoginDialog.dismiss();
			}
			if (!GoagalInfo.isChangeAccount) {
				if (loginResult.result) {
					Logger.msg("登录成功----");
					GoagalInfo.isLogin = true;
					// Util.toast(acontext, "登录成功");
					
					if(GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.agentId)){
						if(!GoagalInfo.userInfo.agentId.equals(GoagalInfo.agentid)){
							GoagalInfo.agentid = GoagalInfo.userInfo.agentId;
							new ReInitInfoTaskByUserId().execute();
						}
					}
					
					if (!StringUtils.isEmpty(GoagalInfo.noticeMsg)) {
						AutoNoticeDialog noticeDialog = new AutoNoticeDialog(acontext, GoagalInfo.noticeMsg);
						noticeDialog.show();
						noticeDialog.setOnDismissListener(new OnDismissListener() {

							@Override
							public void onDismiss(DialogInterface dialog) {
								loginSuccess();
							}
						});
					} else {
						loginSuccess();
					}
				} else {
					GoagalInfo.isLogin = false;
					Logger.msg("登录失败----");
					LoginErrorMsg loginErrorMsg = new LoginErrorMsg(-1,
							StringUtils.isEmpty(loginResult.message) ? "账号或密码错误，请重试" : loginResult.message);
					GoagalInfo.loginlistener.loginError(loginErrorMsg);
				}
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
			InitEngin initEngin = new InitEngin(acontext);
			return initEngin.run();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}
	
	public void loginSuccess() {

		LogincallBack logincallBack = new LogincallBack();
		
		if(GoagalInfo.userInfo.newSdkReg == 0){
			logincallBack.username = GoagalInfo.userInfo.username;
			if(GoagalInfo.userInfo.cpNotice == 0 && !StringUtils.isEmpty(GoagalInfo.userInfo.fixName)){
				logincallBack.username = GoagalInfo.userInfo.fixName;
			}
		} else {
			logincallBack.username = GoagalInfo.userInfo.userId;
		}
		
		logincallBack.userId = GoagalInfo.userInfo.userId;
		logincallBack.isBindPhone = GoagalInfo.userInfo.validateMobile == 1 ? true : false;
		logincallBack.logintime = GoagalInfo.userInfo.logintime;
		logincallBack.sign = GoagalInfo.userInfo.sign;

		GoagalInfo.loginlistener.loginSuccess(logincallBack);

		// 保存登录成功的登录方式，下次直接到此页面
		PreferenceUtil.getImpl(acontext).putInt(SystemUtil.getPhoneIMEI(acontext), GoagalInfo.loginType);
	}

	/**
	 * 自动登录时，根据获取到的用户，取用户的所有信息
	 * 
	 * @param userName
	 * @return
	 */
	private UserInfo getUserInfoByUserName(String userName) {
		UserInfo userInfo = null;
		try {
			/*
			 * String userInfoStr =
			 * PreferenceUtil.getImpl(acontext).getString(SystemUtil.
			 * getPhoneIMEI(acontext)+userName, ""); if
			 * (!StringUtils.isEmpty(userInfoStr)) { userInfo =
			 * JSON.parseObject(userInfoStr, UserInfo.class); }
			 */

		} catch (Exception e) {
			Logger.msg(e.getMessage());
		}
		return userInfo;
	}

	public UserInfo getLastUserInfo() {
		UserInfo lastUserInfo = null;
		
		List<UserInfo> list = AccountInfoUtil.loadAllUserInfo(acontext);
		
		if(list == null){
			list = new ArrayList<UserInfo>();
		}
		
		//合并账号，只读取一次
		boolean isRead = PreferenceUtil.getImpl(acontext).getBoolean(Constants.isReadLastVersion, false);
		if(!isRead){
			List<UserInfo> lastVersionPhoneList = MobileInfoUtil.loadAllUserInfo(acontext);
			if(lastVersionPhoneList != null && lastVersionPhoneList.size() > 0){
				if(list.size() == 0){
					for (int i = 0; i < lastVersionPhoneList.size(); i++) {
						UserInfo _userInfo = lastVersionPhoneList.get(i);
						list.add(0,_userInfo);
						AccountInfoUtil.insertUserInfo(acontext, _userInfo);
					}
				}else{
					for (int i = 0; i < lastVersionPhoneList.size(); i++) {
						UserInfo _userInfo = lastVersionPhoneList.get(i);
						
						int len = list.size();
						for(int j = 0;j < len;j++){
							if(!StringUtils.isEmpty(_userInfo.username) && !_userInfo.username.equals(list.get(j).username)){
								list.add(0,_userInfo);
								AccountInfoUtil.insertUserInfo(acontext, _userInfo);
							}
						}
					}
				}
			}
			PreferenceUtil.getImpl(acontext).putBoolean(Constants.isReadLastVersion, true);
			
			//合并2.2之前的版本，第一次读取时，清除主页模块的缓存
			
			PreferenceUtil.getImpl(acontext).putString(ServerConfig.MAIN_MODULE_URL, "");
		}
		
		if (list != null && list.size() > 0) {
			lastUserInfo = list.get(0);
		} else {
			lastUserInfo = UserLoginInfodao.getInstance(acontext).getUserInfoLastByType();
		}
		
		return lastUserInfo;
	}

	/**
	 * 显示充值(含回调地址)
	 *
	 * @param context
	 *            Activity
	 * @param roleid
	 *            角色id
	 * @param money
	 *            充值金额
	 * @param serverid
	 *            服务器id
	 * @param productname
	 *            游戏名称 例如【诛仙-3阶成品天琊】
	 * @param productdesc
	 *            产品描述
	 * @param fcallbackurl
	 *            回调地址，此处可不填，由后台配置
	 * @param attach
	 *            拓展参数【若有自定义参数传递】
	 * @param paymentListener
	 *            充值回调监听
	 **/
	@Deprecated
	public void pay(Context context, String roleid, String money, String serverid, String productname,
			String productdesc, String fcallbackurl, String attach, OnPaymentListener paymentListener) {
		pay(context, roleid, money, serverid, productname, productdesc, attach, paymentListener);
	}

	/**
	 * 显示充值(不含回调地址)
	 *
	 * @param context
	 *            Activity
	 * @param roleid
	 *            角色id
	 * @param money
	 *            充值金额
	 * @param serverid
	 *            服务器id
	 * @param productname
	 *            游戏名称 例如【诛仙-3阶成品天琊】
	 * @param productdesc
	 *            产品描述
	 * @param attach
	 *            拓展参数【若有自定义参数传递】
	 * @param paymentListener
	 *            充值回调监听
	 **/
	public void pay(Context context, String roleid, String money, String serverid, String productname,
			String productdesc, String attach, OnPaymentListener paymentListener) {
		if (!NetworkImpl.isNetWorkConneted(context)) {
			Util.toast(acontext, "网络不给力，请检查网络设置");
			return;
		}

		if (!GoagalInfo.isLogin) {
			Util.toast(acontext, "请先登录");
			return;
		}

		if (null == money || "".equals(money) || !NumberUtils.isNumber(money)) {
			Util.toast(acontext, "请输入金额，金额为数字");
			return;
		}

		GoagalInfo.paymentListener = paymentListener;

		float moneys = Float.parseFloat(money);

		Intent pay_int = new Intent(context, PayActivity.class);
		PayActivity.paymentListener = paymentListener;
		pay_int.putExtra("roleid", roleid);
		pay_int.putExtra("money", moneys);
		pay_int.putExtra("serverid", serverid);
		pay_int.putExtra("productname", productname);
		pay_int.putExtra("productdesc", productdesc);
		pay_int.putExtra("fcallbackurl", "");
		pay_int.putExtra("attach", attach);
		pay_int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(pay_int);
		instance.removeFloatButton();
	}

	/**
	 * 退出游戏(回收资源)
	 */
	public void exitSDK() {

		// recycle();

		Intent intent = new Intent(acontext, LoginoutActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		acontext.startActivity(intent);
	}

	/**
	 * 切换帐号(回收资源)
	 */
	public void switchUser() {
		recycle(1);
		
		//TODO 此处需求待定，切换账号时，SDK中只注销资源，不跳转到登录界面，游戏收到回调后做可跳转到登录操作界面
		if(!isOpenLogout()){
			Intent intent = new Intent(acontext, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			acontext.startActivity(intent);
		}
	}
	
	/**
	 * 显示悬浮按钮
	 */
	public void createFloatButton() {
		if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
			TeaAgent.onResume(acontext);
			Logger.msg("TeaAgent onResume");
		}
		if (!GoagalInfo.isLogin) {
			return;
		}
		
		boolean isShow = PreferenceUtil.getImpl(acontext).getBoolean("IS_SHOW_OPEN_WINDOW", true);
		if(isShow && Build.VERSION.SDK_INT >= 24){
			acontext.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(acontext, "请手动开启悬浮窗权限",Toast.LENGTH_LONG).show();
				}
			});
			PreferenceUtil.getImpl(acontext).putBoolean("IS_SHOW_OPEN_WINDOW", false);
		}
		
		Logger.msg("悬浮按钮启动");
		FloatViewImpl.getInstance(acontext).ShowFloat();
	}

	/**
	 * 移除悬浮按钮
	 */
	public void removeFloatButton() {
		// if (GoagalInfo.isLogin) {
		Logger.msg("移除悬浮按钮");
		if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
			TeaAgent.onPause(acontext);
			Logger.msg("TeaAgent onPause");

		}
		FloatViewImpl.getInstance(acontext).removeFloat();
		// }
	}

	/**
	 * 
	 * @param type
	 * 1.切换账号
	 * 2.退出游戏
	 */
	public void recycle(int type) {

		Logger.msg("回收资源");
		
		if (switchCallBack != null && type == 1) {
			switchCallBack.run();
		}
		
		if (alogout != null  && type == 2) {
			alogout.run();
		}
		
		removeFloatButton();

		GoagalInfo.userInfo = null;
		GoagalInfo.isLogin = false;
	}

	/**
	 * 返回游戏SDK版本号
	 * 
	 * @return 返回游戏SDK版本号
	 */
	public String getVersion() {
		return "2.3.3";
	}

	/**
	 * 打开注销功能
	 * 
	 * @return 打开注销功能
	 */
	public void openLogout() {
		if (alogout != null) {
			isOpenLogout = true;
		}
	}

	/**
	 * 返回是否打开注销功能
	 * 
	 * @return 返回是否打开注销功能
	 */
	public boolean isOpenLogout() {
		return isOpenLogout;
	}

	/**
	 * 返回是否初始化成功
	 * 
	 * @return 返回是否初始化成功
	 */
	public boolean isInitOk() {
		return isInitOk;
	}

	public void setInitOk(boolean isInitOk) {
		this.isInitOk = isInitOk;
	}

}
