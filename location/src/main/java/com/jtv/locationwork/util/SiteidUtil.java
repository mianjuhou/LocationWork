package com.jtv.locationwork.util;

import android.text.TextUtils;

public class SiteidUtil {
	public static final String DJD = "S01115";
	public static final String DXD = "S01116";
	public static final String HLR = "S01505";
	public static final String DQD = "S01403";
	public static final String ZZQGD = "S05118";

	public static boolean isDJD(String siteid) {
		return TextUtils.equals(siteid, DJD);
	}

	public static boolean isDXD(String siteid) {
		return TextUtils.equals(siteid, DXD);
	}

	public static boolean isHLR(String siteid) {
		return TextUtils.equals(siteid, HLR);
	}

	public static boolean isDQD(String siteid) {
		return TextUtils.equals(siteid, DQD);
	}

	public static boolean isZZQGD(String siteid) {
		return TextUtils.equals(siteid, ZZQGD);
	}

}
