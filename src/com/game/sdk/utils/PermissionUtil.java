package com.game.sdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PermissionUtil {

	public static boolean hasPermission(Context context, String permission) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_PERMISSIONS);
			if (info.requestedPermissions != null) {
				for (String p : info.requestedPermissions) {
					if (p.equals(permission)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 检测是否有某个权限
	 * 
	 * @param context
	 * @param permission
	 * @return
	 */
	public static boolean checkPermission(Context context, String permission) {
		PackageManager pm = context.getPackageManager();
		boolean result = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission,
				context.getPackageName()));
		return result;
	}

}
