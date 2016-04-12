package com.jtv.locationwork.activity;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jtv.base.activity.BaseAty;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.adapter.DepartLevelAdapterJson;
import com.jtv.locationwork.adapter.DepartNiceSpinerAdapter;
import com.jtv.locationwork.adapter.PersionSpinnerAdapter;
import com.jtv.locationwork.adapter.WorkShopAdapter;
import com.jtv.locationwork.entity.DepartJson;
import com.jtv.locationwork.entity.DepartLevel;
import com.jtv.locationwork.entity.PersionJson;
import com.jtv.locationwork.entity.WorkShopJson;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.SpUtiles;
import com.plutus.libraryui.dialog.LoadDataDialog;
import com.plutus.libraryui.spinner.NiceSpinner;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

public class DepartSeting extends BaseAty implements ObserverCallBack {

	private static org.json.JSONArray departArray;

	private LoadDataDialog loadProgressDialog;

	private List<DepartLevel> departLevel;
	private List<DepartJson> mArrDuan;
	private List<WorkShopJson> mArrWorkShop;
	private List<DepartJson> mArrArea;
	private List<PersionJson> mArrPerion;

	private NiceSpinner ns_bumen;
	private NiceSpinner ns_duan;
	private NiceSpinner ns_area;
	private NiceSpinner ns_shop;
	private NiceSpinner ns_persion;

	private DepartLevelAdapterJson bumenAdapter;
	private DepartNiceSpinerAdapter duanAdapter;
	private WorkShopAdapter workshopAdapter;
	private DepartNiceSpinerAdapter areaAdapter;
	private PersionSpinnerAdapter persionAdapter;

	private int state = 2;

	private LinearLayout ll_area;

	private Button btn_save;

	private boolean close;

