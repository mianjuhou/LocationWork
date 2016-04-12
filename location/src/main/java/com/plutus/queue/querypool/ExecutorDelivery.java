package com.plutus.queue.querypool;

import java.util.concurrent.Executor;

import android.os.Handler;

public class ExecutorDelivery
		implements PostResponse {/*
									 * /** Used for posting responses, typically to the main thread.
									 */
	private final Executor mResponsePoster;

	/**
	 * Creates a new response delivery interface.
	 * 
	 * @param handler
	 *            {@link Handler} to post responses on
	 */
	public ExecutorDelivery(final Handler handler) {
		// Make an Executor that just wraps the handler.
		mResponsePoster = new Executor() {
			@Override
			public void execute(Runnable command) {
				handler.post(command);
			}
		};
	}

	/**
	 * Creates a new response delivery interface, mockable version for testing.
	 * 
	 * @param executor
	 *            For running delivery tasks
	 */
	public ExecutorDelivery(Executor executor) {
		mResponsePoster = executor;
	}

	/**
	 * A Runnable used for delivering network responses to a listener on the main thread.
	 */
	@SuppressWarnings("rawtypes")
	private class ResponseDeliveryRunnable implements Runnable {

		private final Response mResponse;
		private final Object mPar;

		public ResponseDeliveryRunnable(Response response,Object ...par) {
			mResponse = response;
			mPar = par;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			mResponse.onResponse(mPar);
		}
	}

	@Override
	public void postResponse(Response response,Object ...obj) {
		mResponsePoster.execute(new ResponseDeliveryRunnable(response,obj));
	}
}
