package com.jtv.hrb.locationwork.fragment.mvp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.LineCurveCheckActivity;
import com.jtv.hrb.locationwork.comparator.ConfigRowComparator;
import com.jtv.hrb.locationwork.comparator.HistoryComparator;
import com.jtv.hrb.locationwork.comparator.QuestionComparator;
import com.jtv.hrb.locationwork.domain.ConfigRowName;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.SpUtiles;

public class StraightCheckPresenter {
	private IStraightCheckView scView;
	private IStraightCheckModel scModel;
	private LineCurveCheckActivity mActivity;

	public String assetnum;
	public String wonum;
	public String startkm;
	public String endkm;
	private String siteid;
	private String orgid;
	private List<ConfigRowName> straightLineConfig;
	private List<MeasureData> historyMeasureData;
	private List<MeasureData> questions;

	public StraightCheckPresenter(LineCurveCheckActivity activity, IStraightCheckView scView) {
		this.scView = scView;
		this.scModel = new StraightCheckModel(activity);
		this.mActivity = activity;
		// 获取工单数据
		assetnum = activity.assetnum;
		wonum = activity.wonum;
		startkm = activity.startmeasure;
		endkm = activity.endmeasure;
		// 获取常用数据
		siteid = GlobalApplication.siteid;
		orgid = GlobalApplication.orgid;
	}

	public void initPageState() {
		// 获取DB历史数据
		historyMeasureData = scModel.getLastMeasureData(wonum, assetnum);
		if (historyMeasureData != null && historyMeasureData.size() > 0) {
			// 查询DB录入问题
			questions = scModel.getQuestions(historyMeasureData.get(0));
			if (questions != null && questions.size() > 0) {
				Collections.sort(questions, new QuestionComparator());
			}
		}
		// 获取配置数据
		scModel.getWorkOrderInfo(mActivity,//
				GlobalApplication.siteid,//
				new ObserverCallBack() {
					@Override
					public void badBack(String error, int method, Object obj) {
						straightLineConfig = scModel.getDefaultConfigDatas();
						afterConfigData();
					}

					@Override
					public void back(String data, int method, Object obj) {
						if (method == MethodApi.HTTP_QUERY_CHECK_CONFIG) {
							straightLineConfig = getNetworkConfigDatas(data);
						} else {
							straightLineConfig = scModel.getDefaultConfigDatas();
						}
						afterConfigData();
					}
				});
	}

	private void afterConfigData() {
		showHistoryDataInPage();
		scView.showQuestions(questions);
	}

	private void showHistoryDataInPage() {
		Collections.sort(historyMeasureData, new HistoryComparator());
		MeasureData measureData = historyMeasureData.get(0);
		scView.setStartLc(getKM(measureData.getStartmeasure() + "") + "", getM(measureData.getEndmeasure() + "") + "");
		scView.setEndLc(getKM(measureData.getEndmeasure() + "") + "", getM(measureData.getEndmeasure() + "") + "");
		scView.setGh(measureData.getGh());
		scView.setMeasureType(isMeasureType4(historyMeasureData));

	}

