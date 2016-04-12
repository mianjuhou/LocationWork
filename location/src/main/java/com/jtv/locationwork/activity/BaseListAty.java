package com.jtv.locationwork.activity;

import com.jtv.base.activity.BaseAty;
import com.jtv.hrb.locationwork.R;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class BaseListAty extends BaseAty {

	protected ListView listView;
	protected BaseAdapter adapter;

	@Override
	public void onClick(View v) {

	}

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		setContentView(R.layout.location_jtv_listview);
		listView = (ListView) findViewById(R.id.listview);
	}

	protected void refersh() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

}
