package com.jtv.locationwork.imp;

import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.socket.SockeThread;

import android.content.Context;

public class SockConfigDepart extends SockConfigMessid {

	public SockConfigDepart(String msid, String mValue) {
		super(msid, mValue);
	}

	@Override
	public void doSomeThing(SockeThread sock, Context con, String data, Object obj) {
		UToast.makeShortTxt(con, con.getString(R.string.dis_tos_departchage));
	}
}
