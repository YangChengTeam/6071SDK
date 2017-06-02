package com.game.sdk.view;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.MResource;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class AutoNoticeDialog extends Dialog implements android.view.View.OnClickListener{
	
	private Context mContext;
	
	private String notice;
	
	//private TextView noticeTv;
	
	private CustomWebView webview;
	
	private ImageView closeIv;
	
	public AutoNoticeDialog(Context context, String msg) {
		super(context, MResource.getIdByName(context, "style", "CustomSdkDialog"));
		this.mContext = context;
		this.notice = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	public void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(MResource.getIdByName(mContext, "layout", "notice_dialog"), null);
		
		closeIv = (ImageView) view.findViewById(MResource.getIdByName(mContext, "id", "close_icon"));
		webview = (CustomWebView)view.findViewById(MResource.getIdByName(mContext, "id", "notice_webview"));
		
		webview.loadData(notice, "text/html; charset=UTF-8", null);
		
		closeIv.setOnClickListener(this);
		setContentView(view);
		
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		
		if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 0){
			params.width = (int) (DimensionUtil.getWidth(mContext) * 0.65);
			params.height = (int) (DimensionUtil.getHeight(mContext) * 0.7);
		}
		
		if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 1){
			params.width = (int) (DimensionUtil.getWidth(mContext) * 0.8);
			params.height = (int) (DimensionUtil.getHeight(mContext) * 0.6);
		}
		
		params.gravity =Gravity.CENTER;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == MResource.getIdByName(mContext, "id", "close_icon")) {
			this.dismiss();
		}
	}
	
}
