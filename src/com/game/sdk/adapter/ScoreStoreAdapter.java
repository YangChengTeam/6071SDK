package com.game.sdk.adapter;

import java.util.List;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ScoreStore;
import com.game.sdk.security.Base64;
import com.game.sdk.security.Encrypt;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import okhttp3.OkHttpClient;

public class ScoreStoreAdapter extends BaseAdapter {

	private Context mContext;

	private List<ScoreStore> scoreStoreList;

	OkHttpClient mOkHttpClient;

	public DownApkListener downListener;
	
	public interface DownApkListener {
		public void gameBoxDown();
	}
	
	public void setDownListener(DownApkListener downListener) {
		this.downListener = downListener;
	}
	
	public ScoreStoreAdapter(Context mContext, List<ScoreStore> list) {
		super();
		this.mContext = mContext;
		this.scoreStoreList = list;
		mOkHttpClient = new OkHttpClient();
	}

	public void addNewList(List<ScoreStore> list) {
		if (scoreStoreList != null) {
			scoreStoreList.addAll(list);
		} else {
			this.scoreStoreList = list;
		}
	}

	@Override
	public int getCount() {
		return scoreStoreList.size();
	}

	@Override
	public Object getItem(int pos) {
		return scoreStoreList.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		// final int pos = position;
		final String url = scoreStoreList.get(position).img;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext)
					.inflate(MResource.getIdByName(mContext, "layout", "score_store_item"), null);

			holder.scoreGameIv = (ImageView) convertView.findViewById(MResource.getIdByName(mContext, "id", "game_iv"));
			holder.gameNameTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_name_tv"));
			//holder.scoreTv = (TextView) convertView.findViewById(MResource.getIdByName(mContext, "id", "score_tv"));
			holder.exchangeBtn = (Button) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "exchange_btn"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.gameNameTv.setText(scoreStoreList.get(position).name);
		//holder.scoreTv.setText(scoreStoreList.get(position).stock);

		if (!StringUtils.isEmpty(url)) {
			Picasso.with(mContext).load(url).into(holder.scoreGameIv);
		}

		holder.exchangeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CheckUtil.isInstallGameBox(mContext)) {
					
					String pwd = Base64.encode(Encrypt.encode(GoagalInfo.userInfo.password).getBytes());

					String mobile = StringUtils.isEmpty(GoagalInfo.userInfo.mobile) ? GoagalInfo.userInfo.username: GoagalInfo.userInfo.mobile;
					
					Uri uri = Uri.parse("gamebox://?act=GoodTypeActivity&pwd=" + pwd + "&phone="
							+ mobile + "&username=" + GoagalInfo.userInfo.username +"&data="+GoagalInfo.gameid);
					
					Logger.msg("积分商城URI---" + uri.toString());
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					mContext.startActivity(intent);
				} else {
					downListener.gameBoxDown();
				}
			}
		});
		holder.exchangeBtn.setTag(url);
		return convertView;
	}

	class ViewHolder {
		ImageView scoreGameIv;
		TextView gameNameTv;
		//TextView scoreTv;
		Button exchangeBtn;
	}
	
}
