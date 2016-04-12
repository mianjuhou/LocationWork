package com.plutus.queue.querypool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class Vollery {
	/** 默认的缓存目录 */
	private static final String DEFAULT_CACHE_DIR = "volley";

	/**
	 * 创建一个默认的队列{@link RequestQueue#start()} on it.
	 *
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @param stack
	 *            An {@link HttpStack} to use for the network, or null for default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context, BlockingQueue<Request> que, int size) {
		return newRequestQueue(context, que, size, false);
	}

	public static RequestQueue newRequestQueue(Context con, BlockingQueue<Request> que, int size,
			boolean isPostResponse) {

		if (que == null) {
			que = new PriorityBlockingQueue<Request>();
		}
		if (size < 1) {
			size = 0;
		}
		RequestQueue queue = null;
		if (isPostResponse) {
			queue = new RequestQueue(que, size, new PostResponseDelivery(new Handler(Looper.getMainLooper())));
		} else {
			queue = new RequestQueue(que, size, null);
		}
		queue.start();

		return queue;

	}

	/**
	 * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
	 *
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context) {
		return newRequestQueue(context, null, 1);
	}

	public static RequestQueue newRequestQueue(Context context, int size) {
		if (size < 1) {
			size = 1;
		}
		return newRequestQueue(context, null, size);
	}
}
