package com.jtv.locationwork.httputil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.locationwork.util.LogUtil;
import com.jtv.locationwork.util.NetUtil;
import com.jtv.locationwork.util.StringUtil;

import android.content.Context;
import android.text.TextUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * okhttp 访问网络的工具类
 * <p>
 *
 * @author 更生
 * @version 2016年2月25日
 */
public class OkHttpUtil {

	private static OkHttpClient mOkHttpClient;
	private static Cache cache;

	static {
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.connectTimeout(30, TimeUnit.SECONDS);// 设置连接超时12秒
		clientBuilder.writeTimeout(100, TimeUnit.SECONDS);
		clientBuilder.readTimeout(60*5, TimeUnit.SECONDS);
		int cachesize = 10 * 1024 * 100;
		File dir = GlobalApplication.mContext.getDir("http_cache", Context.MODE_PRIVATE);
		cache = new Cache(dir, cachesize);
		clientBuilder.cache(cache);
		clientBuilder.interceptors().add(new CacheApplicationInterceptor());
		clientBuilder.addNetworkInterceptor(new CacheNetWorkInterceptor());
		mOkHttpClient = clientBuilder.build();
		// Response response = build.cache().get(new );
	}

	// 网络缓存
	static class CacheNetWorkInterceptor implements Interceptor {

		@Override
		public Response intercept(Chain chain) throws IOException {
			Request request = chain.request();
			LogUtil.i("[ ok-http method --> " + request.url() + " ] 访问网络中: ");

			Response proceed = chain.proceed(request);
			if (proceed == null) {
				LogUtil.i("[ ok-http method --> " + request.url() + " ] 网络获取数据失败: ");
			}

			LogUtil.i("[ ok-http method --> " + request.url() + " ] 网络获取数据结束: ");

			return proceed;
		}

	}

	// 缓存拦截
	static class CacheApplicationInterceptor implements Interceptor {

		@Override
		public Response intercept(Chain chain) throws IOException {
			Request request = chain.request();
			String control = null;

			try {
				Headers headers = request.headers();
				control = headers.get("Cache-Control");

				if (!TextUtils.isEmpty(control)) {
					control = StringUtil.replaceBlank(control);
					Pattern compile = Pattern.compile("max-age=\\d{1,}");
					Matcher matcher = compile.matcher(control);
					matcher.find();
					String max = matcher.group();
					String age = max.replace("max-age=", "");

					int maxAge = Integer.valueOf(age);

					String md5Hex = Util.md5Hex(request.url().toString());
					String end = ".1";// 官方格式

					File directory = cache.directory();
					String path = md5Hex + end;
					File file = new File(directory, path);

					if (file != null && file.exists() && maxAge > 0) {
						long lastModified = file.lastModified();
						long currentThreadTimeMillis = System.currentTimeMillis();

						if (currentThreadTimeMillis - lastModified < maxAge * 1000) {// 换算等量单位毫秒
							Response res = getCacheResponse(request);

							if (res != null) {
								LogUtil.i("[ ok-http method --> " + request.url() + " ] 不访问网络-使用缓存: ");
								return res;
							}

						} else {// 超过了这个网络需要联网更新
							request = request.newBuilder().addHeader("Cache-Control", "no-cache").build();
						}
					}
				}
			} catch (Exception e) {
				LogUtil.w(e);// 当没有缓存头时，会报异常，可以不用关注
			}

			try {
				if (!NetUtil.hasConnectedNetwork(GlobalApplication.mContext)) {
					Pattern compile = Pattern.compile("max-stale=\\d{1,}");
					Matcher matcher = compile.matcher(control);
					matcher.find();
					String max = matcher.group();
					String stale = max.replace("max-stale=", "");
					int staleTime = Integer.valueOf(stale);

					if (staleTime > 10000) {// 意思是允许没网获取缓存
						Response res = getCacheResponse(request);
						if (res != null) {
							LogUtil.i("[ ok-http method --> " + request.url() + " ] 手机网络关闭-使用缓存: ");
							return res;
						}
					}

				}
			} catch (Exception e) {
				LogUtil.w(e);// 当没有缓存头时，会报异常，可以不用关注
			}

			return chain.proceed(request);
		}

