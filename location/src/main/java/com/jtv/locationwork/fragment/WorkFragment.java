package com.jtv.locationwork.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

import com.jtv.base.activity.BaseCalenderAty;
import com.jtv.base.activity.BaseSwipRefershGragment;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.activity.CheckAty;
import com.jtv.locationwork.activity.GdxqActivity;
import com.jtv.locationwork.activity.HomeFragment;
import com.jtv.locationwork.activity.PhotoTreeAty;
import com.jtv.locationwork.activity.StationWonumAty;
import com.jtv.locationwork.activity.TakePhotoAty;
import com.jtv.locationwork.activity.WonumCacheUtil;
import com.jtv.locationwork.adapter.WorkListAdapter;
import com.jtv.locationwork.entity.ItemWoListAttribute;
import com.jtv.locationwork.entity.ItemWonum;
import com.jtv.locationwork.httputil.AnsynHttpRequest;
import com.jtv.locationwork.httputil.HttpApi;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.listener.OnItemDelClickListenter;
import com.jtv.locationwork.listener.OnItemNextClickListener;
import com.jtv.locationwork.util.CalculateNearWorkListUtils;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.NetUtil;
import com.jtv.locationwork.util.ScreenUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;
import com.jtv.locationwork.util.WonumUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class WorkFragment extends BaseSwipRefershGragment implements ObserverCallBack, OnClickListener,
		OnItemNextClickListener, OnItemDelClickListenter, OnItemClickListener {

	private WorkListAdapter adapter = null;

	public List<ItemWonum> mTotalItem = new ArrayList<ItemWonum>();// 展示的总共

	public List<ItemWonum> mPersionItem = new ArrayList<ItemWonum>();// 当前展示的个人工单

	public List<ItemWonum> mAreaWorklist = new ArrayList<ItemWonum>();// 工区下的追加工单

	public static final int RESULTCODE = 0X11;

	String dataScr;// 后台返回的数据

	String dataField;// 数据映射键

	static Date day = new Date();// 施工日期

	private TextView tv_time;

	boolean stopOnGlobalLayout = false;

	private Button btn_up;

	@Override
	protected View addHeadView() {
		View view = LayoutInflater.from(con).inflate(R.layout.location_jtv_wonum_head, null);
		return view;
	}

	@Override
	public void onStop() {
		super.onStop();

		int startTimeDistanceMinute = GlobalApplication.getLocationApp().getStartTimeDistanceMinute();

		if (startTimeDistanceMinute > 60 * 8) {// 如果启动的时间大于8小时需要更新

			if (tv_time != null) {
				currDayTime = DateUtil.getCurrDateFormat(DateUtil.style_nyr);
			}

			mAreaWorklist = WonumCacheUtil.getAvaiableCache(con);
			GlobalApplication.getLocationApp().requestVersionUpdate();

		}

	};

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (mTotalItem != null && mTotalItem.size() > 0) {

					if (adapter == null) {
						adapter = new WorkListAdapter(con, mTotalItem, WorkFragment.this, WorkFragment.this);
						mList.setAdapter(adapter);
					} else {
						adapter.onRefresh(mTotalItem);
					}

				}
				break;
			case 2:
				int height = mSwipeRefreshLayout.getHeight() / 2;

				if (height < 400) {
					height = ScreenUtil.getScreenHeight(con) / 2 + 100;
				}

				dragView(mSwipeRefreshLayout, height);
				break;
			}
		};
	};

	private String currDayTime = "";

	public void init() {

		find();

		currDayTime = DateUtil.getCurrDateFormat(DateUtil.style_nyr);

		tv_time.setText(currDayTime);
	}

	public void find() {

		tv_time = (TextView) view.findViewById(R.id.tv_time);

		btn_up = (Button) view.findViewById(R.id.btn_up);

		view.findViewById(R.id.ib_foraward).setOnClickListener(this);
		view.findViewById(R.id.ib_back).setOnClickListener(this);

		btn_up.setOnClickListener(this);
		tv_time.setOnClickListener(this);
	}

	public boolean isCurrDay() {

		String time = getEditTime();

		if (TextUtils.equals(currDayTime, time)) {
			return true;
		} else {
			currDayTime = DateUtil.getCurrDateFormat(DateUtil.style_nyr);
		}

		return false;
	}

	public void updateWorkorders() {

		String time = getEditTime();

		boolean empty = TextUtil.isEmpty(GlobalApplication.area_id);

		String siteid = "";

		if (!empty) {// 代表没有工区,不需要传站点
			siteid = GlobalApplication.siteid;
		}

		TrackAPI.getALLWolist(con, this, GlobalApplication.mBase64Lead, time, siteid, null);

	}

	private String getEditTime() {

		String time = "";

		time = tv_time.getText().toString();

		return time;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_up) {

			if (mTotalItem != null && mTotalItem.size() > 5) {
				mList.setSelection(0);
			}

			int height = mSwipeRefreshLayout.getHeight() / 2;

			if (height < 400) {
				height = ScreenUtil.getScreenHeight(con) / 2 + 100;
			}

			dragView(mSwipeRefreshLayout, height);

		} else if (id == R.id.tv_time) {// 时间选择

			Intent calender = new Intent(con, BaseCalenderAty.class);

			startActivityForResult(calender, BaseCalenderAty.CALENDER_RESULT);

		} else if (id == R.id.ib_foraward) {

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

	boolean mParseFlag = false;

	/**
	 * 解析数据
	 * 
	 * @param dataFile
	 * @param str
	 * @return
	 */
	public List<ItemWonum> parsePersionDate(String dataFile, String str) {

		if (mParseFlag)
			return mPersionItem;

		if (TextUtil.isEmpty(dataFile) || TextUtil.isEmpty(str)) {
			return new ArrayList<ItemWonum>();
		}

		mParseFlag = true;
		JSONArray jsonArray2 = null;

		try {
			jsonArray2 = new JSONArray(str);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mPersionItem = WonumUtil.swithcLists(jsonArray2);

		mParseFlag = false;
		return mPersionItem;
	}

	@Override
	public void back(String data, int method, Object osbj) {
		switch (method) {
		case MethodApi.HTTP_LOC_GET_WOLIST:
			mSwipeRefreshLayout.setRefreshing(false);

			if (TextUtil.isEmpty(data) || "-1".equals(data)) {

				SpUtiles.NearWorkListInfo.clearALL();
				UToast.makeShortTxt(con, getString(R.string.tos_not_found_wonum));

				mPersionItem.clear();

				totalWonum();

				refershAdapter();

				caluWonum();

				return;
			}

			// LocationApp.getLocationApp().saveWonum(data);//测试数据
			dataScr = data;

			mPersionItem = parsePersionDate(dataField, data);

			totalWonum();

			caluWonum();

			refershAdapter();
			break;
		case MethodApi.HTTP_REQUEST_WOLISTFAILED:

			if (TextUtil.isEmpty(data)) {
				return;
			}

			try {
				new JSONArray(data);
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}

			Editor edit = SpUtiles.BaseInfo.mbasepre.edit();

			edit.putString(CoustomKey.WONUM_TABLE_FIELD, data);

			edit.commit();
			dataField = data;
			break;
		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {
		switch (method) {
		case MethodApi.HTTP_LOC_GET_WOLIST:

			mTotalItem.clear();
			mPersionItem.clear();

			totalWonum();

			refershAdapter();

			mSwipeRefreshLayout.setRefreshing(false);
			break;

		default:
			break;
		}
	}

	private void caluWonum() {
		if (!TextUtil.isEmpty(dataField) && mTotalItem.size() > 0) {// 需要计算工单

			if (isCurrDay()) {// 代表本地保存工单

				String calculateWonum = CalculateNearWorkListUtils.calculateWonum(mTotalItem);

				if (!TextUtil.isEmpty(calculateWonum))
					UToast.makeShortTxt(con, "最近工单:" + calculateWonum);

			}

		}
	}

	/**
	 * 计算总的工单
	 */
	private void totalWonum() {

		mTotalItem.clear();

		mTotalItem.addAll(mPersionItem);

		boolean currDay = isCurrDay();
		if (currDay) {
			mTotalItem.addAll(mAreaWorklist);
		}
	}

	private void refershAdapter() {

		if (mTotalItem != null) {
			// sortList(workListArray);
			if (adapter == null) {
				adapter = new WorkListAdapter(con, mTotalItem, this, this);
				mList.setAdapter(adapter);
			} else {
				adapter.onRefresh(mTotalItem);
			}
		}

		HomeFragment home = (HomeFragment) con;
		if (home.getMessageRight() != null) {
			home.getMessageRight().setText(mTotalItem.size() + "");
		}
	}

	@Override
	public void data(Bundle savedInstanceState) {
		super.data(savedInstanceState);
		init(savedInstanceState);
	}

	@SuppressLint("NewApi")
	protected void init(Bundle savedInstanceState) {
		GlobalApplication.getLocationApp().setSiteid(GlobalApplication.siteid);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		GlobalApplication.initDB();

		mAreaWorklist = WonumCacheUtil.getAvaiableCache(con);

		LayoutParams layoutParams = (LayoutParams) mSwipeRefreshLayout.getLayoutParams();

		if (layoutParams != null) {
			layoutParams.setMargins(20, 15, 20, 0);
			mList.setLayoutParams(layoutParams);
			mList.setDivider(null);
			mList.setDividerHeight(10);
		}

		mList.setOnItemClickListener(this);

		View footView = View.inflate(con, R.layout.location_jtv_other_worder_item, null);
		mList.addFooterView(footView);

		init();

		// 从本地加载数据
		String time = getEditTime();

		boolean empty = TextUtil.isEmpty(GlobalApplication.area_id);

		String siteid = "";

		if (!empty) {// 代表没有工区,不需要传站点
			siteid = GlobalApplication.siteid;
		}

		String url = HttpApi.Http_interface_get_wonumlist_bjd + "&personid=" + GlobalApplication.mBase64Lead
				+ "&schedstartdate=" + time + "&siteid=" + siteid;
		String data = AnsynHttpRequest.getFromDiskCache(url);
		dataField = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.WONUM_TABLE_FIELD, "");

		if (!TextUtil.isEmpty(data) && !TextUtil.isEmpty(dataField)) {// 成功从本地读取到数据

			back(data, MethodApi.HTTP_LOC_GET_WOLIST, null);

			if (NetUtil.hasConnectedNetwork(getContext())) {// 有网络在从网络上获取
				onRefresh();
			}

		} else {// 需要显示刷新动画，在从网络获取
			ViewTreeObserver vto2 = mSwipeRefreshLayout.getViewTreeObserver();//
			// 获取不到高度
			vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					if (VERSION.SDK_INT >= 18) {// 4.3
						mSwipeRefreshLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					} else {
						mSwipeRefreshLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
					if (stopOnGlobalLayout) {
						return;
					}
					stopOnGlobalLayout = true;

					new Thread() {

						public void run() {
							handler.sendEmptyMessage(2);
						};
					}.start();
				}
			});
		}

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
	public void onRefresh() {

		if (TextUtil.isEmpty(dataField)) {// 获取标的解析字段

			dataField = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.WONUM_TABLE_FIELD, "");

			if (TextUtil.isEmpty(dataField)) {

				TrackAPI.getWolistField(con, this, GlobalApplication.siteid);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

		updateWorkorders();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case BaseCalenderAty.CALENDER_RESULT:

			Calendar mCalender = (Calendar) data.getSerializableExtra(BaseCalenderAty.CALENDER_DATA);
			Date time = mCalender.getTime();

			String mDate = DateUtil.getDateFormat(time, DateUtil.style_nyr);
			tv_time.setText(mDate);

			break;
		case StationWonumAty.RESULT:

			ItemWonum other = null;

			if (data.hasExtra("result")) {
				other = data.getParcelableExtra("result");
			}

			if (other != null) {
				WonumCacheUtil.saveCacheWonumItem(con, other);// 把追加工单保存到本地
			}

			mAreaWorklist = WonumCacheUtil.getAvaiableCache(con);

			totalWonum();

			refershAdapter();

			if (mTotalItem.size() < 1) {
				return;
			}

			caluWonum();
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		ItemWonum infoObjs = (ItemWonum) arg0.getItemAtPosition(arg2);

		if (infoObjs == null) {// 是否是最后一个
			Intent intent = new Intent(con, StationWonumAty.class);
			startActivityForResult(intent, 12);
		} else {

			if (infoObjs != null) {
				// Intent woInfoIntent = new Intent(this, WoQueryAty.class);
				Intent woInfoIntent = new Intent(con, GdxqActivity.class);
				woInfoIntent.putExtra(Constants.INTENT_KEY_WONUM, infoObjs);
				startActivity(woInfoIntent);
			}

		}

	}

	public void onItemNextListener(ItemWonum infoObjs, int position) {

		if (position < 0) {
			Intent woInfoIntent = new Intent(con, CheckAty.class);
			woInfoIntent.putExtra(Constants.INTENT_KEY_WONUM, infoObjs);
			startActivity(woInfoIntent);
		}

		if (infoObjs != null && position >= 0) {
			Intent woInfoIntent = new Intent(con, GdxqActivity.class);
			woInfoIntent.putExtra(Constants.INTENT_KEY_WONUM, infoObjs);
			startActivity(woInfoIntent);
		}
	}

	@Override
	public void onItemDelClickListener(ItemWonum delObj, int position) {

		if (GlobalApplication.isDQ() || GlobalApplication.isHLE()) {// 大庆和海拉尔没有卡控

			Intent intent = new Intent(con, TakePhotoAty.class);
			HashMap<String, ItemWoListAttribute> field = delObj.get();

			ItemWoListAttribute itemWoListAttribute = field.get("wonum");

			intent.putExtra(CoustomKey.WONUM, itemWoListAttribute.getDisValue());
			startActivity(intent);

			return;
		}

		Intent intent = new Intent(con, PhotoTreeAty.class);
		HashMap<String, ItemWoListAttribute> field = delObj.get();

		ItemWoListAttribute itemWoListAttribute = field.get("wonum");
		ItemWoListAttribute type = field.get("type");
		ItemWoListAttribute isimportant = field.get("isimportant");

		if (type != null) {
			intent.putExtra("auto_type", type.getDisValue());
		}

		if (isimportant != null) {
			intent.putExtra("isimportant", isimportant.getDisValue());
		}

		if (itemWoListAttribute != null)
			intent.putExtra(CoustomKey.WONUM, itemWoListAttribute.getDisValue());

		startActivity(intent);

	}

}
