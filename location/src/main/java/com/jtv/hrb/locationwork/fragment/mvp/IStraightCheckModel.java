package com.jtv.hrb.locationwork.fragment.mvp;

import java.util.ArrayList;
import java.util.List;

import com.jtv.hrb.locationwork.domain.ConfigRowName;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.locationwork.httputil.ObserverCallBack;

import android.content.Context;

public interface IStraightCheckModel {
	void getWorkOrderInfo(Context con,String siteid,ObserverCallBack back);
	List<ConfigRowName> getDefaultConfigDatas();
	void saveMeasureData(MeasureData data);
	ArrayList<MeasureData> getLastMeasureData(String wonum, String assetnum);
	List<MeasureData> getQuestions(MeasureData measureData);
	boolean saveQuestion(MeasureData measureData);
	ArrayList<MeasureData> getStraightLineMeasureData(MeasureData measureData);
}
