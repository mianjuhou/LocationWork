package com.jtv.locationwork.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gitonway.niftydialogeffects.widget.niftydialogeffects.Effectstype;
import com.gitonway.niftydialogeffects.widget.niftydialogeffects.NiftyDialogBuilder;
import com.jtv.base.activity.BaseFragmentActivity;
import com.jtv.base.ui.LoadingView.OnRefreshListener;
import com.jtv.base.util.CollectionActivity;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.StraightLineBean;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.entity.CacheCommit;
import com.jtv.locationwork.entity.CacheUtil;
import com.jtv.locationwork.entity.LimitOfMenu;
import com.jtv.locationwork.fragment.SingleHomeFragment;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.services.DownLoadHttpServices;
import com.jtv.locationwork.socket.SockThreadImp;
import com.jtv.locationwork.util.Arrays;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.IntentUtils;
import com.jtv.locationwork.util.NetUtil;
import com.jtv.locationwork.util.ShareUtil;
import com.jtv.locationwork.util.TextUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeFragment extends BaseFragmentActivity implements ObserverCallBack {

	private Fragment[] fragments;// 对应的fragment

	private ImageView[] imagebuttons;// 图片的按钮

	private TextView[] textviews;// 底部的文本

	private String[] title = new String[3];// 首页的标题

	private int index;// 当前的角标

	private TextView tv_messageRight = null;// 没读消息

	private TextView tv_messageLeft = null;// 没读消息

	private int currentTabIndex;// 当前fragment的index

	private String currFragmentTag = "";// 当前正在执行的fragment

	private static NetWorkBroadCastReceiver netWorkBroadCastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_jtv_main_fragment);

		GlobalApplication.home = this;
		GlobalApplication.mContext = this;
		CollectionActivity.putTopActivity(this);

		initFragment();

		getHeaderBackBtn().setVisibility(View.GONE);

		setHeaderTitleText(title[0]);

		initconfig(savedInstanceState);
		// LocationApp.getLocationApp().updateBumenDate();

	}

	// 防止重复的fragment
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}

	private void initFragment() {

		title[0] = getString(R.string.dis_kakong);
		title[1] = getString(R.string.dis_finder);
		title[2] = getString(R.string.home_set_title);

		// title = { getString(R.string.dis_kakong),
		// getString(R.string.dis_finder),
		// getString(R.string.home_set_title) };// 首页的标题

		tv_messageRight = (TextView) findViewById(R.id.tv_unread_messageright);
		tv_messageLeft = (TextView) findViewById(R.id.tv_unread_message);

		fragments = new Fragment[3];
		imagebuttons = new ImageView[3];
		textviews = new TextView[3];

		fragments[0] = SingleHomeFragment.creatWorkNumFragment();
		fragments[1] = SingleHomeFragment.creatFinderFragment();
		fragments[2] = SingleHomeFragment.creatSettingFramgment();

		imagebuttons[0] = (ImageView) findViewById(R.id.iv_wrok_number);
		imagebuttons[1] = (ImageView) findViewById(R.id.iv_finder);
		imagebuttons[2] = (ImageView) findViewById(R.id.iv_setting);
		imagebuttons[1].setSelected(true);

		textviews[0] = (TextView) findViewById(R.id.tv_wrok_number);
		textviews[1] = (TextView) findViewById(R.id.tv_finder);
		textviews[2] = (TextView) findViewById(R.id.tv_setting);
		textviews[1].setTextColor(0xFF45C01A);

		// 显示fragmet
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0], title[0])
				.add(R.id.fragment_container, fragments[1], title[1])
				.add(R.id.fragment_container, fragments[2], title[2])
				.hide(fragments[0]).hide(fragments[2])
				.show(fragments[1]).commit();
		currentTabIndex=1;
	}

	public void onTabClicked(View view) {

		switch (view.getId()) {
		case R.id.rl_wrok_number:
			index = 0;
			break;
		case R.id.rl_finder:
			index = 1;
			break;
		case R.id.rl_setting:
			index = 2;
			break;
		}

		currFragmentTag = title[index];

		if (currentTabIndex != index) {

			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();

			trx.hide(fragments[currentTabIndex]);

			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index], currFragmentTag);
			}

			trx.show(fragments[index]).commit();

			if (fragments[index] instanceof OnRefreshListener) {
				((OnRefreshListener) fragments[index]).onRefresh();
			}
		}
		imagebuttons[currentTabIndex].setSelected(false);

		// 把当前tab设为选中状态
		imagebuttons[index].setSelected(true);

		textviews[currentTabIndex].setTextColor(0xFF999999);

		textviews[index].setTextColor(0xFF45C01A);

		setHeaderTitleText(title[index]);

		if (GlobalApplication.isFindUpdateVersion) {

			tv_messageLeft.setVisibility(View.VISIBLE);

		} else {

			tv_messageLeft.setVisibility(View.GONE);

		}

		currentTabIndex = index;

	}

	public TextView getMessageRight() {
		return tv_messageRight;
	}

	// 访问网络
	private void initconfig(Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			return;
		}

		TrackAPI.getWolistField(this, GlobalApplication.getLocationApp(), GlobalApplication.siteid);// 获取字段

		TrackAPI.getPadConfig(this, GlobalApplication.getLocationApp(), GlobalApplication.siteid);// 获取pad常用的配置

		// 注册网络监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		netWorkBroadCastReceiver = new NetWorkBroadCastReceiver();
		try {
			registerReceiver(netWorkBroadCastReceiver, filter);
		} catch (Exception e) {
			unregisterReceiver(netWorkBroadCastReceiver);
		}

		// 获取上传文件树
		TrackAPI.getTree(this, this, GlobalApplication.siteid, GlobalApplication.siteid);

		GlobalApplication.getLocationApp().setDepartId(null);

		int state = LimitOfMenu.getLimitState(7);// 获取当前的考勤是否有权限

		if (state == LimitOfMenu.STATE_CAN_USE) {// 代表有权限
			// 下载人脸

		}

		GlobalApplication.getLocationApp().connectSocket();

		// if (false) {
		// LocationApp.getLocationApp().updateBumenDate();
		// }

		GlobalApplication.getLocationApp().requestVersionUpdate();

		Intent intent2 = new Intent(this, SplashAty.class);

		GlobalApplication.getLocationApp().disPlayNotification(intent2);

		if (GlobalApplication.isFirstUseVersion(this)) {
			interdouce();
		}
	
		TrackAPI.queryPersonCheckWoinfoList(this, this, GlobalApplication.siteid, GlobalApplication.mBase64Lead,
				DateUtil.getCurrDateFormat(DateUtil.style_nyr).toString());

	}

	public void showKillDialog(final Context con) {

		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setMessage(con.getString(R.string.dis_background_interdouce))
				.setPositiveButton(con.getString(R.string.dis_tv_exit), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						exit(con);
					}
				})
				.setNegativeButton(con.getString(R.string.dis_tv_backgroundrun), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							dialog.dismiss();
							Intent startHome = IntentUtils.startHome(con);
							startActivity(startHome);
						} catch (Exception e) {
						}

					}
				});
		final AlertDialog create = builder.create();
		create.setCanceledOnTouchOutside(true);
		// AnimationDialogUtil.startAnimation(Effectstype.Fliph, create);
		create.show();
	}

	public static void exit(Context con) {
		ArrayList<Activity> allArrayList = CollectionActivity.getAllArrayList();
		if (allArrayList != null) {
			for (int i = 0; i < allArrayList.size(); i++) {
				Activity mAty = allArrayList.get(i);
				if (mAty != null) {
					mAty.finish();
				}
			}
		}
		GlobalApplication.getLocationApp().stopServicesGPS();

		if (netWorkBroadCastReceiver != null)
			con.unregisterReceiver(netWorkBroadCastReceiver);

		con.stopService(new Intent(con, DownLoadHttpServices.class));

		NotificationManager mfm = (NotificationManager) con.getSystemService(Activity.NOTIFICATION_SERVICE);
		mfm.cancelAll();
		DBFactory.getConnection().close();

		try {
			GlobalApplication.getLocationApp().closeSock();
		} catch (Exception e) {
			e.printStackTrace();
		}

		android.os.Process.killProcess(android.os.Process.myPid());
		java.lang.System.exit(0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (TextUtils.isEmpty(GlobalApplication.lead) || TextUtils.isEmpty(GlobalApplication.getAreaIdOrShopId())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.not_foun_people))
					.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							exit(HomeFragment.this);
						}
					}).show();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 提交缓存数据到服务器只执行一次
		CacheUtil instance = CacheUtil.getInstance(this);
		if (instance != null) {
			instance.get();
			instance.start();
		}
