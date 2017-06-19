package com.game.sdk.utils;

/**
 * author janecer 2014年7月21日下午4:52:09
 */
public class Constants {

	public final static boolean DEBUG = false;

	/**
	 * 用户注册地址
	 */
	public final static String URL_USER_REGISTER = "http://sdk.289.com/Api/registernew";

	/**
	 * 用户登录地址
	 */
	public final static String URL_USER_LOGIN = "http://sdk.289.com/Api/login";

	/**
	 * 创建订单
	 */
	public final static String URL_CREATE_ORDER = "http://sdk.289.com/Api/createOrderId";

	/**
	 * 用户一键注册地址
	 */
	public final static String URL_USER_ONKEY2REGISTER = "http://sdk.289.com/Api/oneRegister";

	/**
	 * 退出
	 */
	public final static String URL_USER_LOGIN_OUT = "http://sdk.289.com/Api/logout";

	/**
	 * 获取支付聚道
	 */
	public final static String URL_GET_Init = "http://sdk.289.com/Api/init";

	/**
	 * 获取支付聚道
	 */
	public final static String URL_GET_CHARGERCHANNEL = "http://sdk.289.com/Api/getPayWay";

	/**
	 * 将相关数据带给支付宝时，先将相关数据发送到我方服务端
	 */
	public final static String URL_CHARGER_ZIFUBAO = "http://sdk.289.com/Api/order";

	/**
	 * 支付宝充值回调路径
	 */
	public final static String URL_NOTIFY_URL = "http://sdk.289.com/Api/notify_url/Alipay";
	/**
	 * 将相关数据带给现在支付时，先将相关数据发送到我方服务端
	 **/
	public final static String URL_CHARGER_NOWPAY = "http://sdk.289.com/Api/order";
	/**
	 * 现在支付回调路径
	 **/
	public final static String URL_NOWPAY_URL = "http://sdk.289.com/Api/notify_url/Weixin";

	/**
	 * 获取平台币信息
	 */
	public final static String URL_USER_PAYTTB = "http://sdk.289.com/Api/getMoney";

	/**
	 * 根据手机imeil码获取账号信息
	 */
	public final static String URL_IMSI_USERINFO = "http://sdk.289.com/Api/searchUserByImeil";

	/**
	 * 获取客服qq与客户电话
	 */
	public final static String URL_GETSERVICE_TELANDQQ = "http://sdk.289.com/Api/init";

	/**
	 * 一键支付地址
	 */
	public final static String URL_USR_ONEKEYPAY = "http://sdk.289.com/Api/order";

	/**
	 * 用户中心
	 */
	public static final String URL_Float_USER = "http://sdk.289.com/Api/Api/user/inuser";

	/**
	 * 礼包中心
	 */
	public static final String URL_Float_Gift = "http://sdk.289.com/Api/Api/user/incred";

	/**
	 * 客服中心
	 */
	public static final String URL_Float_Kefu = "http://sdk.289.com/Api/Api/user/inmse";

	/**
	 * 论坛中心
	 */
	public static final String URL_Float_BBS = "http://bbs.xingyoust.com/forum.php";

	/**
	 * 密码找回
	 */
	public static final String URL_Forgetpwd = "http://sdk.289.com/Api/public/forgot";

	/**
	 * 发送验证码
	 */
	public final static String URL_SEND_CODE = "http://sdk.289.com/Api/sendcode";

	/**
	 * 验证验证码
	 */
	public final static String URL_CHECK_CODE = "http://sdk.289.com/Api/checkcode";

	/**
	 * 上传图片
	 */
	public final static String URL_UPLOAD_PIC = "http://sdk.289.com/Api/uploadPic";

	public final class Resouce {
		public final static String LAYOUT = "layout";
		public final static String ID = "id";
	}

	/** sharepref里面的相关配置地址 **/
	public static final String CONFIG = "6071GameConfig";

	public static final String LOGIN_USER_USERNAME = "login_user_username";
	public static final String LOGIN_USER_PWD = "login_user_pwd";
	public static final String ISFIRST_INSTALL = "isfirst_install_config"; // 用来判断
																			// 这个xssdk是否是第一次安装
																			// 启动

	public final static String SEND_MESSAGE_PER = "android.permission.SEND_SMS";

	public final static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";

	public final static int PHONE_LOGIN_TYPE = 0;

	public final static int ACCOUNT_LOGIN_TYPE = 1;
	
	//充值记录
	public final static String CHARGE_RECORD = "CHARGE_RECORD";
	//游戏礼包
	public final static String GAME_PACKAGE = "GAME_PACKAGE";
	//积分商城
	public final static String SCORE_STORE = "SCORE_STORE";
	//账号安全
	public final static String ACCOUNT_SAFETY = "ACCOUNT_SAFETY";
	//活动中心
	public final static String COMPAIGN_CENTER = "COMPAIGN_CENTER";
	//联系客服
	public final static String SERVER_CALL = "SERVER_CALL";
	//游戏中-->下载或者打开盒子
	public final static String GAME_CENTER = "GAME_CENTER";
	
	//支付宝充值
	public final static String ALIPAY_CR = "alipay";
	//微信充值
	public final static String WXPAY_CR = "wxpay";
	//平台
	public final static String MONEY_CR = "money";
	//游戏币
	public final static String GAME_MONEY_CR = "gamemoney";
	
	//修改用户信息成功
	public final static int UPDATE_SUCCESS = 2;
	
	public static final int NOTIFICATION_ID_UPDATE = 0x0;
	
	public static final String isAutoLogin = "is_auto_login";
	
	//首次获取手机号码列表
	public static final String isFirstMobile = "is_first_mobile";
	
	//首次获取账号列表
	public static final String isFirstAccount = "is_first_account";
	
	//logo图片
	public static final String AGENT_LOGO_IMAGE = "agent_game_logo_image";
		
	//启动页图片
	public static final String AGENT_INIT_IMAGE = "agent_game_init_image";
	
	//logo图片
	public static final String LOGO_IMAGE = "game_logo_image";
	
	//启动页图片
	public static final String INIT_IMAGE = "game_init_image";
	
	public static final String DRAG_IMAGE = "drag_image";
	
	public static final String DRAG_LEFT_IMAGE = "drag_left_image";
	
	public static final String DRAG_RIGHT_IMAGE = "drag_right_image";
	
	//新版本2.2合并之前版本的账号,是否读取过一次
	public static final String isReadLastVersion = "is_read_last_version";
	
	//最后一次登录用户的渠道信息
	public static final String LAST_AGENT_ID = "last_agent_id";
	
	//是否显示过引导页
	public static final String IS_SHOW_GUIDE = "is_show_guide";
}
