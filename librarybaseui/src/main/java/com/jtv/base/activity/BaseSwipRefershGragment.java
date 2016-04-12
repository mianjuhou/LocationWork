
package com.jtv.base.activity;

import com.jtv.base.fragment.BaseFragmet;
import com.plutus.libraryui.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public abstract class BaseSwipRefershGragment extends BaseFragmet implements OnRefreshListener {

	protected View view;
	protected Context con;

	@Override
	public View creatView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.base_swipepullto, null);
		return view;
	}

	@Override
	public void data(Bundle savedInstanceState) {
		con = getActivity();
		onCreatInit(savedInstanceState);
	}

	protected SwipeRefreshLayout mSwipeRefreshLayout;

	protected ListView mList;

	protected LinearLayout lin_Content;

	protected void onCreatInit(Bundle savedInstanceState) {
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl);
		lin_Content = (LinearLayout) view.findViewById(R.id.content_head);
		View addHeadView = addHeadView();
		
		if (addHeadView == null) {
			lin_Content.setVisibility(View.GONE);
		} else {
			lin_Content.addView(addHeadView);
		}
		
		view.findViewById(R.id.title).setVisibility(View.GONE);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mList = (ListView) view.findViewById(R.id.list);
	}

	@Override
	public void onRefresh() {
	}

	protected abstract View addHeadView();

}
