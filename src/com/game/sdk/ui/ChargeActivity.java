package com.game.sdk.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.math.NumberUtils;

import com.alipay.sdk.app.PayTask;
import com.game.sdk.domain.ChargeMoneyList;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.PayInfo;
import com.game.sdk.engin.ChargeEngin;
import com.game.sdk.engin.ChargeMoneyEngin;
import com.game.sdk.engin.PayCoinEngin;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.security.Rsa;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.NetworkImpl;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.ThreadPoolManager;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.game.sdk.view.ServiceDialog;
import com.ipaynow.plugin.api.IpaynowPlugin;
import com.ipaynow.plugin.utils.PreSignMessageUtil;
import com.umeng.analytics.MobclickAgent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChargeActivity extends BaseActivity implements OnClickListener {

	private ImageView backIv;

	private Button rightBtn;

	private TextView platformMoneyTv;

	private TextView charge10Tv;

	private TextView charge50Tv;

	private TextView charge100Tv;

	private TextView charge200Tv;

	private TextView charge500Tv;

	// private TextView charge1000Tv;

	private TextView realMoney10Tv;

	private TextView realMoney50Tv;

	private TextView realMoney100Tv;

	private TextView realMoney200Tv;

	private TextView realMoney500Tv;

	// private TextView realMoney1000Tv;

	private LinearLayout charge10Layout;

	private LinearLayout charge50Layout;

	private LinearLayout charge100Layout;

	private LinearLayout charge200Layout;

	private LinearLayout charge500Layout;

	// private LinearLayout charge1000Layout;

	private EditText customMoneyEv;

	private Button chargeBtn;

	private RelativeLayout alipayLayout;

	private RelativeLayout wxpayLayout;

	private ImageView alipaySelectedIcon;

	private ImageView wxpaySelectedIcon;

	private TextView callServiceTv;

	// 充值金额
	private float chargeMoney = 0;

	public TextView[] tvs;

	public TextView[] realMoneyTvs;

	public LinearLayout[] chargeLayouts;

	public int[] chargeMoneys;

	public int[] realMoneys;

	private String payWay = Constants.ALIPAY_CR;

	private ChargeEngin chargeEngin;

	private PayCoinEngin payCoinEngin;

	private ChargeMoneyEngin chargeMoneyEngin;

	CustomDialog payDialog;

	CustomDialog reloadPayInfoDialog;
	
	private String orderid;

	private String productname;// 支付游戏名称

	private String productdesc;// 产品描述

	private String attach;// 游戏方传递的拓展参数

	public boolean ischarge = false;

	public static String isnowpay = "0";

	public static String nowpayCode;

	public static String nowpayMsg;

	private IpaynowPlugin mIpaynowplugin;

	private ServiceDialog serviceDialog;

	private boolean isReturnMoney = false;

	// 获得平台币金额
	private TextView realMoneyHintTv;

	// 赠送游戏币模块
	private LinearLayout giveGameMoneyLayout;

	// 赠送游戏币金额
	private TextView giveGameMoneyTv;

	// 实付
	private TextView realPayAmountTv;

	private String mixMoney;

	private String rateLow;

	private String rateHigh;

	InputMethodManager inputManager ;
	
	//是否自定义金额
	private boolean isCustomMoney = false;
	
	private LinearLayout serviceLayout;
	
	private LinearLayout explainLayout;
	
	private TextView explainTv;
	
	
	private LinearLayout chargeInfoLayout;
	
	private LinearLayout netErrorLayout;
	
	private Button refreshBtn;
	
	private boolean isPayInitOk = false;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 支付宝支付回调
					// ChargerActivity.paymentListener.paymentSuccess(callbackInfo);
				String result = (String) msg.obj;
				if (null != result) {
					String[] result_obj = result.split(";");
					int resultStatus;// 返回状态码
					String memo;// 提示信息
					resultStatus = Integer.parseInt(
							result_obj[0].substring(result_obj[0].indexOf("{") + 1, result_obj[0].lastIndexOf("}")));
					memo = result_obj[1].substring(result_obj[1].indexOf("{") + 1, result_obj[1].lastIndexOf("}"));
					if (resultStatus == 9000) {
						// 支付成功
						ischarge = true;

						Util.toast(ChargeActivity.this, "支付成功");

						// 更新平台币
						// new PayInitTask().execute();
						finish();

					} else {
						ischarge = false;
						Util.toast(ChargeActivity.this, memo);
					}
				} else {
					// 如果msg为null 是支付宝那边返回数据为null
					ischarge = false;
					Util.toast(ChargeActivity.this, "无法判别支付是否成功！具体请查看后台数据");
				}
				// ctx.finish();// 不管支付是否成功 直接退出游戏界面
				Logger.msg("result:" + result);
				break;
			case 1:
				
				if(isPayInitOk){
					chargeInfoLayout.setVisibility(View.VISIBLE);
					netErrorLayout.setVisibility(View.GONE);
					
					if (chargeMoneys != null && chargeMoneys.length > 0) {
						for (int i = 0; i < tvs.length; i++) {
							tvs[i].setText(chargeMoneys[i] + findStringByResId("charge_unit_text"));
						}
					}

					if (isReturnMoney) {
						giveGameMoneyLayout.setVisibility(View.VISIBLE);
					}
					
					//根据是否返利，显示不用的内容
					if(isReturnMoney){
						serviceLayout.setVisibility(View.GONE);
						explainLayout.setVisibility(View.VISIBLE);
						
						String html = "<div><font color=\"#8a8a8a\">1：充值金额≥30元才可享受充值福利。<br>"
								+ "<font>2：只有带返利标签的游戏才可享受充值福利。<br>"
								+ "<font>3：充值比例：1元=1平台币+1游戏币。<br>"
								+ "<font>4：平台币、游戏币区别:平台币可用于平台所有游戏,游戏币用于单款指定游戏。</font></div>";
						explainTv.setText(Html.fromHtml(html));
					}else{
						serviceLayout.setVisibility(View.VISIBLE);
						explainLayout.setVisibility(View.GONE);
					}
				}else{
					chargeInfoLayout.setVisibility(View.GONE);
					netErrorLayout.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "fysdk_activity_charge";
	}

	@Override
	public void initVars() {
		super.initVars();
		mIpaynowplugin = IpaynowPlugin.getInstance().init(this);// 1.插件初始化
		mIpaynowplugin.unCkeckEnvironment();
	}

	@Override
	public void initViews() {
		super.initViews();
		productname = "平台币";
		productdesc = productname;
		attach = "{\"gameid\" :" + 0 + "}";
		serviceDialog = new ServiceDialog(this, 0.8f);
		payDialog = new CustomDialog(this, "正在充值");
		reloadPayInfoDialog = new CustomDialog(this, "重新加载中");
		backIv = findImageViewByString("back_iv");
		rightBtn = findButtonByString("right_btn");
		rightBtn.setVisibility(View.VISIBLE);

		platformMoneyTv = findTextViewByString("platform_money_tv");
		charge10Tv = findTextViewByString("charge_10_tv");
		charge50Tv = findTextViewByString("charge_50_tv");
		charge100Tv = findTextViewByString("charge_100_tv");
		charge200Tv = findTextViewByString("charge_200_tv");
		charge500Tv = findTextViewByString("charge_500_tv");
		// charge1000Tv = findTextViewByString("charge_1000_tv");

		realMoney10Tv = findTextViewByString("real_money_10_tv");
		realMoney50Tv = findTextViewByString("real_money_50_tv");
		realMoney100Tv = findTextViewByString("real_money_100_tv");
		realMoney200Tv = findTextViewByString("real_money_200_tv");
		realMoney500Tv = findTextViewByString("real_money_500_tv");
		// realMoney1000Tv = findTextViewByString("real_money_1000_tv");

		charge10Layout = (LinearLayout) findViewByString("charge_10_layout");
		charge50Layout = (LinearLayout) findViewByString("charge_50_layout");
		charge100Layout = (LinearLayout) findViewByString("charge_100_layout");
		charge200Layout = (LinearLayout) findViewByString("charge_200_layout");
		charge500Layout = (LinearLayout) findViewByString("charge_500_layout");
		// charge1000Layout = (LinearLayout)
		// findViewByString("charge_1000_layout");

		realMoneyHintTv = findTextViewByString("real_money_hint_tv");
		giveGameMoneyLayout = (LinearLayout) findViewByString("give_game_money_layout");
		giveGameMoneyTv = findTextViewByString("give_game_money_tv");
		realPayAmountTv = findTextViewByString("real_pay_amount_tv");

		customMoneyEv = findEditTextByString("custom_money_ev");

		alipayLayout = (RelativeLayout) findViewByString("alipay_layout");
		wxpayLayout = (RelativeLayout) findViewByString("wxpay_layout");

		serviceLayout = (LinearLayout) findViewByString("service_layout");
		explainLayout = (LinearLayout) findViewByString("charge_explain_layout");
		
		callServiceTv = findTextViewByString("call_service_tv");
		explainTv = findTextViewByString("explain_tv");
		
		alipaySelectedIcon = findImageViewByString("alipay_selected_icon");
		wxpaySelectedIcon = findImageViewByString("wxpay_selected_icon");

		chargeInfoLayout = (LinearLayout) findViewByString("charge_info_layout");
		netErrorLayout = (LinearLayout) findViewByString("net_error_layout");
		refreshBtn = findButtonByString("refresh_net_btn");
		
		chargeBtn = findButtonByString("charge_btn");
		backIv.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
		
		charge10Layout.setOnClickListener(this);
		charge50Layout.setOnClickListener(this);
		charge100Layout.setOnClickListener(this);
		charge200Layout.setOnClickListener(this);
		charge500Layout.setOnClickListener(this);
		// charge1000Layout.setOnClickListener(this);

		alipayLayout.setOnClickListener(this);
		wxpayLayout.setOnClickListener(this);
		chargeBtn.setOnClickListener(this);
		callServiceTv.setOnClickListener(this);
		customMoneyEv.setOnClickListener(this);
		tvs = new TextView[] { charge10Tv, charge50Tv, charge100Tv, charge200Tv, charge500Tv };

		realMoneyTvs = new TextView[] { realMoney10Tv, realMoney50Tv, realMoney100Tv, realMoney200Tv, realMoney500Tv };
		chargeLayouts = new LinearLayout[] { charge10Layout, charge50Layout, charge100Layout, charge200Layout,
				charge500Layout };

		inputManager = (InputMethodManager) customMoneyEv.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		customMoneyEv.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s != null && s.toString().length() > 0) {

					if (s.toString().substring(0, 1).equals(".")) {
						customMoneyEv.setText("");
						return;
					}

					for (int i = 0; i < tvs.length; i++) {

						if (Float.parseFloat(s.toString()) == chargeMoneys[i]) {
							chargeLayouts[i].setSelected(true);
						} else {
							chargeLayouts[i].setSelected(false);
						}
					}

					realMoneyHintTv.setText(s.toString());
					realPayAmountTv.setText(s.toString());

					if (isReturnMoney) {
						int gameMoney = countGameMoney(s.toString(), mixMoney, rateLow, rateHigh);
						giveGameMoneyTv.setText(gameMoney + "");
					}
				} else {
					if (StringUtils.isEmpty(customMoneyEv.getText()) && isCustomMoney) {
						// 设置为初始值
						realMoneyHintTv.setText("0");
						realPayAmountTv.setText("0");
						giveGameMoneyTv.setText("0");
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		setPlatformMoney();

		new ChargeMoneyInitTask().execute();
	}

	/**
	 * 计算获得的游戏币
	 * 
	 * @param customMoney
	 *            自定义金额
	 * @param mixMoney
	 *            阈值
	 * @param rateLow
	 *            阈值下比例
	 * @param rateHigh
	 *            阈值上比例
	 * @return
	 */
	public int countGameMoney(String customMoney, String mixMoney, String rateLow, String rateHigh) {
		float gameMoney = 0;

		if (StringUtils.isEmpty(mixMoney)) {
			return 0;
		}
		if (StringUtils.isEmpty(rateLow)) {
			return 0;
		}
		if (StringUtils.isEmpty(rateHigh)) {
			return 0;
		}

		if (!StringUtils.isEmpty(customMoney)) {

			float cMoney = Float.parseFloat(customMoney);// 充值金额
			float mMoney = Float.parseFloat(mixMoney);// 返利游戏币阈值

			if (cMoney < mMoney) {
				gameMoney = cMoney * Float.parseFloat(rateLow);
			} else {
				gameMoney = cMoney * Float.parseFloat(rateHigh);
			}
			
			//30以下，不返利游戏币
			if(cMoney < 30){
				gameMoney = 0;
			}
		}

		return (int) gameMoney;
	}

	// 设置平台币
	public void setPlatformMoney() {
		platformMoneyTv.setText(!StringUtils.isEmpty(GoagalInfo.userInfo.ttb) ? GoagalInfo.userInfo.ttb : "0");
	}

	private class ChargeMoneyInitTask extends AsyncTask<String, Integer, ChargeMoneyList> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ChargeMoneyList doInBackground(String... params) {
			chargeMoneyEngin = new ChargeMoneyEngin(ChargeActivity.this);
			return chargeMoneyEngin.run();
		}

		@Override
		protected void onPostExecute(ChargeMoneyList result) {
			super.onPostExecute(result);
			
			if(reloadPayInfoDialog != null && reloadPayInfoDialog.isShowing()){
				reloadPayInfoDialog.dismiss();
			}
			
			if (result != null) {
				// 是否开启返利游戏币功能
				isReturnMoney = result.isOpen;
				mixMoney = result.mixMoney;
				rateLow = result.rateLow;
				rateHigh = result.rateHigh;

				if (result.chargeMoneyList != null && result.chargeMoneyList.size() > 0) {
					chargeMoneys = new int[result.chargeMoneyList.size()];

					// if (result.isOpen) {
					realMoneys = new int[result.chargeMoneyList.size()];
					// }
					
					try{
						for (int i = 0; i < result.chargeMoneyList.size(); i++) {
							if (result.chargeMoneyList.get(i) != null
									&& result.chargeMoneyList.get(i).chargeMoney != null) {
								chargeMoneys[i] = Integer.parseInt(result.chargeMoneyList.get(i).chargeMoney);
								isPayInitOk = true;
							}

							if (realMoneys != null && realMoneys.length > 0 && result.chargeMoneyList.get(i) != null
									&& result.chargeMoneyList.get(i).returnGameMoney != null) {
								realMoneys[i] = Integer.parseInt(result.chargeMoneyList.get(i).returnGameMoney);
								isPayInitOk = true;
							}
						}
					}catch(NumberFormatException e){
						Logger.msg("pay init NumberFormatException --->");
						e.printStackTrace();
					}
				}
			}

			/*// 如果从服务器未获取到值，使用默认值
			if (chargeMoneys == null || chargeMoneys.length == 0) {
				chargeMoneys = new int[] { 10, 50, 100, 200, 500, 1000 };
			}

			// 如果从服务器未获取到值，使用默认值
			if (realMoneys == null || realMoneys.length == 0) {
				realMoneys = new int[] { 10, 50, 100, 200, 500, 1000 };
			}*/
			
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);
		}
	}

	@Override
	public void initData() {
		super.initData();
		initTheme();
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {

		if (GoagalInfo.inItInfo != null) {
			if (GoagalInfo.inItInfo.template != null) {
				String btnColor = GoagalInfo.inItInfo.template.btnColor;
				if (!StringUtils.isEmpty(btnColor)) {

					int roundRadius = DimensionUtil.dip2px(this, 3); // 8dp 圆角半径
					// 默认颜色
					int fillColor = Color.parseColor("#" + btnColor);// 内部填充颜色
					// 按压后颜色
					int fillColorPressed = Color.parseColor("#979696");

					// 默认
					GradientDrawable gdNormal = new GradientDrawable();
					gdNormal.setColor(fillColor);
					gdNormal.setCornerRadius(roundRadius);

					// 按压后
					GradientDrawable gdPressed = new GradientDrawable();
					gdPressed.setColor(fillColorPressed);
					gdPressed.setCornerRadius(roundRadius);

					StateListDrawable stateDrawable = new StateListDrawable();

					// 获取对应的属性值 Android框架自带的属性 attr
					int pressed = android.R.attr.state_pressed;
					int window_focused = android.R.attr.state_window_focused;
					int focused = android.R.attr.state_focused;
					int selected = android.R.attr.state_selected;

					stateDrawable.addState(new int[] { pressed, window_focused }, gdPressed);
					stateDrawable.addState(new int[] { pressed, -focused }, gdPressed);
					stateDrawable.addState(new int[] { selected }, gdPressed);
					stateDrawable.addState(new int[] { focused }, gdPressed);
					stateDrawable.addState(new int[] { -selected, -focused, -pressed }, gdNormal);

					chargeBtn.setBackgroundDrawable(stateDrawable);

				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		
		// 充值
		if (v.getId() == findIdByString("charge_btn")) {

			if (!NetworkImpl.isNetWorkConneted(this)) {
				Util.toast(this, "网络不给力，请检查网络设置");
				return;
			}
			if (!GoagalInfo.isLogin) {
				Util.toast(this, "请先登录");
				return;
			}
			
			if(isCustomMoney){
				if (null == customMoneyEv.getText() || "".equals(customMoneyEv.getText().toString())
						|| !NumberUtils.isNumber(customMoneyEv.getText().toString())) {
					Util.toast(this, "请输入金额，金额为数字");
					return;
				}
				
				chargeMoney = Float.parseFloat(customMoneyEv.getText().toString());
			}else{
				if(chargeMoney == 0){
					Util.toast(this, "请选择充值金额");
					return;
				}
			}
			
			if (payWay.equals(Constants.ALIPAY_CR)) {
				/*
				 * if (chargeMoney > 2000) { Util.toast(this,
				 * "支付宝单次充值金额不能超过2000"); return; }
				 */

				new PayInfoTask(ChargeActivity.this, GoagalInfo.userInfo.userId, 0, payWay, chargeMoney, "").execute();
			}

			if (payWay.equals(Constants.WXPAY_CR)) {
				prePayMessage();
				preSign.mhtOrderNo = "{orderid}";
				ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = manager.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 支付类型： 微信支付
					preSign.payChannelType = "13";
					// 生成代签名
					preSignStr = preSign.generatePreSignMessage();
				}

				new PayInfoTask(ChargeActivity.this, GoagalInfo.userInfo.userId, 0, payWay, chargeMoney, preSignStr)
						.execute();
			}

			payDialog.show();
		}

		if (v.getId() == findIdByString("alipay_layout")) {
			alipaySelectedIcon.setVisibility(View.VISIBLE);
			wxpaySelectedIcon.setVisibility(View.INVISIBLE);
			payWay = Constants.ALIPAY_CR;
		}

		if (v.getId() == findIdByString("wxpay_layout")) {
			alipaySelectedIcon.setVisibility(View.INVISIBLE);
			wxpaySelectedIcon.setVisibility(View.VISIBLE);
			payWay = Constants.WXPAY_CR;
		}

		if (v.getId() != findIdByString("charge_btn") && v.getId() != findIdByString("alipay_layout")
				&& v.getId() != findIdByString("wxpay_layout") && v.getId() != findIdByString("back_iv")
				&& v.getId() != findIdByString("custom_money_ev") && v.getId() != findIdByString("refresh_net_btn")) {
			selectText(v.getId());
			// 隐藏键盘
			inputManager.hideSoftInputFromWindow(customMoneyEv.getWindowToken(), 0);
		}

		if (v.getId() == findIdByString("custom_money_ev")) {
			isCustomMoney = true;
			for (int i = 0; i < chargeLayouts.length; i++) {
				chargeLayouts[i].setSelected(false);
			}

			// 设置为初始值
			if (StringUtils.isEmpty(customMoneyEv.getText())) {
				realMoneyHintTv.setText("0");
				realPayAmountTv.setText("0");
				giveGameMoneyTv.setText("0");
			}

			customMoneyEv.setSelected(true);
			customMoneyEv.setFocusableInTouchMode(true);
			customMoneyEv.setFocusable(true);
			customMoneyEv.setClickable(true);
			customMoneyEv.requestFocus();
			
			inputManager.showSoftInput(customMoneyEv, 0);
		}

		if (v.getId() == findIdByString("back_iv")) {
			this.finish();
		}

		if (v.getId() == findIdByString("call_service_tv")) {
			serviceDialog.setCanceledOnTouchOutside(true);
			serviceDialog.show();
		}

		if (v.getId() == findIdByString("right_btn")) {
			Intent intent = new Intent(ChargeActivity.this, ChargeRecordActivity.class);
			startActivity(intent);
		}
		
		//网络错误，重新刷新
		if (v.getId() == findIdByString("refresh_net_btn")) {
			
			if (!NetworkImpl.isNetWorkConneted(ChargeActivity.this)) {
				Util.toast(ChargeActivity.this, "网络不给力，请检查网络设置");
				return;
			}
			
			reloadPayInfoDialog.show();
			
			new ChargeMoneyInitTask().execute();
		}
	}

	public void selectText(int selectId) {
		customMoneyEv.setFocusable(false);
		customMoneyEv.setSelected(false);
		for (int i = 0; i < chargeLayouts.length; i++) {
			if (chargeLayouts[i].getId() == selectId) {
				chargeMoney = chargeMoneys[i];
				// customMoneyEv.setText(chargeMoney + "");
				realMoneyHintTv.setText((int)chargeMoney + "");
				realPayAmountTv.setText((int)chargeMoney + "");// 实付
				if (realMoneys != null && i < realMoneys.length) {
					giveGameMoneyTv.setText((int) realMoneys[i] + "");
				}
				chargeLayouts[i].setSelected(true);
			} else {
				chargeLayouts[i].setSelected(false);
			}
		}
		isCustomMoney = false;
		customMoneyEv.setText("");
	}

	private class PayInfoTask extends AsyncTask<String, Integer, PayInfo> {

		private Context pContext;

		public String userId;

		public int payType;

		public String payWay;

		public double chargeMoney = 0;

		public String md5signstr;

		public PayInfoTask(Context context, String userId, int payType, String payWay, double chargeMoney,
				String md5signstr) {
			this.pContext = context;
			this.userId = userId;
			this.payType = payType;
			this.payWay = payWay;
			this.chargeMoney = chargeMoney;
			this.md5signstr = md5signstr;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected PayInfo doInBackground(String... params) {
			chargeEngin = new ChargeEngin(pContext, userId, payType, payWay, chargeMoney, md5signstr);
			return chargeEngin.pay();
		}

		@Override
		protected void onPostExecute(PayInfo result) {
			super.onPostExecute(result);
			payDialog.dismiss();
			if (result != null) {
				Logger.msg("支付信息获取成功----");
				orderid = result.orderSn;
				if (result.params != null) {
					if (payWay.equals(Constants.ALIPAY_CR)) {
						payAlipayMoney(result.params.partnerid, result.params.email, result.params.privatekey);
					}

					if (payWay.equals(Constants.WXPAY_CR)) {
						// payAlipayMoney(result.params.partnerid,
						// result.params.email, result.params.privatekey);

						preSign.mhtOrderNo = orderid;
						preSign.appId = result.params.partnerid;
						preSign.mhtOrderStartTime = result.starttime;

						preSignStr = preSign.generatePreSignMessage();
						String mhtSignature = preSignStr + "&mhtSignature=" + result.rsmd5 + "&mhtSignType=MD5";
						try {
							mIpaynowplugin.setCallResultActivity(ChargeActivity.this).pay(mhtSignature);
							isnowpay = "1";
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				Logger.msg("支付信息获取失败----");
			}
		}
	}

	/**
	 * 支付宝支付金额
	 * 
	 * @param partnerId
	 * @param email
	 * @param privateKey
	 */
	public void payAlipayMoney(String partnerId, String email, String privateKey) {
		try {
			privateKey = Util.getKey(privateKey);
			Logger.msg(privateKey);
			String info = getNewOrderInfo(partnerId, email);
			String sign = Rsa.sign(info, privateKey);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();
			final String orderInfo = info;
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					// 构造PayTask 对象
					PayTask alipay = new PayTask(ChargeActivity.this);
					// 调用支付接口
					String result = alipay.pay(orderInfo);

					Message msg = new Message();
					msg.what = 0;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
			Util.toast(this, "支付失败");
		}
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	private String getNewOrderInfo(String partnerId, String email) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\""); // 合作者身份id 等待服务端传递
		sb.append(partnerId);
		sb.append("\"&out_trade_no=\"");// 订单号，等待服务端传递
		sb.append(orderid);
		sb.append("\"&subject=\"");
		sb.append(Uri.decode(productname));
		sb.append("\"&body=\"");
		sb.append(Uri.decode(productdesc));
		sb.append("\"&total_fee=\"");
		sb.append(chargeMoney);
		sb.append("\"&notify_url=\"");
		// 网址需要做URL编码
		String url = ServerConfig.getPayCallUrl(payWay);
		if (Constants.DEBUG) {
			url = url.replace("http://api.6071.com/", "http://sdk.289.com/Api/");
		}
		Logger.msg(url);
		sb.append(URLEncoder.encode(url, "UTF-8"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		// sb.append("\"&return_url=\"");
		// sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\""); // 卖家支付宝账号，等待服务端传递
		sb.append(email);
		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");
		return new String(sb);
	}

	// 微信支付
	private PreSignMessageUtil preSign = new PreSignMessageUtil();
	private static ProgressDialog progressDialog;
	private static String preSignStr = null;

	private void prePayMessage() {
		preSign.appId = "{appid}"; // preSign.appId="1448005170893457";
		preSign.mhtCharset = "UTF-8";
		preSign.mhtCurrencyType = "156";
		// 支付金额
		preSign.mhtOrderAmt = Integer.toString((int) (chargeMoney * 100));

		preSign.mhtOrderDetail = productdesc;

		preSign.mhtOrderName = productname;

		preSign.mhtOrderStartTime = preSign.mhtOrderStartTime = "{starttime}"; // new
																				// SimpleDateFormat("yyyyMMddHHmmss",Locale.CHINA).format(new
																				// Date());

		preSign.mhtOrderTimeOut = "3600";
		preSign.mhtOrderType = "01";
		preSign.mhtReserved = attach;

		String url = ServerConfig.getPayCallUrl(payWay);
		Logger.msg(url);
		preSign.notifyUrl = url;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ChargeActivity");
		MobclickAgent.onResume(this);

		MobclickAgent.onEvent(this, "sdk_open_charge_page");

		if (isnowpay.equals("2")) {
			if (nowpayCode.equals("00")) {
				Util.toast(this, "支付成功");
				// new PayInitTask().execute();
				finish();
			}
			if (nowpayCode.equals("02")) {
				Util.toast(this, "支付取消");
			}
			if (nowpayCode.equals("01")) {
				Util.toast(this, nowpayMsg);
			}
			isnowpay = "0";
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		super.onActivityResult(arg0, arg1, data);
		if (isnowpay.equals("1")) {
			nowpayCode = data.getExtras().getString("respCode");
			nowpayMsg = data.getExtras().getString("respMsg");
			isnowpay = "2";
		}
	}

	private class PayInitTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			payCoinEngin = new PayCoinEngin(ChargeActivity.this, GoagalInfo.userInfo.userId);
			return payCoinEngin.run();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ChargeActivity");
		MobclickAgent.onPause(this);
	}
}
