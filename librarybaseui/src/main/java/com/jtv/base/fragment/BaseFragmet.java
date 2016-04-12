package com.jtv.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragmet extends Fragment {
	public FragmentActivity context;

	public BaseFragmet bf = null;

	public BaseFragmet() {
		// this.bf = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		data(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		return creatView(inflater, container, savedInstanceState);
	}

	public abstract View creatView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	public abstract void data(Bundle savedInstanceState);
}
