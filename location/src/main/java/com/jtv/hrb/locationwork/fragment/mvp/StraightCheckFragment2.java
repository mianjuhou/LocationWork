package com.jtv.hrb.locationwork.fragment.mvp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.ClipData.Item;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.LineCurveCheckActivity;
import com.jtv.hrb.locationwork.QuestionInputActivity;
import com.jtv.hrb.locationwork.R;
import com.jtv.hrb.locationwork.comparator.ConfigRowComparator;
import com.jtv.hrb.locationwork.comparator.HistoryComparator;
import com.jtv.hrb.locationwork.comparator.QuestionComparator;
import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.ConfigRowName;
import com.jtv.hrb.locationwork.domain.LineNumBean;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.hrb.locationwork.domain.QuestionBean;
import com.jtv.hrb.locationwork.domain.RailNumBean;
import com.jtv.hrb.locationwork.domain.StraightLineBean;
import com.jtv.hrb.locationwork.domain.TurnoutUiData;
import com.jtv.locationwork.activity.ClassSelectActivity;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.SpUtiles.NearWorkListInfo;
import com.tencent.mapsdk.a.s;
import com.yixia.weibo.sdk.util.ToastUtils;

@SuppressLint("InflateParams")
public class StraightCheckFragment2 extends Fragment {
	private static String LOG_TAG = "LineCheckFragment2";
	private View contentView;
	private LinearLayout questionLayout;
	private int typeColorSelect = Color.rgb(0xFF, 0xA6, 0xA6);// #FFA6A6
	private int typeColorUnSelect = Color.rgb(0xC1, 0xC1, 0xC1);// #C1C1C1
	private StraightLineBean straightLineBean;
	private List<ConfigRowName> straightLineConfig;
	
