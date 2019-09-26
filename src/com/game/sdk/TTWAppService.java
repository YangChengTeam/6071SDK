package com.game.sdk;

import java.util.List;

import com.game.sdk.domain.ChannelMessage;
import com.game.sdk.domain.DeviceMsg;
import com.game.sdk.domain.PayWay;
import com.game.sdk.domain.UserInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;

/**
 * author janecer 2014年7月22日上午9:46:00 sdk系统核心类
 */
public class TTWAppService extends Service {
	public static UserInfo userinfo;
	public static String gameid;
	public static String appid;
	public static String agentid;
	public static String service_tel;
	public static String service_qq;
	public static int ttbrate;
	public static String notice;
	public static String ptbkey;
	public static String logo;
	public static Bitmap logpBitmap;
	public static String ttb;   //平台币
	public static String gttb;  //游戏币
	public static DeviceMsg dm;// 设备信息
	public static List<PayWay> channels;
	public static String tips;
	public static boolean isLogin = false;// 判断用户是否登录
	public static int isgift = 1;// 判断用户是否登录
	private static Context actx;
	public static String publicKey;
	public static int code;
	public static int badge;
	public static int debug = 1;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public static void startService(Context ctx) {
		if (!isServiceRunning(ctx)) {
			actx = ctx;
			Intent intent_service = new Intent(ctx,TTWAppService.class);
			intent_service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.startService(intent_service);
		}
	}
		
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String succ = "";
		try {
			succ = intent.getStringExtra("login_success");
		}catch (Exception e) {
			succ = "";
			//e.printStackTrace();
		}
		handCommand();
		return START_STICKY;
	}
	
	private void handCommand() {
		Notification notification = new Notification(0, "",
				System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		//notification.setLatestEventInfo(this, "", "", contentIntent);
		startForeground(0, notification);
	}

	/**
	 * 用来判断当前服务类是否在运行
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isServiceRunning(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runServices = am
				.getRunningServices(Integer.MAX_VALUE);
		if (null == runServices || 0 == runServices.size()) {
			return false;
		}
		for (int i = 0; i < runServices.size(); i++) {
			if (runServices.get(i).service.getClassName()
					.equals(TTWAppService.class.getName())) {
				return true;
			}
		}
		return false;
	}
}
