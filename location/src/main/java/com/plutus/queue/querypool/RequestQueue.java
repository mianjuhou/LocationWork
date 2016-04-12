package com.plutus.queue.querypool;

import java.util.concurrent.BlockingQueue;

import android.annotation.SuppressLint;

public class RequestQueue {
	private BlockingQueue<Request> que;

	protected int[] mThreadSize = null;

	protected DispatchQuene[] mDis = null;

	protected PostResponse post = null;

	public RequestQueue(BlockingQueue<Request> que, int threadSize) {
		mThreadSize = new int[threadSize];
		this.que = que;
	}

	public RequestQueue(BlockingQueue<Request> que, int threadSize, PostResponse post) {
		this.que = que;
		mThreadSize = new int[threadSize];
		this.post = post;
	}

	public void start() {
		stop();
		mDis = new DispatchQuene[mThreadSize.length];
		for (int i = 0; i < mThreadSize.length; i++) {
			DispatchQuene dispatchQuene = new DispatchQuene(que, this, post);
			mDis[i] = dispatchQuene;
			dispatchQuene.start();
		}
	}

	public void stop() {

		if (mDis != null) {

			for (int i = 0; i < mDis.length; i++) {

				DispatchQuene dispatchQuene = mDis[i];

				if (dispatchQuene != null)
					dispatchQuene.quit();
			}
		}
	}

	public DispatchQuene[] getDisPatchThread() {
		DispatchQuene[] mCurrdis =	new DispatchQuene[mDis.length];
		for (int i = 0; i < mDis.length; i++) {
			mCurrdis[i]=mDis[i];
		}
		return mCurrdis;
	}

	@SuppressLint("NewApi")
	public void add(Request response) {
		// que.put(response);
		que.add(response);
	}
}
