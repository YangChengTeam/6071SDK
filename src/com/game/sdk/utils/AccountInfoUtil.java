package com.game.sdk.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.security.Base64;
import com.game.sdk.security.Encrypt;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * Created by zhangkai on 16/11/16.
 */
public class AccountInfoUtil {

	private static final String PATH = Environment.getExternalStorageDirectory() + "/6071GameBox2SDK";
	
	
	public static void insertUserInfoFromPublic(Context context, UserInfo userInfo) {
		List<UserInfo> userInfos = loadAllUserInfo(context);
		
		boolean isSame = false;
		int len = 0;
		if(userInfos != null && userInfos.size() > 0){
			len = userInfos.size();
			for (int i = 0; i < len; i++) {
				UserInfo _userInfo = userInfos.get(i);
				if (_userInfo.username.equals(userInfo.username)) {
					isSame = true;
					break;
				}
			}
		}
		
		if (len == 0 || !isSame) {
			if(userInfos == null){
				userInfos = new ArrayList<UserInfo>();
			}
			userInfos.add(0,userInfo);
		}
		saveUserInfos(context, userInfos);
	}
	
	public static void insertUserInfo(Context context, UserInfo userInfo) {
		List<UserInfo> userInfos = loadAllUserInfo(context);
		int len = userInfos.size();
		for (int i = 0; i < len; i++) {
			UserInfo _userInfo = userInfos.get(i);
			if (_userInfo.username.equals(userInfo.username)) {
				userInfos.remove(i);
				break;
			}
		}
		
		if (userInfos != null) {
			userInfos.add(0,userInfo);
		}
		
		saveUserInfos(context, userInfos);
	}
	
	//修改用户密码时，如果用户对应的手机号也登录过，则同时修改账号/手机号对应的密码
	public static void updateUsersInfo(Context context, UserInfo userInfo) {
		List<UserInfo> userInfos = loadAllUserInfo(context);
		
		if(userInfos == null){
			userInfos = new ArrayList<UserInfo>();
		}
		
		int len = userInfos.size();
		
		//修改密码的用户是否存在用户列表中
		int isAdd = 0;
		
		for (int i = 0; i < len; i++) {
			UserInfo _userInfo = userInfos.get(i);
			
			if(!StringUtils.isEmpty(userInfo.mobile) && _userInfo.username.equals(userInfo.mobile)){
				isAdd ++;
				userInfos.remove(i);
				
				UserInfo nUser = new UserInfo();
				nUser.username = userInfo.mobile;
				nUser.mobile = userInfo.mobile;
				nUser.password = userInfo.password;
				
				if (userInfos != null) {
					userInfos.add(0,nUser);
					continue;
				}
			}
			
			if (!StringUtils.isEmpty(userInfo.username) && _userInfo.mobile.equals(userInfo.mobile)) {
				isAdd ++;
				userInfos.remove(i);
				if (userInfos != null) {
					userInfo.username = _userInfo.username;
					userInfos.add(0,userInfo);
					continue;
				}
			}
		}
		
		if(isAdd == 0){
			userInfos.add(0,userInfo);
		}
		
		saveUserInfos(context, userInfos);
	}
	
	
	public static void deleteUserInfo(Context context, UserInfo userInfo) {
		List<UserInfo> userInfos = loadAllUserInfo(context);
		for (int i = 0; i < userInfos.size(); i++) {
			UserInfo _userInfo = userInfos.get(i);
			if (_userInfo.username.equals(userInfo.username)) {
				userInfos.remove(i);
				break;
			}
		}
		saveUserInfos(context, userInfos);
	}

