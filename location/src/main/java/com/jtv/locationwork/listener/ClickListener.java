package com.jtv.locationwork.listener;

import android.view.View;

public interface ClickListener<T> {
	public void onClick(View view,T t,Object... obj);
}