	private boolean isMeasureType4(List<MeasureData> historyMeasureData) {
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

	public void saveMeasureData(String svalue, String tag) {
		MeasureData measureData = new MeasureData();
		if (TextUtils.isEmpty(svalue)) {
			return;
		} else {
			double value1 = Double.parseDouble(svalue);
			measureData.setValue1(value1);
		}
		double[] gps = SpUtiles.BaseInfo.getGps();
		measureData.setXvalue(gps[0] + "");
		measureData.setYvalue(gps[1] + "");
		measureData.setInspdate(DateUtil.getCurrDateFormat(DateUtil.style_nyrsfm));

		String[] splits = tag.split("_");
		String lineNumStr = splits[splits.length - 2];
		int lineNum = Integer.parseInt(lineNumStr);
		measureData.setLinenum(lineNum);
		measureData.setGh(scView.getGh());
		measureData.setStatus("0");
		measureData.setFlag_v("2");
		measureData.setNametype("轨距");
		measureData.setHasld(0);
		measureData.setOrgid(orgid);
		measureData.setSiteid(siteid);
		measureData.setAssetnum(assetnum);
		measureData.setWonum(wonum);
		measureData.setCp1nanumcfgrownum(straightLineConfig.get(lineNum - 1).getCP1NANUMCFGROWNUM());
		measureData.setStartmeasure(getLcScope(scView.getStartLc()));
		measureData.setEndmeasure(getLcScope(scView.getEndLc()));
		measureData.setTag(tag);
		if (scView.getMeasureType()) {
			measureData.setMeasuretype(4);
		} else {
			measureData.setMeasuretype(8);
		}
		scModel.saveMeasureData(measureData);
	}

	private double getLcScope(String[] startLc) {
		int km = Integer.parseInt(startLc[0]);
		int m = Integer.parseInt(startLc[1]);
		double lc = km * 1.0 + m * 1.0 / 1000;
		return lc;
	}

	private List<ConfigRowName> getNetworkConfigDatas(String data) {
		List<ConfigRowName> configRowData = JSON.parseArray(data, ConfigRowName.class);
		List<ConfigRowName> straightLineConfigData = getRowConfig("", configRowData);
		return straightLineConfigData;
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

	public void setNextLc() {
		String[] endLc = scView.getEndLc();
		int endKm = Integer.parseInt(endLc[0]);
		endKm++;
		scView.setStartLc(endLc[0], endLc[1]);
		scView.setEndLc(endKm + "", endLc[1]);
		// 轨号初始化
		scView.setGh("1");
		//操作表格数据
		getAndShowTableLayoutData();
		//操作问题数据
		getAndShowQuestionListData();
	}
	
	public void setPreviousLc() {
		String[] startLc = scView.getStartLc();
		int startKm = Integer.parseInt(startLc[0]);
		startKm--;
		if (startKm < 0) {
			startKm = 0;
		}
		scView.setStartLc(startKm + "", startLc[1]);
		scView.setEndLc(startLc[0], startLc[1]);
		// 轨号初始化
		scView.setGh("1");
		// 查询历史数据
		
		// 显示历史数据
		
	}

	public void setNextGh() {
		int gh = Integer.parseInt(scView.getGh());
		gh++;
		scView.setGh(gh + "");
		// 查询历史数据

		// 显示历史数据
	}

	public void setPreviousGh() {
		String sGh = scView.getGh();
		int gh = Integer.parseInt(sGh);
		gh--;
		if (gh <= 0) {
			gh = 1;
		}
		scView.setGh(gh + "");
		if (sGh.equals(gh + "")) {
			return;
		}
		// 查询历史数据
		// 显示历史数据
	}

	public void getAndShowTableLayoutData(){
		//查询表格数据
		historyMeasureData=scModel.getStraightLineMeasureData(createMeasureData());
		if (historyMeasureData != null && historyMeasureData.size() > 0) {
			//排序表格数据
			Collections.sort(historyMeasureData, new HistoryComparator());
			//显示表格数据
			scView.showTableData(historyMeasureData);
		}else{
			scView.clearTableData();
		}
		
	}
	
	public void getAndShowQuestionListData(){
		//查询问题数据
		questions=scModel.getQuestions(createMeasureData());
		if(questions!=null&&questions.size()>0){
			//排序问题数据
			Collections.sort(questions, new QuestionComparator());
			//显示问题数据
			scView.showQuestions(questions);
		}else{
			scView.clearQuestions();
		}
	}
	
	public MeasureData createMeasureData(){
		MeasureData measureData=new MeasureData();
		measureData.setOrgid(orgid);
		measureData.setSiteid(siteid);
		measureData.setWonum(wonum);
		measureData.setAssetnum(assetnum);
		measureData.setStartmeasure(getLcScope(scView.getStartLc()));
		measureData.setEndmeasure(getLcScope(scView.getEndLc()));
		measureData.setGh(scView.getGh());
		return measureData;
	}
	
	public void addAndUpdateQt(String qtJson) {
		MeasureData measureData = JSON.parseObject(qtJson, MeasureData.class);
		// 保存
		boolean saveResult = scModel.saveQuestion(measureData);
		if (saveResult) {
			// 重新获取
			questions = scModel.getQuestions(measureData);
			if (questions != null && questions.size() > 0) {
				// 排序
				Collections.sort(questions, new QuestionComparator());
			}
			// 显示
			scView.showQuestions(questions);
		}
	}

}
