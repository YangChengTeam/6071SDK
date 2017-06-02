package com.game.sdk.ui.adapter;

import java.util.List;

import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.utils.AccountInfoUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.MobileInfoUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserLoginListAdapter extends BaseAdapter {

	private Context mContext;

	private RelativeLayout rl_delete;

	protected LayoutInflater inflater;

	List<UserInfo> userLoginInfos;
	
	private int type;
	
	public CloseListener closeListener;
	
	public interface CloseListener{
		void popWindowClose();
	}
	
	public void setCloseListener(CloseListener closeListener) {
		this.closeListener = closeListener;
	}
	
	public UserLoginListAdapter(Context context, List<UserInfo> dataList,int type) {
		this.mContext = context;
		this.userLoginInfos = dataList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.type = type;
	}
	
	public void initDataList(List<UserInfo> datas){
		if(userLoginInfos != null && userLoginInfos.size() > 0){
			userLoginInfos.clear();
		}
		this.userLoginInfos = datas;
	}
	
	@Override
	public int getCount() {
		return userLoginInfos.size() > 3 ? 3 : userLoginInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return userLoginInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			View view = inflater.inflate(MResource.getIdByName(mContext, "layout", "login_user_list_item"), null);

			convertView = view;
		}
		
		Logger.msg("position---"+position);
		
		TextView tv = (TextView) convertView.findViewById(MResource.getIdByName(mContext, "id", "login_user_name_tv"));
		rl_delete = (RelativeLayout) convertView.findViewById(MResource.getIdByName(mContext, "id", "rl_delete"));
		
		/*if(GoagalInfo.userInfo != null){
			
			if(GoagalInfo.userInfo.username.equals(userLoginInfos.get(position).username)){
				rl_delete.setVisibility(View.INVISIBLE);
			}else{
				rl_delete.setVisibility(View.VISIBLE);
			}
		}*/
		
		rl_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(type == 0){
					MobileInfoUtil.deleteUserInfo(mContext, userLoginInfos.get(position));
				}
				
				if(type == 1){
					AccountInfoUtil.deleteUserInfo(mContext, userLoginInfos.get(position));
				}
				
				UserLoginInfodao.getInstance(mContext).deleteUserLoginByName(userLoginInfos.get(position).username);
				userLoginInfos.remove(position);
				
				if(getCount() == 0){
					closeListener.popWindowClose();
				}else{
					notifyDataSetChanged();
				}
			}
		});
		tv.setText(userLoginInfos.get(position).username);
		return convertView;
	}
}