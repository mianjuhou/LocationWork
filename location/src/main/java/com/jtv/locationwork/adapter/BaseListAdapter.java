package com.jtv.locationwork.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class BaseListAdapter<T> extends BaseAdapter {
	protected Context con;
	protected List<T> list;

	public BaseListAdapter(Context con, List<T> list) {
		this.list = list;
		this.con = con;
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if(list!=null){
			return list.get(position);
		}
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void refershAdapter(ArrayList<T> list) {
		this.list = list;
		notifyDataSetChanged();
	}
}
