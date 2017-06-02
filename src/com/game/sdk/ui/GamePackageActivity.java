package com.game.sdk.ui;

import java.util.ArrayList;
import java.util.List;

import com.game.sdk.adapter.GamePackageAdapter;
import com.game.sdk.domain.GamePackage;
import com.game.sdk.domain.GamePackageList;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.engin.GamePackageEngin;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.listeners.Callback;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GamePackageActivity extends BaseActivity implements OnClickListener {

	private ImageView backIv;

	private TextView titleTv;

	private ListView listView;

	private GamePackageAdapter adapter;

	List<GamePackage> list;

	private GamePackageEngin gamePackageEngin;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				adapter.addNewList(list);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "fysdk_activity_game_package";
	}

	@Override
	public void initViews() {
		super.initViews();
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		titleTv.setText(findStringByResId("game_package_text"));
		listView = (ListView) findViewByString("game_package_list");
		gamePackageEngin = GamePackageEngin.getImpl(this);
		backIv.setOnClickListener(this);
		
	}
	
	@Override
	public void initData() {
		super.initData();
		// initTheme();
		
		list = new ArrayList<GamePackage>();
		adapter = new GamePackageAdapter(this, list);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(list != null && list.get(position) != null){
					Intent intent = new Intent(GamePackageActivity.this, GamePackageDetailActivity.class);
					intent.putExtra("gameId", list.get(position).gameId);
					startActivity(intent);
				}
			}
		});
		
		new GamePackageTask().execute();
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("GamePackageActivity");
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			finish();
		}
	}
	
	private class GamePackageTask extends AsyncTask<String, Integer, List<GamePackage>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<GamePackage> doInBackground(String... params) {
			gamePackageEngin.getScoreGameList(GoagalInfo.userInfo.userId, new Callback<GamePackageList>() {

				@Override
				public void onSuccess(ResultInfo<GamePackageList> resultInfo) {

					if (resultInfo.data.list != null && resultInfo.data.list.size() > 0) {
						list = resultInfo.data.list;

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
		protected void onPostExecute(List<GamePackage> result) {
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				list = result;

				// 刷新数据
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("GamePackageActivity");
		MobclickAgent.onPause(this);
	}
}
