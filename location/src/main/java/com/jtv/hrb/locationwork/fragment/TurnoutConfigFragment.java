package com.jtv.hrb.locationwork.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.QuestionTurnoutActivity;
import com.jtv.hrb.locationwork.R;
import com.jtv.hrb.locationwork.TurnoutCheck2Activity;
import com.jtv.hrb.locationwork.comparator.HistoryComparator;
import com.jtv.hrb.locationwork.comparator.QuestionComparator;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.hrb.locationwork.domain.TurnoutUiData;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.SpUtiles;

import ct.ce;

@SuppressLint("InflateParams")
public class TurnoutConfigFragment extends Fragment {
	public static int QUEST_REQUEST_CODE=100;
	private TurnoutUiData mUiData;
	private View contentView;
	private TableLayout tableLayout;
	private TurnoutCheck2Activity activity;
	//
	private int FalseEditableColor;
	//
	private ArrayList<MeasureData> historyMeasureDatas;
	private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(//
			LinearLayout.LayoutParams.MATCH_PARENT,//
			LinearLayout.LayoutParams.WRAP_CONTENT);
	public TurnoutConfigFragment(TurnoutUiData turnoutUiData) {
		mUiData = turnoutUiData;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (TurnoutCheck2Activity) getActivity();
		FalseEditableColor=getResources().getColor(R.color.false_editable);
		//获取当前页的历史数据
		historyMeasureDatas=activity.service.getTurnoutDataByGh(activity.wonum,activity.assetnum,mUiData.partOrder);
		if(historyMeasureDatas!=null&&historyMeasureDatas.size()>0){
			Collections.sort(historyMeasureDatas,new HistoryComparator());
			//获取问题数据
			List<MeasureData> initQuestions = activity.service.getTurnoutQuestions(historyMeasureDatas.get(0));
			if(initQuestions!=null&&initQuestions.size()>0){
				Collections.sort(initQuestions, new QuestionComparator());
				questionDatas = initQuestions;
			}else{
				questionDatas=null;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.sublayout_turnout_config, null);
		tableLayout = (TableLayout) contentView.findViewById(R.id.tablelayout);
		questionLayout = (LinearLayout) contentView.findViewById(R.id.ll_questions);
		
		initTableViewAndData();
		initQtViewAndData();
		
		return contentView;
	}

	private void initQtViewAndData() {
		if(syncState!=1){
			contentView.findViewById(R.id.btn_other_quest).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent qtRequestIntent=new Intent(activity, QuestionTurnoutActivity.class);
					qtRequestIntent.putExtra("uiData", JSON.toJSONString(mUiData));
					qtRequestIntent.putExtra("isAdd", true);
					qtRequestIntent.putExtra("linename", activity.description);
					qtRequestIntent.putExtra("gh", mUiData.partOrder);
					qtRequestIntent.putExtra("linenum", questionLayout.getChildCount());
					qtRequestIntent.putExtra("wonum", activity.wonum);
					startActivityForResult(qtRequestIntent, QUEST_REQUEST_CODE);
				}
			});
		}
		showCurrentQtDatas();
	}
	
	private void initTableViewAndData() {
		if(historyMeasureDatas!=null&&historyMeasureDatas.size()>0){
			syncState = historyMeasureDatas.get(0).getSyncstate();
			if(syncState==1){
				contentView.findViewById(R.id.btn_other_quest).setClickable(false);
			}
		}
		int itemDataindex=0;
		for (int i = 0; i < mUiData.rowNum; i++) {
			TableRow rowView = (TableRow) View.inflate(activity,R.layout.sublayout_turnout_row, null);
			TextView rowTextView=(TextView) rowView.getChildAt(0);
			EditText cell1=(EditText) rowView.getChildAt(1);
			EditText cell2=(EditText) rowView.getChildAt(2);
			if(mUiData.rowNames!=null&&mUiData.rowNames.size()>0){
				rowTextView.setText(mUiData.rowNames.get(i));
			}else{
				rowTextView.setVisibility(View.GONE);
			}
			cell1.setTag(i+"_"+1);
			cell2.setTag(i+"_"+2);
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
			//设置是否可编辑
			if(mUiData.editMatrix!=null&&i<mUiData.editMatrix.size()){
				ArrayList<Boolean> edits = mUiData.editMatrix.get(i);
				setCellEditable(cell1,edits.get(0));
				setCellEditable(cell2,edits.get(1));
			}
			
			if(syncState==1){
				cell1.setFocusable(false);
				cell2.setFocusable(false);
			}
			tableLayout.addView(rowView);
		}
	}
	
	private void setCellEditable(EditText cell1, boolean editable) {
		if(!editable){
			cell1.setFocusable(false);
			cell1.setBackgroundColor(FalseEditableColor);
		}
	}

	private TextView createQtItemView(String text) {
		View view = View.inflate(getActivity(), R.layout.layout_quest_textview, null);
		TextView tv = (TextView) view.findViewById(R.id.tv);
		tv.setText(text);
		return tv;
	}
	
	public void setAllCellData(ArrayList<ArrayList<String>> datas){
		for (int i = 0; i < datas.size(); i++) {
			ArrayList<String> rowData = datas.get(i);
			TableRow tableRow=(TableRow) tableLayout.getChildAt(i);
			for (int j = 0; j < rowData.size(); j++) {
				EditText cell1=(EditText) tableRow.getChildAt(j+1);
				EditText cell2=(EditText) tableRow.getChildAt(j+2);
				cell1.setText(rowData.get(j));
				cell2.setText(rowData.get(j));
			}
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
					measureData.setWonum(activity.wonum);
					measureData.setAssetnum(activity.assetnum);
					measureData.setCp1nanumcfgrownum(mUiData.getRowNums().get(lineNum));
					measureData.setFlag_v("0");
					measureData.setStatus("0");
					measureData.setAssetattrid("ddd");
					measureData.setValue1(getEdtiTextNum(et));
					measureData.setGh(mUiData.partOrder+"");
					
					if("1".equals(rowNumStr)){
						measureData.setNametype("轨距");
					}else{
						measureData.setNametype("水平");
					}
					double[] gps = SpUtiles.BaseInfo.getGps();
					measureData.setXvalue(gps[0]+"");
					measureData.setYvalue(gps[1]+"");
					measureData.setInspdate(DateUtil.getCurrDateFormat(DateUtil.style_nyrsfm));
					
					measureData.setSyncstate(-1);
					
					activity.service.savePage4Data(measureData);
				}
				
			}else{
				et.setSelectAllOnFocus(true);
			}
		}
	};
	private LinearLayout questionLayout;
	private List<MeasureData> questionDatas;
	private int syncState=-2;
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
		if(syncState==-2||syncState==1){
			return;
		}
		activity.service.updateTurnoutMeasureDataState(activity.wonum, activity.assetnum, mUiData.partOrder);
	}
	
	private void saveQtData(Intent data) {
		MeasureData newMeasureData=createSaveQtData(data);
		boolean saveResult = activity.service.saveTurnoutQuestion(newMeasureData);
		if(saveResult){
			List<MeasureData> newMeasureDatas = activity.service.getTurnoutQuestions(newMeasureData);
			if(newMeasureDatas!=null&&newMeasureDatas.size()>0){
				Collections.sort(newMeasureDatas, new QuestionComparator());
				questionDatas=newMeasureDatas;
				showCurrentQtDatas();
			}
		}
	}
	
	private MeasureData createSaveQtData(Intent data) {
		MeasureData measureData=new MeasureData();
		measureData.setOrgid(GlobalApplication.orgid);
		measureData.setSiteid(GlobalApplication.siteid);
		measureData.setWonum(activity.wonum);
		measureData.setAssetnum(activity.assetnum);
		measureData.setCp1nanumcfgrownum(data.getStringExtra("cp1nanumcfgrownum"));
		measureData.setFlag_v("0");
		measureData.setStatus("-1");
		measureData.setLinenum(data.getIntExtra("linenum", 0));
		measureData.setAssetattrid("ddd");
		measureData.setValue1(data.getIntExtra("value1",0));
		measureData.setGh(mUiData.partOrder+"");
		measureData.setNametype(data.getStringExtra("nametype"));
		measureData.setDefectclass(data.getStringExtra("defectclass"));
		measureData.setFvalue(getDoubleValue(data.getStringExtra("fvalue")));
		measureData.setXvalue(data.getStringExtra("xvalue"));
		measureData.setYvalue(data.getStringExtra("yvalue"));
		measureData.setInspdate(DateUtil.getCurrDateFormat(DateUtil.style_nyrsfm));
		measureData.setSyncstate(-1);
		return measureData;
	}

	private double getDoubleValue(String doubleStr){
		double dvalue=0;
		if(!TextUtils.isEmpty(doubleStr)){
			try {
				dvalue = Double.parseDouble(doubleStr);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return dvalue;
	}
	
	private void showCurrentQtDatas() {
		questionLayout.removeAllViews();
		if(questionDatas!=null&&questionDatas.size()>0){
			for (int i = 0; i < questionDatas.size(); i++) {
				MeasureData qtData = questionDatas.get(i);
				TextView qtItemView = createQtItemView(qtData.getNametype());
				qtItemView.setTag(qtData);
				qtItemView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//点击问题条目时
						MeasureData qtData=(MeasureData) v.getTag();
						Intent requestQtIntent=new Intent(activity, QuestionTurnoutActivity.class);
						requestQtIntent.putExtra("uiData", JSON.toJSONString(mUiData));
						requestQtIntent.putExtra("isAdd", false);
						requestQtIntent.putExtra("linename", activity.description);//线名
						requestQtIntent.putExtra("gh", mUiData.partOrder);
						requestQtIntent.putExtra("value1", qtData.getValue1());
						requestQtIntent.putExtra("nametype", qtData.getNametype());
						requestQtIntent.putExtra("defectclass", qtData.getDefectclass());
						requestQtIntent.putExtra("fvalue", qtData.getFvalue()+"");
						requestQtIntent.putExtra("xvalue", qtData.getXvalue());
						requestQtIntent.putExtra("yvalue", qtData.getYvalue());
						requestQtIntent.putExtra("linenum", qtData.getLinenum());//行号
						requestQtIntent.putExtra("wonum", activity.wonum);
						requestQtIntent.putExtra("cp1nanumcfgrownum", qtData.getCp1nanumcfgrownum());
						startActivityForResult(requestQtIntent, QUEST_REQUEST_CODE);
					}
				});
				questionLayout.addView(qtItemView, params);
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==activity.RESULT_OK){
			if(requestCode==QUEST_REQUEST_CODE){
				//保存或更新返回的数据
				saveQtData(data);
				//
			}
		}
	}

	
}
