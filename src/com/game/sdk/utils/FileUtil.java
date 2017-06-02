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

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.security.Base64;
import com.game.sdk.security.Encrypt;
import com.game.sdk.security.Md5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;

public class FileUtil {
	/// < 获取相关联的名字
	public static String getAppContactName(Context context, String name) {
		return Md5.md5(GoagalInfo.channel + name);
	}

	/// < 读取输入流
	public static String readString(InputStream in) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		StringBuffer result = new StringBuffer();
		while ((line = br.readLine()) != null) {
			result.append(line + "\n");
		}

		return result.toString();
	}

	/// < 写入字符到sdcard
	public static void writeInfoInSDCard(Context context, String result, String dir, String name) {
		String tmpName = getAppContactName(context, name);
		String tmpResult = Base64.encode(Encrypt.encode(result).getBytes());
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
		Logger.msg("w ->" + gpxfile.getAbsolutePath());
	}

	/// < 写入图片到sdcard
	public static void writeImageInSDCard(Context context, Bitmap bitmap, String dir, String name) {
		String tmpName = getAppContactName(context, name);
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
		Logger.msg("w logo->" + logoFile.getAbsolutePath());
	}

	/// < 从sdcard获取图片
	public static Bitmap getImageFromSDCard(Context context, String dir, String name) {
		String tmpName = getAppContactName(context, name);
		String filePath = dir + "/" + tmpName;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		Logger.msg("r logo->" + filePath);
		return bitmap;
	}

	/// < 从sdcard获取字符
	public static String readInfoInSDCard(Context context, String dir, String name) {
		String tmpName = getAppContactName(context, name);
		String filePath = dir + "/" + tmpName;
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

	public static boolean copyFileFromSDPath(Context context, File sourceFile, String outFilePath) {
		boolean copyIsFinish = false;
		try {
			InputStream is = new FileInputStream(sourceFile);
			File file = new File(outFilePath);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			copyIsFinish = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return copyIsFinish;
	}

}