		/**
		 * 从磁盘读取网络缓存
		 * 
		 * @param request 网络请求
		 * @return 如果有缓存响应缓存结果,没有返回null
		 */
		private Response getCacheResponse(Request request) {
			Class<? extends Cache> class1 = cache.getClass();
			Method method = null;
			try {
				method = class1.getDeclaredMethod("get", Request.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			method.setAccessible(true);
			Response res = null;
			try {
				res = (Response) method.invoke(cache, request);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return res;
		}

	}

	/**
	 * 该不会开启异步线程。
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static Response execute(Request request) throws IOException {
		return mOkHttpClient.newCall(request).execute();
	}

	/**
	 * 开启异步线程访问网络
	 * 
	 * @param request
	 * @param responseCallback
	 */
	public static void enqueue(Request request, Callback responseCallback) {
		mOkHttpClient.newCall(request).enqueue(responseCallback);
	}

	// head 可以用来做缓存
	public static void get(String url, ObserverCallBack obback, int method, Object obj) {
		OkCallBack okCallBack = new OkCallBack(obback, method, obj);
		okhttp3.Request.Builder build = creatBuilder();
		if (obj != null && obj instanceof Headers) {
			setCache((Headers) obj, build);
		}

		Request request = build.url(url).build();
		enqueue(request, okCallBack);
	}

	private static Request.Builder creatBuilder() {
		okhttp3.Request.Builder build = new Request.Builder();
		return build;
	}

	private static void setCache(Headers head, Request.Builder builder) {
		if (head == null || builder == null) {
			return;
		}
		CacheControl parse = CacheControl.parse(head);
		if (parse != null) {
			String value = parse.toString();
			if (!TextUtils.isEmpty(value))
				builder.header("Cache-Control", value);
		}
	}

	public static String getStringFromServer(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		Response response = execute(request);
		if (response.isSuccessful()) {
			String responseUrl = response.body().string();
			return responseUrl;
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}

	/**
	 * 为HttpGet 的 url 方便的添加1个name value 参数。
	 * 
	 * @param url
	 * @param name
	 * @param value
	 * @return
	 */
	public static String attachHttpGetParam(String url, String name, String value) {
		return url + "?" + name + "=" + value;
	}

	public static void download(String url, File file, ObserverCallBack back, int method, Object obj) {
		okhttp3.Request.Builder build = creatBuilder();
		if (obj != null && obj instanceof Headers) {
			setCache((Headers) obj, build);
		}
		final Request request = build.url(url).build();
		FileOkCallBack fileOkCallBack = new FileOkCallBack(file, back, method, obj);
		enqueue(request, fileOkCallBack);
	}

	/** 堵塞的get请求 */
	public static String getStringFromServer(String url, Map<String, String> parmter) throws IOException {
		String content = "";
		Set<String> keySet = parmter.keySet();
		int i = 0;
		String start = "?";

		for (String key : keySet) {
			if (i == 0) {
				if (url.contains("?")) {
					start = "&";
				}
				content = content + key + "=" + parmter.get(key);
			} else {
				content = content + "&" + key + "=" + parmter.get(key);
			}

			i++;
		}

		url = url + start + content;

		return getStringFromServer(url);
	}

	private static String guessMimeType(String path) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String contentTypeFor = fileNameMap.getContentTypeFor(path);
		if (contentTypeFor == null) {
			contentTypeFor = "application/octet-stream";
		}
		return contentTypeFor;
	}

	public static void post(String url, File file, ObserverCallBack back, int method, Object obj) {
		RequestBody create = RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file);
		okhttp3.Request.Builder build = creatBuilder();
		if (obj != null && obj instanceof Headers) {
			setCache((Headers) obj, build);
		}

		Request request = build.url(url).post(create).build();
		OkCallBack okCallBack = new OkCallBack(back, method, obj);
		enqueue(request, okCallBack);
	}

	/**
	 * 堵塞的方式上传文件
	 * 
	 * @param url
	 * @param body
	 * @return
	 */
	public static String post(String url, RequestParmter body) {

		okhttp3.Request.Builder build = creatBuilder();

		// 构造上传请求，类似web表单
		okhttp3.MultipartBody.Builder type = new MultipartBody.Builder();

		Map<String, Object> body2 = body.body;
		Set<String> keySet = body2.keySet();
		RequestBody fileBody = null;
		for (String key : keySet) {
			Object object = body2.get(key);

			if (object != null) {
				if (object instanceof String) {

					type.addFormDataPart(key, (String) object);
					// type.addPart(Headers.of("Content-Disposition",
					// "form-data; name=\"" + key + "\""),
					// RequestBody.create(null, (String) object));

				} else if (object instanceof File) {
					File file = (File) object;
					fileBody = RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file);
					type.addPart(
							Headers.of("Content-Disposition",
									"form-data; name=\"" + key + "\"; filename=\"" + file.getName() + "\"\r\n"),
							fileBody);
					// type.addFormDataPart(key, file.getName(),
					// RequestBody.create(null, file));
				}
			}
		}

		RequestBody requestBody = type.build();
		Request request = build.url(url).post(requestBody).build();
		try {
			Response execute = execute(request);
			boolean successful = execute.isSuccessful();
			if (successful) {
				return execute.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void post(String url, RequestParmter body, ObserverCallBack back, int mehtod, Object obj) {

		okhttp3.Request.Builder build = creatBuilder();
		if (obj != null && obj instanceof Headers) {
			setCache((Headers) obj, build);
		}

		// 构造上传请求，类似web表单
		okhttp3.MultipartBody.Builder type = new MultipartBody.Builder();

		Map<String, Object> body2 = body.body;
		Set<String> keySet = body2.keySet();
		RequestBody fileBody = null;
		for (String key : keySet) {
			Object object = body2.get(key);

			if (object != null) {
				if (object instanceof String) {

					type.addFormDataPart(key, (String) object);
					// type.addPart(Headers.of("Content-Disposition",
					// "form-data; name=\"" + key + "\""),
					// RequestBody.create(null, (String) object));

				} else if (object instanceof File) {
					File file = (File) object;
					fileBody = RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file);
					type.addPart(
							Headers.of("Content-Disposition",
									"form-data; name=\"" + key + "\"; filename=\"" + file.getName() + "\"\r\n"),
							fileBody);
					// type.addFormDataPart(key, file.getName(),
					// RequestBody.create(null, file));
				}
			}
		}

		RequestBody requestBody = type.build();
		Request request = build.url(url).post(requestBody).build();
		OkCallBack okCallBack = new OkCallBack(back, mehtod, obj);
		enqueue(request, okCallBack);
	}

	// 堵塞的post请求
	public static String post(String url, Map<String, String> parmter) throws IOException {

		Builder type = new FormBody.Builder();
		Set<String> keySet = parmter.keySet();
		for (String key : keySet) {
			type.add(key, parmter.get(key));
		}

		RequestBody requestBody = type.build();
		Request request = creatBuilder().url(url).post(requestBody).build();
		Response execute = execute(request);

		if (execute.isSuccessful()) {
			return execute.body().string();
		} else {
			throw new IOException();
		}
	}

}