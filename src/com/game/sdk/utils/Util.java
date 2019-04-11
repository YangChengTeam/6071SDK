package com.game.sdk.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.alibaba.fastjson.JSON;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.Md5Util;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.FYGameSDK;
import com.game.sdk.TTWAppService;
import com.game.sdk.domain.ChannelInfo;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.floatwindow.FloatWebActivity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

public class Util {

	private static final String TAG = "Util";

	/**
	 * @return
	 */
	public static String getOrderId() {
		String orderId = "" + System.currentTimeMillis();
		return orderId;
	}

	// public static String md5Encode(String s) {
	// if (s == null) {
	// return "";
	// }
	// try {
	// MessageDigest md5 = MessageDigest.getInstance("MD5");
	// byte[] digest = md5.digest(s.getBytes("utf-8"));
	// byte[] encode = Base64.encodeBase64(digest);
	// return new String(encode, "utf-8");
	// } catch (Exception e) {
	// Log.i(TAG, "md5 encode exception");
	// }
	// return s;
	// }

	/**
	 * 随机获取16位
	 * 
	 * @return
	 */
	public static String getRandom16() {
		String s = "";
		Random random = new Random();
		s += random.nextInt(9) + 1;
		for (int i = 0; i < 16 - 1; i++) {
			s += random.nextInt(10);
		}
		BigInteger bigInteger = new BigInteger(s);
		System.out.println(bigInteger);
		return bigInteger.toString();
	}

	/**
	 * 获取ua信息
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static String getUserUa(Context context) {
		WebView webview = new WebView(context);
		webview.layout(0, 0, 0, 0);
		String str = webview.getSettings().getUserAgentString();
		Logger.msg(str);
		return str;
	}

	private static String url(String url) {
		String appkey = "xyst@!sdk";
		String time = String.valueOf(System.currentTimeMillis());
		String timeStr = time.substring(0, time.length() - 3);
		String sign = "username=" + TTWAppService.userinfo.username + "&appkey=" + appkey + "&logintime=" + timeStr;
		sign = Md5Util.md5(sign);
		url = url + "?gameid=" + TTWAppService.gameid + "&username=" + TTWAppService.userinfo.username + "&version="
				+ FYGameSDK.defaultSDK().getVersion() + "&logintime=" + timeStr + "&sign=" + sign;
		return url;
	}

	public static void web(Context context, String name, String url) {
		url = url(url);
		Intent intent_view = new Intent(context, FloatWebActivity.class);
		if (Constants.DEBUG) {
			url = url.replace("http://api.6071.com/", "http://sdk.289.com/");
		}
		Logger.msg(url);
		intent_view.putExtra("url", url);
		intent_view.putExtra("title", name);
		intent_view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent_view);
	}
	
	/**
	 * 从清单文件中获取gameid与appid
	 * 
	 * @param context
	 * @return
	 */
	public static void getGameInfo(Context context) {
		PackageManager pm = context.getPackageManager();
		try {

			Logger.msg("context.getPackageName()---" + context.getPackageName());
			ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = appinfo.metaData;
			if (null != bundle) {
				GoagalInfo.appid = bundle.getInt("FY_APPID") + "";
				GoagalInfo.gameid = bundle.getInt("FY_GAMEID") + "";
				GoagalInfo.agentid = bundle.getInt("FY_AGENT") + "";
				
				GoagalInfo.teaAppId = bundle.getInt("TEA_APP_ID")+"";
				GoagalInfo.teaAppName = bundle.getString("TEA_APP_NAME");
			}
			Logger.msg(
					"GoagalInfo.appid---" + GoagalInfo.appid + "---" + GoagalInfo.gameid + "---" + GoagalInfo.agentid);
		} catch (NameNotFoundException e) {
			Logger.msg("AndroidManifest.xml meta->配置问题");
			return;
		}
		
		String[] results = getChannel(context);
		
		String lastAgentId = PreferenceUtil.getImpl(context).getString(Constants.LAST_AGENT_ID, "");
		if(!StringUtils.isEmpty(lastAgentId)){
			GoagalInfo.agentid = lastAgentId;
		}else{
			
			String channel = results[0];
			if (channel == null || channel.isEmpty()) {
				GoagalInfo.agentid = GoagalInfo.channel;
			} else {
				ChannelInfo channelInfo = getChannelInfo(channel);
				if (channelInfo != null) {
					GoagalInfo.agentid = channelInfo.agent_id;
				} else {
					GoagalInfo.agentid = GoagalInfo.channel;
				}
			}
			PreferenceUtil.getImpl(context).putString(Constants.LAST_AGENT_ID, GoagalInfo.agentid);
		}
		
		GoagalInfo.publicKey = results[1];
		
		getFromId(context);
	}
	
