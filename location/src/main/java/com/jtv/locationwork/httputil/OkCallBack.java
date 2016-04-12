package com.jtv.locationwork.httputil;

import java.io.IOException;

import android.os.Handler;
import android.os.Looper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OkCallBack implements Callback {
	protected static Handler handler = new Handler(Looper.getMainLooper());
	protected ObserverCallBack back;
	protected int method;
	protected Object obj;
	protected String response;

	public OkCallBack(ObserverCallBack back, int method, Object obj) {
		this.back = back;
		this.method = method;
		this.obj = obj;
	}

	@Override
	public void onFailure(Call arg0, IOException arg1) {
		if (arg0 != null) {
			response = arg0.toString();
		}

		handler.post(new Runnable() {
			public void run() {

				if (back != null) {
					back.badBack(response, method, obj);
				}
			}
		});
	}

	@Override
	public void onResponse(Call arg1, Response arg0) throws IOException {

		if (arg0 != null) {
			try {
				response = arg0.body().string();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		handler.post(new Runnable() {
			public void run() {
				if (back != null) {
					back.back(response, method, obj);
				}
			}
		});

	}

}