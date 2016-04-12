package com.jtv.locationwork.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jtv.base.activity.BaseAty;
import com.jtv.hrb.locationwork.R;

public abstract class SwipePulltoRefershAty2 extends BaseAty implements OnRefreshListener {

	protected SwipeRefreshLayout mSwipeRefreshLayout;

	protected ListView mList;

	protected LinearLayout lin_Content;

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		setContentView(R.layout.base_swipepullto2);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl);
		lin_Content = (LinearLayout) findViewById(R.id.content_head);
		View addHeadView = addHeadView();
		if(addHeadView==null){
			lin_Content.setVisibility(View.GONE);
		}else{
			lin_Content.addView(addHeadView);
		}
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mList = (ListView) findViewById(R.id.list);
		init(savedInstanceState);
	}

	@Override
	public void onRefresh() {
	}

	protected abstract void init(Bundle savedInstanceState);
	protected abstract View  addHeadView();

}
