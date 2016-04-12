package com.jtv.locationwork.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 
 * @author zhangxi@jtv.com.cn 骆驼祥子 创建日期 :2015-3-20 文件名称：PDADeviceInfoService.java 备注信息：PDA手持机设备的硬件信息
 */
public class PDADeviceInfoService {

	private TelephonyManager tm;

	private static final String DEVICEDKEY = "config_devicesid";

	private static final String NAME = "config";

	private static String CACHE_DEVICEID = "";

	Context con = null;

	public PDADeviceInfoService(Context context) {
		super();
		con = context;
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
	 * 获取设备的唯一码，测试时可能获取不到
	 * 
	 * @return
	 */
	public String getDeviceId() {
		String deviceId = null;
		deviceId = tm.getDeviceId();

		return deviceId;
	}

	// 当没有获取到ID时,通过本地缓存获取
	public String getDeviceId(Context con) {
		String deviceId = tm.getDeviceId();

		if ("".equals(deviceId) || deviceId == null) {
			deviceId = getCacheDevicid(con);
		} else if (!TextUtils.equals(deviceId, CACHE_DEVICEID)) {// 是否需要缓存工单
			cacheDevicid(con, deviceId);
		}
		return deviceId;
	}

	// 当获取不到id时通过缓存获取
	private String getCacheDevicid(Context con) {
		SharedPreferences mSp = con.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		String deviceId = mSp.getString(DEVICEDKEY, "");
		return deviceId;
	}

	// 缓存工单
	private void cacheDevicid(Context con, String deviceId) {
		SharedPreferences mSp = con.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		Editor edit = mSp.edit();
		edit.putString(DEVICEDKEY, deviceId);
		edit.commit();
	}

	/**
	 * 获取电话状态
	 * 
	 * @author zhangxi@jtv.com.cn 骆驼祥子Administrator 创建日期： 2015-3-20
	 * @return
	 */
	public int getTelephoneCallState() {
		return tm.getCallState();
	}

	/**
	 * 获取android应用名称
	 * 
	 * @author zhangxi@jtv.com.cn 骆驼祥子Administrator 创建日期： 2015-3-21
	 * @return
	 * @throws NameNotFoundException
	 */
	public String getDeviceAppName(Context context) throws NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		if (pm != null) {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
			if (ai != null) {
				String appName = (String) pm.getApplicationLabel(ai);
				return appName;
			}
		}
		return "NAN";
	}

	/**
	 * 获取android应用的版本号
	 * 
	 * @author zhangxi@jtv.com.cn 骆驼祥子Administrator 创建日期： 2015-3-21
	 * @return
	 * @throws NameNotFoundException
	 */
	public String getDeviceAppVersion(Context context) throws NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		if (pm != null) {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				return pi.versionName;
			}
		}
		return "NAN";
	}

	/**
	 * 获取android系统的SDK号
	 * 
	 * @author zhangxi@jtv.com.cn 骆驼祥子Administrator 创建日期： 2015-3-21
	 * @return
	 * @throws NameNotFoundException
	 */
	public String getDeviceSDKVersion() throws NameNotFoundException {
		return android.os.Build.VERSION.SDK;
	}

	/**
	 * 获取android系统的版本号 4.1.2
	 * 
	 * @author zhangxi@jtv.com.cn 骆驼祥子Administrator 创建日期： 2015-3-21
	 * @return
	 * @throws NameNotFoundException
	 */
	public String getDeviceSystemVersion(Context context) throws NameNotFoundException {
		return android.os.Build.VERSION.RELEASE;
	}

}
