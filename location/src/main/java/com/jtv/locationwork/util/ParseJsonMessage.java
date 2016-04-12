package com.jtv.locationwork.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.locationwork.util.Constants;

public class ParseJsonMessage {
	protected String date;

	protected String id;

	protected String value;

	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public ParseJsonMessage(String date) {
		this.date = date;
		try {
			parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parse(String date) {
		JSONObject mJson = null;
		try {
			mJson = new JSONObject(date);
			boolean has = mJson.has(Constants.MESSAGEID);
			boolean hasValue = mJson.has(Constants.MESSAGEID_VALUE);
			if (has) {
				id = mJson.optString(Constants.MESSAGEID);
			}
			if (hasValue) {
				value = mJson.optString(Constants.MESSAGEID_VALUE);
			}
		} catch (JSONException e) {
		}
	}

}
