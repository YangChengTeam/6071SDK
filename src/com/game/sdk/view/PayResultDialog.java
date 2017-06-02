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

public class PayResultDialog extends Dialog implements android.view.View.OnClickListener {

	private Context mContext;

	private Button continueBtn;

	private Button payExitBtn;
	
	public PayResultListener payResultListener;
	
	public interface PayResultListener{
		public void continuePay();
		public void paySuccess();
	}
	
	public PayResultListener getPayResultListener() {
		return payResultListener;
	}

	public void setPayResultListener(PayResultListener payResultListener) {
		this.payResultListener = payResultListener;
	}

	public PayResultDialog(Context context) {
		super(context, MResource.getIdByName(context, "style", "CustomSdkDialog"));
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	public void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);

		View view = layoutInflater.inflate(MResource.getIdByName(mContext, "layout", "pay_result_dialog"), null);
		continueBtn = (Button) view.findViewById(MResource.getIdByName(mContext, "id", "continue_pay_btn"));
		payExitBtn = (Button) view.findViewById(MResource.getIdByName(mContext, "id", "pay_exit_btn"));

		setContentView(view);

		/*Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		params.width = DimensionUtil.dip2px(mContext, 380);
		// params.y = DimensionUtil.dip2px(mContext, 280);
		params.gravity = Gravity.CENTER;*/

		continueBtn.setOnClickListener(this);
		payExitBtn.setOnClickListener(this);
	}
	
	List<String> imgs = new ArrayList<String>();

	@Override
	public void onClick(View v) {

		if (v.getId() == MResource.getIdByName(mContext, "id", "continue_pay_btn")) {
			payResultListener.continuePay();
		}
		
		if (v.getId() == MResource.getIdByName(mContext, "id", "pay_exit_btn")) {
			payResultListener.paySuccess();
		}
	}

}