	//获取推广的盒子中的fromID
	public static void getFromId(Context context){
		try{
        	ContentResolver contentResolver = context.getContentResolver();
	        Uri uri = Uri.parse("content://com.sdk.rpc.provide/gamesdk");
	        
        	 Bundle gameBoxBundle = contentResolver.call(uri, "search_channel", null, null);
             if(gameBoxBundle != null && !StringUtils.isEmpty(gameBoxBundle.getString("channel"))){
            	 GoagalInfo.fromId = gameBoxBundle.getString("channel");
             }else{
            	 GoagalInfo.fromId = GoagalInfo.gameid;
             }
             Logger.msg("game_box_channel--->" + GoagalInfo.fromId);
        }catch(Exception e){
        	Logger.msg("getFromId ContentResolver error--->");
        	e.printStackTrace();
        }
	}
	
	public static void reInitChannel(Context context){
		String[] results = getChannel(context);
		
		String channel = results[0];

		if (channel == null || channel.isEmpty()) {
			GoagalInfo.agentid = GoagalInfo.channel;
		} else {
			ChannelInfo channelInfo = getChannelInfo(channel);
			if (channelInfo != null) {
				GoagalInfo.agentid = channelInfo.agent_id;
			} else {
				GoagalInfo.agentid = GoagalInfo.channel;
			}
		}
		PreferenceUtil.getImpl(context).putString(Constants.LAST_AGENT_ID, GoagalInfo.agentid);
	}
	
	private static ChannelInfo getChannelInfo(String channel) {
		try {
			return JSON.parseObject(channel, ChannelInfo.class);
		} catch (Exception e) {
			Logger.msg("渠道信息解析错误->" + e.getMessage());
		}
		return null;
	}

