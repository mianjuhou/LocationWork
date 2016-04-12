package com.jtv.locationwork.activity;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.base.activity.BaseAty;
import com.jtv.base.adapter.LBaseAdapter;
import com.jtv.base.ui.CustomListView;
import com.jtv.base.ui.CustomListView.OnRefreshListener;
import com.jtv.base.util.FileUtil;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.ParseJson;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.IntentUtils;
import com.jtv.locationwork.util.TextUtil;
import com.plutus.libraryui.dialog.LoadDataDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class FileManagerAty extends BaseAty implements OnClickListener, OnItemDeleteClick, OnRefreshListener,
		DialogInterface.OnClickListener, ObserverCallBack {
	private CustomListView lv_down;
	private ArrayList<File> arrayList;
	private DownLoadedAdapter downLoadedAdapter;
	private String path;
	private LoadDataDialog loadDataDialog;
	private String pathfile;
	private int onclickcount = 0;
	private long firstTime;

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		setContentView(R.layout.location_lay_display_filelist);
		loadDataDialog = new LoadDataDialog(this);
		path = CreatFileUtil.getFilePath(CreatFileUtil.getDownLoad(this));

		if (TextUtil.isEmpty(path)) {
			UToast.makeShortTxt(this, getString(R.string.not_tf));
			finish();
			return;
		}

		getHeaderTitleTv().setText(getString(R.string.title_download));
		getHeaderTitleTv().setOnClickListener(this);
		setBackOnClickFinish();
		lv_down = (CustomListView) findViewById(R.id.lv_down);
		arrayList = new ArrayList<File>();
		downLoadedAdapter = new DownLoadedAdapter(this, arrayList);
		lv_down.setAdapter(downLoadedAdapter);
		lv_down.setCanLoadMore(false);
		lv_down.setOnRefreshListener(this);
		lv_down.setDivider(null);
		lv_down.setDividerHeight(5);
		lv_down.setCanRefresh(true);
		loadDataDialog.open();
		getHeaderOkBtn().setText("上传");
		getHeaderOkBtn().setVisibility(View.VISIBLE);
		getHeaderOkBtn().setOnClickListener(this);
		ListData();
	}

	private void ListData() {
		lv_down.setCanRefresh(false);
		new AsyncTask<Void, Void, Void>() {

			private File file;

			@Override
			protected Void doInBackground(Void... params) {
				file = new File(path);
				File[] listFiles = file.listFiles();
				if (listFiles == null) {
					return null;
				}
				if (listFiles.length > 70) {// 存入文件过多给他删除了
					FileUtil.delete(file);
					listFiles = file.listFiles();
				}
				arrayList.clear();
				for (int i = 0; i < listFiles.length; i++) {
					arrayList.add(listFiles[i]);
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				loadDataDialog.close();
				lv_down.onRefreshComplete();
				downLoadedAdapter.refershData(arrayList);
				lv_down.setCanRefresh(true);
			};
		}.execute();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_ok) {// 提交文件到服务器
			Intent intent = new Intent(this, FileChoose.class);
			startActivityForResult(intent, 300);
		} else if (R.id.tv_title == id) {

			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000)
				onclickcount = 0;
			firstTime = secondTime;
			if (onclickcount++ == 5) { // 如果两次按回退键小于两秒
				firstTime = secondTime;

				String jtvRoot = CreatFileUtil.getFilePath(CreatFileUtil.getRootFile(this));
				;
				if (TextUtil.isEmpty(jtvRoot)) {
					UToast.makeShortTxt(this, R.string.not_tf);
				}
				jtvRoot = jtvRoot + "log.txt";
				File file = new File(jtvRoot);
				if (file.exists()) {
					CreatFileUtil.getRootFile(this);
					Intent textFileIntent = IntentUtils.getTextFileIntent(file);
					startActivity(textFileIntent);
				}
				onclickcount = 0;
			}

		}
	}

	public void uploadFile(File file) {
		if (file == null) {
			return;
		}
		if (!file.exists()) {
			return;
		}

		TrackAPI.uploadFile(this, MethodApi.HTTP_CONSTANT, file);

	}

	@Override
	public void deleteClick(View v, File file, int position) {
		FileUtil.delete(file);
		arrayList.remove(position);
		downLoadedAdapter.refershData(arrayList);
	}

	@Override
	public void onRefresh() {
		ListData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		pathfile = "";
		if (resultCode == 200) {
			if (data == null)
				return;
			boolean hasExtra = data.hasExtra("path");
			if (!hasExtra)
				return;
			pathfile = data.getStringExtra("path");
			if (TextUtil.isEmpty(pathfile)) {
				return;
			}
			if (!new File(pathfile).exists()) {
				UToast.makeShortTxt(this, getString(R.string.file_not_exit));
				return;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.ok_uploadfile)).setPositiveButton(getString(R.string.ok), this)
					.setNegativeButton(getString(R.string.cancel), this).show();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == Dialog.BUTTON_POSITIVE) {// 确认
			loadDataDialog.open();
			uploadFile(new File(pathfile));
		} else if (which == Dialog.BUTTON_NEGATIVE) {// 取消

		}
		dialog.dismiss();
	}

	@Override
	public void lookClick(View v, File file, int position) {
		String name = file.getName();
		if (TextUtil.isEmpty(name)) {
			return;
		}
		int lastIndexOf = name.lastIndexOf(".");
		String value = name.substring(lastIndexOf + 1);
		if (TextUtil.isEmpty(value)) {
			return;
		}
		Intent intent = null;
		if (value.contains("txt")) {
			intent = IntentUtils.getTextFileIntent(file);
		} else if (value.contains("pdf")) {
			intent = IntentUtils.getPdfFileIntent(file);
		} else if (value.contains("doc")) {
			intent = IntentUtils.getWordFileIntent(file);
		} else if (value.contains("ppt")) {
			intent = IntentUtils.getPptFileIntent(file);
		} else if (value.contains("xlsx")) {
			intent = IntentUtils.getExcelFileIntent(file);
		} else if (value.contains("chm")) {
			intent = IntentUtils.getChmFileIntent(file);
		} else if (value.contains("mp4") || value.contains("avi")) {
			intent = IntentUtils.getVideoFileIntent(file);
		} else if (value.contains("mp3")) {
			intent = IntentUtils.getAudioFileIntent(file);
		} else if (value.contains("jpg") || value.contains("png") || value.contains("jpeg")) {
			intent = IntentUtils.getImageFileIntent(file);
		} else if (value.contains("htm") || value.contains("html")) {
			intent = IntentUtils.getHtmlFileIntent(file.getAbsolutePath());
		}
		name = null;
		value = null;
		try {
			startActivity(intent);
		} catch (Exception e) {
			intent = IntentUtils.openFile(file);
			try {
				startActivity(intent);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Override
	public void back(String data, int method, Object obj) {
		loadDataDialog.close();
		boolean parseStatusSuccessful = false;
		try {
			parseStatusSuccessful = ParseJson.parseStatusSuccessful(new JSONObject(data));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (parseStatusSuccessful) {
			UToast.makeShortTxt(this, getString(R.string.upload_successful));
		} else {
			UToast.makeShortTxt(this, getString(R.string.upload_failed));
		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {
		loadDataDialog.close();
		UToast.makeShortTxt(this, getString(R.string.upload_failed));
	}

}

class DownLoadedAdapter extends LBaseAdapter<File> {
	public DownLoadedAdapter(Context con, ArrayList<File> list) {
		super(con, list);
	}

	@Override
	public View getLView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflate.inflate(R.layout.location_item_displayfile, null);
		}
		TextView tv_path = get(convertView, R.id.tv_path);
		TextView tv_name = get(convertView, R.id.tv_name);
		TextView tv_look = get(convertView, R.id.tv_look);
		TextView tv_size = get(convertView, R.id.tv_size);
		File file = list.get(position);
		long length = file.length();

		if (length > 0) {
			length = length / 1024 / 1024;
		}

		tv_size.setText("文件大小:" + length);
		String name = file.getName();
		tv_name.setText("文件名字:" + name);
		tv_path.setText("文件路径:" + list.get(position).getAbsolutePath());
		TextView tv_delete = get(convertView, R.id.tv_delete);

		tv_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (con instanceof OnItemDeleteClick) {
					((OnItemDeleteClick) con).deleteClick(v, list.get(position), position);
				}
			}
		});
		tv_look.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (con instanceof OnItemDeleteClick) {
					((OnItemDeleteClick) con).lookClick(v, list.get(position), position);
				}
			}
		});
		return convertView;
	}
}

interface OnItemDeleteClick {
	public void deleteClick(View v, File file, int position);

	public void lookClick(View v, File file, int position);
}
