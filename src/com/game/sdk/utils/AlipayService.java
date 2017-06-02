package com.game.sdk.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import com.alipay.sdk.app.PayTask;
import com.game.sdk.domain.OnChargerListener;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * author janecer 2014年6月26日上午10:08:23
 */
public class AlipayService {
	private static final int ALIPAY_BACK = 10;
	private Activity activity;
	private OnChargerListener chargerListener;
	private Double charger_money;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ALIPAY_BACK:
				String result = (String) msg.obj;
				if (null != result) {
					String[] result_obj = result.split(";");
					int resultStatus;// 返回状态码
					String memo;// 提示信息
					resultStatus = Integer.parseInt(result_obj[0].substring(
							result_obj[0].indexOf("{") + 1,
							result_obj[0].lastIndexOf("}")));
					memo = result_obj[1].substring(
							result_obj[1].indexOf("{") + 1,
							result_obj[1].lastIndexOf("}"));
					if (resultStatus == 9000) {
						// Toast.makeText(activity,
						// "支付成功"+memo,Toast.LENGTH_SHORT).show();
						chargerListener.chargerSuccess(charger_money);
					} else {
						Toast.makeText(activity, "支付失败" + memo,
								Toast.LENGTH_SHORT).show();
						chargerListener.chargerFail("" + memo, charger_money);
					}
				}
				break;
			}
		};
	};

	/**
	 * 先将相关数据发送到服务端，再将相关数据带给支付宝进行处理 然后显示支付宝界面 进行支付
	 */
	public void pay(final Activity ctx, final String productname,
			final String productdesc, final double charge_money,
			OnChargerListener chargerListener) {
		this.chargerListener = chargerListener;
		this.activity = ctx;
		this.charger_money = charge_money;
		payTask(productname, productdesc, charge_money);
	}

	/**
	 * 确定支付，将相关信息发送到支付宝服务端
	 */
	private void payTask(String productname, String productdesc,
			double charge_money) {
		try {
			String info = getNewOrderInfo(productname, productdesc,
					charge_money);
			String sign = Rsa.sign(info, Keys.PRIVATE);

			sign = URLEncoder.encode(sign);

			info += "&sign=\"" + sign + "\"&" + getSignType();
			Log.i("ExternalPartner", "start pay");
			// start the pay.
			Logger.msg("info = " + info);
			final String orderInfo = info;
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					// 构造PayTask 对象
					PayTask alipay = new PayTask(activity);
					// 调用支付接口
					String result = alipay.pay(orderInfo);

					Message msg = new Message();
					msg.what = ALIPAY_BACK;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
			Util.toast(activity, "支付失败！");
		}
	}
	

	private String getNewOrderInfo(String productname, String productdesc,
			double charge_money) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");// 合作者身份id 等待服务端传递
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");// 订单号，等待服务端传递
		sb.append(getOutTradeNo());
		sb.append("\"&subject=\"");
		sb.append(Uri.decode(productname));
		sb.append("\"&body=\"");
		sb.append(Uri.decode(productdesc));
		sb.append("\"&total_fee=\"");
		sb.append(charge_money);
		sb.append("\"&notify_url=\"www.baidu.com");
		// 网址需要做URL编码
		sb.append(URLEncoder.encode(Constants.URL_NOTIFY_URL, "UTF-8"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		// sb.append("\"&return_url=\"");
		// sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");// 卖家支付宝账号，等待服务端传递
		sb.append(Keys.DEFAULT_SELLER);
		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");
		return new String(sb);
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/**
	 * 获取订单号
	 * 
	 * @return
	 */
	private String getOutTradeNo() {
		// '251wan'.time().rand(100000,999999);
		long timestamp = System.currentTimeMillis();
		Random random = new Random();
		int s = random.nextInt(9999) % (9999 - 1000 + 1) + 1000;
		return "251" + System.currentTimeMillis() + s;
	}
}
