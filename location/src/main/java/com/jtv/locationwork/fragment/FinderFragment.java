package com.jtv.locationwork.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.jtv.base.util.AplicationUtils;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.hrb.locationwork.StaticCheckActivity;
import com.jtv.locationwork.activity.CacheAty;
import com.jtv.locationwork.activity.CheckAty;
import com.jtv.locationwork.activity.CheckCacheAty;
import com.jtv.locationwork.activity.FaceAty;
import com.jtv.locationwork.activity.FileManagerAty;
import com.jtv.locationwork.activity.GdxqActivity2;
import com.jtv.locationwork.activity.MapAty;
import com.jtv.locationwork.activity.PhotoAty;
import com.jtv.locationwork.activity.PreviewDCIMAty;
import com.jtv.locationwork.activity.QueryToolAty;
import com.jtv.locationwork.activity.StationWonumAty2;
import com.jtv.locationwork.activity.TakePhotoAty;
import com.jtv.locationwork.activity.TakeVideoAlbum;
import com.jtv.locationwork.activity.WorkHandOverTabAty;
import com.jtv.locationwork.entity.LimitOfMenu;
import com.jtv.locationwork.entity.ModulItem;
import com.jtv.locationwork.entity.ModulItemShow;
import com.jtv.locationwork.httputil.AnsynHttpRequest;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.listener.ModulItemListener;
import com.jtv.locationwork.services.GpsServices;
import com.jtv.locationwork.util.BitmapUtils;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.DiskCacheUtil;
import com.jtv.locationwork.util.ImageUtil;
import com.jtv.locationwork.util.ShareUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TTSUtil;
import com.jtv.locationwork.util.TextUtil;
import com.plutus.libraryui.ppw.AddPopWindow;
import com.plutus.libraryui.ppw.PopUpItemBean;
import com.plutus.libraryui.ppw.PopUpWindowsForIos.onPopupClickCallBack;
import com.spore.jni.FaceUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

