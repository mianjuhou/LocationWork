package com.jtv.locationwork.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.locationwork.entity.ItemWonum;
import com.jtv.locationwork.util.Cache.Entry;
import com.jtv.locationwork.util.DiskBasedCache;
import com.jtv.locationwork.util.DiskCacheUtil;
import com.jtv.locationwork.util.JsonUtil;
import com.jtv.locationwork.util.WonumUtil;

import android.content.Context;

/**
 * 追加工单的工具类用来维护工单是否已经追加
 *
 */
public class WonumCacheUtil {

	public static void removeCacheWonumItem(Context con, ItemWonum other) {
		
		if(other==null){
			return;
		}
		
		String mCurrWonum = other.toString();

		DiskBasedCache cache = DiskCacheUtil.getCache(con);

		Entry entry = cache.get(DiskCacheUtil.WONUM_APPEND_KEY);

		if (entry == null||entry.isExpired()) {
			return;
		}

		byte[] data = entry.data;

		String hoistory = new String(data);

		try {

			JSONObject itemJson = new JSONObject(mCurrWonum);

			JSONArray jsonArray = new JSONArray(hoistory);

			int index = -1;

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject itemWonum = jsonArray.optJSONObject(i);

				if (itemWonum.toString().equals(itemJson.toString())) {
					index = i;
				}

			}

			if (index >= 0)
				JsonUtil.removeJsonArrayIndex(index, jsonArray);
			
			Entry entry2 = DiskCacheUtil.getEntry(jsonArray.toString(), 9);

			cache.put(DiskCacheUtil.WONUM_APPEND_KEY, entry2);// 保存当前工单到缓存
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void saveCacheWonumItem(Context con, ItemWonum other) {

		String mCurrWonum = other.toString();// 获取到后台的响应数据为jsonobject

		JSONObject mCurrWonumJson;// 当前的一条工单变成为jsonobject

		try {
			mCurrWonumJson = new JSONObject(mCurrWonum);
		} catch (JSONException e1) {
			return;
		}

		JSONArray jsonArray = null;

		DiskBasedCache cache = DiskCacheUtil.getCache(con);

		Entry entry = cache.get(DiskCacheUtil.WONUM_APPEND_KEY);// 获取到原有数据是否可用，原有数据为jsonarray

		if (entry != null && !entry.isExpired()) {// 数据可用

			try {
				byte[] data = entry.data;

				String mAlered = new String(data);

				jsonArray = new JSONArray(mAlered);

				data = null;
				mAlered = null;

			} catch (Exception e) {
				e.printStackTrace();
				jsonArray = null;
			}

		}

		if (jsonArray == null) {
			jsonArray = new JSONArray();
		}

		jsonArray.put(mCurrWonumJson);// 存一条工单数据

		mCurrWonum = jsonArray.toString();

		entry = DiskCacheUtil.getEntry(mCurrWonum,9);

		cache.put(DiskCacheUtil.WONUM_APPEND_KEY, entry);// 保存当前工单到缓存

		mCurrWonum = null;
	}

	/**
	 * 从缓存中获取还可用的工单，可能有多条
	 * 
	 * @param con
	 * @return 多条添加的工单
	 */
	public static List<ItemWonum> getAvaiableCache(Context con) {

		List<ItemWonum> mWonumList = new ArrayList<ItemWonum>();

		DiskBasedCache cache = DiskCacheUtil.getCache(con);

		Entry entry = cache.get(DiskCacheUtil.WONUM_APPEND_KEY);

		if (entry == null || entry.isExpired()) {

			return mWonumList;
		}

		byte[] data = entry.data;

		String string = new String(data);

		JSONArray jsonArray;

		try {

			jsonArray = new JSONArray(string);

		} catch (JSONException e) {
			e.printStackTrace();
			return mWonumList;
		}

		mWonumList = WonumUtil.swithcLists(jsonArray);

		return mWonumList;

	}

	public static void clear(Context con) {
		DiskBasedCache cache = DiskCacheUtil.getCache(con);
		cache.remove(DiskCacheUtil.WONUM_APPEND_KEY);
	}
}
