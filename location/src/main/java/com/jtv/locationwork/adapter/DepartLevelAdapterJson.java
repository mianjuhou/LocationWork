package com.jtv.locationwork.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.locationwork.entity.DepartLevel;
import com.plutus.libraryui.spinner.BaseNiceSpinnerAdapter;

import android.content.Context;

public class DepartLevelAdapterJson extends BaseNiceSpinnerAdapter {
	private List<DepartLevel> departLevel;

	public DepartLevelAdapterJson(Context context, List<DepartLevel> departLevel) {
		super(context);
		this.departLevel = departLevel;
	}

	@Override
	public Object getItem(int position) {
		if (departLevel != null) {
			try {
				return departLevel.get(position);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		if (departLevel != null) {
			return departLevel.size();
		}
		return 0;
	}

	@Override
	public String getItemInDataset(int position) {
		return departLevel.get(position).getName();
	}

	@Override
	public Object getName(int position) {
		return departLevel.get(position).getName();
	}

}
