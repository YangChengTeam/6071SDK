package com.game.sdk.utils;

import com.game.sdk.TTWAppService;

import android.util.Log;

/**
 * 日志输出类
 * 
 * @author Administrator
 * 
 */
public class Logger {
	private static final boolean DEBUG = true;
	
	private static final int LEVEL = 2;// 日志输出级别
	private static final int V = 0;
	private static final int D = 1;
	private static final int I = 2;
	private static final int W = 3;
	private static final int E = 4;
	private static final String TAG = "game_sdk";
	public static void msg(String msg) {
		if(TTWAppService.debug == 0){
			return;
		}
		switch (LEVEL) {
		case V:
			Log.w(TAG, msg);
			break;
		case D:
			Log.d(TAG, msg);
			break;
		case I:
			Log.i(TAG, msg);
			break;
		case W:
			Log.w(TAG, msg);
			break;
		case E:
			Log.e(TAG, msg);
			break;
		default:
			break;
		}
	}
}
