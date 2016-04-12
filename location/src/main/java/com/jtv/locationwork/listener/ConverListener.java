package com.jtv.locationwork.listener;

import android.view.View;
import android.view.View.OnClickListener;

public class ConverListener<T> implements OnClickListener{
	
	protected ClickListener<T> listener;
	protected T t;
	protected Object [] t2;
	
	public ConverListener(ClickListener<T> listener){
		this(listener, null);
	}
	
	public ConverListener(ClickListener<T> listener,T t){
		this(listener, t,null);
	}
	
	public ConverListener(ClickListener<T> listener,T t,Object... t2){
		this.listener=listener;
		this.t=t;
		this.t2=t2;
	}
	
	@Override
	public void onClick(View v) {
		if(listener!=null){
			listener.onClick(v, t, t2);
		}
	}
	
}
