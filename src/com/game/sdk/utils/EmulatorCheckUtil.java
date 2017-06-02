package com.game.sdk.utils;

import com.feiyou.plugin.EmulatorCheck;

import android.os.Build;

/**
 * Created by zhangkai on 16/11/16.
 */
public class EmulatorCheckUtil {
	public static boolean isEmulator() {

		Logger.msg("isEmulator --->" + Build.FINGERPRINT + "--->" + Build.FINGERPRINT + "--->" + Build.MODEL + "--->"
				+ Build.MANUFACTURER + "--->" + Build.BRAND + "--->" + Build.DEVICE);

		boolean flag = Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown")
				|| Build.FINGERPRINT.contains("vbox") || Build.FINGERPRINT.contains("ONEPLUS")
				|| Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion")
				|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
				|| "google_sdk".equals(Build.PRODUCT);

		if (!flag) {
			EmulatorCheck emulatorCheck = new EmulatorCheck();
			flag = emulatorCheck.anti() > 0 ? true : false;
		}
		return flag;
	}

}
