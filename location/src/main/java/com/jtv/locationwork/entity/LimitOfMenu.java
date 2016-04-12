package com.jtv.locationwork.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.fragment.FinderFragment;
import com.jtv.locationwork.listener.ModulItemListener;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.SiteidUtil;
import com.plutus.libraryui.ppw.BasePopUpItemBean;
import com.plutus.libraryui.ppw.PopUpWindowsForIos.onPopupClickCallBack;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;

//构建首页权数据
public class LimitOfMenu {

	// 当数据发生变化时会更新版本
	private int version = 2;

	private ModulItemListener lisitener;// 每个条目的点击回调

	private Context con; // 上下文

	private View adjunctPop;// popup依附在那个控件之上

	private onPopupClickCallBack poplistener;// pop的监听事件

	public static final int STATE_CAN_USE = 1;

	public static final int STATE_UN_CAN_USE = 0;

	public LimitOfMenu(ModulItemListener lisitener, Context con, View mpop, onPopupClickCallBack poplistener) {
		this.lisitener = lisitener;
		this.con = con;
		this.adjunctPop = mpop;
		this.poplistener = poplistener;
	}

	/**
	 * 是否有初始化权限，根据版本判定
	 * 
	 * @return 已经初始化返回true
	 */
	public boolean isUnInitLimit() {

		Cursor mModify = DBFactory.getConnection().queryCursor(Constants.DB_LIMIT_DISPLAY_HOME, new String[] { "lastupdate" }, "id =?",
				new String[] { "1" }, null, null, null);

		if (mModify != null) {
			if (mModify.moveToNext()) {
				int columnIndex = mModify.getColumnIndex("lastupdate");
				double double1 = mModify.getDouble(columnIndex);

				if (double1 == version) {// 代表不需要更新版本
					return true;
				}
			}
			mModify.close();
		}

		return false;
	}

	public ArrayList<ModulItem> getAvailable() {

		ArrayList<ModulItem> mItem = new ArrayList<ModulItem>();
		ArrayList<AvailableBean> mLimit = queryAvailable();

		for (int i = 0; i < mLimit.size(); i++) {
			AvailableBean mBean = mLimit.get(i);
			ModulItem mMenu = getMapingMenu(mBean);

			if (mMenu != null) {
				mItem.add(mMenu);
			}

		}

		return mItem;
	}

