package com.jtv.locationwork.httputil;

import java.io.File;
import java.util.Map;

//利用httpclient上传文件
public class AnsyHttpRunner extends AnsyHttp {
	protected String url;
	protected ObserverCallBack back;
	protected String endpoint;
	protected int method = 0;
	protected Object obj = null;
	private Map<String, File> files;
	private Map<String, String> params2;

	public AnsyHttpRunner(String url, Map<String, File> files, Map<String, String> params2, ObserverCallBack back,
			int method) {
		this.back = back;
		this.url = url;
		this.method = method;
		this.files = files;
		this.params2 = params2;
	}

	public void seteObj(Object obj) {
		this.obj = obj;
	}

	@Override
	protected Object doInBackground(Object... params) {
		try {
			return HttpUtil.httpPOST(url, params2, files);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
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
