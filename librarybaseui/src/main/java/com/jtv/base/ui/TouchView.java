package com.jtv.base.ui;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TouchView extends RelativeLayout {
	private SetViewListener listener;

	public void setListener(SetViewListener listener) {
		this.listener = listener;
	}

	public TouchView(Context context) {
		super(context);
		init(context);
	}

	public TouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}


	public void init(Context context) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (listener != null) {
				listener.getView(this);
			}
			break;
		case MotionEvent.ACTION_MOVE:

			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	public interface SetViewListener {
		void getView(TouchView view);
	}

}
