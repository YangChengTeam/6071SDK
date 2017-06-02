package com.game.sdk.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.service.DownGameBoxService;
import com.game.sdk.ui.GamePackageDetailActivity.DownAsyncTask;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.PathUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.game.sdk.utils.ZipUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class BoxDownDialog extends Dialog implements android.view.View.OnClickListener {

	private Context mContext;

	private Button boxDownBtn;

	private Button boxCancelBtn;

	public String orderId;

	public BoxDownDialog(Context context) {
		super(context, MResource.getIdByName(context, "style", "CustomSdkDialog"));
		this.mContext = context;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	public void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);

		View view = layoutInflater.inflate(MResource.getIdByName(mContext, "layout", "fysdk_box_down_dialog"), null);
		boxDownBtn = (Button) view.findViewById(MResource.getIdByName(mContext, "id", "box_down_btn"));
		boxCancelBtn = (Button) view.findViewById(MResource.getIdByName(mContext, "id", "box_cancel_btn"));

		setContentView(view);

		boxDownBtn.setOnClickListener(this);
		boxCancelBtn.setOnClickListener(this);
	}

	List<String> imgs = new ArrayList<String>();

	@Override
	public void onClick(View v) {

		if (v.getId() == MResource.getIdByName(mContext, "id", "box_down_btn")) {
			gameBoxDown();
			this.dismiss();
		}

		if (v.getId() == MResource.getIdByName(mContext, "id", "box_cancel_btn")) {
			this.dismiss();
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
				mContext.startActivity(intent);
			}
		}
	}
	
	public void gameBoxDown() {
		if (!SystemUtil.isServiceWork(mContext, "com.game.sdk.service.DownGameBoxService")) {
			// 如果下载文件存在，直接启动安装
			File downFile = new File(PathUtil.getApkPath("game_box"));
			if (downFile.exists()) {

				if (ZipUtil.isArchiveFile(downFile)) {
					if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo != null && !StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)) {
						new DownAsyncTask().execute();
					}
				} else {
					downFile.delete();// 删除下载的错误的盒子文件，提示用户重新下载
					Util.toast(mContext, "盒子文件错误，请重新下载");
				}

			} else {
				downBoxApp();
			}
		} else {
			Util.toast(mContext, "游戏盒子下载中");
		}
	}

	public void downBoxApp() {
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo != null
				&& !StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)) {
			Intent intent = new Intent(mContext, DownGameBoxService.class);
			intent.putExtra("downUrl", GoagalInfo.inItInfo.boxInfo.boxDownUrl);
			mContext.startService(intent);
		} else {
			Util.toast(mContext, "下载地址有误，请稍后重试");
		}
	}
	
}
