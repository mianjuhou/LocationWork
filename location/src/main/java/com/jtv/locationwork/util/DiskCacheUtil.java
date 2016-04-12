package com.jtv.locationwork.util;

import com.jtv.locationwork.util.Cache.Entry;

import android.content.Context;

/**
 * 缓存的工具类
 * 
 * @author beyound
 *
 */
public class DiskCacheUtil {

	private static String NAME_CACHE = "cache_jtv";

	private static DiskBasedCache diskBasedCache = null;

	public static String FACE_KEY = "face";

	public static String TOOL_KEY = "tool";

	public static String WONUM_APPEND_KEY = "";// 保存的追加工单

	/**
	 * 获取缓存
	 * 
	 * @param con
	 * @return
	 */
	public static DiskBasedCache getCache(Context con) {
		if (con == null) {
			return null;
		}

		if (diskBasedCache == null) {
			synchronized (DiskCacheUtil.class) {
				diskBasedCache = new DiskBasedCache(con.getDir(NAME_CACHE, Context.MODE_PRIVATE));
				diskBasedCache.initialize();
			}
		}

		return diskBasedCache;
	}

	/**
	 * 获取一个实体
	 * 
	 * @param data
	 *            内容
	 * @param hour
	 *            小时
	 * @return
	 */
	public static Entry getEntry(String data, int hour) {

		Entry entry = new Cache.Entry();
		long currTime = System.currentTimeMillis();
		entry.softTtl = currTime + 1000 * 60 * 60 * hour;
		entry.ttl = entry.softTtl;

		entry.data = data.getBytes();

		return entry;
	}

	/**
	 * 获取一个实体
	 * 
	 * @param data
	 *            内容
	 * @param milli
	 *            秒
	 * @return
	 */
	public static Entry getEntryForMills(String data, int milli) {

		Entry entry = new Cache.Entry();
		long currTime = System.currentTimeMillis();
		entry.softTtl = currTime + milli*1000;
		entry.ttl = entry.softTtl;

		entry.data = data.getBytes();

		return entry;
	}
}
