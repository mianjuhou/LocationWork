package com.jtv.locationwork.adapter;

import java.util.List;

import com.plutus.libraryui.spinner.BaseNiceSpinnerAdapter;

import android.content.Context;

public class LBaseNiceSpiner<T> extends BaseNiceSpinnerAdapter {
	private List<T> departLevel;

	public LBaseNiceSpiner(Context context, List<T> departLevel) {
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
		if(departLevel==null||departLevel.get(position)==null){
			return "";
		}
		return departLevel.get(position).toString();
	}

	@Override
	public Object getName(int position) {
		if(departLevel==null||departLevel.get(position)==null){
			return "";
		}
		return departLevel.get(position).toString();
	}

}
