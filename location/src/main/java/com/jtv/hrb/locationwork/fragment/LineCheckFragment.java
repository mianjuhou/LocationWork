package com.jtv.hrb.locationwork.fragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.tencent.mapsdk.a.ac;
import com.yixia.weibo.sdk.util.ToastUtils;

@SuppressLint("InflateParams")
public class LineCheckFragment extends Fragment {
	private static String LOG_TAG = "LineCheckFragment";
	private View contentView;
	private LinearLayout questionLayout;
	private int typeColorSelect = Color.rgb(0xFF, 0xA6, 0xA6);// #FFA6A6
	private int typeColorUnSelect = Color.rgb(0xC1, 0xC1, 0xC1);// #C1C1C1

	private StraightLineBean straightLineBean;
	private StaticCheckDbService service = new StaticCheckDbService(getActivity());
	private String timestamp = System.currentTimeMillis() + "";

	private List<ConfigRowName> straightLineConfig;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = (LineCurveCheckActivity) getActivity();
		assetnum = activity.assetnum;
		wonum = activity.wonum;
		startkm = activity.startmeasure;
		endkm = activity.endmeasure;
		siteid = GlobalApplication.siteid;
		orgid = GlobalApplication.orgid;
		// 从数据库中获取本工单，本作业范围内的历史数据
		// 如果获取的数据为空则新建一个空的Bean
		historyMeasureData = service.getLastMeasureData(wonum, assetnum);
		if (historyMeasureData != null && historyMeasureData.size() > 0) {
			// 查询录入问题
			questions = service.getQuestions(historyMeasureData.get(0));
			if (questions != null && questions.size() > 0) {
				Collections.sort(questions, new QuestionComparator());
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.activity_line_check, null);
		initConfigData();
		return contentView;
	}
	
	private void initConfigData() {
		activity.once=true;
		TrackAPI.queryPersonCheckWoInfoConfig(getActivity(), new ObserverCallBack() {
			@Override
			public void badBack(String error, int method, Object obj) {
				getDefaultConfigDatas();
				afterConfigData();
			}

			@Override
			public void back(String data, int method, Object obj) {
				if (method == MethodApi.HTTP_QUERY_CHECK_CONFIG) {
					try {
						getNetworkConfigDatas(data);
					} catch (Exception e) {
						e.printStackTrace();
						getDefaultConfigDatas();
					}
				} else {
					getDefaultConfigDatas();
				}
				afterConfigData();
			}

			
		}, GlobalApplication.siteid);

	}
	
	private void getNetworkConfigDatas(String data) {
		List<ConfigRowName> configRowData = JSON.parseArray(data, ConfigRowName.class);
		straightLineConfig = getRowConfig("", configRowData);
	}

