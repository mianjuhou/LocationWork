package com.jtv.base.util;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class InitFirst {
	private static String XML_NAME = ""; // xml名字

	private String XML_VERSION_KEY = "version_code_config"; // 版本的键

	private static long endOpenTime = 0; // 保存的是上一次打开的时间

	private static final String XML_END_OPEN_TIME = "end_open_time"; // 最后打开的时间键

	private static final String XML_APP_COUNT = "app_start_count"; // 版本打开的次数键

	private static final String XML_TODAY_OPEN_COUNT = "today_open_count"; // 今天打开的次数

	private static SharedPreferences sharedPreferences; // 保存的sp

	private int versionCode; // 当前的手机版本

	public static InitFirst instance = null; // 对象

	private static Context con; // 上下文

	private static int day; // 今天的天数

	public static InitFirst getInstance(Context con) {
		if (instance == null) {
			instance = new InitFirst();
			XML_NAME = "app_init_first_config";
			day = new Date().getDay();

			try {
				sharedPreferences = con.getSharedPreferences(XML_NAME, Activity.MODE_PRIVATE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			int openCount = getOpenCount();
			endOpenTime = sharedPreferences.getLong(XML_END_OPEN_TIME, 0);
			openCount++;
			int todayCount = getTodayCount();
			todayCount++;

			Editor edit = sharedPreferences.edit();
			edit.putLong(XML_END_OPEN_TIME, new Date().getTime());
			edit.putInt(XML_APP_COUNT, openCount);
			edit.putInt(XML_TODAY_OPEN_COUNT + day, todayCount);
			edit.commit();
		}
		InitFirst.con = con;
		return instance;
	}

	public void start() throws Exception {
		PackageInfo pi = null;
		try {
			pi = con.getPackageManager().getPackageInfo(con.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		versionCode = pi.versionCode;

		if (sharedPreferences == null) {
			throw new Exception("上下文不可用context");
		}
		boolean contains = sharedPreferences.contains(XML_VERSION_KEY);
		if (!contains) {// 代表第一次安装
			Editor edit = sharedPreferences.edit();
			edit.putInt(XML_VERSION_KEY, versionCode).commit();
			if (listener != null) {
				listener.initInstall(true);
				listener.initVersion(true);
			}
		} else {// 代表不是第一次安装,检查是否第一次安装该版本
			if (listener != null)
				listener.initInstall(false);
			int saveVersion = sharedPreferences.getInt(XML_VERSION_KEY, -1);
			if (saveVersion == -1) { // 代表第一次使用该版本
				Editor edit = sharedPreferences.edit();
				edit.putInt(XML_VERSION_KEY, versionCode).commit();

				if (listener != null)
					listener.initVersion(false);
			} else {
				if (versionCode == saveVersion) {// 不是第一次使用该版本

					if (listener != null)
						listener.initVersion(false);
				} else { // 第一次使用该版本

					Editor edit = sharedPreferences.edit();
					edit.putInt(XML_VERSION_KEY, versionCode).commit();

					if (listener != null)
						listener.initVersion(true);
				}
			}
		}

	}

	/**
	 * 清除第一次安装
	 */
	public void clearFirstInstall() {
		Editor edit = sharedPreferences.edit();
		edit.remove(XML_VERSION_KEY).commit();
	}

	/**
	 * 清除第一次使用该版本
	 */
	public void clearFirstVersion() {
		Editor edit = sharedPreferences.edit();
		edit.putInt(XML_VERSION_KEY, -1).commit();
	}

	private InitFirst() {
	}

	public OnInitFirstListener listener;

	public void setOnInitFirstListener(OnInitFirstListener listener) {
		this.listener = listener;
	}

	/**
	 * 获取app从安装到现在的启动次数
	 * 
	 * @return
	 */
	public static int getOpenCount() {

		if (sharedPreferences != null) {

			return sharedPreferences.getInt(XML_APP_COUNT, 0);

		}
		return 0;
	}

	/**
	 * 获取上一次打开app时间
	 * 
	 * @return
	 */
	public static long getEndOpenAppTime() {
		return endOpenTime;
	}

	/**
	 * 今天打开的次数
	 * 
	 * @return 次数
	 */
	public static int getTodayCount() {

		if (sharedPreferences != null) {
			Editor edit = sharedPreferences.edit();

			edit.putInt(XML_TODAY_OPEN_COUNT + (day - 1), 0);

			edit.commit();

			return sharedPreferences.getInt(XML_TODAY_OPEN_COUNT + day, 0);
		}
		return 0;
	}

	/**
	 * 都会调用这两个方法
	 * 
	 * @author beyound
	 * @email whatl@foxmail.com
	 * @date 2015年7月8日
	 */
	public interface OnInitFirstListener {
		/**
		 * 
		 * @param true
		 *            代表第一次安装apk 或则已经安装在次安装为true
		 */
		public void initInstall(boolean isFirstInstall);

		/**
		 *
		 * @param isFirstVersion
		 *            true 代表第一次使用该版本
		 */
		public void initVersion(boolean isFirstVersion);

	}

}
