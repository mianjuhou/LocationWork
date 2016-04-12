package com.jtv.locationwork.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.R;

/**
 * @author: zn E-mail:zhangn@jtv.com.cn
 * @version:2015-1-17 类说明:部门级别适配器
 */

public class DepartLevelAdapter extends BaseJsonAdapter implements
		SpinnerAdapter {

	public DepartLevelAdapter(Context context, JSONArray array) {
		super(context, array);
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

		try {
			JSONObject obj = mArray.getJSONObject(arg0);
			holder.getItemName().setText(obj.getString("name"));
		} catch (JSONException e) {
			UToast.makeShortTxt(mContext, e.getMessage());
		}
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
