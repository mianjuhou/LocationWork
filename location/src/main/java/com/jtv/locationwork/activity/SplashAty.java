package com.jtv.locationwork.activity;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.base.activity.BaseAty;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.LogUtil;
import com.jtv.locationwork.util.PackageUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class SplashAty extends BaseAty implements ObserverCallBack, Runnable {

	private int sleeptime = 3000;

	private final int START_ACTIVITY = 10;

	private String startActivityName = "";

	Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case START_ACTIVITY:

				startActivity();
				finish();

				break;

			default:
				break;
			}
		};
	};

	private Intent mTestIntent;

	@Override
	public void onClick(View v) {

	}

	// 第一次使用
	private void firstInstall() {

		GlobalApplication.deleteDB();

		// 设置成不是第一次使用
		String version = PackageUtil.getVersionName(this);
		GlobalApplication.setFirstUserVersion(version);

	}

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		setContentView(R.layout.location_jtv_splash);
		boolean val = GlobalApplication.isPression(this);
		if (!val) {
			LogUtil.i("没权限");
			startActivity(new Intent(this, RequestPermissionAty.class));
			finish();
			return;
		}

		GlobalApplication.mContext = this;
		GlobalApplication.home = this;
		File rootFile = CreatFileUtil.getRootFile(this);
		String filePath = CreatFileUtil.getFilePath(rootFile);
		String path = filePath + File.separator + "location.db";
		File file = new File(path);

		GlobalApplication.getLocationApp().setApplication();

		boolean firstVersion = GlobalApplication.isFirstUseVersion(this);

		if (firstVersion) {
			firstInstall();
		}

		GlobalApplication.initDB();

		initConfig();

		if (TextUtil.isEmpty(GlobalApplication.lead) || !file.exists()) {
			sleeptime = 6000;
		} else {
			sleeptime = 3000;
		}

		// testActivity();

		new Thread(this).start();
	}

	private void testActivity() {
		Intent mTestIntent = new Intent(this, DepartSeting.class);
		startActivity(mTestIntent);
	}

	private void startActivity() {

		String lead = SpUtiles.SettingINF.getString(CoustomKey.LEAD);
		if (TextUtil.isEmpty(lead)) {
			startActivityName = "Depart";
			startSetDepart();

		} else {
			startActivityName = "HomeMenu";
			startHome();

		}

	}

	@Override
	public void onBackPressed() {

	}

	private void startHome() {
		Intent mIntent = new Intent(this, HomeFragment.class);
		startActivity(mIntent);
	}

	private void startSetDepart() {
		Intent setting = new Intent(this, DepartSeting.class);
		startActivity(setting);
	}

	private void initConfig() {
		TrackAPI.requestDeparttlist(this, this);// 获取pad所属部门
	}

	@Override
	public void back(String data, int method, Object obj) {
		switch (method) {
		case MethodApi.HTTP_REQUEST_DEPARTMENT:// 更新部门
			// messagevalue:["{\"deptid\":\"2\",\"personid\":\"颜德贵\",\"attid\":\"352584063600750\",\"siteid\":\"104000\"}"]

			LogUtil.i("TAG", data);

			JSONArray jsonArray;
			try {

				if (TextUtil.isEmpty(data) || "-1".equals(data)) {
					data = "[{[]}"; // 直接报错
				}

				jsonArray = new JSONArray(data);

			} catch (JSONException e) {
				e.printStackTrace();

				SpUtiles.SettingINF.sp.edit().clear().commit();
				GlobalApplication.getLocationApp().setApplication();

				// 没有数据需要清除数据
				if ("HomeMenu".equals(startActivityName)) {

					// 如果当前的home界面已经打开，需要跳转到设置部门界面

					startSetDepart();

					if (!isFinishing()) {
						finish();
					}
				}

				return;
			}

			Editor edit = SpUtiles.SettingINF.sp.edit();
			JSONObject json = null;
			if (jsonArray != null) {
				try {
					json = (JSONObject) jsonArray.get(jsonArray.length() - 1);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (json == null) {
					return;
				}

				String deptid = json.optString(CoustomKey.DEPTID);
				String personid = json.optString(CoustomKey.PERSONID);
				String siteid = json.optString(CoustomKey.SITEID);
				String orgid = json.optString("orgid");
				String workshopname = json.optString("depname");
				String duanname = json.optString("sitename");
				String areaname = json.optString("areaname");
				String areid = json.optString("workarea");

				GlobalApplication.departid = deptid;
				GlobalApplication.getLocationApp().setLead(personid);

				edit.putString(CoustomKey.LEAD, personid);
				edit.putString(CoustomKey.SITEID, siteid);
				edit.putString(CoustomKey.DEPARTID_AREA, areid);
				edit.putString(CoustomKey.DEPARTID_WORK_SPACE, deptid);
				edit.putString(CoustomKey.ORG_ID, orgid);
				edit.putString(CoustomKey.DUAN_NAME, duanname);
				edit.putString(CoustomKey.AREA_NAME, areaname);
				edit.putString(CoustomKey.WORK_SHOP_NAME, workshopname);
				edit.commit();

				// LocationApp.lead=personid;
				// String baselead = Base64UtilCst.encode(personid);
				// LocationApp.mBase64Lead=baselead;
				// LocationApp.siteid=siteid;
				// LocationApp.departid=deptid;
				// LocationApp.area_id=areid;
				// LocationApp.orgid=orgid;
				//
				//
				// LocationApp.duan_name=duanname;
				// LocationApp.work_shop_name=workshopname;
				//
				// LocationApp.area_name=areaname;
				//
				//
				// LocationApp.getLocationApp().setLead(null);

				GlobalApplication.getLocationApp().setApplication();

			}
		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {

	}

	@Override
	public void run() {

		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mHandler.sendEmptyMessage(START_ACTIVITY);
	}

}
