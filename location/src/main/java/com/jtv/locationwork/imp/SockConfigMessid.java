package com.jtv.locationwork.imp;

import com.jtv.locationwork.listener.SockMessageCallBack;
import com.jtv.locationwork.socket.SockeThread;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;

import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
 * 版本更新
 * 
 * @author beyound
 * @email whatl@foxmail.com
 * @date 2015年8月28日
 */
public class SockConfigMessid implements SockMessageCallBack {
	protected String messid;

	protected String mValue;

	public SockConfigMessid(String msid, String mValue) {
		messid = msid;
		this.mValue = mValue;
	}

	@Override
	public void doSomeThing(SockeThread sock, Context con, String data,Object obj) {
		if (!TextUtil.isEmpty(messid)) {
			// data ＝1 强制更新,data =2 可选更新
			Editor edit = SpUtiles.BaseInfo.mbasepre.edit();
			edit.putString(messid, mValue);
			edit.commit();
		}
	}

	@Override
	public void doOver(SockeThread sock, Context con, String data,Object obj) {
//		if (sock != null) {
//			JSONObject jsonObject = new JSONObject();
//			try {
//				jsonObject.put(Constants.MESSAGEID, messid);
//				try {
//					sock.sendSocket(jsonObject.toString());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//		}
	}

}
