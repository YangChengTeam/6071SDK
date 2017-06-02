package com.game.sdk.adapter;

import java.util.List;

import com.game.sdk.domain.GamePackageDetail;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.security.Base64;
import com.game.sdk.security.Encrypt;
import com.game.sdk.utils.CheckUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.Util;
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

public class GamePackageDetailAdapter extends BaseAdapter {

	private Context mContext;

	private List<GamePackageDetail> gamePackageDetailList;

	OkHttpClient mOkHttpClient;

	public DownApkListener downListener;

	public interface DownApkListener {
		public void gameBoxDown();
	}

	public void setDownListener(DownApkListener downListener) {
		this.downListener = downListener;
	}

	public GamePackageDetailAdapter(Context mContext, List<GamePackageDetail> list) {
		super();
		this.mContext = mContext;
		this.gamePackageDetailList = list;
		mOkHttpClient = new OkHttpClient();
	}

	public void addNewList(List<GamePackageDetail> list) {
		if (gamePackageDetailList != null) {
			gamePackageDetailList.addAll(list);
		} else {
			this.gamePackageDetailList = list;
		}
	}

	@Override
	public int getCount() {
		return gamePackageDetailList.size();
	}

	@Override
	public Object getItem(int pos) {
		return gamePackageDetailList.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		final int pos = position;
		final String url = gamePackageDetailList.get(position).img;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext)
					.inflate(MResource.getIdByName(mContext, "layout", "game_package_detail_item"), null);
			holder.gamePackageIv = (ImageView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_iv"));
			holder.gameNameTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_name_tv"));
			holder.gamePackageSulplusTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_package_surplus_tv"));
			holder.gamePackageDetailTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_package_detail_tv"));
			holder.detailGetBtn = (Button) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "detail_get_btn"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!StringUtils.isEmpty(url)) {
			Picasso.with(mContext).load(url).into(holder.gamePackageIv);
		}

		holder.gameNameTv.setText(gamePackageDetailList.get(position).name);
		holder.gamePackageSulplusTv.setText(gamePackageDetailList.get(position).remainNum);
		holder.gamePackageDetailTv.setText(gamePackageDetailList.get(position).content);

		holder.detailGetBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (CheckUtil.isInstallGameBox(mContext)) {
					if (gamePackageDetailList.get(pos) != null) {
						String pwd = Base64.encode(Encrypt.encode(GoagalInfo.userInfo.password).getBytes());
						
						String mobile = StringUtils.isEmpty(GoagalInfo.userInfo.mobile) ? GoagalInfo.userInfo.username: GoagalInfo.userInfo.mobile;
						
						if(gamePackageDetailList.get(pos).isPay.equals("1")){
							
							if(!StringUtils.isEmpty(gamePackageDetailList.get(pos).goodsTypeId)){
								
								String tempData = Base64.encode(("{\"id\":\""+ gamePackageDetailList.get(pos).goodsTypeId+ "\", \"title\":\"礼包\"}").getBytes());
								Logger.msg("tempData->"+"{\"goods_type_id\":\""+ gamePackageDetailList.get(pos).goodsTypeId+ "\", \"title\":\"礼包\"}");
								Uri uri = Uri.parse("gamebox://?act=GoodListActivity&pwd=" + pwd + "&phone="
										+ mobile + "&username=" + GoagalInfo.userInfo.username+"&data="+tempData);
								
								Logger.msg("积分商城URI---" + uri.toString());
								Intent intent = new Intent(Intent.ACTION_VIEW, uri);
								mContext.startActivity(intent);
							}else{
								Util.toast(mContext, "服务器数据错误，请稍后重试");
							}
							
						}else{
							Uri uri = Uri.parse("gamebox://?act=GiftDetailActivity&pwd=" + pwd + "&phone="
									+ mobile + "&username=" + GoagalInfo.userInfo.username + "&data="
									+ gamePackageDetailList.get(pos).id);
							
							Logger.msg("游戏礼包领取URI---" + uri.toString());
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							mContext.startActivity(intent);
						}
						
					}
				}else{
					downListener.gameBoxDown();
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView gamePackageIv;
		TextView gameNameTv;
		TextView gamePackageSulplusTv;
		TextView gamePackageDetailTv;
		Button detailGetBtn;
	}

}
