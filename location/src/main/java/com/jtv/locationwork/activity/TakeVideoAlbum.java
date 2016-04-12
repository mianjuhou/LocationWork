package com.jtv.locationwork.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.dao.FileStateDao;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.entity.FileState;
import com.jtv.locationwork.httputil.AnsynHttpRequest;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.ParseJson;
import com.jtv.locationwork.httputil.RequestParmter;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.TextUtil;
import com.jtv.video.recorder.activity.RecordVideoAty;
import com.plutus.libraryui.dialog.LoadDataDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

public class TakeVideoAlbum extends BaseVideoAlbum implements ObserverCallBack, OnItemLongClickListener {

	private String mroot_image; // 图片的路径

	private String duan = "";// 段号id

	private String persionid = "";// persionid 64 位编码

	private String attid = "";// 手持机唯一码

	private String mWonum = "";// 工单号

	private String deptid = "";// 部门编号

	private LoadDataDialog mWaitDialog;

	public View addBottom() {
		return View.inflate(this, R.layout.location_jtv_addbottom_delete, null);
	}

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		super.onCreatInit(savedInstanceState);
		mroot_image = CreatFileUtil.getFilePath(CreatFileUtil.getVideo(this));
		initParmter();

		if (mData != null) {
			mData.clear();
			DBFileState empty = new DBFileState();
			empty.setObj(false);
			mData.add(empty);
		}

		if (mAdapter == null) {
			mAdapter = new PhotoAlbumState(this, mData);
		}

		setHeaderTitleText(getString(R.string.dis_tv_videotitle));
		mGrid.setAdapter(mAdapter);
		mGrid.setOnItemClickListener(this);
		mGrid.setOnItemLongClickListener(this);

		getHeaderOkBtn().setVisibility(View.VISIBLE);
		getHeaderOkBtn().setText(getString(R.string.upload_photo));
		setBackOnClickFinish();
		getHeaderOkBtn().setOnClickListener(this);

		mRellayBottom.addView(addBottom());
		findViewById(R.id.btn_delete).setOnClickListener(this);

