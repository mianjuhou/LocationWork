package com.jtv.locationwork.imp;

import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.locationwork.httputil.AnsynHttpRequest;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.listener.SockMessageCallBack;
import com.jtv.locationwork.socket.SockeThread;
import com.jtv.locationwork.util.TextUtil;

import android.content.Context;
public class SockPadConfig implements SockMessageCallBack {
	String messageValue;

	public SockPadConfig(String messageValue) {
		this.messageValue = messageValue;
	}

	@Override
	public void doSomeThing(SockeThread sock, Context con, String data, Object obj) {
		if (!TextUtil.isEmpty(messageValue)) {
			GlobalApplication.getLocationApp().back(messageValue, MethodApi.HTTP_PAD_CONFIG, null);
		}
	}

	@Override
	public void doOver(SockeThread sock, Context con, String data, Object obj) {
	}

}
