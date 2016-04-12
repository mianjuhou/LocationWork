package com.jtv.locationwork.httputil;

//method使用在同一个activity中请求后台参数过多的情况判断下使用。
public interface ObserverCallBack {
	/**
	 * 
	 * @param data
	 *            服务端的信息
	 * @param encoding
	 *            响应状态
	 * @param method
	 *            请求的接口
	 * @param obj
	 *            附带值
	 */
	public void back(String data, int method, Object obj);

	public void badBack(String error, int method, Object obj);
}