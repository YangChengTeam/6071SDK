package com.game.sdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.game.sdk.adapter.GameCoinAdapter;
import com.game.sdk.domain.GameCoin;
import com.game.sdk.domain.GameCoinList;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.engin.GameCoinEngin;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.listeners.Callback;
import com.game.sdk.ui.MainActivity;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * 
 * @author admin
 *
 */
public class GameCoinListFragment extends BaseFragment implements OnClickListener {

	private MainActivity mainActivity;

	private ImageView backIv;

	private TextView titleTv;

	private ListView listView;

	private GameCoinAdapter adapter;

	List<GameCoin> gameCoinList;

	private GameCoinEngin gameCoinEngin;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				adapter.addNewList(gameCoinList);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "game_coin_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		mainActivity = (MainActivity) getActivity();
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		titleTv.setText(findStringByResId("game_coin_list_text"));
		listView = (ListView) findViewByString("game_coin_list");
		gameCoinEngin = GameCoinEngin.getImpl(mainActivity);
		backIv.setOnClickListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();

		gameCoinList = new ArrayList<GameCoin>();
		adapter = new GameCoinAdapter(getActivity(), gameCoinList);
		listView.setAdapter(adapter);
		new GameCoinTask().execute();
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
		//MobclickAgent.onPageStart("GameCoinListFragment");
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			mainActivity.changeFragment(1);
		}
	}
	
	private class GameCoinTask extends AsyncTask<String, Integer, List<GameCoin>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<GameCoin> doInBackground(String... params) {
			gameCoinEngin.getGameCoinList(GoagalInfo.userInfo.userId, new Callback<GameCoinList>() {

				@Override
				public void onSuccess(ResultInfo<GameCoinList> resultInfo) {

					if (resultInfo != null && resultInfo.data.gameList != null && resultInfo.data.gameList.size() > 0) {
						gameCoinList = resultInfo.data.gameList;

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
		protected void onPostExecute(List<GameCoin> result) {
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				gameCoinList = result;

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
		//MobclickAgent.onPageEnd("GameCoinListFragment");
	}
}