public class FinderFragment extends BaseGridMenuFragment
		implements ModulItemListener, onPopupClickCallBack, ObserverCallBack {

	public static final int ID_WORK_HANDOVER_MORNING = 100;// 考勤早班

	public static final int ID_WORK_HANDOVER_NIGHT = 101;// 考勤晚班

	public static final int ID_TOOL_QUERY = 200;// 机具查询

	public static final int ID_TOOL = 201;// 机具

	public static final int ID_WORKCLOCK_QUERY = 300;// 考勤查询

	public static final int ID_WORKCLOCK = 301;// 考勤

	public static final int ID_WONUM_TAKE_PHOTO = 400;// 考勤拍照

	public static final int ID_WONUM_TAKE_VIDEO = 401;// 考勤视频

	public static final int ID_PHOTO_DCIM = 502;// 相册

	public static final int ID_LINE_QUESTION_LOOK = 1000;// 查看线路检查

	public static final int ID_LINE_QUESTION = 1001;// 线路检查
	
	public static final int ID_STATIC_CHECK = 1001;// 静态检查

	protected ArrayList<ModulItem> mGridItem = new ArrayList<ModulItem>();// gridview显示条目的容器

	// protected ArrayList<LayoutInfo> mArrPop;

	protected AddPopWindow mPopup;

	private String mWonum;// 最近工单号

	private static final int REFERSH_HOME = 0X10;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFERSH_HOME:
				refersh();
				break;

			default:
				break;
			}

		};
	};

	private String mroot_image;

	private TTSUtil ttsUtil;

	protected void onCreatInit(Bundle savedInstanceState) {
		GlobalApplication.getLocationApp().setSiteid(null);

		GlobalApplication.getLocationApp().setLead(null);
		init();
		ttsUtil = new TTSUtil(con);
		mGridView.setOnItemClickListener(this);

		FaceUtil.initContext(con);
		// SQLiteDatabase readableDatabase =
		// DBFactory.getConnection().getWritableDatabase();
		// BuildTree configurationTreeForDB = new DateBaseTree.BuildTree();
		// ArrayList<Integer> arrayList = new ArrayList<Integer>();
		//
		// DateBaseTree instance =
		// configurationTreeForDB.setDb(readableDatabase).getInstance();
		// arrayList.add(1);
		//// instance.insertLevelTreeNode(arrayList);
		//
		// ParmterMutableTreeNode tree = instance.getTree();
		// System.out.println("======"+tree.toStringPaths(tree));
		new AsynBuildHome().start();
	}

	protected void init() {

		// String test = SpUtiles.BaseInfo.mbasepre.getString("testtest", "");
		// if(TextUtil.isEmpty(test)){
		// LocationApp.deleteDB();uil
		// LocationApp.initDB();
		// SpUtiles.BaseInfo.mbasepre.edit().putString("testtest",
		// "test").commit();
		// }else{
		//
		// }

		boolean mGpsServices = AplicationUtils.isServiceRunning(con, "GpsServices");// 保证服务在运行
		if (!mGpsServices) {
			Intent intent = new Intent(con, GpsServices.class);
			con.startService(intent);
		}

	}

	// 每个条目的点击事件
	@Override
	public void doSomeThing(Object... obj) {
		ModulItem mItem = null;
		if (obj != null) {
			mItem = (ModulItem) obj[0];
		}

		if (mItem == null)
			return;
		Intent mIntent = null;
		switch (mItem.getPosition()) {

		case 0:
			break;

		case 1:

			break;
		case 2:
			mIntent = new Intent(con, MapAty.class);
			break;

		case 3:
			mIntent = new Intent(con, FileManagerAty.class);
			break;
		case 4:
			break;

		case 5:

			break;
		case 6:

			break;

		case 7:

			break;
		case 10:
			
			break;
		case 12:
			//静态检查
			mIntent = new Intent(con, StationWonumAty2.class);
			break;
		}

		if (mIntent != null) {
			startActivity(mIntent);
		}
	}

	private void doWonumTakePhoto() {
		Intent mIntent = new Intent(con, TakePhotoAty.class);
		mIntent.putExtra(CoustomKey.WONUM, SpUtiles.NearWorkListInfo.getWonum());// 工单编号
		mIntent.putExtra(Constants.TYPE, 3);
		startActivity(mIntent);
	}

	private void doWonumTakeVideo() {
		Intent mIntent = new Intent(con, TakeVideoAlbum.class);
		mIntent.putExtra(CoustomKey.WONUM, SpUtiles.NearWorkListInfo.getWonum());// 工单编号
		mIntent.putExtra(Constants.TYPE, 3);
		startActivity(mIntent);
	}

	// popupforios 外置触摸关闭事件
	@Override
	public void outSideonclick(View arg0) {
		// mPopForIosStyle.closePopUp();

	}

	// popupforios 事件的取消事件
	@Override
	public void cancelonclick(View arg0, PopUpItemBean pop) {
		// if (mPopForIosStyle != null)
		// mPopForIosStyle.closePopUp();
		ModulItemShow.closedPopIng();
	}

	// popupforios的每个条目的点击事件
	@Override
	public void itemonclick(View arg0, PopUpItemBean pop) {
		Intent mIntent = null;
		switch (pop.getID()) {
		case ID_WORK_HANDOVER_MORNING:
			mIntent = new Intent(con, WorkHandOverTabAty.class).putExtra(Constants.TYPE, Constants.MORNING);
			break;

		case ID_WORK_HANDOVER_NIGHT:
			mIntent = new Intent(con, WorkHandOverTabAty.class).putExtra(Constants.TYPE, Constants.NINGTH);
			break;
		case ID_WORKCLOCK_QUERY:
			mIntent = new Intent(con, CacheAty.class);
			mIntent.putExtra("key", DiskCacheUtil.FACE_KEY);
			break;
		case ID_WORKCLOCK:
			// if (TextUtils.isEmpty(SpUtiles.NearWorkListInfo.getWonum())) {
			// UToast.makeShortTxt(this, R.string.nowonum);
			// return;
			// }
			if (!ShareUtil.isFaceSo(con)) {// 没有so
				UToast.makeShortTxt(con, con.getString(R.string.tos_download_seting_face));
				return;
			}

			mIntent = new Intent(con, FaceAty.class);
			mIntent.putExtra(CoustomKey.WONUM, SpUtiles.NearWorkListInfo.getWonum());
			mIntent.putExtra("project", SpUtiles.NearWorkListInfo.getStringForJson("project_name"));
			mIntent.putExtra("date", SpUtiles.NearWorkListInfo.getStringForJson("date") + " "
					+ SpUtiles.NearWorkListInfo.getStringForJson("time"));
			break;
		case ID_TOOL_QUERY:
			mIntent = new Intent(con, CacheAty.class);
			mIntent.putExtra("key", DiskCacheUtil.TOOL_KEY);
			break;
		case ID_TOOL:
			mWonum = SpUtiles.NearWorkListInfo.getWonum();
			mWonum = "wo200";
			if (TextUtils.isEmpty(mWonum)) {
				UToast.makeShortTxt(con, R.string.nowonum);
				return;
			}
			mIntent = new Intent(con, QueryToolAty.class);
			mIntent.putExtra(CoustomKey.WONUM, mWonum);
			break;
		case ID_WONUM_TAKE_PHOTO:
			doWonumTakePhoto();
			break;
		case ID_WONUM_TAKE_VIDEO:
			doWonumTakeVideo();
			break;
		case ID_PHOTO_DCIM:
			mIntent = new Intent(con, PreviewDCIMAty.class);
			mIntent.putExtra("edit", "edit");
			break;
		case ID_LINE_QUESTION:
			mIntent = new Intent(con, CheckAty.class);
//			mIntent=new Intent(con, StationWonumAty2.class);
//			mIntent=new Intent(con, GdxqActivity2.class);
			break;
		case ID_LINE_QUESTION_LOOK:
			mIntent = new Intent(con, CheckCacheAty.class);
			break;
		}
		if (mIntent != null) {
			startActivity(mIntent);
		}

		ModulItemShow.closedPopIng();
		// if (mPopForIosStyle != null) {
		// mPopForIosStyle.closePopUp();
		// }
	}

	// popupforios消失的事件
	@Override
	public void onDismiss() {

	}

	// 异步加载首页
	class AsynBuildHome extends Thread {

		@Override
		public synchronized void run() {
			super.run();
			LimitOfMenu mBuildHome = new LimitOfMenu(FinderFragment.this, con, mGridView, FinderFragment.this);

			boolean isInitLimit = mBuildHome.isUnInitLimit();

			if (!isInitLimit) {// 没有初始化权限
				String siteid = SpUtiles.SettingINF.getString(CoustomKey.SITEID);

				if (TextUtils.isEmpty(siteid)) {
					siteid = "S01505";
				}
				// 发布
				// siteid = "S05118";

				String initlimit = mBuildHome.getInitlimit(siteid);
				mBuildHome.modifyLimit(initlimit);

			}

			ArrayList<ModulItem> mData = mBuildHome.getAvailable();
			adds(mData);

			mHandler.sendEmptyMessage(REFERSH_HOME);
		}

	}

	@Override
	public void back(String data, int method, Object obj) {

		switch (method) {
		case MethodApi.HTTP_GET_LIMIT_MENU:// 修改权限

			LimitOfMenu limitOfMenu = new LimitOfMenu(null, null, null, null);
			limitOfMenu.modifyLimit(data);
			new AsynBuildHome().start();

			break;
		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {

	}

	@Override
	public void data(Bundle savedInstanceState) {
		super.data(savedInstanceState);
		onCreatInit(savedInstanceState);
		initConfig();

	}

	private void initConfig() {
		TrackAPI.requestLimitMenu(con, this, GlobalApplication.siteid);// 获取功能权限
	}

}
