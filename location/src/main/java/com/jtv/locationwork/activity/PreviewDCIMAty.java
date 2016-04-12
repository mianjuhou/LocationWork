package com.jtv.locationwork.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jtv.base.adapter.LBaseAdapter;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.util.CheckImageLoaderConfiguration;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.RotateImageViewAware;
import com.jtv.locationwork.util.ScreenUtil;
import com.jtv.locationwork.util.ThumbnailsUtil;
import com.jtv.locationwork.util.UniversalImageLoadTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class PreviewDCIMAty extends BaseGridPhotoAty implements OnItemClickListener,OnItemLongClickListener {

	public static final int RESULT_CODE = 8087;
	public static final String RESULT_ADDRESS = "DCIM_PATH";

	public ArrayList<String> mDCIM = new ArrayList<String>();

	private BaseAdapter mAdapter = null;

	private int width = 0;
	private int height = 0;

	private boolean isEditor =false;

	@Override
	public void onClick(View v) {

	}

	@Override
	protected void onStart() {
		super.onStart();
		CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
	}

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		super.onCreatInit(savedInstanceState);
		mGrid.setAdapter(mAdapter);
		int screenWidth = ScreenUtil.getScreenWidth(this);
		width = screenWidth / 2;
		height = width + 20;

		// mGrid.setColumnWidth(width);
		mGrid.setNumColumns(2);
		mGrid.setOnItemClickListener(this);
		mGrid.setOnItemLongClickListener(this);

		new FindPhotoPath().execute();
		setBackOnClickFinish();
		isEditor = getIntent().hasExtra("edit");

		setHeaderTitleText("相册");
	}

	private class PrveicDCIMAdapter extends LBaseAdapter<String> {

		public PrveicDCIMAdapter(Context con, List<? extends String> list) {
			super(con, list);
		}

		@SuppressLint("InflateParams")
		@Override
		public View getLView(int position, View arg1, ViewGroup arg2) {

			String path = list.get(position);

			if (arg1 == null) {
				arg1 = inflate.inflate(R.layout.location_jtv_dcim_image_item, null);
			}
			ImageView iv = get(arg1, R.id.iv_image);

			if (iv != null) {
				LayoutParams layoutParams = (LayoutParams) iv.getLayoutParams();
				layoutParams.width = width;
				layoutParams.height = width;
				iv.setLayoutParams(layoutParams);
			}

			if (path != null) {// 显示
				ThumbnailsUtil.MapgetHashValue(path.hashCode(), "file://" + path);
				UniversalImageLoadTool.disPlay("file://" + path, new RotateImageViewAware(iv, path),
						R.drawable.icon_jtv_eror);
				// 当路径不存在的时候会报can not open 路径
			}

			return arg1;
		}
	}

	public void onRefersh() {

		if (mAdapter == null) {
			mAdapter = new PrveicDCIMAdapter(this, mDCIM);
			mGrid.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

	}

	class FindPhotoPath extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			File dcim = CreatFileUtil.getDCIM(PreviewDCIMAty.this);
			File dcim2 = CreatFileUtil.getImage(PreviewDCIMAty.this);
			File dcim3 = CreatFileUtil.getMorningImage(PreviewDCIMAty.this);
			File dcim4 = CreatFileUtil.getNightImage(PreviewDCIMAty.this);
			ArrayList<File> arrayList = new ArrayList<File>();
			com.jtv.locationwork.util.Arrays.arr2List((dcim == null) ? null : dcim.listFiles(), arrayList);
			com.jtv.locationwork.util.Arrays.arr2List((dcim2 == null) ? null : dcim2.listFiles(), arrayList);
			com.jtv.locationwork.util.Arrays.arr2List((dcim3 == null) ? null : dcim3.listFiles(), arrayList);
			com.jtv.locationwork.util.Arrays.arr2List((dcim4 == null) ? null : dcim4.listFiles(), arrayList);

			for (int i = 0; i < arrayList.size(); i++) {
				File file = arrayList.get(i);
				if (file.isFile()) {
					mDCIM.add(file.getAbsolutePath());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			onRefersh();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if (isEditor) {
			Intent intent = new Intent(this, PainterAty.class);
			String path = (String) arg0.getItemAtPosition(arg2);
			intent.putExtra(PhotoAty.PHOTO_PATH, path);
			startActivity(intent);
			return;
		}

		if (arg0 != null) {
			String path = (String) arg0.getItemAtPosition(arg2);
			setResult(path);
			finish();
		}
	}

	protected void setResult(String path) {
		Intent intent = new Intent();
		intent.putExtra(RESULT_ADDRESS, path);
		setResult(RESULT_CODE, intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, PainterAty.class);
		String path = (String) arg0.getItemAtPosition(arg2);
		intent.putExtra(PhotoAty.PHOTO_PATH, path);
		startActivity(intent);
		return false;
	}

}