		mWaitDialog = new LoadDataDialog(this);
		mWaitDialog.open();
		new AnsyQuery().execute();
	}

	private void initParmter() {
		mWonum = getIntent().getStringExtra(CoustomKey.WONUM);

		// mWonum = "wotest";
		duan = GlobalApplication.siteid; // 获取到段别
		attid = GlobalApplication.attid; // 获取到手持机id

		deptid = GlobalApplication.getAreaIdOrShopId();

		persionid = GlobalApplication.mBase64Lead;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_ok:
			getSelectFileUpload();

			break;
		case R.id.btn_delete:
			deleteFile();
			break;
		}
	}

	private int deleteFile() {
		boolean change = false;
		int state = -1;

		ArrayList<Integer> deleteIndex = new ArrayList<Integer>();

		for (int i = 0; i < mData.size(); i++) {
			FileState fileState = mData.get(i);
			Boolean isChoose = (Boolean) fileState.getObj();
			int state2 = fileState.getState();

			if (isChoose == null) {
				fileState.setObj(false);
				isChoose = false;
			}
			String path = fileState.getPath();

			if (isChoose && state2 == Constants.READY_UPLOAD) {
				File file = new File(path);
				if (file.exists()) {
					boolean delete = file.delete();

					if (delete) {
						deleteIndex.add(i);
					}

					change = true;
					state = 2;
				}

			}
		}

		for (int i = deleteIndex.size() - 1; i >= 0; i--) {
			int index = deleteIndex.get(i);
			mData.remove(index);
		}

		if (change)
			refershDate();

		return state;
	}

	// 获取选中的文件
	private void getSelectFileUpload() {
		boolean change = false;

		for (int i = 0; i < mData.size(); i++) {

			DBFileState mFileDB = mData.get(i);
			boolean isChoose = (Boolean) mFileDB.getObj();
			String path = mFileDB.getPath();

			if (TextUtil.isEmpty(path)) {
				continue;
			}
			File mFile = new File(path);

			if (isChoose) {
				mFileDB.setState(Constants.UPLOADING);
				upload(mFile, mFileDB);
				change = true;
			}

		}

		if (change)
			refershDate();
	}

	// 上传选中的视频
	private void upload(File file, DBFileState state) {
		RequestParmter mParmters = new RequestParmter();
		if (TextUtil.isEmpty(mWonum)) {
			mWonum = "";
		}
		if (TextUtil.isEmpty(deptid)) {
			deptid = "";
		}
		if (TextUtil.isEmpty(attid)) {
			attid = "";
		}
		mParmters.addBodyParmter("wonum", mWonum);
		mParmters.addBodyParmter("deptid", deptid);
		mParmters.addBodyParmter("type", "3");
		mParmters.addBodyParmter("attid", attid);
		mParmters.addBodyParmter("siteid", duan);
		mParmters.addBodyParmter("personid", persionid);
		mParmters.addBodyParmter("upfile", file);
		TrackAPI.uploadvideo(this, this, mParmters, state);
	}

	@Override
	public void onItemClick(Boolean oneClick, AdapterView<?> parent, View view, int position, int faceposition,
			ViewGroup child) {

		if (oneClick) {// 第一个条目
			openRecordVideo();
			return;
		}

		Object itemAtPosition = parent.getItemAtPosition(position);

		if (!(itemAtPosition instanceof FileState)) {
			return;
		}

		FileState mFs = (FileState) itemAtPosition;
		int childCount = child.getChildCount();
		int state = mFs.getState();
		boolean mchoose = (Boolean) mFs.getObj();
		View mChoose = null;
		for (int i = 0; i < childCount; i++) {
			View childAt = child.getChildAt(i);
			int id = childAt.getId();
			if (childAt instanceof TextView && id == R.id.tv_xuanzhong) {
				mChoose = childAt;
				break;
			}
		}
		if (mChoose == null)
			return;

		switch (state) {
		case Constants.READY_UPLOAD:
			if (mchoose) {// 选中
				mChoose.setVisibility(View.GONE);
				mFs.setObj(false);
			} else {
				mChoose.setVisibility(View.VISIBLE);
				mFs.setObj(true);
			}
			break;

		case Constants.UPLOADING:

			break;
		case Constants.UPLOAD_FINISH:

			break;
		}

	}

	private void openRecordVideo() {
		Intent mIntent = new Intent(this, RecordVideoAty.class);
		String name = new Date().getTime() + ".mp4";
		name = CreatFileUtil.getFilePath(CreatFileUtil.getVideo(this)) + File.separator + name;

		mIntent.putExtra(RecordVideoAty.ADDRESS_PATH, name);
		mIntent.putExtra(RecordVideoAty.MAX_TIME_RECORD, GlobalApplication.max_video_time);
		startActivityForResult(mIntent, Intent.EXTRA_DOCK_STATE_CAR);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {

		case com.jtv.video.recorder.activity.BaseRecordVideoAty.RESULT_CODE:

			boolean intExtra = data.getBooleanExtra(RecordVideoAty.RECORD_STATE, false);
			String mPath = data.getStringExtra(RecordVideoAty.RESULT_PATH);

			if (intExtra) {
				if (!TextUtil.isEmpty(mPath)) {

					// 图片拍摄失败
					Bitmap mBitmap = ThumbnailUtils.createVideoThumbnail(mPath, MediaStore.Video.Thumbnails.MINI_KIND);

					if (mBitmap != null) {

						FileStateDao mFileDao = (FileStateDao) DBFactory.getDBFileState(this);

						DBFileState dbFileState = new DBFileState();
						dbFileState.setObj(false);
						dbFileState.setObj2(mBitmap);
						dbFileState.setPath(mPath);
						dbFileState.setState(Constants.READY_UPLOAD);
						long insert = mFileDao.insert(dbFileState);
						dbFileState.setId((int) insert);

						mData.add(dbFileState);
						refershDate();
					}

				}
			}

			break;

		default:
			break;
		}
	}

	class AnsyQuery extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			getVideoSource();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			mWaitDialog.close();
			refershDate();
		}

	}

	private void getVideoSource() {
		FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
		if (TextUtils.isEmpty(mroot_image)) {
			return;
		}
		File file3 = new File(mroot_image);
		if (file3.exists()) {
			File[] listFiles2 = file3.listFiles();
			for (int j = 0; j < listFiles2.length; j++) {
				String path = listFiles2[j].getAbsolutePath();
				List<DBFileState> find = fileStateDao.find(null, "path =? ", new String[] { path }, null, null, null,
						null);
				if (find.size() < 1) {
					DBFileState mFileState = new DBFileState();
					mFileState.setObj(false);
					Bitmap mBitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
					mFileState.setObj2(mBitmap);
					mFileState.setPath(path);
					mFileState.setState(Constants.READY_UPLOAD);
					int insert = (int) fileStateDao.insert(mFileState);
					mFileState.setId(insert);
					find.add(mFileState);
				}
				if (find != null) {

					for (int i = 0; i < find.size() - 1; i++) {
						DBFileState dbFileState = find.get(i);
						long id = dbFileState.getId();
						fileStateDao.delete(id + "");

					}
					try {
						DBFileState mFile = find.get(find.size() - 1);
						Bitmap mBitmap = ThumbnailUtils.createVideoThumbnail(path,
								MediaStore.Video.Thumbnails.MINI_KIND);
						mFile.setObj(false);
						mFile.setObj2(mBitmap);
						mData.add(mFile);
					} catch (Exception e) {
					}

					find.clear();
					find = null;
				}
			}
		}
	}

	public void modifyStateDao(int state, DBFileState mState) {
		mState.setState(state);
		FileStateDao mDao = (FileStateDao) DBFactory.getDBFileState(this);
		mDao.update(mState);
	}

	@Override
	public void back(String data, int method, Object obj) {
		switch (method) {
		case MethodApi.HTTP_UPLOAD_VIDEO_WONUM:
			JSONObject mJson;
			try {
				mJson = new JSONObject(data);
				boolean parseStatusSuccessful = ParseJson.parseStatusSuccessful(mJson);
				if (parseStatusSuccessful) {
					DBFileState mFile = (DBFileState) obj;
					modifyStateDao(Constants.UPLOAD_FINISH, mFile);
					if (!isFinishing()) {
						refershDate();
					}

				} else {
					DBFileState mFile = (DBFileState) obj;
					modifyStateDao(Constants.READY_UPLOAD, mFile);
					if (!isFinishing()) {
						refershDate();
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		}
	}

	@Override
	public void badBack(String error, int method, Object obj) {
		switch (method) {
		case MethodApi.HTTP_UPLOAD_VIDEO_WONUM:
			DBFileState mFile = (DBFileState) obj;
			modifyStateDao(Constants.READY_UPLOAD, mFile);
			if (!isFinishing()) {
				refershDate();
			}
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		Object itemAtPosition = parent.getItemAtPosition(position);
		// int arg4 = position - parent.getFirstVisiblePosition();
		if (position == 0 || itemAtPosition == null) {// 代表的是拍照
			return false;
		} else {
			DBFileState mFs = (DBFileState) itemAtPosition;
			String path_absolute = mFs.getPath();
			Intent intent = new Intent(this, VideoPlayerAty.class);
			intent.putExtra(VideoPlayerAty.VIDEO_PATH, path_absolute);
			startActivity(intent);
			return true;
		}
	}

}
