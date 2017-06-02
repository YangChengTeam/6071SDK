package com.game.sdk.view;

import java.io.File;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.security.Base64;
import com.game.sdk.security.Encrypt;
import com.game.sdk.service.DownGameBoxService;
import com.game.sdk.ui.GamePackageDetailActivity.DownAsyncTask;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.PathUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.game.sdk.utils.ZipUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebView extends WebView {

	private Context context;

	public CustomWebView(Context context) {
		this(context, null);
	}

	public CustomWebView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initWebSettings();
		addJavascriptInterface(new AndroidJSObject(), "AndroidJSObject");
		this.setWebViewClient(new CustomWebViewClient(context));
		this.setWebChromeClient(new WebChromeClient());
	}

	public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressLint("NewApi")
	private void initWebSettings() {
		WebSettings webSettings = this.getSettings();
		webSettings.setLoadsImagesAutomatically(false);
		// 手机必须运行 在Android 4.2 或更高才能使用setAllowUniversalAccessFromFileURLs() API
		// 级别 16 + 上才可用。
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			webSettings.setAllowUniversalAccessFromFileURLs(true);
		}
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setNeedInitialFocus(false);
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setDefaultTextEncodingName("UTF-8");
		webSettings.setAppCacheEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 默认不使用缓存
		webSettings.setAppCacheMaxSize(8 * 1024 * 1024); // 缓存最多可以有8M
		webSettings.setAllowFileAccess(true); // 可以读取文件缓存(manifest生效)
		String appCaceDir = context.getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCaceDir);
		if ((Build.VERSION.SDK_INT >= 11) && (Build.MANUFACTURER.toLowerCase().contains("lenovo")))
			this.setLayerType(1, null);
	}

	// 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。
	private class CustomWebViewClient extends WebViewClient {

		private Context context;

		public CustomWebViewClient(Context context) {
			this.context = context;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (url.contains("gamebox://?act=GiftDetailActivity")) {
				if (CheckUtil.isInstallGameBox(context)) {
					String pwd = Base64.encode(Encrypt.encode(GoagalInfo.userInfo.password).getBytes());

					Uri uri = Uri.parse(url+"&pwd=" + pwd + "&phone=" + GoagalInfo.userInfo.mobile + "&username=" + GoagalInfo.userInfo.username);
					Logger.msg("游戏礼包领取URI---" + uri.toString());
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(uri);
					context.startActivity(intent);
				}else{
					gameBoxDown();
				}
				return true;
			}

			if (url.contains("http://")) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(url);
				intent.setData(content_url);
				context.startActivity(intent);
				return true;
			}

			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

		}
	}

	class AndroidJSObject {
		@JavascriptInterface
		public void search(final String searchKey) {

		}
	}
	
	public void gameBoxDown() {
		if (!SystemUtil.isServiceWork(context, "com.game.sdk.service.DownGameBoxService")) {
			// 如果下载文件存在，直接启动安装
			File downFile = new File(PathUtil.getApkPath("game_box"));
			if (downFile.exists()) {
				if(ZipUtil.isArchiveFile(downFile)){
					if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo != null && !StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)) {
						new DownAsyncTask().execute();
					}
				}else{
					downFile.delete();//删除下载的错误的盒子文件，提示用户重新下载
					Util.toast(context, "盒子文件错误，请重新下载");
				}
			} else {
				downBoxApp();
			}
		} else {
			Util.toast(context, "游戏盒子下载中");
		}
	}
	
	public void downBoxApp() {
		if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo!= null && !StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)){
			Intent intent = new Intent(context, DownGameBoxService.class);
			intent.putExtra("downUrl", GoagalInfo.inItInfo.boxInfo.boxDownUrl);
			context.startService(intent);
		}else{
			Util.toast(context, "下载地址有误，请稍后重试");
		}
	}
	
	public class DownAsyncTask extends AsyncTask<Integer, Integer, Integer> {
		@Override
		protected Integer doInBackground(Integer... params) {
			return CheckUtil.getFileLengthByUrl(GoagalInfo.inItInfo.boxInfo.boxDownUrl);
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			File downFile = new File(PathUtil.getApkPath("game_box"));

			if (result != downFile.length()) {
				downFile.delete();
				downBoxApp();
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(downFile), "application/vnd.android.package-archive");
				context.startActivity(intent);
			}
		}
	}
	
}
