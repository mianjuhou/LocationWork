package com.jtv.locationwork.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 网络的工具类
 * 
 * @author ls
 * 
 */
public class NetUtil {
	public static int NetType = 0;
	public static final String proxyMobile = "10.0.0.172";
	public static final String proxyTel = "10.0.0.200";
	public static final String CTWAP = "ctwap";
	public static final String CMWAP = "cmwap";
	public static final String WAP_3G = "3gwap";
	public static final String UNIWAP = "uniwap";
	public static final int TYPE_CM_CU_WAP = 4;
	public static final int TYPE_CT_WAP = 5;
	public static final int TYPE_OTHER_NET = 6;
	public static Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");

	/**
	 * 判断当前网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasConnectedNetwork(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService("connectivity");
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						info=null;
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 判断当前是否有可用的网络以及网络类型 0：无网络 1：WIFI 2：CMWAP 3：CMNET
	 * 
	 * @param context
	 * @return
	 */
	public static int isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return 0;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						NetworkInfo netWorkInfo = info[i];
						if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return 1;
						} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							String extraInfo = netWorkInfo.getExtraInfo();
							if ("cmwap".equalsIgnoreCase(extraInfo)
									|| "cmwap:gsm".equalsIgnoreCase(extraInfo)) {
								return 2;
							}
							return 3;
						}
					}
				}
			}
		}
		return 0;
	}
	/**
	 * 主线程访问网络
	 */
//	public void mainNetHttp(){
//		if (android.os.Build.VERSION.SDK_INT > 9) {
//		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//		    StrictMode.setThreadPolicy(policy);
//		}
//	}

	/**
	 * 检查当前的网络类型
	 * 
	 * @param mContext
	 * @return
	 */
	public static int checkNetworkType(Context mContext) {
		try {
			final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mobNetInfoActivity = connectivityManager
					.getActiveNetworkInfo();
			if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
				return TYPE_OTHER_NET;
			} else {
				int netType = mobNetInfoActivity.getType();
				if (netType == ConnectivityManager.TYPE_WIFI) {
					return TYPE_OTHER_NET;
				} else if (netType == ConnectivityManager.TYPE_MOBILE) {
					final Cursor c = mContext.getContentResolver().query(
							PREFERRED_APN_URI, null, null, null, null);
					if (c != null) {
						c.moveToFirst();
						final String user = c.getString(c
								.getColumnIndex("user"));
						if (!TextUtils.isEmpty(user)) {
							if (user.startsWith(CTWAP)) {
								return TYPE_CT_WAP;
							}
						}
					}
					c.close();

					String netMode = mobNetInfoActivity.getExtraInfo();
					if (netMode != null) {
						netMode = netMode.toLowerCase();
						if (netMode.equals(CMWAP) || netMode.equals(WAP_3G)
								|| netMode.equals(UNIWAP)) {
							return TYPE_CM_CU_WAP;
						}

					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return TYPE_OTHER_NET;
		}

		return TYPE_OTHER_NET;

	}

	/**
	 * 通过io流把字符串写入本地
	 * 
	 * @param path
	 *            sd卡全路径
	 * @param content
	 * @throws IOException
	 */
	private static void wirteStringToLocal(String path, String content)
			throws IOException {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			byte[] bytes = content.getBytes("UTF-8");
			out.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 把一个地址通过utf-8编码把他组装成get请求的地址
	 * 
	 * @param map
	 * @return url
	 */
	private static String encodeParameters(Map<String, String> map) {
		StringBuffer buf = new StringBuffer();
		Set<String> set = map.keySet();
		Iterator<String> iterator = set.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = (String) map.get(key);

			if ((key == null) || ("".equals(key)) || (value == null)
					|| ("".equals(value))) {
				continue;
			}
			if (i != 0)
				buf.append("&");
			try {
				buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
						.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			i++;
		}
		return buf.toString();
	}

	/**
	 * 是否是wifi连接
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 打开网络设置
	 * 
	 * @param activity
	 */
	public static void openSetting(Activity activity) {
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}
	// Gps是否可用  
	    private boolean isGpsEnable( Context mContext) {  
	       LocationManager locationManager =   
	               ((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE));  
	       return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
	   }  

}