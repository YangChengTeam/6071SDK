package com.game.sdk.ui.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.alipay.sdk.app.PayTask;
import com.game.sdk.adapter.ChargeRecordAdapter;
import com.game.sdk.adapter.ChargeRecordAdapter.ContinuePayListener;
import com.game.sdk.domain.ChargeRecord;
import com.game.sdk.domain.ChargeRecordList;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.PayContinueInfo;
import com.game.sdk.domain.PayInfo;
import com.game.sdk.domain.PayRequestParams;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.engin.ChargeEngin;
import com.game.sdk.engin.ChargeRecordEngin;
import com.game.sdk.engin.PayCancelEngin;
import com.game.sdk.engin.PayContinueEngin;
import com.game.sdk.engin.WxChargeEngin;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.listeners.Callback;
import com.game.sdk.security.Rsa;
import com.game.sdk.ui.ChargeRecordActivity;
import com.game.sdk.ui.ChargeRecordActivity.PayResultListener;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.ThreadPoolManager;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CancelConfigDialog;
import com.game.sdk.view.CancelConfigDialog.CancelListener;
import com.game.sdk.view.CustomDialog;
import com.ipaynow.plugin.api.IpaynowPlugin;
import com.ipaynow.plugin.utils.PreSignMessageUtil;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChargeRecordFragment extends BaseFragment implements OnClickListener, ContinuePayListener, CancelListener, OnScrollListener,PayResultListener{
	
	private ChargeRecordActivity mainActivity;
	
	private ListView listView;

	private ChargeRecordAdapter adapter;

	List<ChargeRecord> chargeRecordList;

	private View loadMoreView;

	private LinearLayout loadMoreLayout;

	private LinearLayout noMoreLayout;

	private TextView noMoreTv;

	private ImageView noDataIv;
	
	private ImageView loadMoreIcon;

	private ChargeRecordEngin chargeRecordEngin;

	private ChargeEngin chargeEngin;

	private WxChargeEngin wxchargeEngin;

	CustomDialog payGameDialog;

	CustomDialog payCancelDialog;

	private String orderid;

	private float amount;

	private String payWay = Constants.ALIPAY_CR;

	private String productname;

	private String productdesc;

	private String realMoney;

	public static String isnowpay = "0";

	public static String nowpayCode;

	public static String nowpayMsg;

	private IpaynowPlugin mIpaynowplugin;

	private String attach;

	public CancelConfigDialog cancelConfigDialog;

	private int lastItem;

	private int currentPage = 1;

	private int isAllGame = 0;// 0：当前游戏订单列表 1：所有游戏订单列表

	private int orderState = 4;// 订单状态: -1:全部, //
								// 0(待支付)，1(支付失败)，2(支付成功)，3(充值失败)，4(充值完成)
	
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
						Util.toast(mainActivity, "支付成功");
						if(adapter != null){
							adapter.clearAllList();
							loadChargeRecordData();
						}
					} else {
						Util.toast(mainActivity, memo);
					}
				} else {
					// 如果msg为null 是支付宝那边返回数据为null
					Util.toast(mainActivity, "无法判别支付是否成功！具体请查看后台数据");
				}
				Logger.msg("result:" + result);
				break;
			case 1:
				//listView.setDividerHeight(DimensionUtil.dip2px(ChargeRecordActivity.this, 10));
				loadMoreLayout.setVisibility(View.GONE);
				stopAnimation();

				adapter.addNewList(chargeRecordList);
				adapter.notifyDataSetChanged();
				break;
			case 2:
				Util.toast(mainActivity, "取消订单成功");
				// new ChargeRecordTask().execute();
				loadChargeRecordData();
				break;
			case 3:
				Util.toast(mainActivity, "取消订单失败");
				break;
			case 4:

				loadMoreLayout.setVisibility(View.GONE);
				noMoreLayout.setVisibility(View.VISIBLE);
				if (chargeRecordList != null && chargeRecordList.size() == 0) {
					listView.setDividerHeight(DimensionUtil.dip2px(mainActivity, 0));
					noMoreLayout.setBackgroundColor(Color.TRANSPARENT);
					noMoreTv.setVisibility(View.GONE);
					noDataIv.setVisibility(View.VISIBLE);
				} else {
					noDataIv.setVisibility(View.GONE);
					noMoreTv.setVisibility(View.VISIBLE);
					noMoreTv.setText(findStringByResId("no_more_text"));
					noMoreTv.setPadding(0, 0, 0, DimensionUtil.dip2px(mainActivity, 8));
				}
				
				stopAnimation();

				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "fysdk_charge_record_fragment";
	}
	
	@Override
	public void initVars() {
		super.initVars();
		mainActivity = (ChargeRecordActivity) getActivity();
		mainActivity.setPayResultListener(this);
		mIpaynowplugin = IpaynowPlugin.getInstance().init(mainActivity);// 1.插件初始化
		mIpaynowplugin.unCkeckEnvironment();
		
		Bundle bundle = getArguments();
		isAllGame = bundle.getInt("isAllGame");
		orderState = bundle.getInt("orderState");
		
		Logger.msg("isAllGame--->" + isAllGame + "orderState--->" + orderState);
	}

	@Override
	public void initViews() {
		super.initViews();
		
		loadMoreView = mainActivity.getLayoutInflater().inflate(MResource.getIdByName(mainActivity, "layout", "list_view_footer"), null);
		loadMoreLayout = (LinearLayout) loadMoreView
				.findViewById(MResource.getIdByName(mainActivity, "id", "load_more_layout"));
		noMoreLayout = (LinearLayout) loadMoreView.findViewById(MResource.getIdByName(mainActivity, "id", "no_more_layout"));
		loadMoreIcon = (ImageView) loadMoreView.findViewById(MResource.getIdByName(mainActivity, "id", "loading_icon"));
		noMoreTv = (TextView) loadMoreView.findViewById(MResource.getIdByName(mainActivity, "id", "no_more_tv"));
		noDataIv = (ImageView)loadMoreView.findViewById(MResource.getIdByName(mainActivity, "id", "no_data_iv"));
		
		listView = (ListView) findViewByString("charge_record_list");

		listView.addFooterView(loadMoreView);

		chargeRecordEngin = ChargeRecordEngin.getImpl(mainActivity);
		payGameDialog = new CustomDialog(mainActivity, "正在创建订单");
		payCancelDialog = new CustomDialog(mainActivity, "正在取消订单");
		cancelConfigDialog = new CancelConfigDialog(mainActivity);
		cancelConfigDialog.setCancelListener(this);
		listView.setOnScrollListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();
		chargeRecordList = new ArrayList<ChargeRecord>();
		adapter = new ChargeRecordAdapter(mainActivity, chargeRecordList, cancelConfigDialog);
		adapter.setPayListener(this);
		listView.setAdapter(adapter);

		// new ChargeRecordTask().execute();
		loadChargeRecordData();
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}

	@Override
	public void onClick(View v) {
		/*if (v.getId() == findIdByString("back_iv")) {
			mainActivity.changeFragment(1);
		}*/
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(mainActivity);

		if (isnowpay.equals("2")) {
			if (nowpayCode.equals("00")) {
				Util.toast(mainActivity, "支付成功");
				// new PayInitTask().execute();
				if(adapter != null){
					adapter.clearAllList();
					loadChargeRecordData();
				}
			}
			if (nowpayCode.equals("02")) {
				Util.toast(mainActivity, "支付取消");
			}
			if (nowpayCode.equals("01")) {
				Util.toast(mainActivity, nowpayMsg);
			}
			isnowpay = "0";
		}
	}

	public void loadChargeRecordData() {
		chargeRecordEngin.getChargeRecordList(isAllGame,orderState,currentPage, GoagalInfo.userInfo.userId, "", "",
				new Callback<ChargeRecordList>() {

					@Override
					public void onSuccess(ResultInfo<ChargeRecordList> resultInfo) {
						if (resultInfo.data != null && resultInfo.data.chargeRecordList != null && resultInfo.data.chargeRecordList.size() > 0) {

							chargeRecordList = resultInfo.data.chargeRecordList;

							// 刷新数据
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = 4;
							handler.sendMessage(msg);
						}
					}

					@Override
					public void onFailure(Response response) {

					}
				});
	}

	private class ChargeRecordTask extends AsyncTask<String, Integer, List<ChargeRecord>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<ChargeRecord> doInBackground(String... params) {

			chargeRecordEngin.getChargeRecordList(0,-1,currentPage, GoagalInfo.userInfo.userId, "", "",
					new Callback<ChargeRecordList>() {

						@Override
						public void onSuccess(ResultInfo<ChargeRecordList> resultInfo) {
							if (resultInfo.data != null && resultInfo.data.chargeRecordList != null) {

								chargeRecordList = resultInfo.data.chargeRecordList;

								// 刷新数据
								Message msg = new Message();
								msg.what = 1;
								handler.sendMessage(msg);
							} else {
								Message msg = new Message();
								msg.what = 4;
								handler.sendMessage(msg);
							}
						}

						@Override
						public void onFailure(Response response) {

						}
					});
			return null;
		}

		@Override
		protected void onPostExecute(List<ChargeRecord> result) {
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				chargeRecordList = result;

				// 刷新数据
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}
	}

	// 微信支付
	private PreSignMessageUtil preSign = new PreSignMessageUtil();

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

	@Override
	public void continuePay(String orderId, String realMoney) {
		this.realMoney = realMoney;
		payGameDialog.show();
		new PayContinueTask(orderId).execute();
	}

	@Override
	public void cancelPay(String orderId) {
		new PayCancelTask(orderId).execute();
	}

	private class PayContinueTask extends AsyncTask<String, Integer, PayContinueInfo> {
		public String orderId;

		public PayContinueTask(String orderId) {
			this.orderId = orderId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected PayContinueInfo doInBackground(String... params) {
			PayContinueEngin payContinueEngin = new PayContinueEngin(mainActivity, orderId);
			return payContinueEngin.run();
		}

		@Override
		protected void onPostExecute(PayContinueInfo result) {
			super.onPostExecute(result);
			if (result != null) {
				PayRequestParams params = new PayRequestParams();
				params.is_game_pay = "1";
				params.goods_id = result.goods_id;
				params.card_id = result.card_id;
				params.good_type_name = result.good_type_name;
				params.pay_ways = result.pay_ways;

				params.amount = result.amount;
				params.role = result.role;
				params.server = result.server;
				params.appid = result.appid;

				if (!StringUtils.isEmpty(result.amount)) {
					amount = Float.parseFloat(result.amount);
				}

				productname = result.productname;
				productdesc = result.productname;
				params.productname = result.productname;
				params.attach = result.attach;
				params.md5signstr = "";
				orderid = result.orderSn;
				payWay = result.pay_ways;
				attach = result.attach;

				try {
					if (!StringUtils.isEmpty(payWay) && payWay.equals(Constants.ALIPAY_CR)) {
						if (payGameDialog != null && payGameDialog.isShowing()) {
							payGameDialog.dismiss();
						}

						if (!StringUtils.isEmpty(realMoney)) {
							amount = Float.parseFloat(realMoney);
						}
						payAlipayMoney(result.params.partnerid, result.params.email, result.params.privatekey);
					}

					if (!StringUtils.isEmpty(payWay) && payWay.equals(Constants.WXPAY_CR)) {
						prePayMessage();

						preSign.mhtOrderNo = "{orderid}";
						ConnectivityManager manager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo info = manager.getActiveNetworkInfo();
						if (info != null && info.isConnected()) {
							// 支付类型： 微信支付
							preSign.payChannelType = "13";
							// 生成代签名
							preSignStr = preSign.generatePreSignMessage();
						}
						params.md5signstr = preSignStr;
						new WxContinuePayTask(orderid, preSignStr).execute();
					}
				} catch (Exception e) {
					if (payGameDialog != null && payGameDialog.isShowing()) {
						payGameDialog.dismiss();
					}
				}

			} else {
				if (payGameDialog != null && payGameDialog.isShowing()) {
					payGameDialog.dismiss();
				}
			}
		}
	}

	private class PayCancelTask extends AsyncTask<String, Integer, Boolean> {
		public String orderId;

		public PayCancelTask(String orderId) {
			this.orderId = orderId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			PayCancelEngin payCancelEngin = new PayCancelEngin(mainActivity, orderId);
			return payCancelEngin.run();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (payCancelDialog != null && payCancelDialog.isShowing()) {
				payCancelDialog.dismiss();
			}

			if (cancelConfigDialog != null && cancelConfigDialog.isShowing()) {
				cancelConfigDialog.dismiss();
			}
			// 刷新数据
			Message msg = new Message();
			if (result) {
				msg.what = 2;
			} else {
				msg.what = 3;
			}
			handler.sendMessage(msg);
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
					PayTask alipay = new PayTask(mainActivity);
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
			Util.toast(mainActivity, "支付失败");
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

	private class WxContinuePayTask extends AsyncTask<String, Integer, PayInfo> {

		public String orderId;
		public String md5signstr;

		public WxContinuePayTask(String orderId, String md5signstr) {
			this.orderId = orderId;
			this.md5signstr = md5signstr;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected PayInfo doInBackground(String... params) {
			wxchargeEngin = new WxChargeEngin(mainActivity, orderId, md5signstr);
			return wxchargeEngin.wxContinuePay();
		}

		@Override
		protected void onPostExecute(PayInfo result) {
			super.onPostExecute(result);

			if (payGameDialog != null && payGameDialog.isShowing()) {
				payGameDialog.dismiss();
			}

			if (result != null && result.code == HttpConfig.STATUS_OK) {
				orderid = result.orderSn;
				amount = result.rmbMoney != null ? Float.parseFloat(result.rmbMoney) : 0;

				Logger.msg("支付的金额---" + amount);

				if (result.params != null) {
					if (amount > 0) {
						if (payWay.equals(Constants.WXPAY_CR)) {

							preSign.mhtOrderNo = orderid;
							preSign.appId = result.params.partnerid;
							preSign.mhtOrderStartTime = result.starttime;

							preSignStr = preSign.generatePreSignMessage();
							String mhtSignature = preSignStr + "&mhtSignature=" + result.rsmd5 + "&mhtSignType=MD5";
							try {
								mIpaynowplugin.setCallResultActivity(mainActivity).pay(mhtSignature);
								isnowpay = "1";
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						Util.toast(mainActivity, "支付成功");
					}
				}
			} else {
				Util.toast(mainActivity, result.errorMsg);
			}
		}
	}
	
	public void onActivityResult(int arg0, int arg1, Intent data) {
		super.onActivityResult(arg0, arg1, data);
		
		Logger.msg("chargeRecordFragment---onActivityResult--->");
		
		if (isnowpay.equals("1")) {
			nowpayCode = data.getExtras().getString("respCode");
			nowpayMsg = data.getExtras().getString("respMsg");
			isnowpay = "2";
		}
	}

	@Override
	public void cancelConfig(String orderId) {
		payCancelDialog.show();
		new PayCancelTask(orderId).execute();
	}
	
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ChargeRecordActivity");
		MobclickAgent.onPause(mainActivity);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码

			if (view.getLastVisiblePosition() == view.getCount() - 1 && noMoreLayout.getVisibility() == View.GONE) {

				if(adapter.getCount() >= 10){
					loadMoreLayout.setVisibility(View.VISIBLE);
					startAnimation();

					currentPage++;
					// new ChargeRecordTask().execute();
					loadChargeRecordData();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastItem = firstVisibleItem + visibleItemCount - 1;
	}

	/**
	 * 旋转动画
	 * 
	 * @return
	 */
	public Animation rotaAnimation() {
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setInterpolator(new LinearInterpolator());
		ra.setDuration(1500);
		ra.setRepeatCount(-1);
		ra.setStartOffset(0);
		ra.setRepeatMode(Animation.RESTART);
		return ra;
	}

	public void startAnimation() {
		if (loadMoreIcon != null) {
			loadMoreIcon.startAnimation(rotaAnimation());
		}
	}

	public void stopAnimation() {
		if (loadMoreIcon != null) {
			loadMoreIcon.clearAnimation();
		}
	}

	@Override
	public void payResult(int arg0, int arg1, Intent data) {
		Logger.msg("chargeRecordFragment---payResult回调--->");
		
		if (isnowpay.equals("1")) {
			nowpayCode = data.getExtras().getString("respCode");
			nowpayMsg = data.getExtras().getString("respMsg");
			isnowpay = "2";
		}
	}
}
