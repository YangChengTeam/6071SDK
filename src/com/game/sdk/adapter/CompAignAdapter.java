package com.game.sdk.adapter;

import java.util.List;

import com.game.sdk.domain.CompAign;
import com.game.sdk.ui.MainActivity;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.TimeUtils;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import okhttp3.OkHttpClient;

public class CompAignAdapter extends BaseAdapter {

	private Context mContext;

	private List<CompAign> compaignInfoList;

	OkHttpClient mOkHttpClient;

	public CompAignAdapter(Context mContext, List<CompAign> list) {
		super();
		this.mContext = mContext;
		this.compaignInfoList = list;
		mOkHttpClient = new OkHttpClient();
	}

	public void addNewList(List<CompAign> list) {
		if (compaignInfoList != null) {
			compaignInfoList.addAll(list);
		} else {
			this.compaignInfoList = list;
		}
	}

	@Override
	public int getCount() {
		return compaignInfoList.size();
	}

	@Override
	public Object getItem(int pos) {
		return compaignInfoList.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		final String url = compaignInfoList.get(position).img;
		final int pos = position;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext)
					.inflate(MResource.getIdByName(mContext, "layout", "compaign_item"), null);
			holder.compAignIv = (ImageView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "compaign_iv"));
			holder.compAignTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "compaign_tv"));
			holder.compAignDateTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "compaign_date"));
			holder.detailBtn = (Button) convertView.findViewById(MResource.getIdByName(mContext, "id", "detail_btn"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.compAignTv.setText(compaignInfoList.get(pos).title);
		holder.compAignDateTv.setText(TimeUtils.getTime(new Long(compaignInfoList.get(pos).startTime + "000")));

		if (!StringUtils.isEmpty(url)) {
			Picasso.with(mContext).load(url).into(holder.compAignIv);
		}
		holder.detailBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (compaignInfoList.get(pos) != null) {
					((MainActivity) mContext).detailFragment(compaignInfoList.get(pos).id + "",compaignInfoList.get(pos).type);
				}

			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView compAignIv;
		TextView compAignTv;
		TextView compAignDateTv;
		Button detailBtn;
	}

}
