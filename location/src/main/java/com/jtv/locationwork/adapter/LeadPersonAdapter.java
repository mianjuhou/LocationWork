package com.jtv.locationwork.adapter;

import java.util.List;
import java.util.Map;

import com.jtv.hrb.locationwork.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class LeadPersonAdapter extends BaseAdapter implements SpinnerAdapter {

	Context mContext;

	List<Map<String, String>> mArray;

	LayoutInflater mInflater;

	public LeadPersonAdapter(Context context, List<Map<String, String>> array) {
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
		Object obj = null;
		if (mArray != null && mArray.size() > 0) {
			obj = mArray.get(arg0);
		}
		return obj;
	}

	/**
	 * 刷新
	 */
	public void refresh(List<Map<String, String>> mArray) {
		this.mArray = mArray;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;
		View view = arg1;
		if (view == null) {
			view = mInflater.inflate(R.layout.location_jtv_module_basesp_item, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else
			holder = (ViewHolder) view.getTag();
		Map<String, String> personMap = mArray.get(position);
		holder.getItemName().setText(personMap.get("displayname"));
		return view;
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
