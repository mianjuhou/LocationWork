package com.jtv.hrb.locationwork.comparator;

import java.util.Comparator;

import com.jtv.hrb.locationwork.domain.ConfigRowName;

public class ConfigRowComparator implements Comparator<ConfigRowName>{
	@Override
	public int compare(ConfigRowName lhs, ConfigRowName rhs) {
		if(lhs.getLINENUM()>rhs.getLINENUM()){
			return 1;
		}else if(lhs.getLINENUM()<rhs.getLINENUM()){
			return -1;
		}else{
			return 0;
		}
	}
}
