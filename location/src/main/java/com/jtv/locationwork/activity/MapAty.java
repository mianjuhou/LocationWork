package com.jtv.locationwork.activity;

import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.entity.Gps;
import com.jtv.locationwork.util.MapUtils;
import com.jtv.locationwork.util.PositionUtil;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.GeoPoint;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapController;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.OnInforWindowClickListener;
import com.tencent.tencentmap.mapsdk.map.OnMapLongPressListener;
import com.tencent.tencentmap.mapsdk.map.OnMarkerDragedListener;
import com.tencent.tencentmap.mapsdk.map.OnMarkerPressListener;
import com.tencent.tencentmap.mapsdk.map.OverlayItem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MapAty extends MapActivity implements OnClickListener, TencentLocationListener, OnMarkerDragedListener,
		OnMarkerPressListener, OnInforWindowClickListener, OnMapLongPressListener {
	private TextView tv_title;
	private Button btn_ok;
	private Button iv_ok;
	private MapView mMapView;
	private TencentLocationManager mLocationManager;
	private Object mRequestParams;
	private TencentLocation mLocation;
	private String TAG = "MapAty";
	private MapController mapController;
	private OverlayItem overlayItem;
	private Drawable drawable;
	/** 定位的mark */
	private Marker locationMark;
	private boolean first = true;
	private TextView iv_back;

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startLocation();
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopLocation();
	}

	private void startLocation() {
		TencentLocationRequest request = TencentLocationRequest.create();
		request.setInterval(5000);
		mLocationManager.requestLocationUpdates(request, this);
		mRequestParams = request.toString() + ", 坐标系=" + MapUtils.toString(mLocationManager.getCoordinateType());
	}

	private void stopLocation() {
		mLocationManager.removeUpdates(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_jtv_lay_tencet_map);
		getHeaderTitleTv().setText("地图");
		getHeaderBackBtn().setVisibility(View.VISIBLE);
		drawable = getResources().getDrawable(R.drawable.mark_location);
		initMapView();
		mapController = mMapView.getController();
		// 标注点击监听
		mapController.setOnMarkerClickListener(this);
		// 标注拖动监听
		mapController.setOnMarkerDragListener(this);
		// InfoWindow点击监听
		mapController.setOnInforWindowClickListener(this);
		// 地图长按监听
		mapController.setOnMapPressClickLisener(this);
		mLocationManager = TencentLocationManager.getInstance(this);
		// 设置坐标系为 gcj-02, 缺省坐标为 gcj-02, 所以通常不必进行如下调用
		// wgs84坐标是世界坐标系，gcj是火星坐标系，需要使用gcj坐标系才能定位准确，但是在gps
		// 定位中用到了wgs84坐标系，所以如果在这里用gcj坐标系会报错
		mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_WGS84);
		getHeaderBackBtn().setOnClickListener(this);

	}

	private void initMapView() {
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.getController().setZoom(12);// 设置默认缩放级别
	}

	public TextView getHeaderBackBtn() {
		if (iv_back == null) {
			iv_back = (TextView) findViewById(R.id.tv_back);
		}
		return iv_back;
	}

	public TextView getHeaderTitleTv() {
		if (tv_title == null) {
			tv_title = (TextView) findViewById(R.id.tv_title);
		}
		return tv_title;
	}

	public Button getHeaderOkIvbtn() {
		if (iv_ok == null) {
			iv_ok = (Button) findViewById(R.id.iv_ok);
		}
		return iv_ok;
	}

	public Button getHeaderOkBtn() {
		if (btn_ok == null) {
			btn_ok = (Button) findViewById(R.id.btn_ok);
		}
		return btn_ok;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_back) {
			finish();
		} else {
		}
	}

	@Override
	public void onLocationChanged(TencentLocation location, int error, String arg2) {

		if (error == TencentLocation.ERROR_OK) {
			mLocation = location;
			if (locationMark != null) {
				locationMark.remove();
			}
			overlayItem = new OverlayItem(ofWGS842GCJ(location), "我的位置", "我的位置我的", drawable);
			overlayItem.setDragable(false);
			locationMark = MapAty.this.mMapView.add(overlayItem);
			// 定位成功
			StringBuilder sb = new StringBuilder();
			sb.append("定位参数=").append(mRequestParams).append("\n");
			sb.append("(纬度=").append(location.getLatitude()).append(",经度=").append(location.getLongitude())
					.append(",精度=").append(location.getAccuracy()).append("), 来源=").append(location.getProvider())
					.append(", 地址=").append(location.getAddress());
			Log.i(TAG, "定位成功 " + sb.toString());

			if (first) {
				mMapView.getController().animateTo(ofWGS842GCJ(mLocation));// 移动到当前的位置
				first = false;
			}
		}
	}

	@Override
	public void onStatusUpdate(String arg0, int arg1, String arg2) {

	}

	// wgs ----> acj
	private static GeoPoint ofWGS842GCJ(TencentLocation location) {
		Gps gps84_To_Gcj02 = PositionUtil.gps84_To_Gcj02(location.getLatitude(), location.getLongitude());
		GeoPoint ge = new GeoPoint((int) (gps84_To_Gcj02.getWgLat() * 1E6), (int) (gps84_To_Gcj02.getWgLon() * 1E6));
		return ge;
	}

	// gcj ---gcj
	private static GeoPoint ofGCJ2GCJ(TencentLocation location) {
		GeoPoint ge = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
		return ge;
	}

	/**
	 * 窗口点击的时候调用
	 */
	@Override
	public void onInforWindowClick(Marker arg0) {

	}

	/**
	 * mark 被按下时候调用
	 */
	@Override
	public void onMarkerPressed(Marker arg0) {

	}

	/**
	 * mark 拖动的时候调用
	 */
	@Override
	public void onMarkerDrag(Marker arg0) {

	}

	/**
	 * 拖动结束的时候调用
	 */
	@Override
	public void onMarkerDragEnd(Marker arg0) {

	}

	/**
	 * 拖动开始的时候调用
	 */
	@Override
	public void onMarkerDragStart(Marker arg0) {

	}

	/**
	 * 地图长按的时候调用
	 */
	@Override
	public void onMapLongPress(LatLng arg0) {

	}
}