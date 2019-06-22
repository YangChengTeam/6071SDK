package com.game.sdk.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.game.sdk.utils.Logger;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;

/**
 * Created by zhangkai on 16/9/19.
 */
public class GoagalInfo {
	public static final String TAG = "6071Box";

	public static String publicKey = "-----BEGIN PUBLIC KEY-----"
			+ "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEApsmOWfJecgy0Al55X8hG"
			+ "C/HMaZQ4WdVW99szOmE5tuVl342fmL/Vm/g2sJSc6AaowNnZrl1Rt9UrWnjVLhSL"
			+ "mpChMI2F+r5XAJL+Z3+xeKetejpia2OWslO+sT+YBWm2ilb+ZOdJ8Ms3dDBndvuA"
			+ "rgNRPwbW8Mxh2LH4mFy2ivYjw7mXgWNzXIAlUT4i9AaoqmX/Osr7l6+VdXi6uLlR"
			+ "JIEZC+A7KPL35iXrBfPCgMn9QennaFvzLPDn7R4kCkgjo6rF6SdeMDq/eUFwr2bM"
			+ "wfjIO2QfGtF+nv3Yg6i2HKMwZde2N+vvjxxCdupYfHHVhbBOEwPreR9tvEiDGGDf"
			+ "ERwURh7gL5L5adBa6AtqtaKRuwtWcnOAueTkVh7nP6Gt8J253mqhp2upu4pJaKfz"
			+ "Q1RvczAr93tmA1XkUQS3RkMEuqocay/u3iiSKwJob9Oh+fJC8CgKL4hOtIK1VRGu"
			+ "QPc7BBQtLWxmOoTZstLoC6oVS6BoQTk/hQ3STtyJqHuoqi5Cu/e2Fioi4bc0ViNJ"
			+ "R67sFHlN5rQSdXWS4ouR6zQK4yAGVSCUlFznx2Jsu3slABijPUrNP3RkP91qvC3/"
			+ "7osOB5lbJsZNgygHcy3VylwlQ/EqXEnNrCwvCPSWgIJJ0KgScEttHd1FVEmntNJd" + "MU6CKowXCLCWhtMt9AysiDkCAwEAAQ=="
			+ "-----END PUBLIC KEY-----";
	
	public static boolean isLogin = false;// 判断用户是否登录
	
	public static String channel = "3";
	public static ChannelInfo channelInfo = null;
	public static InItInfo inItInfo = null;
	public static PackageInfo packageInfo = null;
	public static UserInfo userInfo = null;
	public static int loginType = 0;// 0.未注册, 1.极速注册成功，2.手机号登录/账号密码登录（已合并）,3.注册页面,4.极速试玩登录

	public static String gameid;
	public static String appid;
	public static String agentid;
	public static String imei;
	public static String fromId;
	
	public static String teaAppId;
	public static String teaAppName;
	
	public static String validateCode;// 上行验证码

	public static OnLoginListener loginlistener;// 登录的监听

	public static OnPaymentListener paymentListener;// 登录的监听

	public static int isQuick = 0; // 是否快速登录（1：是，0：否）

	public static int isGetValidate = 0;// 是否获取过登录验证码(0:否，1：是)

	public static String noticeMsg = "";

	public static List<CouponInfo> couponList;
	
	public static int couponCount;
	
	public static int vertical = 0;//0横 1竖
	
	public static Runnable loginoutRunnable;
	
	public static Activity initActivity;

	public static boolean isChangeAccount = false;
	
	public static boolean isEmulator = false;
	
	public static int qqKefuFrom = 0;//qq客服界面来源，0从登陆界面进入，1从注册页面进入
	
	public static int bindMobileFrom = 0;//绑定手机界面来源,0从主界面进入，1，从账号安全界面进入
	
	public static Activity tempActivity;
	
	/// < 获取渠道信息
	/*public static void setGoagalInfo(Context context, String dir) {
		String result1 = null;
		String result2 = null;
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		ZipFile zf = null;
		try {
			zf = new ZipFile(sourceDir);
			ZipEntry ze1 = zf.getEntry("META-INF/gamechannel.json");
			InputStream in1 = zf.getInputStream(ze1);
			result1 = FileUtil.readString(in1);

			Logger.msg("渠道->" + result1);

			ZipEntry ze2 = zf.getEntry("META-INF/rsa_public_key.pem");
			InputStream in2 = zf.getInputStream(ze2);
			result2 = FileUtil.readString(in2);
			Logger.msg("公钥->" + result2);
		} catch (Exception e) {
			if (zf != null) {
				try {
					zf.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Logger.msg("apk中gamechannel或rsa_public_key文件不存在");
		} finally {
			if (zf != null) {
				try {
					zf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		String name = "gamechannel.json";
		if (result1 != null) {
			FileUtil.writeInfoInSDCard(context, result1, dir, name);
		} else {
			result1 = FileUtil.readInfoInSDCard(context, dir, name);
		}

		if (result1 != null) {
			GoagalInfo.channel = result1;
		}

		if (result2 != null) {
			GoagalInfo.publicKey = getPublicKey(result2);
		}

		GoagalInfo.channelInfo = GoagalInfo.getChannelInfo();
		GoagalInfo.packageInfo = GoagalInfo.getPackageInfo(context);
	}*/

	private static ChannelInfo getChannelInfo() {
		try {
			return JSON.parseObject(GoagalInfo.channel, ChannelInfo.class);
		} catch (Exception e) {
			Logger.msg("渠道信息解析错误->" + e.getMessage());
		}
		return null;
	}

	/// < 从输入流获取公钥
	private static String getPublicKey(InputStream in) {
		String result = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String mLine;
			while ((mLine = reader.readLine()) != null) {
				if (mLine.startsWith("----")) {
					continue;
				}
				result += mLine;
			}
		} catch (Exception e) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e2) {
				}
			}
		}
		return result;
	}

	public static String getPublicKey() {
		GoagalInfo.publicKey = getPublicKey(GoagalInfo.publicKey);
		return GoagalInfo.publicKey;
	}

	public static String getPublicKey(String key) {
		return key.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "")
				.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
				.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\r", "")
				.replace("\n", "");
	}

	public static PackageInfo getPackageInfo(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pInfo;
		} catch (Exception e) {
		}
		return null;
	}
}
