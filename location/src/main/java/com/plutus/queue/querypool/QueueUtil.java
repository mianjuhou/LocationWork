package com.plutus.queue.querypool;

import android.content.Context;
import com.plutus.queue.querypool.Request;
public class QueueUtil {
	private static RequestQueue mQueue = null;
	private static final int threadSize=4;

	public static void add(RequestQueue queu, Request request) {
		queu.add(request);
	}

	public static RequestQueue getRequestQueue(Context c) {
		boolean isQuite = false;
		if (mQueue == null) {
			mQueue = Vollery.newRequestQueue(c,threadSize);
		} else {
			DispatchQuene[] disPatchThread = mQueue.getDisPatchThread();
			for (int i = 0; i < disPatchThread.length; i++) {
				boolean quit = disPatchThread[i].isQuit();
				isQuite = quit;

				if (!quit) {// 有一个线程没有退出
					break;
				}

			}
			disPatchThread = null;
		}
		if (isQuite) {// 退出了
			mQueue = Vollery.newRequestQueue(c,threadSize);
		}
		return mQueue;
	}
}
