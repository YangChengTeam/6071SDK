package com.game.sdk.adapter;

import java.util.List;

import com.game.sdk.domain.ModuleInfo;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import okhttp3.OkHttpClient;

public class MainModuleAdapter extends BaseAdapter {

	private Context mContext;

	private List<ModuleInfo> moduleInfoList;

	OkHttpClient mOkHttpClient;

	public MainModuleAdapter(Context mContext, List<ModuleInfo> list) {
		super();
		this.mContext = mContext;
		this.moduleInfoList = list;
		mOkHttpClient = new OkHttpClient();
	}

	public void addNewList(List<ModuleInfo> list) {
		if (moduleInfoList != null) {
			moduleInfoList.clear();
			moduleInfoList = list;
		} else {
			this.moduleInfoList = list;
		}
	}

	@Override
	public int getCount() {
		return moduleInfoList.size();
	}

	@Override
	public Object getItem(int pos) {
		return moduleInfoList.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		final String url = moduleInfoList.get(position).ico;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext)
					.inflate(MResource.getIdByName(mContext, "layout", "main_module_item"), null);
			holder.moduleIv = (ImageView) convertView.findViewById(MResource.getIdByName(mContext, "id", "module_iv"));
			holder.moduleTv = (TextView) convertView.findViewById(MResource.getIdByName(mContext, "id", "module_tv"));
			
			//提示数量
			holder.numLayout = (RelativeLayout) convertView.findViewById(MResource.getIdByName(mContext, "id", "num_layout"));
			holder.numTv = (TextView) convertView.findViewById(MResource.getIdByName(mContext, "id", "num_tv"));
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(moduleInfoList.get(position).num > 0){
			holder.numLayout.setVisibility(View.VISIBLE);
			holder.numTv.setText(moduleInfoList.get(position).num + "");
		}else{
			holder.numLayout.setVisibility(View.INVISIBLE);
		}
		
		holder.moduleTv.setText(moduleInfoList.get(position).title);

		if (!StringUtils.isEmpty(url)) {
			Picasso.with(mContext).load(url).into(holder.moduleIv);
		}
		
		return convertView;
	}

	class ViewHolder {
		ImageView moduleIv;
		TextView moduleTv;
		RelativeLayout numLayout;
		TextView numTv;
	}
	
}
