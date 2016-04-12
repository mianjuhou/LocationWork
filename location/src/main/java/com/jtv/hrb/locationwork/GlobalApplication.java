package com.jtv.hrb.locationwork;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.gitonway.niftydialogeffects.widget.niftydialogeffects.Effectstype;
import com.jtv.base.util.ApkUtils;
import com.jtv.base.util.CollectionActivity;
import com.jtv.base.util.FileUtil;
import com.jtv.base.util.LDownLoadManager;
import com.jtv.base.util.LDownLoadManager.onDownLoadListener;
import com.jtv.base.util.UToast;
import com.jtv.locationwork.activity.RequestPermissionAty;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.entity.DepartMentBeanJson;
import com.jtv.locationwork.exception.MyCrashHandler;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.ParseJson;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.imp.SockConfigDepart;
import com.jtv.locationwork.imp.SockConfigMessid;
import com.jtv.locationwork.imp.SockPadConfig;
import com.jtv.locationwork.imp.SockPhotoQuality;
import com.jtv.locationwork.imp.SockStartVideo;
import com.jtv.locationwork.listener.SockMessageCallBack;
import com.jtv.locationwork.services.DownLoadHttpServices;
import com.jtv.locationwork.services.GpsServices;
import com.jtv.locationwork.socket.SingleSocket;
import com.jtv.locationwork.socket.SockThreadImp;
import com.jtv.locationwork.socket.SockeThread;
import com.jtv.locationwork.util.AnimationDialogUtil;
import com.jtv.locationwork.util.Base64UtilCst;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.IntentUtils;
import com.jtv.locationwork.util.LogUtil;
import com.jtv.locationwork.util.PDADeviceInfoService;
import com.jtv.locationwork.util.PackageUtil;
import com.jtv.locationwork.util.ParseJsonMessage;
import com.jtv.locationwork.util.ScanQRUtil;
import com.jtv.locationwork.util.SiteidUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.StringUtil;
import com.jtv.locationwork.util.TextUtil;
import com.jtv.video.recorder.helper.VCameraUtil;
import com.plutus.libraryui.dialog.MessageUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

public class GlobalApplication extends Application implements ObserverCallBack {

	private long startTime = 0;

	private static String TAG = "LocationApp";

	public Intent mGpsService = null;

	public static GlobalApplication loc = null;

	public static Location oldInfo = null;// 保存的是最近的定位信息

	public static Activity home = null; // activity

	public static Context mContext; // 上下文

	public Notification nfim;

	// 当前手持机的配置全局变量

	public static String lead;// 但前手持机登陆人

	public static String mBase64Lead;// 当前手持机的base64编码

	public static String siteid;// 当前手持机的站点

	public static String attid = "";// 手持机的id

	public static String bumen_name = "";// 部门的名字

	public static String duan_name = "";// 段的名字

	public static String work_shop_name = "";// 车间的名字

	public static String area_name = "";// 工区的名字

	public static String area_id;// 工区的id

	public static String departid;// 当前手持机的车间id

	public static String orgid;// orgid

	private static boolean isFirstVersion = false;// 当前手持机是否第一次安装

	public static Handler handler;// 全局处理的handler

	private static final int DOWNLOAD_APP = 101; // 下载程序

	private static final int TOAST = 99;

	private static final int POPUP_DOWNLOAD_DIALOG = 102;// 弹出下载提示框

	private static SockThreadImp sock = null;// 全局的socket

	public static boolean isFindUpdateVersion = false; // 是否发现新版本

	public static String rtsp_ip;

	public static int rtsp_port = 1935;

	public static int max_video_time = 10 * 1000;

	public SockThreadImp getSock() {
		return sock;
	}

	// 当前版本是否第一次使用
	public static boolean isFirstUseVersion(Context con) {

		if (isFirstVersion) {
			return isFirstVersion;
		}

		String version = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.SP_KEY_VERSION, "0");

		String curr = PackageUtil.getVersionName(con);

		if (TextUtils.equals(curr, version)) {// 不是第一次使用
			isFirstVersion = false;
		} else {
			isFirstVersion = true;
		}

