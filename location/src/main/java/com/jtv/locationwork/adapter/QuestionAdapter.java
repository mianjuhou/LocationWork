package com.jtv.locationwork.adapter;

import java.util.List;

import com.jtv.locationwork.tree.ParmterMutableTreeNode;
import com.plutus.libraryui.spinner.BaseNiceSpinnerAdapter;

import android.content.Context;

public class QuestionAdapter extends BaseNiceSpinnerAdapter {
	private List<ParmterMutableTreeNode> departLevel;

	public QuestionAdapter(Context context, List<ParmterMutableTreeNode> departLevel) {
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
		String title = "";
		try {
			title = (String) departLevel.get(position).getUserObject();
		} catch (Exception e) {
			
		}

		return title;
	}

	@Override
	public Object getName(int position) {
		String title = "";
		try {
			title = (String) departLevel.get(position).getUserObject();
		} catch (Exception e) {
			
		}
		return title;
	}

}
