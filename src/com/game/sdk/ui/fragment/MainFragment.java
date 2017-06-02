package com.game.sdk.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.game.sdk.FYGameSDK;
import com.game.sdk.adapter.MainModuleAdapter;
import com.game.sdk.adapter.ViewPagerAdapter;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ModuleInfo;
import com.game.sdk.domain.ModuleList;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.engin.MainModuleEngin;
import com.game.sdk.engin.PayCoinEngin;
import com.game.sdk.engin.UserInfoEngin;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.listeners.Callback;
import com.game.sdk.security.Base64;
import com.game.sdk.security.Encrypt;
import com.game.sdk.service.DownGameBoxService;
import com.game.sdk.ui.ChargeActivity;
import com.game.sdk.ui.MainActivity;
import com.game.sdk.ui.UserInfoActivity;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.PathUtil;
import com.game.sdk.utils.PreferenceUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.game.sdk.utils.ZipUtil;
import com.game.sdk.view.BoxDownDialog;
import com.game.sdk.view.CustomRoundImageView;
import com.game.sdk.view.ServiceDialog;
import com.game.sdk.view.ShareDialog;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 账号密码登录主界面
 * 
 * @author admin
 * 
 */
public class MainFragment extends BaseFragment implements OnClickListener,
		OnPageChangeListener, OnItemClickListener {

	public FYGameSDK fyGmaeSDk;

	private MainActivity mainActivity;

	private ImageView backIv;
	
	private LinearLayout bgLayout;

	private RelativeLayout titleLayout;
	
	private ImageView titleLogo;

	private ImageView shareIv;

	private LinearLayout nickNameLayout;
	
	private LinearLayout mobileLayout;
	
	private CustomRoundImageView userHeadIv;

	private TextView nicknNameTv;

	private ImageView mobileIv;

	private TextView mobileTv;
	
	private LinearLayout platformCountLayout;
	
	private LinearLayout gameCountLayout;
	
	// 平台币
	private TextView platformCountTv;
	// 游戏币
	private TextView gameCountTv;

	private ImageView platformRefreshIv;

	private ImageView gameMoneyListIv;

	private Button chargeBtn;

	private ViewPager viewPager;

	// 定义ViewPager适配器
	private ViewPagerAdapter vpAdapter;

	// 定义一个ArrayList来存放View
	private ArrayList<View> views;

	private GridView moduleGridView;

	private GridView moduleGridView1;

	private CheckBox autoLoginCk;

	private TextView changeAccountTv;

	// 底部小点的图片
	private ImageView[] points;

	private MainModuleAdapter adapter;

	List<ModuleInfo> moduleInfoList;

	// 记录当前选中位置
	private int currentIndex;

	private UserInfoEngin userInfoEngin;

	private MainModuleEngin mainModuleEngin;

	private ServiceDialog callDialog;

	private ShareDialog shareDialog;

	private PayCoinEngin payCoinEngin;

	Bitmap headBitmap;

	private BoxDownDialog boxDownDialog;
	
	private ImageView userMemberLevelIv;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				adapter.addNewList(moduleInfoList);
				adapter.notifyDataSetChanged();
				break;
			case 2:
				if (GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.face)) {
					Picasso.with(mainActivity).load(GoagalInfo.userInfo.face)
							.into(userHeadIv);
				}
				nicknNameTv.setText(StringUtils.isEmpty(GoagalInfo.userInfo.nickName) ? GoagalInfo.userInfo.username
								: GoagalInfo.userInfo.nickName);
				mobileTv.setText(GoagalInfo.userInfo.mobile);
				platformCountTv
						.setText(!StringUtils.isEmpty(GoagalInfo.userInfo.ttb) ? GoagalInfo.userInfo.ttb
								: "0");
				gameCountTv
						.setText(!StringUtils.isEmpty(GoagalInfo.userInfo.gttb) ? GoagalInfo.userInfo.gttb
								: "0");
				if (GoagalInfo.userInfo.validateMobile == 1) {
					Picasso.with(mainActivity)
							.load(MResource.getIdByName(mainActivity,
									"drawable", "phone_bind_icon"))
							.into(mobileIv);
				}
				
				if(GoagalInfo.userInfo.vipLevel > 0){					
					userMemberLevelIv.setBackgroundResource(MResource.getIdByName(mainActivity, "drawable", "member_level" + GoagalInfo.userInfo.vipLevel));
				}
				
				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "fysdk_main_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		fyGmaeSDk = FYGameSDK.defaultSDK();
		mainActivity = (MainActivity) getActivity();

		boxDownDialog = new BoxDownDialog(mainActivity);
		
		bgLayout = (LinearLayout) findViewByString("bg_layout");
		titleLayout = (RelativeLayout) findViewByString("common_title");
		
		backIv = findImageViewByString("back_iv");
		titleLogo = findImageViewByString("game_sdk_logo");
		shareIv = findImageViewByString("share_iv");
		nickNameLayout = (LinearLayout) findViewByString("nick_name_layout");
		mobileLayout = (LinearLayout) findViewByString("mobile_layout");
		// 个人信息
		userHeadIv = (CustomRoundImageView) findViewByString("user_head_iv");
		userMemberLevelIv = findImageViewByString("user_member_level_tv");
		nicknNameTv = findTextViewByString("nick_name_tv");
		mobileIv = findImageViewByString("mobile_iv");
		mobileTv = findTextViewByString("mobile_tv");
		
		platformCountLayout = (LinearLayout)findViewByString("platform_count_layout");
		gameCountLayout = (LinearLayout)findViewByString("game_count_layout");
		
		platformCountTv = findTextViewByString("platform_count_tv");
		gameCountTv = findTextViewByString("game_count_tv");

		platformRefreshIv = findImageViewByString("platform_refresh_iv");
		gameMoneyListIv = findImageViewByString("game_money_list_iv");
		chargeBtn = findButtonByString("charge_btn");

		autoLoginCk = (CheckBox) findViewByString("auto_login_ck");
		changeAccountTv = findTextViewByString("change_account_tv");

		userInfoEngin = new UserInfoEngin(mainActivity,
				GoagalInfo.userInfo != null ? GoagalInfo.userInfo.userId : "");
		mainModuleEngin = MainModuleEngin.getImpl(getActivity());

		// 实例化ArrayList对象
		views = new ArrayList<View>();

		// 实例化ViewPager
		viewPager = (ViewPager) findViewByString("viewpager");
		viewPager.setOnPageChangeListener(this);

		moduleGridView = (GridView)LayoutInflater.from(getActivity()).inflate(
				MResource.getIdByName(getActivity(), "layout",
						"module_view_pager"), null);

		moduleGridView1 = (GridView)LayoutInflater.from(getActivity()).inflate(
				MResource.getIdByName(getActivity(), "layout",
						"module_view_pager"), null);

		/*moduleGridView = (GridView) moduleView.findViewById(MResource
				.getIdByName(getActivity(), "id", "main_module_grid_view"));

		moduleGridView1 = (GridView) moduleView1.findViewById(MResource
				.getIdByName(getActivity(), "id", "main_module_grid_view"));*/

		views.add(moduleGridView);
		// views.add(moduleView1);
		// 实例化ViewPager适配器
		vpAdapter = new ViewPagerAdapter(views);

		viewPager.setAdapter(vpAdapter);
		backIv.setOnClickListener(this);
		shareIv.setOnClickListener(this);
		moduleGridView.setOnItemClickListener(this);
		chargeBtn.setOnClickListener(this);
		
		platformCountLayout.setOnClickListener(this);
		gameCountLayout.setOnClickListener(this);
		
		//platformRefreshIv.setOnClickListener(this);
		//gameMoneyListIv.setOnClickListener(this);
		userHeadIv.setOnClickListener(this);
		nickNameLayout.setOnClickListener(this);
		mobileLayout.setOnClickListener(this);
		changeAccountTv.setOnClickListener(this);
		
		boolean isAutoLogin = PreferenceUtil.getImpl(mainActivity).getBoolean(Constants.isAutoLogin, false);
		autoLoginCk.setChecked(isAutoLogin);
		
		autoLoginCk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 保存是否自动登录
				PreferenceUtil.getImpl(mainActivity).putBoolean(
						Constants.isAutoLogin, isChecked);
			}
		});
	}

	@Override
	public void initData() {
		super.initData();
		initTheme();
		
		if (GoagalInfo.inItInfo != null) {
			if (GoagalInfo.inItInfo.isSpeedUp == 1) {
				changeAccountTv.setVisibility(View.GONE);
			} else {
				changeAccountTv.setVisibility(View.VISIBLE);
			}
		}

		// 初始化底部滑动导航
		// initPoint();

		moduleInfoList = getModuleList(mainModuleEngin.getUrl());
		
		if(moduleInfoList == null){
			moduleInfoList = new ArrayList<ModuleInfo>();
		}
		
		adapter = new MainModuleAdapter(getActivity(), moduleInfoList);
		moduleGridView.setAdapter(adapter);
		moduleGridView1.setAdapter(adapter);
		new MainModuleTask().execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		//MobclickAgent.onResume(mainActivity);
		MobclickAgent.onPageStart("MainFragment");
		// 获取用户信息
		new UserInfoTask().execute();
	}

	public void initUserInfo() {
		if (GoagalInfo.isLogin && GoagalInfo.userInfo != null) {
			// 先设置用户信息
			Message msg = new Message();
			msg.what = 2;
			handler.sendMessage(msg);
		}
	}
	
	/**
	 * 初始化底部小点
	 */
	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewByString("gride_point_layout");

		points = new ImageView[2];

		// 循环取得小点图片
		for (int i = 0; i < 2; i++) {
			// 得到一个LinearLayout下面的每一个子元素
			points[i] = (ImageView) linearLayout.getChildAt(i);
			// 默认都设为灰色
			points[i].setEnabled(true);
			// 给每个小点设置监听
			points[i].setOnClickListener(this);
			// 设置位置tag，方便取出与当前位置对应
			points[i].setTag(i);
		}

		// 设置当面默认的位置
		currentIndex = 0;
		// 设置为白色，即选中状态
		points[currentIndex].setEnabled(false);
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
		
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.logoBitmp != null) {
			titleLogo.setImageBitmap(GoagalInfo.inItInfo.logoBitmp);
		}
		
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.template != null) {
			String bgColor = GoagalInfo.inItInfo.template.bgColor;
			String headColor = GoagalInfo.inItInfo.template.headColor;
			String btnColor = GoagalInfo.inItInfo.template.btnColor;
			if (!StringUtils.isEmpty(bgColor)) {
				GradientDrawable allBg = (GradientDrawable) bgLayout.getBackground();
				allBg.setColor(Color.parseColor("#" + bgColor));
				// allBg.setAlpha(150);

				if (!StringUtils.isEmpty(headColor)) {
					if (bgColor.equals(headColor)) {
						GradientDrawable titleBg = (GradientDrawable) titleLayout.getBackground();
						titleBg.setColor(Color.parseColor("#00000000"));
					} else {
						GradientDrawable titleBg = (GradientDrawable) titleLayout.getBackground();
						titleBg.setColor(Color.parseColor("#" + GoagalInfo.inItInfo.template.headColor));
					}
				} else {
					GradientDrawable titleBg = (GradientDrawable) titleLayout.getBackground();
					titleBg.setColor(Color.parseColor("#00000000"));
				}
			}
			
			if (!StringUtils.isEmpty(btnColor)) {
				//GradientDrawable btnBg = (GradientDrawable) chargeBtn.getBackground();
				//btnBg.setColor(Color.parseColor("#" + btnColor));
				
				int roundRadius = DimensionUtil.dip2px(getActivity(), 3); // 8dp 圆角半径
			    //默认颜色
			    int fillColor = Color.parseColor("#" + btnColor);//内部填充颜色
			    //按压后颜色
			    int fillColorPressed = Color.parseColor("#979696");
			    
			    //默认
			    GradientDrawable gdNormal = new GradientDrawable();
			    gdNormal.setColor(fillColor);
			    gdNormal.setCornerRadius(roundRadius);
			    
				//按压后
			    GradientDrawable gdPressed = new GradientDrawable();
			    gdPressed.setColor(fillColorPressed);
			    gdPressed.setCornerRadius(roundRadius);
			    
				StateListDrawable stateDrawable = new StateListDrawable();
				
				//获取对应的属性值 Android框架自带的属性 attr
				int pressed = android.R.attr.state_pressed;  
				int window_focused = android.R.attr.state_window_focused;  
				int focused = android.R.attr.state_focused;  
				int selected = android.R.attr.state_selected;
				
				stateDrawable.addState(new int []{pressed , window_focused}, gdPressed);
				stateDrawable.addState(new int []{pressed , - focused}, gdPressed);
				stateDrawable.addState(new int []{selected }, gdPressed);
				stateDrawable.addState(new int []{focused }, gdPressed);
				stateDrawable.addState(new int []{-selected,- focused,- pressed}, gdNormal);
				chargeBtn.setBackgroundDrawable(stateDrawable);
			}
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == findIdByString("back_iv")) {
			((Activity) mainActivity).finish();
			return;
		}
		if (v.getId() == findIdByString("share_iv")) {
			shareDialog = new ShareDialog((Activity) mainActivity);
			shareDialog.show();
			return;
		}

		if (v.getId() == findIdByString("user_head_iv")) {
			Intent intent = new Intent(mainActivity, UserInfoActivity.class);
			startActivity(intent);
			return;
		}

		if (v.getId() == findIdByString("nick_name_layout")) {
			Intent intent = new Intent(mainActivity, UserInfoActivity.class);
			startActivity(intent);
			return;
		}
		
		//跳转到手机号绑定界面
		if (v.getId() == findIdByString("mobile_layout")) {
			GoagalInfo.bindMobileFrom = 0;
			mainActivity.changeFragment(8);
			return;
		}
		
		if (v.getId() == findIdByString("charge_btn")) {
			//Intent intent = new Intent(mainActivity, ChargeActivity.class);
			//startActivity(intent);
			
			Intent intent = new Intent(mainActivity, ChargeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			mainActivity.overridePendingTransition(0, 0);
			return;
		}

		//if (v.getId() == findIdByString("platform_refresh_iv")) {
			// new PayInitTask().execute();
			//new UserInfoTask().execute();
		//}

		//if (v.getId() == findIdByString("game_money_list_iv")) {
		//}
		
		if (v.getId() == findIdByString("platform_count_layout")) {
			toGameBoxPersionInfo();
			return;
		}
		if (v.getId() == findIdByString("game_count_layout")) {
			toGameBoxPersionInfo();
			return;
		}
		if (v.getId() == findIdByString("change_account_tv")) {
			GoagalInfo.loginType = 2;
			// GoagalInfo.userInfo = null;
			GoagalInfo.isLogin = false;
			fyGmaeSDk.switchUser();
			((Activity) mainActivity).finish();
			return;
		}
	}

	/**
	 * 跳转到游戏盒子个人中心页面
	 */
	public void toGameBoxPersionInfo(){
		String pwd = Base64.encode(Encrypt.encode(GoagalInfo.userInfo.password).getBytes());
		
		String mobile = StringUtils.isEmpty(GoagalInfo.userInfo.mobile) ? GoagalInfo.userInfo.username: GoagalInfo.userInfo.mobile;
		
		Uri uri = Uri.parse("gamebox://?act=MainActivity&tab=3&pwd=" + pwd + "&phone=" + mobile + "&username=" + GoagalInfo.userInfo.username);
		
		Logger.msg("游戏盒子个人中心---" + uri.toString());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		mainActivity.startActivity(intent);
	}
	
	/**
	 * 获取用户信息
	 * 
	 * @author admin
	 * 
	 */
	private class UserInfoTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return userInfoEngin.getUserInfo();
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
	 * 主页模块信息
	 * 
	 * @author admin
	 * 
	 */
	private class MainModuleTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			mainModuleEngin.getModuleInfoList(1, new Callback<ModuleList>() {

				@Override
				public void onSuccess(ResultInfo<ModuleList> resultInfo) {

					if (resultInfo.data.list != null
							&& resultInfo.data.list.size() > 0) {
						moduleInfoList = resultInfo.data.list;

						saveModuleList(mainModuleEngin.getUrl(), moduleInfoList);

						// 刷新数据
						Message msg = new Message();
						msg.what = 1;
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
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}

	/**
	 * 存储主页模块信息到SP
	 * 
	 * @param key
	 * @param moduleList
	 */
	private void saveModuleList(String key, List<ModuleInfo> moduleList) {
		if (moduleList != null && moduleList.size() > 0) {
			String moduleStr = JSON.toJSONString(moduleList);
			Logger.msg("save--moduleStr---" + moduleStr);
			try {
				PreferenceUtil.getImpl(getActivity()).putString(key, moduleStr);
			} catch (Exception e) {
				Logger.msg(e.getMessage());
			}
		}
	}

	/**
	 * 从SP读取主页模块信息
	 * 
	 * @param key
	 * @return
	 */
	private List<ModuleInfo> getModuleList(String key) {
		List<ModuleInfo> list = null;
		try {
			String moduleStr = PreferenceUtil.getImpl(getActivity()).getString(
					key, "");
			Logger.msg("get--moduleStr---" + moduleStr);
			if (!StringUtils.isEmpty(moduleStr)) {
				list = JSON.parseArray(moduleStr, ModuleInfo.class);
			}
		} catch (Exception e) {
			Logger.msg(e.getMessage());
		}
		return list;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int pos) {
		setCurDot(pos);
	}

	/**
	 * 设置当前的小点的位置
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > 1 || currentIndex == positon) {
			return;
		}
		points[positon].setEnabled(false);
		points[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (moduleInfoList != null && moduleInfoList.size() > 0) {
			ModuleInfo moduleInfo = moduleInfoList.get(position);
			if (moduleInfo.type == 0
					&& moduleInfo.typeVal.equals(Constants.SERVER_CALL)) {
				callDialog = new ServiceDialog(mainActivity, 0.6f);
				callDialog.setCanceledOnTouchOutside(true);
				callDialog.show();
			}
			if (moduleInfo.type == 0
					&& moduleInfo.typeVal.equals(Constants.COMPAIGN_CENTER)) {
				mainActivity.changeFragment(2);
			}
			if (moduleInfo.type == 0
					&& moduleInfo.typeVal.equals(Constants.CHARGE_RECORD)) {
				/*Intent intent = new Intent(mainActivity,
						ChargeRecordActivity.class);
				startActivity(intent);*/
				
				mainActivity.changeFragment(9);
			}
			if (moduleInfo.type == 0
					&& moduleInfo.typeVal.equals(Constants.SCORE_STORE)) {
				/*Intent intent = new Intent(mainActivity,
						ScoreStoreActivity.class);
				startActivity(intent);*/
				//mainActivity.changeFragment(10);
				
				if (CheckUtil.isInstallGameBox(mainActivity)) {
					
					String pwd = Base64.encode(Encrypt.encode(GoagalInfo.userInfo.password).getBytes());

					String mobile = StringUtils.isEmpty(GoagalInfo.userInfo.mobile) ? GoagalInfo.userInfo.username: GoagalInfo.userInfo.mobile;
					
					Uri uri = Uri.parse("gamebox://?act=GoodTypeActivity&pwd=" + pwd + "&phone="
							+ mobile + "&username=" + GoagalInfo.userInfo.username +"&data="+GoagalInfo.gameid);
					
					Logger.msg("积分商城URI---" + uri.toString());
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					mainActivity.startActivity(intent);
				} else {
					gameBoxDown();
				}
				
			}
			if (moduleInfo.type == 0
					&& moduleInfo.typeVal.equals(Constants.GAME_PACKAGE)) {
				// Intent intent = new Intent(mainActivity,
				// GamePackageActivity.class);
				// startActivity(intent);
				
				/*Intent intent = new Intent(mainActivity,
						GamePackageDetailActivity.class);
				intent.putExtra("gameId", GoagalInfo.gameid);
				startActivity(intent);*/
				
				//mainActivity.changeFragment(11);
				
				if (CheckUtil.isInstallGameBox(mainActivity)) {
					
					String pwd = Base64.encode(Encrypt.encode(GoagalInfo.userInfo.password).getBytes());
					
					String mobile = StringUtils.isEmpty(GoagalInfo.userInfo.mobile) ? GoagalInfo.userInfo.username: GoagalInfo.userInfo.mobile;
					
					String tempData = Base64.encode(("{\"game_id\":\""+ GoagalInfo.gameid + "\", \"game_name\":\"\"}").getBytes());
					Uri uri = Uri.parse("gamebox://?act=GiftListActivity&pwd=" + pwd + "&phone="
							+ mobile + "&username=" + GoagalInfo.userInfo.username+"&data="+tempData);
					
					Logger.msg("积分商城URI---" + uri.toString());
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					mainActivity.startActivity(intent);
					
				}else {
					gameBoxDown();
				}
			}
			
			if (moduleInfo.type == 0
					&& moduleInfo.typeVal.equals(Constants.ACCOUNT_SAFETY)) {
				mainActivity.changeFragment(6);
			}
			
			//游戏中心
			if (moduleInfo.type == 0 && moduleInfo.typeVal.equals(Constants.GAME_CENTER)) {
				if (CheckUtil.isInstallGameBox(mainActivity)) {
					String pwd = Base64.encode(Encrypt.encode(GoagalInfo.userInfo.password).getBytes());
					
					String mobile = StringUtils.isEmpty(GoagalInfo.userInfo.mobile) ? GoagalInfo.userInfo.username: GoagalInfo.userInfo.mobile;
					
					Uri uri = Uri.parse("gamebox://?act=MainActivity&pwd=" + pwd + "&phone="
							+ mobile + "&username=" + GoagalInfo.userInfo.username);
					
					Logger.msg("游戏中心---" + uri.toString());
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					mainActivity.startActivity(intent);
				}else{
					gameBoxDown();
				}
			}
		}
	}
	
	
	public void gameBoxDown() {
		if (!SystemUtil.isServiceWork(mainActivity, "com.game.sdk.service.DownGameBoxService")) {
			// 如果下载文件存在，直接启动安装
			File downFile = new File(PathUtil.getApkPath("game_box"));
			if (downFile.exists()) {
				if (ZipUtil.isArchiveFile(downFile)) {
					if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo != null && !StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)) {
						new DownAsyncTask().execute();
					}
				} else {
					downFile.delete();// 删除下载的错误的盒子文件，提示用户重新下载
					Util.toast(mainActivity, "盒子文件错误，请重新下载");
				}
			} else {
				downBoxApp();
			}
		} else {
			Util.toast(mainActivity, "游戏盒子下载中");
		}
	}

	public void downBoxApp() {
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo != null
				&& !StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)) {
			Util.toast(mainActivity, "开始下载游戏盒子");
			Intent intent = new Intent(mainActivity, DownGameBoxService.class);
			intent.putExtra("downUrl", GoagalInfo.inItInfo.boxInfo.boxDownUrl);
			mainActivity.startService(intent);
		} else {
			Util.toast(mainActivity, "下载地址有误，请稍后重试");
		}
	}
	
	
	public class DownAsyncTask extends AsyncTask<Integer, Integer, Integer> {
		@Override
		protected Integer doInBackground(Integer... params) {
			return CheckUtil.getFileLengthByUrl(GoagalInfo.inItInfo.boxInfo.boxDownUrl);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			File downFile = new File(PathUtil.getApkPath("game_box"));
			
			if(result != downFile.length()){
				downFile.delete();
				downBoxApp();
			}else{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(downFile), "application/vnd.android.package-archive");
				startActivity(intent);
			}
		}
	}
	
	private class PayInitTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			payCoinEngin = new PayCoinEngin(mainActivity,
					GoagalInfo.userInfo.userId);
			return payCoinEngin.run();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				platformCountTv
						.setText(!StringUtils.isEmpty(GoagalInfo.userInfo.ttb) ? GoagalInfo.userInfo.ttb
								: "0");
				gameCountTv
						.setText(!StringUtils.isEmpty(GoagalInfo.userInfo.gttb) ? GoagalInfo.userInfo.gttb
								: "0");
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//MobclickAgent.onPause(mainActivity);
		MobclickAgent.onPageEnd("MainFragment");
	}
}
