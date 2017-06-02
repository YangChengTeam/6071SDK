package com.game.sdk.ui.fragment;

import com.game.sdk.utils.MResource;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseFragment extends Fragment {

	public View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(MResource.getIdByName(getActivity(), "layout", getLayoutId()), container, false);
		}
		
		initVars();
		initViews();
		initData();
		return mView;
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
		return MResource.getIdByName(getActivity(), "id", idString);
	}

	public View findViewByString(String idString) {
		return mView.findViewById(findIdByString(idString));
	}
	
	public String findStringByResId(String idString){
		return getActivity().getResources().getString(MResource.getIdByName(getActivity(), "string", idString));
	}
	
}
