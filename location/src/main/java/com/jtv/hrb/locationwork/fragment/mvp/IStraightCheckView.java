package com.jtv.hrb.locationwork.fragment.mvp;

import java.util.List;

import com.jtv.hrb.locationwork.domain.MeasureData;


public interface IStraightCheckView {
	String[] getStartLc();
	String[] getEndLc();
	void setStartLc(String startKm,String startM);
	void setEndLc(String endKm,String endM);
	String getGh();
	void setGh(String gh);
	boolean getMeasureType();
	void setMeasureType(boolean type);
	void showQuestions(List<MeasureData> questions);
	void showTableData(List<MeasureData> historyMeasureData);
	void clearTableData();
	void clearQuestions();
}
