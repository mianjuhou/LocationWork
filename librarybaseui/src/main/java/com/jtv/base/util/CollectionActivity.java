package com.jtv.base.util;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import android.app.Activity;

/**
 * 所有界面的管理者
 * 
 * @author beyound
 * @email whatl@foxmail.com
 * @date 2015年7月14日
 */
public class CollectionActivity {
	private CollectionActivity() {
		
	}

	private static SoftReference<ArrayList<Activity>> soft = new SoftReference<ArrayList<Activity>>(
			new ArrayList<Activity>());

	private static SoftReference<ArrayList<Activity>> allAdd = new SoftReference<ArrayList<Activity>>(
			new ArrayList<Activity>());

	private static ArrayList<Activity> arrayList;

	private static ArrayList<Activity> allArrayList;

	/**
	 * 添加一个activity到顶部
	 * 
	 * @param act
	 */
	public static void putTopActivity(Activity act) {
		arrayList = getArrayList();

		allArrayList = getAllArrayList();
		if (allArrayList.size() > 30) {// 如果大与30
			for (int i = 0; i < 10; i++) {
				allArrayList.remove(i);
			}
		}
		removeTopActivity(arrayList);
		allArrayList.add(act);
		arrayList.add(act);
	}

	public static void removeTopActivity() {
		arrayList = getArrayList();
		removeTopActivity(arrayList);
	}

	public static void removeTopActivity(ArrayList<Activity> list) {
		if (list != null && list.size() > 0) {
			list.remove(list.size() - 1);
		}
	}

	public static Activity getTopActivity() {
		arrayList = getArrayList();
		if (arrayList != null && arrayList.size() > 0) {
			return arrayList.get(arrayList.size() - 1);
		}
		return null;
	}

	private static ArrayList<Activity> getArrayList() {
		if (soft == null) {
			soft = new SoftReference<ArrayList<Activity>>(new ArrayList<Activity>());
		}
		if (soft.get() == null) {
			soft = new SoftReference<ArrayList<Activity>>(new ArrayList<Activity>());
		}
		return soft.get();
	}

	/**
	 * 获取所有的activity
	 * 
	 * @return
	 */
	public static ArrayList<Activity> getAllArrayList() {
		if (allAdd == null) {
			allAdd = new SoftReference<ArrayList<Activity>>(new ArrayList<Activity>());
		}
		if (allAdd.get() == null) {
			allAdd = new SoftReference<ArrayList<Activity>>(new ArrayList<Activity>());
		}
		return allAdd.get();
	}

}