	private List<ConfigRowName> getRowConfig(String partType, List<ConfigRowName> configRowData) {
		List<ConfigRowName> rows = new ArrayList<ConfigRowName>();
		for (ConfigRowName row : configRowData) {
			if (partType.equals(row.getPARTTYPE())) {
				rows.add(row);
			}
		}
		Collections.sort(rows, new ConfigRowComparator());
		return rows;
	}
	protected void getDefaultConfigDatas() {
		String json="[{\"LINENUM\":1,\"HASLD\":0,\"DESCRIPTION\":\"接头\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1001\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":1,\"ROWSTAMP\":\"698858400\"},{\"LINENUM\":3,\"HASLD\":0,\"DESCRIPTION\":\"中间\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1003\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":3,\"ROWSTAMP\":\"698858402\"},{\"LINENUM\":2,\"HASLD\":0,\"DESCRIPTION\":\"\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1002\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":2,\"ROWSTAMP\":\"698858401\"},{\"LINENUM\":4,\"HASLD\":0,\"DESCRIPTION\":\"\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1004\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":4,\"ROWSTAMP\":\"698858403\"},{\"LINENUM\":5,\"HASLD\":0,\"DESCRIPTION\":\"接头\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1005\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":5,\"ROWSTAMP\":\"698858413\"},{\"LINENUM\":6,\"HASLD\":0,\"DESCRIPTION\":\"\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1006\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":6,\"ROWSTAMP\":\"698858414\"},{\"LINENUM\":7,\"HASLD\":0,\"DESCRIPTION\":\"中间\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1007\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":7,\"ROWSTAMP\":\"698858415\"},{\"LINENUM\":8,\"HASLD\":0,\"DESCRIPTION\":\"\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1008\",\"CP1NANUMCFGNUM\":\"1001\",\"PARTTYPE\":\"\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":8,\"ROWSTAMP\":\"698858416\"},{\"LINENUM\":1,\"HASLD\":0,\"DESCRIPTION\":\"基本轨接头\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1009\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":9,\"ROWSTAMP\":\"698858417\"},{\"LINENUM\":2,\"HASLD\":0,\"DESCRIPTION\":\"尖轨尖端\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1010\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":10,\"ROWSTAMP\":\"698858418\"},{\"LINENUM\":3,\"HASLD\":0,\"DESCRIPTION\":\"尖轨中\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1011\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":11,\"ROWSTAMP\":\"698858419\"},{\"LINENUM\":4,\"HASLD\":0,\"DESCRIPTION\":\"尖轨跟端（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1012\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":12,\"ROWSTAMP\":\"698858420\"},{\"LINENUM\":5,\"HASLD\":0,\"DESCRIPTION\":\"尖轨跟端（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1013\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"转辙部位\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":13,\"ROWSTAMP\":\"698858421\"},{\"LINENUM\":6,\"HASLD\":0,\"DESCRIPTION\":\"直线（前）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1014\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":14,\"ROWSTAMP\":\"698858422\"},{\"LINENUM\":7,\"HASLD\":0,\"DESCRIPTION\":\"直线（中）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1015\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":15,\"ROWSTAMP\":\"698858423\"},{\"LINENUM\":8,\"HASLD\":0,\"DESCRIPTION\":\"直线（后）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1016\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":16,\"ROWSTAMP\":\"698858424\"},{\"LINENUM\":9,\"HASLD\":0,\"DESCRIPTION\":\"导曲线(前)\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1017\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":17,\"ROWSTAMP\":\"698858425\"},{\"LINENUM\":10,\"HASLD\":0,\"DESCRIPTION\":\"导曲线(中)\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1018\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":18,\"ROWSTAMP\":\"698858426\"},{\"LINENUM\":11,\"HASLD\":0,\"DESCRIPTION\":\"导曲线(后)\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1019\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"导曲线部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":19,\"ROWSTAMP\":\"698858427\"},{\"LINENUM\":12,\"HASLD\":0,\"DESCRIPTION\":\"叉心前（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1020\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":20,\"ROWSTAMP\":\"698858428\"},{\"LINENUM\":13,\"HASLD\":0,\"DESCRIPTION\":\"叉心前（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1021\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":21,\"ROWSTAMP\":\"698858429\"},{\"LINENUM\":14,\"HASLD\":0,\"DESCRIPTION\":\"叉心中（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1022\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":22,\"ROWSTAMP\":\"698858430\"},{\"LINENUM\":15,\"HASLD\":0,\"DESCRIPTION\":\"叉心中（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1023\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":23,\"ROWSTAMP\":\"698858431\"},{\"LINENUM\":16,\"HASLD\":0,\"DESCRIPTION\":\"叉心后（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1024\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":24,\"ROWSTAMP\":\"698858432\"},{\"LINENUM\":17,\"HASLD\":0,\"DESCRIPTION\":\"叉心后（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1025\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":25,\"ROWSTAMP\":\"698858433\"},{\"LINENUM\":18,\"HASLD\":0,\"DESCRIPTION\":\"查照间隔（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1026\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":26,\"ROWSTAMP\":\"698858434\"},{\"LINENUM\":19,\"HASLD\":0,\"DESCRIPTION\":\"查照间隔（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1027\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":27,\"ROWSTAMP\":\"698858435\"},{\"LINENUM\":20,\"HASLD\":0,\"DESCRIPTION\":\"护背距离（直）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1028\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":28,\"ROWSTAMP\":\"698858436\"},{\"LINENUM\":21,\"HASLD\":0,\"DESCRIPTION\":\"护背距离（曲）\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1029\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"辙叉部分\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":29,\"ROWSTAMP\":\"698858437\"},{\"LINENUM\":22,\"HASLD\":0,\"DESCRIPTION\":\"支距\",\"SITEID\":\"S05118\",\"CP1NANUMCFGROWNUM\":\"1030\",\"CP1NANUMCFGNUM\":\"1002\",\"PARTTYPE\":\"支距\",\"ORGID\":\"ORG2\",\"CP1NANUMCFGROWID\":30,\"ROWSTAMP\":\"698858438\"}]";
		getNetworkConfigDatas(json);
	}

