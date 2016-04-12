package com.jtv.locationwork.httputil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.locationwork.util.TextUtil;

import android.os.Bundle;
import android.text.TextUtils;

public class ParseJson {

	public static boolean parseOk(String response) {
		if (TextUtil.isEmpty(response)) {
			return false;
		}

		try {
			int status = Integer.valueOf(response);
			if (status == 1) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;

	}

	public static boolean parseStatusSuccessful(String value) {
		JSONObject jsonObject2;
		try {
			jsonObject2 = new JSONObject(value);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return parseStatusSuccessful(jsonObject2);
	}

	/**
	 * 解析状态码是否正确{"status":"true"}
	 * 
	 * @param json
	 * @return
	 */
	public static boolean parseStatusSuccessful(JSONObject jsonObject2) {
		boolean flag = false;
		if (jsonObject2 == null) {
			return flag;
		}
		if (TextUtil.isEmpty(jsonObject2.optString("status")))
			return flag;
		String status = jsonObject2.optString("status");
		if (TextUtil.isEmpty(status))
			return false;
		if (TextUtils.equals("true", status)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 解析视频
	 * 
	 * @param JSONObject
	 * @return
	 */
	public static Bundle parseUploadVideo(JSONObject JSONObject) {
		Bundle bundle = new Bundle();
		boolean parseStatusSuccessful = parseStatusSuccessful(JSONObject);
		if (parseStatusSuccessful) {
			bundle.putBoolean("status", true);
			String optString = JSONObject.optString("errmsg");
			bundle.putString("message", optString);
		} else {
			bundle.putBoolean("status", false);
			String optString = JSONObject.optString("errmsg");
			bundle.putString("message", optString);
		}
		return bundle;
	}

	public static boolean parseSuccess(JSONArray json) {
		if (json != null) {
			String string = json.toString();

			if ("".equals(string) || string == null || "[]".equals(string)) {
				return false;
			}
		}
		return false;
	}

	public static boolean parseSuccess(String result) {

		if ("".equals(result) || result == null || "-1".equals(result)) {
			return false;
		}

		return true;
	}

	public static boolean parseSuccess(JSONObject json) {
		if (json != null) {
			String string = json.toString();

			if ("".equals(string) || string == null || "{}".equals(string)) {
				return false;
			}
		}
		return false;
	}

	public static boolean parseJsonObjectSuccess(String result) {
		boolean parseSuccess = parseSuccess(result);

		if (parseSuccess) {
			try {
				new JSONObject(result);
			} catch (JSONException e) {
				e.printStackTrace();
				parseSuccess = false;
			}

		}
		return parseSuccess;
	}

	public static boolean parseJsonArraySuccess(String result) {
		boolean parseSuccess = parseSuccess(result);

		if (parseSuccess) {
			try {
				new JSONArray(result);
			} catch (JSONException e) {
				e.printStackTrace();
				parseSuccess = false;
			}

		}

		return parseSuccess;
	}

	public static boolean parseHasJsonData(String result) {
		String startFlag = "";
		String endFlag = "";

		if (result.startsWith("[")) {
			startFlag = "[";
		}

		if (result.startsWith("{")) {
			startFlag = "{";
		}

		if (result.endsWith("]")) {
			endFlag = "]";
		}

		if (result.endsWith("}")) {
			endFlag = "}";
		}

		String total = startFlag + endFlag;

		if (result.equals(total)) {// 没有json数据
			return false;
		}

		return true;
	}

}
