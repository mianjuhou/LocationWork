package com.jtv.locationwork.util;

import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.locationwork.httputil.AnsynHttpRequest;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;

import android.content.Context;

public class DownloadFace implements ObserverCallBack {

	/**
	 * 下载一个人的信息
	 * 
	 * @param con
	 * @param lead
	 */
	public void downloadPerson(Context con, String lead) {
		ShareUtil.downloadFace(con, this, lead);
	}

	@Override
	public void back(String data, int method, Object obj) {
		switch (method) {
		case MethodApi.HTTP_DOWNLOAD_FACE:
			ShareUtil.insertFace(data, GlobalApplication.departid);
			break;
		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {
	}

}
