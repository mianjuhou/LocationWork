package com.jtv.locationwork.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;

public class JsonUtil {

	public static String parseString(Object t) {
		String value = null;
		if (t != null)
			value = JSON.toJSONString(t);
		return value;
	}

	// 如果需要大写改为public
	public static String parseString(Object t, String... filterType) {
		SimplePropertyPreFilter filter = null;
		if (filterType != null) {
			Set<String> excludes = new HashSet<String>();
			filter = new com.jtv.locationwork.util.SimplePropertyPreFilter();
			for (String string : filterType) {
				excludes.add(string);
			}
			filter.setExcludes(excludes);
		}

		String value = null;
		if (t != null && filter != null)
			value = JSON.toJSONString(t, filter);
		else
			value = JSON.toJSONString(t);
		return value;
	}

	public static <T> List<T> parseArrayObject(String data, Class<T> t) {
		try {
			List<T> parseArray = JSON.parseArray(data, t);
			return parseArray;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static <T> T parseObject(String data, Class<T> t) {
		try {
			T parseArray = JSON.parseObject(data, t);
			return parseArray;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static JSONObject parseJSONObject(Object t) {
		String value = parseString(t);
		JSONObject parseObject = null;
		if (!"".equals(value) && value != null)
			parseObject = JSON.parseObject(value);
		return parseObject;
	}

	public static JSONObject parseJSONObject(String t) {
		JSONObject parseObject = null;
		if (!"".equals(t) && t != null)
			parseObject = JSON.parseObject(t);
		return parseObject;
	}

	public static JSONArray parseArray(String data) {
		return JSON.parseArray(data);
	}

	public static JSONArray productJSONArray(JSONObject json) {
		JSONArray jsonArray = new JSONArray();
		if (json != null)
			jsonArray.add(json);
		return jsonArray;
	}

	public static String productJSONArrays(JSONObject json) {
		JSONArray productJSONArray = productJSONArray(json);
		return productJSONArray.toString();
	}

	// 移除一个jsonarray index
	@SuppressLint("NewApi")
	public static void removeJsonArrayIndex(int index, org.json.JSONArray mJson) throws Exception {

		if (VERSION.SDK_INT >= 19) {
			mJson.remove(index);
		} else {
			if (index < 0)
				return;
			Field valuesField = JSONArray.class.getDeclaredField("values");
			valuesField.setAccessible(true);
			List<Object> values = (List<Object>) valuesField.get(mJson);
			if (index >= values.size())
				return;
			values.remove(index);
		}
	}
}
