package com.game.sdk.ui.fragment;

import com.game.sdk.utils.MResource;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 短信一键登录
 * 
 * @author admin
 *
 */
public class MessageQuickFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(MResource.getIdByName(getActivity(), "layout", "message_quick_login"),
				container, false);
		return view;
	}
}
