package com.jtv.locationwork.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author: zn E-mail:zhangn@jtv.com.cn
 * @version:2014-11-11 类说明：适配器基类
 */

public class BaseJsonAdapter extends BaseAdapter{

	Context mContext;
	JSONArray mArray;//赋值列表
	LayoutInflater mInflater;

	public BaseJsonAdapter(Context context, JSONArray array) {
		this.mContext = context;
		this.mArray = array;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if(mArray==null){
			return 0;
		}
		return mArray.length();
	}

	@Override
	public Object getItem(int arg0) {
		try {
			return mArray.get(arg0);
		} catch (JSONException e) {
			return null;
		}
		
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return null;
	}

}
