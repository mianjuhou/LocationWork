package com.jtv.locationwork.adapter;

import java.util.List;

import com.jtv.locationwork.entity.DepartJson;
import com.jtv.locationwork.util.TextUtil;
import com.plutus.libraryui.spinner.BaseNiceSpinnerAdapter;

import android.content.Context;

public class DepartNiceSpinerAdapter extends BaseNiceSpinnerAdapter {

	private List<DepartJson> data;

	public DepartNiceSpinerAdapter(Context context, List<DepartJson> data) {
		super(context);
		this.data = data;
	}

	@Override
	public Object getItem(int position) {
		DepartJson departJson = data.get(position);
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
			DepartJson departJson = data.get(position);
			String description = departJson.getDescription();
			if(TextUtil.isEmpty(description)){
				description="";
			}
			return description;
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
			DepartJson departJson = data.get(position);
			String description = departJson.getDescription();
			if(TextUtil.isEmpty(description)){
				description="";
			}
			return description;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}

}
