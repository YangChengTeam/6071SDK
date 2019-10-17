package com.game.sdk.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.game.sdk.domain.CouponInfo;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.domain.PayInfo;
import com.game.sdk.domain.PayRequestParams;
import com.game.sdk.domain.PayValidateResult;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentCancelMsg;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.engin.ChargeEngin;
import com.game.sdk.engin.PayCancelEngin;
import com.game.sdk.engin.PayCoinEngin;
import com.game.sdk.engin.PayValidateEngin;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.security.Rsa;
import com.game.sdk.ui.adapter.CouponListAdapter;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.ThreadPoolManager;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.game.sdk.view.PayResultDialog;
import com.game.sdk.view.PayResultDialog.PayResultListener;
import com.ipaynow.plugin.api.IpaynowPlugin;
import com.ipaynow.plugin.utils.PreSignMessageUtil;
import com.ss.android.common.lib.EventUtils;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PayActivity extends BaseActivity implements OnClickListener, PayResultListener {

	private LayoutInflater inflater;

	public static OnPaymentListener paymentListener;// 充值接口监听

	private ImageView closeIv;

	private TextView productNameTv;

	private TextView orderAmountTv;

	private TextView realPayAmountTv;

	private TextView couponUseInfoTv;

	private TextView platformMoneyTv;

	private TextView gameMoneyTv;

	private LinearLayout payLayout;

	private LinearLayout moreCouponLayout;

	private TextView couponCountTv;

	private RelativeLayout couponLayout;

	private CheckBox couponCheckBox;

	private CheckBox platformCoinCheckBox;

	private CheckBox gameCoinCheckBox;

	private CouponListAdapter adapter;

	private PopupWindow pwCoupon;

	private LinearLayout alipayLayout;

	private LinearLayout wxpayLayout;

	private TextView payGameBtn;

	private List<CouponInfo> couponInfoList;

	private PayCoinEngin payCoinEngin;

	private PayValidateEngin payValidateEngin;

	private String payWay = Constants.ALIPAY_CR;

	private ChargeEngin chargeEngin;

	private CouponInfo currentCoupon;

	private CouponInfo lastCoupon;

	private float amount;

	private float totalPrice = 0;

	private String role;

	private String server;

	private String appid;

	private String productname;

	private String productdesc;

	private String attach;

	private String orderid;

	CustomDialog payGameDialog;

	public static String isnowpay = "0";

	public static String nowpayCode;

	public static String nowpayMsg;

	PayResultDialog payResultDialog;

	private IpaynowPlugin mIpaynowplugin;

	// 验证订单执行的次数，如果失败一次，再执行一次
	private int validateCount = 1;

	private int cancelType;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
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
						if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
							if(amount > 1) {
								EventUtils.setPurchase("","","",1,payWay,"rmb",true,(int)amount);
								Logger.msg("TeaAgent Pay True");
							}else {
								Logger.msg("TeaAgent Pay amount <=1 ");
							}
						}
						PaymentCallbackInfo pci = new PaymentCallbackInfo();
						pci.money = amount;
						pci.msg = memo;
						// Util.toast(PayActivity.this, "支付成功");

						// 验证订单
						new PayValidateTask().execute();

						if (GoagalInfo.paymentListener != null) {
							GoagalInfo.paymentListener.paymentSuccess(pci);
						}

						// finish();
						finishSuccess();
					} else {
						if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
							EventUtils.setPurchase("","","",1,payWay,"rmb",false,(int)amount);
					        Logger.msg("TeaAgent Pay False");
						}
						PaymentErrorMsg msg_e = new PaymentErrorMsg();
						msg_e.code = resultStatus;
						msg_e.msg = memo;
						msg_e.money = amount;

						// Util.toast(PayActivity.this, memo);
						if (GoagalInfo.paymentListener != null) {
							GoagalInfo.paymentListener.paymentError(msg_e);
						}

						if (SystemUtil.isValidContext(PayActivity.this)) {
							if (payResultDialog != null && !payResultDialog.isShowing()) {
								payResultDialog.show();
							}
						}
					}
				} else {
					if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
						EventUtils.setPurchase("","","",1,payWay,"rmb",false,(int)amount);
						Logger.msg("TeaAgent Pay False");
					}
					// 如果msg为null 是支付宝那边返回数据为null
					PaymentErrorMsg msg_e = new PaymentErrorMsg();
					msg_e.code = 88888888;
					msg_e.msg = "无法判别支付是否成功！具体请查看后台数据";
					msg_e.money = amount;
					// Util.toast(PayActivity.this, msg_e.msg);

					if (GoagalInfo.paymentListener != null) {
						GoagalInfo.paymentListener.paymentError(msg_e);
					}

					if (SystemUtil.isValidContext(PayActivity.this)) {
						if (payResultDialog != null && !payResultDialog.isShowing()) {
							payResultDialog.show();
						}
					}
				}
				// ctx.finish();// 不管支付是否成功 直接退出游戏界面
				Logger.msg("result:" + result);
				break;
			case 1:
				break;
			case 2:

				if (!StringUtils.isEmpty(GoagalInfo.userInfo.gttb) && Float.parseFloat(GoagalInfo.userInfo.gttb) > 0) {
					gameMoneyTv.setText(GoagalInfo.userInfo.gttb);
				} else {
					gameMoneyTv.setText("0");
					gameCoinCheckBox.setClickable(false);
				}

				if (!StringUtils.isEmpty(GoagalInfo.userInfo.ttb) && Float.parseFloat(GoagalInfo.userInfo.ttb) > 0) {
					platformMoneyTv.setText(GoagalInfo.userInfo.ttb);
				} else {
					platformMoneyTv.setText("0");
					platformCoinCheckBox.setClickable(false);
				}

				couponCountTv.setText(GoagalInfo.couponCount + "");

				break;
			case 3:
				if (msg.obj != null && !msg.obj.toString().equals("")) {
					Util.toast(PayActivity.this, msg.obj.toString());
				} else {
					Util.toast(PayActivity.this, "支付成功");
				}
				break;
			case 4:
				validateCount++;
				// 再次验证订单(因为网络延迟问题，多请求一次)
				new PayValidateTask().execute();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "fysdk_activity_payment";
	}

	@Override
	public void initVars() {
		super.initVars();

		setOrientation();
		if (mIpaynowplugin == null) {
			mIpaynowplugin = IpaynowPlugin.getInstance().init(this);// 1.插件初始化
			mIpaynowplugin.unCkeckEnvironment();
		}

		Logger.msg("initVars width---" + DimensionUtil.getWidth(PayActivity.this));
	}

	@Override
	public void initViews() {
		super.initViews();
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		payLayout = (LinearLayout) findViewByString("pay_layout");

		couponLayout = (RelativeLayout) findViewByString("coupon_layout");
		moreCouponLayout = (LinearLayout) findViewByString("more_coupon_layout");
		couponCountTv = (TextView) findTextViewByString("num_tv");
		alipayLayout = (LinearLayout) findViewByString("alipay_layout");
		wxpayLayout = (LinearLayout) findViewByString("wxpay_layout");
		// 默认设置为支付宝支付
		alipayLayout.setSelected(true);

		couponCheckBox = (CheckBox) findViewByString("coupon_icon");
		platformCoinCheckBox = (CheckBox) findViewByString("platform_icon");
		gameCoinCheckBox = (CheckBox) findViewByString("game_icon");
		closeIv = findImageViewByString("close_iv");

		productNameTv = findTextViewByString("product_name_tv");
		orderAmountTv = findTextViewByString("order_amount_tv");
		realPayAmountTv = findTextViewByString("real_pay_amount_tv");

		couponUseInfoTv = findTextViewByString("coupon_use_info_tv");
		platformMoneyTv = findTextViewByString("platform_money_tv");
		gameMoneyTv = findTextViewByString("game_money_tv");
		payGameBtn = findTextViewByString("pay_game_btn");
		payGameBtn.setOnClickListener(new NoDoubleClickListener() {

			@Override
			public void onNoDoubleClick(View v) {
				pay();
			}
		});
		closeIv.setOnClickListener(this);
		moreCouponLayout.setOnClickListener(this);
		couponLayout.setOnClickListener(this);
		alipayLayout.setOnClickListener(this);
		wxpayLayout.setOnClickListener(this);

		couponCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked == false) {
					couponUseInfoTv.setText(findStringByResId("coupon_coin_text"));
					couponCheckBox.setClickable(false);

					if (currentCoupon != null) {
						float goodsWorth = currentCoupon.goodsWorth != null ? Float.parseFloat(currentCoupon.goodsWorth)
								: 0;
						if (goodsWorth > 0) {
							amount = amount + goodsWorth;
							amount = (float) Math.round(amount * 100) / 100;
							realPayAmountTv.setText(amount >= 0 ? amount + "" : "0");
						}
					}
					lastCoupon = null;
				}
			}
		});

		gameCoinCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					amount = amount
							- (GoagalInfo.userInfo.gttb != null ? Float.parseFloat(GoagalInfo.userInfo.gttb) : 0);
					amount = (float) Math.round(amount * 100) / 100;
					realPayAmountTv.setText(amount >= 0 ? amount + "" : "0");
				} else {
					amount = amount
							+ (GoagalInfo.userInfo.gttb != null ? Float.parseFloat(GoagalInfo.userInfo.gttb) : 0);
					amount = (float) Math.round(amount * 100) / 100;
					realPayAmountTv.setText(amount >= 0 ? amount + "" : "0");
				}
			}
		});

		platformCoinCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					amount = amount - (GoagalInfo.userInfo.ttb != null ? Float.parseFloat(GoagalInfo.userInfo.ttb) : 0);
					amount = (float) Math.round(amount * 100) / 100;
					realPayAmountTv.setText(amount >= 0 ? amount + "" : "0");
				} else {
					amount = amount + (GoagalInfo.userInfo.ttb != null ? Float.parseFloat(GoagalInfo.userInfo.ttb) : 0);
					amount = (float) Math.round(amount * 100) / 100;
					realPayAmountTv.setText(amount >= 0 ? amount + "" : "0");
				}
			}
		});

		PayActivity.this.setFinishOnTouchOutside(true);
	}

	@Override
	public void initData() {
		super.initData();

		Intent intent = getIntent();
		role = intent.getStringExtra("roleid");
		server = intent.getStringExtra("serverid");
		amount = intent.getFloatExtra("money", 0);
		totalPrice = amount;
		productname = intent.getStringExtra("productname");
		productdesc = intent.getStringExtra("productdesc");
		// fcallbackurl = intent.getStringExtra("fcallbackurl");
		attach = intent.getStringExtra("attach");

		productNameTv.setText(productname);
		orderAmountTv.setText(amount + "");
		realPayAmountTv.setText(amount + "");

		payCoinEngin = new PayCoinEngin(PayActivity.this,
				GoagalInfo.userInfo == null ? "" : GoagalInfo.userInfo.userId);
		payGameDialog = new CustomDialog(this, "正在创建订单");
		payResultDialog = new PayResultDialog(PayActivity.this);
		payResultDialog.setPayResultListener(this);
		payResultDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (cancelType == 1) {
					payExit();
				}
			}
		});
		new PayInitTask().execute();
	}

	/**
	 * 获取优惠券列表信息
	 * 
	 * @param key
	 * @return
	 */
	private List<CouponInfo> getCouponInfoList(String key) {
		List<CouponInfo> list = null;
		try {
			String moduleStr = PreferenceUtil.getImpl(this).getString(key, "");
			Logger.msg("get--CouponInfoList---" + moduleStr);
			if (!StringUtils.isEmpty(moduleStr)) {
				list = JSON.parseArray(moduleStr, CouponInfo.class);
			}
		} catch (Exception e) {
			Logger.msg(e.getMessage());
		}
		return list;
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == findIdByString("close_iv")) {
			this.finish();
		}

		if (v.getId() == findIdByString("more_coupon_layout")) {
			showCouponList();
		}

		if (v.getId() == findIdByString("coupon_layout")) {
			showCouponList();
		}
		if (v.getId() == findIdByString("alipay_layout")) {
			alipayLayout.setSelected(true);
			wxpayLayout.setSelected(false);
			payWay = Constants.ALIPAY_CR;
		}
		if (v.getId() == findIdByString("wxpay_layout")) {
			alipayLayout.setSelected(false);
			wxpayLayout.setSelected(true);
			payWay = Constants.WXPAY_CR;
		}
		/*
		 * int cc = 0; if (v.getId() == findIdByString("pay_game_btn")) { cc++;
		 * Logger.msg("点击支付" + cc); Util.toast(this, "点击支付" + cc); pay(); }
		 */
	}

	public abstract class NoDoubleClickListener implements OnClickListener {

		public abstract void onNoDoubleClick(View v);

		public static final int MIN_CLICK_DELAY_TIME = 5000;
		private long lastClickTime = 0;

		@Override
		public void onClick(View v) {
			long currentTime = Calendar.getInstance().getTimeInMillis();
			if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
				Logger.msg("点击触发支付--->" + currentTime);
				lastClickTime = currentTime;
				onNoDoubleClick(v);
			}
		}
	}

	/**
	 * 支付方法
	 */
	public void pay() {
		Logger.msg("支付前实际支付金额---" + totalPrice);

		PayRequestParams params = new PayRequestParams();
		params.is_game_pay = "1";

		String tempPayWay = payWay;

		if (amount <= 0) {
			tempPayWay = "";
		}

		if (couponCheckBox.isChecked()) {
			params.goods_id = currentCoupon.goodsId;
			params.card_id = currentCoupon.cardId;
			params.good_type_name = currentCoupon.cardTypeName;

			tempPayWay = payWay;
		}

		StringBuffer temp = new StringBuffer(tempPayWay);

		if (gameCoinCheckBox.isChecked()) {

			if (!temp.toString().equals("")) {
				temp.append("|");
			}

			temp.append("gamemoney");
		}

		if (platformCoinCheckBox.isChecked()) {

			if (!temp.toString().equals("")) {
				temp.append("|");
			}

			temp.append("money");
		}

		params.pay_ways = temp.toString();
		params.amount = totalPrice + "";
		params.role = role;
		params.server = server;
		params.appid = "";
		params.productname = productname;
		params.attach = attach;

		if (payWay.equals(Constants.ALIPAY_CR)) {
			params.md5signstr = "";
			new PayGameTask(params).execute();
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
			params.md5signstr = preSignStr;

			Logger.msg("pay params -->" + JSONObject.toJSONString(params));
			new PayGameTask(params).execute();
		}
		if (SystemUtil.isValidContext(PayActivity.this)) {
			if (payGameDialog != null && !payGameDialog.isShowing()) {
				payGameDialog.show();
			}
		}

	}

	public void showCouponList() {
		if (SystemUtil.isValidContext(PayActivity.this)) {
			if (pwCoupon != null && pwCoupon.isShowing()) {
				pwCoupon.dismiss();
			} else {
				// couponInfoList = getCouponInfoList(payCoinEngin.getUrl());

				List<CouponInfo> tempList = new ArrayList<CouponInfo>();

				if (GoagalInfo.couponList != null && GoagalInfo.couponList.size() > 0) {
					tempList.addAll(GoagalInfo.couponList);
				}

				couponInfoList = tempList;

				if (null == couponInfoList) {
					return;
				}
				if (null == adapter) {
					adapter = new CouponListAdapter(this, couponInfoList);
				}

				int pwidth = DimensionUtil.dip2px(this, 340);
				if (pwCoupon == null) {
					View view = inflater.inflate(MResource.getIdByName(this, "layout", "coupon_list"), null);
					ListView lv_pw = (ListView) view
							.findViewById(MResource.getIdByName(this, "id", "coupon_list_view"));
					lv_pw.setCacheColorHint(0x000000);
					lv_pw.setAdapter(adapter);
					lv_pw.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> adapterview, View view, int position, long row) {
							pwCoupon.dismiss();
							// Util.toast(PayActivity.this, "pos---" +
							// position);
							if (couponInfoList.get(position) != null) {

								if (!StringUtils.isEmpty(couponInfoList.get(position).goods_uc_money)) {
									if (totalPrice < Float.parseFloat(couponInfoList.get(position).goods_uc_money)) {
										Toast.makeText(PayActivity.this, "不满足优惠券使用条件,请选择其他优惠", Toast.LENGTH_LONG)
												.show();
										return;
									}
								}

								currentCoupon = couponInfoList.get(position);

								if (lastCoupon != null) {
									if (!lastCoupon.cardId.equals(currentCoupon.cardId)) {
										float goodsWorth = currentCoupon.goodsWorth != null
												? Float.parseFloat(currentCoupon.goodsWorth) : 0;

										float lastGoodsWorth = lastCoupon.goodsWorth != null
												? Float.parseFloat(lastCoupon.goodsWorth) : 0;

										String temp = currentCoupon.goodsName + goodsWorth + "元";
										couponUseInfoTv.setText(temp);
										couponCheckBox.setChecked(true);
										couponCheckBox.setClickable(true);

										if (goodsWorth > 0 && lastGoodsWorth > 0) {
											amount = amount + lastGoodsWorth - goodsWorth;

											amount = (float) Math.round(amount * 100) / 100;

											realPayAmountTv.setText(amount >= 0 ? amount + "" : "0");
										}
										lastCoupon = currentCoupon;
									}
								} else {
									lastCoupon = currentCoupon;
									float goodsWorth = currentCoupon.goodsWorth != null
											? Float.parseFloat(currentCoupon.goodsWorth) : 0;

									String temp = currentCoupon.goodsName + goodsWorth + "元";
									couponUseInfoTv.setText(temp);
									couponCheckBox.setChecked(true);
									couponCheckBox.setClickable(true);

									if (goodsWorth > 0) {
										amount = amount - goodsWorth;
										amount = (float) Math.round(amount * 100) / 100;
										realPayAmountTv.setText(amount >= 0 ? amount + "" : "0");
									}
								}
							}
						}
					});

					pwCoupon = new PopupWindow(view, pwidth, LinearLayout.LayoutParams.WRAP_CONTENT, true);
					pwCoupon.setBackgroundDrawable(new ColorDrawable(0x00000000));
					pwCoupon.setContentView(view);
				} else {
					adapter.initDataList(tempList);
					adapter.notifyDataSetChanged();
				}
				pwCoupon.showAsDropDown(moreCouponLayout, -pwidth + (int) DimensionUtil.dip2px(this, 48), 0);
			}
		}
	}

	/**
	 * 创建游戏支付订单
	 * 
	 * @author admin
	 *
	 */
	private class PayGameTask extends AsyncTask<String, Integer, PayInfo> {

		public PayRequestParams payRequestParams;

		public PayGameTask(PayRequestParams params) {
			this.payRequestParams = params;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected PayInfo doInBackground(String... params) {
			chargeEngin = new ChargeEngin(PayActivity.this, payRequestParams);
			return chargeEngin.payGame();
		}

		@Override
		protected void onPostExecute(PayInfo result) {
			super.onPostExecute(result);
			if (SystemUtil.isValidContext(PayActivity.this)) {
				if (payGameDialog != null && payGameDialog.isShowing()) {
					payGameDialog.dismiss();
				}
			}
			if (result != null && result.code == HttpConfig.STATUS_OK) {
				orderid = result.orderSn;
				amount = result.rmbMoney != null ? Float.parseFloat(result.rmbMoney) : 0;

				Logger.msg("支付的金额---" + amount);

				if (result.params != null) {
					if (amount > 0) {
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
								mIpaynowplugin.setCallResultActivity(PayActivity.this).pay(mhtSignature);
								isnowpay = "1";
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						// Util.toast(PayActivity.this, "支付成功");

						PaymentCallbackInfo pci = new PaymentCallbackInfo();
						pci.money = amount;
						pci.msg = "支付成功";

						// 验证订单
						new PayValidateTask().execute();

						if (GoagalInfo.paymentListener != null) {
							GoagalInfo.paymentListener.paymentSuccess(pci);
						}
						finishSuccess();
						// finish();
					}
				}
			} else if (result != null && result.code == HttpConfig.ORDER_ERROR) {
				Util.toast(PayActivity.this, result.errorMsg != null ? result.errorMsg : "订单错误，请关闭后重试");
				finishSuccess();
				// finish();
			} else {
				Util.toast(PayActivity.this, result.errorMsg);
			}
		}
	}

	/**
	 * 支付成功后验证订单
	 * 
	 */
	private class PayValidateTask extends AsyncTask<String, Integer, PayValidateResult> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected PayValidateResult doInBackground(String... params) {
			payValidateEngin = new PayValidateEngin(PayActivity.this, orderid);
			return payValidateEngin.run();
		}

		@Override
		protected void onPostExecute(PayValidateResult payValidateResult) {
			super.onPostExecute(payValidateResult);
			if (payValidateResult != null && payValidateResult.result) {
				Message msg = new Message();
				msg.obj = payValidateResult.pointMessage;
				msg.what = 3;
				handler.sendMessage(msg);
			} else {
				if (validateCount == 1) {
					Message msg = new Message();
					msg.what = 4;
					handler.sendMessage(msg);
				} else {
					Util.toast(PayActivity.this, "支付成功");
				}
			}
		}
	}

	/**
	 * 支付前获取的支付信息
	 * 
	 * @author admin
	 *
	 */
	private class PayInitTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			return payCoinEngin.run();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				// 更新用户信息
				Message msg = new Message();
				msg.what = 2;
				handler.sendMessage(msg);
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
					PayTask alipay = new PayTask(PayActivity.this);
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
		sb.append(amount);
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
		preSign.mhtOrderAmt = Integer.toString((int) (amount * 100));

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

	public void setOrientation() {
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();

		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 0) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			Logger.msg("onresume width land ---" + DimensionUtil.getWidth(PayActivity.this));
			params.width = (int) (DimensionUtil.getWidth(PayActivity.this) * 0.55);
			params.gravity = Gravity.CENTER;
		}
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 1) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			Logger.msg("onresume width port ---" + DimensionUtil.getWidth(PayActivity.this));
			params.width = (int) (DimensionUtil.getWidth(PayActivity.this) * 0.85);
			params.gravity = Gravity.CENTER;
		}

		dialogWindow.setAttributes(params);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("PayActivity");
		MobclickAgent.onResume(this);

		MobclickAgent.onEvent(this, "game_open_charge");

		Logger.msg("Payactivity onResume---");
		// setOrientation();

		if (isnowpay.equals("2")) {
			if (nowpayCode.equals("00")) {
				if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
					if(amount > 1) {
						EventUtils.setPurchase("","","",1,payWay,"rmb",true,(int)amount);
						Logger.msg("TeaAgent Pay True");
					}else {
						Logger.msg("TeaAgent Pay amount <=1 ");
					}
				}
				// Util.toast(this, "支付成功");
				Logger.msg("支付成功--->");
				// new PayInitTask().execute();

				PaymentCallbackInfo pci = new PaymentCallbackInfo();
				pci.money = amount;
				pci.msg = "支付成功";
				// Util.toast(PayActivity.this, "支付成功");

				// 验证订单
				new PayValidateTask().execute();

				if (GoagalInfo.paymentListener != null) {
					GoagalInfo.paymentListener.paymentSuccess(pci);
				}

				finishSuccess();
				// finish();
			}
			if (nowpayCode.equals("02")) {
				if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
					EventUtils.setPurchase("","","",1,payWay,"rmb",false,(int)amount);
					Logger.msg("TeaAgent Pay False");
				}
				// Util.toast(this, "支付取消");
				Logger.msg("支付取消--->");

				PaymentErrorMsg msg_e = new PaymentErrorMsg();
				msg_e.code = 2;
				msg_e.msg = "取消支付";
				msg_e.money = amount;

				// Util.toast(PayActivity.this, memo);
				if (GoagalInfo.paymentListener != null) {
					GoagalInfo.paymentListener.paymentError(msg_e);
				}

				if (SystemUtil.isValidContext(PayActivity.this)) {
					if (payResultDialog != null && !payResultDialog.isShowing()) {
						payResultDialog.show();
					}
				}

			}
			if (nowpayCode.equals("01")) {
				if(GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.isPostToToutiaoSdk == 1) {
					EventUtils.setPurchase("","","",1,payWay,"rmb",false,(int)amount);
					Logger.msg("TeaAgent Pay False");
				}
				Util.toast(this, nowpayMsg);
				PaymentErrorMsg msg_e = new PaymentErrorMsg();
				msg_e.code = 1;
				msg_e.msg = nowpayMsg;
				msg_e.money = amount;

				// Util.toast(PayActivity.this, memo);
				if (GoagalInfo.paymentListener != null) {
					GoagalInfo.paymentListener.paymentError(msg_e);
				}
			}
			isnowpay = "0";
		}

	}

	/*
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * super.onConfigurationChanged(newConfig); Logger.msg(
	 * "Payactivity onConfigurationChanged---"); setOrientation(); }
	 */

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		super.onActivityResult(arg0, arg1, data);
		if (isnowpay.equals("1")) {
			nowpayCode = data.getExtras().getString("respCode");
			nowpayMsg = data.getExtras().getString("respMsg");
			isnowpay = "2";
		}
	}

	private class PayCancelTask extends AsyncTask<String, Integer, Boolean> {
		public String orderId;
		// 0,继续支付取消，1，确认退出取消
		public int mType;

		public PayCancelTask(String orderId, int type) {
			this.orderId = orderId;
			this.mType = type;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			PayCancelEngin payCancelEngin = new PayCancelEngin(PayActivity.this, orderId);
			return payCancelEngin.run();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// setOrientation();

			// 0,继续支付取消，1，确认退出取消
			Logger.msg("mType --->" + mType);
			if (mType == 0) {
				if (SystemUtil.isValidContext(PayActivity.this)) {
					if (payResultDialog != null && payResultDialog.isShowing()) {
						cancelType = 0;
						payResultDialog.dismiss();
					}
				}
			} else {
				if (SystemUtil.isValidContext(PayActivity.this)) {
					if (payResultDialog != null && payResultDialog.isShowing()) {
						cancelType = 1;
						payResultDialog.dismiss();
					}
				}
				finish();
			}
		}
	}

	@Override
	public void continuePay() {
		// pay();
		if (orderid != null) {
			new PayCancelTask(orderid, 0).execute();
		} else {
			Util.toast(this, "数据异常，请稍后重试!");
		}
	}

	@Override
	public void payExit() {
		if (orderid != null) {
			new PayCancelTask(orderid, 1).execute();
		} else {
			Util.toast(this, "数据异常，请稍后重试!");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("PayActivity");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.msg("Pay----onDestroy--->");
	}

	public void finishSuccess() {
		super.finish();
		Logger.msg("正常支付成功后finishSuccess--->");
	}

	@Override
	public void finish() {
		Logger.msg("Pay----finish--->");

		try { // 将取消支付移动到onDestroy方法中
			PaymentCancelMsg msg_c = new PaymentCancelMsg();
			msg_c.code = 2;
			msg_c.msg = "取消支付";
			msg_c.money = amount;

			if (GoagalInfo.paymentListener != null) {
				GoagalInfo.paymentListener.paymentCancel(msg_c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.finish();
	}

}
