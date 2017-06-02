package com.game.sdk.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author janecer
 * 
 */
public class CheckCardUtil {

	/**
	 * 移动充值卡 卡号，密码 长度验证
	 * 
	 * @return
	 */
	public static boolean checkYidong(Context context, String c, String p) {
		String match = "^[\\d]*$";
		if (!c.matches(match)) {
			Toast.makeText(context, "卡号格式不正确", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!p.matches(match)) {
			Toast.makeText(context, "密码格式不正确", Toast.LENGTH_SHORT).show();
			return false;
		}
		if ((checkLen(17, 18, c, p) && checkLen(10, 8, c, p) && checkLen(16,
				17, c, p))) {
			Toast.makeText(context, "移动充值卡号或者密码长度不匹配", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	/**
	 * 检查联通充值卡号的长度
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkLianTong(Context context, String c, String p) {
		if (checkLen(15, 19, c, p)) {
			Toast.makeText(context, "联通充值卡卡号长度为15位，密码长度为19位，请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 电信充值卡
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkDianXing(Context context, String c, String p) {
		if (checkLen(19, 18, c, p)) {
			Toast.makeText(context, "电信充值卡卡号长度为19位，密码长度为18位，请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 骏网一卡通
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkJunWang(Context context, String c, String p) {
		if (checkLen(16, 16, c, p)) {
			Toast.makeText(context, "骏网一卡通充值卡卡号长度为16位，密码长度为16位，请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 盛大
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkShenDa(Context context, String c, String p) {
		// 卡号15位或16位的数字字母，密码8位或9位的阿拉伯数字。
		if (!((c.length() == 15 || c.length() == 16) && (p.length() == 9 || p
				.length() == 8))) {
			Toast.makeText(context, "盛大卡号15位或16位的数字字母，密码8位或9位的阿拉伯数字 请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 征途
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkZhenTu(Context context, String c, String p) {
		// 全国官方征途游戏充值卡，卡号16位阿拉伯数字，密码8位阿拉伯数字。
		if (checkLen(16, 8, c, p)) {
			Toast.makeText(context, "征途游戏充值卡，卡号16位阿拉伯数字，密码8位阿拉伯数字 请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * Q币
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkQbi(Context context, String c, String p) {
		// 全国各地Q币卡，卡号：9位的阿拉伯数字、密码：12位的阿拉伯数字。
		if (checkLen(9, 12, c, p)) {
			Toast.makeText(context, "Q币充值卡，卡号9位阿拉伯数字，密码12位阿拉伯数字 请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 久游卡
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkJiuYou(Context context, String c, String p) {
		// 卡号13位、密码10位的阿拉伯数字
		if (checkLen(13, 10, c, p)) {
			Toast.makeText(context, "久游充值卡，卡号13位、密码10位的阿拉伯数字   请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 全国官方网易游戏充值卡，卡号13位、密码9位的阿拉伯数字
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkWangYi(Context context, String c, String p) {
		if (checkLen(13, 9, c, p)) {
			Toast.makeText(context, "网易游戏充值卡，卡号13位、密码9位的阿拉伯数字 请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 全国官方完美游戏充值卡，卡号10位、密码15位的阿拉伯数字
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkWangMei(Context context, String c, String p) {
		if (checkLen(10, 15, c, p)) {
			Toast.makeText(context, "完美游戏充值卡，卡号10位、密码15位的阿拉伯数字 请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 卡号20位、密码12位的阿拉伯数字
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkSoHu(Context context, String c, String p) {
		if (checkLen(20, 12, c, p)) {
			Toast.makeText(context, "搜狐充值卡， 卡号20位、密码12位的阿拉伯数字  请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 纵游一卡通 卡号与密码均为15位阿拉伯数字
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkZhongYou(Context context, String c, String p) {
		if (checkLen(15, 15, c, p)) {
			Toast.makeText(context, "纵游一卡通，卡号15位、密码15位的阿拉伯数字  请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 天下通 卡号是15位阿拉伯数字，密码是8位阿拉伯数字，
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkTianXia(Context context, String c, String p) {
		if (checkLen(15, 8, c, p)) {
			Toast.makeText(context, "天下通，卡号15位、密码8位的阿拉伯数字  请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 天宏一卡通 ※卡号为12位，前2位是大写或小写英文字母，后10位是数字；密码15位是纯数字。
	 * ※卡号为10位，前2位是大写或小写英文字母，后8位是数字；密码10位是纯数字。 ※暂不支持卡号以V开头的天宏专充卡。 ，
	 * 
	 * @param context
	 * @param c
	 * @param p
	 * @return
	 */
	public static boolean checkTianHong(Context context, String c, String p) {
		if (checkLen(12, 15, c, p)) {
			Toast.makeText(context,
					"天宏卡号为12位，前2位是大写或小写英文字母，后10位是数字；密码15位是纯数字  请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (checkLen(10, 10, c, p)) {
			Toast.makeText(context,
					"天宏卡号为10位，前2位是大写或小写英文字母，后8位是数字；密码10位是纯数字 请检查！ ",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 根据传进来的长度判断字符串的长度
	 * 
	 * @param length
	 * @param str
	 * @return true表示检查没有通过，false表示检查通过
	 */
	public static boolean checkLen(int length_c, int length_p, String c,
			String p) {
		if (c.length() == length_c && p.length() == length_p) {
			return false;
		}
		return true;
	}
}
