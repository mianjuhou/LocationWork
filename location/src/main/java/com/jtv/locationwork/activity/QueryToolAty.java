package com.jtv.locationwork.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.base.activity.BaseAty;
import com.jtv.base.util.UToast;
import com.jtv.dbentity.dao.BaseDaoImpl;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.entity.LocalCache;
import com.jtv.locationwork.httputil.HttpApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.imp.CacheTool;
import com.jtv.locationwork.util.Cache.Entry;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.DiskBasedCache;
import com.jtv.locationwork.util.DiskCacheUtil;
import com.jtv.locationwork.util.EditorUtils;
import com.jtv.locationwork.util.ScanQRUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.StringUtil;
import com.jtv.locationwork.util.TextUtil;
import com.plutus.libraryzxing.ErcodeScanActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author: zn E-mail:zhangn@jtv.com.cn
 * @version:2015-1-16 类说明:机具检索页面(该页面仅仅就是根据条码查询工具的详情)
 */
public class QueryToolAty extends BaseAty implements OnClickListener, ObserverCallBack {

	private EditText et_barcode;// 条码

	private TextView tv_barcode;

	private TextView tv_toolnum;// 工具编号

	private TextView tv_toolname;// 工具名称

	private TextView tv_toolsize;// 工具规格

	private TextView tv_wstate;// 设备状态(0:入库;1:出库;2:调拨出库；3：调拨入库)

	private TextView tv_toolstatus;// 使用状态(0,正常;1,维修;2,报废;3,备用;4,调出;)

	private String barcode;

	private String wonum;

	// 扫描的请求码
	public static final int SCANREQUESTCODE = 0x1;

