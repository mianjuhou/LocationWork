package com.jtv.locationwork.adapter;

import java.util.List;

import com.jtv.locationwork.entity.WorkShopJson;
import com.jtv.locationwork.util.TextUtil;
import com.plutus.libraryui.spinner.BaseNiceSpinnerAdapter;

import android.content.Context;

public class WorkShopAdapter extends BaseNiceSpinnerAdapter {

	private List<WorkShopJson> data;

	public WorkShopAdapter(Context context, List<WorkShopJson> data) {
		super(context);
		this.data = data;
	}

	@Override
	public Object getItem(int position) {
		try {
			WorkShopJson departJson = data.get(position);
			return departJson;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}

	@Override
	public int getCount() {
		if (data != null) {
			return data.size();
		}
		return 0;
	}

	@Override
	public String getItemInDataset(int position) {
		String name = "";

		try {
			if (data == null) {
				return "";
			}
			WorkShopJson departJson = data.get(position);
			name = departJson.getShortname();
			if (TextUtil.isEmpty(name)) {
				name = "";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return name;
	}

	@Override
	public Object getName(int position) {
		String name = "";

		try {
			if (data == null) {
				return "";
			}
			WorkShopJson departJson = data.get(position);
			name = departJson.getShortname();
			if (TextUtil.isEmpty(name)) {
				name = "";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return name;
	}

}
