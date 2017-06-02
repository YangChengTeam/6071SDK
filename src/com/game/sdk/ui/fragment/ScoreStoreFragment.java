package com.game.sdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.game.sdk.adapter.ScoreStoreAdapter;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.domain.ScoreStore;
import com.game.sdk.domain.ScoreStoreList;
import com.game.sdk.engin.ScoreStoreEngin;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.listeners.Callback;
import com.game.sdk.ui.MainActivity;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author admin
 *
 */
public class ScoreStoreFragment extends BaseFragment implements OnClickListener,OnScrollListener{

	private MainActivity mainActivity;

	private ImageView backIv;

	private TextView titleTv;

	private GridView scoreStoreGridView;

	private ScoreStoreAdapter adapter;

	List<ScoreStore> scoreStoreInfoList;

	List<ScoreStore> scoreStorePageList;
	
	private ScoreStoreEngin scoreStoreEngin;
	
	private LinearLayout noDataLayout;
	
	private int lastItem;

	private int currentPage = 1;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				adapter.addNewList(scoreStorePageList);
				adapter.notifyDataSetChanged();
				break;
			case 2:
				if(adapter != null && adapter.getCount() == 0){
					scoreStoreGridView.setVisibility(View.GONE);
					noDataLayout.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "score_store_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		mainActivity = (MainActivity) getActivity();
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		noDataLayout = (LinearLayout)findViewByString("no_data_layout");
		titleTv.setText(findStringByResId("score_store_text"));
		scoreStoreGridView = (GridView) findViewByString("score_store_grid_view");
		scoreStoreEngin =  ScoreStoreEngin.getImpl(mainActivity);
		backIv.setOnClickListener(this);
		scoreStoreGridView.setOnScrollListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();

		scoreStoreInfoList = new ArrayList<ScoreStore>();
		adapter = new ScoreStoreAdapter(getActivity(), scoreStoreInfoList);
		scoreStoreGridView.setAdapter(adapter);
		
		loadScoreStoreData();
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
		//MobclickAgent.onPageStart("ScoreStoreFragment");
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			mainActivity.changeFragment(1);
		}
	}
	
	public void loadScoreStoreData() {
		scoreStoreEngin.getScoreStoreList(currentPage,GoagalInfo.userInfo.userId, new Callback<ScoreStoreList>() {

			@Override
			public void onSuccess(ResultInfo<ScoreStoreList> resultInfo) {

				if (resultInfo.data != null && resultInfo.data.list != null && resultInfo.data.list.size() > 0) {
					scoreStorePageList = resultInfo.data.list;
					if(scoreStoreInfoList != null && scoreStoreInfoList.size() > 0){
						scoreStoreInfoList.addAll(resultInfo.data.list);
					}else{
						scoreStoreInfoList = resultInfo.data.list;
					}
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
	
	
	/*private class ScoreStoreTask extends AsyncTask<String, Integer, List<ScoreStore>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<ScoreStore> doInBackground(String... params) {
			return scoreStoreEngin.run(currentPage);
		}

		@Override
		protected void onPostExecute(List<ScoreStore> result) {
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				scoreStorePageList = result;
				if(scoreStoreInfoList != null && scoreStoreInfoList.size() > 0){
					scoreStoreInfoList.addAll(result);
				}else{
					scoreStoreInfoList = result;
				}
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
	}*/
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(mainActivity);
		//MobclickAgent.onPageEnd("ScoreStoreFragment");
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码

			if (view.getLastVisiblePosition() == view.getCount() - 1) {

				if(adapter.getCount() >= 10){
					
					currentPage++;
					
					//new ScoreStoreTask().execute();
					
					loadScoreStoreData();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastItem = firstVisibleItem + visibleItemCount - 1;
	}
}