	private StraightCheckPresenter scPresenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.activity_line_check, null);
		findView();
		scPresenter=new StraightCheckPresenter((LineCurveCheckActivity)getActivity(),scView);
		scPresenter.initPageState();
		initViewState();
		return contentView;
	}
	

	private void findView() {
		etStartKM = getEditText(R.id.et_start_km);
		etStartM = getEditText(R.id.et_start_m);
		etEndKM = getEditText(R.id.et_end_km);
		etEndM = getEditText(R.id.et_end_m);
		etGh = getEditText(R.id.et_rail_num);
		questionLayout = (LinearLayout) contentView.findViewById(R.id.ll_questions);
		tableLayout = (TableLayout) contentView.findViewById(R.id.table_layout);
		btnType4 = getButton(R.id.btn_measuretype_4);
		btnType8 = getButton(R.id.btn_measuretype_8);
		tvLineName = getTextView(R.id.tv_linename);
		btn_minus_km = getButton(R.id.btn_minus_km);
		btn_plus_km = getButton(R.id.btn_plus_km);
		btn_previous_rail = getButton(R.id.btn_previous_rail);
		btn_next_rail = getButton(R.id.btn_next_rail);
		btn_other_quest = getButton(R.id.btn_other_quest);
	}
	private void initViewState() {
		btn_minus_km.setOnClickListener(lcGhListener);
		btn_plus_km.setOnClickListener(lcGhListener);
		btn_previous_rail.setOnClickListener(lcGhListener);
		btn_next_rail.setOnClickListener(lcGhListener);
	}
	
	private IStraightCheckView scView=new IStraightCheckView(){
		@Override
		public String[] getStartLc() {
			String[] nums=new String[2];
			nums[0]=getEditTextValue(etStartKM);
			nums[1]=getEditTextValue(etStartM);
			return nums;
		}
		
		@Override
		public String[] getEndLc() {
			String[] nums=new String[2];
			nums[0]=getEditTextValue(etEndKM);
			nums[1]=getEditTextValue(etEndM);
			return nums;
		}

		@Override
		public void setStartLc(String startKm, String startM) {
			etStartKM.setText(startKm);
			etStartM.setText(startM);
		}

		@Override
		public void setEndLc(String endKm, String endM) {
			etEndKM.setText(endKm);
			etEndM.setText(endM);
		}

		@Override
		public String getGh() {
			return getEditTextValue(etGh);
		}

		@Override
		public void setGh(String gh) {
			etGh.setText(gh);
		}

		@Override
		public boolean getMeasureType() {
			return measureType;
		}

		@Override
		public void setMeasureType(boolean type) {
			if(type){
				measureType = true;
				initTypeButtonColor();
				initTableLayoutType();
			}else{
				measureType = false;
				initTypeButtonColor();
				initTableLayoutType();
			}
		}
		
		@Override
		public void showQuestions(List<MeasureData> questions) {
			if (questions != null && questions.size() > 0) {
				questionLayout.removeAllViews();
				for (MeasureData question : questions) {
					String questDesc = question.getNametype();
					TextView qtTv = getQuestTextView(questDesc);
					qtTv.setTag(question);
					qtTv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							MeasureData question = (MeasureData) v.getTag();
							Intent requestIntent = new Intent(getActivity(), QuestionInputActivity.class);
							requestIntent.putExtra("questsrcjson", JSON.toJSONString(question));
							requestIntent.putExtra("linename", activity.lineDescription);
							startActivityForResult(requestIntent, LineCurveCheckActivity.QUEST_REQUEST_CODE);
						}
					});
					questionLayout.addView(qtTv, params);
				}
			} else {
				questionLayout.removeAllViews();
			}
		}

		@Override
		public void showTableData(List<MeasureData> historyMeasureData) {
			int tableRowCount = tableLayout.getChildCount();
			int index = 0;
			syncState = historyMeasureData.get(0).getSyncstate();
			if(syncState==1){
				getButton(R.id.btn_other_quest).setClickable(false);
			}
			for (int i = 0; i < tableRowCount; i++) {
				TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
				if (tableRow.getVisibility() == View.VISIBLE) {
					EditText cell1 = (EditText) tableRow.getChildAt(1);
					if (index < historyMeasureData.size()) {
						String uiTag = (String) cell1.getTag();
						MeasureData measureData = historyMeasureData.get(index);
						String dataTag = measureData.getTag();
						if (uiTag.equals(dataTag)) {
							cell1.setText(((int) historyMeasureData.get(index).getValue1()) + "");
							index++;
						} else {
							cell1.setText("");
						}
					} else {
						cell1.setText("");
					}
					EditText cell2 = (EditText) tableRow.getChildAt(2);
					if (index < historyMeasureData.size()) {
						String uiTag = (String) cell2.getTag();
						MeasureData measureData = historyMeasureData.get(index);
						String dataTag = measureData.getTag();
						if (uiTag.equals(dataTag)) {
							cell2.setText((int) measureData.getValue1() + "");
							index++;
						} else {
							cell2.setText("");
						}
					} else {
						cell2.setText("");
					}
					
					if(syncState==1){
						cell1.setFocusable(false);
						cell2.setFocusable(false);
					}
				}
			}
		}

		@Override
		public void clearTableData() {
			int viewIndex = 0;
			int tableRowCount = tableLayout.getChildCount();
			while (viewIndex < tableRowCount) {
				TableRow tableRow = (TableRow) tableLayout.getChildAt(viewIndex);
				EditText et1 = (EditText) tableRow.getChildAt(1);
				EditText et2 = (EditText) tableRow.getChildAt(2);
				et1.setText("");
				et2.setText("");
				viewIndex++;
			}
		}

		@Override
		public void clearQuestions() {
			questionLayout.removeAllViews();
		}
	};
	
	private String getEditTextValue(EditText et){
		return et.getText().toString().trim();
	}
	
	private OnClickListener lcGhListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_minus_km:
					scPresenter.setPreviousLc();
					break;
				case R.id.btn_plus_km:
					scPresenter.setNextLc();
					break;
				case R.id.btn_previous_rail:
					scPresenter.setPreviousGh();
					break;
				case R.id.btn_next_rail:
					scPresenter.setNextGh();
					break;
				default:
					break;
			}
		}
	};

	private void initContentView(View contentView) {
		btn_other_quest.setOnClickListener(questClickListener);
		btn_minus_km.setOnClickListener(minusPlusClickListener);
		btn_plus_km.setOnClickListener(minusPlusClickListener);
		btn_previous_rail.setOnClickListener(minusPlusClickListener);
		btn_next_rail.setOnClickListener(minusPlusClickListener);
		
		btnType4.setOnClickListener(measureTypeClickListener);
		btnType8.setOnClickListener(measureTypeClickListener);
		initTableLayout();
	}

	private boolean measureType = false;// false 8次测量 true 4次测量

	private void initTableLayout() {
		initTypeButtonColor();
		inintTableLayoutNames();
		initTableLayoutType();
	}

	private void initTypeButtonColor() {
		if (measureType) {
			btnType4.setBackgroundColor(typeColorSelect);
			btnType8.setBackgroundColor(typeColorUnSelect);
		} else {
			btnType4.setBackgroundColor(typeColorUnSelect);
			btnType8.setBackgroundColor(typeColorSelect);
		}
	}

	private void inintTableLayoutNames() {
		int tableRowCount = tableLayout.getChildCount();
		for (int i = 0; i < tableRowCount; i++) {
			TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
			TextView tvPartName = (TextView) tableRow.getChildAt(0);
			tvPartName.setText(straightLineConfig.get(i).getDESCRIPTION());
		}
	}

	private void initTableLayoutType() {
		int tableRowCount = tableLayout.getChildCount();
		for (int i = 0; i < tableRowCount; i++) {
			TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
			EditText cell1 = (EditText) tableRow.getChildAt(1);
			EditText cell2 = (EditText) tableRow.getChildAt(2);
			cell1.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					EditText et = (EditText) v;
					String tag = (String) et.getTag();
					if (!hasFocus) {
						String strValue = et.getText().toString().trim();
						scPresenter.saveMeasureData(strValue,tag);
					} else {
						et.setSelectAllOnFocus(true);
					}
				}
			});
			cell2.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					EditText et = (EditText) v;
					if (!hasFocus) {
						String strValue = et.getText().toString().trim();
						String tag = (String) v.getTag();
						scPresenter.saveMeasureData(strValue, tag);
					} else {
						et.setSelectAllOnFocus(true);
					}
				}
			});

			if (i % 2 == 1) {
				if (measureType) {
					tableRow.setVisibility(View.GONE);
				} else {
					tableRow.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void initState() {
		etStartKM.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					etEndKM.setText(getNextNumStr(getEdtiTextNum(etStartKM)));
				}
			}
		});
		etStartM.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				etEndM.setText(s.toString());
			}
		});
		tvLineName.setText(activity.lineDescription);
