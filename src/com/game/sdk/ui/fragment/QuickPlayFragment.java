package com.game.sdk.ui.fragment;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.game.sdk.view.NoticeDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 账号密码登录主界面
 * 
 * @author admin
 *
 */
public class QuickPlayFragment extends BaseFragment implements OnClickListener {

	LoginActivity loginActivity;
	
	private TextView changeAccountTv;

	private TextView quickRegisterTv;
	
	private LinearLayout quickRegisterLayout;
	
	private TextView playAccountTv;
	
	private Button intoGameBtn;
	
	private ImageView titleLogo;
	
	private RelativeLayout bgLayout;
	
	private LinearLayout titleLayout;
	
	private LinearLayout serverLayout;
	
	private TextView serviceTelTv;

	private TextView serviceQQTv;
	
	private ImageView registerIv;
	
	CustomDialog intoGameDialog;
	
	NoticeDialog noticeDialog;
	
	@Override
	public String getLayoutId() {
		return "quick_play_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		loginActivity = (LoginActivity) getActivity();
		changeAccountTv = findTextViewByString("change_account_tv");
		quickRegisterTv = findTextViewByString("quick_register_tv");
		playAccountTv = findTextViewByString("play_account_tv");
		intoGameBtn = findButtonByString("into_game_btn");
		
		quickRegisterLayout = (LinearLayout)findViewByString("quick_register_layout");
		
		titleLogo = findImageViewByString("quick_play_logo");
		bgLayout = (RelativeLayout)findViewByString("bg_layout");
		titleLayout = (LinearLayout) findViewByString("common_title_layout");
		serverLayout = (LinearLayout)findViewByString("service_number_layout");
		registerIv = findImageViewByString("register_icon");
		
		serviceTelTv = findTextViewByString("service_tel_tv");
		serviceQQTv = findTextViewByString("service_qq_tv");
		
		changeAccountTv.setOnClickListener(this);
		quickRegisterTv.setOnClickListener(this);
		intoGameBtn.setOnClickListener(this);
		quickRegisterLayout.setOnClickListener(this);
	}
	
	@Override
	public void initData() {
		super.initData();
		
		initTheme();
		
		if (GoagalInfo.inItInfo != null) {

			if (!StringUtils.isEmpty(GoagalInfo.inItInfo.tel)) {
				serviceTelTv.setText("客服电话：" + Html.fromHtml(GoagalInfo.inItInfo.tel));
			} else {
				serviceTelTv.setText(
						"客服电话:" + Html.fromHtml("<a href='tel://400-796-6071' style='text-decoration:none;'>"));
			}

			if (!StringUtils.isEmpty(GoagalInfo.inItInfo.qq)) {
				serviceQQTv.setText("客服QQ：" + Html.fromHtml(GoagalInfo.inItInfo.qq));
			} else {
				serviceQQTv.setText("客服QQ：3453725652");
			}

			NoUnderlineSpan mNoUnderlineSpan = new NoUnderlineSpan();
			if (serviceTelTv.getText() instanceof Spannable) {
				Spannable s = (Spannable) serviceTelTv.getText();
				s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
			}

			if (serviceQQTv.getText() instanceof Spannable) {
				Spannable s = (Spannable) serviceQQTv.getText();
				s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
			}
		} else {
			serviceTelTv.setText("客服电话：400-796-6071");
			serviceQQTv.setText("客服QQ：3453725652");
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(loginActivity);
		//MobclickAgent.onPageStart("QuickPlayFragment");
	}
	
	/**
	 * 初始化主题颜色
	 */
	public void initTheme(){
		if(GoagalInfo.inItInfo.logoBitmp != null){
			titleLogo.setImageBitmap(GoagalInfo.inItInfo.logoBitmp);
		}
		
		if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.template != null){
			String bgColor = GoagalInfo.inItInfo.template.bgColor;
			String headColor = GoagalInfo.inItInfo.template.headColor;
			String btnColor = GoagalInfo.inItInfo.template.btnColor;
			String noticeColor = GoagalInfo.inItInfo.template.noticeColor;
			String changeFontColor = GoagalInfo.inItInfo.template.fontColor;
			
			if(!StringUtils.isEmpty(bgColor)){
				GradientDrawable allBg = (GradientDrawable) bgLayout.getBackground();
				allBg.setColor(Color.parseColor("#" + bgColor));
				
				if(!StringUtils.isEmpty(headColor)){
					if(bgColor.equals(headColor)){
						GradientDrawable titleBg = (GradientDrawable) titleLayout.getBackground();
						titleBg.setColor(Color.parseColor("#00000000"));
					}else{
						GradientDrawable titleBg = (GradientDrawable) titleLayout.getBackground();
						titleBg.setColor(Color.parseColor("#" + GoagalInfo.inItInfo.template.headColor));
					}
				}else{
					GradientDrawable titleBg = (GradientDrawable) titleLayout.getBackground();
					titleBg.setColor(Color.parseColor("#00000000"));
				}
			}
			
			if(!StringUtils.isEmpty(btnColor)){
				GradientDrawable btnBg = (GradientDrawable) intoGameBtn.getBackground();
				btnBg.setColor(Color.parseColor("#" + btnColor));
				
				quickRegisterTv.setTextColor(Color.parseColor("#" + btnColor));
			}
			
			if(!StringUtils.isEmpty(noticeColor)){
				serverLayout.setBackgroundColor(Color.parseColor("#" + noticeColor));
			}
			
			if(!StringUtils.isEmpty(changeFontColor)){
				changeAccountTv.setTextColor(Color.parseColor("#" + changeFontColor));
				if(!StringUtils.isEmpty(GoagalInfo.userInfo.username)){
					playAccountTv.setText("账号："+GoagalInfo.userInfo.username);
					playAccountTv.setTextColor(Color.parseColor("#" + changeFontColor));
				}
			}
			
			if(GoagalInfo.inItInfo.registerBitmp != null){
				registerIv.setImageBitmap(GoagalInfo.inItInfo.registerBitmp);
			}
		}
		
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("change_account_tv")) {
			loginActivity.changeFragment(2);
			return;
		}
		
