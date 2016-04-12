package com.jtv.locationwork.httputil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

public class FileOkCallBack extends OkCallBack {
	protected File file;

	public FileOkCallBack(File address, ObserverCallBack back, int mehtod, Object obj) {
		super(back, mehtod, obj);
		this.file = address;
	}

	@Override
	public void onResponse(Call arg1, Response response) throws IOException {

		InputStream is = null;
		byte[] buf = new byte[2048];
		int len = 0;
		FileOutputStream fos = null;
		try {
			is = response.body().byteStream();
			fos = new FileOutputStream(file);
			while ((len = is.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
			fos.flush();
			// 如果下载文件成功，第一个参数为文件的绝对路径
			handler.post(new Runnable() {
				public void run() {
					if (back != null) {
						back.back(file.getAbsolutePath(), method, obj);
					}
				}
			});
		} catch (IOException e) {
			handler.post(new Runnable() {
				public void run() {
					if (back != null) {
						back.back(file.getAbsolutePath(), method, obj);
					}
				}
			});
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
			}
		}

	}
}
