package com.jtv.locationwork.listener;

import com.jtv.locationwork.socket.SockeThread;

import android.content.Context;

public interface SockMessageCallBack {
	
	public void doSomeThing(SockeThread sock, Context con, String data, Object obj);

	public void doOver(SockeThread sock, Context con, String data, Object obj);
}
