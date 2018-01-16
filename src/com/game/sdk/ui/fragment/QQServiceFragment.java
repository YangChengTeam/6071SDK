package com.game.sdk.ui.fragment;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.ui.LoginActivity;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.Util;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author admin
 *
 */
public class QQServiceFragment extends BaseFragment implements OnClickListener {

	private LoginActivity loginActivity;

	private ImageView backIv;

	private TextView titleTv;

	private LinearLayout firstQQLayout;

	private LinearLayout secondQQLayout;
	
	private TextView firstTv;
	
	private TextView secondTv;
	
	private String[] kefu = null;

	@Override
	public String getLayoutId() {
		return "fysdk_qq_service_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		loginActivity = (LoginActivity) getActivity();
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		firstQQLayout = (LinearLayout) findViewByString("first_qq_service_layout");
		secondQQLayout = (LinearLayout) findViewByString("second_qq_service_layout");
		firstTv = findTextViewByString("first_tv");
		secondTv = findTextViewByString("second_tv");
		titleTv.setText(findStringByResId("service_qq_num_text"));
		backIv.setOnClickListener(this);
		firstQQLayout.setOnClickListener(this);
		secondQQLayout.setOnClickListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();

		kefu = kefuQQ(GoagalInfo.inItInfo.gameKefuQQ);
		
		if(kefu != null){
			if(!StringUtils.isEmpty(kefu[0]) && kefu[0].length() > 16){
				firstTv.setText("客服QQ群1");
			}
			
			if(!StringUtils.isEmpty(kefu[1]) && kefu[1].length() > 16){
				secondTv.setText("客服QQ群2");
			}
		}
		
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}

	@Override
	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(loginActivity);
		MobclickAgent.onPageStart("QQServiceFragment");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			if (GoagalInfo.qqKefuFrom == 0) {
				loginActivity.changeFragment(2);
			}

			if (GoagalInfo.qqKefuFrom == 1) {
				loginActivity.changeFragment(3);
			}
		}

		if (v.getId() == findIdByString("first_qq_service_layout")) {
			if (kefu != null && kefu.length > 0) {
				startQQ(kefu[0]);
			}
		}
		if (v.getId() == findIdByString("second_qq_service_layout")) {
			if (kefu != null && kefu.length > 1) {
				startQQ(kefu[1]);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(loginActivity);
		MobclickAgent.onPageEnd("QQServiceFragment");
	}

	public String[] kefuQQ(String qqNumString) {
		if (!StringUtils.isEmpty(qqNumString)) {
			return qqNumString.split(",");
		} else {
			return null;
		}
	}

	public void startQQ(String qqNum) {
		if (StringUtils.isEmpty(qqNum)) {
			return;
		}

		CheckUtil.setPackageNames(loginActivity);
		if (!CheckUtil.isQQAvilible(loginActivity)) {
			Util.toast(loginActivity, "请安装QQ");
		} else {
			// 加入QQ群
			if (qqNum.length() > 16) {
				joinQQGroup(qqNum);
			} else {
				String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum;
				loginActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		}
	}

	/****************
	 * 发起添加群流程。群号：XXX 的 key 为：xxxxx 调用 joinQQGroup(xxxx) 即可发起手Q客户端申请加群XXX
	 *
	 * @param key
	 *            由官网生成的key
	 * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	 ******************/
	public boolean joinQQGroup(String key) {
		Intent intent = new Intent();
		intent.setData(Uri
				.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D"
						+ key));
		// 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
		// //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		try {
			startActivity(intent);
			return true;
		} catch (Exception e) {
			// 未安装手Q或安装的版本不支持
			return false;
		}
	}

}
