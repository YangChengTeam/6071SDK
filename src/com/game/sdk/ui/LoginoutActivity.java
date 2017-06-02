package com.game.sdk.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.LoginOutInfo;
import com.game.sdk.security.Base64;
import com.game.sdk.security.Encrypt;
import com.game.sdk.service.DownGameBoxService;
import com.game.sdk.service.DownOtherApkService;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.PathUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.game.sdk.utils.ZipUtil;
import com.game.sdk.view.PicassoRoundTransform;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class LoginoutActivity extends BaseActivity implements OnClickListener {

	private ImageView leftIv;

	private ImageView closeIv;
	
	private LinearLayout bgLayout;

	private RelativeLayout titleLayout;
	
	private ImageView loginOutIv;

	private Button loginOutBtn;

	private Button downGameBoxBtn;

	private LoginOutInfo loginOutInfo;

	@Override
	public String getLayoutId() {
		return "fysdk_activity_loginout";
	}

	@Override
	public void initVars() {
		super.initVars();
		setOrientation();
	}

	@Override
	public void initViews() {
		super.initViews();

		leftIv = findImageViewByString("left_iv");
		closeIv = findImageViewByString("close_iv");
		bgLayout = (LinearLayout) findViewByString("bg_layout");
		titleLayout = (RelativeLayout) findViewByString("common_title_layout");
		loginOutIv = findImageViewByString("login_out_iv");
		loginOutBtn = findButtonByString("login_out_btn");
		downGameBoxBtn = findButtonByString("down_game_box_btn");
		closeIv.setOnClickListener(this);
		loginOutBtn.setOnClickListener(this);
		loginOutIv.setOnClickListener(this);
		downGameBoxBtn.setOnClickListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		
		initTheme();
		
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.logoBitmp != null) {
			leftIv.setImageBitmap(GoagalInfo.inItInfo.logoBitmp);
		}

		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.logout != null) {
			loginOutInfo = GoagalInfo.inItInfo.logout;
			Picasso.with(this).load(loginOutInfo.img).transform(new PicassoRoundTransform()).into(loginOutIv);
		}
		
		if (CheckUtil.isInstallGameBox(LoginoutActivity.this)) {
			downGameBoxBtn.setText(getResources().getString(MResource.getIdByName(this, "string", "open_game_box_text")));
		}
	}
	
	public void initTheme() {
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.template != null) {
			String bgColor = GoagalInfo.inItInfo.template.bgColor;
			String headColor = GoagalInfo.inItInfo.template.headColor;
			String btnColor = GoagalInfo.inItInfo.template.btnColor;
			String changeFontColor = GoagalInfo.inItInfo.template.fontColor;

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
				GradientDrawable btnBg = (GradientDrawable) downGameBoxBtn.getBackground();
				btnBg.setColor(Color.parseColor("#" + btnColor));
				
				//退出按钮设置边框线
				GradientDrawable drawable = (GradientDrawable) loginOutBtn.getBackground();
				drawable.setStroke(DimensionUtil.dip2px(LoginoutActivity.this, 1), Color.parseColor("#" + btnColor));
				
				//退出按钮设置字体的颜色
				loginOutBtn.setTextColor(Color.parseColor("#" + btnColor));
			}
			
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LoginoutActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setOrientation();
	}

	public void setOrientation() {
		double scaleW = 0.7;
		double scaleH = 0.7;
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 0) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			scaleW = 0.7;
			scaleH = 0.7;
		}
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.vertical == 1) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			scaleW = 0.85;
			scaleH = 0.5;
		}

		LayoutParams params = getWindow().getAttributes();
		// params.width = DimensionUtil.dip2px(this, 400);
		params.width = (int) (DimensionUtil.getWidth(LoginoutActivity.this) * scaleW);
		params.height = (int) (DimensionUtil.getHeight(LoginoutActivity.this) * scaleH);

		params.gravity = Gravity.CENTER;
		getWindow().setAttributes(params);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == MResource.getIdByName(this, "id", "close_iv")) {
			this.finish();
		}

		if (v.getId() == MResource.getIdByName(this, "id", "login_out_btn")) {
			GoagalInfo.isLogin = false;
			GoagalInfo.inItInfo = null;
			GoagalInfo.userInfo = null;

			if (GoagalInfo.loginoutRunnable != null) {
				GoagalInfo.loginoutRunnable.run();
			}
			if (GoagalInfo.initActivity != null) {
				try {
					GoagalInfo.initActivity.finish();
				} catch (Exception e) {
				}
			}
			// 友盟保存数据
			MobclickAgent.onKillProcess(this);
			fyGmaeSDk.setInitOk(false);
			fyGmaeSDk.recycle(2);// 资源回收
			this.finish();
			System.exit(0);
		}
		
		if (v.getId() == MResource.getIdByName(this, "id", "login_out_iv")) {
			otherApkDown();
		}
		if (v.getId() == MResource.getIdByName(this, "id", "down_game_box_btn")) {
			gameBoxDown(1);
		}
	}
	
	public class DownAsyncTask extends AsyncTask<Integer, Integer, Integer> {
		String fileName;
		String url;

		public DownAsyncTask(String fileName, String url) {
			this.fileName = fileName;
			this.url = url;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			return CheckUtil.getFileLengthByUrl(url);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			File downFile = new File(PathUtil.getApkPath(fileName));

			if (result != downFile.length()) {
				downFile.delete();
				downBoxApp(fileName, url);
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(downFile), "application/vnd.android.package-archive");
				startActivity(intent);
			}
		}
	}

	/**
	 * 判断盒子是否下载等逻辑
	 * 
	 * @param type
	 * 
	 *  type = 1 直接打开盒子 
	 *  type = 2 安装过盒子，则通过盒子打开下载"推广APK"的下载界面
	 */
	public void gameBoxDown(int type) {

		String serviceName = "com.game.sdk.service.DownGameBoxService";

		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo != null) {

			if (!StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxPackageName)) {
				
				if (CheckUtil.isInstallGameBox(LoginoutActivity.this)) {
					if (type == 1) {
						Uri uri = Uri.parse("gamebox://?act=MainActivity");
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
					}
					if (type == 2) {
						String pwd = "";
						String mobile = "";
						String userName = "";
						String gameName = "";
						
						if(GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.password)){
							pwd = Base64.encode(Encrypt.encode(GoagalInfo.userInfo.password).getBytes());
						}
						
						if(GoagalInfo.userInfo != null){
							mobile = StringUtils.isEmpty(GoagalInfo.userInfo.mobile) ? GoagalInfo.userInfo.username: GoagalInfo.userInfo.mobile;
							if(mobile == null){
								mobile = "";
							}
						}
						
						if(GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.username)){
							userName = GoagalInfo.userInfo.username;
						}
						
						if(!StringUtils.isEmpty(loginOutInfo.typeVal)){
							if(!StringUtils.isEmpty(loginOutInfo.gameName)){
								try {
									gameName = URLEncoder.encode(loginOutInfo.gameName, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							}
							
							String tempData = Base64.encode(("{\"iconUrl\":\""+loginOutInfo.gameIcon+"\",\"name\":\""+gameName+"\",\"packageName\":\""+loginOutInfo.packageName+"\"," + "\"url\":\""+loginOutInfo.typeVal+"\"}")
									.getBytes());
							
							Uri uri = Uri.parse("gamebox://?act=DownloadActivity&pwd=" + pwd + "&phone=" + mobile + "&username=" + userName + "&data=" + tempData);
							
							Logger.msg("推广APK下载URI---" + uri.toString());
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
						}else{
							Util.toast(this, "下载地址数据错误，请退出后重试");
						}
					}
				} else {
					if (!StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)) {
						if (!SystemUtil.isServiceWork(this, serviceName)) {
							// 如果下载文件存在，直接启动安装
							File downFile = new File(PathUtil.getApkPath("game_box"));
							if (downFile.exists()) {
								if (ZipUtil.isArchiveFile(downFile)) {
									if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo != null
											&& !StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)) {
										new DownAsyncTask("game_box", GoagalInfo.inItInfo.boxInfo.boxDownUrl).execute();
									}
								} else {
									downFile.delete();// 删除下载的错误的盒子文件，提示用户重新下载
									Util.toast(this, "盒子文件错误，请重新下载");
								}

							} else {
								downBoxApp("game_box", GoagalInfo.inItInfo.boxInfo.boxDownUrl);
							}
						} else {
							Util.toast(this, "应用下载中");
						}
					}else {
						Util.toast(this, "盒子下载地址数据错误，请退出后重试");
					}
				}
			}
		} else {
			Util.toast(this, "盒子下载地址数据错误，请退出后重试");
		}
	}
	
	//其他的推广APK通过盒子下载
	public void otherApkDown() {
		
		if (GoagalInfo.inItInfo != null && loginOutInfo != null) {
			
			if(!StringUtils.isEmpty(loginOutInfo.packageName) && loginOutInfo.packageName.equals(getPackageName())){
				Util.toast(this, "应用已安装，请直接使用");
				return;
			}
			
			// 0：为下载其他的APP
			if (loginOutInfo.type == 0) {
				if (CheckUtil.isInstall(this, loginOutInfo.packageName)) {
					PackageManager packageManager = this.getPackageManager();
					Intent intent = new Intent();
					intent = packageManager.getLaunchIntentForPackage(loginOutInfo.packageName);
					startActivity(intent);
				} else {
					//如果推广的APK未安装，则通过盒子下载，首先判断是否安装盒子，未安装盒子则先安装盒子，安装过盒子，则跳转到盒子的下载页面
					if (!StringUtils.isEmpty(loginOutInfo.typeVal)) {
						gameBoxDown(2);
					}else {
						Util.toast(this, "下载地址有误，请稍后重试");
					}
				}
			}
			
			// 1: 打开外部网页
			if (loginOutInfo.type == 1) {
				if (!StringUtils.isEmpty(loginOutInfo.typeVal)) {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(loginOutInfo.typeVal);
					intent.setData(content_url);
					startActivity(intent);
				}
			}
		}else{
			Util.toast(this, "初始化数据错误，请退出后稍后重试");
		}
	}

	public void downBoxApp(String fileName, String url) {
		if (fileName.equals("game_box")) {
			Util.toast(this, "开始下载游戏盒子");
			Intent intent = new Intent(this, DownGameBoxService.class);
			intent.putExtra("downUrl", url);
			startService(intent);
		}

		if (fileName.equals("other_game")) {
			Util.toast(this, "开始下载应用");
			Intent intent = new Intent(this, DownOtherApkService.class);
			intent.putExtra("downUrl", url);
			startService(intent);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("LoginoutActivity");
		MobclickAgent.onPause(this);
	}

}
