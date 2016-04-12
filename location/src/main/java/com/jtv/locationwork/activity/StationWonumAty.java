package com.jtv.locationwork.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import com.jtv.base.activity.SwipePulltoRefershAty;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.adapter.CommonViewHolder;
import com.jtv.locationwork.adapter.WorkListAdapter;
import com.jtv.locationwork.entity.ItemWoListAttribute;
import com.jtv.locationwork.entity.ItemWonum;
import com.jtv.locationwork.httputil.AnsynHttpRequest;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.listener.OnItemDelClickListenter;
import com.jtv.locationwork.listener.OnItemNextClickListener;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.ScreenUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;
import com.jtv.locationwork.util.WonumUtil;

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
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class StationWonumAty extends SwipePulltoRefershAty
		implements ObserverCallBack, OnItemNextClickListener, OnItemDelClickListenter, OnItemClickListener {

	private final int PULLFERSH = 1;

	private String filedmaping = "";

	private static List<ItemWonum> returnED = null;

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PULLFERSH:
				int height = mSwipeRefreshLayout.getHeight() / 2;
				if (height < 400) {
					height = ScreenUtil.getScreenHeight(StationWonumAty.this) / 2 + 100;
				}
				dragView(mSwipeRefreshLayout, height);
				break;

			}

		};
	};

	private List<ItemWonum> mArrWorkList;

	private AreaWolistAdapter adapter;

	@Override
	public void onClick(View v) {

	}

	@Override
	protected void init(Bundle savedInstanceState) {
		returnED = WonumCacheUtil.getAvaiableCache(this);
		filedmaping = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.WONUM_TABLE_FIELD, "");// 获取到对应的表字段

		ViewTreeObserver vto2 = mSwipeRefreshLayout.getViewTreeObserver();// 获取不到高度
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
		return null;
	}

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
		TrackAPI.queryWonumForAreaorShop(this, this, GlobalApplication.siteid, id, type);
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
					adapter = new AreaWolistAdapter(this, mArrWorkList, this, this);
					mList.setAdapter(adapter);
				} else {
					adapter.onRefresh(mArrWorkList);
				}
			}
		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {
		mSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onItemNextListener(ItemWonum infoObj, int position) {
		if (infoObj != null) {
			Intent woInfoIntent = new Intent(this, GdxqActivity.class);
			woInfoIntent.putExtra(Constants.INTENT_KEY_WONUM, infoObj);
			startActivity(woInfoIntent);
		}
	}

	public static final int RESULT = 12;

	@Override
	public void onItemDelClickListener(ItemWonum delObj, int position) {

		if (position < 0) {

			WonumCacheUtil.removeCacheWonumItem(this, delObj);
			Intent intent2 = new Intent();
			setResult(RESULT, intent2);
			finish();
			return;
		}

		Intent intent2 = new Intent();
		intent2.putExtra("result", delObj);
		setResult(RESULT, intent2);
		finish();

	}

	class AreaWolistAdapter extends WorkListAdapter {

		public AreaWolistAdapter(Context con, List<ItemWonum> list, OnItemNextClickListener mNextListener,
				OnItemDelClickListenter mDelListener) {
			super(con, list, mNextListener, mDelListener);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = View.inflate(con, R.layout.location_jtv_wolist_lvitem, null);
			}
			defaultData(position, convertView, parent);
			return convertView;
		}

		@Override
		protected void defaultData(int position, View convertView, ViewGroup parent) {

			final ItemWonum mItem = list.get(position);

			TextView tv_dis_plandate = CommonViewHolder.get(convertView, R.id.tv_dis_plandate);
			TextView tv_dis_lead = CommonViewHolder.get(convertView, R.id.tv_dis_lead);
			TextView tv_dis_wonum = CommonViewHolder.get(convertView, R.id.tv_dis_wonum);
			TextView tv_dis_planpro = CommonViewHolder.get(convertView, R.id.tv_dis_planpro);
			TextView iv_itemnext = CommonViewHolder.get(convertView, R.id.iv_itemnext);
			TextView tv_desceibute = CommonViewHolder.get(convertView, R.id.tv_desceibute);
			ImageView iv_itemdel = CommonViewHolder.get(convertView, R.id.iv_itemdel);

			iv_itemdel.setVisibility(View.GONE);
			// iv_itemdel.setBackgroundResource(R.drawable.selector_gou_ok_state);
			// iv_itemdel.setSelected(false);
			//
			// if (mDelListener == null) {
			// iv_itemdel.setVisibility(View.GONE);
			// }

			tv_dis_plandate.setText("");
			tv_dis_lead.setText("");
			tv_dis_wonum.setText("");
			tv_dis_planpro.setText("");

			HashMap<String, ItemWoListAttribute> hashMap = mItem.get();

			Set<String> keySet = hashMap.keySet();

			TextView[] item = { tv_dis_wonum, tv_dis_plandate, tv_dis_planpro, tv_dis_lead };

			int i = 0;

			for (String key : keySet) {

				if (i >= item.length) {
					break;
				}

				ItemWoListAttribute itemWoListAttribute = hashMap.get(key);

				String disPlayname = itemWoListAttribute.getDisPlayname();

				if (TextUtil.isEmpty(disPlayname)) {
					continue;
				}

				item[i].setText(disPlayname + ":" + itemWoListAttribute.getDisValue());

				i++;

			}

			item = null;

			iv_itemnext.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					mNextListener.onItemNextListener(mItem, curPosition);
				}
			});

			// 已经返回的数据不需要保存
			for (int j2 = 0; j2 < returnED.size(); j2++) {

				ItemWonum ItemWonum2 = returnED.get(j2);

				if (ItemWonum2 != null && ItemWonum2.equals(mItem) || ItemWonum2 == mItem) {

					tv_desceibute.setSelected(true);

					break;
				}
			}

			int flag = curPosition;
			tv_desceibute.setText("添  加");

			if (tv_desceibute.isSelected()) {
				flag = -1;
				tv_desceibute.setText("取消添加");
			}

			tv_desceibute.setOnClickListener(new OnItemDelClick(flag, mItem));

		}

		class OnItemDelClick implements OnClickListener {

			private int flag = 0;
			private ItemWonum item;

			public OnItemDelClick(int flag, ItemWonum item) {
				this.flag = flag;
				this.item = item;
			}

			@Override
			public void onClick(View v) {
				mDelListener.onItemDelClickListener(item, flag);
			}

		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		ItemWonum infoObjs = (ItemWonum) arg0.getItemAtPosition(arg2);

		if (infoObjs != null) {
			// Intent woInfoIntent = new Intent(this, WoQueryAty.class);

			Intent woInfoIntent = new Intent(this, GdxqActivity.class);
			woInfoIntent.putExtra(Constants.INTENT_KEY_WONUM, infoObjs);
			startActivity(woInfoIntent);

		}

	}

}
