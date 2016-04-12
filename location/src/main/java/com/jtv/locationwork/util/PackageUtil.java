package com.jtv.locationwork.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtil {

	public static int getVersion(Context con) {

		PackageInfo pi = null;
		int versionCode = -1;
		try {
			pi = con.getPackageManager().getPackageInfo(con.getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionCode;
	}

	public static String getPackage(Context con) {
		return con.getPackageName();
	}
	
	
	public static String getVersionName(Context con){


		PackageInfo pi = null;
		String versionName = "-1";
		try {
			pi = con.getPackageManager().getPackageInfo(con.getPackageName(), 0);
			 versionName = pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionName;
	
	}
	
	
}
