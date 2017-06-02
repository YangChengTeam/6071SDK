package com.game.sdk.view;

import java.util.ArrayList;
import java.util.List;

import com.game.sdk.utils.MResource;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class CancelConfigDialog extends Dialog implements android.view.View.OnClickListener {

	private Context mContext;

	private Button payConfigBtn;

	private Button payCancelBtn;
	
	public CancelListener cancelListener;
	
	public String orderId;
	
	public interface CancelListener{
		public void cancelConfig(String orderId);
	}
	
	public CancelListener getCancelListener() {
		return cancelListener;
	}

	public void setCancelListener(CancelListener payResultListener) {
		this.cancelListener = payResultListener;
	}

	public CancelConfigDialog(Context context) {
		super(context, MResource.getIdByName(context, "style", "CustomSdkDialog"));
		this.mContext = context;
	}
	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	public void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);

		View view = layoutInflater.inflate(MResource.getIdByName(mContext, "layout", "pay_cancel_dialog"), null);
		payConfigBtn = (Button) view.findViewById(MResource.getIdByName(mContext, "id", "pay_config_btn"));
		payCancelBtn = (Button) view.findViewById(MResource.getIdByName(mContext, "id", "pay_cancel_btn"));

		setContentView(view);

		/*Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		params.width = DimensionUtil.dip2px(mContext, 380);
		// params.y = DimensionUtil.dip2px(mContext, 280);
		params.gravity = Gravity.CENTER;*/

		payConfigBtn.setOnClickListener(this);
		payCancelBtn.setOnClickListener(this);
	}
	
	List<String> imgs = new ArrayList<String>();

	@Override
	public void onClick(View v) {

		if (v.getId() == MResource.getIdByName(mContext, "id", "pay_config_btn")) {
			cancelListener.cancelConfig(orderId);
		}
		
		if (v.getId() == MResource.getIdByName(mContext, "id", "pay_cancel_btn")) {
			this.dismiss();
		}
	}

}
