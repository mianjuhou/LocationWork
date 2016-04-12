package com.jtv.locationwork.adapter;

import java.util.List;

import com.jtv.locationwork.entity.PersionJson;
import com.plutus.libraryui.spinner.BaseNiceSpinnerAdapter;

import android.content.Context;

public class PersionSpinnerAdapter extends BaseNiceSpinnerAdapter{

	private List<PersionJson> data;

	public PersionSpinnerAdapter(Context context, List<PersionJson> data) {
		super(context);
		this.data = data;
	}

	@Override
	public Object getItem(int position) {
		PersionJson departJson = data.get(position);
		return departJson;
	}

	@Override
	public int getCount() {
		if(data!=null){
			return data.size();
		}
		return 0;
	}

	@Override
	public String getItemInDataset(int position) {
		try {
			if(data==null){
				return "";
			}
			PersionJson departJson = data.get(position);
			return departJson.getDisplayname();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Object getName(int position) {
		try {
			if(data==null){
				return "";
			}
			PersionJson departJson = data.get(position);
			return departJson.getDisplayname();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