	private int count = 0;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_save) {
			boolean state = onSave();// 保存设置的信息
			if (state) {

				loadProgressDialog.open();

				TrackAPI.onSaveDepart(this, this, GlobalApplication.lead, GlobalApplication.siteid,
						GlobalApplication.departid, GlobalApplication.area_id, GlobalApplication.duan_name,
						GlobalApplication.work_shop_name, GlobalApplication.area_name, GlobalApplication.attid,
						GlobalApplication.orgid);
				//
				// String lead = SpUtiles.SettingINF.getString(CoustomKey.LEAD);
				// LocationApp.getLocationApp().setLead(lead);
				// LocationApp.getLocationApp().setSiteid(null);

			} else {
				UToast.makeShortTxt(this, R.string.tos_save_failed);
			}

		} else {
		}
	}

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		setContentView(R.layout.location_jtv_depart_seting);
		loadProgressDialog = new LoadDataDialog(this);
		loadProgressDialog.open();

		setHeaderTitleText("设置部门");

		getHeaderBackBtn().setVisibility(View.GONE);

		findID();

		init();
		departLevel = getDepartLevel();
		setAdapter();
	}

	private void setAdapter() {

		bumenAdapter = new DepartLevelAdapterJson(this, departLevel);
		ns_bumen.setAdapterInternal(bumenAdapter);

		ns_bumen.addOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DepartLevel mDepart = (DepartLevel) arg0.getItemAtPosition(arg2);
				state = mDepart.getId();

				clear(mArrWorkShop);
				refersh(workshopAdapter);
				ns_duan.setSelectedIndex(-1);
				ns_shop.setSelectedIndex(-1);
				ns_persion.setSelectedIndex(-1);
				ns_area.setSelectedIndex(-1);

				if (state == 3) {
					ll_area.setVisibility(View.VISIBLE);
				} else {
					ll_area.setVisibility(View.GONE);
				}
			}
		});

		ns_duan.addOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				loadProgressDialog.open();
				DepartJson mDepart = (DepartJson) arg0.getItemAtPosition(arg2);
				String siteid = mDepart.getSiteid();
				TrackAPI.queryWorKShopData(DepartSeting.this, DepartSeting.this, siteid);
			}
		});

		ns_shop.addOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				loadProgressDialog.open();
				WorkShopJson mDepart = (WorkShopJson) arg0.getItemAtPosition(arg2);
				String siteid = mDepart.getSiteid();
				String value = mDepart.getDeptnum();

				if (3 == (state)) {// 工区
					TrackAPI.queryAreaData(DepartSeting.this, DepartSeting.this, siteid, value);
				} else {
					TrackAPI.queryWoInfo(DepartSeting.this, DepartSeting.this, value, null);
				}

			}
		});

		ns_area.addOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				loadProgressDialog.open();
				DepartJson mDepart = (DepartJson) arg0.getItemAtPosition(arg2);
				String value = mDepart.getValue();
				TrackAPI.queryWoInfo(DepartSeting.this, DepartSeting.this, value, null);

			}
		});

	}

	private void init() {
		TrackAPI.queryDuanData(this, this);
	}

	private void findID() {

		ll_area = (LinearLayout) findViewById(R.id.ll_area);

		ns_bumen = (NiceSpinner) findViewById(R.id.ns_bumen);
		ns_duan = (NiceSpinner) findViewById(R.id.ns_duan);
		ns_area = (NiceSpinner) findViewById(R.id.ns_area);
		ns_shop = (NiceSpinner) findViewById(R.id.ns_shop);
		ns_persion = (NiceSpinner) findViewById(R.id.ns_persion);

		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(this);

	}

	@Override
	public void back(String data, int method, Object obj) {
		close = true;
		switch (method) {
		case MethodApi.HTTP_QUERY_DUAN:

			try {
				mArrDuan = JSON.parseArray(data, DepartJson.class);

			} catch (Exception e) {
				e.printStackTrace();
				// mArrDuan.clear();
				clear(mArrDuan);
			}

			// JSONArray parseArray = JSONObject.parseArray(data);
			// for (int i = 0; i < parseArray.size(); i++) {
			// Object object = parseArray.get(i);
			// }
			// mArrDuan = JSON.parseArray(data, DepartJson.class);
			duanAdapter = new DepartNiceSpinerAdapter(this, mArrDuan);
			ns_duan.setAdapterInternal(duanAdapter);

			break;
		case MethodApi.HTTP_QUERY_WORK_SHOP:
			try {
				mArrWorkShop = JSONArray.parseArray(data, WorkShopJson.class);

			} catch (Exception e) {
				e.printStackTrace();
				clear(mArrWorkShop);
			}

			ns_area.setSelectedIndex(-1);// 设置下面联级数据不可用
			ns_persion.setSelectedIndex(-1);
			clear(mArrArea);
			refersh(areaAdapter);

			clear(mArrPerion);
			refersh(persionAdapter);

			workshopAdapter = new WorkShopAdapter(this, mArrWorkShop);
			ns_shop.setAdapterInternal(workshopAdapter);
			break;
		case MethodApi.HTTP_QUERY_AREA:
			try {
				mArrArea = JSONArray.parseArray(data, DepartJson.class);
			} catch (Exception e) {
				e.printStackTrace();
				clear(mArrArea);

			}

			ns_persion.setSelectedIndex(-1);// 设置下面联级数据不可用
			clear(mArrPerion);
			refersh(persionAdapter);

			areaAdapter = new DepartNiceSpinerAdapter(this, mArrArea);
			ns_area.setAdapterInternal(areaAdapter);
			break;
		case MethodApi.HTTP_CONSTANT:
			// data =
			// "[{\"title\":\"副主任\",\"personid\":\"张凤德\",\"displayname\":\"张凤德\"},{\"title\":\"副主任\",\"personid\":\"孔宪军1\",\"displayname\":\"孔宪军\"}]";
			try {
				mArrPerion = JSONArray.parseArray(data, PersionJson.class);
			} catch (Exception e) {
				e.printStackTrace();
				clear(mArrPerion);
			}

			persionAdapter = new PersionSpinnerAdapter(this, mArrPerion);
			ns_persion.setAdapterInternal(persionAdapter);

			break;
		case MethodApi.HTTP_SAVE_DEPARTMENT:
			Intent intent = new Intent(this, HomeFragment.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}

		if (close) {
			loadProgressDialog.close();
		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {
		close = true;
		switch (method) {
		case MethodApi.HTTP_SAVE_DEPARTMENT:
			Intent intent = new Intent(this, HomeFragment.class);
			startActivity(intent);
			finish();
			break;

		case MethodApi.HTTP_QUERY_DUAN:

			close = false;// 不关闭继续请求

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (!isFinishing()) {
				TrackAPI.queryDuanData(this, this);// 获取段数据一定要成工轮训
			}

			if (count == 0) {
				count++;
				UToast.makeLongTxt(this, R.string.error_http);
			}

			break;

		default:
			break;
		}

		if (close) {
			loadProgressDialog.close();
		}
	}

	private boolean onSave() {

		String shopid = "";
		String areid = "";
		String bumen_name = "";
		String duan_name = "";
		String workshop_name = "";
		String area_name = "";
		String personid2 = "";
		String siteid = "";
		String orgid = "";

		try {

			int selectedIndex = ns_bumen.getSelectedIndex();
			DepartLevel bumen = (DepartLevel) bumenAdapter.getItem(selectedIndex);

			int selectedIndex2 = ns_duan.getSelectedIndex();
			DepartJson duan = (DepartJson) duanAdapter.getItem(selectedIndex2);

			int selectedIndex3 = ns_shop.getSelectedIndex();
			WorkShopJson shop = (WorkShopJson) workshopAdapter.getItem(selectedIndex3);

			DepartJson are = null;
			if (state == 3) {
				int selectedIndex4 = ns_area.getSelectedIndex();
				are = (DepartJson) areaAdapter.getItem(selectedIndex4);
				// areid = are.getValue();
			}

			int selectedIndex5 = ns_persion.getSelectedIndex();
			PersionJson persion = (PersionJson) persionAdapter.getItem(selectedIndex5);

			shopid = shop.getDeptnum();

			siteid = duan.getSiteid();
			orgid = duan.getOrgid();

			// areid = shopid;

			if (are != null) {
				areid = are.getValue();
				area_name = are.getDescription();
			}

			personid2 = persion.getPersonid();
			bumen_name = bumen.getName();
			duan_name = duan.getDescription();

			workshop_name = shop.getDeptname();

			SpUtiles.SettingINF.sp.edit().clear().commit();

			Editor edit = SpUtiles.SettingINF.sp.edit();
			edit.putString(CoustomKey.LEAD, personid2);
			edit.putString(CoustomKey.SITEID, siteid);
			edit.putString(CoustomKey.DEPARTID_AREA, areid);
			edit.putString(CoustomKey.DEPARTID_WORK_SPACE, shopid);
			edit.putString(CoustomKey.BU_MEN_HAO, state + "");

			edit.putString(CoustomKey.BUMEN_NAME, bumen_name);
			edit.putString(CoustomKey.DUAN_NAME, duan_name + "");
			edit.putString(CoustomKey.WORK_SHOP_NAME, workshop_name);
			edit.putString(CoustomKey.AREA_NAME, area_name);
			edit.putString(CoustomKey.ORG_ID, orgid);

			edit.commit();

			GlobalApplication.getLocationApp().setApplication();
			//
			// LocationApp.bumen_name = bumen_name;
			// LocationApp.duan_name = duan_name;
			// LocationApp.work_shop_name = workshop_name;
			// LocationApp.area_name = area_name;
			// LocationApp.area_id = areid;
			// LocationApp.orgid = orgid;
			// LocationApp.lead = personid2;
			// LocationApp.siteid = siteid;
			// String base64Lead = Base64UtilCst.encode(personid2);
			// LocationApp.mBase64Lead = base64Lead;

		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}
		return true;

	}

	private void clear(List list) {
		if (list != null) {
			list.clear();
		}
	}

	private void refersh(BaseAdapter adatpter) {
		if (adatpter != null) {
			adatpter.notifyDataSetChanged();
		}
	}

	public static List<DepartLevel> getDepartLevel() {
		List<DepartLevel> arr = new ArrayList<DepartLevel>();

		DepartLevel departLevel = new DepartLevel();
		departLevel.setId(2);
		departLevel.setName("车间");
		arr.add(departLevel);

		DepartLevel departLevel2 = new DepartLevel();
		departLevel2.setId(3);
		departLevel2.setName("工区");
		arr.add(departLevel2);

		DepartLevel departLevel3 = new DepartLevel();
		departLevel3.setId(1);
		departLevel3.setName("科室");
		arr.add(departLevel3);

		DepartLevel departLevel4 = new DepartLevel();
		departLevel4.setId(0);
		departLevel4.setName("段别");
		arr.add(departLevel4);

		return arr;
	}

	/**
	 * 构造部门级别数据 段别是0 科室是1 车间是2 工区是3
	 */
	public static org.json.JSONArray buildDepartLevelData() {
		try {
			departArray = new org.json.JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("id", "2");
			obj.put("name", "车间");

			JSONObject obj1 = new JSONObject();
			obj1.put("id", "3");
			obj1.put("name", "工区");

			JSONObject obj2 = new JSONObject();
			obj2.put("id", "1");
			obj2.put("name", "科室");

			JSONObject obj0 = new JSONObject();
			obj0.put("id", "0");
			obj0.put("name", "段别");

			departArray.put(obj2);
			departArray.put(obj);
			departArray.put(obj1);

			return departArray;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		HomeFragment.exit(this);

		// ArrayList<Activity> mAty = CollectionActivity.getAllArrayList();
		//
		// for (Activity activity : mAty) {
		//
		// if (!activity.isFinishing()) {
		// activity.finish();
		// }
		// }
		//
		// android.os.Process.killProcess(android.os.Process.myPid());
		// java.lang.System.exit(0);

	}
}
