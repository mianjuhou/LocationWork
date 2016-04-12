package com.jtv.locationwork.entity;

import java.io.File;
import java.util.HashMap;

import com.jtv.locationwork.entity.FileRequest.FileUploadlistener;
import com.plutus.queue.querypool.Response;

import com.plutus.queue.querypool.Request;
import android.os.Handler;
import android.os.Looper;

public class FileRequest<K, V> extends Request {
	private File mFile = null;
	private Object mObj = null;

	public FileRequest(HashMap<K, V> obj, File file, FileUploadlistener listener) {
		super(new HttpResponse<K, V>(listener), new Object());
		if (obj == null) {
			obj = new HashMap<K, V>();
		}
		mFile = file;
		par = new Object[2];
		par[0] = file;
		par[1] = obj;
	}

	public File getFile() {
		return mFile;
	}

	public Object getObj() {
		return mObj;
	}

	public FileRequest(HashMap<K, V> obj, File file, FileUploadlistener listener, Object mobj) {
		super(new HttpResponse<K, V>(listener), new Object());
		if (obj == null) {
			obj = new HashMap<K, V>();
		}
		mFile = file;
		par = new Object[3];
		par[0] = file;
		par[1] = obj;
		par[2] = mobj;
		this.mObj = mobj;
	}

	public interface FileUploadlistener {

		public String onFileStart(HashMap mhash, File file, Object obj);

		public void onFilePostEnd(HashMap mhash, File file, Object obj, String response);
	}
}

class HttpResponse<K, V> implements Response, Runnable {
	protected final FileUploadlistener mListener;

	public HttpResponse(FileUploadlistener listener) {
		mListener = listener;
	}

	Handler mHandler;

	String response;

	File mFile = null;

	HashMap<K, V> mParmter = null;

	Object obj = null;

	@Override
	public void onResponse(Object... t) {

		for (int i = 0; i < t.length; i++) {
			if (t[i] instanceof File) {
				mFile = (File) t[i];
				continue;
			} else if (t[i] instanceof HashMap) {
				mParmter = (HashMap<K, V>) t[i];
				continue;
			} else {
				obj = t[i];
			}

		}
		if (mFile != null && mListener != null) {
			response = mListener.onFileStart(mParmter, mFile, obj);
			mHandler = new Handler(Looper.getMainLooper());
			mHandler.post(this);
		}
		t = null;
	}

	@Override
	public void run() {
		mListener.onFilePostEnd(mParmter, mFile, obj, response);
	}
}
