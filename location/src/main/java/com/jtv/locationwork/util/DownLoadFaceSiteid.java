package com.jtv.locationwork.util;

import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.plutus.libraryui.dialog.LoadDataDialog;

import android.app.Activity;
import android.content.Context;

public class DownLoadFaceSiteid implements ObserverCallBack {
	private Context con;
	private LoadDataDialog loadDataDialog;

	public DownLoadFaceSiteid(Context con, String siteid) {
		this.con = con;
		TrackAPI.requestDownLoadFacePath(siteid, this);
		loadDataDialog = new LoadDataDialog((Activity) con);
		loadDataDialog.open();
	}

	@Override
	public void back(String data, int method, Object obj) {
		switch (method) {
		case MethodApi.HTTP_CONSTANT:
			TrackAPI.requestDownloadFile(data, this, con);
			break;

		case MethodApi.HTTP_DOWNLOAD_FACE:
			UToast.makeShortTxt(con, con.getString(R.string.download_ok));
			loadDataDialog.close();
			break;
		}
	}

	@Override
	public void badBack(String error, int method, Object obj) {
		loadDataDialog.close();
		switch (method) {
		case MethodApi.HTTP_CONSTANT:
			UToast.makeShortTxt(con, con.getString(R.string.download_failed));
			break;
		case MethodApi.HTTP_DOWNLOAD_FACE:
			UToast.makeShortTxt(con, con.getString(R.string.download_failed));
			break;
		}
	}
}
