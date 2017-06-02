package com.game.sdk.ui.adapter;

import java.util.List;

import com.game.sdk.domain.CouponInfo;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.TimeUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CouponListAdapter extends BaseAdapter {

	private Context mContext;

	protected LayoutInflater inflater;

	List<CouponInfo> couponInfos;

	public CouponListAdapter(Context context, List<CouponInfo> dataList) {
		this.mContext = context;
		this.couponInfos = dataList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void initDataList(List<CouponInfo> datas) {
		if (couponInfos != null && couponInfos.size() > 0) {
			couponInfos.clear();
		}
		this.couponInfos = datas;
	}

	@Override
	public int getCount() {
		return couponInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return couponInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			View view = inflater.inflate(MResource.getIdByName(mContext, "layout", "coupon_list_item"), null);

			convertView = view;
		}
		TextView goodNameTv = (TextView) convertView
				.findViewById(MResource.getIdByName(mContext, "id", "good_name_tv"));
		TextView goodWorthTv = (TextView) convertView
				.findViewById(MResource.getIdByName(mContext, "id", "good_worth_tv"));
		TextView startDateTv = (TextView) convertView
				.findViewById(MResource.getIdByName(mContext, "id", "start_date_tv"));
		TextView endDateTv = (TextView) convertView.findViewById(MResource.getIdByName(mContext, "id", "end_date_tv"));
		
		LinearLayout useDateLayout = (LinearLayout)convertView.findViewById(MResource.getIdByName(mContext, "id", "use_date_layout"));
		
		goodNameTv.setText(couponInfos.get(position).goodsName);
		goodWorthTv.setText(couponInfos.get(position).goodsWorth);
		
		if(!StringUtils.isEmpty(couponInfos.get(position).goodsUcStartTime) && !StringUtils.isEmpty(couponInfos.get(position).goodsUcEndTime)){
			if(couponInfos.get(position).goodsUcStartTime.equals("0") && couponInfos.get(position).goodsUcEndTime.equals("0")){
				useDateLayout.setVisibility(View.INVISIBLE);
			}else{
				useDateLayout.setVisibility(View.VISIBLE);
				startDateTv.setText(TimeUtils.getTime(Long.parseLong(couponInfos.get(position).goodsUcStartTime), TimeUtils.DATE_FORMAT_DATE));
				endDateTv.setText(TimeUtils.getTime(Long.parseLong(couponInfos.get(position).goodsUcEndTime), TimeUtils.DATE_FORMAT_DATE));
			}
		}
		
		return convertView;
	}
}