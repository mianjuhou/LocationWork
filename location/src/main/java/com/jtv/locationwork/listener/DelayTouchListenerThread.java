package com.jtv.locationwork.listener;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 延迟加载的线程
 * @author Administrator
 *
 */
public class DelayTouchListenerThread extends Thread {
	private OnTouchListener listener;
	private View v;
	private MotionEvent event;
	private boolean cancel = false;
	private int delayResponse = 400;
	private Activity con;

	public DelayTouchListenerThread(Activity aty, OnTouchListener listener, View v, MotionEvent event,
			int delayResponse) {
		this.listener = listener;
		this.v = v;
		this.event = event;
		this.delayResponse = delayResponse;
		this.con = aty;
	}

	public DelayTouchListenerThread(Activity aty, OnTouchListener listener, View v, MotionEvent event) {
		this(aty, listener, v, event, 400);
	}

	@Override
	public void run() {
		super.run();
		try {
			cancel = false;
			Thread.sleep(delayResponse);
		} catch (InterruptedException e) {
			cancel = true;
			return;
		}
		if (cancel) {
			return;
		}
		event.setAction(MotionEvent.ACTION_DOWN);
		con.runOnUiThread(new Runnable() {
			public void run() {
				listener.onTouch(v, event);
			}
		});
	}

	@Override
	public synchronized void start() {
		super.start();
	}

	public void cancel() {
		// stop();
		interrupt();
		if (!cancel) {
			event.setAction(MotionEvent.ACTION_UP);
			listener.onTouch(v, event);
		}
	}
}