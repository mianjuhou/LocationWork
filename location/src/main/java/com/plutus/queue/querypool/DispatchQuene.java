package com.plutus.queue.querypool;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import android.annotation.SuppressLint;
import android.os.Process;

@SuppressLint("NewApi")
public class DispatchQuene extends Thread {
	protected BlockingQueue<Request> queue;

	protected RequestQueue request;

	protected PostResponse postResponse;

	private boolean mQuit = false;

	public DispatchQuene(BlockingQueue<Request> que, RequestQueue request) {
		queue = que;
	}

	public DispatchQuene(BlockingQueue<Request> que, RequestQueue request, PostResponse postResponse) {
		queue = que;
		this.postResponse = postResponse;
	}

	@Override
	public void run() {
		super.run();
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		while (!mQuit) {
			try {
				System.out.println("开始");
				Request take = queue.take();

				Response response = take.getResponse();
				Object[] parmter = take.getParmter();
				if (postResponse != null) {

					postResponse.postResponse(response,parmter);
				} else {
					response.onResponse(parmter);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				mQuit=true;
				continue;
			}

		}
		release();

	}
	
	public boolean isQuit(){
		return mQuit;
	}
	
	public void release(){
		mQuit =true;
		if(queue!=null){
			queue.clear();
		}
	}

	/**
	 * Forces this dispatcher to quit immediately. If any requests are still in the queue, they are
	 * not guaranteed to be processed.
	 */
	public void quit() {
		mQuit = true;
	
		interrupt();
	}
}
