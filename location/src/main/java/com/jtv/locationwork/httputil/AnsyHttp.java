package com.jtv.locationwork.httputil;

import android.os.AsyncTask;

public class AnsyHttp extends AsyncTask{
	public static final int POST = 1; // post 提交
	public static final int GET = 2; // get 提交
	public static final int SUCCESS_HTTP = 200;// 成功
	public static final int FAILURE_HTTP = 400;// 失败
	
	@Override
	protected Object doInBackground(Object... params) {
		//子线程
		return null;
	}

}