//		etStartKM.setText(getKM(startkm) + "");
//		etStartM.setText(getM(startkm) + "");
		etGh.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				//如果是以上传数据则不能进行更新
				if(syncState==-2||syncState==1){
					return;
				}
//				double startScope = getLcScope(etStartKM, etStartM);
//				double endScope = getLcScope(etEndKM, etEndM);
//				int gh = getEdtiTextNum(etGh);
//				service.updateRailMeasureDataState(startScope, endScope, gh);
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private OnClickListener measureTypeClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int resId = v.getId();
			if (resId == R.id.btn_measuretype_4) {
				if (!measureType) {
					scView.setMeasureType(true);
				}
			} else {
				if (measureType) {
					scView.setMeasureType(false);
				}
			}
		}
	};

	private OnClickListener questClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent requestIntent = new Intent(getActivity(), QuestionInputActivity.class);
			requestIntent.putExtra("linename", activity.lineDescription);
			requestIntent.putExtra("startkm", getEdtiTextNum2(etStartKM));
			requestIntent.putExtra("startm", getEdtiTextNum2(etStartM));
			requestIntent.putExtra("endkm", getEdtiTextNum2(etEndKM));
			requestIntent.putExtra("endm", getEdtiTextNum2(etEndM));
			requestIntent.putExtra("railnum", getEdtiTextNum(etGh));
			requestIntent.putExtra("assetnum", assetnum);
			requestIntent.putExtra("wonum", wonum);
			requestIntent.putExtra("linenum", questionLayout.getChildCount());
			startActivityForResult(requestIntent, LineCurveCheckActivity.QUEST_REQUEST_CODE);
		}
	};

	private OnClickListener minusPlusClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			saveFocusEditTextData();//
			resetFocusLocation();
			int resId = v.getId();
			if (resId == R.id.btn_minus_km) {
				scPresenter.setPreviousLc();
			} else if (resId == R.id.btn_plus_km) {
				scPresenter.setNextLc();
			} else if (resId == R.id.btn_previous_rail) {
				scPresenter.setPreviousGh();
			} else {
				scPresenter.setNextGh();
			}
		}
	};

	private void showHistoryPageData() {
		int tableRowCount = tableLayout.getChildCount();
		int index = 0;
		syncState = historyMeasureData.get(0).getSyncstate();
		if(syncState==1){
			getButton(R.id.btn_other_quest).setClickable(false);
		}
		for (int i = 0; i < tableRowCount; i++) {
			TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
			if (tableRow.getVisibility() == View.VISIBLE) {
				EditText cell1 = (EditText) tableRow.getChildAt(1);
				if (index < historyMeasureData.size()) {
					String uiTag = (String) cell1.getTag();
					MeasureData measureData = historyMeasureData.get(index);
					String dataTag = measureData.getTag();
					if (uiTag.equals(dataTag)) {
						cell1.setText(((int) historyMeasureData.get(index).getValue1()) + "");
						index++;
					} else {
						cell1.setText("");
					}
				} else {
					cell1.setText("");
				}
				EditText cell2 = (EditText) tableRow.getChildAt(2);
				if (index < historyMeasureData.size()) {
					String uiTag = (String) cell2.getTag();
					MeasureData measureData = historyMeasureData.get(index);
					String dataTag = measureData.getTag();
					if (uiTag.equals(dataTag)) {
						cell2.setText((int) measureData.getValue1() + "");
						index++;
					} else {
						cell2.setText("");
					}
				} else {
					cell2.setText("");
				}
				
				if(syncState==1){
					cell1.setFocusable(false);
					cell2.setFocusable(false);
				}
			}
		}
	}

	protected void resetFocusLocation() {
		TableRow tableRow = (TableRow) tableLayout.getChildAt(0);
		EditText et0_0 = (EditText) tableRow.getChildAt(1);
		if(et0_0.isFocusable()){
			et0_0.setFocusable(true);
			et0_0.setFocusableInTouchMode(true);
			et0_0.requestFocus();
			et0_0.findFocus();
		}
	}

	private int getEdtiTextNum2(EditText editText) {
		String railNumStr = editText.getText().toString().trim();
		if (!TextUtils.isEmpty(railNumStr)) {
			int railNum = Integer.parseInt(railNumStr);
			return railNum;
		} else {
			return 0;
		}
	}

	private int getEdtiTextNum(EditText editText) {
		String railNumStr = editText.getText().toString().trim();
		if (!TextUtils.isEmpty(railNumStr)) {
			int railNum = Integer.parseInt(railNumStr);
			if (railNum < 0) {
				return -1;
			}
			return railNum;
		} else {
			return -2;
		}
	}

	private String getNextNumStr(int number) {
		if (number >= 0) {
			return (number + 1) + "";
		} else {
			return "0";
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == LineCurveCheckActivity.QUEST_REQUEST_CODE) {
				String qtJson = data.getStringExtra("josn");
				scPresenter.addAndUpdateQt(qtJson);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Button getButton(int resId) {
		Button btn = (Button) contentView.findViewById(resId);
		return btn;
	}

	private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(//
			LinearLayout.LayoutParams.MATCH_PARENT,//
			LinearLayout.LayoutParams.WRAP_CONTENT);
	private EditText etStartKM;
	private EditText etStartM;
	private EditText etEndKM;
	private EditText etEndM;
	private EditText etGh;
	private TableLayout tableLayout;
	private Button btnType4;
	private Button btnType8;
	private TextView tvLineName;
	private LineCurveCheckActivity activity;
	private String assetnum;
	private String wonum;
	private String startkm;
	private String endkm;
	private String siteid;
	private String orgid;
	private ArrayList<MeasureData> historyMeasureData;
	private List<MeasureData> questions;
	private int syncState=-2;
	private Button btn_minus_km;
	private Button btn_plus_km;
	private Button btn_previous_rail;
	private Button btn_next_rail;
	private Button btn_other_quest;

	private EditText getEditText(int resId) {
		EditText et = (EditText) contentView.findViewById(resId);
		return et;
	}

	private TextView getTextView(int resId) {
		TextView tv = (TextView) contentView.findViewById(resId);
		return tv;
	}

	private TextView getQuestTextView(String text) {
		View view = View.inflate(getActivity(), R.layout.layout_quest_textview, null);
		TextView tv = (TextView) view.findViewById(R.id.tv);
		tv.setText(text);
		return tv;
	}

	/**
	 * 保存当前光标所在单元格中的数据
	 */
	private void saveFocusEditTextData() {
		// 获取焦点所出控件，如果在表格范围内则存储
		EditText etFocus = (EditText) activity.getWindow().getDecorView().findFocus();
		if (etFocus != null) {
			String tag = (String) etFocus.getTag();
			if ((!TextUtils.isEmpty(tag)) && tag.contains("_")) {
				String svalue=etFocus.getText().toString().trim();
				if (!TextUtils.isEmpty(svalue)) {
					scPresenter.saveMeasureData(svalue, tag);
				}
			}
		}
	}
}
