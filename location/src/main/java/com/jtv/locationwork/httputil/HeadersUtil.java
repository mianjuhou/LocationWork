package com.jtv.locationwork.httputil;

import okhttp3.Headers;

/**
 * okhttp and webservices 的网络缓存头
 * <p>
 * 设置网络在多少秒之内不去请求网络，使用缓存
 * <p>
 * 当没有网络的时候，使用缓存
 * 
 * @author 更生
 * @version 2016/2/24
 *
 */
public class HeadersUtil {

	public static final String QUERY_CACHE = "QUERY_FOR_CACHE";// 查询
	public static final String SUBMIT = "SUBMIT";// 提交

	/**
	 * 
	 * 在240秒之内读取缓存，没有网在四周之内读取缓存
	 * 
	 */
	public static Headers defaultHead() {

		// 单位秒
		// Headers of = Headers.of("Cache-Control", "max-age= 0,
		// max-stale=1296000");// 两周
		// Headers of = Headers.of("Cache-Control", " only-if-cached,
		// max-stale=1296000");
		// "Cache-Control", "no-cache" 强制刷新
		// "Cache-Control", "only-if-cached" 强制使用缓存响应
		// "Cache-Control", "max-stale=" + maxStale 缓存过时的时间
		// max-age 验证缓存数据是否有效
		// Headers of = Headers.of("Cache-Control",
		// "max-age=240,max-stale=1296000");

		return defaultHead(300);
	}

	/**
	 * 当没有连接网络的时候，使用网络数据，网络数据默认保存四周
	 * 
	 * @param seconds 多少秒之内不请求网络，使用缓存
	 * @return
	 */
	public static Headers defaultHead(int seconds) {
		Headers of = Headers.of("Cache-Control", "max-age=" + seconds + ",max-stale=1296000");
		return of;
	}

}
