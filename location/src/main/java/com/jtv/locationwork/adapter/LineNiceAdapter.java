package com.jtv.locationwork.adapter;

import com.plutus.libraryui.spinner.BaseNiceSpinnerAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class LineNiceAdapter extends BaseNiceSpinnerAdapter {

	private String[] data;

	public LineNiceAdapter(Context context, String[] data) {
		super(context);
		this.data = data;
	}
	
	@Override
	public Object getItem(int position) {
		String title = "";
		try {
			title = data[position];
		} catch (Exception e) {

		}

		return title;
	}

	@Override
	public Object getName(int position) {
		String title = "";
		try {
			title = data[position];
		} catch (Exception e) {

		}

		return title;
	}

	@Override
	public int getCount() {

		if (data == null) {
			return 0;
		}
		return data.length;
	}

	@Override
	public Object getItemInDataset(int position) {
		String title = "";
		try {
			title = data[position];
		} catch (Exception e) {

		}

		return title;
	}
}
