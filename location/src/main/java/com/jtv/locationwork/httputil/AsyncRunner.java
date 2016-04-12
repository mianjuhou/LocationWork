package com.jtv.locationwork.httputil;

import org.json.JSONException;

import com.jtv.locationwork.httputil.AsyncRunner.RequestListener;
import com.jtv.locationwork.util.NetUtil;
import com.jtv.locationwork.util.TextUtil;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author: zn E-mail:zhangn@jtv.com.cn
 * @version:2015-2-11(update) 异常接口方案,提供开始状态、运行完成及异常处理
 */
public class AsyncRunner {
	private static mHandler handler;
	private static Thread thread;
	public static Context con;

	static {
		handler = new mHandler();
	}

	public final static int DONE = 0x09810231;// 线程执行完毕
	public final static int READING = 0x09810232;// 线程执行完毕
	public final static int UNINTERNET = 0x09810233;// 访问不到网络
	public static final int FINAL = 0x09810235;// 始终会执行
	public static final Boolean isShowError = true;

	// 请求的监听接口
	public interface RequestListener {
		// 请求之前运行在主线程中
		public void onReading();

		// 请求完成处理数据,运行在子线程中
		public void onRequesting() throws Exception, JSONException;

		// 处理完成,运行在主线程中
		public void onDone();

		/** 没有网络时调用，如果没有网络不会访问走onRequesting,运行在主线程,在onReading之前调用 */
		public void onUnInternet();

		// 请求处理时的异常,运行在子线程中
		public void onAppError(Exception e);

		// 主线程中的handler
		public void onPostMessage(int what, Bundle bundle, String msessage, RequestListener me);

		// 当请求完毕的最后执行运行在主线程中,一定会执行
		public void onFinal();

	}

	public static void sendMessage(RequestListener listener, int what, String msg, Bundle bundle) {
		if (listener == null) {
			try {
				throw new Exception("空指针异常");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (handler != null) {
			handler.sendMessage(listener, bundle, what, msg);
		}
	}

	// 快速入口
	public static void HttpGet(final RequestListener listener) {
		if (!NetUtil.hasConnectedNetwork(AsyncRunner.con)) {
			// 判断当前线程是否是主线程
			if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
				listener.onUnInternet();
				listener.onFinal();
			} else {// 不是主线程需要在handler中执行
				if (handler != null) {
					Message obtain = Message.obtain();
					obtain.what = AsyncRunner.UNINTERNET;
					obtain.obj = listener;
					handler.sendMessage(obtain);
				}
			}
			return;
		}
		if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
			listener.onReading();
		} else {// 不是主线程需要在handler中执行
			if (handler != null) {
				Message obtain = Message.obtain();
				obtain.what = AsyncRunner.READING;
				obtain.obj = listener;
				handler.sendMessage(obtain);
			}
		}
		thread = new Thread() {

			@Override
			public void run() {
				// Looper.prepare();
				try {
					listener.onRequesting();
					if (handler != null) {
						Message obtain = Message.obtain();
						obtain.what = DONE;
						obtain.obj = listener;
						handler.sendMessage(obtain);
					}
				} catch (Throwable e) {
					if (isShowError) {
						listener.onAppError(new Exception("请求解析异常:" + e.getMessage()));
					}
				} finally {
					if (handler != null) {
						Message obtain = Message.obtain();
						obtain.what = FINAL;
						obtain.obj = listener;
						handler.sendMessage(obtain);
					}
				}
				// Looper.loop();
			}
		};
		thread.start();
	}
}

class mHandler extends Handler {
	public void sendMessage(final RequestListener listener, Bundle bundle, int what, String msg) {
		Message obtain = Message.obtain();
		obtain.obj = listener;
		obtain.what = what;
		if (bundle != null && !TextUtil.isEmpty(msg)) {
			bundle.putString("MESSAGEHANDLER", msg);
		} else if (bundle == null && !TextUtil.isEmpty(msg)) {
			bundle = new Bundle();
			bundle.putString("MESSAGEHANDLER", msg);
		}
		if (bundle != null) {
			obtain.setData(bundle);
		}
		this.sendMessage(obtain);
	}

	public void handleMessage(android.os.Message msg) {

		if (msg.what == AsyncRunner.DONE) {// 代表执行完毕了
			RequestListener listener = null;
			if (msg.obj instanceof RequestListener) {
				listener = (RequestListener) msg.obj;
			}
			if (listener != null) {
				listener.onDone();
			}
		} else if (msg.what == AsyncRunner.READING) {
			RequestListener listener = null;
			if (msg.obj instanceof RequestListener) {
				listener = (RequestListener) msg.obj;
			}
			if (listener != null) {
				listener.onReading();
			}
		} else if (msg.what == AsyncRunner.FINAL) {
			RequestListener listener = null;
			if (msg.obj instanceof RequestListener) {
				listener = (RequestListener) msg.obj;
			}
			if (listener != null) {
				listener.onFinal();
			}
		} else if (msg.what == AsyncRunner.UNINTERNET) {
			RequestListener listener = null;
			if (msg.obj instanceof RequestListener) {
				listener = (RequestListener) msg.obj;
			}
			if (listener != null) {
				listener.onUnInternet();
				listener.onFinal();
			}
		} else {// 代表自己的吐司
			RequestListener listener = null;
			if (msg.obj instanceof RequestListener) {
				listener = (RequestListener) msg.obj;
			}
			String mes = null;
			if (listener != null) {
				int what = msg.what;
				Bundle data = msg.getData();
				if (data != null) {
					if (data.containsKey("MESSAGEHANDLER")) {
						mes = data.getString("MESSAGEHANDLER");
					}
				}
				listener.onPostMessage(what, data, mes, listener);
			}
		}
	};
}