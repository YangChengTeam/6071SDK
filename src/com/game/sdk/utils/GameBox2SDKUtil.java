package com.game.sdk.utils;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhangkai on 16/11/16.
 */
public class GameBox2SDKUtil {
    private static final String TAG = "GameBox2SDKUtil";
    private static final String PATH = Environment.getExternalStorageDirectory() + "/6071GameBox2SDK";

    public static UserInfo exchangeUserInfo(com.game.sdk.domain.UserInfo userInfo, int type){
        GameBox2SDKUtil.UserInfo g2sUserInfo = new GameBox2SDKUtil
                .UserInfo();
        g2sUserInfo.name = userInfo.username;
        g2sUserInfo.phone = userInfo.mobile;
        g2sUserInfo.pwd = userInfo.password;
        g2sUserInfo.type = type;
        return g2sUserInfo;
    }

    public static UserInfo exchangeUserInfo(String phone, String pwd, int type){
        GameBox2SDKUtil.UserInfo g2sUserInfo = new GameBox2SDKUtil
                .UserInfo();
        g2sUserInfo.name = phone;
        g2sUserInfo.phone = phone;
        g2sUserInfo.pwd = pwd;
        g2sUserInfo.type = type;
        return g2sUserInfo;
    }
    
  //删除文件和目录
    public static void clearFiles(String workspaceRootPath){
         File file = new File(workspaceRootPath);
         if(file.exists()){
              deleteFile(file);
         }
    }
    public static void deleteFile(File file){
         if(file.isDirectory()){
              File[] files = file.listFiles();
              for(int i=0; i<files.length; i++){
                   deleteFile(files[i]);
              }
         }
         file.delete();
    }
    
    public static void insertUserInfo(Context context, UserInfo userInfo) {
    	
        List<UserInfo> userInfos = loadAllUserInfo(context);
        int len = userInfos.size();
        boolean isSame = false;
        for (int i = 0; i < len; i++) {
            UserInfo _userInfo = userInfos.get(i);
            if (_userInfo.type == userInfo.type && (_userInfo.phone.equals(userInfo.phone) || _userInfo.name.equals(userInfo.name))) {
                _userInfo.phone = userInfo.phone;
                _userInfo.name = userInfo.name;
                _userInfo.pwd = userInfo.pwd;
                _userInfo.type = userInfo.type;
                isSame = true;
                break;
            }
        }
        if(len == 0 || !isSame){
            userInfos.add(0,userInfo);
        }
        saveUserInfos(context, userInfos);
    }
    
    public static void deleteUserInfo(Context context, UserInfo userInfo) {
        List<UserInfo> userInfos = loadAllUserInfo(context);
        for (int i = 0; i < userInfos.size(); i++) {
            UserInfo _userInfo = userInfos.get(i);
            if (_userInfo.phone.equals(userInfo.phone) || _userInfo.name.equals(userInfo.name)) {
                userInfos.remove(i);
                break;
            }
        }
        saveUserInfos(context, userInfos);
    }
    
    public static List<UserInfo> loadAllUserInfo(Context context) {
    	//清除文件夹
    	//clearFiles(PATH);
    	
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
                JSONArray jsonArray = new JSONArray(userInfosStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    UserInfo userInfo = new UserInfo();
                    userInfo.phone = jsonObject.getString("phone");
                    userInfo.name = jsonObject.getString("name");
                    userInfo.pwd = jsonObject.getString("pwd");
                    userInfo.type = jsonObject.getInt("type");
                    userInfos.add(userInfo);
                }
            } catch (Exception e) {
            }
        }
        return userInfos;
    }
    
    public static void saveUserInfos(Context context, List<UserInfo> userInfos) {
        String userInfosStr = "[";
        int len = userInfos.size();
        for (int i = 0; i < len; i++) {
            UserInfo _userInfo = userInfos.get(i);
            userInfosStr += getJsonStr(_userInfo);
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


    public static class UserInfo {
        public String phone;
        public String name;
        public String pwd;
        public int  type;  //0 手机号 1 用户名
    }

    private static File getUserInfosFile(Context context) {
        return new File(getFilePath(md5(getUid(context) + "users")));
    }

    private static String getFilePath(String name) {
        makeBaseDir();
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getAbsolutePath() + "/" + name;
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


    private static String md5(String name) {
        try {
            byte[] data = name.getBytes();
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] digest = md.digest(data);
            return toHex(digest);
        } catch (Exception e) {
            //LogUtil.msg("Md5 Fail");
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

    private static final char[] HEX = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static String getJsonStr(UserInfo userInfo) {
        return "{\"phone\":\"" + userInfo.phone + "\", \"name\" :\"" + userInfo.name + "\"," +
                "\"pwd\":\"" + userInfo.pwd + "\", \"type\":"+ userInfo.type + "}";
    }

    private static void makeBaseDir() {
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static String getUid(Context context) {
        String uid = "";
        if (uid.isEmpty()) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            uid = telephonyManager.getDeviceId();
        }
        if (uid == null || uid.isEmpty()) {
            uid = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return uid;
    }

}
