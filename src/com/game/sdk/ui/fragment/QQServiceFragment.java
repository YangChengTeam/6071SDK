package com.game.sdk.ui.fragment;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.Util;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author admin
 *
 */
public class QQServiceFragment extends BaseFragment implements OnClickListener {

	private LoginActivity loginActivity;

	private ImageView backIv;

	private TextView titleTv;
	
	private LinearLayout firstQQLayout;
	
	private LinearLayout secondQQLayout;
	
	private String[] kefu = null;
 	
	@Override
	public String getLayoutId() {
		return "fysdk_qq_service_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		loginActivity = (LoginActivity) getActivity();
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		firstQQLayout = (LinearLayout)findViewByString("first_qq_service_layout");
		secondQQLayout = (LinearLayout)findViewByString("second_qq_service_layout");
		titleTv.setText(findStringByResId("service_qq_num_text"));
		backIv.setOnClickListener(this);
		firstQQLayout.setOnClickListener(this);
		secondQQLayout.setOnClickListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();
		
		kefu = kefuQQ(GoagalInfo.inItInfo.gameKefuQQ);
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//MobclickAgent.onResume(loginActivity);
		MobclickAgent.onPageStart("QQServiceFragment");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			if(GoagalInfo.qqKefuFrom == 0){	
				loginActivity.changeFragment(2);
			}
			
			if(GoagalInfo.qqKefuFrom == 1){
				loginActivity.changeFragment(3);
			}
		}
		
		if (v.getId() == findIdByString("first_qq_service_layout")) {
			if(kefu != null && kefu.length > 0){
				startQQ(kefu[0]);
			}
		}
		if (v.getId() == findIdByString("second_qq_service_layout")) {
			if(kefu != null && kefu.length > 1){
				startQQ(kefu[1]);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//MobclickAgent.onPause(loginActivity);
		MobclickAgent.onPageEnd("QQServiceFragment");
	}
	
	public String[] kefuQQ(String qqNumString){
		if(!StringUtils.isEmpty(qqNumString)){
			return qqNumString.split(",");
		}else{
			return null;
		}
	}
	
	public void startQQ(String qqNum){
       
		CheckUtil.setPackageNames(loginActivity);
		if (!CheckUtil.isQQAvilible(loginActivity)) {
			Util.toast(loginActivity, "请安装QQ");
		} else {
			String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum;
			loginActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}
        
	}
	
}
