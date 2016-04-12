package com.jtv.locationwork.activity;

import com.jtv.hrb.locationwork.R;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

/**
 * android 6.0没权限
 * <p>
 *
 * @author 更生
 * @version 2016年3月13日
 */
public class RequestPermissionAty extends FragmentActivity {

	public static String[] PRESSION = { Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA };

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.location_jtv_main_fragment);
		// if (ContextCompat.checkSelfPermission(this,
		// Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
		// 申请WRITE_EXTERNAL_STORAGE权限
		ActivityCompat.requestPermissions(this, PRESSION, 1);
		// }
	}

	@SuppressLint("Override")
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		finish();
		System.exit(0);
		// if (requestCode == 1) {
		// if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
		// // Permission Granted
		// System.exit(0);
		// } else {
		// // Permission Denied
		// System.exit(0);
		// }
		// }
	}

}
