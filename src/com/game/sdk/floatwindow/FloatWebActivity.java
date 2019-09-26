package com.game.sdk.floatwindow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import com.game.sdk.FYGameSDK;
import com.game.sdk.TTWAppService;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.ui.BaseActivity;
import com.game.sdk.utils.DialogUtil;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.ThreadPoolManager;
import com.game.sdk.utils.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FloatWebActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "FloatWebActivity";
	private WebView wv;

	private String url, title;

	public static OnPaymentListener paymentListener;
	public static String isnowpay = "0";
	public static boolean ischarge;

	@Override
	public String getLayoutId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initVars() {
		// TODO Auto-generated method stub
		super.initVars();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		super.initViews();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(MResource.getIdByName(getApplication(), "layout", "sdk_float_web"));

		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		title = intent.getStringExtra("title");

		wv = (WebView) findViewById(MResource.getIdByName(getApplication(), "id", "wv_content"));

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setLoadsImagesAutomatically(true);
		wv.getSettings().setAppCacheEnabled(false);
		wv.getSettings().setDomStorageEnabled(true);
		/*
		 * CloseWindowJavaScriptInterface closejs = new
		 * CloseWindowJavaScriptInterface(); closejs.ctx = this;
		 * wv.addJavascriptInterface(closejs, "AndroidObject");
		 */

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				wv.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						DialogUtil.showDialog(FloatWebActivity.this, true, "网页正在加载...");
					}
				});
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				try {
					DialogUtil.dismissDialog();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		wv.loadUrl(url);
	}

	@Override
	public void onResume() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 强制竖屏

		// TODO Auto-generated method stub
		if (FloatWebActivity.isnowpay.equals("2")) {
			String str = FloatWebActivity.nowpayCode;
			Intent intent = this.getIntent();
			float charge_money = intent.getFloatExtra("money", 0.0f);
			if (str.equals("00")) {
				PaymentCallbackInfo pci = new PaymentCallbackInfo();
				pci.money = charge_money;
				pci.msg = "支付成功";
				FloatWebActivity.paymentListener.paymentSuccess(pci);
				Util.toast(this, "支付成功");
				ThreadPoolManager.getInstance().addTask(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject json = new JSONObject();
							json.put("a", TTWAppService.gameid);
							json.put("b", TTWAppService.userinfo.username);
							json.put("timestamp", Util.getOrderId());
							json.put("version", FYGameSDK.defaultSDK().getVersion());
							//GetDataImpl.getInstance(FloatWebActivity.this).getTTB(json.toString());
						} catch (NullPointerException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			}
			if (str.equals("02")) {
				PaymentErrorMsg msg_e = new PaymentErrorMsg();
				msg_e.code = 02;
				msg_e.msg = "支付取消";
				msg_e.money = charge_money;
				Util.toast(this, msg_e.msg);
				FloatWebActivity.paymentListener.paymentError(msg_e);
			}
			if (str.equals("01")) {

				PaymentErrorMsg msg_e = new PaymentErrorMsg();
				msg_e.code = 03;
				msg_e.msg = FloatWebActivity.nowpayMsg;
				msg_e.money = charge_money;
				Util.toast(this, msg_e.msg);
				FloatWebActivity.paymentListener.paymentError(msg_e);
			}
			FloatWebActivity.isnowpay = "0";
			// this.finish();// 不管支付是否成功 直接退出游戏界面

		}
		super.onResume();
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()) {
			wv.goBack();// 返回前一个页面
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.finish();
	}

	public static String nowpayCode;
	public static String nowpayMsg;

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && null != data) {

			Bundle extras = data.getExtras();

			Bitmap photo = null;
			if (extras != null) {
				photo = extras.getParcelable("data");

			} else {
				Uri selectedImage = data.getData();

				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				Bitmap bmp = BitmapFactory.decodeFile(picturePath, options);
				options.inSampleSize = options.outWidth / 200;
				options.inJustDecodeBounds = false;
				photo = BitmapFactory.decodeFile(picturePath, options);

				if (photo == null) {
					try {
						Bitmap temp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
						photo = Bitmap.createScaledBitmap(temp, 150, 150, false);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (photo != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);// (0 -
																		// 100)压缩文件
				byte[] byteArray = stream.toByteArray();
				String streamStr = Base64.encodeToString(byteArray, Base64.DEFAULT);
				String image = "data:image/png;base64," + streamStr;
				DialogUtil.showDialog(this, "正在上传图像...");
				new UploadImageAsyncTask(image).execute();
			}
		}

		if (data == null) {
			return;
		}
		if (isnowpay.equals("1")) {
			String msg = data.getExtras().getString("respMsg");
			String errorcode = data.getExtras().getString("errorCode");
			String respCode = data.getExtras().getString("respCode");

			FloatWebActivity.nowpayMsg = msg;
			FloatWebActivity.nowpayCode = respCode;
			FloatWebActivity.isnowpay = "2";
		}

	}

	/**
	 * 上传图片
	 * 
	 * @author Administrator
	 */
	public class UploadImageAsyncTask extends AsyncTask<Void, Void, String> {
		private String imageBase64str;

		public UploadImageAsyncTask(String imageBase64str) {
			this.imageBase64str = imageBase64str;
		}

		@Override
		protected String doInBackground(Void... params) {
			//String result = GetDataImpl.getInstance(FloatWebActivity.this).uploadImage(imageBase64str);
			String result = null;
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
			try {
				DialogUtil.dismissDialog();
				JSONObject rjson = new JSONObject(result);
				int status = rjson.getInt("status");
				if (status == 1) {
					String url = rjson.getString("data");
					FloatWebActivity.this.wv.loadUrl("javascript:previewImage('" + url + "')");
				}
			} catch (Exception e) {
				Util.toast(FloatWebActivity.this, "上传失败，请重新上传");
			}
		}
	}

}
