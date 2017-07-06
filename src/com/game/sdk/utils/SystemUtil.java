package com.game.sdk.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class SystemUtil {

	/**
	 * 获取运营商信息 0：移动 1：联通 2：电信
	 * 
	 * @param context
	 * @return
	 */
	public static String getOperator(Context context) {
		int operatorNumber = 0;
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
				// 中国移动
				operatorNumber = 0;
			} else if (operator.equals("46001")) {
				// 中国联通
				operatorNumber = 1;
			} else if (operator.equals("46003")) {
				// 中国电信
				operatorNumber = 2;
			}
		}
		return operatorNumber + "";
	}

	public static void sendMesasge(String phone, String message) {
		SmsManager manager = SmsManager.getDefault();
		ArrayList<String> list = manager.divideMessage(message); // 因为一条短信有字数限制，因此要将长短信拆分
		for (String text : list) {
			manager.sendTextMessage(phone, null, text, null, null);
		}
	}

	public static void send2(Context context, String number, String message) {
		String SENT = "sms_sent";
		String DELIVERED = "sms_delivered";

		PendingIntent sentPI = PendingIntent.getActivity(context, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getActivity(context, 0, new Intent(DELIVERED), 0);

		context.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				Logger.msg("send======" + getResultCode());

				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Logger.msg("RESULT_OK1111");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Logger.msg("RESULT_ERROR_GENERIC_FAILURE");
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Logger.msg("RESULT_ERROR_NO_SERVICE");
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Logger.msg("RESULT_ERROR_NULL_PDU");
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Logger.msg("RESULT_ERROR_RADIO_OFF");
					break;

				case Activity.RESULT_CANCELED:
					Logger.msg("RESULT_ERROR_RADIO_OFF");
					break;
				}
			}
		}, new IntentFilter(SENT));

		SmsManager smsm = SmsManager.getDefault();
		smsm.sendTextMessage(number, null, message, sentPI, deliveredPI);
	}

	/**
	 * 获取手机IMEI码
	 */
	public static String getPhoneIMEI(Context cxt) {
		
		boolean flag = false;
		String imei = "";
		
		if(cxt == null){
			flag = true;
		}
		
		if(!flag){
			TelephonyManager tm = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();

			if (imei == null || imei.length() == 0) {
				flag = true;
			}
		}
		
		// 获取不到IMEI时，通过这个方式来获取
		if (flag) {
			imei = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
					+ Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
					+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
					+ Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
					+ Build.USER.length() % 10;
		}
		return imei;
	}

	/**
	 * 获取上行验证码
	 * 
	 * @param cxt
	 * @return
	 */
	public static String getUpValidateCode(Context cxt) {
		StringBuilder str = new StringBuilder();// 定义变长字符串
		Random random = new Random();
		// 随机生成数字，并添加到字符串
		for (int i = 0; i < 9; i++) {
			str.append(random.nextInt(10));
		}
		Logger.msg("validateCode-----" + TimeUtils.getCurrentTimeInLong() + str.toString());
		return TimeUtils.getCurrentTimeInLong() + str.toString();
	}

	/**
	 * 判断某个服务是否正在运行的方法
	 *
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
	 * @return true代表正在运行，false代表服务没有正在运行
	 */
	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(200);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {

			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

	/**
	 * 判断是否包含SIM卡
	 *
	 * @return 状态
	 */
	public static boolean hasSimCard(Context context) {

		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = telMgr.getSimState();
		boolean result = true;
		switch (simState) {
		case TelephonyManager.SIM_STATE_ABSENT:
			result = false; // 没有SIM卡
			break;
		case TelephonyManager.SIM_STATE_UNKNOWN:
			result = false;
			break;
		}

		return result;
	}

	@SuppressLint("NewApi")
	public static boolean isValidContext(Context ctx) {
		Activity activity = (Activity) ctx;

		if (Build.VERSION.SDK_INT > 17) {
			if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
				return false;
			} else {
				return true;
			}
		} else {
			if (activity == null || activity.isFinishing()) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