		if (v.getId() == findIdByString("quick_register_layout")) {
			loginActivity.changeFragment(3);
			return;
		}
		if (v.getId() == findIdByString("quick_register_tv")) {
			loginActivity.changeFragment(3);
			return;
		}
		if (v.getId() == findIdByString("into_game_btn")) {
			
			if (GoagalInfo.userInfo != null) {
				GoagalInfo.isLogin = true;
				LogincallBack logincallBack = new LogincallBack();
				
				logincallBack.username = GoagalInfo.userInfo.userId;
				logincallBack.userId = GoagalInfo.userInfo.userId;
				logincallBack.isBindPhone = GoagalInfo.userInfo.validateMobile == 1 ? true : false;
				logincallBack.logintime = GoagalInfo.userInfo.logintime;
				logincallBack.sign = GoagalInfo.userInfo.sign;
				
				//返回实名认证，生日
				logincallBack.isAuthenticated = GoagalInfo.userInfo.isAuthenticated == 0 ? false : true;
				logincallBack.birthday = GoagalInfo.userInfo.birthday;
				
				GoagalInfo.loginlistener.loginSuccess(logincallBack);
			} else {
				GoagalInfo.isLogin = false;
				
				Util.toast(loginActivity,"登录失败,请重试");
				
				LoginErrorMsg loginErrorMsg = new LoginErrorMsg(-1,"登录失败,请重试");
				GoagalInfo.loginlistener.loginError(loginErrorMsg);
			}
			
			if(!StringUtils.isEmpty(GoagalInfo.noticeMsg) && GoagalInfo.isLogin){
				noticeDialog = new NoticeDialog(loginActivity, GoagalInfo.noticeMsg);
				noticeDialog.show();
				noticeDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						((Activity)loginActivity).finish();
					}
				});
			}else{
				((Activity)loginActivity).finish();
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(loginActivity);
		//MobclickAgent.onPageEnd("QuickPlayFragment");
	}
	
	public class NoUnderlineSpan extends UnderlineSpan {
		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(ds.linkColor);
			ds.setUnderlineText(false);
		}
	}
}
