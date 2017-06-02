package com.game.sdk.ui.fragment;

import com.game.sdk.domain.CompAignDetail;
import com.game.sdk.engin.CompAignDetailEngin;
import com.game.sdk.ui.MainActivity;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 账号密码登录主界面
 * 
 * @author admin
 *
 */
public class CompAignDetailFragment extends BaseFragment implements OnClickListener {

	private MainActivity mainActivity;

	private ImageView backIv;

	private ScrollView typeSdkView;

	private TextView titleTv;

	private TextView compAignTitleTv;

	private ImageView compAignIv;

	private TextView bodyTv;

	private WebView webView;

	private TextView compAignStartDateTv;

	private TextView compAignEndDateTv;

	private CompAignDetailEngin compAignDetailEngin;

	private CompAignDetail compAignDetail;

	private String activityId;

	private int type;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (compAignDetail != null) {

					if (type == 0) {
						compAignTitleTv.setText(compAignDetail.title);
						if (compAignDetail.img != null) {
							Picasso.with(mainActivity).load(compAignDetail.img).into(compAignIv);
						}
						bodyTv.setText(compAignDetail.body);
						compAignStartDateTv.setText(compAignDetail.startTime);
						compAignEndDateTv.setText(compAignDetail.endTime);
					}

					if (type == 1) {
						webView.loadUrl(compAignDetail.typeValue);
						webView.setWebViewClient(new WebViewClient() {
							@Override
							public boolean shouldOverrideUrlLoading(WebView view, String url) {
								// TODO Auto-generated method stub
								// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
								view.loadUrl(url);
								return true;
							}
						});
					}
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "compaign_detail";
	}

	@Override
	public void initViews() {
		super.initViews();
		mainActivity = (MainActivity) getActivity();
		backIv = findImageViewByString("back_iv");

		typeSdkView = (ScrollView) findViewByString("type_sdk_view");
		titleTv = findTextViewByString("title_tv");
		titleTv.setText(findStringByResId("compaign_center_text"));

		compAignTitleTv = findTextViewByString("compaign_title_tv");
		compAignIv = findImageViewByString("compaign_iv");
		bodyTv = findTextViewByString("body_tv");
		compAignStartDateTv = findTextViewByString("start_date_tv");
		compAignEndDateTv = findTextViewByString("end_date_tv");
		webView = (WebView) findViewByString("web_view");

		Bundle bundle = getArguments();
		if (bundle != null) {
			activityId = bundle.getString("aid");
			type = bundle.getInt("type");
		}

		backIv.setOnClickListener(this);
		compAignDetailEngin = new CompAignDetailEngin(mainActivity, activityId);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();
		if (type == 0) {
			typeSdkView.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
		}
		if (type == 1) {
			typeSdkView.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
		}
		new CompAignDetailTask().execute();
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(mainActivity);
		//MobclickAgent.onPageStart("CompAignDetailFragment");
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			mainActivity.changeFragment(2);
		}
	}

	private class CompAignDetailTask extends AsyncTask<String, Integer, CompAignDetail> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected CompAignDetail doInBackground(String... params) {
			return compAignDetailEngin.run();
		}

		@Override
		protected void onPostExecute(CompAignDetail result) {
			super.onPostExecute(result);
			if (result != null) {
				compAignDetail = result;
				// 刷新数据
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(mainActivity);
		//MobclickAgent.onPageEnd("CompAignDetailFragment");
	}
}
