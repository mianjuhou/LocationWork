package com.jtv.hrb.locationwork.comparator;

import java.util.Comparator;

import com.jtv.hrb.locationwork.domain.MeasureData;

public class QuestionComparator implements Comparator<MeasureData>{
	@Override
	public int compare(MeasureData lhs, MeasureData rhs) {
		int llinenum = lhs.getLinenum();
		int rlinenum = rhs.getLinenum();
		if(llinenum>rlinenum){
			return 1;
		}else if(llinenum<rlinenum){
			return -1;
		}else{
			return 0;
		}
	}
}