	public static String readString(InputStream in) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		StringBuffer result = new StringBuffer();
		while ((line = br.readLine()) != null) {
			result.append(line + "\n");
		}
		return result.toString();
	}

	// 渠道获取 从配置文件中 找到文件
	public static String[] getChannel(Context context) {
		String result1 = null;
		String result2 = null;
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		ZipFile zf = null;
		try {
			zf = new ZipFile(sourceDir);
			ZipEntry ze1 = zf.getEntry("META-INF/gamechannel.json");
			InputStream in1 = zf.getInputStream(ze1);
			result1 = readString(in1);
			Logger.msg(result1);

			ZipEntry ze2 = zf.getEntry("META-INF/rsa_public_key.pem");
			InputStream in2 = zf.getInputStream(ze2);
			result2 = readString(in2);
			Logger.msg(result2);
		} catch (Exception e) {
			if (zf != null) {
				try {
					zf.close();
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Logger.msg("apk中gamechannel或rsa_public_key文件不存在");
		} finally {
			if (zf != null) {
				try {
					zf.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String name = "gamechannel.json";
		if (result1 != null) {
			writeInfoInSDCard(context, result1, name);

		} else {
			result1 = readInfoInSDCard(context, name);
		}
		if (result2 == null || result2.isEmpty()) {
			result2 = "-----BEGIN PUBLIC KEY-----" + "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEApsmOWfJecgy0Al55X8hG"
					+ "C/HMaZQ4WdVW99szOmE5tuVl342fmL/Vm/g2sJSc6AaowNnZrl1Rt9UrWnjVLhSL"
					+ "mpChMI2F+r5XAJL+Z3+xeKetejpia2OWslO+sT+YBWm2ilb+ZOdJ8Ms3dDBndvuA"
					+ "rgNRPwbW8Mxh2LH4mFy2ivYjw7mXgWNzXIAlUT4i9AaoqmX/Osr7l6+VdXi6uLlR"
					+ "JIEZC+A7KPL35iXrBfPCgMn9QennaFvzLPDn7R4kCkgjo6rF6SdeMDq/eUFwr2bM"
					+ "wfjIO2QfGtF+nv3Yg6i2HKMwZde2N+vvjxxCdupYfHHVhbBOEwPreR9tvEiDGGDf"
					+ "ERwURh7gL5L5adBa6AtqtaKRuwtWcnOAueTkVh7nP6Gt8J253mqhp2upu4pJaKfz"
					+ "Q1RvczAr93tmA1XkUQS3RkMEuqocay/u3iiSKwJob9Oh+fJC8CgKL4hOtIK1VRGu"
					+ "QPc7BBQtLWxmOoTZstLoC6oVS6BoQTk/hQ3STtyJqHuoqi5Cu/e2Fioi4bc0ViNJ"
					+ "R67sFHlN5rQSdXWS4ouR6zQK4yAGVSCUlFznx2Jsu3slABijPUrNP3RkP91qvC3/"
					+ "7osOB5lbJsZNgygHcy3VylwlQ/EqXEnNrCwvCPSWgIJJ0KgScEttHd1FVEmntNJd"
					+ "MU6CKowXCLCWhtMt9AysiDkCAwEAAQ==" + "-----END PUBLIC KEY-----";
		}
		result2 = getKey(result2);
		return new String[] { result1, result2 };
	}

	public static String getAppContactName(Context context, String name) {
		return Md5Util.md5(context.getPackageName() + GoagalInfo.appid + "-" + GoagalInfo.gameid + name);
	}

	public static String getInitLogoName(Context context, String name) {
		Logger.msg("init packagename --->"+context.getPackageName());
		Logger.msg("md5 init logo name--->"+Md5Util.md5(context.getPackageName() + GoagalInfo.agentid + SystemUtil.getPhoneIMEI(context) + name));
		return Md5Util.md5(context.getPackageName() + GoagalInfo.agentid + SystemUtil.getPhoneIMEI(context) + name);
	}
	
	public static final String dirName = "6071Game";

	public static void writeInfoInSDCard(Context context, String result, String name) {
		String tmpName = getAppContactName(context, name);
		String tmpResult = Base64.encode(Encrypt.encode(result).getBytes());
		String dir = Environment.getExternalStorageDirectory() + "/" + dirName;
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdir();
		}
		File gpxfile = new File(dir, tmpName);
		try {
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(tmpResult);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.msg("r->" + e.getMessage());
		}
		Logger.msg("w ->" + result + "->" + tmpName);
	}

	public static void writeImageInSDCard(Context context, Bitmap bitmap, String name) {
		String tmpName = getAppContactName(context, name);
		String dir = Environment.getExternalStorageDirectory() + "/" + dirName;
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdir();
		}
		File logoFile = new File(dir, tmpName);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
		byte[] bitmapdata = bos.toByteArray();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(logoFile);
			fos.write(bitmapdata);
			fos.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.msg(tmpName + "->" + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.msg(tmpName + "->" + e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Logger.msg("w logo->" + dir + tmpName);
	}

	public static void writeLaunchImageInSDCard(Context context, Bitmap bitmap, String name) {
		String tmpName = getInitLogoName(context, name);
		String dir = Environment.getExternalStorageDirectory() + "/" + dirName;
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdir();
		}
		File logoFile = new File(dir, tmpName);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
		byte[] bitmapdata = bos.toByteArray();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(logoFile);
			fos.write(bitmapdata);
			fos.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.msg(tmpName + "->" + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.msg(tmpName + "->" + e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Logger.msg("w logo->" + dir + tmpName);
	}

	public static Bitmap getLogoFileBitmap(Context context, String name) {
		String tmpName = getAppContactName(context, name);
		String filePath = Environment.getExternalStorageDirectory() + "/" + dirName + "/" + tmpName;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		Logger.msg("r logo->" + filePath);
		return bitmap;
	}

	public static Bitmap getInitLogoFileBitmap(Context context, String name) {
		String tmpName = getInitLogoName(context, name);
		String filePath = Environment.getExternalStorageDirectory() + "/" + dirName + "/" + tmpName;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		Logger.msg("r logo->" + filePath);
		return bitmap;
	}

	public static String readInfoInSDCard(Context context, String name) {
		String tmpName = getAppContactName(context, name);
		String filePath = Environment.getExternalStorageDirectory() + "/" + dirName + "/" + tmpName;
		File file = new File(filePath);
		if (file.exists()) {
			// Read text from file
			StringBuilder text = new StringBuilder();
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					text.append(line);
				}
				br.close();
				String reuslt = Encrypt.decode(new String(Base64.decode(text.toString())));
				Logger.msg("r->" + tmpName + "->" + reuslt);
				return reuslt;
			} catch (IOException e) {
				// You'll need to add proper error handling here
				e.printStackTrace();
				Logger.msg("r->" + e.getMessage());
			}
		}
		Logger.msg("r->" + tmpName + "->" + null);
		return null;
	}

	private static String getPK(InputStream in) {
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

	// 获取公钥
	@SuppressWarnings("resource")
	public static String getPublicKey(Context context) {
		String result = "";
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		ZipInputStream zin = null;
		ZipEntry ze = null;
		try {
			FileInputStream fin = new FileInputStream(sourceDir);
			zin = new ZipInputStream(fin);
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					continue;
				} else {
					if (ze.getName().contains("META-INF/rsa_public_key.pem")) {
						result = getPK(zin);
						zin.closeEntry();
						break;
					}
				}
			}
		} catch (Exception e) {
			if (ze != null) {
				try {
					zin.closeEntry();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		}
		if (result == null || result.isEmpty()) {
			result = "-----BEGIN PUBLIC KEY-----" + "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEApsmOWfJecgy0Al55X8hG"
					+ "C/HMaZQ4WdVW99szOmE5tuVl342fmL/Vm/g2sJSc6AaowNnZrl1Rt9UrWnjVLhSL"
					+ "mpChMI2F+r5XAJL+Z3+xeKetejpia2OWslO+sT+YBWm2ilb+ZOdJ8Ms3dDBndvuA"
					+ "rgNRPwbW8Mxh2LH4mFy2ivYjw7mXgWNzXIAlUT4i9AaoqmX/Osr7l6+VdXi6uLlR"
					+ "JIEZC+A7KPL35iXrBfPCgMn9QennaFvzLPDn7R4kCkgjo6rF6SdeMDq/eUFwr2bM"
					+ "wfjIO2QfGtF+nv3Yg6i2HKMwZde2N+vvjxxCdupYfHHVhbBOEwPreR9tvEiDGGDf"
					+ "ERwURh7gL5L5adBa6AtqtaKRuwtWcnOAueTkVh7nP6Gt8J253mqhp2upu4pJaKfz"
					+ "Q1RvczAr93tmA1XkUQS3RkMEuqocay/u3iiSKwJob9Oh+fJC8CgKL4hOtIK1VRGu"
					+ "QPc7BBQtLWxmOoTZstLoC6oVS6BoQTk/hQ3STtyJqHuoqi5Cu/e2Fioi4bc0ViNJ"
					+ "R67sFHlN5rQSdXWS4ouR6zQK4yAGVSCUlFznx2Jsu3slABijPUrNP3RkP91qvC3/"
					+ "7osOB5lbJsZNgygHcy3VylwlQ/EqXEnNrCwvCPSWgIJJ0KgScEttHd1FVEmntNJd"
					+ "MU6CKowXCLCWhtMt9AysiDkCAwEAAQ==" + "-----END PUBLIC KEY-----";
			result = getKey(result);
		}
		// Util.toast(context, result);
		return result;
	}

	public static String getKey(String key) {
		return key.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "")
				.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
				.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\r", "")
				.replace("\n", "");
	}

	// 判断格式是否为email
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	// 判断格式是否为手机号
	public static boolean isPhone(String phone) {
		return true;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	@SuppressLint("NewApi")
	public static int getHeight(Context paramContext) {
		if (Build.VERSION.SDK_INT >= 13) {
			Display localDisplay = ((WindowManager) paramContext.getSystemService(Application.WINDOW_SERVICE))
					.getDefaultDisplay();
			Point localPoint = new Point();
			localDisplay.getSize(localPoint);
			return localPoint.y;
		}
		return paramContext.getResources().getDisplayMetrics().heightPixels;
	}

	public static void toast(Context context, String msg) {
		if (msg == null || msg.trim().isEmpty()) {
			msg = "火☆秂攻入哋球，请重试";
		}
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static boolean isWeixinAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断qq是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isQQClientAvailable(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mobileqq")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean checkAliPayInstalled(Context context) {
		Uri uri = Uri.parse("alipays://platformapi/startApp");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		ComponentName componentName = intent.resolveActivity(context.getPackageManager());
		return componentName != null;
	}

	private static final Handler gUiHandler = new Handler(Looper.getMainLooper());

	public static void post(Runnable r) {
		gUiHandler.post(r);
	}

	public static void postDelayed(long delay, Runnable r) {
		gUiHandler.postDelayed(r, delay);
	}

}
