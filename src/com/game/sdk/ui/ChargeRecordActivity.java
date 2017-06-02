package com.game.sdk.ui;

import com.game.sdk.ui.fragment.BaseFragment;
import com.game.sdk.ui.fragment.ChargeRecordFailFragment;
import com.game.sdk.ui.fragment.ChargeRecordFragment;
import com.game.sdk.ui.fragment.ChargeRecordSuccessFragment;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChargeRecordActivity extends BaseActivity implements OnClickListener {

	private ImageView backIv;

	private TextView titleTv;

	public static String isnowpay = "0";

	public static String nowpayCode;

	public static String nowpayMsg;

	private PopupWindow gameSelectPop;

	private LinearLayout gameChargeLayout;

	private RelativeLayout titleLayout;

	private RelativeLayout currentGameLayout;

	private RelativeLayout allGameLayout;

	private ImageView currentGameSelectIcon;

	private ImageView allGameSelectIcon;

	private ImageView chargeSelectIv;

	private TextView chargeSuccessTv;

	private TextView chargeFailTv;

	private TextView chargeWaitPayTv;

	private int isAllGame = 0;// 0：当前游戏订单列表 1：所有游戏订单列表

	private int orderState = 4;// 订单状态: -1:全部, //
								// 0(待支付)，1(支付失败)，2(支付成功)，3(充值失败)，4(充值完成)

	private FragmentManager fm;

	private FragmentTransaction transaction;

	private ChargeRecordFragment waitPayFragment;

	private ChargeRecordSuccessFragment paySuccessFragment;
	
	private ChargeRecordFailFragment payFailFragment;
	
	private int currentSelect = 1;
	
	public interface PayResultListener{
		void payResult(int arg0, int arg1, Intent arg2);
	}
	
	private PayResultListener payResultListener;
	
	public PayResultListener getPayResultListener() {
		return payResultListener;
	}

	public void setPayResultListener(PayResultListener payResultListener) {
		this.payResultListener = payResultListener;
	}
	
	@Override
	public String getLayoutId() {
		return "fysdk_activity_charge_record";
	}

	@Override
	public void initVars() {
		super.initVars();
	}

	@Override
	public void initViews() {
		super.initViews();
		
		isAllGame = 0;
		orderState = 0;
		
		defaultFragment();

		titleLayout = (RelativeLayout) findViewById(MResource.getIdByName(this, "id", "common_title_layout"));
		gameChargeLayout = (LinearLayout) findViewById(MResource.getIdByName(this, "id", "game_charge_layout"));
		chargeSelectIv = findImageViewByString("charge_select_iv");

		chargeWaitPayTv = findTextViewByString("charge_wait_pay_tv");
		chargeSuccessTv = findTextViewByString("charge_success_tv");
		chargeFailTv = findTextViewByString("charge_fail_tv");

		View popView = getLayoutInflater().inflate(MResource.getIdByName(this, "layout", "charge_record_popwindow"),
				null);

		currentGameLayout = (RelativeLayout) popView
				.findViewById(MResource.getIdByName(this, "id", "sdk_current_game_layout"));
		allGameLayout = (RelativeLayout) popView.findViewById(MResource.getIdByName(this, "id", "sdk_all_game_layout"));

		currentGameSelectIcon = (ImageView) popView
				.findViewById(MResource.getIdByName(this, "id", "sdk_current_selected_icon"));
		allGameSelectIcon = (ImageView) popView
				.findViewById(MResource.getIdByName(this, "id", "sdk_all_selected_icon"));

		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		titleTv.setText(
				findStringByResId("sdk_current_game_charge_text") + "-" + findStringByResId("charge_record_text"));

		backIv.setOnClickListener(this);

		gameChargeLayout.setOnClickListener(this);

		currentGameLayout.setOnClickListener(this);
		allGameLayout.setOnClickListener(this);

		chargeWaitPayTv.setOnClickListener(this);
		chargeSuccessTv.setOnClickListener(this);
		chargeFailTv.setOnClickListener(this);

		gameSelectPop = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		gameSelectPop.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				chargeSelectIv.setBackgroundResource(
						MResource.getIdByName(ChargeRecordActivity.this, "drawable", "select_down_icon"));
			}
		});
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();
		
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}

	public void defaultFragment() {
		fm = getSupportFragmentManager();
		transaction = fm.beginTransaction();
		waitPayFragment = new ChargeRecordFragment();
		Bundle bundle = new Bundle();  
        bundle.putInt("isAllGame", isAllGame);
        bundle.putInt("orderState", orderState);
        waitPayFragment.setArguments(bundle);
		transaction.add(MResource.getIdByName(this, "id", "main_content"), waitPayFragment);
		transaction.commit();
	}

	public void changeFragment(int type,boolean isAll) {
		if (fm == null) {
			fm = getSupportFragmentManager();
		}

		transaction = fm.beginTransaction();
		
		if(waitPayFragment != null){
			transaction.hide(waitPayFragment);
		}
		
		if(paySuccessFragment != null){
			transaction.hide(paySuccessFragment);
		}
		
		if(payFailFragment != null){
			transaction.hide(payFailFragment);
		}
		
		switch (type) {
		case 1:
			if(waitPayFragment != null && !isAll){
				transaction.show(waitPayFragment);
			}else{
				waitPayFragment = new ChargeRecordFragment();
				Bundle bundle = new Bundle();  
		        bundle.putInt("isAllGame", isAllGame);
		        bundle.putInt("orderState", orderState);
		        waitPayFragment.setArguments(bundle);
				transaction.add(MResource.getIdByName(this, "id", "main_content"), waitPayFragment);
			}
			
			break;
		case 2:
			if(paySuccessFragment != null && !isAll){
				transaction.show(paySuccessFragment);
			}else{
				paySuccessFragment = new ChargeRecordSuccessFragment();
				Bundle bundle = new Bundle();  
		        bundle.putInt("isAllGame", isAllGame);
		        bundle.putInt("orderState", orderState);
		        paySuccessFragment.setArguments(bundle);
				transaction.add(MResource.getIdByName(this, "id", "main_content"), paySuccessFragment);
			}
			break;
		case 3:
			if(payFailFragment != null && !isAll){
				transaction.show(payFailFragment);
			}else{
				payFailFragment = new ChargeRecordFailFragment();
				Bundle bundle = new Bundle();  
		        bundle.putInt("isAllGame", isAllGame);
		        bundle.putInt("orderState", orderState);
		        payFailFragment.setArguments(bundle);
				transaction.add(MResource.getIdByName(this, "id", "main_content"), payFailFragment);
			}
			break;
		default:
			break;
		}
		transaction.commit();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			finish();
		}

		if (v.getId() == findIdByString("game_charge_layout")) {
			gameSelectPop.showAsDropDown(titleLayout, 0, 0);
			chargeSelectIv.setBackgroundResource(MResource.getIdByName(this, "drawable", "select_up_icon"));
		}

		if (v.getId() == findIdByString("sdk_current_game_layout")) {
			currentGameSelectIcon.setVisibility(View.VISIBLE);
			allGameSelectIcon.setVisibility(View.INVISIBLE);
			titleTv.setText(
					findStringByResId("sdk_current_game_charge_text") + "-" + findStringByResId("charge_record_text"));
			if (gameSelectPop != null && gameSelectPop.isShowing()) {
				gameSelectPop.dismiss();
			}
		}

		if (v.getId() == findIdByString("sdk_all_game_layout")) {
			allGameSelectIcon.setVisibility(View.VISIBLE);
			currentGameSelectIcon.setVisibility(View.INVISIBLE);
			titleTv.setText(
					findStringByResId("sdk_all_game_charge_text") + "-" + findStringByResId("charge_record_text"));
			if (gameSelectPop != null && gameSelectPop.isShowing()) {
				gameSelectPop.dismiss();
			}
		}

		// 当前游戏
		if (v.getId() == findIdByString("sdk_current_game_layout")) {
			isAllGame = 0;
			changeFragment(currentSelect,true);
		}
		// 所有游戏
		if (v.getId() == findIdByString("sdk_all_game_layout")) {
			isAllGame = 1;
			changeFragment(currentSelect,true);
		}
		// 待支付
		if (v.getId() == findIdByString("charge_wait_pay_tv")) {
			orderState = 0;
			
			chargeWaitPayTv.setBackgroundResource(MResource.getIdByName(this, "color", "account_register_color"));
			chargeSuccessTv.setBackgroundResource(MResource.getIdByName(this, "color", "white2"));
			chargeFailTv.setBackgroundResource(MResource.getIdByName(this, "color", "white2"));
			changeFragment(1,false);
			currentSelect = 1;
		}
		// 充值完成
		if (v.getId() == findIdByString("charge_success_tv")) {
			orderState = 4;
			
			chargeWaitPayTv.setBackgroundResource(MResource.getIdByName(this, "color", "white2"));
			chargeSuccessTv.setBackgroundResource(MResource.getIdByName(this, "color", "account_register_color"));
			chargeFailTv.setBackgroundResource(MResource.getIdByName(this, "color", "white2"));
			changeFragment(2,false);
			currentSelect = 2;
		}
		// 充值失败
		if (v.getId() == findIdByString("charge_fail_tv")) {
			orderState = 3;
			
			chargeWaitPayTv.setBackgroundResource(MResource.getIdByName(this, "color", "white2"));
			chargeSuccessTv.setBackgroundResource(MResource.getIdByName(this, "color", "white2"));
			chargeFailTv.setBackgroundResource(MResource.getIdByName(this, "color", "account_register_color"));
			changeFragment(3,false);
			currentSelect = 3;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ChargeRecordActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ChargeRecordActivity");
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		payResultListener.payResult(arg0, arg1, arg2);
		Logger.msg("ChargeRecordActivity---onActivityResult--->");
	}
}
