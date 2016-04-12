package com.jtv.locationwork.util;

import android.util.Log;

/**
 * 一个简单的打印日志
 * 
 * @author beyound
 * @email whatl@foxmail.com
 * @date 2015年9月7日
 */
public class LogUtil {

	private static boolean i = true; // 控制是否显示info

	private static boolean e = true; // 控制是否显示异常

	private static boolean w = true; // 控制是否显示异常

	public static String iName = "JTV-TAG"; // 显示tag标记名字

	public static String eName = "eTAG"; // 显示异常tag标记名字

	public static String base_i = "打印信息日志:"; // 打印信息最基本信息

	public static String base_e = "打印错误日志:"; // 打印错误最基本信息

	public static String base_w = "打印错误日志:"; // 打印警告最基本信息;

	public static void i(String message) {
		i(iName, message);
	}

	public static void i(String tag, String message) {
		if (i) {
			Log.i(tag, base_i + " " + message);
		}
	}

	public static void e(String message) {
		e(eName, message);
	}

	public static void e(Exception message) {
		e(eName, message);
	}

	public static void e(String tag, Exception message) {
		if (e) {
			try {
				Log.e(tag, base_e + " " + Log.getStackTraceString(message));
			} catch (Exception e) {
				Log.i(tag, base_e + " " + Log.getStackTraceString(e));
			}
		}
	}

	public static void e(String tag, String message) {
		if (e) {
			Log.e(tag, base_e + " " + message);
		}
	}

	public static void w(String tag, Exception e){
		if (w) {
			try {
				Log.w(tag, base_w +" " + Log.getStackTraceString(e));
			} catch (Exception ep) {
				Log.w(tag, base_w + " " + Log.getStackTraceString(ep));
			}
		}
	}
	public static void w(Exception e){
		if (w) {
			try {
				Log.w(eName, base_w +" " + Log.getStackTraceString(e));
			} catch (Exception ep) {
				Log.w(eName, base_w + " " + Log.getStackTraceString(ep));
			}
		}
	}
}
