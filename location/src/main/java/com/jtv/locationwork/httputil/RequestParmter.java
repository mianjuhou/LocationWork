package com.jtv.locationwork.httputil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RequestParmter {

	public Map<String, Object> body = new HashMap<String, Object>();

	public void addBodyParmter(String key, String vlaue) {
		body.put(key, vlaue);
	}

	public void addBodyParmter(String key, File vlaue) {
		body.put(key, vlaue);
	}

	public void put(String key, String vlaue) {
		addBodyParmter(key, vlaue);
	}

	public void put(String key, File vlaue) {
		addBodyParmter(key, vlaue);
	}
}
