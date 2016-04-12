package com.jtv.locationwork.util;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class EditorUtils {
	/**
	 * 注册一个edit的回车事件
	 * 
	 * @param et
	 * @param listener
	 */
	public static void registerEnterListener(EditText et, final OnKeyListener listener) {
		if (et == null)
			return;
		et.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_ENTER )
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					if (listener != null) {
						return listener.onKey(v, keyCode, event);
					}
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 手动模拟回车事件
	 * 
	 * @param et
	 */
	public static void enterClick(EditText et) {
		if (et == null)
			return;
		et.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
	}
}
