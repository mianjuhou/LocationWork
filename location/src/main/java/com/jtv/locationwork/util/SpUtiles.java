package com.jtv.locationwork.util;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.locationwork.entity.ItemWoListAttribute;
import com.jtv.locationwork.entity.ItemWonum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;


public class SpUtiles {

	// 最近的工单
	public static class NearWorkListInfo {

		private static SharedPreferences sp = GlobalApplication.mContext.getSharedPreferences(CoustomKey.NAME_NEAR_WONUM,
				Context.MODE_PRIVATE);

		public static void putBoolean(String key, boolean value) {
			Editor edit = sp.edit();
			edit.putBoolean(key, value);
			edit.commit();
		}

		/**
		 * 获取可以点击
		 * 
		 * @param key
		 * @return
		 */
		public static boolean getBoolean(String key) {
			if (sp.contains(key)) {
				boolean boolean1 = sp.getBoolean(key, true);
				return boolean1;
			}
			return false;
		}

		public static void putString(String key, String value) {
			Editor edit = sp.edit();
			edit.putString(key, value);
			edit.commit();
		}

		public static JSONObject getNearWonum() {
			SharedPreferences sp = SpUtiles.NearWorkListInfo.getSp();
			boolean contains = sp.contains(CoustomKey.NEAR_WONUM);
			if (!contains) {
				return null;
			}
			String won = sp.getString(CoustomKey.NEAR_WONUM, "");
			if (TextUtils.isEmpty(won)) {
				return null;
			}
			JSONObject mJson = null;
			try {
				mJson = new JSONObject(won);
				return mJson;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 是否是昨天的xml
		 */
		public static void deleteYesterday() {
			boolean contains = SpUtiles.NearWorkListInfo.getSp().contains("lastday");
			if (contains) {
				int date = new Date().getDate();
				int xmldate = SpUtiles.NearWorkListInfo.getSp().getInt("lastday", -1);
				if (date == xmldate) {// 如果相等代表是同一天

				} else {
					SpUtiles.NearWorkListInfo.getSp().edit().clear().commit();
				}
			}
		}

		public static void setAll(ItemWonum item) {

			HashMap<String, ItemWoListAttribute> field = item.get();

			if (field == null) {
				return;
			}
			boolean containsKey = field.containsKey(Constants.WONUM_TYPE);
			String wotype = "";
			if (containsKey) {
				ItemWoListAttribute itemWoListAttribute = field.get(Constants.WONUM_TYPE);
				wotype = itemWoListAttribute.getDisValue();
				// wotype = field.get(Constants.WONUM_TYPE);
			}
			containsKey = field.containsKey(Constants.START_TIME);
			String startTimes = "";
			if (containsKey) {
				ItemWoListAttribute itemWoListAttribute = field.get(Constants.START_TIME);
				startTimes = itemWoListAttribute.getDisValue();

				// startTimes = field.get(Constants.START_TIME);
			}
			containsKey = field.containsKey(Constants.END_TIME);
			String endTimes = "";
			if (containsKey) {

				ItemWoListAttribute itemWoListAttribute = field.get(Constants.END_TIME);
				endTimes = itemWoListAttribute.getDisValue();

				// endTimes = field.get(Constants.END_TIME);
			}

			containsKey = field.containsKey(Constants.JSON_KEY_WONUM);
			String wonum = "";

			if (containsKey) {

				ItemWoListAttribute itemWoListAttribute = field.get(Constants.JSON_KEY_WONUM);
				wonum = itemWoListAttribute.getDisValue();

				// wonum = field.get(Constants.JSON_KEY_WONUM);
			}

			containsKey = field.containsKey("type");

			String type = "";

			if (containsKey) {

				ItemWoListAttribute itemWoListAttribute = field.get("type");

				type = itemWoListAttribute.getDisValue();
				// type = field.get("type");
			}

			setAll(wonum, startTimes, endTimes, wotype, type);
		}

		public static void setAll(String wonum, String starttiem, String endtime, String wotype, String type) {
			String lead = SpUtiles.SettingINF.getString(CoustomKey.LEAD);
			Editor edit = sp.edit();
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(CoustomKey.NEAR_START_TIME, starttiem);
				jsonObject.put(CoustomKey.NEAR_END_TIME, endtime);
				jsonObject.put(CoustomKey.WONUM, wonum);
				jsonObject.put(CoustomKey.LEAD, lead);
				jsonObject.put(CoustomKey.WONUM_TYPE, wotype);
				jsonObject.put("type", type);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			edit.putInt("lastday", new Date().getDate());
			edit.putString(CoustomKey.NEAR_WONUM, jsonObject.toString());
			edit.commit();
		}

		public static String getStringForJson(String key) {
			JSONObject jsonObject = SpUtiles.NearWorkListInfo.getJsonObject(CoustomKey.NEAR_WONUM);
			if (jsonObject == null) {
				return null;
			}
			return jsonObject.optString(key);

		}

		public static void setJsonKey(String key, boolean v) {
			JSONObject jsonObject = SpUtiles.NearWorkListInfo.getJsonObject(CoustomKey.NEAR_WONUM);
			if (jsonObject == null) {
				return;
			}
			try {
				jsonObject.put(key, v);
				JSONArray jsonArray2 = new JSONArray();
				jsonArray2.put(jsonObject);
				sp.edit().clear().commit();
				sp.edit().putString(CoustomKey.NEAR_WONUM, jsonArray2.toString()).commit();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 默认false
		 * 
		 * @param key
		 * @return
		 */
		public static boolean getBooleanForJsonBo(String key) {
			JSONObject jsonObject = SpUtiles.NearWorkListInfo.getJsonObject(CoustomKey.NEAR_WONUM);
			if (jsonObject == null) {
				return false;
			}
			return jsonObject.optBoolean(key);
		}

		/**
		 * 获取工单号
		 * 
		 * @return
		 */
		public static String getWonum() {
			JSONObject jsonObject = getJsonObject(CoustomKey.NEAR_WONUM);
			if (jsonObject == null) {
				return "";
			}
			return jsonObject.optString(CoustomKey.WONUM);
		}

		public static JSONObject getJsonObject(String key) {
			if (sp == null) {
				sp = GlobalApplication.mContext.getSharedPreferences(CoustomKey.NAME_NEAR_WONUM, Context.MODE_PRIVATE);
			}
			boolean contains = sp.contains(key);
			if (contains) {
				String obj = sp.getString(key, "");
				if (!TextUtils.isEmpty(obj)) {
					try {
						JSONObject jsonObject = new JSONObject(obj);
						return jsonObject;
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
			return null;

		}

		public static SharedPreferences getSp() {
			return sp;
		}

		public static void clearALL() {
			SharedPreferences sp = GlobalApplication.mContext.getSharedPreferences(CoustomKey.NAME_NEAR_WONUM,
					Context.MODE_PRIVATE);
			sp.edit().clear().commit();
		}

	}

	public static class BaseInfo {
		
		public static  JSONArray jsonArr = new JSONArray();
		public static final SharedPreferences mbasepre = GlobalApplication.mContext
				.getSharedPreferences(CoustomKey.NAME_BASEINFO, Context.MODE_PRIVATE);

		/**
		 * 把gps保存到本地,由于又一个子线程在不断的重新获取到坐标, 由多个线程操作容易产生并发异常
		 * 
		 * @param latitude
		 * @param longiitude
		 */
		public static void saveGps(String latitude, String longiitude) {
			JSONObject obj = new JSONObject();// 构造数据
			try {
				jsonArr = new JSONArray();
				obj.put("lat", latitude);
				obj.put("lon", longiitude);
				jsonArr.put(obj);
				// 保存在本地
				Editor editor = mbasepre.edit();
				editor.putString(CoustomKey.SP_KEY_GPSINFO_ARRAY, jsonArr.toString());
				editor.commit();
			} catch (JSONException e) {
				Log.e("error", e.getMessage());
			}
		}

		/**
		 * 获取到最新的定位坐标 返回的是一个数组 如果没有返回0.0，0.0 arr[0]=经度 arr[1]=纬度
		 * 
		 * @return
		 */
		public static double[] getGps() {
			double arr[] = new double[2];
			if (mbasepre.contains(CoustomKey.SP_KEY_GPSINFO_ARRAY)) {
				String location = mbasepre.getString(CoustomKey.SP_KEY_GPSINFO_ARRAY, null);
				if (!TextUtil.isEmpty(location)) {
					try {
						JSONArray jsonArray = new JSONArray(location);
						JSONObject jsonObject = (org.json.JSONObject) jsonArray.get(jsonArray.length() - 1);
						String lats = "";
						String lons = "";
						if (jsonObject != null) {
							lats = jsonObject.getString("lat");
							lons = jsonObject.getString("lon");
						}
						if (TextUtil.isEmpty(lats) || TextUtil.isEmpty(lons)) {
							lats = 0.0 + "";
							lons = 0.0 + "";
						}
						arr[0] = Double.valueOf(lons);
						arr[1] = Double.valueOf(lats);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return arr;
		}
	}

	public static class SettingINF {
		public static final SharedPreferences sp = GlobalApplication.mContext.getSharedPreferences(CoustomKey.NAME_SETTING,
				Activity.MODE_PRIVATE);

		public static String getString(String key) {
			if (sp == null)
				return null;
			String value = null;
			if (sp.contains(key)) {
				value = sp.getString(key, "");
			}
			if (TextUtil.isEmpty(value)) {
				return null;
			}
			return value;
		}

	}

	public static class TAG {
		public static final SharedPreferences sp = GlobalApplication.mContext.getSharedPreferences(CoustomKey.NAME_TAG,
				Activity.MODE_PRIVATE);

		public static String getString(String key) {
			if (sp == null)
				return null;
			String value = sp.getString(key, "");
			if (TextUtil.isEmpty(value)) {
				return null;
			}
			return value;
		}

		public static boolean getBoolean(String key, boolean defaults) {
			if (sp.contains(key)) {
				boolean boolean1 = sp.getBoolean(key, defaults);
				return boolean1;
			}
			return defaults;
		}

		public static void setBoolean(String key, boolean val) {
			Editor edit = sp.edit();
			edit.putBoolean(key, val);
			edit.commit();
		}
	}

	// 每一个key都保存了一个时间
	public static class DateConfig {
		private static LDateSharedPreferce mDateSharedPreferce;

		private static String name;

		public static synchronized LDateSharedPreferce getInstance(String name) {
			if (!name.equals(DateConfig.name)) {
				if (mDateSharedPreferce != null) {
					mDateSharedPreferce.unregisterOnSharedPreferenceChangeListener(null);
				}
				mDateSharedPreferce = null;
			}
			DateConfig.name = name;
			if (mDateSharedPreferce == null) {
				mDateSharedPreferce = new LDateSharedPreferce(GlobalApplication.mContext, DateConfig.name,
						Activity.MODE_PRIVATE);
			}
			return mDateSharedPreferce;
		}

	}
}
