package com.jtv.locationwork.httputil;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jtv.base.util.CollectionActivity;
import com.jtv.base.util.UToast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

/**
 * 异步数据请求
 */
public class AnsynHttpRequest {
	public static final int POST = 1; // post 提交
	public static final int GET = 2; // get 提交
	public static final int SUCCESS_HTTP = 200;// 成功
	public static final int FAILURE_HTTP = 400;// 失败

	public static void requestGet(String url, ObserverCallBack callBack, int method, Context con, boolean iscache) {
		requestGet(url, callBack, method, con, null, iscache);
	}

	public static void requestGet(String url, ObserverCallBack callBack, int method, Context con, Object obj,
			boolean iscache) {
		requestGet(url, callBack, method, con, obj, getRequestQueue(con), iscache);
	}

	public static void requestGet(String url, ObserverCallBack callBack, int method, Context con, Object obj,
			RequestQueue quenue, boolean iscache) {
		requestGetOrPost(AnsynHttpRequest.GET, con, url, null, callBack, quenue, method, obj, null, iscache);
	}

	public static void requestPost(String url, ObserverCallBack callBack, int method, Context con,
			Map<String, String> map, boolean iscache) {
		requestPost(url, callBack, method, con, null, map, iscache);
	}

	public static void requestPost(String url, ObserverCallBack callBack, int method, Context con, Object obj,
			Map<String, String> map, boolean iscache) {
		requestPost(url, callBack, method, con, obj, getRequestQueue(con), map, iscache);
	}

	public static void requestPost(String url, ObserverCallBack callBack, int method, Context con, Object obj,
			RequestQueue quenue, Map<String, String> map, boolean iscache) {
		requestGetOrPost(AnsynHttpRequest.POST, con, url, map, callBack, quenue, method, obj, null, iscache);
	}

	/***
	 * get和post请求方法
	 * 
	 * @param sendType
	 *            请求类型：get和post
	 * @param context
	 *            上下文
	 * @param url
	 *            请求地址
	 * @param map
	 *            post使用到的
	 * @param callBack
	 *            异步回调
	 * @param mQueue
	 *            volly最终请求类
	 * @param i
	 *            请求的方法对应的int值（整个项目中唯一不重复的）
	 * @param obj
	 *            此参数用于一些http请求结果回调里,需要请求前传递一些参数的方法.不需要时,可以传空,不处理即可.
	 */
	public static void requestGetOrPost(final int sendType, final Context context, String url,
			final Map<String, String> map, final ObserverCallBack callBack, RequestQueue mQueue, final int i,
			final Object obj) {
		requestGetOrPost(sendType, context, url, map, callBack, mQueue, i, obj, null, true);
	}

	/**
	 * 网络连接失败的时候有Toast
	 * 
	 * @param sendType
	 * @param context
	 * @param url
	 * @param map
	 * @param callBack
	 * @param mQueue
	 * @param i
	 * @param obj
	 */
	public static void requestGetOrPostE(final int sendType, final Context context, String url,
			final Map<String, String> map, final ObserverCallBack callBack, RequestQueue mQueue, final int i,
			final Object obj, boolean iscache) {
		requestGetOrPost(sendType, context, url, map, callBack, mQueue, i, obj, "网络连接失败", iscache);
	}

