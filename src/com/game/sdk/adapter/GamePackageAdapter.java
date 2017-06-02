package com.game.sdk.adapter;

import java.util.List;

import com.game.sdk.domain.GamePackage;
import com.game.sdk.ui.GamePackageActivity;
import com.game.sdk.ui.GamePackageDetailActivity;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.Util;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import okhttp3.OkHttpClient;

public class GamePackageAdapter extends BaseAdapter {

	private Context mContext;

	private List<GamePackage> gamePackageList;

	OkHttpClient mOkHttpClient;

	public GamePackageAdapter(Context mContext, List<GamePackage> list) {
		super();
		this.mContext = mContext;
		this.gamePackageList = list;
		mOkHttpClient = new OkHttpClient();
	}

	public void addNewList(List<GamePackage> list) {
		if (gamePackageList != null) {
			gamePackageList.clear();
			gamePackageList = list;
		} else {
			this.gamePackageList = list;
		}
	}

	@Override
	public int getCount() {
		return gamePackageList.size();
	}

	@Override
	public Object getItem(int pos) {
		return gamePackageList.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		final int pos = position;
		final String url = gamePackageList.get(position).ico;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext)
					.inflate(MResource.getIdByName(mContext, "layout", "game_package_item"), null);
			holder.detailLayout = (LinearLayout) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_detail_layout"));
			holder.gamePackageIv = (ImageView) convertView.findViewById(MResource.getIdByName(mContext, "id", "game_iv"));
			holder.gameNameTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_name_tv"));
			holder.gameCountTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_package_count_tv"));
			holder.detailBtn = (Button) convertView.findViewById(MResource.getIdByName(mContext, "id", "detail_btn"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.gameNameTv.setText(gamePackageList.get(position).gameName);
		holder.gameCountTv.setText(gamePackageList.get(position).num);

		if (!StringUtils.isEmpty(url)) {
			Picasso.with(mContext).load(url).into(holder.gamePackageIv);
		}
		
		holder.detailBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, GamePackageDetailActivity.class);
				intent.putExtra("gameId", gamePackageList.get(pos).gameId);
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}

	class ViewHolder {
		LinearLayout detailLayout;
		ImageView gamePackageIv;
		TextView gameNameTv;
		TextView gameCountTv;
		Button detailBtn;
	}
	
}
