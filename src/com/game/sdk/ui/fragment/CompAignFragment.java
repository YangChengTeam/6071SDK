package com.game.sdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.game.sdk.adapter.CompAignAdapter;
import com.game.sdk.domain.CompAign;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.engin.CompAignEngin;
import com.game.sdk.ui.MainActivity;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 
 * @author admin
 *
 */
public class CompAignFragment extends BaseFragment implements OnClickListener,OnScrollListener{

	private MainActivity mainActivity;

	private ImageView backIv;

	private TextView titleTv;

	private GridView compAignGridView;

	private CompAignAdapter adapter;

	List<CompAign> compAignInfoList;

	List<CompAign> compAignPageList;
	
	private CompAignEngin compAignEngin;
	
	private LinearLayout noDataLayout;
	
	private int lastItem;

	private int currentPage = 1;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				adapter.addNewList(compAignPageList);
				adapter.notifyDataSetChanged();
				break;
			case 2:
				if(adapter != null && adapter.getCount() == 0){
					compAignGridView.setVisibility(View.GONE);
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
		return "compaign_fragment";
	}

	@Override
	public void initViews() {
		super.initViews();
		mainActivity = (MainActivity) getActivity();
		backIv = findImageViewByString("back_iv");
		titleTv = findTextViewByString("title_tv");
		noDataLayout = (LinearLayout)findViewByString("no_data_layout");
		titleTv.setText(findStringByResId("compaign_center_text"));
		compAignGridView = (GridView) findViewByString("compaign_grid_view");
		compAignEngin = new CompAignEngin(mainActivity, GoagalInfo.userInfo.userId);
		backIv.setOnClickListener(this);
		compAignGridView.setOnScrollListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		// initTheme();

		compAignInfoList = new ArrayList<CompAign>();
		adapter = new CompAignAdapter(getActivity(), compAignInfoList);
		compAignGridView.setAdapter(adapter);

		compAignGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (compAignInfoList != null && compAignInfoList.get(position) != null) {
					int activityId = compAignInfoList.get(position).id;
					mainActivity.detailFragment(activityId + "", compAignInfoList.get(position).type);
				}
			}
		});

		new CompAignTask().execute();
	}

	/**
	 * 初始化主题颜色
	 */
	public void initTheme() {
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//MobclickAgent.onResume(mainActivity);
		MobclickAgent.onPageStart("CompAignFragment");
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			mainActivity.changeFragment(1);
		}
	}

	private class CompAignTask extends AsyncTask<String, Integer, List<CompAign>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CompAign> doInBackground(String... params) {
			return compAignEngin.run(currentPage);
		}

		@Override
		protected void onPostExecute(List<CompAign> result) {
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				compAignPageList = result;
				if(compAignInfoList != null && compAignInfoList.size() > 0){
					compAignInfoList.addAll(result);
				}else{
					compAignInfoList = result;
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
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//MobclickAgent.onPause(mainActivity);
		MobclickAgent.onPageEnd("CompAignFragment");
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码

			if (view.getLastVisiblePosition() == view.getCount() - 1) {

				if(adapter.getCount() >= 10){
					
					currentPage++;
					
					new CompAignTask().execute();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastItem = firstVisibleItem + visibleItemCount - 1;
	}
}
