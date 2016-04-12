package com.jtv.locationwork.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONException;

import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.dao.FileStateDao;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.entity.FileRequest;
import com.jtv.locationwork.entity.FileState;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.ImageUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.YaSuoUtil;
import com.plutus.libraryui.dialog.LoadDataDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

public class TakePhotoAty extends BaseHttpGridPhotoAty {
	LoadDataDialog mWaitDialog = null;
	private String mroot_image;

	private String mWonum;
	private String geox;
	private String geoy;

	public View addBottom() {
		return View.inflate(this, R.layout.location_jtv_addbottom_delete, null);
	}

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		super.onCreatInit(savedInstanceState);
		mWaitDialog = new LoadDataDialog(this);
		mWaitDialog.open();
		mroot_image = CreatFileUtil.getFilePath(CreatFileUtil.getImage(this)) + File.separator;
		// upload.clear();
		getHeaderTitleTv().setText(getString(R.string.dis_tv_phototitle));
		getHeaderOkBtn().setVisibility(View.VISIBLE);
		getHeaderOkBtn().setOnClickListener(this);
		getHeaderBackBtn().setVisibility(View.VISIBLE);
		getHeaderBackBtn().setOnClickListener(this);
		getHeaderOkBtn().setText(getString(R.string.upload_photo));

		getHeaderOkIvbtn().setVisibility(View.VISIBLE);
		getHeaderOkIvbtn().setOnClickListener(this);
		getHeaderOkIvbtn().setText(getString(R.string.dis_tv_dcim));

		mRellayBottom.addView(addBottom());

		setBackOnClickFinish();
		initParmter();

		if (savedInstanceState != null) {// 代表有数据
			mroot_image = savedInstanceState.getString(Constants.PHOTO_PATH);
			// list
		} else {// 第一次加载
			DBFileState dbFileState = new DBFileState();
			dbFileState.setObj(false);
			mData.add(dbFileState);
			new AnsyQuery().execute();
		}

		findViewById(R.id.btn_delete).setOnClickListener(this);
		mGrid.setOnItemLongClickListener(this);

	}

	// 初始化一些参数
	private void initParmter() {
		double[] gps = SpUtiles.BaseInfo.getGps();
		geox = gps[0] + "";
		geoy = gps[1] + "";
		mWonum = getIntent().getStringExtra(CoustomKey.WONUM);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(Constants.PHOTO_PATH, mroot_image);
		super.onSaveInstanceState(outState);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			boolean change = false;
			for (int i = 0; i < mData.size(); i++) {
				FileState fileState = mData.get(i);
				Boolean isChoose = (Boolean) fileState.getObj();
				if (isChoose == null) {
					fileState.setObj(false);
					isChoose = false;
				}
				String path = fileState.getPath();

				if (isChoose) {
					File file = new File(path);
					if (file.exists()) {
						HashMap<String, String> mHash = new HashMap<String, String>();
						mHash.put(CoustomKey.WONUM, mWonum);
						mHash.put("geox", geox);
						mHash.put("geoy", geoy);
						FileRequest<String, String> mRequest = new FileRequest<String, String>(mHash, file, this,
								fileState);
						putFile(mRequest);
						fileState.setState(Constants.UPLOADING);
						change = true;
					}

				}
			}
			if (change)
				refershDate();

			break;

		case R.id.iv_ok:

			Intent intent = new Intent(this, PreviewDCIMAty.class);
			startActivityForResult(intent, REQUESTCAMERA);

			break;
		case R.id.btn_delete:
			int state = deleteFile();

			if (state == 2) {
				UToast.makeShortTxt(this, getString(R.string.dis_delete_ok));
			} else {
				UToast.makeShortTxt(this, getString(R.string.no_select_data));
			}

			break;
		}
	}

	protected void openCamera() {
		String MACCHA_PATH = CreatFileUtil.getFilePath(CreatFileUtil.getImage(this)) + File.separator;// 图片的路径
		ImageUtil.getInstance().createMkdir(MACCHA_PATH);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mroot_image = MACCHA_PATH + ImageUtil.getPhotoFilename(mWonum, new Date());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mroot_image)));
		startActivityForResult(intent, REQUESTCAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCAMERA:
			if (resultCode != RESULT_CANCELED) {
				if (!TextUtils.isEmpty(mroot_image) && PreviewDCIMAty.RESULT_CODE != resultCode) {
					mroot_image = YaSuoUtil.ctrlCameraImage(mroot_image);
					File downLoad = CreatFileUtil.getDownLoad(this);
					String add = downLoad.getAbsolutePath() + "/" + "test.png";
					// YaSuoUtil.transImage(mroot_image, add, 700, 1270, 100);
					DBFileState fileState = new DBFileState();
					fileState.setObj(false);
					fileState.setPath(mroot_image);
					fileState.setState(Constants.READY_UPLOAD);
					FileStateDao mDBFile = (FileStateDao) DBFactory.getDBFileState(this);
					long insert = mDBFile.insert(fileState);
					fileState.setId((int) insert);
					mData.add(fileState);
					refershDate();
				} else if (PreviewDCIMAty.RESULT_CODE == resultCode) {
					String path = data.getStringExtra(PreviewDCIMAty.RESULT_ADDRESS);

					if (!TextUtils.isEmpty(path)) {
						DBFileState fileState = new DBFileState();
						fileState.setObj(false);
						fileState.setPath(path);
						fileState.setState(Constants.READY_UPLOAD);
						FileStateDao mDBFile = (FileStateDao) DBFactory.getDBFileState(this);
						long insert = mDBFile.insert(fileState);
						fileState.setId((int) insert);
						mData.add(fileState);
						refershDate();
					}

				}
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
			Intent intent = new Intent(this, PainterAty.class);
			intent.putExtra(PhotoAty.PHOTO_PATH, path_absolute);
			startActivity(intent);
			return true;
		}

	}

	@Override
	public void onItemClick(Boolean oneClick, AdapterView<?> parent, View view, int position, int faceposition,
			ViewGroup child) {
		if (oneClick) {// 第一个条目
			openCamera();
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

	/**
	 * 从数据库中查找图片
	 * 
	 * @throws
	 */
	private void imageAsynTask() {
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
						fileStateDao.delete(id+"");

					}
					try {
						DBFileState mFile = find.get(find.size() - 1);
						mData.add(mFile);
					} catch (Exception e) {
					}

					find.clear();
					find = null;
				}
			}
		}
	}

	private int number = new Random().nextInt();

	@Override
	public String onFileStart(HashMap mhash, File file, Object obj) {
		String wonum = (String) mhash.get(CoustomKey.WONUM);
		String geox = (String) mhash.get("geox");
		String geoy = (String) mhash.get("geoy");
		String result = "";

		try {
			number++;
			result = TrackAPI.uploadImageTest(wonum, file.getAbsolutePath(), geox, geoy, this, GlobalApplication.mBase64Lead,
					number + "");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void onFilePostEnd(HashMap mhash, File file, Object obj, String response) {
		super.onFilePostEnd(mhash, file, obj, response);

		if (mData != null && mData.size() > 0 && obj != null) {

			if (!isFinishing() && obj instanceof FileState) {// 当前界面没有被finish
				FileState mFile = (FileState) obj;
				for (int i = 0; i < mData.size(); i++) {
					FileState fileState = mData.get(i);
					if (fileState == mFile) {
						fileState.setState(mState);
						break;
					}
				}
				refershDate();
			}
		}
	}

	class AnsyQuery extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			imageAsynTask();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			mWaitDialog.close();
			refershDate();
		}

	}

}
