package com.jtv.locationwork.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.jtv.base.activity.SwipePulltoRefershAty;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.entity.ItemWoListAttribute;
import com.jtv.locationwork.entity.ItemWonum;
import com.jtv.locationwork.util.CommonViewHolder;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.TextUtil;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GdxqActivity extends SwipePulltoRefershAty {

	private QueryWonumInfo mAdapter;

	private ItemWonum iwl;

	@Override
	public void onClick(View v) {
	}

	protected void init(Bundle savedInstanceState) {

		getHeaderTitleTv().setText(getString(R.string.query));

		setBackOnClickFinish();

		iwl = getIntent().getParcelableExtra(Constants.INTENT_KEY_WONUM);

		clearNull();

		mAdapter = new QueryWonumInfo(this, iwl);

		mList.setAdapter(mAdapter);
	}

	private void clearNull() {

		ArrayList<String> delete = new ArrayList<String>();

		HashMap<String, ItemWoListAttribute> hashMap = iwl.get();

		Set<String> keySet = hashMap.keySet();

		for (String key : keySet) {
			ItemWoListAttribute itemWoListAttribute = hashMap.get(key);

			if (TextUtil.isEmpty(itemWoListAttribute.getDisPlayname())
					|| TextUtil.isEmpty(itemWoListAttribute.getDisValue())) {

				delete.add(key);

			}

		}

		for (String key : delete) {
			hashMap.remove(key);
		}
	}

	class QueryWonumInfo extends BaseAdapter {

		HashMap<String, ItemWoListAttribute> data = new HashMap<String, ItemWoListAttribute>();

		List<String> list = new ArrayList<String>();

		Context con;

		public QueryWonumInfo(Context con, ItemWonum item) {

			data = item.get();
			this.con = con;

			Set<String> keySet = data.keySet();

			for (String string : keySet) {
				list.add(string);
			}

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			String string = list.get(position);
			return data.get(string);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = View.inflate(con, R.layout.location_jtv_woquery_body_gdxq, null);
			}

			String string = list.get(position);

			ItemWoListAttribute itemAttribute = data.get(string);

			TextView tv_name = CommonViewHolder.get(convertView, R.id.tv_name);
			TextView tv_value = CommonViewHolder.get(convertView, R.id.tv_value);

			String disPlayname = itemAttribute.getDisPlayname();
			String disValue = itemAttribute.getDisValue();

			tv_name.setText(disPlayname);
			tv_value.setText(disValue);

			return convertView;
		}

	}

	@Override
	public void onRefresh() {
		super.onRefresh();
		mSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	protected View addHeadView() {
		return null;
	}
}