	public static List<UserInfo> loadAllUserInfo(Context context) {
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		String userInfosStr = "";
		try {
			File file = getUserInfosFile(context);
			userInfosStr = new String(readFromFile(file));
		} catch (Exception e) {
		}
		if (!userInfosStr.isEmpty()) {
			userInfosStr = Encrypt.decode(new String(Base64.decode(userInfosStr)));
			try {
				
				userInfos = JSON.parseArray(userInfosStr, UserInfo.class);
				
				/*JSONArray jsonArray = new JSONArray(userInfosStr);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					UserInfo userInfo = new UserInfo();
					
					userInfo.accountType = jsonObject.getInt("accountType") > -1 ? jsonObject.getInt("accountType") : 0;
					userInfo.userId = jsonObject.getString("user_id") != null ? jsonObject.getString("user_id") :"";
					userInfo.username = jsonObject.getString("name")!= null ? jsonObject.getString("name") :"";
					userInfo.password = jsonObject.getString("password")!= null ? jsonObject.getString("password") :"";
					userInfo.device = jsonObject.getInt("device") > -1 ? jsonObject.getInt("device") : -1;
					userInfo.validateMobile = jsonObject.getInt("is_vali_mobile") > -1 ? jsonObject.getInt("is_vali_mobile") : -1;
					userInfo.isrpwd = jsonObject.getInt("isrpwd") > -1 ? jsonObject.getInt("isrpwd") : -1;
					userInfo.logintime = jsonObject.getLong("logintime") > -1 ? jsonObject.getLong("logintime") : 0 ;
					userInfo.sex = jsonObject.getInt("sex") > -1 ? jsonObject.getInt("sex") : -1;
					userInfo.sign = jsonObject.getString("sign")!= null ? jsonObject.getString("sign") :"";
					
					userInfos.add(userInfo);
				}*/

			} catch (Exception e) {
			}
		}
		return userInfos;
	}

	public static UserInfo getUserInfoByName(Context context, String userName) {
		UserInfo userInfo = null;
		List<UserInfo> userInfos = loadAllUserInfo(context);
		for (UserInfo uinfo : userInfos) {
			if (userName.equals(uinfo.username)) {
				userInfo = uinfo;
				break;
			}
		}
		return userInfo;
	}

	public static void saveUserInfos(Context context, List<UserInfo> userInfos) {
		String userInfosStr = "[";
		int len = userInfos.size();
		for (int i = 0; i < len; i++) {
			UserInfo _userInfo = userInfos.get(i);
			//userInfosStr += getJsonStr(_userInfo);
			userInfosStr += JSON.toJSONString(_userInfo);
			if (i != len - 1) {
				userInfosStr += ",";
			}
		}
		userInfosStr += "]";
		userInfosStr = Base64.encode(Encrypt.encode(userInfosStr).getBytes());
		File file = getUserInfosFile(context);
		try {
			writeToFile(file, userInfosStr.getBytes());
		} catch (Exception e) {
		}
	}

	private static File getUserInfosFile(Context context) {
		return new File(getFilePath(md5(getUid(context) + "accounts")));
	}

	private static String getFilePath(String username) {
		makeBaseDir();
		File dir = new File(PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir.getAbsolutePath() + "/" + username;
	}

	private static void writeToFile(File file, byte[] data) throws IOException {
		FileOutputStream fou = null;
		try {
			fou = new FileOutputStream(file);
			fou.write(data);
		} finally {
			if (fou != null) {
				try {
					fou.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static byte[] readFromFile(File file) throws IOException {
		FileInputStream fin = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			fin = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int read = 0;
			while ((read = fin.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			byte[] data = out.toByteArray();
			out.close();
			return data;
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static String md5(String username) {
		try {
			byte[] data = username.getBytes();
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			byte[] digest = md.digest(data);
			return toHex(digest);
		} catch (Exception e) {
			Logger.msg("Md5 Fail");
		}
		return null;
	}

	private static final String ALGORITHM = "MD5";

	private static String toHex(byte[] b) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			int v = b[i];
			builder.append(HEX[(0xF0 & v) >> 4]);
			builder.append(HEX[0x0F & v]);
		}
		return builder.toString();
	}

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	private static String getJsonStr(UserInfo userInfo) {
		return "{\"username\" :\"" + userInfo.username + "\"," + "\"password\":\"" + userInfo.password + "\"}";
	}

	private static void makeBaseDir() {
		File dir = new File(PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	private static String getUid(Context context) {
		String uid = "";
		if (uid.isEmpty()) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			uid = telephonyManager.getDeviceId();
		}
		if (uid.isEmpty()) {
			uid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		return uid;
	}

}
