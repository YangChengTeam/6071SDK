package com.game.sdk;

import java.io.File;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentCancelMsg;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.Util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TTWSDKActivity extends Activity implements OnClickListener {

	public FYGameSDK fyGmaeSDk;
	private EditText et_money;
	private TextView tv_msg;
	private Button btn_login, btn_charger;
	
	public static TTWSDKActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		instance = this;
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		fyGmaeSDk = FYGameSDK.defaultSDK();
		// fyGmaeSDk.openLogout();
		fyGmaeSDk.initSDK(TTWSDKActivity.this, new OnSDKInitListener() {
			@Override
			public void initSuccess() {
				//Util.toast(TTWSDKActivity.this, "初始化成功");
				Logger.msg("初始化成功");
				Logger.msg("处理后的结果值------" + JSON.toJSONString(GoagalInfo.inItInfo));
			}
			
			@Override
			public void initFailure() {
				Logger.msg("初始化失败");
				//Util.toast(TTWSDKActivity.this, "初始化失败");
			}
		}, new Runnable() {
			@Override
			public void run() {
				Logger.msg("切换账号回调--->");
				//Toast.makeText(TTWSDKActivity.this, "切换账号回调", Toast.LENGTH_LONG).show();
				//TTWSDKActivity.this.finish();
				//System.exit(0);
			}
		}, new Runnable() {
			@Override
			public void run() {
				Logger.msg("退出游戏回调--->");
				//Toast.makeText(TTWSDKActivity.this, "退出游戏回调", Toast.LENGTH_LONG).show();
				//TTWSDKActivity.this.finish();
				//System.exit(0);
			}
		});
		
		setContentView(MResource.getIdByName(getApplication(), Constants.Resouce.LAYOUT, "ttw_sdk"));
		btn_login = (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_login"));
		btn_charger = (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_charger"));
		tv_msg = (TextView) findViewById(MResource.getIdByName(getApplication(), "id", "tv_msg"));

		et_money = (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "et_money"));

		btn_login.setOnClickListener(this);
		btn_charger.setOnClickListener(this);

	}

	private static final String PATH = Environment.getExternalStorageDirectory() + "/6071GameBox2SDK";
	
	private static void makeBaseDir() {
	        File dir = new File(PATH);
	        if (!dir.exists()) {
	            dir.mkdir();
	        }
	}
	
	private static String getFilePath(String name) {
        makeBaseDir();
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getAbsolutePath() + "/" + name;
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// 登录事件
		if (null != btn_login && btn_login.getId() == v.getId()) {
			
			//String pathName = getFilePath("zdhtest");
			//Util.toast(this, "path---"+pathName);
			fyGmaeSDk.login(TTWSDKActivity.this, false, new OnLoginListener() {
				@Override
				public void loginSuccess(LogincallBack logincallback) {
					//Util.toast(TTWSDKActivity.this, "登录成功");
					fyGmaeSDk.createFloatButton();
				}
				
				@Override
				public void loginError(LoginErrorMsg errorMsg) {
					Util.toast(TTWSDKActivity.this, errorMsg.msg);
				}
			});
			return;
		}
		
		// 充值事件
		if (null != btn_charger && btn_charger.getId() == v.getId() && fyGmaeSDk.isInitOk()) {
			String money_str = et_money.getText().toString().trim();
			String money = "0.1f";
			if (!TextUtils.isEmpty(money_str) && !"".equals(money_str)) {
				money = money_str;
			}
			
			int max=9999999;
	        int min=1000000;
	        Random random = new Random();
	        
	        //支付时，订单编号不能相同,随机产生一个订单编号,方便测试
	        int number = random.nextInt(max)%(max-min+1) + min;
			String orderId = "20151215121112-2227-" + number;
			
			fyGmaeSDk.pay(TTWSDKActivity.this, "114811482001126", money, "1046", "阴阳师:640枚勾玉", "阴阳师:640枚勾玉", orderId, new OnPaymentListener() {
						@Override
						public void paymentSuccess(PaymentCallbackInfo callbackInfo) {
							Logger.msg("充值金额数：" + callbackInfo.money + " 消息提示：" + callbackInfo.msg);
						}

						@Override
						public void paymentError(PaymentErrorMsg errorMsg) {
							Logger.msg("充值失败：code:" + errorMsg.code + "  ErrorMsg:" + errorMsg.msg + "  预充值的金额：" + errorMsg.money);
						}
						
						@Override
						public void paymentCancel(PaymentCancelMsg cancelMsg) {
							// TODO Auto-generated method stub
							//Toast.makeText(getApplication(), "充值取消",Toast.LENGTH_LONG).show();
							Logger.msg("充值取消paymentCancel--->");
						}
					});
			return;
		}
	}

	@Override
	protected void onDestroy() {
		// fyGmaeSDk.exitSDK();//游戏退出调用
		super.onDestroy();
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		fyGmaeSDk.removeFloatButton();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		fyGmaeSDk.createFloatButton();
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		/*
		 * Intent intent = new Intent(this, LoginoutActivity.class);
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * startActivity(intent); return;
		 */
		fyGmaeSDk.exitSDK();
		return;
	}
	
}
