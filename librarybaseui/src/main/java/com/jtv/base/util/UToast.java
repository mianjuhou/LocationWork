package com.jtv.base.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @author: zn E-mail:zhangn@jtv.com.cn
 * @version:2014-10-10 类说明:Toast显示
 */

public class UToast {

	/**
	 * Toast显示(时间显示较短)
	 * 
	 * @author:zn
	 * @version:2014-10-10
	 * @param activity
	 * @param res
	 *            (显示的资源地址)
	 */
	public static void makeShortTxt(Context context, int res) {
		if(context==null){
			return;
		}
		Toast.makeText(context, context.getString(res), Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * Toast显示(时间显示较短)
	 * 
	 * @author:zn
	 * @version:2014-10-10
	 * @param context
	 *            (当前上下文)
	 * @param res
	 *            (显示的资源地址)
	 */
	public static void makeShortTxt(Context context, String res) {
		if(context==null){
			return;
		}
		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast显示(时间显示较长)
	 * 
	 * @author:zn
	 * @version:2015-1-6
	 * @param context
	 *            (当前上下文)
	 * @param res
	 *            (显示的资源地址)
	 */
	public static void makeLongTxt(Context context, int res) {
		if(context==null){
			return;
		}
		Toast.makeText(context, context.getString(res), Toast.LENGTH_LONG)
				.show();
	}

	/**
	 * Toast显示(时间显示较长)
	 * 
	 * @author:zn
	 * @version:2015-1-6
	 * @param context
	 *            (当前上下文)
	 * @param res
	 *            (显示的资源地址)
	 */
	public static void makeLongTxt(Context context, String res) {
		if(context==null){
			return;
		}
		Toast.makeText(context, res, Toast.LENGTH_LONG).show();
	}
}
