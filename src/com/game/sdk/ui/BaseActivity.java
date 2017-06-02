package com.game.sdk.ui;

import com.game.sdk.FYGameSDK;
import com.game.sdk.utils.MResource;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseActivity extends FragmentActivity {
	
	public FYGameSDK fyGmaeSDk;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(this, "layout", getLayoutId()));
		fyGmaeSDk = FYGameSDK.defaultSDK();
		initVars();
		initViews();
		initData();
	}
	
	public abstract String getLayoutId();

	public void initVars() {
	}

	public void initViews() {
		
	}
	
	public void initData(){
		
	}
	
	public TextView findTextViewByString(String idString) {
		return (TextView) findViewByString(idString);
	}

	public Button findButtonByString(String idString) {
		return (Button) findViewByString(idString);
	}

	public EditText findEditTextByString(String idString) {
		return (EditText) findViewByString(idString);
	}
	
	public ImageView findImageViewByString(String idString) {
		return (ImageView) findViewByString(idString);
	}
	
	public int findIdByString(String idString) {
		return MResource.getIdByName(this, "id", idString);
	}

	public View findViewByString(String idString) {
		return findViewById(findIdByString(idString));
	}
	
	public String findStringByResId(String idString){
		return getResources().getString(MResource.getIdByName(this, "string", idString));
	}
}
