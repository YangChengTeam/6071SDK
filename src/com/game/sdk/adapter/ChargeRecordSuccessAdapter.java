package com.game.sdk.adapter;

import java.util.List;

import com.game.sdk.domain.ChargeRecord;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.view.CancelConfigDialog;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChargeRecordSuccessAdapter extends BaseAdapter {

	private Context mContext;

	private List<ChargeRecord> chargeRecordList;
	
	public CancelConfigDialog cancelDialog;
	
	public interface ContinuePayListener {
		void continuePay(String orderId, String realMoney);

		void cancelPay(String orderId);
	}

	public ContinuePayListener payListener;

	public void setPayListener(ContinuePayListener payListener) {
		this.payListener = payListener;
	}

	public ChargeRecordSuccessAdapter(Context mContext, List<ChargeRecord> list,CancelConfigDialog cancelDialog) {
		super();
		this.mContext = mContext;
		this.chargeRecordList = list;
		this.cancelDialog = cancelDialog;
	}
	
	public void addList(List<ChargeRecord> list) {
		if (chargeRecordList != null) {
			chargeRecordList.addAll(list);
		} else {
			this.chargeRecordList = list;
		}
	}
	
	public void addNewList(List<ChargeRecord> list) {
		if (chargeRecordList != null) {
			//chargeRecordList.clear();
			chargeRecordList.addAll(list);
		} else {
			this.chargeRecordList = list;
		}
	}
	
	public void clearAllList(){
		if (chargeRecordList != null) {
			chargeRecordList.clear();
		}
	}
	
	@Override
	public int getCount() {
		return chargeRecordList.size();
	}

	@Override
	public Object getItem(int pos) {
		return chargeRecordList.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		final int pos = position;

		ChargeRecord chargeRecord = chargeRecordList.get(position);
		String orderSn = chargeRecordList.get(position).orderSn;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext)
					.inflate(MResource.getIdByName(mContext, "layout", "fysdk_charge_record_item"), null);
			holder.chargeIv = (ImageView)convertView
			.findViewById(MResource.getIdByName(mContext, "id", "charge_iv"));
			holder.orderIdTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "order_id_tv"));
			holder.orderStateTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "order_state_tv"));
			holder.gameNameTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "game_name_tv"));
			holder.chargeDateTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "charge_date_tv"));
			holder.chargeMoneyTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "charge_money_tv"));
			holder.chargeRealMoneyTv = (TextView) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "charge_real_money_tv"));
			

			holder.continuePayLayout = (RelativeLayout) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "continue_pay_layout"));
			holder.coutinuePayBtn = (Button) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "continue_pay_btn"));
			holder.cancelPayBtn = (Button) convertView
					.findViewById(MResource.getIdByName(mContext, "id", "cancel_pay_btn"));

			convertView.setTag(holder);
			holder.continuePayLayout.setTag(orderSn);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		holder.orderIdTv.setText(chargeRecordList.get(position).orderSn);
		holder.orderStateTv.setText(Html.fromHtml(chargeRecordList.get(position).statusMsg));
		holder.gameNameTv.setText(chargeRecordList.get(position).desp);
		holder.chargeDateTv.setText(chargeRecordList.get(position).finishTime);
		holder.chargeMoneyTv.setText(chargeRecordList.get(position).money);
		holder.chargeRealMoneyTv.setText(
				chargeRecordList.get(position).rmbMoney != null ? chargeRecordList.get(position).rmbMoney : "0");
		
		if(chargeRecordList.get(position) != null && !StringUtils.isEmpty(chargeRecordList.get(position).payWayTitle)){
			
			String zfbString = mContext.getResources().getString(MResource.getIdByName(mContext, "string", "alipay_pay_text"));
			
			String wxString = mContext.getResources().getString(MResource.getIdByName(mContext, "string", "wxpay_pay_text"));
			
			if(chargeRecordList.get(position).payWayTitle.equals(zfbString)){
				holder.chargeIv.setBackgroundResource(MResource.getIdByName(mContext, "drawable", "alipay_icon"));
			}
			
			if(chargeRecordList.get(position).payWayTitle.equals(wxString)){
				holder.chargeIv.setBackgroundResource(MResource.getIdByName(mContext, "drawable", "tencent_pay_icon"));
			}
			
		}
		
		if (chargeRecordList.get(position).status == 1) {
			holder.continuePayLayout.setVisibility(View.VISIBLE);
		}else{
			holder.continuePayLayout.setVisibility(View.GONE);
		}
		
		holder.coutinuePayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (chargeRecordList.get(pos).status == 1) {
					payListener.continuePay(chargeRecordList.get(pos).orderSn, chargeRecordList.get(pos).rmbMoney);
				}
			}
		});
		
		holder.cancelPayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (chargeRecordList.get(pos).status == 1) {
					
					if(cancelDialog != null){
						cancelDialog.setOrderId(chargeRecordList.get(pos).orderSn);
						cancelDialog.show();
					}
					
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView chargeIv;
		TextView orderIdTv;
		TextView orderStateTv;
		TextView gameNameTv;
		TextView chargeDateTv;
		TextView chargeMoneyTv;
		TextView chargeRealMoneyTv;
		RelativeLayout continuePayLayout;
		Button coutinuePayBtn;
		Button cancelPayBtn;
	}

}
