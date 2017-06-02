package com.game.sdk.adapter;

import java.util.List;

import com.game.sdk.domain.GameCoin;
import com.game.sdk.utils.MResource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GameCoinAdapter extends BaseAdapter {

	private Context mContext;

	private List<GameCoin> gameCoinList;

	public GameCoinAdapter(Context mContext, List<GameCoin> list) {
		super();
		this.mContext = mContext;
		this.gameCoinList = list;
	}

	public void addNewList(List<GameCoin> list) {
		if (gameCoinList != null) {
			gameCoinList.clear();
			gameCoinList = list;
		} else {
			this.gameCoinList = list;
		}
	}

	@Override
	public int getCount() {
		return gameCoinList.size();
	}

	@Override
	public Object getItem(int pos) {
		return gameCoinList.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext)
					.inflate(MResource.getIdByName(mContext, "layout", "game_coin_item"), null);
			holder.gameNameTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_name_tv"));
			holder.gameMoneyTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_money_tv"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.gameNameTv.setText(gameCoinList.get(position).name);
		holder.gameMoneyTv.setText(gameCoinList.get(position).money);
		return convertView;
	}

	class ViewHolder {
		TextView gameNameTv;
		TextView gameMoneyTv;
	}

}
