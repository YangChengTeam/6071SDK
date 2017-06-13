package com.game.sdk.net.constans;

import com.game.sdk.utils.Constants;

import android.os.Environment;

public class ServerConfig {

	public static final boolean DEBUG = true;

	public static final String PATH = Environment.getExternalStorageDirectory() + "/6071GAME";
	
	public static final String BASE_URL = "http://api.6071.com/index2/";

	public static final String DEBUG_BASE_URL = "http://test.6071.com/api/index2/";

	public static final String INIT_URL = getBaseUrl() + "init";
	
	public static final String QUICK_LOGIN_URL = getBaseUrl() + "mobile_regORlogin";

	public static final String LOGIN_URL = getBaseUrl() + "login";

	public static final String GET_VALIDATE_URL = getBaseUrl() + "send_code";

	public static final String REGISTER_ACCOUNT_URL = getBaseUrl() + "reg";

	public static final String MAIN_MODULE_URL = getBaseUrl() + "module_list";

	public static final String COMPAIGN_LIST_URL = getBaseUrl() + "activity_list";

	public static final String USER_INFO_URL = getBaseUrl() + "get_user_info";

	public static final String PAY_URL = getBaseUrl() + "pay";

	public static final String PAY_CALL_ALIPAY_URL = getBaseUrl() + "notify_url/Alipay";

	public static final String PAY_CALL_WEIXIN_URL = getBaseUrl() + "notify_url/Weixin";

	public static final String PAY_INIT_URL = getBaseUrl() + "pay_init";

	public static final String GAME_COIN_LIST_URL = getBaseUrl() + "game_money_list";

	public static final String CHARGE_LIST_URL = getBaseUrl() + "order_list";

	// 积分商城
	public static final String SCORE_STORE_LIST_URL = getBaseUrl() + "goods_list";

	// 礼包首页
	public static final String GAME_PACKAGE_LIST_URL = getBaseUrl() + "gift_index";

	// 游戏礼包详情
	public static final String GAME_PACKAGE_DETAIL_LIST_URL = getBaseUrl() + "gift_list";

	// 修改密码
	public static final String UPDATE_PASS_WORD_URL = getBaseUrl() + "upd_pwd";

	public static final String BIND_PHONE_VALIDATE_URL = getBaseUrl() + "bind_mobile_send_code";

	public static final String BIND_PHONE_URL = getBaseUrl() + "bind_mobile";

	public static final String UPDATE_USER_INFO_URL = getBaseUrl() + "upd_user_info";

	// 活动详情
	public static final String COMPAIGN_DETAIL_URL = getBaseUrl() + "activity_detail";

	// 充值金额初始化
	public static final String CHARGE_INIT_URL = getBaseUrl() + "pay_opt";

	// 继续支付
	public static final String CONTINUE_PAY_URL = getBaseUrl() + "continue_pay";

	// 微信继续支付
	public static final String WX_CONTINUE_PAY_URL = getBaseUrl() + "continue_pay_wx";
	
	//修改通道号码
	public static final String UPDATE_MT_CODE_URL = getBaseUrl() + "upd_mtCodes";
	
	//取消订单
	public static final String PAY_CANCEL_URL = getBaseUrl() + "cancel_pay";
	
	//验证订单
	public static final String PAY_VALIDATE_URL = getBaseUrl() + "orders_chk";
	
	public static String getBaseUrl() {
		return DEBUG ? DEBUG_BASE_URL : BASE_URL;
	}

	public static String getPayCallUrl(String payWay) {
		return payWay.equals(Constants.ALIPAY_CR) ? PAY_CALL_ALIPAY_URL : PAY_CALL_WEIXIN_URL;
	}

}