		return isFirstVersion;
	}

	// 设置当前的使用版本
	public static void setFirstUserVersion(String version) {
		Editor edit = SpUtiles.BaseInfo.mbasepre.edit();
		edit.putString(CoustomKey.SP_KEY_VERSION, version);
		edit.commit();
	}

	static {
		handler = new Handler() {

			private String pathServer;

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case TOAST:
					try {
						String toast = (String) msg.obj;
						if (!TextUtil.isEmpty(toast)) {
							UToast.makeShortTxt(mContext, toast);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				case DOWNLOAD_APP: // 下载app
					pathServer = (String) msg.obj;
					LDownLoadManager lDownLoadManager = new LDownLoadManager(mContext);
					lDownLoadManager.setDeleteExtie();
					lDownLoadManager.setOnDownLoadListener(new onDownLoadListener() {

						@SuppressLint("NewApi")
						@Override
						public void downLoadCallBack(Intent intent, Query arg1, DownloadManager arg2) {

							if (arg1 == null) {// 下载失败
								startWebView(pathServer);
								return;
							}

							long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
							if (id == -1)
								return;
							arg1 = arg1.setFilterById(id);
							Cursor cos = arg2.query(arg1);
							boolean moveToNext = cos.moveToNext();
							if (!moveToNext) {
								startWebView(pathServer);
								return;
							}
							int value = cos.getColumnIndex(DownloadManager.COLUMN_STATUS);
							value = cos.getInt(value);
							switch (value) {
							case DownloadManager.STATUS_SUCCESSFUL:
								String sdPath = FileUtil.getSDPath();
								String position = sdPath + File.separator + Environment.DIRECTORY_DOWNLOADS
										+ File.separator + FileUtil.getServerFileName(pathServer);
								try {
									ApkUtils.installApk(mContext, position);
								} catch (Exception e) {
									e.printStackTrace();
									UToast.makeShortTxt(mContext,
											mContext.getString(R.string.error_install_failed) + " 手动安装位置:" + position);
								}
								break;
							case DownloadManager.STATUS_RUNNING:
								break;
							}
						}

						// 防止下载失败用浏览器下载
						private void startWebView(String pathServer) {

							Intent startWebView = IntentUtils.startWebView(pathServer);
							try {
								mContext.startActivity(startWebView);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});
					long id = lDownLoadManager.addFile(pathServer, Environment.DIRECTORY_DOWNLOADS,
							FileUtil.getServerFileName(pathServer));
					LogUtil.i("download", "download id  ... " + id);
					break;
				case POPUP_DOWNLOAD_DIALOG:// 弹出下载对话
					pathServer = (String) msg.obj;

					Activity topActivity = CollectionActivity.getTopActivity();

					if (topActivity == null)
						topActivity = home;

					String forceFlag = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.MESSAGEID001, "2");

					if ("1".equals(forceFlag)) {// 强制更新
						Message obtain = Message.obtain();
						obtain.obj = pathServer;
						obtain.what = DOWNLOAD_APP;
						handler.sendMessage(obtain);
						return;
					}

					AlertDialog.Builder builder = new AlertDialog.Builder(topActivity);
					builder.setMessage(topActivity.getString(R.string.find_version_update))
							.setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int i) {

							Message obtain = Message.obtain();
							obtain.obj = pathServer;
							obtain.what = DOWNLOAD_APP;
							handler.sendMessage(obtain);
							dialog.dismiss();
						}
					}).setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								dialog.dismiss();
							} catch (Exception e) {
							}

						}
					});
					final AlertDialog create = builder.create();
					create.setCanceledOnTouchOutside(true);
					try {
						AnimationDialogUtil.startAnimation(Effectstype.Shake, create);
					} catch (Exception e) {
					}

					create.show();

					break;
				case SockeThread.RECEIVER_DATA:// 接收到后台返回的数据
					String date = (String) msg.obj;
					Activity topActivity2 = CollectionActivity.getTopActivity();

					if (topActivity2 == null) {
						topActivity2 = home;
					}

					if (TextUtil.isEmpty(date)) {
						// UToast.makeShortTxt(mContext, "收到后台消息null");
						return;
					}

					// UToast.makeShortTxt(mContext, date);
					SockMessageCallBack sc = null;
					String messid = "";
					String mValue = "";
					ParseJsonMessage parseJsonMessage = new ParseJsonMessage(date);
					messid = parseJsonMessage.getId();
					mValue = parseJsonMessage.getValue();

					if (CoustomKey.MESSAGEID001.equalsIgnoreCase(messid)) {// 版本更新
						sc = new SockConfigMessid(messid, mValue);
					} else if (CoustomKey.MESSAGEID002.equalsIgnoreCase(messid)) {// 人员配置部门管理
						sc = new SockConfigDepart(messid, mValue);
					} else if (CoustomKey.MESSAGEID003.equalsIgnoreCase(messid)) {// 视频请求
						sc = new SockStartVideo();
					} else if (CoustomKey.MESSAGEID004.equalsIgnoreCase(messid)) {// 功能权限
						// sc = new SockConfigMessid(messid, mValue);
					} else if (CoustomKey.MESSAGEID005.equalsIgnoreCase(messid)) {// 照片通过与否
						sc = new SockPhotoQuality(mValue);
					} else if (CoustomKey.MESSAGEID006.equalsIgnoreCase(messid)) {
						sc = new SockPadConfig(mValue);
					} else {// 找不到处理的方式

						if ("response".equals(date))
							UToast.makeShortTxt(mContext, R.string.socket_success);
					}

					if (sc != null) {
						sc.doSomeThing(sock, topActivity2, date, messid);
						sc.doOver(sock, topActivity2, date, messid);
					}
					parseJsonMessage = null;
					messid = null;
					mValue = null;
					break;
				}
			}

		};
	}

	public static GlobalApplication getLocationApp() {
		if (loc == null) {
			loc = new GlobalApplication();
		}
		return loc;
	}

	public void requestVersionUpdate() {
		PackageInfo pi = null;
		try {
			pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		int versionCode = pi.versionCode;
		JSONObject json = new JSONObject();
		try {
			String version_name = "";

			if (isDXD()) {// 大修段
				version_name = "dxd_location";
			} else if (isDJD()) {// 大机段
				version_name = "djd_location";
			} else if (isZZQGD()) {// 郑州桥工段
				version_name = "zzqgd_location";
			} else if (isHLE()) {// 海拉尔工务段
				version_name = "location";
			} else if (isDQ()) {// 大庆工务段现场作业V2.3
				version_name = "dq_location";
			} else {
				version_name = "app";
			}
			if (TextUtil.isEmpty(version_name)) {
				return;
			}

			json.put("app", version_name);// 代表是哪个apk
			json.put("version", versionCode);
			json.put("attid", GlobalApplication.attid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		TrackAPI.requestApkUpdate(json, this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		boolean val = isPression(mContext);
		if (!val) {
			LogUtil.i("没权限");
			return;
		}
		loc = this;
		startTime = new Date().getTime();
		new Thread() {

			public void run() {
				ScanQRUtil.openScanQR();
				ScanQRUtil.setOutputInFocs();
				ScanQRUtil.appendEnter();
				ScanQRUtil.startTrigglerScan();
				deleteCache();
				SpUtiles.NearWorkListInfo.deleteYesterday(); // 删除昨天的工单号
			};
		}.start();

		// mGpsInfoArray = new JSONArray();

		startServiceGPS();
		startServiceDownLoad();

		getException();
		try {
			rtsp_ip = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.SOCKET_RTSP_IP, Constants.RTSP_VIDEO_SOCKET);
			String[] split = rtsp_ip.split(":");
			rtsp_ip = split[0];
			rtsp_port = Integer.valueOf(split[1]);
		} catch (Exception e) {
		}

		// 设置sock的参数
		setSockParmter(new OnSockRequestParmter() {

			@Override
			public String parmter() {
				JSONObject jsonObject = new JSONObject();
				mBase64Lead = Base64UtilCst.encodeUrl(lead);
				PDADeviceInfoService pdaDeviceInfoService = new PDADeviceInfoService(mContext);
				try {
					jsonObject.put("devicesid", pdaDeviceInfoService.getDeviceId());
					jsonObject.put("lead", mBase64Lead);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return jsonObject.toString();
			}
		});

		// if (!TextUtil.isEmpty(lead)) {
		// connectSocket(); // 连接到后台
		// } else {
		// UToast.makeShortTxt(mContext, R.string.socket_failed);
		// }

		// 在Activity中的onDestroy中:'
		try {
			VCameraUtil.init(this);
		} catch (Throwable e) {
		}

	}

	/**
	 * 检查是否有权限
	 * 
	 * @param con
	 * @return
	 */
	public static boolean isPression(Context con) {
		boolean premission = true;
		if (Build.VERSION.SDK_INT > 22) {

			String[] pre = RequestPermissionAty.PRESSION;
			for (String string : pre) {
				if (ContextCompat.checkSelfPermission(con, string) == PackageManager.PERMISSION_DENIED) {
					premission = false;
					break;
				}
			}

		}

		return premission;
	}

	private void getException() {
		MyCrashHandler instance = MyCrashHandler.getInstance(); // 开始捕获异常
		instance.init(mContext);
		Thread.setDefaultUncaughtExceptionHandler(instance);
	}

	private void startServiceDownLoad() {
		Intent intent = new Intent(mContext, DownLoadHttpServices.class); // 开启接收后台下载的服务
		mContext.startService(intent);
	}

	public void disPlayNotification(Intent intent2) {

		// if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {//
		// 低版本报错不能捕获
		// return;
		// }

		// try {
		// Bitmap mBitmapIcon = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_launcher);
		// nfim = MessageUtil.showNotification(mContext, true, intent2, 1,
		// R.drawable.ic_launcher, mBitmapIcon,
		// mContext.getString(R.string.app_name),
		// mContext.getString(R.string.app_state), "");
		// } catch (Exception e) {
		// e.printStackTrace();// 不兼容低版本

		nfim = MessageUtil.showLownNotification(mContext, false, intent2, 3245, R.drawable.ic_launcher,
				mContext.getString(R.string.app_name), mContext.getString(R.string.app_state), "");

		// }

	}

	/**
	 * 获取一个工区id或者车间id，优先获取工区ID
	 * 
	 * @return
	 */
	public static String getAreaIdOrShopId() {
		if (TextUtil.isEmpty(GlobalApplication.area_id)) {
			return GlobalApplication.departid;
		} else {
			return GlobalApplication.area_id;
		}
	}

	public static void saveGpsInfo(Location info) {

		if (info != null) {

			try {
				SpUtiles.BaseInfo.saveGps(info.getLatitude() + "", info.getLongitude() + "");
			} catch (Exception e) {
				LogUtil.e("error", e);
			}

			oldInfo = info;
			info = null;
		}

	}

	@SuppressWarnings("deprecation")
	private void deleteCache() {
		Date mDate = new Date();
		File rootFile = CreatFileUtil.getRootFile(this);

		if (rootFile == null) {
			return;
		}
		File[] listFiles = rootFile.listFiles();

		if (listFiles != null) {
			for (int i = 0; i < listFiles.length; i++) {
				File file = listFiles[i];

				String name = file.getName();

				if (file.exists() && file.isDirectory() && !name.equals(CreatFileUtil.mDownLoad)) {// 下载的文件不允许删除
					long lastModified = file.lastModified();
					Date mFileDate = new Date(lastModified);
					int day = mFileDate.getDay();
					if (day != mDate.getDay()) {
						FileUtil.delete(file);
					}
				}

			}
		}
	}

	public void setApplication() {
		GlobalApplication.getLocationApp().setLead(null);
		GlobalApplication.getLocationApp().setSiteid(null);
		String devicei = SpUtiles.SettingINF.sp.getString(CoustomKey.DEVICE_ID, "");
		if (TextUtil.isEmpty(devicei)) {
			PDADeviceInfoService pdaDeviceInfoService = new PDADeviceInfoService(getApplicationContext());
			SpUtiles.SettingINF.sp.edit().putString(CoustomKey.DEVICE_ID, pdaDeviceInfoService.getDeviceId()).commit();
		}

		GlobalApplication.attid = new PDADeviceInfoService(this).getDeviceId();

		GlobalApplication.siteid = SpUtiles.SettingINF.getString(CoustomKey.SITEID);
		GlobalApplication.departid = SpUtiles.SettingINF.getString(CoustomKey.DEPARTID_WORK_SPACE);
		GlobalApplication.area_id = SpUtiles.SettingINF.getString(CoustomKey.DEPARTID_AREA);

		GlobalApplication.bumen_name = SpUtiles.SettingINF.getString(CoustomKey.BUMEN_NAME);
		GlobalApplication.work_shop_name = SpUtiles.SettingINF.getString(CoustomKey.WORK_SHOP_NAME);
		GlobalApplication.area_name = SpUtiles.SettingINF.getString(CoustomKey.AREA_NAME);

		GlobalApplication.orgid = SpUtiles.SettingINF.getString(CoustomKey.ORG_ID);
		GlobalApplication.lead = SpUtiles.SettingINF.getString(CoustomKey.LEAD);
		GlobalApplication.duan_name = SpUtiles.SettingINF.getString(CoustomKey.DUAN_NAME);

		GlobalApplication.max_video_time = SpUtiles.BaseInfo.mbasepre.getInt(CoustomKey.RECORD_TIME,
				GlobalApplication.max_video_time);

		String baselead = Base64UtilCst.encodeUrl(GlobalApplication.lead);

		if (TextUtil.isEmpty(area_name)) {
			area_name = "";
		}

		if (TextUtil.isEmpty(duan_name)) {
			duan_name = "";
		}

		if (TextUtil.isEmpty(work_shop_name)) {
			work_shop_name = "";
		}

		if (TextUtil.isEmpty(lead)) {
			lead = "";
		}

		if (TextUtil.isEmpty(area_id)) {
			area_id = "";
		}

		if (TextUtil.isEmpty(departid)) {
			departid = "";
		}

		GlobalApplication.mBase64Lead = baselead;
	}

	/**
	 * 保存gps并上传
	 * 
	 * @param info
	 */
	private static void useGPSData(final Location info) {

		if (info == null)
			return;

		if (oldInfo != null && oldInfo.getLatitude() == info.getLatitude()
				&& oldInfo.getLongitude() == info.getLongitude()) {
			oldInfo = null;
			return;
		}

		saveGpsInfo(info);

		String woNum = SpUtiles.NearWorkListInfo.getWonum();

		TrackAPI.saveGpsinfoTest(mContext, woNum, info.getLongitude() + "", info.getLatitude() + "", "1", mBase64Lead);

	}

	// private Location loinfo;

	private static LocationListene mLocation = null;

	public void setLocationListener(LocationListene loc) {
		mLocation = loc;
	}

	public interface LocationListene {

		public boolean locationChangeListener(Location loinfo);
	}

	/**
	 * 设置当前全局的lead,从本地数据读取到
	 * 
	 * @return 设置成功返回true
	 */
	public boolean setLead(String lead) {
		if (TextUtil.isEmpty(lead)) {

			String tempLead = SpUtiles.SettingINF.getString(CoustomKey.LEAD);

			if (!TextUtil.isEmpty(tempLead)) {
				GlobalApplication.lead = tempLead;
			}
		} else {
			GlobalApplication.lead = lead;
		}

		if (TextUtil.isEmpty(GlobalApplication.lead)) {
			return false;
		}

		return true;

	}

	/**
	 * 
	 * @return
	 */
	public boolean setDepartId(String departid) {
		if (TextUtil.isEmpty(departid)) {

			String tempLead = SpUtiles.SettingINF.getString(CoustomKey.DEPARTID_WORK_SPACE);

			if (!TextUtil.isEmpty(tempLead)) {
				GlobalApplication.departid = tempLead;
			}
		} else {
			GlobalApplication.departid = departid;
		}

		if (TextUtil.isEmpty(GlobalApplication.departid)) {
			return false;
		}

		return true;

	}

	/**
	 * 设置当前手持机全局站点状态
	 * 
	 * @param siteid
	 * @return 设置失败返回false
	 */
	public boolean setSiteid(String siteid) {
		if (TextUtil.isEmpty(siteid)) {
			GlobalApplication.siteid = SpUtiles.SettingINF.getString(CoustomKey.SITEID);
		} else {
			GlobalApplication.siteid = siteid;
		}

		if (TextUtil.isEmpty(GlobalApplication.siteid)) {
			return false;
		}

		return true;

	}

	/**
	 * 接收到后台gps的回调
	 * 
	 * @param loinfo
	 */
	public static void receiverGPS(Location loinfo) {

		if (loinfo == null) {
			return;
		}

		if (mLocation != null) {
			boolean locationChangeListener = mLocation.locationChangeListener(loinfo);
			if (locationChangeListener) {
				return;
			}
		}

		try {
			useGPSData(loinfo);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	private void startServiceGPS() {

		mGpsService = new Intent(mContext, GpsServices.class);
		mContext.startService(mGpsService);

		LogUtil.e("error", "打开了服务,注册了广播");
	}

	public void stopServicesGPS() {

		if (mGpsService != null) {
			mContext.stopService(mGpsService);
		}

		LogUtil.e("error", "关闭了服务,取消了广播");
	}

	private static boolean depart_state_update = false;

	JSONArray jsonArr;

	@Override
	public void back(String data, int method, Object obsj) {
		switch (method) {
		case MethodApi.HTTP_VERSION_UPDATE:
			isFindUpdateVersion = false;
			if (TextUtils.isEmpty(data)) {
				Message obtain = Message.obtain();
				obtain.what = TOAST;
				obtain.obj = "没有发现最新版本";
				handler.sendMessage(obtain);
				return;
			}

			if ("-1".equals(data)) {// 异常
				Message obtain = Message.obtain();
				obtain.what = TOAST;
				obtain.obj = "没有发现最新版本";
				handler.sendMessage(obtain);
				LogUtil.e("download", "服务端返回错误地址");
			} else if ("0".equals(data)) {// 最新版本
				Message obtain = Message.obtain();
				obtain.what = TOAST;
				obtain.obj = "已经是最新版本";
				handler.sendMessage(obtain);
			} else if (data.length() < 200) {// 开始更新

				Message obtain = Message.obtain();
				obtain.what = POPUP_DOWNLOAD_DIALOG;
				obtain.obj = data;
				handler.sendMessage(obtain);
				isFindUpdateVersion = true;
				LogUtil.i("download", "服务端地址:" + data);
			}
			break;
		case MethodApi.HTTP_GET_REQUEST_DEPARTMENT:// 更新数据库部门编号

			if (TextUtil.isEmpty(data) || TextUtils.equals("-1", data)) {
				return;
			}
			depart_state_update = true;
			System.out.println("xiangying" + data);

			List<DepartMentBeanJson> parseArray = JSON.parseArray(data, DepartMentBeanJson.class);
			DBFactory.getConnection().delete("departtbl", null, null);
			for (int j = 0; j < parseArray.size(); j++) {
				DepartMentBeanJson departMentBeanJson = parseArray.get(j);
				if (departMentBeanJson == null)
					continue;
				ContentValues contentValues = new ContentValues();
				String ancestor = departMentBeanJson.getAncestor();
				if (!TextUtil.isEmpty(ancestor)) {
					contentValues.put("superdepart", ancestor);
				}
				String deptnum = departMentBeanJson.getDeptnum();
				if (!TextUtil.isEmpty(deptnum)) {
					contentValues.put("departid", deptnum);
				}
				String deptname = departMentBeanJson.getDeptname();
				if (!TextUtil.isEmpty(deptname)) {
					contentValues.put("departname", deptname);
				}
				String shortname = departMentBeanJson.getShortname();
				if (!TextUtil.isEmpty(shortname)) {
					contentValues.put("departabb", shortname);
				}
				String type = departMentBeanJson.getType();
				if (!TextUtil.isEmpty(type)) {
					contentValues.put("departlevel", type);
				}
				String siteid = departMentBeanJson.getSiteid();
				if (!TextUtil.isEmpty(siteid)) {
					contentValues.put("siteid", siteid);
				}

				String mOrgid = departMentBeanJson.getOrgid();
				if (!TextUtil.isEmpty(mOrgid)) {
					contentValues.put("orgid", mOrgid);
				}
				// contentValues.put("last_update_date",
				// optJSONObject.getString("deptnum"));
				DBFactory.getConnection().insert("departtbl", contentValues);

			}
			parseArray.clear();
			parseArray = null;
			depart_state_update = false;
			UToast.makeShortTxt(getApplicationContext(), R.string.toast_update_finish);
			break;

		case MethodApi.HTTP_REQUEST_WOLISTFAILED:// 获取动态字段
			if (TextUtil.isEmpty(data))
				return;

			try {
				new JSONArray(data);
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
			Editor edit = SpUtiles.BaseInfo.mbasepre.edit();
			edit.putString(CoustomKey.WONUM_TABLE_FIELD, data);
			edit.commit();
			break;
		case MethodApi.HTTP_PAD_CONFIG:// 获取配置文件

			try {

				if (!ParseJson.parseHasJsonData(data)) {
					return;
				}

				SharedPreferences mBaseInfo = SpUtiles.BaseInfo.mbasepre;
				Editor edit2 = mBaseInfo.edit();

				JSONObject mJson = new JSONObject(data);

				String mSessionIp = mJson.optString("sessionip");// 会话ip
				if (!TextUtil.isEmpty(mSessionIp) && !mSessionIp.contains("//")) {
					edit2.putString(CoustomKey.SOCKET_IP_SERVICE, mSessionIp);
				}

				String mVideoip = mJson.optString("videoip");// 视频ip
				if (!TextUtil.isEmpty(mVideoip)) {
					edit2.putString(CoustomKey.SOCKET_RTSP_IP, mVideoip);
				}

				String mIP = mJson.optString("ip");// 所有接口ip
				if (!TextUtil.isEmpty(mIP)) {
					edit2.putString(CoustomKey.SERVICES_IP2, mIP);
				}
				String word = mJson.optString(CoustomKey.k_word);// 语音文本接口ip
				if (!TextUtil.isEmpty(word)) {
					edit2.putString(CoustomKey.k_word, word);
				}

				String mPhotoPixel = mJson.optString("pixel");// 质量
				if (!TextUtil.isEmpty(mPhotoPixel)) {
					try {
						int mQuality = Integer.valueOf(mPhotoPixel);

						if (mQuality != 30 || mQuality != 60 || mQuality != 90) {// 后台传递值错误，给默认值
							// mQuality = 60;
							if (mQuality <= 30) {
								mQuality = 30;
							} else if (mQuality <= 60) {
								mQuality = 60;
							} else {
								mQuality = 90;
							}

						}

						edit2.putInt(CoustomKey.IMAGE_QUALITY, mQuality);
					} catch (Exception e) {
					}
				}

				String mPhotoSize = mJson.optString("comratio");// 大小
				if (!TextUtil.isEmpty(mPhotoSize)) {
					try {
						int mPhoto = Integer.valueOf(mPhotoSize);

						if (mPhoto != 480 || mPhoto != 720 || mPhoto != 1080) {// 后台传递值错误，给默认值
							mPhoto = 480;
						}

						edit2.putInt(CoustomKey.IMAGE_PIXEI, mPhoto);
					} catch (Exception e) {
					}
				}

				String mradiotime = mJson.optString(CoustomKey.RECORD_TIME);
				if (!TextUtil.isEmpty(mradiotime)) {
					try {
						int mTime = Integer.valueOf(mradiotime);

						// 换算为秒
						mTime = mTime * 60 * 1000;
						edit2.putInt(CoustomKey.RECORD_TIME, mTime);
						max_video_time = mTime;
					} catch (Exception e) {
					}
				}

				String mlicence = mJson.optString("licence");
				if (!TextUtil.isEmpty(mlicence)) {
					try {
						edit2.putString(CoustomKey.FACE_LICENCE, mlicence);
					} catch (Exception e) {
					}
				}

				edit2.commit();
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}

			break;

		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {

	}

	public void closeSock() {
		if (sock != null) {
			sock.closeSocket();
		}
	}

	/**
	 * 更新部门数据,防止多次调用
	 */
	public synchronized void updateBumenDate() {

		if (depart_state_update) {
			return;
		}
		depart_state_update = true;
		// new Thread(new Runnable() {
		// public void run() {
		TrackAPI.updateDepartmentFromServices(GlobalApplication.this, GlobalApplication.this);

		// JsonParseUtil jsonParseUtil = new JsonParseUtil();
		// String path
		// =Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"devlopement.txt";
		// String parseJsonForFile = jsonParseUtil.parseJsonForFile(path);
		// back(parseJsonForFile, AnsynHttpRequest.SUCCESS_HTTP,
		// HttpApiConfig.HTTP_GET_REQUEST_DEPARTMENT, null);;

		// }
		// }).start();

		// depart_state_update = false;
	}

	/**
	 * 删除数据库
	 */
	public static void deleteDB() {
		String jtvRoot = CreatFileUtil.getFilePath(CreatFileUtil.getRootFile(mContext)) + File.separator;
		if (!TextUtil.isEmpty(jtvRoot)) {
			jtvRoot += "location.db";
			File file = new File(jtvRoot);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	// 初始化数据库
	public static void initDB() {

		Activity topActivity = CollectionActivity.getTopActivity();

		if (topActivity == null)
			topActivity = home;

		try {
			CreatFileUtil.getRootFile(mContext);
		} catch (Exception e) {
		}
		DBFactory.init(topActivity);

		// string.xml 中的db_factory
		// CommonMain.commonContext = topActivity;
		// CommTool.initSystem(topActivity);
		// DBFactory.initFactory(topActivity,"com.jtv.framework.common.MyDBPool");

	}

	public static String SOCKET_HOST_IP = "";// 会话通讯的IP地址

	public static int SOCKET_HOST_PORT = 8089;// 会话通讯的端口号

	private int sock_flag = SockeThread.OK; // sock 错误的标示

	private boolean isConnecting = false; // 代表但前的链接状态

	/**
	 * 连接socket,如果没有登陆人连接失败
	 */
	public void connectSocket() {
		if (isConnecting)
			return;
		if (sock != null && !sock.isClosed()) {// 当前流可用
			return;
		}

		if (TextUtil.isEmpty(lead)) {

			setLead(null);

		}

		if (TextUtil.isEmpty(lead)) {
			UToast.makeShortTxt(mContext, R.string.socket_failed);
			return;
		}

		new Thread() {

			public void run() {

				if (sock != null) {// 至于为什么要先关闭在连接android 的一个bug
					sock.closeSocket();
					SingleSocket.closeSocket();
					sock = null;
				}

				SingleSocket single = new SingleSocket() {

					@Override
					protected Socket creatSocket() {
						try {

							String mSessionDefaultIp = mContext.getString(R.string.socket_ip);// 获取到默认的ip

							String mSessionIP = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.SOCKET_IP_SERVICE,
									mSessionDefaultIp);// 获取到配置在本地的ip

							// mSessionIP=" 172.16.91.120:8080";
							// mSessionIP = " 172.16.90.50:8089";

							SOCKET_HOST_IP = StringUtil.getHttpAddressIP(mSessionIP);// 获取到ip

							int mPort = StringUtil.getHttpAddressPort(mSessionIP);// 获取到端口

							if (mPort > 0) {
								SOCKET_HOST_PORT = mPort;
							}

							return new Socket(SOCKET_HOST_IP, SOCKET_HOST_PORT);
						} catch (UnknownHostException e) {
							sock_flag = SockeThread.ERROR_NOT_HOST;
							e.printStackTrace();
							LogUtil.e("socket", "error unkonownHostException.. " + e.getStackTrace().toString());
						} catch (IOException e) {
							e.printStackTrace();
							sock_flag = SockeThread.ERROR_NOT_PORT;
							LogUtil.e("socket", "error IOException.." + e.getStackTrace().toString());
						}
						return null;
					}
				};

				LogUtil.i("socket Thread", "socket  正在连接中。。。。。。。。");
				isConnecting = true;
				Socket singleSocket = single.getSingleSocket();

				if (singleSocket != null) {
					LogUtil.i("socket Thread", "socket 连接成功。。。。。。。。");
				} else {
					LogUtil.i("socket Thread", "socket 连接失败。。。。。。。。");
				}

				isConnecting = false;
				if (singleSocket == null) {// 第一次连接

					Handler handler = new Handler(mContext.getMainLooper());
					handler.post(new Runnable() {

						public void run() {

							if (sock_flag != SockeThread.OK) {
								UToast.makeShortTxt(mContext, R.string.socket_failed);
							} else {

							}
						}
					});
				}

				if (singleSocket != null) {

					if (sock != null) {
						boolean interrupted = sock.isInterrupted();
						if (interrupted) {
							sock.interrupt();
						}
					}

					sock = new SockThreadImp(mContext, singleSocket, handler);
					sock.start();

					sock.setSleepThread(5 * 1000);// 休息15秒中

					try {
						String date = "";
						try {
							date = parmter.parmter();
						} catch (Exception e) {
							e.printStackTrace();
							// date="{\"lead\":"+encode+"\",\"\wonum\":\"+wonum+\"}";
						}

						sock.sendSocket(date);// 发送一个工单号
					} catch (IOException e) {
						e.printStackTrace();
						UToast.makeShortTxt(mContext, R.string.socket_failed_send);
						LogUtil.e("socket thread ", "send  date  error " + e.getStackTrace().toString());
					}
				}

			};
		}.start();
	}

	/**
	 * 获取app到现在启动的时间多少分钟
	 * 
	 * @return 分钟数
	 */
	public int getStartTimeDistanceMinute() {

		if (startTime == 0) {
			return 0;
		}

		long time = Calendar.getInstance().getTimeInMillis();

		long cha = time - startTime;

		int minute = (int) (cha / 1000 / 60);

		return minute;
	}

	private OnSockRequestParmter parmter = null;// 首次链接socket需要发送的数据

	/**
	 * 设置首次连接socket的参数
	 * 
	 * @param parmter
	 */
	public void setSockParmter(OnSockRequestParmter parmter) {
		this.parmter = parmter;
	}

	public interface OnSockRequestParmter {

		public String parmter();
	}

	public static boolean isDXD() {
		return SiteidUtil.isDXD(GlobalApplication.siteid);
	}

	public static boolean isDJD() {
		return SiteidUtil.isDJD(GlobalApplication.siteid);
	}

	public static boolean isZZQGD() {
		return SiteidUtil.isZZQGD(GlobalApplication.siteid);
	}

	public static boolean isDQ() {
		return SiteidUtil.isDQD(GlobalApplication.siteid);
	}

	public static boolean isHLE() {
		return SiteidUtil.isHLR(GlobalApplication.siteid);
	}

}
