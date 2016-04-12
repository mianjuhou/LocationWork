package com.jtv.locationwork.fragment;

import java.io.File;
import java.util.Arrays;

import com.jtv.base.fragment.BaseFragmet;
import com.jtv.base.ui.LoadingView.OnRefreshListener;
import com.jtv.base.util.FileUtil;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.httputil.HttpApi;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.OkHttpUtil;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.DownLoadFaceSiteid;
import com.jtv.locationwork.util.PackageUtil;
import com.jtv.locationwork.util.ShareUtil;
import com.jtv.locationwork.util.SimpleDialog;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;
import com.plutus.libraryui.dialog.DiaLogUtil;
import com.plutus.libraryui.dialog.LoadDataDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class SetOtherFragment extends BaseFragmet
		implements OnClickListener, DialogInterface.OnClickListener, OnRefreshListener {

	private static boolean one = true;

	private String photo_Quality[] = { "低", "中", "高" };// 当前的压缩质量名字

	private int[] photo_Quality_Value = { 30, 60, 90 };// 当前的值

	private String photo_Size[] = { "480p", "720p", "1080p" };// 当前显示的大小名字

	private int[] photo_Size_Value = { 480, 720, 1080 };// 当前压缩的实际大小值,对应显示名字

	private View view;
	private TextView tv_machine_code;
	private TextView tv_about_version;
	private TextView tv_ip;
	private TextView tv_session;
	private TextView tv_video_ip;
	private View rl_db;
	private View rl_face;
	private View rl_photo_quality;
	private View rl_photo_size;
	private TextView tv_photo_quality;
	private TextView tv_photo_size;

	private int onClickId;
	private int bin_qulaity;// 默认选中的质量值

	private int bin_size;// 默认选中的尺寸值

	private Context con;

	private LoadDataDialog mWaitDialog;

	private TextView tv_duan;

	private TextView tv_workshop;

	private TextView tv_workarea;

	private TextView tv_lead;

	@Override
	public View creatView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.location_jtv_set_other, null);
		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rl_db:

			con = getActivity();

			String message = con.getString(R.string.dis_tv_isinitdb);
			String ok = con.getString(R.string.ok);
			String cancel = con.getString(R.string.cancel);

			SimpleDialog.showSimpleDialog(con, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == dialog.BUTTON1) {
						GlobalApplication.deleteDB();
						GlobalApplication.initDB();
					}
					dialog.dismiss();

				}
			}, null, message, ok, cancel);

			break;

		case R.id.rl_facedata:
			try {
				new DownLoadFaceSiteid(con, GlobalApplication.siteid);
				// new DownloadFace().downloadPerson(con, LocationApp.lead);
			} catch (Exception e) {
			}

			break;
		case R.id.rl_photo_quality:
			onClickId = 1;
			DiaLogUtil.showSelectItemDiaLog(context, "照片", photo_Quality, bin_qulaity, this);
			break;
		case R.id.rl_photo_size:
			onClickId = 2;
			DiaLogUtil.showSelectItemDiaLog(context, "大小", photo_Size, bin_size, this);
			break;
		case R.id.rl_vewsion_update:
			GlobalApplication.getLocationApp().requestVersionUpdate();
			break;
		case R.id.rl_download_face_so:// 下载人脸识别库

			boolean needDownload = ShareUtil.isFaceSo(getContext());
			con = getContext();

			if (!needDownload && one) {// 需要下载，走网络
				message = con.getString(R.string.dis_download_faceso_interdouce);
				ok = con.getString(R.string.ok);
				cancel = con.getString(R.string.cancel);

				SimpleDialog.showSimpleDialog(getContext(), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == dialog.BUTTON1) {
							mWaitDialog.open();
							one = false;
							String pathServer = HttpApi.Http_Interface_download_facsso;
							File downLoad2 = CreatFileUtil.getDownLoad(getContext());
							String save = downLoad2.getAbsoluteFile() + File.separator
									+ FileUtil.getServerFileName(pathServer);
							OkHttpUtil.download(pathServer, new File(save), new ObserverCallBack() {

								@Override
								public void back(String data, int method, Object obj) {
									mWaitDialog.close();
									one = true;
									Log.i("download", "下载成功＝＝＝＝＝＝＝＝＝＝＝＝");
									boolean needDownload = ShareUtil.isFaceSo(getContext());
									UToast.makeShortTxt(con, con.getString(R.string.download_ok));
								}

								@Override
								public void badBack(String error, int method, Object obj) {
									mWaitDialog.close();
									UToast.makeShortTxt(con, con.getString(R.string.download_failed));
									Log.i("download", "下载失败＝＝＝＝＝＝＝＝＝＝＝＝");
									one = true;
								}
							}, MethodApi.HTTP_CONSTANT, null);

						}
						dialog.dismiss();

					}
				}, null, message, ok, cancel);

			} else {
				if (one)
					UToast.makeShortTxt(getContext(), getContext().getString(R.string.dis_download_facefinish));
			}

			break;
		}
	}

	@Override
	public void data(Bundle savedInstanceState) {
		con = getContext();
		mWaitDialog = new LoadDataDialog((Activity) con);
		findid();
		setValut();

	}

	private void findid() {
		tv_machine_code = (TextView) view.findViewById(R.id.tv_machine_code);
		tv_about_version = (TextView) view.findViewById(R.id.tv_about_version);
		tv_ip = (TextView) view.findViewById(R.id.tv_ip);
		tv_session = (TextView) view.findViewById(R.id.tv_session);
		tv_video_ip = (TextView) view.findViewById(R.id.tv_video_ip);

		rl_db = view.findViewById(R.id.rl_db);
		rl_face = view.findViewById(R.id.rl_facedata);

		rl_photo_quality = view.findViewById(R.id.rl_photo_quality);
		rl_photo_size = view.findViewById(R.id.rl_photo_size);

		tv_photo_quality = (TextView) view.findViewById(R.id.tv_photo_quality);
		tv_photo_size = (TextView) view.findViewById(R.id.tv_photo_size);

		tv_duan = (TextView) view.findViewById(R.id.tv_duan);
		tv_workshop = (TextView) view.findViewById(R.id.tv_workshop);
		tv_workarea = (TextView) view.findViewById(R.id.tv_workarea);
		tv_lead = (TextView) view.findViewById(R.id.tv_lead);

		view.findViewById(R.id.rl_download_face_so).setOnClickListener(this);
		view.findViewById(R.id.rl_vewsion_update).setOnClickListener(this);

		rl_db.setOnClickListener(this);
		rl_face.setOnClickListener(this);
		rl_photo_quality.setOnClickListener(this);
		rl_photo_size.setOnClickListener(this);

	}

	private void setValut() {
		String version = PackageUtil.getVersionName(getContext());

		String rootIP2 = HttpApi.RootIP2;

		if (rootIP2 != null && rootIP2.contains("http://")) {
			rootIP2 = rootIP2.replace("http://", "");
		}

		displayPeople();

		tv_machine_code.setText("机 器 码: " + GlobalApplication.attid);

		String html = "<html>点击检查更新  当前版本:<font color=\"#FF0000\">V" + version + "</font></html>";

		tv_about_version.setText(Html.fromHtml(html));

		// tv_about_version.setText("点击检查更新 当前版本：V" + version);
		tv_ip.setText("服 务 端: " + rootIP2);
		tv_session.setText("会话通讯: " + GlobalApplication.SOCKET_HOST_IP + ":" + GlobalApplication.SOCKET_HOST_PORT);
		tv_video_ip.setText("视频请求: " + GlobalApplication.rtsp_ip + ":" + GlobalApplication.rtsp_port);

		// 获取照片的值
		int piex = SpUtiles.BaseInfo.mbasepre.getInt(CoustomKey.IMAGE_PIXEI, 480);
		int qulaity = SpUtiles.BaseInfo.mbasepre.getInt(CoustomKey.IMAGE_QUALITY, 30);
		findIndex(piex, qulaity);

		tv_photo_quality.setText("照片质量: " + photo_Quality[bin_qulaity]);
		tv_photo_size.setText("照片压缩率: " + photo_Size[bin_size]);
	}

	// 设置当前的部门数据更新
	private void displayPeople() {

		if (tv_duan == null || tv_workshop == null) {
			return;
		}

		tv_duan.setText(GlobalApplication.duan_name + "(" + GlobalApplication.siteid + ")");
		tv_workshop.setText(GlobalApplication.work_shop_name + "(" + GlobalApplication.departid + ")");

		if (TextUtils.isEmpty(GlobalApplication.area_name)) {
			tv_workarea.setVisibility(View.GONE);
		} else {
			tv_workarea.setText(GlobalApplication.area_name + "(" + GlobalApplication.area_id + ")");
		}

		String lead = GlobalApplication.lead;

		if (!TextUtil.isEmpty(lead) && lead.length() > 1) {
			lead = lead.replaceAll("\\d", "");// 替换掉数字
		}

		tv_lead.setText(lead);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		switch (onClickId) {
		case 1:
			int quality = photo_Quality_Value[which];
			Editor edit = SpUtiles.BaseInfo.mbasepre.edit();
			edit.putInt(CoustomKey.IMAGE_QUALITY, quality);
			edit.commit();
			bin_qulaity = which;
			tv_photo_quality.setText("照片质量: " + photo_Quality[bin_qulaity]);
			break;

		case 2:
			int size = photo_Size_Value[which];
			edit = SpUtiles.BaseInfo.mbasepre.edit();
			edit.putInt(CoustomKey.IMAGE_PIXEI, size);
			edit.commit();
			bin_size = which;
			tv_photo_size.setText("照片比率: " + photo_Size[bin_size]);
			break;
		}
		dialog.dismiss();

	}

	private void findIndex(int piex, int qulaity) {
		// 获取角标
		bin_qulaity = Arrays.binarySearch(photo_Quality_Value, qulaity);
		bin_size = Arrays.binarySearch(photo_Size_Value, piex);

		// 防止角标越界
		if (bin_qulaity < 0) {
			bin_qulaity = 2;
		}
		// 防止角标越界
		if (bin_size < 0) {
			bin_size = 2;
		}
	}

	@Override
	public void onRefresh() {
		displayPeople();
	}
}
