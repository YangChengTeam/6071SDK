package com.game.sdk.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.game.sdk.adapter.GamePackageDetailAdapter;
import com.game.sdk.adapter.GamePackageDetailAdapter.DownApkListener;
import com.game.sdk.domain.GamePackageDetail;
import com.game.sdk.domain.GamePackageDetailList;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.engin.GamePackageDetailEngin;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.listeners.Callback;
import com.game.sdk.service.DownGameBoxService;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.PathUtil;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.SystemUtil;
import com.game.sdk.utils.Util;
import com.game.sdk.utils.ZipUtil;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class GamePackageDetailActivity extends BaseActivity implements OnClickListener, DownApkListener,OnScrollListener {

	private ImageView backIv;

	private TextView titleTv;

	private ListView listView;
	
	private View loadMoreView;

	private LinearLayout loadMoreLayout;

	private LinearLayout noMoreLayout;

	private TextView noMoreTv;

	private ImageView noDataIv;
	
	private ImageView loadMoreIcon;
	
	private GamePackageDetailAdapter adapter;

	List<GamePackageDetail> gamePackageDetailList;

	private GamePackageDetailEngin gamePackageDetailEngin;

	private String gameId;
	
	private int lastItem;

	private int currentPage = 1;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				loadMoreLayout.setVisibility(View.GONE);
				stopAnimation();
				
				adapter.addNewList(gamePackageDetailList);
				adapter.notifyDataSetChanged();
				break;
			case 2:
				loadMoreLayout.setVisibility(View.GONE);
				noMoreLayout.setVisibility(View.VISIBLE);
				
				if (gamePackageDetailList != null && gamePackageDetailList.size() == 0) {
					listView.setDividerHeight(DimensionUtil.dip2px(GamePackageDetailActivity.this, 0));
					noMoreLayout.setBackgroundColor(Color.TRANSPARENT);
					noMoreTv.setVisibility(View.GONE);
					noDataIv.setVisibility(View.VISIBLE);
				} else {
					noDataIv.setVisibility(View.GONE);
					noMoreTv.setVisibility(View.VISIBLE);
					noMoreTv.setText(findStringByResId("no_more_text"));
					noMoreTv.setPadding(0, DimensionUtil.dip2px(GamePackageDetailActivity.this, 8), 0, DimensionUtil.dip2px(GamePackageDetailActivity.this, 8));
				}
				
				stopAnimation();

				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "fysdk_activity_game_package_detail";
	}

	@Override
	public void initVars() {
		super.initVars();
		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			gameId = intent.getExtras().getString("gameId");
		}
	}

	@Override
	public void initViews() {
		super.initViews();
		
		loadMoreView = getLayoutInflater().inflate(MResource.getIdByName(this, "layout", "list_view_footer"), null);
		loadMoreLayout = (LinearLayout) loadMoreView
				.findViewById(MResource.getIdByName(this, "id", "load_more_layout"));
		noMoreLayout = (LinearLayout) loadMoreView.findViewById(MResource.getIdByName(this, "id", "no_more_layout"));
		loadMoreIcon = (ImageView) loadMoreView.findViewById(MResource.getIdByName(this, "id", "loading_icon"));
		noMoreTv = (TextView) loadMoreView.findViewById(MResource.getIdByName(this, "id", "no_more_tv"));
		noDataIv = (ImageView)loadMoreView.findViewById(MResource.getIdByName(this, "id", "no_data_iv"));
		
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		titleTv.setText(findStringByResId("game_package_detail_text"));
		listView = (ListView) findViewByString("game_package_detail_list");
		listView.addFooterView(loadMoreView);
		
		gamePackageDetailEngin = GamePackageDetailEngin.getImpl(this);
		backIv.setOnClickListener(this);
		listView.setOnScrollListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();

		gamePackageDetailList = new ArrayList<GamePackageDetail>();
		adapter = new GamePackageDetailAdapter(this, gamePackageDetailList);
		adapter.setDownListener(this);
		listView.setAdapter(adapter);
		//new GamePackageDetailTask().execute();
		loadGamePackageDetailData();
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("GamePackageDetailActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			finish();
		}
	}

	public void loadGamePackageDetailData() {
		
		gamePackageDetailEngin.getGamePackageDetailList(currentPage, gameId, new Callback<GamePackageDetailList>() {

			@Override
			public void onSuccess(ResultInfo<GamePackageDetailList> resultInfo) {

				if (resultInfo.data.list != null && resultInfo.data.list.size() > 0) {
					gamePackageDetailList = resultInfo.data.list;
					// 刷新数据
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				}else{
					// 刷新数据
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
				}
			}

			@Override
			public void onFailure(Response response) {

			}
		});
	}
	
	
	private class GamePackageDetailTask extends AsyncTask<String, Integer, List<GamePackageDetail>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<GamePackageDetail> doInBackground(String... params) {

			gamePackageDetailEngin.getGamePackageDetailList(currentPage,gameId, new Callback<GamePackageDetailList>() {

				@Override
				public void onSuccess(ResultInfo<GamePackageDetailList> resultInfo) {

					if (resultInfo.data.list != null && resultInfo.data.list.size() > 0) {
						gamePackageDetailList = resultInfo.data.list;
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
		protected void onPostExecute(List<GamePackageDetail> result) {
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				gamePackageDetailList = result;

				// 刷新数据
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
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

			if (result != downFile.length()) {
				downFile.delete();
				downBoxApp();
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(downFile), "application/vnd.android.package-archive");
				startActivity(intent);
			}
		}
	}
	
	@Override
	public void gameBoxDown() {
		if (!SystemUtil.isServiceWork(this, "com.game.sdk.service.DownGameBoxService")) {
			// 如果下载文件存在，直接启动安装
			File downFile = new File(PathUtil.getApkPath("game_box"));
			if (downFile.exists()) {
				if (ZipUtil.isArchiveFile(downFile)) {
					if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo != null && !StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)) {
						new DownAsyncTask().execute();
					}
				} else {
					downFile.delete();// 删除下载的错误的盒子文件，提示用户重新下载
					Util.toast(this, "盒子文件错误，请重新下载");
				}
			} else {
				downBoxApp();
			}
		} else {
			Util.toast(this, "游戏盒子下载中");
		}
	}

	public void downBoxApp() {
		// 启动服务重新下载
		if (GoagalInfo.inItInfo != null && GoagalInfo.inItInfo.boxInfo != null
				&& !StringUtils.isEmpty(GoagalInfo.inItInfo.boxInfo.boxDownUrl)) {
			Util.toast(this, "开始下载游戏盒子");
			Intent intent = new Intent(this, DownGameBoxService.class);
			intent.putExtra("downUrl", GoagalInfo.inItInfo.boxInfo.boxDownUrl);
			startService(intent);
		} else {
			Util.toast(this, "下载地址有误，请稍后重试");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("GamePackageDetailActivity");
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码

			if (view.getLastVisiblePosition() == view.getCount() - 1 && noMoreLayout.getVisibility() == View.GONE) {

				if(adapter.getCount() >= 10){
					loadMoreLayout.setVisibility(View.VISIBLE);
					startAnimation();

					currentPage++;
					// new ChargeRecordTask().execute();
					loadGamePackageDetailData();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastItem = firstVisibleItem + visibleItemCount - 1;
	}
	
	/**
	 * 旋转动画
	 * 
	 * @return
	 */
	public Animation rotaAnimation() {
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setInterpolator(new LinearInterpolator());
		ra.setDuration(1500);
		ra.setRepeatCount(-1);
		ra.setStartOffset(0);
		ra.setRepeatMode(Animation.RESTART);
		return ra;
	}
	
	public void startAnimation() {
		if (loadMoreIcon != null) {
			loadMoreIcon.startAnimation(rotaAnimation());
		}
	}

	public void stopAnimation() {
		if (loadMoreIcon != null) {
			loadMoreIcon.clearAnimation();
		}
	}
}
