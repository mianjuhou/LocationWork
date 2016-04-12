package com.jtv.locationwork.fragment;

import android.support.v4.app.Fragment;

public class SingleHomeFragment {

	private static Fragment wroknum;

	private static Fragment setting;

	private static Fragment finder;

	private SingleHomeFragment() {
	};

	public static Fragment creatWorkNumFragment() {
		if (wroknum == null) {
			wroknum = creat(wroknum, new WorkFragment());
		}
		return wroknum;
	}

	public static Fragment creatSettingFramgment() {
		if (setting == null) {
			setting = creat(setting, new SetOtherFragment());
		}
		return setting;
	}

	public static Fragment creatFinderFragment() {

		if (finder == null) {
			finder = creat(finder, new FinderFragment());
		}
		return finder;

	}

	private static Fragment creat(Fragment switchf, Fragment creatOK) {
		if (switchf == null) {
			switchf = creatOK;
		}
		return switchf;
	}

}
