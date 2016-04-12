package com.plutus.queue.querypool;

import android.os.Handler;

public class PostResponseDelivery implements PostResponse {
	protected final Handler mHandler;

	public PostResponseDelivery(Handler handler) {
		this.mHandler = handler;
	}

	@Override
	public void postResponse(final Response response, final Object... obj) {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				public void run() {
					response.onResponse(obj);
				}
			});
		}
	}

}
