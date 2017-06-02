package com.game.sdk.utils;

import java.io.File;

import com.game.sdk.net.constans.ServerConfig;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class PathUtil {
	/**
	 * get the path of download apk cache.
	 */
	public static String getPluginPath(String name) {
		makeBaseDir();
		File dir = new File(ServerConfig.PATH + "/plugins");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir.getAbsolutePath() + "/" + name + ".apk";
	}

	/**
	 * get the path of download apk cache.
	 *
	 * @param name
	 *            the name of file is apk
	 * @return the apk file path
	 */
	public static String getApkPath(String name) {
		makeBaseDir();
		File dir = new File(ServerConfig.PATH + "/apks");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir.getAbsolutePath() + "/" + name + ".apk";
	}

	/**
	 * get the path of theme cache.
	 *
	 * @return the theme file path
	 */
	public static String getThemeDir() {
		makeBaseDir();
		File dir = new File(ServerConfig.PATH + "/themes/");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir.getAbsolutePath();
	}

	/**
	 * get the path of goagal cache.
	 *
	 * @return the golgal file path
	 */
	public static String getGolgalDir() {
		makeBaseDir();
		File dir = new File(ServerConfig.PATH + "/goagal/");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir.getAbsolutePath();
	}

	public static String getImageDir() {
		makeBaseDir();
		File dir = new File(ServerConfig.PATH + "/images/");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir.getAbsolutePath();
	}

	private static void makeBaseDir() {
		File dir = new File(ServerConfig.PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
	
	public static String getSoPath(Context context) {
		File dir = new File(context.getCacheDir() + File.separator + "6071Box" + File.separator + "so");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir.getAbsolutePath();
	}
	
}
