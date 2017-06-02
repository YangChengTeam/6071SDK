package com.game.sdk.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.widget.Toast;

/**
 * author janecer 2014年4月10日下午3:29:24
 */
public class NetworkImpl {
	public static final int TIMEOUT = 8;
	public static HttpClient getHttpClient(Context ctx) {
		if (!isNetWorkConneted(ctx)) {
			return null;
		}
		HttpClient client = null;
		if (isCmwapType(ctx)) {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);// 设置链接超时时间
			HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);// 设置请求超时时间
			HttpConnectionParams.setSocketBufferSize(params, 100 * 1024);
			HttpClientParams.setRedirecting(params, true);
			client = new DefaultHttpClient(params);
		} else {
			client = new DefaultHttpClient();
			HttpParams params = client.getParams();
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					TIMEOUT * 1000);
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT * 1000);
		}
		return client;
	}

	/**
	 * 网络是否是连接状态
	 * 
	 * @return true表示可能，false网络不可用
	 */
	public static boolean isNetWorkConneted(Context ctx) {
		try {
			ConnectivityManager cmr = (ConnectivityManager) ctx
						.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cmr != null) {
				NetworkInfo networkinfo = cmr.getActiveNetworkInfo();
				if (networkinfo != null && networkinfo.isConnectedOrConnecting()) {
					return networkinfo.isAvailable(); 
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * 是否使用代理上网
	 * 
	 * @param ctx
	 * @return
	 */
	private static boolean isCmwapType(Context ctx) {
		ConnectivityManager cmr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cmr.getActiveNetworkInfo();
		String nettype = netinfo.getExtraInfo();
		if (null == nettype) {
			return false;
		}
		return "cmwap".equalsIgnoreCase(nettype)
				|| "3gwap".equalsIgnoreCase(nettype)
				|| "uniwap".equalsIgnoreCase(nettype);
	}
}