	Handler handler = new Handler() {
		private JSONObject obj = null;

		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:// 获取edittext中的数据进行本地查询数据
				barcode = StringUtil.replaceBlank((String) msg.obj);
				tv_barcode.setText(barcode);
				if (!StringUtil.isEmpty(barcode)) {
					try {
						String gps_jd = "0", gps_wd = "0";

						double[] mGPS = SpUtiles.BaseInfo.getGps();
						gps_wd = "" + mGPS[0];
						gps_jd = "" + mGPS[1];

						final JSONArray ja = new JSONArray();
						obj = new JSONObject();
						try {
							obj.put("GPS", gps_wd + "," + gps_jd);
							obj.put("TOOLNUM", barcode);
							obj.put("WONUM", wonum);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						ja.put(obj);

						// cacheTool(barcode);

						TrackAPI.saveToolInfo(QueryToolAty.this, QueryToolAty.this, ja.toString(), ja.toString());

					} catch (Exception e) {

					}
				} else {
					if (StringUtil.isEmpty(barcode)) {
						UToast.makeShortTxt(QueryToolAty.this, R.string.base_error_nofound);
					}
				}
				et_barcode.setText("");
				break;
			}
		};
	};

	private String tool_Type;

	private String toolsQrcode;

	private String toolsName;

	private String c_model;

	private String wareState;

	private String toolsState;

	private String toolsNum;

	protected void onCreatInit(Bundle savedInstanceState) {
		setContentView(R.layout.location_jtv_module_query_main);
		init();
		Intent intent = getIntent();
		wonum = intent.getStringExtra(CoustomKey.WONUM);
		listenEnter();
	}

	public void clearText() {
		tv_toolnum.setText("");
		tv_toolname.setText("");
		tv_toolsize.setText("");
		tv_wstate.setText("");
		tv_toolstatus.setText("");
	}

	public void init() {
		getHeaderBackBtn().setVisibility(View.VISIBLE);
		getHeaderTitleTv().setText(R.string.home_wo_title);
		et_barcode = (EditText) findViewById(R.id.et_barcode);
		tv_barcode = (TextView) findViewById(R.id.tv_barcode);
		tv_toolnum = (TextView) findViewById(R.id.tv_toolnum);
		tv_toolname = (TextView) findViewById(R.id.tv_toolname);
		tv_toolsize = (TextView) findViewById(R.id.tv_toolsize);
		tv_wstate = (TextView) findViewById(R.id.tv_wstate);// 使用状态
		tv_toolstatus = (TextView) findViewById(R.id.tv_toolstatus);// 设备状态
		findViewById(R.id.btn_scan).setOnClickListener(this);
		setBackOnClickFinish();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(SCAN_ACTION);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_scan) {
			Intent intent = new Intent(this, ErcodeScanActivity.class);
			startActivityForResult(intent, SCANREQUESTCODE);
		}
	}

	/**
	 * 监听扫码框
	 * 
	 * @author:zn
	 * @version:2015-1-21
	 */
	private void listenEnter() {
		EditorUtils.registerEnterListener(et_barcode, new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				String code = et_barcode.getText().toString().trim();
				if (TextUtil.isEmpty(code)) {
					return true;
				}
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = code;
				handler.sendMessage(msg);
				et_barcode.setText("");
				return true;
			}
		});
	}

	public boolean flag = true;

	/**
	 * 根据条码查询出来的工具信息进行显示设置 {[query:dsfdf,****:****],day:2} 上道机具缓存的格式
	 * 
	 * @author zhaoyj
	 * @date 2015-4-21
	 * @param str
	 * 
	 */
	private synchronized void setJsonArrayText(String str) {
		if (TextUtil.isEmpty(str))
			return;

		JSONObject saveJsonData = new JSONObject();
		JSONArray ja = null;
		JSONObject jo = null;
		try {
			ja = new JSONArray(str);
			if (ja != null && "[{}]".equals(ja.toString())) {// 没有数据
				return;
			}
			int length = ja.length();
			if (length == 0)
				return;
			jo = ja.getJSONObject(0);
			if (jo == null)
				return;
			if (jo.has("toolsQrcode")) {
				toolsQrcode = jo.getString("toolsQrcode");
				tv_barcode.setText(toolsQrcode);
				saveJsonData.put("toolsQrcode", toolsQrcode);
			}
			if (jo.has("toolsName")) {
				toolsName = jo.getString("toolsName");
				tv_toolname.setText(toolsName);
				saveJsonData.put("toolsName", toolsName);
			}
			if (jo.has("c_model")) {
				c_model = jo.getString("c_model");
				tv_toolsize.setText(c_model);
				saveJsonData.put("c_model", c_model);
			}
			if (jo.has("wareState")) {
				wareState = jo.getString("wareState");
				tv_wstate.setText(wareState);
				saveJsonData.put("wareState", wareState);
			}
			if (jo.has("toolsState")) {
				toolsState = jo.getString("toolsState");
				tv_toolstatus.setText(toolsState);
				saveJsonData.put("toolsState", toolsState);
			}
			if (jo.has("toolsNum")) {
				toolsNum = jo.getString("toolsNum");
				tv_toolnum.setText(toolsNum);
				saveJsonData.put("toolsNum", toolsNum);
			}
			saveJsonData.put("type", tool_Type);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		cacheTool(toolsQrcode);

	}

	// 缓存机具数据方便下次查看
	private void cacheTool(String tool) {

		tool = tool + "   扫描时间: " + DateUtil.getCurrDateFormat(DateUtil.style_nyr + "/" + DateUtil.style_sf);

		// 获取一个缓存
		DiskBasedCache cache = DiskCacheUtil.getCache(this);

		// 获取原来的缓存
		Entry entry = cache.get(DiskCacheUtil.TOOL_KEY);// 获取缓存

		// 创建一个新缓存
		Entry entry2 = DiskCacheUtil.getEntry(tool, 8);// 创建新的缓存

		Map<String, String> responseHeaders = null;

		if (entry != null) {

			if (!entry.isExpired()) {// 如果没有过期

				responseHeaders = entry.responseHeaders;
				responseHeaders.put(tool, tool);

			} else {
				cache.remove(DiskCacheUtil.TOOL_KEY);
			}

		}

		if (responseHeaders == null) {
			responseHeaders = new HashMap<String, String>();
			responseHeaders.put(tool, tool);
		}

		entry2.responseHeaders = responseHeaders;

		// 保存缓存
		cache.put(DiskCacheUtil.TOOL_KEY, entry2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ScanQRUtil.openScanQR();
		if (resultCode == SCANREQUESTCODE) {// 二维码扫描
			if (data != null && !TextUtil.isEmpty(data.getStringExtra("result"))) {
				String mCode = data.getStringExtra("result");

				et_barcode.setText(mCode);
				EditorUtils.enterClick(et_barcode);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ScanQRUtil.openScanQR();
	}

	@Override
	public void back(String data, int method, Object obj) {

		clearText();

		if (data == null || TextUtils.equals(data, "") || TextUtils.equals(data, "-1")) {
			return;
		}

		setJsonArrayText(data);
		UToast.makeShortTxt(this, getString(R.string.upload_successful));

	}

	@Override
	public void badBack(String error, int method, Object obj) {
		if (obj != null) {

			String json = (String) obj;
			JSONArray jsonObject2;

			try {
				jsonObject2 = new JSONArray(json);

				BaseDaoImpl<LocalCache> localCacheDao = DBFactory.getLocalCacheDao(GlobalApplication.mContext);
				LocalCache localCache = new LocalCache();
				localCache.setKey(jsonObject2.toString());
				localCache.setClasspath(CacheTool.class);
				localCache.setIp(HttpApi.Http_Interface_SaveToolInfo);

				long insert = localCacheDao.insert(localCache);
				localCache.setCache_id((int) insert);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

}