	/**
	 * 网络连接失败的时候有自定义Toast
	 * 
	 * @param sendType
	 * @param context
	 * @param url
	 * @param map
	 * @param callBack
	 * @param mQueue
	 * @param i
	 * @param obj
	 */
	public static void requestGetOrPost(final int sendType, final Context context, final String url,
			final Map<String, String> map, final ObserverCallBack callBack, RequestQueue mQueue, final int i,
			final Object obj, final String errorToast, final boolean isAllowCache) {

		// url = Utf8URLencode(url);
		// LogUtil.i("url : ", url + " 请求方法: " + i);
		switch (sendType) {
		case POST:
			StringRequest stringRequest = new StringRequest(Method.POST, url, new Response.Listener<String>() {// 成功回调
				@Override
				public void onResponse(String response) {
					boolean responseException = responseException(response);

					if (responseException) {
						if (callBack != null) {
							callBack.badBack(response, i, obj);
						}

					} else {
						if (callBack != null) {
							callBack.back(response, i, obj);
						}
					}

				}
			}, new Response.ErrorListener() { // 请求失败
				@Override
				public void onErrorResponse(VolleyError error) {

					if (isAllowCache) {
						// 从本地获取数据
						String fromDiskCache = AnsynHttpRequest.getFromDiskCache(url);

						if (!TextUtils.isEmpty(fromDiskCache)) {// 从本地获取
							if (callBack != null) {
								callBack.back(fromDiskCache, i, obj);
							}

							return;
						}
					}

					if (!TextUtils.isEmpty(errorToast)) {
						UToast.makeShortTxt(context, errorToast);
					}
					if (callBack != null) {
						callBack.badBack((error == null) ? null : error.toString(), i, obj);

					}
				}
			}) {
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					return map;
				}

				@Override
				protected Response<String> parseNetworkResponse(NetworkResponse response) {
					try {
						String jsonString = new String(response.data, "UTF-8");
						return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
					} catch (UnsupportedEncodingException e) {
						return Response.error(new ParseError(e));
					} catch (Exception je) {
						return Response.error(new ParseError(je));
					}
				}
			};
			mQueue.add(stringRequest);
			break;
		case GET:
			Response.Listener<String> listener = new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					boolean responseException = responseException(response);

					if (responseException) {
						if (callBack != null) {
							callBack.badBack(response, i, obj);
						}

					} else {
						if (callBack != null) {
							callBack.back(response, i, obj);
						}
					}

				}
			};
			Response.ErrorListener errorListener = new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {

					if (isAllowCache) {
						// 从本地获取数据
						String fromDiskCache = AnsynHttpRequest.getFromDiskCache(url);

						if (!TextUtils.isEmpty(fromDiskCache)) {// 从本地获取
							callBack.back(fromDiskCache, i, obj);
							return;
						}
					}

					Log.e("GET: error  ", (error == null) ? null : error.toString());
					if (!TextUtils.isEmpty(errorToast)) {
						UToast.makeShortTxt(context, errorToast);
					}

					if (callBack != null) {
						callBack.badBack((error == null) ? null : error.toString(), i, obj);
					}
				}
			};
			StringRequest stringRequest2 = new StringRequest(url, listener, errorListener) {
				@Override
				protected Response<String> parseNetworkResponse(NetworkResponse response) {
					try {
						String jsonString = new String(response.data, "UTF-8");
						return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
					} catch (UnsupportedEncodingException e) {
						return Response.error(new ParseError(e));
					} catch (Exception je) {
						return Response.error(new ParseError(je));
					}
				}
			};
			mQueue.add(stringRequest2);
			break;
		default:
			break;
		}
	}

	/**
	 * Utf8URL编码
	 * 
	 * @param s
	 * @return
	 */
	public static String Utf8URLencode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= 0 && c <= 255) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}

	public static void readBitmapViaVolley(String imgUrl, final ImageView imageView, RequestQueue mQueue) {
		ImageRequest imgRequest = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap arg0) {
				imageView.setBackgroundResource(0);
				imageView.setImageBitmap(arg0);
			}
		}, 300, 200, Config.ARGB_8888, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {

			}
		});
		mQueue.add(imgRequest);
	}

	public static class BitmapCache implements ImageCache {
		private LruCache<String, Bitmap> mCache;

		public BitmapCache() {
			int maxSize = 10 * 1024 * 1024;
			mCache = new LruCache<String, Bitmap>(maxSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getRowBytes() * value.getHeight();
				}

			};
		}

		@Override
		public Bitmap getBitmap(String url) {
			return mCache.get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			mCache.put(url, bitmap);
		}

	}

	/**
	 * 后台响应错误数据
	 * 
	 * @param str
	 * @return
	 */
	private static boolean responseException(String str) {
		if (str != null && str.length() > 40 && str.contains("<title>系统提示</title>")) {
			return true;
		}
		return false;
	}

	/**
	 * 从本地读取数据
	 * 
	 * @param url
	 * @return
	 */
	public static String getFromDiskCache(String url) {
		if (mQueue == null) {
			mQueue = getRequestQueue(CollectionActivity.getTopActivity());
		}
		Cache cache = mQueue.getCache();

		if (cache == null) {
			return null;
		}

		if (cache.get(url) != null) {
			try {
				Entry entry = mQueue.getCache().get(url);

				// boolean expired = entry.isExpired();//设置缓存时间为一个小时
				// HttpHeaderParser 中可以修改
				// if(expired){
				// return null;
				// }
				String str = new String((entry.data));

				if (responseException(str)) {
					return null;
				}

				return str;
				// ……（省略操作）
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		}
		return null;
	}

	private static RequestQueue mQueue;

	public static RequestQueue getRequestQueue(Context c) {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(c);
		}
		return mQueue;
	}
}
