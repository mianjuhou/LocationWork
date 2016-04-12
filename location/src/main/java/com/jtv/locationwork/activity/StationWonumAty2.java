package com.jtv.locationwork.activity;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

import com.alibaba.fastjson.JSON;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.hrb.locationwork.domain.WorkOrderData;
import com.jtv.locationwork.adapter.BaseListAdapter;
import com.jtv.locationwork.adapter.CommonViewHolder;
import com.jtv.locationwork.entity.CacheCommit;
import com.jtv.locationwork.entity.ItemWonum;
import com.jtv.locationwork.entity.LineCheckJson;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.OkHttpUtil;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.listener.ClickListener;
import com.jtv.locationwork.listener.ConverListener;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.JsonUtil;
import com.jtv.locationwork.util.ScreenUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;
import com.jtv.locationwork.util.WonumUtil;
import com.yixia.weibo.sdk.util.ToastUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 静态检查工单界面
 * <p>
 *
 * @version 2016年3月13日
 */
public class StationWonumAty2 extends SwipePulltoRefershAty2
		implements ObserverCallBack, OnItemClickListener, ClickListener<LineCheckJson> {

	private final int PULLFERSH = 1;
	private String filedmaping = "";
	private static List<ItemWonum> returnED = null;
	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PULLFERSH:
				int height = mSwipeRefreshLayout.getHeight() / 2;
				if (height < 400) {
					height = ScreenUtil.getScreenHeight(StationWonumAty2.this) / 2 + 100;
				}
				dragView(mSwipeRefreshLayout, height);
				break;
			}
		};
	};
	private List<ItemWonum> mArrWorkList;
	private AreaWolistAdapter2 adapter;

	@Override
	public void onClick(View v) {
	}

	// http://localhost:8080/operation/operation/workorder/Workorder.do?
	// method=queryPersonCheckWoInfoList&siteid=S05118&personid=5LuY5bmz5LmQ
	private void initData2() {
		// http://localhost:8080/operation/operation/workorder/Workorder.do?method=queryPersonCheckWoInfoConfig&siteid
		RequestBody requestBody = new FormBody.Builder()//
				// .add("method", "queryPersonCheckWoInfoList")//
				.add("siteid", GlobalApplication.getLocationApp().siteid)//
				.build();
		Request request = new Request.Builder()//
				.url("http://172.16.90.50:8080/operation/operation/workorder/Workorder.do?method=queryPersonCheckWoInfoConfig")//
				.post(requestBody).build();
		OkHttpUtil.enqueue(request, new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				ResponseBody responseBody = response.body();
				if (responseBody == null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							ToastUtils.showToast(StationWonumAty2.this, "responseBody为空");
						}
					});
					return;
				}
				String result = responseBody.string();
				if (TextUtil.isEmpty(result)) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							ToastUtils.showToast(StationWonumAty2.this, "responseBody.string为空");
						}
					});
					return;
				}

			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				handler.post(new Runnable() {

					@Override
					public void run() {
						ToastUtils.showToast(StationWonumAty2.this, "请求失败");
					}
				});
			}
		});
	}

	private void initData() {
		// returnED=;
		RequestBody requestBody = new FormBody.Builder()//
				// .add("method", "queryPersonCheckWoInfoList")//
				.add("siteid", GlobalApplication.getLocationApp().siteid)//
				.add("personid", GlobalApplication.mBase64Lead)//
				.build();
		Request request = new Request.Builder()//
				.url("http://172.16.90.50:8080/operation/operation/workorder/Workorder.do?method=queryPersonCheckWoInfoList")//
				.post(requestBody).build();
		OkHttpUtil.enqueue(request, new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				ResponseBody responseBody = response.body();
				if (responseBody == null) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							ToastUtils.showToast(StationWonumAty2.this, "responseBody为空");
						}
					});
					return;
				}
				String result = responseBody.string();
				if (TextUtil.isEmpty(result)) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							ToastUtils.showToast(StationWonumAty2.this, "responseBody.string为空");
						}
					});
					return;
				}
				try {
					List<WorkOrderData> WorkOrderList = JSON.parseArray(result, WorkOrderData.class);
					System.out.println(WorkOrderList.get(0).getLinename());
				} catch (com.alibaba.fastjson.JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				handler.post(new Runnable() {

					@Override
					public void run() {
						ToastUtils.showToast(StationWonumAty2.this, "请求失败");
					}
				});
			}
		});
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		returnED = WonumCacheUtil.getAvaiableCache(this);
		// initData();
		filedmaping = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.WONUM_TABLE_FIELD, "");// 获取到对应的表字段

		ViewTreeObserver vto2 = mSwipeRefreshLayout.getViewTreeObserver();//
		// 获取不到高度
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (VERSION.SDK_INT >= 18) {// 4.3
					mSwipeRefreshLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					mSwipeRefreshLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}

				new Thread() {

					public void run() {
						handler.sendEmptyMessage(PULLFERSH);
					};
				}.start();
			}
		});

		LayoutParams layoutParams = (LayoutParams) mSwipeRefreshLayout.getLayoutParams();

		if (layoutParams != null) {
			layoutParams.setMargins(20, 15, 20, 0);
			mList.setLayoutParams(layoutParams);
			mList.setDivider(null);
			mList.setDividerHeight(10);
		}

		mList.setOnItemClickListener(this);

		setBackOnClickFinish();
		getHeaderTitleTv().setText("车间工单");
		
		findViewById(R.id.btn_commit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CacheCommit.getInstance().start(StationWonumAty2.this);
			}
		});
	}

	/**
	 * 模拟一个下拉的事件
	 * 
	 * @param view
	 * @param drawHeight
	 */
	public void dragView(View view, int drawHeight) {
		int height = view.getHeight();
		if (drawHeight > 5) {
			height = drawHeight;
		}
		int heightPoint = 1;
		// Instrumentation in = new Instrumentation();

		MotionEvent obtain = null;

		obtain = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
				100, heightPoint, 0);
		view.dispatchTouchEvent(obtain);
		// in.sendPointerSync(obtain);
		Random random = new Random();
		int flag = height / 15;
		if (flag < 0) {
			flag = 0;
		}
		for (int i = 5; i < height;) {
			heightPoint = i;
			int nextInt = random.nextInt(10);

			if (height - i < flag) {// 刚开始慢点
				i = i + nextInt + 1;
			} else if (height - i > flag) {// 结束时候慢点
				i = i + nextInt + 1;
			} else {// 中间滑动加速
				i = i + 10 + nextInt;
			}
			obtain = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE,
					100, heightPoint, 0);
			view.dispatchTouchEvent(obtain);
			// in.sendPointerSync(obtain);
		}

		obtain = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 102,
				heightPoint, 0);
		view.dispatchTouchEvent(obtain);
		// in.sendPointerSync(obtain);
		if (obtain != null) {
			obtain.recycle();
		}
	}

	@Override
	protected View addHeadView() {
		View timeView = View.inflate(StationWonumAty2.this, R.layout.layout_time_header, null);
		initHeader(timeView);
		return timeView;
	}

	public void initHeader(View view) {
		find(view);
		String currDayTime = DateUtil.getCurrDateFormat(DateUtil.style_nyr);
		tv_time.setText(currDayTime);
	}

	public void find(View view) {
		tv_time = (TextView) view.findViewById(R.id.tv_time);
		Button btn_up = (Button) view.findViewById(R.id.btn_up);
		view.findViewById(R.id.ib_foraward).setOnClickListener(timeOnClickListener);
		view.findViewById(R.id.ib_back).setOnClickListener(timeOnClickListener);
		view.findViewById(R.id.btn_up).setOnClickListener(timeOnClickListener);
		btn_up.setOnClickListener(timeOnClickListener);
		tv_time.setOnClickListener(timeOnClickListener);
	}

	private OnClickListener timeOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.btn_up) {
				onRefresh();
				// if (mTotalItem != null && mTotalItem.size() > 5) {
				// mList.setSelection(0);
				// }
				// int height = mSwipeRefreshLayout.getHeight() / 2;
				// if (height < 400) {
				// height = ScreenUtil.getScreenHeight(con) / 2 + 100;
				// }
				// dragView(mSwipeRefreshLayout, height);
			} /*
				 * else if (id == R.id.tv_time) {// 时间选择 Intent calender = new Intent(con, BaseCalenderAty.class);
				 * startActivityForResult(calender, BaseCalenderAty.CALENDER_RESULT); }
				 */else if (id == R.id.ib_foraward) {
				String time = tv_time.getText().toString();
				try {
					Date parseTime = DateUtil.parseTime(DateUtil.style_nyr, time);
					long time2 = parseTime.getTime();
					long beafor = time2 + (1000 * 60 * 60 * 24);
					Date date = new Date(beafor);
					String dateFormat = DateUtil.getDateFormat(date, DateUtil.style_nyr);
					tv_time.setText(dateFormat);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (id == R.id.ib_back) {
				String time = tv_time.getText().toString();
				try {
					Date parseTime = DateUtil.parseTime(DateUtil.style_nyr, time);
					long time2 = parseTime.getTime();
					long beafor = time2 - (1000 * 60 * 60 * 24);// 前一天的时间毫秒值
					Date date = new Date(beafor);
					String dateFormat = DateUtil.getDateFormat(date, DateUtil.style_nyr);
					tv_time.setText(dateFormat);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void onRefresh() {
		super.onRefresh();
		boolean empty = TextUtil.isEmpty(GlobalApplication.area_name);
		String type = "";
		String id = "";
		if (empty) {// 车间
			type = "0";
			id = GlobalApplication.departid;
		} else {
			type = "1";
			id = GlobalApplication.area_id;// 工区id
		}
		// TrackAPI.queryWonumForAreaorShop(this, this, GlobalApplication.siteid, id, type);
		TrackAPI.queryPersonCheckWoinfoList(this, this, GlobalApplication.siteid, GlobalApplication.mBase64Lead,
				tv_time.getText().toString());
	}

	/**
	 * 解析数据
	 * 
	 * @param dataFile
	 * @param str
	 * @return
	 */
	public List<ItemWonum> parseDate(String dataFile, String str) {

		if (TextUtil.isEmpty(dataFile) || TextUtil.isEmpty(str)) {
			return new ArrayList<ItemWonum>();
		}

		JSONArray jsonArray2 = null;
		try {
			jsonArray2 = new JSONArray(str);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mArrWorkList = WonumUtil.swithcLists(jsonArray2);

		return mArrWorkList;
	}

	@Override
	public void back(String data, int method, Object obj) {
		mSwipeRefreshLayout.setRefreshing(false);

		switch (method) {
		case MethodApi.HTTP_QUERY_STATION:
			mSwipeRefreshLayout.setRefreshing(false);

			if (TextUtil.isEmpty(data) || "-1".equals(data)) {
				data = "";
				WonumCacheUtil.clear(this);
				UToast.makeShortTxt(this, getString(R.string.tos_not_found_wonum));
			}

			mArrWorkList = parseDate(filedmaping, data);

			if (mArrWorkList != null) {
				if (adapter == null) {
					// adapter = new AreaWolistAdapter2(this, mArrWorkList, this);
					mList.setAdapter(adapter);
				} else {
					// adapter.onRefresh(mArrWorkList);
				}
			}
		case MethodApi.HTTP_QUERY_CHECK_WONUM:
			// 直线测试数据
//			data = "[{\"endtime\":\"18:00,\",\"linename\":\"京广\",\"linetype\":\"双\",\"wotype\":\"天窗外\",\"starttime\":\"08:00\",\"planname\":\"整理外观(整理外观、)\",\"location\":\"京广-上行-线(724.5-725),京广-下(724.5-725)\",\"locationdata\":[{\"assetnum\":\"PXL000001\",\"classstructureid\":\"1322\",\"description\":\"京广-下行-线\",\"line\":[{\"xxqc\":1023.9,\"planvalue\":[],\"zhxc\":150,\"qhxc\":150,\"qxbj\":1800},{\"xxqc\":82.39,\"planvalue\":[],\"zhxc\":0,\"qhxc\":0,\"qxbj\":15000},{\"xxqc\":80.77,\"planvalue\":[],\"zhxc\":0,\"qhxc\":0,\"qxbj\":14000},{\"xxqc\":1183.25,\"planvalue\":[],\"zhxc\":130,\"qhxc\":130,\"qxbj\":2000},{\"xxqc\":82.32,\"planvalue\":[],\"zhxc\":0,\"qhxc\":0,\"qxbj\":20000},{\"xxqc\":83.29,\"planvalue\":[],\"zhxc\":0,\"qhxc\":0,\"qxbj\":20000},{\"xxqc\":441.55,\"planvalue\":[],\"zhxc\":150,\"qhxc\":150,\"qxbj\":1800},{\"xxqc\":161.5,\"planvalue\":[],\"zhxc\":40,\"qhxc\":40,\"qxbj\":6000},{\"xxqc\":163.37,\"planvalue\":[],\"zhxc\":40,\"qhxc\":40,\"qxbj\":6000},{\"xxqc\":1172.17,\"planvalue\":[],\"zhxc\":150,\"qhxc\":150,\"qxbj\":1800},{\"xxqc\":458.71,\"planvalue\":[],\"zhxc\":130,\"qhxc\":130,\"qxbj\":2000},{\"xxqc\":458.71,\"planvalue\":[],\"zhxc\":130,\"qhxc\":130,\"qxbj\":2000},{\"xxqc\":1172.17,\"planvalue\":[],\"zhxc\":150,\"qxbj\":1800,\"qhxc\":150},{\"xxqc\":163.37,\"planvalue\":[],\"zhxc\":40,\"qxbj\":6000,\"qhxc\":40},{\"xxqc\":161.5,\"planvalue\":[],\"zhxc\":40,\"qhxc\":40,\"qxbj\":6000},{\"xxqc\":441.55,\"planvalue\":[],\"zhxc\":150,\"qxbj\":1800,\"qhxc\":150},{\"xxqc\":83.29,\"planvalue\":[],\"zhxc\":0,\"qxbj\":20000,\"qhxc\":0},{\"xxqc\":82.32,\"planvalue\":[],\"zhxc\":0,\"qhxc\":0,\"qxbj\":20000},{\"xxqc\":1183.25,\"planvalue\":[],\"zhxc\":130,\"qxbj\":2000,\"qhxc\":130},{\"xxqc\":80.77,\"planvalue\":[],\"zhxc\":0,\"qxbj\":14000,\"qhxc\":0},{\"xxqc\":82.39,\"planvalue\":[],\"zhxc\":0,\"qxbj\":15000,\"qhxc\":0},{\"xxqc\":1023.9,\"planvalue\":[],\"zhxc\":150,\"qxbj\":1800,\"qhxc\":150}],\"startmeasure\":\"724.5\",\"endmeasure\":\"725\"},{\"assetnum\":\"PXL000002\",\"classstructureid\":\"1322\",\"description\":\"京广-上行-线\",\"line\":[{\"xxqc\":441.3,\"planvalue\":[],\"zhxc\":150,\"qhxc\":150,\"qxbj\":1800},{\"xxqc\":1152.3,\"planvalue\":[],\"zhxc\":130,\"qhxc\":130,\"qxbj\":1800},{\"xxqc\":468.44,\"planvalue\":[],\"zhxc\":140,\"qhxc\":140,\"qxbj\":2000},{\"xxqc\":1183.78,\"planvalue\":[],\"zhxc\":130,\"qhxc\":130,\"qxbj\":2000},{\"xxqc\":986,\"planvalue\":[],\"zhxc\":160,\"qhxc\":160,\"qxbj\":1700},{\"xxqc\":986,\"planvalue\":[],\"zhxc\":160,\"qxbj\":1700,\"qhxc\":160},{\"xxqc\":1183.78,\"planvalue\":[],\"zhxc\":130,\"qxbj\":2000,\"qhxc\":130},{\"xxqc\":468.44,\"planvalue\":[],\"zhxc\":140,\"qxbj\":2000,\"qhxc\":140},{\"xxqc\":1152.3,\"planvalue\":[],\"zhxc\":130,\"qxbj\":1800,\"qhxc\":130},{\"xxqc\":441.3,\"planvalue\":[],\"zhxc\":150,\"qhxc\":150,\"qxbj\":1800}],\"startmeasure\":\"724.5\",\"endmeasure\":\"725\"}],\"lead\":\"吴仲华(13608677801)\",\"schedstartdate\":\"2016-03-12\",\"wonum\":\"zz31465\"},{\"endtime\":\"18:00\",\"linename\":\"京广\",\"linetype\":\"双\",\"wotype\":\"天窗外\",\"starttime\":\"08:00\",\"planname\":\"整理外观(整理外观、)\",\"location\":\"京广-上行-线(724.5-725),京广-下(724.5-725)\",\"locationdata\":[{\"assetnum\":\"PXL000001\",\"classstructureid\":\"1322\",\"description\":\"京广-下行-线\",\"turnout\":{\"assetattrid\":\"辙叉长\",\"offsetcfg\":[]},\"startmeasure\":\"724.5\",\"endmeasure\":\"725\"},{\"assetnum\":\"PXL000002\",\"classstructureid\":\"1322\",\"description\":\"京广-上行-线\",\"turnout\":{\"assetattrid\":\"辙叉长\",\"offsetcfg\":[]},\"startmeasure\":\"724.5\",\"endmeasure\":\"725\"}],\"lead\":\"吴仲华(13608677801)\",\"schedstartdate\":\"2016-03-13\",\"wonum\":\"zz31530\"}]";
//			data ="[{\"endtime\": \"18:00\",\"lead\": \"长葛工区()\",\"location\": \"京广-下(641.3-807),京广-上行-线(641.3-807)\",\"locationdata\": [{\"assetnum\": \"PXL000001\",\"classstructureid\": \"1322\",\"description\": \"京广-下行-线\",\"endmeasure\": \"643\",\"line\": [{\"endmeasure\": 397.998,\"label\": \"陇海,下行,起止千米(395.998,397.99),短链\",\"planvalue\": [23.4,45.6,78.9,23.4],\"qhxc\": 150,\"qxbj\": 1800,\"startmeasure\": 395.998,\"xxqc\": 1023.9,\"zhxc\": 150},{\"endmeasure\": 381.998,\"label\": \"陇海,下行,起止千米(395.998,397.99),短链\",\"planvalue\": [23.4,45.6,78.9,23.4],\"qhxc\": 0,\"qxbj\": 15000,\"startmeasure\": 380.998,\"xxqc\": 82.39,\"zhxc\": 0},{\"endmeasure\": 382.998,\"label\": \"陇海,下行,起止千米(395.998,397.99),短链\",\"planvalue\": [],\"qhxc\": 0,\"qxbj\": 14000,\"startmeasure\": 381.998,\"xxqc\": 80.77,\"zhxc\": 0},{\"endmeasure\": 383.998,\"label\": \"陇海,下行,起止千米(395.998,397.99),短链\",\"planvalue\": [],\"qhxc\": 130,\"qxbj\": 2000,\"startmeasure\": 382.998,\"xxqc\": 1183.25,\"zhxc\": 130},{\"endmeasure\": 34.998,\"label\": \"陇海,下行,起止千米(395.998,397.99),短链\",\"planvalue\": [],\"qhxc\": 0,\"qxbj\": 20000,\"startmeasure\": 383.998,\"xxqc\": 82.32,\"zhxc\": 0},{\"endmeasure\": 385.998,\"label\": \"陇海,下行,起止千米(395.998,397.99),短链\",\"planvalue\": [],\"qhxc\": 0,\"qxbj\": 20000,\"startmeasure\": 384.998,\"xxqc\": 83.29,\"zhxc\": 0}],\"startmeasure\": \"641.3\"},{\"assetnum\":\"PXL000001\",\"classstructureid\":\"1344\",\"description\":\"京广-下行-线\",\"turnout\":{\"assetattrid\":\"辙叉长\",\"offsetcfg\":[]}}],\"planname\": \"设备检查(测试静态检查原始数据)\",\"schedstartdate\": \"2016-03-14\",\"starttime\": \"8:00\",\"wonum\": \"17136\",\"wotype\": \"天窗外\"}]";
			// 轨道测试数据
			// data =
			// "[{\"endtime\":\"18:00\",\"linename\":\"京广\",\"linetype\":\"双\",\"wotype\":\"天窗外\",\"starttime\":\"08:00\",\"planname\":\"整理外观(整理外观、)\",\"location\":\"京广-上行-线(724.5-725),京广-下(724.5-725)\",\"locationdata\":[{\"assetnum\":\"PXL000001\",\"classstructureid\":\"1322\",\"description\":\"京广-下行-线\",\"turnout\":{\"assetattrid\":\"辙叉长\",\"offsetcfg\":[]},\"startmeasure\":\"724.5\",\"endmeasure\":\"725\"},{\"assetnum\":\"PXL000002\",\"classstructureid\":\"1322\",\"description\":\"京广-上行-线\",\"turnout\":{\"assetattrid\":\"辙叉长\",\"offsetcfg\":[]},\"startmeasure\":\"724.5\",\"endmeasure\":\"725\"}],\"lead\":\"吴仲华(13608677801)\",\"schedstartdate\":\"2016-03-13\",\"wonum\":\"zz31530\"}]";
			List<LineCheckJson> value = JsonUtil.parseArrayObject(data, LineCheckJson.class);
			
			adapter = new AreaWolistAdapter2(this, value, this);
			mList.setAdapter(adapter);
//			if (value != null) {
//				// for (LineCheckJson line : value) {
//				// // System.out.println(line);
//				//
//				// }
//				adapter = new AreaWolistAdapter2(this, value, this);
//				mList.setAdapter(adapter);
//
//			}

			break;
		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {
		mSwipeRefreshLayout.setRefreshing(false);
		back(error, MethodApi.HTTP_QUERY_CHECK_WONUM, null);
	}

	public static final int RESULT = 12;
	private TextView tv_time;

	class AreaWolistAdapter2 extends BaseListAdapter<LineCheckJson> {

		private ClickListener<LineCheckJson> listener;

		public AreaWolistAdapter2(Context con, List<LineCheckJson> list, ClickListener<LineCheckJson> listener) {
			super(con, list);
			this.listener = listener;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = View.inflate(con, R.layout.location_jtv_wolist_lvitem2, null);
			}
			defaultData(position, convertView, parent);
			return convertView;
		}

		protected void defaultData(int position, View convertView, ViewGroup parent) {

			final LineCheckJson mItem = list.get(position);

//			TextView tv_dis_plandate = CommonViewHolder.get(convertView, R.id.tv_dis_plandate);
			TextView tv_dis_lead = CommonViewHolder.get(convertView, R.id.tv_dis_lead);
			TextView tv_dis_wonum = CommonViewHolder.get(convertView, R.id.tv_dis_wonum);
//			TextView tv_dis_planpro = CommonViewHolder.get(convertView, R.id.tv_dis_planpro);
			TextView iv_itemnext = CommonViewHolder.get(convertView, R.id.iv_itemnext);
			TextView tv_dis_jobitem = CommonViewHolder.get(convertView, R.id.tv_dis_jobitem);
			ImageView iv_itemdel = CommonViewHolder.get(convertView, R.id.iv_itemdel);
			TextView tv_wonum = CommonViewHolder.get(convertView, R.id.tv_desceibute);
			TextView tv_time =CommonViewHolder.get(convertView, R.id.tv_zy_time);
			
			
			iv_itemdel.setVisibility(View.GONE);
			String linename = mItem.getLinename();
			String lead = mItem.getLead();
			String lineType = mItem.getLinetype();
			String planname = mItem.getPlanname();
			String wonum = mItem.getWonum();
			tv_dis_wonum.setText("工单编号:" + wonum);
//			tv_dis_plandate.setText("线名:" + linename);
//			tv_dis_planpro.setText("行别:" + lineType);
			tv_dis_jobitem.setText("作业项目:" + planname);
			tv_dis_lead.setText("作业负责人:" + lead);
			tv_time.setText("作业时间:"+mItem.getSchedstartdate()+"  "+mItem.getStarttime()+"-"+mItem.getEndtime());
			iv_itemnext.setOnClickListener(new ConverListener<LineCheckJson>(listener, mItem));

		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		LineCheckJson infoObjs = (LineCheckJson) arg0.getItemAtPosition(arg2);

		if (infoObjs != null) {
			// Intent woInfoIntent = new Intent(this, WoQueryAty.class);
			Intent woInfoIntent = new Intent(this, GdxqActivity2.class);
			String parseString = JsonUtil.parseString(infoObjs);
			woInfoIntent.putExtra(Constants.INTENT_KEY_WONUM, parseString);
			startActivity(woInfoIntent);

		}

	}

	@Override
	public void onClick(View view, LineCheckJson t, Object... obj) {
		if (t != null) {
			// Intent woInfoIntent = new Intent(this, WoQueryAty.class);
			Intent woInfoIntent = new Intent(this, GdxqActivity2.class);
			String parseString = JsonUtil.parseString(t);
			woInfoIntent.putExtra(Constants.INTENT_KEY_WONUM, parseString);
			startActivity(woInfoIntent);

		}
	}
}
