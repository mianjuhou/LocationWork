package com.jtv.locationwork.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.jtv.base.activity.BaseTableViewPagerFragementAty;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.dao.FileStateDao;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.fragment.PhotoGridFragment;
import com.jtv.locationwork.fragment.VideoFaragment;
import com.jtv.locationwork.httputil.AnsynHttpRequest;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.ParseJson;
import com.jtv.locationwork.httputil.RequestParmter;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.Base64UtilCst;
import com.jtv.locationwork.util.CheckImageLoaderConfiguration;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.ImageUtil;
import com.jtv.locationwork.util.PDADeviceInfoService;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;
import com.jtv.locationwork.util.YaSuoUtil;
import com.jtv.video.recorder.activity.RecordVideoAty;
import com.plutus.libraryui.dialog.LoadDataDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WorkHandOverTabAty extends BaseTableViewPagerFragementAty implements OnClickListener, ObserverCallBack {

	private TextView mTextTab4;
	public static java.util.List<DBFileState> mPhoto = new ArrayList<DBFileState>();

	public static java.util.List<DBFileState> mVideo = new ArrayList<DBFileState>();

	java.util.List<Fragment> mFragment = new ArrayList<Fragment>();

	protected WorkHandOverAdapter mAdapter = null;
	private LoadDataDialog mWaiteDialog;

	private String imageRootPath = "";
	private String videoRootPath = "";
	private int type;
	private String mWonum;
	private String PHOTO_PATH;
	private double[] gps;
	private String deptid = "null";
	private String attid = "null";
	private String duan = "null";
	private String persionid = "null";

	@Override
	protected void onCreate(Bundle arg0) {
		tabCount = 4;
		super.onCreate(arg0);
		VideoFaragment mVideoFragment = new VideoFaragment();
		PhotoGridFragment mPhotoFragment = new PhotoGridFragment();
		mFragment.add(mVideoFragment);
		mFragment.add(mPhotoFragment);
		mWaiteDialog = new LoadDataDialog(this);
		mWaiteDialog.open();

		setParmter();
		setPath();
		mPhoto.clear();
		mVideo.clear();

		new AnsyQuery().execute();

		setBackOnClickFinish();

		String title = "";
		if (type == Constants.MORNING) {
			title = getString(R.string.morning_shift);
		} else {
			title = getString(R.string.night_shift);
		}

		getHeaderOkBtn().setVisibility(View.VISIBLE);
		getHeaderOkBtn().setOnClickListener(this);
		getHeaderOkBtn().setText(getString(R.string.dis_tv_up_video));

		setHeaderTitleText(title);

		FragmentOnPagerChangerListener mChangerListener = new FragmentOnPagerChangerListener(this);
		mViewPager.setOnPageChangeListener(mChangerListener);
	}

	public void setParmter() {
		type = getIntent().getIntExtra(Constants.TYPE, Constants.MORNING);
		mWonum = getIntent().getStringExtra(CoustomKey.WONUM);
		gps = SpUtiles.BaseInfo.getGps();
		persionid = SpUtiles.SettingINF.getString(CoustomKey.LEAD);
		persionid = Base64UtilCst.encodeUrl(persionid);
		duan = SpUtiles.SettingINF.getString(CoustomKey.SITEID);
		deptid = SpUtiles.SettingINF.getString(CoustomKey.DEPARTID_AREA);
		attid = new PDADeviceInfoService(this).getDeviceId();

	}

	public void setPath() {
		switch (type) {
		case Constants.MORNING:

			imageRootPath = CreatFileUtil.getMorningImage(this) + File.separator;
			videoRootPath = CreatFileUtil.getMorningVideo(this) + File.separator;

			break;
		case Constants.NINGTH:

			imageRootPath = CreatFileUtil.getNightImage(this) + File.separator;
			videoRootPath = CreatFileUtil.getNightVideo(this) + File.separator;

			break;
		}

	}

	public VideoFaragment getVideoFragment() {

		for (int i = 0; i < mFragment.size(); i++) {
			Fragment fragment = mFragment.get(i);
			if (fragment instanceof VideoFaragment) {
				return (VideoFaragment) mFragment.get(i);
			}
		}
		return null;
	}

	public PhotoGridFragment getPhotoFragment() {

		for (int i = 0; i < mFragment.size(); i++) {
			Fragment fragment = mFragment.get(i);
			if (fragment instanceof PhotoGridFragment) {
				return (PhotoGridFragment) mFragment.get(i);
			}
		}
		return null;
	}

	@Override
	protected int getContentView() {
		return R.layout.location_lay_workhandover_videophoto;
	}

	@Override
	protected void initTabTitle() {
		mTextTab1 = (TextView) findViewById(R.id.text1);
		mTextTab2 = (TextView) findViewById(R.id.text2);
		mTextTab3 = (TextView) findViewById(R.id.text3);
		mTextTab4 = (TextView) findViewById(R.id.text4);

		mTextTab1.setOnClickListener(new TabTitleOnClickListener(0));
		mTextTab2.setOnClickListener(new TabTitleOnClickListener(1));
		mTextTab3.setOnClickListener(this);
		mTextTab4.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.text3:
			openCamera();
			break;

		case R.id.text4:
			openRecordVideo();
			break;
		case R.id.btn_ok:
			upload();
			break;
		}
	}

	private void upload() {
		int currentItem = mViewPager.getCurrentItem();

		if (currentItem == 0) {// 视频
			uploadVideo();
		} else {// 拍照
			uploadPhoto();
		}
	}

	boolean isNeedUploadVideo = false;
	boolean isNeedUploadPhoto = false;

	private void uploadVideo() {
		isNeedUploadPhoto = false;
		for (int i = 0; i < mVideo.size(); i++) {
			DBFileState mFile = mVideo.get(i);

			boolean isChoose = (Boolean) mFile.getObj();
			int state = mFile.getState();
			if (isChoose && state == Constants.READY_UPLOAD) {// 如果为选中
				String mPath = mFile.getPath();
				File file = new File(mPath);
				mFile.setState(Constants.UPLOADING);
				RequestParmter mParmters = new RequestParmter();
				// mParmters.addBodyParameter("wonum", mWonum);
				mParmters.addBodyParmter("deptid", deptid);
				mParmters.addBodyParmter("type", type + "");
				mParmters.addBodyParmter("attid", attid);
				mParmters.addBodyParmter("siteid", duan);
				mParmters.addBodyParmter("personid", persionid);
				mParmters.addBodyParmter("upfile", file);

				TrackAPI.uploadvideo(this, this, mParmters, mFile);
				isNeedUploadPhoto = true;

			}

		}
		if (isNeedUploadPhoto) {
			getVideoFragment().refershAdapter(mVideo);
		} else {
			UToast.makeShortTxt(this, getString(R.string.no_select_data));
		}

	}

	private void uploadPhoto() {
		isNeedUploadPhoto = false;
		for (int i = 0; i < mPhoto.size(); i++) {
			DBFileState mFile = mPhoto.get(i);

			boolean isChoose = (Boolean) mFile.getObj();
			int state = mFile.getState();
			if (isChoose && state == Constants.READY_UPLOAD) {// 如果为选中
				String mPath = mFile.getPath();
				// File file = new File(mPath);
				mFile.setState(Constants.UPLOADING);
				RequestParmter mParmters = new RequestParmter();
				// mParmters.addBodyParameter("wonum", mWonum);
				// mParmters.addBodyParameter("deptid", deptid);
				mParmters.addBodyParmter("type", type + "");
				mParmters.addBodyParmter("crewid", deptid);
				mParmters.addBodyParmter("attid", attid);
				mParmters.addBodyParmter("siteid", duan);
				mParmters.addBodyParmter("personid", persionid);
				// mParmters.addBodyParameter("upfile", file);

				TrackAPI.uploadphotoWorkHandOver(this, this, mParmters, mFile, mPath);
				isNeedUploadPhoto = true;
			}

		}
		if (isNeedUploadPhoto) {
			getPhotoFragment().refershAdapter(mPhoto);
		} else {
			UToast.makeShortTxt(this, getString(R.string.no_select_data));
		}

	}

	private void openCamera() {
		PHOTO_PATH = imageRootPath + ImageUtil.getPhotoFilename(mWonum, new Date());

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(PHOTO_PATH)));
		startActivityForResult(intent, Constants.REQUEST_TAKE_PHOTO);
	}

	private void openRecordVideo() {
		String name = new Date().getTime() + ".mp4";
		name = videoRootPath + name;

		Intent mIntent = new Intent(this, RecordVideoAty.class);
		mIntent.putExtra(RecordVideoAty.ADDRESS_PATH, name);
		mIntent.putExtra(RecordVideoAty.MAX_TIME_RECORD, GlobalApplication.max_video_time);
		startActivityForResult(mIntent, Intent.EXTRA_DOCK_STATE_CAR);
	}

	@Override
	protected void onStart() {
		super.onStart();
		CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
	}

	@Override
	protected void onActivityResult(int requestcode, int resultCode, Intent intent) {
		super.onActivityResult(requestcode, resultCode, intent);
		if (requestcode == Constants.REQUEST_TAKE_PHOTO) {
			if (resultCode != RESULT_CANCELED) {
				if (!TextUtils.isEmpty(PHOTO_PATH)) {
					PHOTO_PATH = YaSuoUtil.ctrlCameraImage(PHOTO_PATH);
					DBFileState fileState = new DBFileState();
					fileState.setObj(false);
					fileState.setPath(PHOTO_PATH);
					fileState.setState(Constants.READY_UPLOAD);
					FileStateDao mDBFile = (FileStateDao) DBFactory.getDBFileState(this);
					int insert = (int) mDBFile.insert(fileState);
					fileState.setId(insert);
					mPhoto.add(fileState);
					getPhotoFragment().refershAdapter(mPhoto);
				}
			}
		} else if (resultCode == RecordVideoAty.RESULT_CODE) {

			boolean intExtra = intent.getBooleanExtra(RecordVideoAty.RECORD_STATE, false);
			String mPath = intent.getStringExtra(RecordVideoAty.RESULT_PATH);

			if (intExtra) {// 代表视频录制完毕

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

						mVideo.add(dbFileState);

						getVideoFragment().refershAdapter(mVideo);

					}

				}

			}

		}

	}

	private List<DBFileState> getImageSource() {
		List<DBFileState> mList = new ArrayList<DBFileState>();

		FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
		if (TextUtils.isEmpty(imageRootPath)) {
			return mList;
		}
		File file3 = new File(imageRootPath);
		if (file3.exists()) {
			File[] listFiles2 = file3.listFiles();
			for (int j = 0; j < listFiles2.length; j++) {
				String path = listFiles2[j].getAbsolutePath();
				List<DBFileState> find = fileStateDao.find(null, "path =?", new String[] { path }, null, null, null,
						null);
				// if (find.size() < 1) {
				// DBFileState mFileState = new DBFileState();
				// mFileState.setObj(false);
				// mFileState.setPath(path);
				// mFileState.setMessage(secondid);
				// mFileState.setState(Constants.readyupload);
				// fileStateDao.insert(mFileState);
				// find.add(mFileState);
				// }
				if (find != null && find.size() > 0) {

					for (int i = 0; i < find.size() - 1; i++) {
						DBFileState dbFileState = find.get(i);
						int id = dbFileState.getId();
						fileStateDao.delete(id + "");

					}
					try {
						DBFileState mFile = find.get(find.size() - 1);
						mFile.setObj(false);
						mList.add(mFile);
					} catch (Exception e) {
					}

					find.clear();
					find = null;
				} else {
					DBFileState mFile = new DBFileState();
					mFile.setPath(path);
					mFile.setObj(false);
					mFile.setState(Constants.READY_UPLOAD);
					int id = (int) fileStateDao.insert(mFile);
					mFile.setId(id);
					mList.add(mFile);
				}
			}
		}

		return mList;
	}

	public List<DBFileState> getVideoSource() {
		List<DBFileState> mList = new ArrayList<DBFileState>();

		FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
		if (TextUtils.isEmpty(videoRootPath)) {
			return mList;
		}
		File file3 = new File(videoRootPath);
		if (file3.exists()) {
			File[] listFiles2 = file3.listFiles();
			for (int j = 0; j < listFiles2.length; j++) {
				String path = listFiles2[j].getAbsolutePath();
				List<DBFileState> find = fileStateDao.find(null, "path =?", new String[] { path }, null, null, null,
						null);
				// if (find.size() < 1) {
				// DBFileState mFileState = new DBFileState();
				// mFileState.setObj(false);
				// mFileState.setPath(path);
				// mFileState.setMessage(secondid);
				// mFileState.setState(Constants.readyupload);
				// fileStateDao.insert(mFileState);
				// find.add(mFileState);
				// }
				if (find != null && find.size() > 0) {

					for (int i = 0; i < find.size() - 1; i++) {
						DBFileState dbFileState = find.get(i);
						int id = dbFileState.getId();
						fileStateDao.delete(id + "");

					}
					try {
						DBFileState mFile = find.get(find.size() - 1);
						mFile.setObj(false);
						Bitmap mBitmap = ThumbnailUtils.createVideoThumbnail(path,
								MediaStore.Video.Thumbnails.MINI_KIND);
						mFile.setObj2(mBitmap);
						mList.add(mFile);
					} catch (Exception e) {
					}

					find.clear();
					find = null;
				} else {
					DBFileState mFile = new DBFileState();
					Bitmap mBitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
					mFile.setPath(path);
					mFile.setObj(false);
					mFile.setObj2(mBitmap);
					mFile.setState(Constants.READY_UPLOAD);
					int id = (int) fileStateDao.insert(mFile);
					mFile.setId(id);
					mList.add(mFile);
				}
			}
		}

		return mList;
	}

	class AnsyQuery extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			WorkHandOverTabAty.mPhoto = getImageSource();
			WorkHandOverTabAty.mVideo = getVideoSource();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			mWaiteDialog.close();
			mAdapter = new WorkHandOverAdapter(WorkHandOverTabAty.this.getSupportFragmentManager());
			mViewPager.setAdapter(mAdapter);
		}

	}

	class WorkHandOverAdapter extends FragmentPagerAdapter {

		public WorkHandOverAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragment.get(position);
		}

		@Override
		public int getCount() {
			return mFragment.size();
		}

	}

	class FragmentOnPagerChangerListener extends MyOnPageChangeListener {
		private Context con;

		public FragmentOnPagerChangerListener(Context con) {
			this.con = con;
		}

		@Override
		public void onPageSelected(int arg0) {
			super.onPageSelected(arg0);
			switch (arg0) {
			case 0:
				getHeaderOkBtn().setText(con.getString(R.string.dis_tv_up_video));
				break;

			case 1:
				getHeaderOkBtn().setText(con.getString(R.string.dis_tv_up_photo));
				break;
			}

		}
	}

	/**
	 * 设置返回的点击事件并且会关闭当前的页面，如果在关闭的同时需要处理其他逻辑可以在onclick中添加R.id.iv_back
	 */
	public void setBackOnClickFinish() {
		getHeaderBackBtn().setVisibility(View.VISIBLE);
		getHeaderBackBtn().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WorkHandOverTabAty.this.finish();
				WorkHandOverTabAty.this.onClick(v);
			}
		});
	}

	private TextView iv_back = null;
	private TextView tv_title = null;
	private Button btn_ok = null;

	protected TextView getHeaderBackBtn() {
		if (iv_back == null) {
			iv_back = (TextView) findViewById(R.id.tv_back);
		}
		return (TextView) iv_back;
	}

	/**
	 * 设置标题
	 */
	protected void setHeaderTitleText(String mTitle) {
		getHeaderTitleTv().setVisibility(View.VISIBLE);
		getHeaderTitleTv().setText(mTitle);
	}

	protected TextView getHeaderTitleTv() {
		if (tv_title == null) {
			tv_title = (TextView) findViewById(R.id.tv_title);
		}
		return tv_title;
	}

	protected Button getHeaderOkBtn() {
		if (btn_ok == null) {
			btn_ok = (Button) findViewById(R.id.btn_ok);
		}
		return btn_ok;
	}

	private void onResponseVideo(String body, int encoding, Object obj) {
		FileStateDao mDBFile = (FileStateDao) DBFactory.getDBFileState(this);
		switch (encoding) {
		case AnsynHttpRequest.SUCCESS_HTTP:
			Bundle bun = null;
			try {
				bun = ParseJson.parseUploadVideo(new JSONObject(body));

				if (bun.getBoolean("status")) {// 上传成功
					DBFileState mFile = (DBFileState) obj;
					mFile.setState(Constants.UPLOAD_FINISH);
					mDBFile.update(mFile);
					if (!isFinishing()) {
						getVideoFragment().refershAdapter(mVideo);
					}
				} else {
					throw new Exception("上传失败");
				}
			} catch (Exception e) {
				DBFileState mFile = (DBFileState) obj;
				mFile.setState(Constants.READY_UPLOAD);
				mDBFile.update(mFile);
				if (!isFinishing()) {
					getVideoFragment().refershAdapter(mVideo);
				}
			}

			break;

		case AnsynHttpRequest.FAILURE_HTTP:
			DBFileState mFile = (DBFileState) obj;
			mFile.setState(Constants.READY_UPLOAD);
			mDBFile.update(mFile);
			if (!isFinishing()) {
				getVideoFragment().refershAdapter(mVideo);
			}
			break;
		}

	}

	private void onResponsePhoto(String body, int encoding, Object obj) {
		FileStateDao mDBFile = (FileStateDao) DBFactory.getDBFileState(this);
		switch (encoding) {
		case AnsynHttpRequest.SUCCESS_HTTP:
			try {

				if (!TextUtil.isEmpty(body) || TextUtils.equals("1", body)) {// 上传成功

					DBFileState mFile = (DBFileState) obj;
					mFile.setState(Constants.UPLOAD_FINISH);
					mDBFile.update(mFile);
					if (!isFinishing()) {
						getPhotoFragment().refershAdapter(mPhoto);
					}
				} else {
					throw new Exception("上传失败");
				}
			} catch (Exception e) {
				DBFileState mFile = (DBFileState) obj;
				mFile.setState(Constants.READY_UPLOAD);
				mDBFile.update(mFile);
				if (!isFinishing()) {
					getPhotoFragment().refershAdapter(mPhoto);
				}
			}

			break;

		case AnsynHttpRequest.FAILURE_HTTP:
			DBFileState mFile = (DBFileState) obj;
			mFile.setState(Constants.READY_UPLOAD);
			mDBFile.update(mFile);
			if (!isFinishing()) {
				getPhotoFragment().refershAdapter(mPhoto);
			}
			break;
		}

	}

	@Override
	public void back(String data, int method, Object obj) {

		switch (method) {
		case MethodApi.HTTP_UPLOAD_VIDEO_WONUM:
			onResponseVideo(data, AnsynHttpRequest.SUCCESS_HTTP, obj);
			break;

		case MethodApi.HTTP_UPLOAD_PHOTO:
			onResponsePhoto(data, AnsynHttpRequest.SUCCESS_HTTP, obj);
			break;
		}

	}

	@Override
	public void badBack(String error, int method, Object obj) {
		switch (method) {
		case MethodApi.HTTP_UPLOAD_VIDEO_WONUM:
			onResponseVideo(error, AnsynHttpRequest.FAILURE_HTTP, obj);
			break;

		case MethodApi.HTTP_UPLOAD_PHOTO:
			onResponsePhoto(error, AnsynHttpRequest.FAILURE_HTTP, obj);
			break;
		}
	}

}
