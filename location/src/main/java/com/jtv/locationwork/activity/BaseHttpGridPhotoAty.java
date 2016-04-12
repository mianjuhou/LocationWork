package com.jtv.locationwork.activity;

import java.io.File;
import java.util.HashMap;

import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.dao.FileStateDao;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.entity.FileRequest;
import com.jtv.locationwork.entity.FileRequest.FileUploadlistener;
import com.jtv.locationwork.util.CheckImageLoaderConfiguration;
import com.jtv.locationwork.util.Constants;
import com.plutus.queue.querypool.QueueUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

public abstract class BaseHttpGridPhotoAty extends PhotoAblumGridAty
		implements OnItemLongClickListener, FileUploadlistener {

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		super.onCreatInit(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		return false;
	}

	@Override
	public void onClick(View v) {

	}

	public void putFile(FileRequest<String, String> mFile, String title) {

		QueueUtil.add(QueueUtil.getRequestQueue(this), mFile);
		File file = mFile.getFile();
		DBFileState mFileState = (DBFileState) mFile.getObj();
		FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
		mFileState.setPath(file.getAbsolutePath());
		mFileState.setState(Constants.UPLOADING);
		mFileState.setMessage2(title);
		fileStateDao.update(mFileState);

	}

	public void putFile(FileRequest<String, String> mFile) {
		QueueUtil.add(QueueUtil.getRequestQueue(this), mFile);
		File file = mFile.getFile();
		DBFileState mFileState = (DBFileState) mFile.getObj();
		FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
		mFileState.setPath(file.getAbsolutePath());
		mFileState.setState(Constants.UPLOADING);
		mFileState.setMessage("");
		fileStateDao.update(mFileState);

	}

	@Override
	public String onFileStart(HashMap mhash, File file, Object obj) {
		FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
		DBFileState mFileState = (DBFileState) obj;
		mFileState.setPath(file.getAbsolutePath());
		mFileState.setState(Constants.UPLOADING);
		fileStateDao.update(mFileState);
		return "1";
	}

	protected int mState = Constants.READY_UPLOAD;

	@Override
	public void onFilePostEnd(HashMap mhash, File file, Object obj, String response) {
		if ("1".equals(response)) {
			FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
			DBFileState mFileState = (DBFileState) obj;
			mFileState.setPath(file.getAbsolutePath());
			mFileState.setState(Constants.UPLOAD_FINISH);
			fileStateDao.update(mFileState);
			mState = Constants.UPLOAD_FINISH;
		} else {
			FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
			DBFileState mFileState = (DBFileState) obj;
			mFileState.setPath(file.getAbsolutePath());
			mFileState.setState(Constants.READY_UPLOAD);
			fileStateDao.update(mFileState);
			mState = Constants.READY_UPLOAD;
		}

	}
}
