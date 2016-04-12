package com.jtv.base.activity;

import com.jtv.base.fragment.BaseFragmet;
import com.plutus.libraryui.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseGridViewFragment extends BaseFragmet {
	protected View view;

	@Override
	public View creatView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.base_gridview_fragment, null);
		return view;
	}

}