	private void afterConfigData() {
		initContentView(contentView);
		showHistoryDataInPage();
		showQuestions();
		activity.dismissLoadingPop();
	}

	private void showQuestions() {
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

	private void showHistoryDataInPage() {
		if (historyMeasureData != null && historyMeasureData.size() > 0) {
			// 根据行号进行排序
			sortHistoryData(historyMeasureData);
			// 显示作业范围公里数
			MeasureData measureData = historyMeasureData.get(0);
			etStartKM.setText(getKM(measureData.getStartmeasure() + "") + "");
			etStartM.setText(getM(measureData.getStartmeasure() + "") + "");
			etEndKM.setText(getKM(measureData.getEndmeasure() + "") + "");
			etEndM.setText(getM(measureData.getEndmeasure() + "") + "");
			// 显示轨号
			etRailNum.setText(measureData.getGh());
			// 显示
			if (isMeasureType4(historyMeasureData)) {
				measureType = true;
				initTypeButtonColor();
				initTableLayoutType();
			} else {
				measureType = false;
				initTypeButtonColor();
				initTableLayoutType();
			}
			// 先获取本轨号的对应的数据，显示在单元格中
			showHistoryPageData();
		}
	}

	/**
	 * 对获取到的历史数据进行排序
	 * 
	 * @param historyMeasureData2
	 */
	private void sortHistoryData(ArrayList<MeasureData> historyMeasureData) {
		Collections.sort(historyMeasureData, new HistoryComparator());
	}

	private boolean isMeasureType4(ArrayList<MeasureData> historyMeasureData) {
		MeasureData measureData = historyMeasureData.get(historyMeasureData.size() - 1);
		if (measureData.getMeasuretype() == 4) {
			return true;
		} else {
			return false;
		}
	}

	private int getKM(String dkm) {
		BigDecimal bigDecimal = new BigDecimal(dkm);
		int km = bigDecimal.intValue();
		return km;
	}

	private int getM(String dkm) {
		BigDecimal bigDecimal = new BigDecimal(dkm);
		int km = bigDecimal.intValue();
		BigDecimal multiply = bigDecimal.subtract(new BigDecimal(km)).multiply(new BigDecimal(1000));
		return multiply.intValue();
	}

	private void initContentView(View contentView) {
		getButton(R.id.btn_other_quest).setOnClickListener(questClickListener);
		questionLayout = (LinearLayout) contentView.findViewById(R.id.ll_questions);
		etStartKM = getEditText(R.id.et_start_km);
		etStartM = getEditText(R.id.et_start_m);
		etEndKM = getEditText(R.id.et_end_km);
		etEndM = getEditText(R.id.et_end_m);
		etRailNum = getEditText(R.id.et_rail_num);
		getButton(R.id.btn_minus_km).setOnClickListener(minusPlusClickListener);
		getButton(R.id.btn_plus_km).setOnClickListener(minusPlusClickListener);
		getButton(R.id.btn_previous_rail).setOnClickListener(minusPlusClickListener);
		getButton(R.id.btn_next_rail).setOnClickListener(minusPlusClickListener);
		tableLayout = (TableLayout) contentView.findViewById(R.id.table_layout);
		btnType4 = getButton(R.id.btn_measuretype_4);
		btnType8 = getButton(R.id.btn_measuretype_8);
		btnType4.setOnClickListener(measureTypeClickListener);
		btnType8.setOnClickListener(measureTypeClickListener);
		tvLineName = getTextView(R.id.tv_linename);
		initTableLayout();
		initState();
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
						MeasureData measureData = new MeasureData();
						if (TextUtils.isEmpty(strValue)) {
							// measureData.setValue1(0);
							return;
						} else {
							double value = Double.parseDouble(strValue);
							measureData.setValue1(value);
						}
						double[] gps = SpUtiles.BaseInfo.getGps();
						measureData.setXvalue(gps[0] + "");
						measureData.setYvalue(gps[1] + "");
						measureData.setInspdate(DateUtil.getCurrDateFormat(DateUtil.style_nyrsfm));

						String[] splits = tag.split("_");
						String lineNumStr = splits[splits.length - 2];
						int lineNum = Integer.parseInt(lineNumStr);
						measureData.setLinenum(lineNum);
						measureData.setGh(getEdtiTextNum(etRailNum) + "");
						measureData.setStatus("0");
						measureData.setFlag_v("2");
						measureData.setNametype("轨距");
						measureData.setHasld(0);
						measureData.setOrgid(orgid);
						measureData.setSiteid(siteid);
						measureData.setAssetnum(assetnum);
						measureData.setWonum(wonum);
						measureData.setCp1nanumcfgrownum(straightLineConfig.get(lineNum - 1).getCP1NANUMCFGROWNUM());
						double startScope = getLcScope(etStartKM, etStartM);
						double endScope = getLcScope(etEndKM, etEndM);
						measureData.setStartmeasure(startScope);
						measureData.setEndmeasure(endScope);
						measureData.setTag(tag);
						if (measureType) {
							measureData.setMeasuretype(4);
						} else {
							measureData.setMeasuretype(8);
						}
						service.saveMeasureData(measureData, tag, startScope, endScope, getEdtiTextNum(etRailNum));
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
						MeasureData measureData = new MeasureData();
						if (TextUtils.isEmpty(strValue)) {
							// measureData.setValue1(0);
							return;
						} else {
							double value = Double.parseDouble(strValue);
							measureData.setValue1(value);
						}
						double[] gps = SpUtiles.BaseInfo.getGps();
						measureData.setXvalue(gps[0] + "");
						measureData.setYvalue(gps[1] + "");
						measureData.setInspdate(DateUtil.getCurrDateFormat(DateUtil.style_nyrsfm));

						String[] splits = tag.split("_");
						String lineNumStr = splits[splits.length - 2];
						int lineNum = Integer.parseInt(lineNumStr);
						measureData.setLinenum(lineNum);
						measureData.setGh(getEdtiTextNum(etRailNum) + "");
						measureData.setStatus("0");
						measureData.setFlag_v("2");
						measureData.setNametype("水平");
						measureData.setHasld(0);
						measureData.setOrgid(orgid);
						measureData.setSiteid(siteid);
						measureData.setAssetnum(assetnum);
						measureData.setWonum(wonum);
						measureData.setCp1nanumcfgrownum(straightLineConfig.get(lineNum - 1).getCP1NANUMCFGROWNUM());
						double startScope = getLcScope(etStartKM, etStartM);
						double endScope = getLcScope(etEndKM, etEndM);
						measureData.setStartmeasure(startScope);
						measureData.setEndmeasure(endScope);
						measureData.setTag(tag);
						if (measureType) {
							measureData.setMeasuretype(4);
						} else {
							measureData.setMeasuretype(8);
						}
						service.saveMeasureData(measureData, tag, startScope, endScope, getEdtiTextNum(etRailNum));
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
		
		etEndKM.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					String startKm=etStartKM.getText().toString().trim();
					String endKm=etEndKM.getText().toString().trim();
					int startKmInt=Integer.parseInt(startKm);
					int endKmInt=Integer.parseInt(endKm);
					if(endKmInt-startKmInt>=2){
						//弹出提示并把值改为间隔1
						ToastUtils.showToast(activity, "里程范围不能大于1公里");
						etEndKM.setText((startKmInt+1)+"");
					}
				}
			}
		});
		etEndM.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					double start=getLcScope(etStartKM, etStartM);
					double end=getLcScope(etEndKM, etEndM);
					if(end-start>1){
						ToastUtils.showToast(activity, "里程范围不能大于1公里");
						String startKm=etStartKM.getText().toString().trim();
						int startKmInt=Integer.parseInt(startKm);
						etEndKM.setText((startKmInt+1)+"");
						etEndM.setText(etStartM.getText().toString());
					}
				}
			}
		});
		
		tvLineName.setText(activity.lineDescription);

		etStartKM.setText(getKM(startkm) + "");
		etStartM.setText(getM(startkm) + "");

		etRailNum.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				//如果是以上传数据则不能进行更新
				if(syncState==-2||syncState==1){
					return;
				}
				double startScope = getLcScope(etStartKM, etStartM);
				double endScope = getLcScope(etEndKM, etEndM);
				int gh = getEdtiTextNum(etRailNum);
				service.updateRailMeasureDataState(startScope, endScope, gh);
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
					measureType = true;
					initTypeButtonColor();
					initTableLayoutType();
				}
			} else {
				if (measureType) {
					measureType = false;
					initTypeButtonColor();
					initTableLayoutType();
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
			requestIntent.putExtra("railnum", getEdtiTextNum(etRailNum));
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
				int oldStartKM = getEdtiTextNum(etStartKM);
				String startKMStr = getPreviousNumStr(oldStartKM);
				String endKMStr = oldStartKM + "";
				etStartKM.setText(startKMStr);
				etEndKM.setText(endKMStr);
				// 根据测量范围查询出此范围中所有的数据
				historyMeasureData = service.getMeasureDataByRailNum(wonum, assetnum, "1", getLcScope(etStartKM, etStartM),
						getLcScope(etEndKM, etEndM));
				if (historyMeasureData != null && historyMeasureData.size() > 0) {
					// 如果数据存在
					showHistoryDataInPage();
				} else {
					// 如果数据不存在
					// 设置轨号为1
					etRailNum.setText("1");
					// 清空单元格数据
					clearGridData();
					// clearQuestData();
				}
				showCurrenrQuestData();
			} else if (resId == R.id.btn_plus_km) {
				int oldStartKM = getEdtiTextNum(etStartKM);
				etStartKM.setText((oldStartKM + 1) + "");
				etEndKM.setText((oldStartKM + 2) + "");
				// 根据测量范围查询出此范围中所有的数据
				historyMeasureData = service.getMeasureDataByRailNum(wonum, assetnum, "1", getLcScope(etStartKM, etStartM),
						getLcScope(etEndKM, etEndM));
				if (historyMeasureData != null && historyMeasureData.size() > 0) {
					// 如果数据存在
					showHistoryDataInPage();
				} else {
					// 如果数据不存在
					// 设置轨号为1
					etRailNum.setText("1");
					// 清空单元格数据
					clearGridData();
					// clearQuestData();
				}
				showCurrenrQuestData();
			} else if (resId == R.id.btn_previous_rail) {
				// 保存当前页数据
				// 轨号减1
				String railNumStr = getPreviousNumStr(getEdtiTextNum(etRailNum));
				etRailNum.setText(railNumStr);
				// 先获取轨号更改后对应的页面数据
				showCurrentPageData();
				showCurrenrQuestData();
			} else {
				// 先保存上一页的数据
				// 轨号加1
				String railNumStr = getNextNumStr(getEdtiTextNum(etRailNum));
				etRailNum.setText(railNumStr);
				// 再获取本业的历史数据
				// 如果有则回显
				// 如果没有则不处理
				showCurrentPageData();
				showCurrenrQuestData();
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

	protected void showCurrenrQuestData() {
		if (historyMeasureData != null && historyMeasureData.size() > 0) {
			MeasureData newMeasureData = historyMeasureData.get(0);
			List<MeasureData> newQuestions = service.getQuestions(newMeasureData);
			if (newQuestions != null && newQuestions.size() > 0) {
				questions = newQuestions;
				Collections.sort(questions, new QuestionComparator());
			} else {
				questions = null;
			}
			showQuestions();
		} else {
			questions = null;
			showQuestions();
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

	protected void showCurrentPageData() {
		String gh = getEdtiTextNum(etRailNum) + "";
		double startScope = getLcScope(etStartKM, etStartM);
		double endScope = getLcScope(etEndKM, etEndM);
		historyMeasureData = service.getMeasureDataByRailNum(wonum, assetnum, gh, startScope, endScope);
		if (historyMeasureData != null && historyMeasureData.size() > 0) {
			showHistoryDataInPage();
		} else {
			clearGridData();
		}
	}

	private void clearQuestData() {
		questionLayout.removeAllViews();
	}

	private void clearGridData() {
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

	private void getGridData(RailNumBean railNumBean) {
		List<LineNumBean> lineNumBeans = railNumBean.getLineNumBeans();
		int viewIndex = 0;
		int dataIndex = 0;
		int tableRowCount = tableLayout.getChildCount();
		while (viewIndex < tableRowCount) {
			TableRow tableRow = (TableRow) tableLayout.getChildAt(viewIndex);
			if (tableRow.getVisibility() == View.VISIBLE) {
				EditText et1 = (EditText) tableRow.getChildAt(1);
				EditText et2 = (EditText) tableRow.getChildAt(2);
				if (dataIndex < lineNumBeans.size()) {
					LineNumBean lineNumBean = lineNumBeans.get(dataIndex);
					et1.setText(lineNumBean.getValue1() + "");
					et2.setText(lineNumBean.getValue2() + "");
				} else {
					et1.setText("");
					et2.setText("");
				}
				dataIndex++;
			}
			viewIndex++;
		}
	}

	private void getQuestData(RailNumBean railNumBean) {
		List<QuestionBean> questionBeans = railNumBean.getQuestionBeans();
		questionLayout.removeAllViews();
		for (QuestionBean questionBean : questionBeans) {
			questionLayout.addView(getQuestTextView(questionBean.getQuestDesc()));
		}
	}

	private void setQuestData(RailNumBean railNumBean) {
		List<QuestionBean> questionBeans = railNumBean.getQuestionBeans();
		int childCount = questionLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			TextView tv = (TextView) questionLayout.getChildAt(i);
			String question = tv.getText().toString();
			questionBeans.add(new QuestionBean(question));
		}
	}

	private void setGridData(RailNumBean railNumBean) {
		List<LineNumBean> lineNumBeans = railNumBean.getLineNumBeans();
		int tableRowCount = tableLayout.getChildCount();
		int viewIndex = 0;
		int dataIndex = 0;
		while (viewIndex < tableRowCount) {
			TableRow tableRow = (TableRow) tableLayout.getChildAt(viewIndex);
			if (tableRow.getVisibility() == View.VISIBLE) {
				EditText et1 = (EditText) tableRow.getChildAt(1);
				EditText et2 = (EditText) tableRow.getChildAt(2);
				lineNumBeans.add(new LineNumBean(dataIndex, getEdtiTextNum2(et1), getEdtiTextNum2(et2)));
				dataIndex++;
			}
			viewIndex++;
		}

	}

	private double getLcScope(EditText etStartLc, EditText etEndLc) {
		int startLc = getEdtiTextNum(etStartLc);
		int endLc = getEdtiTextNum(etEndLc);
		double lc = startLc * 1.0 + endLc * 1.0 / 1000;
		return lc;
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

	private String getPreviousNumStr(int number) {
		if (number > 1) {
			return (number - 1) + "";
		} else if (number == 0) {
			return "1";
		} else if (number == -1) {
			return "1";
		}
		return "1";
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
				String json = data.getStringExtra("josn");
				MeasureData measureData = JSON.parseObject(json, MeasureData.class);
				boolean saveResult = service.saveQuestion(measureData);
				if (saveResult) {
					questions = service.getQuestions(measureData);
					if (questions != null && questions.size() > 0) {
						Collections.sort(questions, new QuestionComparator());
					}
					showQuestions();
				}
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
	private EditText etRailNum;
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
	private PopupWindow loadinPop;

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
				// 保存
				if (!TextUtils.isEmpty(etFocus.getText().toString().trim())) {
					saveEditTextData(etFocus);
				}
			}
		}
	}

	private void saveEditTextData(EditText etFocus) {
		String tag = (String) etFocus.getTag();

		int num = getEdtiTextNum2(etFocus);
		MeasureData measureData = new MeasureData();
		measureData.setValue1(num);
		double[] gps = SpUtiles.BaseInfo.getGps();
		measureData.setXvalue(gps[0] + "");
		measureData.setYvalue(gps[1] + "");
		measureData.setInspdate(DateUtil.getCurrDateFormat(DateUtil.style_nyrsfm));

		String[] splits = tag.split("_");
		String lineNumStr = splits[splits.length - 2];
		int lineNum = Integer.parseInt(lineNumStr);
		measureData.setLinenum(lineNum);
		measureData.setGh(getEdtiTextNum(etRailNum) + "");
		measureData.setStatus("0");
		measureData.setFlag_v("2");
		measureData.setNametype("水平");
		measureData.setHasld(0);
		measureData.setOrgid(orgid);
		measureData.setSiteid(siteid);
		measureData.setAssetnum(assetnum);
		measureData.setWonum(wonum);
		measureData.setCp1nanumcfgrownum(straightLineConfig.get(lineNum - 1).getCP1NANUMCFGROWNUM());
		double startScope = getLcScope(etStartKM, etStartM);
		double endScope = getLcScope(etEndKM, etEndM);
		measureData.setStartmeasure(startScope);
		measureData.setEndmeasure(endScope);
		measureData.setTag(tag);
		if (measureType) {
			measureData.setMeasuretype(4);
		} else {
			measureData.setMeasuretype(8);
		}
		service.saveMeasureData(measureData, tag, startScope, endScope, getEdtiTextNum(etRailNum));
	}

	@Override
	public void onStop() {
		// saveFocusEditTextData();
		super.onStop();
	}

}
