package com.jtv.locationwork.services;

import com.jtv.hrb.locationwork.GlobalApplication;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GpsServices extends Service implements TencentLocationListener {

	private LocationManager locationManager;

	private static final String TAG = "GpsServices";

	private static double EARTH_RADIUS = 6378137.0;

	private int requestTime = 20 * 1000; // 20s一更新

	private int tencetRequestTime = 30 * 1000; // 30s一更新

	private RequestLocationListener requestLocationListener;

	private TencentLocationManager tencetManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(TAG, "GpsServices 正在后台运行");

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// 为获取地理位置信息时设置查询条件
		// 获取位置信息
		// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
		// location = lm.getLastKnownLocation(bestProvider);
		// lm.addGpsStatusListener(listener);// 监听GPS状态

		requestLocationListener = new RequestLocationListener();

		// lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// requestTime,
		// 0, netWorkListener);
		// 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种，我们选用GPS，网络在此不做讨论
		// 参数2，位置信息更新周期：
		// 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
		// 参数4，监听
		// 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
		// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, requestTime,
		// 0, requestLocationListener);// 监听位置变化

		tencetManager = TencentLocationManager.getInstance(this);
		tencetManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_WGS84);// 设置成世界公认坐标系

		TencentLocationRequest request = TencentLocationRequest.create();
		request.setAllowCache(true);
		request.setInterval(tencetRequestTime); // 设置更新时间

		tencetManager.requestLocationUpdates(request, this);

	}

	class RequestLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {

			Log.i(TAG, "GPS 定位成功");
			sendLocation(location);

		}

		/**
		 * GPS状态变化时触发
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {

			// switch (status) {
			// // GPS状态为可见时
			// case LocationProvider.AVAILABLE:
			// Log.e(TAG, "当前GPS状态为可见状态");
			// break;
			// // GPS状态为服务区外时
			// case LocationProvider.OUT_OF_SERVICE:
			// Log.e(TAG, "当前GPS状态为服务区外状态");
			// break;
			// // GPS状态为暂停服务时
			// case LocationProvider.TEMPORARILY_UNAVAILABLE:
			// Log.e(TAG, "当前GPS状态为暂停服务状态");
			// break;
			// }

		}

		/**
		 * GPS开启时触发
		 */
		public void onProviderEnabled(String provider) {
			Log.i(TAG, "GPS开启");

			Location location = locationManager.getLastKnownLocation(provider);

			if (location == null) {

				return;
			}

			sendLocation(location);
		}

		/**
		 * GPS禁用时触发
		 */
		public void onProviderDisabled(String provider) {
			Log.i(TAG, "GPS禁用");
		}

	}

	private void sendLocation(Location loc) {

		Log.i(TAG, "发送广播");

		if (loc != null) {
			GlobalApplication.receiverGPS(loc);
		}

	}

	// 状态监听
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:

				Log.e(TAG, "第一次定位");

				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				// Log.e(TAG, "卫星状态改变");
				// 获取当前状态
				// GpsStatus gpsStatus = lm.getGpsStatus(null);
				// // 获取卫星颗数的默认最大值
				// int maxSatellites = gpsStatus.getMaxSatellites();
				// // 创建一个迭代器保存所有卫星
				// Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
				// .iterator();
				// int count = 0;
				// while (iters.hasNext() && count <= maxSatellites) {
				// @SuppressWarnings("unused")
				// GpsSatellite s = iters.next();
				// count++;
				// }
				// Log.e(TAG, "搜索到：" + count + "颗卫星");
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:

				Log.e(TAG, "定位启动");

				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:

				Log.e(TAG, "定位结束");

				break;
			}
		};
	};

	// 位置监听
	private void stopLocation() {

		if (tencetManager == null)
			return;

		tencetManager.removeUpdates(this);
	}

	@Override
	public void onDestroy() {

		if (locationManager != null) {
			locationManager.removeUpdates(requestLocationListener);
			requestLocationListener = null;
			locationManager = null;
		}

		stopLocation();

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 返回单位是米,算经纬度的距离
	 * 
	 * @param longitude1
	 * @param latitude1
	 * @param longitude2
	 * @param latitude2
	 * @return
	 */
	public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math
				.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private static double rad(double d) {
		return d * Math.PI / 10;
	}

	boolean isExitListener = false;

	@Override
	public void onLocationChanged(TencentLocation arg0, int arg1, String arg2) {

		Log.i(TAG, "腾讯地图网络定位: " + arg1 + "  状态: " + arg2);

		if (arg1 == TencentLocation.ERROR_OK) {// 定位成功

			double latitude = arg0.getLatitude();
			double longitude = arg0.getLongitude();

			// PositionUtil.gcj_To_Gps84(latitude,longitude);

			Location location = new Location(arg0.getProvider());
			location.setLatitude(latitude);
			location.setLongitude(longitude);

			sendLocation(location);

			if (locationManager != null) {
				locationManager.removeUpdates(requestLocationListener);
				locationManager = null;
			}
			isExitListener = false;
			location = null;

		} else {// 没有定位成功走gps定位

			if (!isExitListener) {

				if (locationManager == null) {
					locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				}

				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, requestTime, 1,
						requestLocationListener);// 监听位置变化

				isExitListener = true;

			}

		}

	}

	@Override
	public void onStatusUpdate(String arg0, int arg1, String arg2) {

	}

}
