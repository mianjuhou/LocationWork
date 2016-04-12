package com.jtv.locationwork.services;

import java.io.File;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.fastjson.JSONException;
import com.jtv.base.util.CollectionActivity;
import com.jtv.base.util.FileUtil;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.httputil.AnsynHttpRequest;
import com.jtv.locationwork.httputil.HttpApi;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.OkHttpUtil;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

@SuppressLint("NewApi")
public class DownLoadHttpServices extends Service {
	public static ConcurrentHashMap<Integer, ProgressBean> download = new ConcurrentHashMap<Integer, ProgressBean>();
	private HttpTimerTask httpTimerTask;
	private Timer timer;
	private static int flag = 0;
	private final int DOWNLOAD = 2;
	private final int SHOWING = 1;
	private final int UPDATE_TIME = 1000 * 60 * 5;// 8分钟执行一次请求

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public synchronized int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			boolean hasExtra = intent.hasExtra("Start_Download");
			if (!hasExtra) {// 没有这个标记代表第一次启动不需要下载
				return START_NOT_STICKY;
			}
		}
		// instance = DownLoadFile.getInstance();
		// String serverAddress = intent.getStringExtra("serverAddress");
		// String saveFileAddress =
		// MkFileUtiles.getInstance().getDownLoadFile();
		// String depotid = intent.getStringExtra("depotid");
		// nfim.cancel(serverAddress.hashCode());
		// instance.startDownLoad(serverAddress, saveFileAddress,
		// getFileName(serverAddress),
		// DownLoadProgressBarAty.listener, depotid);
		return START_NOT_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		flag = 0;
		// 点击通知时转移内容
		// instance = DownLoadFile.getInstance();
		// 主要是设置点击通知时显示内容的类
		download.clear();
		timer = new Timer();
		httpTimerTask = new HttpTimerTask();// 1000*60*3 close
		timer.schedule(httpTimerTask, 10000, UPDATE_TIME);
	}

	int i = 0;

	class HttpTimerTask extends TimerTask {

		private JSONArray receiverArrayJson;
		private JSONObject json;
		private String src;
		private String depotfileid = "";
		private ProgressBean downInfo;
		private Handler handler;

		@Override
		public synchronized void run() {
			// if(true)return;
			String lead = SpUtiles.SettingINF.getString("lead");
			if (TextUtil.isEmpty(lead)) {
				return;
			}
			String response = TrackAPI.requestFileForServer(HttpApi.Http_Interface_requestDownloadFile, lead);
			if (TextUtil.isEmpty(response)) {
				return;
			}
			if (TextUtils.equals("[]", response)) {
				return;
			}
			try {
				receiverArrayJson = new JSONArray(response);
				for (int i = 0; i < receiverArrayJson.length(); i++) {
					json = (JSONObject) receiverArrayJson.get(i);
					src = json.getString("src");
					depotfileid = json.getString("depotfileid");
					if (TextUtil.isEmpty(src) || TextUtil.isEmpty(depotfileid)) {
						continue;
					}
					src = HttpApi.RootIP2 + "/operation" + src;// 组合下载地址
					if (download.containsKey(src.hashCode())) {// 已经存在了,没有必要在进行添加到
						continue;
					}
					downInfo = new ProgressBean(src.hashCode(), -1, src, depotfileid);
					download.put(src.hashCode(), downInfo);

					// 这个是获取顶部的activity
					// 这个是获取顶部的activity
					if (flag == DOWNLOAD) {// 确认下载直接进行下载
						forDownLoad();
						return;
					}
					if (flag == SHOWING) {
						return;
					}
					flag = SHOWING;
					handler = new Handler(Looper.getMainLooper());
					handler.post(new Runnable() {
						public void run() {
							Activity top = CollectionActivity.getTopActivity();
							AlertDialog.Builder builder = new AlertDialog.Builder(top);
							builder.setMessage(top.getString(R.string.print_out_file))
									.setPositiveButton(top.getString(R.string.download),
											new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {// 这个是开始下载
									forDownLoad();
									flag = DOWNLOAD;
								}
							}).setNegativeButton(top.getString(R.string.cancel_download),
									new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									flag = 0;
								}
							}).show();
						}
					});

				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// String a = "http://172.16.140.150:8080/a.rar";
			// String b = "http://172.16.140.150:8080/b.rar";
			// String c = "http://172.16.140.150:8080/c.rar";
			// String d = "http://172.16.140.150:8080/d.rar";
			// String e = "http://172.16.140.150:8080/e.rar";
			// String f = "http://172.16.140.150:8080/f.rar";
			// String g = "http://172.16.140.150:8080/g.rar";
			// String h = "http://172.16.140.150:8080/h.rar";
			// String ii = "http://172.16.140.150:8080/i.rar";
			// String k = "http://172.16.140.150:8080/k.rar";
			// String v = "http://172.16.140.150:8080/v.rar";
			// String arr[] = { a, b, c, d, e, f, g, h, ii, k, v };
			// if (i >= arr.length)
			// return;
			// if (download.containsKey(arr[i].hashCode())) {
			// return;
			// }
			//
			// download.put(arr[i].hashCode(), new
			// ProgressBean(arr[i].hashCode(), -1, arr[i], "FF"));
			// i++;
			// // receiverMessage(arr[i],depotfileid);
			// // download.put(arr[i].hashCode(), new
			// // ProgressBean(arr[i].hashCode(),0, arr[i]));
			// // 这个是获取顶部的activity
			// if (flag == DOWNLOAD) {// 确认下载直接进行下载
			// forDownLoad();
			// return;
			// }
			// if (flag == SHOWING) {
			// return;
			// }
			// flag = SHOWING;
			// handler = new Handler(Looper.getMainLooper());
			// handler.post(new Runnable() {});
		}
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		download.clear();
		// instance.stopALLDownLoad();
		stopSelf();
		super.onDestroy();
	}

	/**
	 * 调用循环下载
	 */
	private synchronized void forDownLoad() {
		Set<Entry<Integer, ProgressBean>> entrySet = download.entrySet();
		for (Entry<Integer, ProgressBean> entry : entrySet) {
			Integer key = entry.getKey();
			ProgressBean progressBean = download.get(key);
			if (progressBean.getID() > -1) {// 已经存在了,没有必要在进行添加到
				continue;
			}
			startDownLoad(progressBean.getPath());// 正式的开始下载
		}
	}

	/**
	 * 请求下载
	 */
	public void startDownLoad(String pathServer) {
		String downLoadFile = CreatFileUtil.getFilePath(CreatFileUtil.getDownLoad(this));
		if (TextUtils.isEmpty(downLoadFile)) {
			Log.i("Download", "不能下载,没有存储卡");
			return;
		}
		DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(pathServer);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		// 设置允许使用的网络类型，这里是移动网络和wifi都可以
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
		// 禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
		// request.setShowRunningNotification(false);
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);// 当下载完成的时候点击notifaction才会被取消
		// 显示下载界面
		request.setVisibleInDownloadsUi(true);
		MimeTypeMap singleton = MimeTypeMap.getSingleton();
		String mimeString = singleton.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(pathServer));
		request.setMimeType(mimeString);// 设置文件的类型, 有些手机无效，原声的没事
		// request.setShowRunningNotification(true);
		long id = 100;
		try {
			request.setDestinationInExternalPublicDir(CreatFileUtil.getUnlessRoot(this, CreatFileUtil.mDownLoad),
					getFileName(pathServer));
					// DownLoadReveiver downLoadReveiver = new
					// DownLoadReveiver();
					// IntentFilter ifl = new IntentFilter();
					// ifl.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
					// ifl.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
					// registerReceiver(downLoadReveiver, ifl);

			// throw new Exception();
			id = downloadManager.enqueue(request);
		} catch (Exception e) {// 当下载失败时执行xutil下载
			e.printStackTrace();
			File downLoad2 = CreatFileUtil.getDownLoad(getApplicationContext());
			String save = downLoad2.getAbsoluteFile() + File.separator + FileUtil.getServerFileName(pathServer);
			OkHttpUtil.download(pathServer, new File(save), new ObserverCallBack() {

				@Override
				public void back(String data, int method, Object obj) {
					Log.i("download", "下载成功＝＝＝＝＝＝＝＝＝＝＝＝");
				}

				@Override
				public void badBack(String error, int method, Object obj) {
					Log.i("download", "下载失败＝＝＝＝＝＝＝＝＝＝＝＝");
				}
			}, MethodApi.HTTP_CONSTANT, null);
		}

		ProgressBean progressBean = download.get(pathServer.hashCode());
		if (progressBean != null) {
			progressBean.setId(id);
			download.put(progressBean.getHashcode(), progressBean);
		}
		String url = HttpApi.Http_Interface_saveDownloadFileState + "&depotfileid=" + progressBean.getPersonid();
		AnsynHttpRequest.requestGet(url, null, MethodApi.HTTP_CONSTANT, this, false);
		// TrackAPI.getTrackerAPI().requestServer(HLocationApiConfig.Http_Interface_saveDownloadFileState,
		// hashMap);
	}

	/**
	 * 获取文件名
	 * 
	 * @param pathServer
	 * @return
	 */
	public String getFileName(String path) {
		int start = path.lastIndexOf(File.separator) + 1;
		return path.substring(start);
	}

}
