package com.jtv.base.util;

import java.io.File;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class LDownLoadManager {
	private Context con;
	private DownLoadReveiver downLoadReveiver;

	@SuppressLint("InlinedApi")
	public LDownLoadManager(Context con) {
		this.con = con;
		if (downLoadReveiver == null) {
			downLoadReveiver = new DownLoadReveiver();
			IntentFilter ifl = new IntentFilter();
			ifl.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
			ifl.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			con.registerReceiver(downLoadReveiver, ifl);
		}
	}

	public interface onDownLoadListener {
		public void downLoadCallBack(Intent intent, DownloadManager.Query query, DownloadManager dm);
	}

	public onDownLoadListener listener;

	private boolean isDelete;

	public void setDeleteExtie() {
		this.isDelete = true;
	}

	public void setOnDownLoadListener(onDownLoadListener listener) {
		this.listener = listener;
	}

	/**
	 * 
	 * @param serverAddress
	 *            服务端的下载地址
	 * @param pathDirector
	 *            路径不需要更目录
	 * @param name
	 *            保存的文件的名字
	 * @return id
	 */
	@SuppressLint("NewApi")
	public long addFile(String serverAddress, String pathDirector, String name) {
		DownloadManager downloadManager = (DownloadManager) con.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(serverAddress);
		DownloadManager.Request request = new DownloadManager.Request(uri);

		// 设置允许使用的网络类型，这里是移动网络和wifi都可以
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

		// 禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
		// request.setShowRunningNotification(false);
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);// 当下载完成的时候点击notifaction才会被取消
		// 显示下载界面
		request.setVisibleInDownloadsUi(true);

		MimeTypeMap singleton = MimeTypeMap.getSingleton();
		String mimeString = singleton.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(serverAddress));
		request.setMimeType(mimeString);// 设置文件的类型, 有些手机无效，原声的没事
		// request.setShowRunningNotification(true);
		String sdPath = FileUtil.getSDPath();
		try {
			if (!TextUtils.isEmpty(sdPath) && !TextUtils.isEmpty(pathDirector)) {// 设置文件的存放路径，默认是在data/data/com.provder.****下面
				if (pathDirector.startsWith(File.separator)) {
					pathDirector = pathDirector.substring(1);
				}
				File file = new File(sdPath + File.separator + pathDirector);
				if (!file.exists()) {
					file.mkdirs();
				}
				if (isDelete) {
					file = new File(sdPath + File.separator + pathDirector + File.separator + name);
					if (file.exists()) {
						file.delete();
					}
				}
				request.setDestinationInExternalPublicDir(pathDirector, name);// 当找不到文件的时候报错

			}

			/*
			 * 设置下载后文件存放的位置,如果sdcard不可用，那么设置这个将报错， 因此最好不设置如果sdcard可用， 下载后的文件
			 * 在/mnt/sdcard/Android/data/packageName/files目录下面，
			 * 如果sdcard不可用,设置了下面这个将报错，不设置，下载后的文件在/cache这个 目录下面
			 */
			// request.setDestinationInExternalFilesDir(this,
			// null,
			// "tar.apk");
			// request.setMimeType("application/cn.trinea.download.file");
			// 如果需要隐藏下载工具的提示和显示，修改代码：
			// request.setShowRunningNotification(false);
			// request.setVisibleInDownloadsUi(false);
			// 加入下面的权限：
			// <uses-permission
			// android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION"/>

			return downloadManager.enqueue(request);// 个别手机报错
		} catch (Exception e) {
			e.printStackTrace();

			if (listener != null) {
				listener.downLoadCallBack(null, null, null);
			}

		}
		return -1;
	}

	@SuppressLint("NewApi")
	class DownLoadReveiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			DownloadManager.Query query = new DownloadManager.Query();
			// final long longExtra =
			// intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			// intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			// Query querys = query.setFilterById(long1);
			// Cursor c = dm.query(querys);
			// if (c.moveToFirst()) {
			// int status =
			// c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			// switch (status) {
			// case DownloadManager.STATUS_PAUSED:
			// case DownloadManager.STATUS_PENDING:
			// case DownloadManager.STATUS_RUNNING: 点击事件
			// case DownloadManager.STATUS_SUCCESSFUL: 下载完成
			// case DownloadManager.STATUS_FAILED:

			DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			if (listener != null) {
				listener.downLoadCallBack(intent, query, dm);
			}
		}
	}
}
