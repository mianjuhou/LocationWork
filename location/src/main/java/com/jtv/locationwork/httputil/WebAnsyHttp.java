package com.jtv.locationwork.httputil;

import java.io.File;

import org.ksoap2.serialization.SoapObject;

import com.jtv.locationwork.util.Cache.Entry;
import com.jtv.locationwork.util.DiskBasedCache;
import com.jtv.locationwork.util.DiskCacheUtil;
import com.jtv.locationwork.util.LogUtil;

import android.content.Context;
import android.text.TextUtils;
import okhttp3.Headers;

public class WebAnsyHttp extends AnsyHttp {
	protected SoapObject soap;
	protected ObserverCallBack back;
	protected String endpoint;
	protected int method = 0;
	protected Object obj = null;
	protected Context con;
	protected String url;
	private String methodName;

	public WebAnsyHttp(Context con, SoapObject soap, String endpoint, ObserverCallBack back, int method) {
		this.soap = soap;
		this.con = con;
		this.endpoint = endpoint;

		methodName = soap.getName();

		if (!TextUtils.isEmpty(endpoint) && con != null) {
			int count = soap.getPropertyCount();

			String parmter = "";
			for (int i = 0; i < count; i++) {
				Object property = soap.getProperty(i);
				parmter += property.toString();
			}

			this.url = endpoint + File.separator + methodName + File.separator + parmter;
		}

		this.back = back;
		this.method = method;
	}

	public void seteObj(Object obj) {
		this.obj = obj;
	}

	@Override
	protected Object doInBackground(Object... params) {
		LogUtil.i("[ web-services method --> " + methodName + " ] 请求网络中...");
		Entry entry = null;
		DiskBasedCache cache = DiskCacheUtil.getCache(con);
		int maxAge = -100;// 如果没有超过这个时间不允许请求网络，单位为秒
		int live = -100;// 缓存存活的生命周期，单位秒

		if (cache != null) {
			if (TextUtils.isEmpty(url)) {
			} else {
				entry = cache.get(url);
			}
		}

		if (con != null && obj != null && obj instanceof Headers) {// 解析缓存头
			Headers head = (Headers) obj;

			String value = head.get("Cache-Control");

			if (value != null && !"".equals(value)) {
				String[] split = value.split(",");
				if (split != null)
					for (int i = 0; i < split.length; i++) {
						String string = split[i];
						if (!TextUtils.isEmpty(string)) {
							String[] split2 = string.split("=");
							if (split2 != null) {
								for (int j = 0; j < split2.length; j++) {
									try {
										String item = split2[j];
										item = item.trim();
										Integer valueOf = Integer.valueOf(item);
										if (maxAge > 0) {
											live = valueOf;
										} else {
											maxAge = valueOf;
										}
									} catch (Exception e) {
									}
								}
							}
						}
					}
				if (maxAge > live) {
					int temp = live;
					live = maxAge;
					maxAge = temp;
				}
			}
		}

		String request = null;

		if (maxAge > 0 && cache != null) {
			File file = cache.getFileForKey(url);
			if (file != null) {// 如果请求没有过期，直接使用缓存
				long lastModified = file.lastModified();
				long currentThreadTimeMillis = System.currentTimeMillis();
				if (currentThreadTimeMillis - lastModified < maxAge * 1000) {// 换算等量单位毫秒
					if (entry != null) {
						byte[] data = entry.data;
						if (data != null) {
							request = new String(data);
							LogUtil.i("[ web-services method --> " + methodName + " ] 使用缓存结果: " + request);
							file = null;
							return request;
						}
						data = null;
					}
				}
			}
		}

		if (soap != null) {// 请求网络
			request = WebServiceUtil.request(endpoint, soap);
			LogUtil.i("[ web-services method --> " + methodName + " ] 访问网络结果: " + request);
		}

		// 是否需要缓存
		if (!TextUtils.isEmpty(request) && live > 0 && cache != null && !TextUtils.isEmpty(url)) {
			entry = DiskCacheUtil.getEntryForMills(request, live);
			cache.put(url, entry);
		} else if (entry != null) {// 从缓存读取数据
			if (entry != null && !entry.isExpired()) {
				byte[] data = entry.data;
				if (data != null) {
					request = new String(data);
					LogUtil.e("[ web-services method --> " + methodName + " ] 网络连接失败,使用本地: " + request);
				}
				data = null;
			}
		}

		return request;
	}

	@Override
	protected void onPostExecute(Object result) {
		if (result == null) {
			if (back != null) {
				back.badBack((result != null) ? result.toString() : "", method, obj);
			}
		} else {
			if (back != null) {
				back.back((result != null) ? result.toString() : "", method, obj);
			}
		}

	}
}