	/**
	 * 获取某一个模块的权限是否可用
	 * 
	 * @param id
	 * @return 0代表没有权限 1代表有权限
	 */
	public static int getLimitState(int id) {

		int state = STATE_UN_CAN_USE;

		try {
			List<HashMap<String, String>> mState = DBFactory.getConnection().queryList(Constants.DB_LIMIT_DISPLAY_HOME, new String[] { "state" },
					" id =?", new String[] { "" + id }, null, null, null);
			HashMap<String, String> mMap = mState.get(0);

			String value = mMap.get("state");

			state = Integer.parseInt(value);
			mState.clear();
			mMap.clear();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return state;

	}

	// 通过查询数据库状态为1获取到可用的item
	public ArrayList<AvailableBean> queryAvailable() {

		ArrayList<AvailableBean> mArrTitle = new ArrayList<AvailableBean>();
		List<HashMap<String, String>> mlimit = DBFactory.getConnection().queryList(Constants.DB_LIMIT_DISPLAY_HOME, null, "state = ?",
				new String[] { "1" }, null, null, null);

		for (int i = 0; i < mlimit.size(); i++) {
			HashMap<String, String> mHash = mlimit.get(i);
			String mTitle = mHash.get("title");
			String mID = mHash.get("id");
			AvailableBean mBean = new AvailableBean();
			mBean.setId(Integer.valueOf(mID));
			mBean.setTitle(mTitle);
			mArrTitle.add(mBean);
		}

		mlimit.clear();
		return mArrTitle;
	}

	// 通过查询数据库状态为1获取到可用的item
	public ArrayList<AvailableBean> queryAll() {

		ArrayList<AvailableBean> mArrTitle = new ArrayList<AvailableBean>();
		List<HashMap<String, String>> mlimit = DBFactory.getConnection().queryList(Constants.DB_LIMIT_DISPLAY_HOME, null, null, null, null, null,
				null);

		for (int i = 0; i < mlimit.size(); i++) {
			HashMap<String, String> mHash = mlimit.get(i);
			String mTitle = mHash.get("title");
			String mID = mHash.get("id");
			AvailableBean mBean = new AvailableBean();
			mBean.setId(Integer.valueOf(mID));
			mBean.setTitle(mTitle);
			mArrTitle.add(mBean);
		}

		mlimit.clear();
		return mArrTitle;
	}

	private ModulItem getMapingMenu(AvailableBean mBean) {
		ModulItem item = null;
		switch (mBean.getId()) {
			case 1:
				break;

			case 2:
				// 构建拍摄
				item = new ModulItem();
				item.setBack(lisitener);
				item.setPosition(1);
				item.setStateIcon(R.drawable.ic_home_takephoto);
				item.setTitle(mBean.getTitle());
				break;
			case 3:
				// 构建地图
				item = new ModulItem();
				item.setBack(lisitener);
				item.setPosition(2);
				item.setStateIcon(R.drawable.ic_home_map);
				item.setTitle(mBean.getTitle());
				break;
			case 4:
				// 构建办公
				item = new ModulItem();
				item.setBack(lisitener);
				item.setPosition(3);
				item.setStateIcon(R.drawable.ic_home_workhandover);
				item.setTitle(mBean.getTitle());
				break;
			case 5:
				break;
			case 6:
				// 构建早班数据
				ArrayList<BasePopUpItemBean> mArrWorkHandover = new ArrayList<BasePopUpItemBean>(); // 仿ios样式popup的容器

				// 早班数据
				mArrWorkHandover.add(new BasePopUpItemBean("早班", null, FinderFragment.ID_WORK_HANDOVER_MORNING));

				mArrWorkHandover.add(new BasePopUpItemBean("晚班", null, FinderFragment.ID_WORK_HANDOVER_NIGHT));

				// 构建交班
				ModulItemShow mWorkHandOver = new ModulItemShow(con, adjunctPop, null);
				mWorkHandOver.setBack(lisitener);
				mWorkHandOver.setPosition(5);
				mWorkHandOver.setStateIcon(R.drawable.ic_home_workhandover);
				mWorkHandOver.setTitle(mBean.getTitle());
				mWorkHandOver.getPopForIos().setOnPopUpClickListener(poplistener);
				mWorkHandOver.getPopForIos().addItem(mArrWorkHandover, new BasePopUpItemBean(((Activity) con).getString(R.string.cancel)));

				item = mWorkHandOver;

				break;
			case 7:

				// 考勤容器
				ArrayList<BasePopUpItemBean> mArrClock = new ArrayList<BasePopUpItemBean>();
				mArrClock.add(new BasePopUpItemBean("查看今日考勤", null, FinderFragment.ID_WORKCLOCK_QUERY));
				mArrClock.add(new BasePopUpItemBean("考勤", null, FinderFragment.ID_WORKCLOCK));

				// 构建考勤

				ModulItemShow mWorkClock = new ModulItemShow(con, adjunctPop, null);
				mWorkClock.setBack(lisitener);
				mWorkClock.setPosition(7);
				mWorkClock.setStateIcon(R.drawable.selector_item_set_style);
				mWorkClock.setIcon(R.drawable.ic_timer_auto_grey600_36dp);
				mWorkClock.setTitle(mBean.getTitle());
				mWorkClock.getPopForIos().setOnPopUpClickListener(poplistener);
				mWorkClock.getPopForIos().addItem(mArrClock, new BasePopUpItemBean(con.getString(R.string.cancel)));
				item = mWorkClock;
				break;
			case 8:

				// 机具的容器
				ArrayList<BasePopUpItemBean> mArrTool = new ArrayList<BasePopUpItemBean>();
				mArrTool.add(new BasePopUpItemBean("查看已扫描机具", null, FinderFragment.ID_TOOL_QUERY));
				mArrTool.add(new BasePopUpItemBean("机具", null, FinderFragment.ID_TOOL));

				// 构建工具
				ModulItemShow mTool = new ModulItemShow(con, adjunctPop, null);
				mTool.setBack(lisitener);
				mTool.setStateIcon(R.drawable.selector_item_set_style);
				mTool.setIcon(R.drawable.icon_set_center);
				mTool.setTitle(mBean.getTitle());
				mTool.getPopForIos().setOnPopUpClickListener(poplistener);
				mTool.getPopForIos().addItem(mArrTool, new BasePopUpItemBean(con.getString(R.string.cancel)));
				item = mTool;
				break;
			case 9:

				ArrayList<BasePopUpItemBean> mArrTakePhotoVideo = new ArrayList<BasePopUpItemBean>();
				mArrTakePhotoVideo.add(new BasePopUpItemBean("查看所有所有照片", null, FinderFragment.ID_PHOTO_DCIM));
				mArrTakePhotoVideo.add(new BasePopUpItemBean("照相", null, FinderFragment.ID_WONUM_TAKE_PHOTO));
				mArrTakePhotoVideo.add(new BasePopUpItemBean("视频", null, FinderFragment.ID_WONUM_TAKE_VIDEO));

				// 构建拍摄视频
				ModulItemShow mTakePhotoVideo = new ModulItemShow(con, adjunctPop, null);
				mTakePhotoVideo.setBack(lisitener);
				mTakePhotoVideo.setStateIcon(R.drawable.selector_item_set_style);
				mTakePhotoVideo.setIcon(R.drawable.ic_camera_alt_grey600_36dp);
				mTakePhotoVideo.setTitle(mBean.getTitle());
				mTakePhotoVideo.getPopForIos().setOnPopUpClickListener(poplistener);
				mTakePhotoVideo.getPopForIos().addItem(mArrTakePhotoVideo, new BasePopUpItemBean(con.getString(R.string.cancel)));
				item = mTakePhotoVideo;
				break;

			case 10:// 直接打开相机,不需要这功能了
				break;
			case 11:

				// 构建问题检查

				ArrayList<BasePopUpItemBean> mArr = new ArrayList<BasePopUpItemBean>();
				mArr.add(new BasePopUpItemBean("查看问题检查", null, FinderFragment.ID_LINE_QUESTION_LOOK));
				mArr.add(new BasePopUpItemBean("问题检查", null, FinderFragment.ID_LINE_QUESTION));

				ModulItemShow mLine = new ModulItemShow(con, adjunctPop, null);
				mLine.setBack(lisitener);
				mLine.setPosition(11);
				mLine.setTitle(mBean.getTitle());
				mLine.setStateIcon(R.drawable.ic_home_questiition);
				mLine.getPopForIos().setOnPopUpClickListener(poplistener);
				mLine.getPopForIos().addItem(mArr, new BasePopUpItemBean(con.getString(R.string.cancel)));
				item = mLine;
				break;

			case 12:
				// 构建办公
				item = new ModulItem();
				item.setBack(lisitener);
				item.setPosition(12);
				item.setStateIcon(R.drawable.ic_home_workhandover);
				item.setTitle(mBean.getTitle());
				break;
		}
		return item;
	}

	// 获取一个初始化的json数据
	public String getInitlimit(String siteid) {
		// 查看数据库表limits_display_home
		String json = "";
		if (SiteidUtil.isHLR(siteid)) {// 海拉尔公务段
			JSONObject mJson = new JSONObject();
			try {
				// 把id为1的状态修改成1
				mJson.put("1", "0");// 作业单不需要此功能被删除
				mJson.put("2", "0");// 卡控不需要此功能被删除
				mJson.put("3", "1");// 地图
				mJson.put("4", "1");// 办公
				mJson.put("5", "0");// 设置不需要此功能被删除
				mJson.put("6", "1");// 交班
				mJson.put("7", "1");// 考勤
				mJson.put("8", "1");// 机具
				mJson.put("9", "1");// 拍摄
				mJson.put("10", "1");// 离线拍摄
				mJson.put("11", "0");// 问题检查
				mJson.put("12", "1");// 静态检查
				json = mJson.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			} // 作业单

		} else if (SiteidUtil.isDJD(siteid)) {// 公务机械段
			JSONObject mJson = new JSONObject();
			try {
				mJson.put("1", "0");// 作业单不需要此功能被删除
				mJson.put("2", "0");// 卡控不需要此功能被删除
				mJson.put("3", "1");// 地图
				mJson.put("4", "1");// 办公
				mJson.put("5", "0");// 设置不需要此功能被删除
				mJson.put("6", "0");// 交班
				mJson.put("7", "0");// 考勤
				mJson.put("8", "0");// 机具
				mJson.put("9", "0");// 拍摄
				mJson.put("10", "1");// 离线拍摄
				mJson.put("11", "0");// 问题检查
				mJson.put("12", "1");// 静态检查
			} catch (JSONException e) {
				e.printStackTrace();
			}
			json = mJson.toString();
		} else if (SiteidUtil.isDXD(siteid)) {// 公务大修段
			JSONObject mJson = new JSONObject();
			try {
				mJson.put("1", "0");// 作业单不需要此功能被删除
				mJson.put("2", "0");// 卡控 不需要此功能被删除
				mJson.put("3", "1");// 地图
				mJson.put("4", "1");// 办公
				mJson.put("5", "0");// 设置 不需要此功能被删除
				mJson.put("6", "0");// 交班
				mJson.put("7", "0");// 考勤
				mJson.put("8", "0");// 机具
				mJson.put("9", "0");// 拍摄
				mJson.put("10", "1");// 离线拍摄
				mJson.put("11", "0");// 问题检查
				mJson.put("12", "1");// 静态检查
			} catch (JSONException e) {
				e.printStackTrace();
			}
			json = mJson.toString();
		} else if (SiteidUtil.isZZQGD(siteid)) {// 郑州桥公段
			JSONObject mJson = new JSONObject();
			try {

				// 把id为1的状态修改成1
				mJson.put("1", "0");// 作业单 不需要此功能被删除
				mJson.put("2", "0");// 卡控 不需要此功能被删除
				mJson.put("3", "1");// 地图
				mJson.put("4", "1");// 办公
				mJson.put("5", "0");// 设置 不需要此功能被删除
				mJson.put("6", "0");// 交班
				mJson.put("7", "0");// 考勤
				mJson.put("8", "0");// 机具
				mJson.put("9", "0");// 拍摄
				mJson.put("10", "1");// 离线拍摄
				mJson.put("11", "0");// 问题检查
				mJson.put("12", "1");// 静态检查
				json = mJson.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			json = mJson.toString();
		} else if (SiteidUtil.isDQD(siteid)) {// 大庆段
			JSONObject mJson = new JSONObject();
			try {
				// 把id为1的状态修改成1
				mJson.put("1", "0");// 作业单不需要此功能被删除
				mJson.put("2", "0");// 卡控不需要此功能被删除
				mJson.put("3", "1");// 地图
				mJson.put("4", "1");// 办公
				mJson.put("5", "0");// 设置不需要此功能被删除
				mJson.put("6", "1");// 交班
				mJson.put("7", "1");// 考勤
				mJson.put("8", "1");// 机具
				mJson.put("9", "1");// 拍摄
				mJson.put("10", "1");// 离线拍摄
				mJson.put("11", "1");// 问题检查
				mJson.put("12", "1");// 静态检查
				json = mJson.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			} // 作业单

		} else {
			JSONObject mJson = new JSONObject();
			try {
				mJson.put("1", "0");
				mJson.put("2", "0");
				mJson.put("3", "1");
				mJson.put("4", "1");
				mJson.put("5", "0");
				mJson.put("6", "1");
				mJson.put("7", "1");
				mJson.put("8", "1");
				mJson.put("9", "1");
				mJson.put("10", "1");// 离线拍摄
				mJson.put("11", "0");// 问题检查
				mJson.put("12", "1");// 静态检查
			} catch (JSONException e) {
				e.printStackTrace();
			}
			json = mJson.toString();
		}
		return json;
	}

	/**
	 * 修改功能权限,需要传入一个json数据
	 */
	public void modifyLimit(String jsonString) {

		if ("".equals(jsonString) || jsonString == null) {
			return;
		}

		JSONObject mJson = null;

		try {
			mJson = new JSONObject(jsonString);
		} catch (Exception e) {// 可能返回的是数组，需转换成jsonobject
			jsonString = jsonString.replace("[", "");
			jsonString = jsonString.replace("]", "");
			jsonString = jsonString.replace("{", "");
			jsonString = jsonString.replace("}", "");
			jsonString = "{" + jsonString + "}";
			try {
				mJson = new JSONObject(jsonString);
			} catch (Exception e2) {
				e2.printStackTrace();
				return;
			}
		}

		ArrayList<AvailableBean> queryAll = queryAll();
		for (int i = 0; i < queryAll.size(); i++) {
			AvailableBean mItem = queryAll.get(i);
			int id = mItem.getId();
			String state = mJson.optString("" + id);

			if (id == 1 || id == 2 || id == 5) {// 这几个永远隐藏，因为不需要这个功能了
				state = STATE_UN_CAN_USE + "";
			}

			// if(id==11){
			// state="1";
			// }

			if (!TextUtils.isEmpty(state)) {
				ContentValues contentValues = new ContentValues();
				contentValues.put("state", Integer.valueOf(state));
				contentValues.put("lastupdate", version);
				int update = DBFactory.getConnection().update(Constants.DB_LIMIT_DISPLAY_HOME, "id   = ?", new String[] { id + "" }, contentValues);

				// if(update<0){//更新失败
				// contentValues.put("title", value);
				// DBFactory.getConnection().insert(Constants.DB_LIMIT_DISPLAY_HOME,
				// contentValues);
				// }
				// System.out.println("=="+update);
			}

		}
	}
}

class AvailableBean {
	private int id;
	private String title;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
