package com.game.sdk.engin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.PayInfo;
import com.game.sdk.domain.PayRequestParams;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.net.constans.HttpConfig;
import com.game.sdk.net.constans.ServerConfig;
import com.game.sdk.utils.Logger;

import android.content.Context;

/**
 * Created by zhangkai on 16/9/20.
 */
public class ChargeEngin extends BaseEngin<PayInfo> {

	public Context mContext;

	public String userId;

	// 充值类型,0为平台币充值，1为游戏充值
	public int payType;

	// 充值方式(alipay|wxpay|money|gamemoney)
	public String payWay;

	public double chargeMoney = 0;

	public String md5signstr;

	public String orderId;

	private PayRequestParams payRequestParams;

	public ChargeEngin() {
	}

	public ChargeEngin(Context context, PayRequestParams params) {
		super(context);
		this.mContext = context;
		this.payRequestParams = params;
	}

	public ChargeEngin(Context context, String userId, int payType, String payWay, double chargeMoney,
			String md5signstr) {
		super(context);
		this.mContext = context;
		this.userId = userId;
		this.payType = payType;
		this.payWay = payWay;
		this.chargeMoney = chargeMoney;
		this.md5signstr = md5signstr;
	}
	
	public ChargeEngin(Context context, String orderId,String md5signstr) {
		this.orderId = orderId;
		this.md5signstr = md5signstr;
	}
	
	@Override
	public String getUrl() {
		return ServerConfig.PAY_URL;
	}

	/**
	 * 充值
	 * 
	 * @return
	 */
	public PayInfo pay() {
		PayInfo payinfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", userId);
			params.put("is_game_pay", payType + "");
			params.put("pay_ways", payWay);
			params.put("amount", chargeMoney + "");
			params.put("md5signstr", md5signstr);

			ResultInfo<PayInfo> resultInfo = getResultInfo(true, PayInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("充值结果----" + JSON.toJSONString(resultInfo.data));
				payinfo = resultInfo.data;
				payinfo.code = resultInfo.code;
			} else {
				payinfo = new PayInfo();
				payinfo.code = resultInfo.code;
				payinfo.errorMsg = resultInfo.message;
			}
		} catch (Exception e) {
		}

		return payinfo;
	}

	/**
	 * 游戏支付
	 * 
	 * @return
	 */
	public PayInfo payGame() {
		PayInfo payinfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", GoagalInfo.userInfo.userId);
			params.put("is_game_pay", payRequestParams.is_game_pay);
			params.put("pay_ways", payRequestParams.pay_ways);
			params.put("amount", payRequestParams.amount);

			params.put("good_type_name", payRequestParams.good_type_name);
			params.put("goods_id", payRequestParams.goods_id);
			params.put("card_id", payRequestParams.card_id);
			params.put("role", payRequestParams.role);
			params.put("server", payRequestParams.server);
			params.put("appid", payRequestParams.appid);
			params.put("productname", payRequestParams.productname);
			params.put("attach", payRequestParams.attach);
			params.put("md5signstr", payRequestParams.md5signstr);

			ResultInfo<PayInfo> resultInfo = getResultInfo(true, PayInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("充值结果----" + JSON.toJSONString(resultInfo));
				payinfo = resultInfo.data;

				payinfo.code = resultInfo.code;
				payinfo.errorMsg = resultInfo.message;
			} else {
				payinfo = new PayInfo();
				payinfo.code = resultInfo.code;
				payinfo.errorMsg = resultInfo.message != null ? resultInfo.message : "支付异常，请稍后重试";
			}
		} catch (Exception e) {
		}

		return payinfo;
	}
	
	/**
	 * 微信继续支付，获取到的参数
	 * 
	 * @return
	 */
	public PayInfo wxContinuePay() {
		PayInfo payinfo = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderid", orderId);
			params.put("md5signstr", md5signstr);

			ResultInfo<PayInfo> resultInfo = getResultInfo(true, PayInfo.class, params);

			if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
				Logger.msg("充值结果----" + JSON.toJSONString(resultInfo));
				payinfo = resultInfo.data;

				payinfo.code = resultInfo.code;
				payinfo.errorMsg = resultInfo.message;
			} else {
				payinfo = new PayInfo();
				payinfo.code = resultInfo.code;
				payinfo.errorMsg = resultInfo.message != null ? resultInfo.message : "支付异常，请稍后重试";
			}
		} catch (Exception e) {
		}

		return payinfo;
	}

}
