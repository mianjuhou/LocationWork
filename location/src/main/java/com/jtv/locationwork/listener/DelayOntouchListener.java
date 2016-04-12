package com.jtv.locationwork.listener;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 延迟加载的TouchEvent
 * 
 * @author Administrator
 *
 */
public class DelayOntouchListener implements OnTouchListener {

	private DelayTouchListenerThread run;
	private OnTouchListener listener;
	private Activity aty;

	public DelayOntouchListener(OnTouchListener listener, Activity aty) {
		this.listener = listener;
		this.aty = aty;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			if (run == null) {
				run = new DelayTouchListenerThread(aty, listener, v, event);
				run.start();
			}

			break;

		case MotionEvent.ACTION_UP:
			if (run != null) {
				run.cancel();
				run = null;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (run != null) {
				run.cancel();
				run = null;
			}
			break;
		}

		return true;
	}

}