//		CacheCommit.getInstance().start(this);
	}

	@Override
	public void onBackPressed() {
		showKillDialog(this);
	}

	private void interdouce() {
		final NiftyDialogBuilder oneDialog = NiftyDialogBuilder.getInstance(this);
		oneDialog.withTitle("版本介绍");
		oneDialog.withTitleColor("#666666");
		oneDialog.withMessage(R.string.dis_interdouce_version);
		oneDialog.withMessageColor("#666666");
		oneDialog.withButton1Text(getString(R.string.ok)).withEffect(Effectstype.Slit);
		oneDialog.withDialogBackGroundColor("#ffffff");
		oneDialog.withButtonDrawable(R.drawable.selector_btn_pressblue_upgray);
		oneDialog.setButton1Click(new OnClickListener() {

			@Override
			public void onClick(View v) {
				oneDialog.dismiss();
			}
		});
		oneDialog.show();
	}

	@Override
	public void back(String data, int method, Object obj) {
		switch (method) {
		case MethodApi.HTTP_TREE:
			String siteid = "";
			if (obj instanceof String) {
				siteid = (String) obj;
			}
			if (!TextUtil.isEmpty(siteid)) {
				new UpdateTree(data, siteid).start();
			}

			break;

		}
	}

	@Override
	public void badBack(String error, int method, Object obj) {

	}

	class UpdateTree extends Thread {

		private String data;
		private String siteid;

		public UpdateTree(String data, String siteid) {
			this.data = data;
			this.siteid = siteid;
		}

		@Override
		public void run() {
			super.run();
			ShareUtil.syncroizedTree(data, siteid);
		}
	}

	class NetWorkBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (NetUtil.hasConnectedNetwork(HomeFragment.this.getApplicationContext())) {
				boolean closed = true;
				SockThreadImp sock = GlobalApplication.getLocationApp().getSock();
				if (sock != null) {
					closed = sock.isClosed();
				}

				if (closed) {// 重连socket
					GlobalApplication.getLocationApp().connectSocket();
				}

			}
		}
	}

}
