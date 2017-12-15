package com.game.sdk.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.game.sdk.FYGameSDK;
import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;

import android.app.Activity;
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
	
	@Override
	protected void onResume() {
		super.onResume();
		Logger.msg("onresume---start--->");
		//callOnResume(GoagalInfo.tempActivity);
		Logger.msg("onresume---end--->");
	}
	
	private void callOnResume(Activity activity){
		if(activity == null){
			Logger.msg("Game Activity is null");
			return;
		}
		
        Method method = null;
        try {
            method = activity.getClass().getDeclaredMethod("onResume");
            method.setAccessible(true);
            method.invoke(activity);
            Logger.msg("Game Activity callOnResume---Is Ok--->");
            
            if(fyGmaeSDk != null){
            	fyGmaeSDk.removeFloatButton();
            	 Logger.msg("callOnResume---removeFloatButton--->");
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
