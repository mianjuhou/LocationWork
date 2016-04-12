package com.jtv.hrb.locationwork.fragment;

import java.util.ArrayList;
import java.util.Collections;

import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.hrb.locationwork.TurnoutCheck2Activity;
import com.jtv.hrb.locationwork.comparator.HistoryComparator;
import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.hrb.locationwork.domain.TurnoutUiData;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.tencent.mapsdk.a.ac;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TurnoutPage4Fragment extends Fragment {
	private View contentView;
	private TurnoutUiData mUiData;
	private TableLayout tableLayout;
	private TurnoutCheck2Activity activity;
	
	private ArrayList<MeasureData> historyMeasureDatas;
	public TurnoutPage4Fragment(TurnoutUiData turnoutUiData) {
		mUiData=turnoutUiData;
		cp1nanumcfgrownum = mUiData.getRowNums().get(0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (TurnoutCheck2Activity) getActivity();
		//获取当前页的历史数据
		historyMeasureDatas=activity.service.getTurnoutDataByGh(activity.wonum,activity.assetnum,mUiData.partOrder);
		if(historyMeasureDatas!=null&&historyMeasureDatas.size()>0){
			Collections.sort(historyMeasureDatas,new HistoryComparator());
		}
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.sublayout_turnout_4zj, null);
		initContentView();
		return contentView;
	}

	private void initContentView() {
		tableLayout = (TableLayout) contentView.findViewById(R.id.tablelayout);
		int itemDataindex=0;
		for (int i = 0; i < mUiData.rowNum; i++) {
			TableRow tableRow=(TableRow) View.inflate(getActivity(), R.layout.tablelayout_page4_item, null);
			EditText cell1=(EditText) tableRow.getChildAt(0);
			EditText cell2=(EditText) tableRow.getChildAt(1);
			cell1.setTag(i+"_"+0);
			cell2.setTag(i+"_"+1);
			setFocusChangeListener(cell1, cell2);
			if(itemDataindex<historyMeasureDatas.size()){
				String uiTag=(String)cell1.getTag();
				String dataTag=historyMeasureDatas.get(itemDataindex).getTag();
				if(uiTag.equals(dataTag)){
					cell1.setText((int)historyMeasureDatas.get(itemDataindex).getValue1()+"");
					itemDataindex++;
				}
			}
			if(itemDataindex<historyMeasureDatas.size()){
				String uiTag=(String)cell2.getTag();
				String dataTag=historyMeasureDatas.get(itemDataindex).getTag();
				if(uiTag.equals(dataTag)){
					cell2.setText((int)historyMeasureDatas.get(itemDataindex).getValue1()+"");
					itemDataindex++;
				}
			}
			tableLayout.addView(tableRow);
		}
	}
	
	private void setFocusChangeListener(EditText cell1,EditText cell2){
		cell1.setOnFocusChangeListener(mOnFocusChangeListener);
		cell2.setOnFocusChangeListener(mOnFocusChangeListener);
	}
	
	private OnFocusChangeListener mOnFocusChangeListener=new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			EditText et=(EditText) v;
			if(!hasFocus){
				if(!TextUtils.isEmpty(et.getText().toString().trim())){
					String tag=(String) v.getTag();
					String[] splits = tag.split("_");
					String lineNumStr=splits[0];
					String rowNumStr=splits[1];
					int lineNum=Integer.parseInt(lineNumStr);
					
					MeasureData measureData=new MeasureData();
					measureData.setLinenum(lineNum);
					measureData.setTag(tag);
					measureData.setOrgid(GlobalApplication.orgid);
					measureData.setSiteid(GlobalApplication.siteid);
					measureData.setAssetnum(activity.assetnum);
					measureData.setWonum(activity.wonum);
					measureData.setCp1nanumcfgrownum(cp1nanumcfgrownum);
					measureData.setFlag_v("0");
					measureData.setStatus("0");
					measureData.setAssetattrid("ddd");
					measureData.setValue1(getEdtiTextNum(et));
					measureData.setGh(mUiData.partOrder+"");
					
					if("0".equals(rowNumStr)){
						measureData.setNametype("计划");
					}else{
						measureData.setNametype("误差");
					}
					
					double[] gps = SpUtiles.BaseInfo.getGps();
					measureData.setXvalue(gps[0]+"");
					measureData.setYvalue(gps[1]+"");
					measureData.setInspdate(DateUtil.getCurrDateFormat(DateUtil.style_nyrsfm));
					
					activity.service.savePage4Data(measureData);
				}
			}else{
				et.setSelectAllOnFocus(true);
			}
		}
	};
	private String cp1nanumcfgrownum;
	private int getEdtiTextNum(EditText editText) {
		String railNumStr=editText.getText().toString().trim();
		if(!TextUtils.isEmpty(railNumStr)){
			int railNum=Integer.parseInt(railNumStr);
			return railNum;
		}else{
			return 0;
		}
	}

	public void updateSyncState() {
		activity.service.updateTurnoutMeasureDataState(activity.wonum, activity.assetnum, mUiData.partOrder);
	}
}
