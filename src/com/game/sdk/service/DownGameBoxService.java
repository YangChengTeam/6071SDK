package com.game.sdk.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.game.sdk.utils.Constants;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.PathUtil;
import com.game.sdk.utils.Util;
import com.game.sdk.utils.ZipUtil;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DownGameBoxService extends Service {

	private final int MSG_UPDATE_PROGRESS = 0x0;

	protected int MSG_STOP_SEVICE = 0x2;

	private static boolean hasStop = true;

	OkHttpClient mOkHttpClient;

	private String downUrl = "";

	private NotificationManager nm;

	NotificationCompat.Builder mBuilder;

	int notifyId = 108;

	private int progress = 0;
	
	public static class DownLoadInfo {
		public long fileSize;
		public long downloadSize;

		public DownLoadInfo(long fileSize, long downloadSize) {
			this.downloadSize = downloadSize;
			this.fileSize = fileSize;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE_PROGRESS && !hasStop) {
				setNotify(progress);
			} else if (msg.what == MSG_STOP_SEVICE) {
				stopSelf();
			}
		}
	};

	@Override
	public void onCreate() {
		hasStop = false;
		super.onCreate();
	}

	private void createNotification() {
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);

		mBuilder.setContentTitle("开始下载游戏盒子").setWhen(System.currentTimeMillis())
				.setSmallIcon(MResource.getIdByName(this, "drawable", "down_logo"))
				// .setPriority(Notification.PRIORITY_DEFAULT)
				.setProgress(100, progress, false);

		nm.notify(notifyId, mBuilder.build());
	}

	/** 设置下载进度 */
	public void setNotify(int progress) {
		if (progress == 100) {
			nm.cancel(notifyId);
			return;
		}
		mBuilder.setProgress(100, progress, false);
		nm.notify(notifyId, mBuilder.build());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		progress = 0;

		if (intent.getStringExtra("downUrl") != null && intent.getStringExtra("downUrl").length() > 0) {
			createNotification();
			downUrl = intent.getStringExtra("downUrl");

			if (mOkHttpClient == null) {
				mOkHttpClient = new OkHttpClient();
			}

			Request request = new Request.Builder().url(downUrl).tag(downUrl).build();
			mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {

				@Override
				public void onResponse(Call arg0, okhttp3.Response response) throws IOException {
					InputStream is = null;
					byte[] buf = new byte[1024 * 10];
					int len = 0;
					FileOutputStream fos = null;
					String apkPath = PathUtil.getApkPath("game_box");
					try {
						is = response.body().byteStream();
						long total = response.body().contentLength();
						File file = new File(apkPath);
						fos = new FileOutputStream(file);
						long sum = 0;
						while ((len = is.read(buf)) != -1) {
							fos.write(buf, 0, len);
							sum += len;
							progress = (int) (sum * 1.0f / total * 100);

							// 刷新数据
							Message msg = new Message();
							msg.what = MSG_UPDATE_PROGRESS;
							handler.sendMessage(msg);
						}
						fos.flush();
						Logger.msg("游戏盒子下载成功");
						
						// 刷新数据
						Message msg = new Message();
						msg.what = MSG_STOP_SEVICE;
						handler.sendMessage(msg);
						
						//判断下载的盒子文件是否完整，完整继续安装，不完整删除文件，提示用户重新下载
						if(file.exists()){
							if(ZipUtil.isArchiveFile(file)){
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
								startActivity(intent);
							}else{
								file.delete();//删除下载的错误的盒子文件，提示用户重新下载
								Util.toast(DownGameBoxService.this, "盒子文件错误，请重新下载");
							}
						}
					} catch (Exception e) {
						Logger.msg("游戏盒子下载失败");
						mBuilder.setContentTitle("游戏盒子下载失败").setProgress(0, 0, true);
						nm.notify(notifyId, mBuilder.build());
					} finally {
						try {
							if (is != null)
								is.close();
						} catch (IOException e) {
						}
						try {
							if (fos != null)
								fos.close();
						} catch (IOException e) {
						}
					}
				}

				@Override
				public void onFailure(Call arg0, IOException arg1) {
					Logger.msg("down_fail---");
				}
			});

		} else {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(DownGameBoxService.this, "下载地址错误，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			});
		}
		return 1;
	}

	public float getUpdateProgress() {
		return progress;
	}

	@Override
	public void onDestroy() {
		hasStop = true;
		cancel(downUrl);
		nm.cancel(Constants.NOTIFICATION_ID_UPDATE);
		super.onDestroy();
	}

	public void cancel(String url) {
		if (mOkHttpClient == null)
			throw new NullPointerException("client == null");

		for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
			if (url.equals(call.request().tag()))
				call.cancel();
		}
		for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
			if (url.equals(call.request().tag()))
				call.cancel();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public static boolean hasStop() {
		return hasStop;
	}

}
