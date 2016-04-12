package com.jtv.locationwork.adapter;

import java.util.HashMap;
import java.util.List;

import com.jtv.hrb.locationwork.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * @author: zn E-mail:zhangn@jtv.com.cn
 * @version:2015-1-17 类说明
 */

public class WorkShAreaAdapter extends BaseAdapter implements SpinnerAdapter {

	Context mContext;

	List<HashMap<String, String>> mArray;

	LayoutInflater mInflater;

	public WorkShAreaAdapter(Context context, List<HashMap<String, String>> array) {
		this.mContext = context;
		this.mArray = array;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (mArray == null) {
			return 0;
		}
		return mArray.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int arg0) {
		return mArray.get(arg0);
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;
		View view = arg1;
		if (view == null) {
			view = mInflater.inflate(R.layout.location_jtv_module_basesp_item, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else
			holder = (ViewHolder) view.getTag();
		HashMap<String, String> hashMap = (HashMap<String, String>) mArray.get(arg0);
		String name = hashMap.get("departname");
		if (name.length() > 17) {// 防止显示不全，换行
			int index = 15;
			String forward = name.substring(0, index);
			forward = forward + "\r\n";
			String after = name.substring(index);
			name = forward + after;
			forward = null;
			after = null;
		}
		holder.getItemName().setText(name);
		name = null;
		return view;
	}

	public void refreshData(List<HashMap<String, String>> mArray) {
		this.mArray = mArray;
		this.notifyDataSetChanged();
	}

	public void refreshInvailde(List<HashMap<String, String>> mArray) {
		this.mArray = mArray;
		this.notifyDataSetInvalidated();
	}

	class ViewHolder {
		View view;

		TextView itemId;// ID

		TextView itemName;// 名称

		public ViewHolder(View view) {
			this.view = view;
		}

		public TextView getItemId() {
			if (itemId == null) {
				itemId = (TextView) view.findViewById(R.id.tv_itemid);
			}
			return itemId;
		}

		public TextView getItemName() {
			if (itemName == null) {
				itemName = (TextView) view.findViewById(R.id.tv_itemname);
			}
			return itemName;
		}
	}

}
