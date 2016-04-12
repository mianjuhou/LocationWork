package com.jtv.hrb.locationwork.fragment.mvp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.jtv.hrb.locationwork.LineCurveCheckActivity;
import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.ConfigRowName;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;

public class StraightCheckModel implements IStraightCheckModel{
	private LineCurveCheckActivity activity;
	private StaticCheckDbService service;
	public StraightCheckModel(LineCurveCheckActivity mActivity) {
		this.activity = mActivity;
		this.service = new StaticCheckDbService(this.activity);
	}
	
	@Override
	public void getWorkOrderInfo(Context con, String siteid, ObserverCallBack back) {
		TrackAPI.queryPersonCheckWoInfoConfig(con, back, siteid);
	}
	
	@Override
	public List<ConfigRowName> getDefaultConfigDatas() {
		List<ConfigRowName> straightLineConfig = new ArrayList<ConfigRowName>();
		straightLineConfig.add(new ConfigRowName("2001", "接头"));
		straightLineConfig.add(new ConfigRowName("2002", ""));
		straightLineConfig.add(new ConfigRowName("2003", "中间"));
		straightLineConfig.add(new ConfigRowName("2004", ""));
		straightLineConfig.add(new ConfigRowName("2005", "接头"));
		straightLineConfig.add(new ConfigRowName("2006", ""));
		straightLineConfig.add(new ConfigRowName("2007", "中间"));
		straightLineConfig.add(new ConfigRowName("2008", ""));
		return straightLineConfig;
	}

	@Override
	public void saveMeasureData(MeasureData data) {
		try {
			int gh=Integer.parseInt(data.getGh());
			service.saveMeasureData(data, data.getTag(), data.getStartmeasure(), data.getEndmeasure(), gh);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<MeasureData> getLastMeasureData(String wonum, String assetnum) {
		return service.getLastMeasureData(wonum, assetnum);
	}

	@Override
	public List<MeasureData> getQuestions(MeasureData measureData) {
		return service.getQuestions(measureData);
	}

	@Override
	public boolean saveQuestion(MeasureData measureData) {
		return service.saveQuestion(measureData);
	}

	@Override
	public ArrayList<MeasureData> getStraightLineMeasureData(MeasureData measureData) {
		return service.getMeasureDataByRailNum(//
				measureData.getWonum(),//
				measureData.getWonum(),//
				measureData.getGh()+"",//
				measureData.getStartmeasure(),//
				measureData.getEndmeasure());
	}
	
}
