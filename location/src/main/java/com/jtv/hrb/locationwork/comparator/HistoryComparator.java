package com.jtv.hrb.locationwork.comparator;

import java.util.Comparator;

import com.jtv.hrb.locationwork.domain.MeasureData;

public class HistoryComparator implements Comparator<MeasureData> {
	@Override
	public int compare(MeasureData lhs, MeasureData rhs) {
		String ltag=lhs.getTag();
		String rtag=rhs.getTag();
		String[] lsplit = ltag.split("_");
		String[] rsplit = rtag.split("_");
		int lrowNum=Integer.parseInt(lsplit[lsplit.length-2]);
		int lcolumnNum=Integer.parseInt(lsplit[lsplit.length-1]);
		int rrowNum=Integer.parseInt(rsplit[lsplit.length-2]);
		int rcolumnNum=Integer.parseInt(rsplit[lsplit.length-1]);
		if(lrowNum>rrowNum){
			return 1;
		}else if(lrowNum==rrowNum){
			if(lcolumnNum>rcolumnNum){
				return 1;
			}else if(lcolumnNum<rcolumnNum) {
				return -1;
			}else{
				return 0;
			}
		}else{
			return -1;
		}
	}
	
}
