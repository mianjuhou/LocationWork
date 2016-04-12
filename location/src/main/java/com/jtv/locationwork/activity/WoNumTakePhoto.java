package com.jtv.locationwork.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.jtv.base.util.CollectionActivity;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.dao.FileStateDao;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.entity.FileRequest;
import com.jtv.locationwork.entity.FileState;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.tree.ParmterMutableTreeNode;
import com.jtv.locationwork.util.CheckImageLoaderConfiguration;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.ImageUtil;
import com.jtv.locationwork.util.NetUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.YaSuoUtil;
import com.plutus.libraryui.dialog.DiaLogUtil;
import com.plutus.libraryui.dialog.LoadDataDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

public class WoNumTakePhoto extends BaseHttpGridPhotoAty
		implements OnItemClickListener, OnClickListener, OnItemLongClickListener, DialogInterface.OnClickListener {

	public String ROOT_IMAGE = "";// 全路径图片

	private File file;

	private LoadDataDialog loadDataDialog = null;

	public final static int REQUESTCAMERA = 0x11;

	private double[] gps = new double[2];

	String diaLogTitle[] = null;// 要显示的item标题

	ParmterMutableTreeNode parmeter[] = null;// 显示的dialog对应的数据

	private String table = ""; // 对应的表

	private String type = "";// 一级数据

	private String img = "";// 二级数据

	private String filterID = "";// 过滤的id

	private ParmterMutableTreeNode second;

	private TextView tv_dis_first;

	private TextView tv_dis_second;

	private static String wonum;

	private String thirdTitle = "备注";// 这个是第三层的标题

	public View addHead() {
		return View.inflate(this, R.layout.location_item_display_texthead, null);
	}
	
	public View addBottom() {
		return View.inflate(this, R.layout.location_jtv_addbottom_delete, null);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(Constants.PHOTO_PATH, PHOTO_PATH);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		super.onCreatInit(savedInstanceState);
		loadDataDialog = new LoadDataDialog(this);
		CollectionActivity.putTopActivity(this);

		mLinearlayHead.addView(addHead());
		mRellayBottom.addView(addBottom());
		
		getIntentParmter();

		File image = CreatFileUtil.getImage(this);
		ROOT_IMAGE = CreatFileUtil.getFilePath(image) + File.separator;
		getHeaderTitleTv().setText(getString(R.string.woquery_camera_tips));
		
		getHeaderOkIvbtn().setVisibility(View.VISIBLE);
		getHeaderOkIvbtn().setOnClickListener(this);
		getHeaderOkIvbtn().setText(getString(R.string.dis_tv_dcim));
		
		getHeaderOkBtn().setVisibility(View.VISIBLE);
		getHeaderOkBtn().setOnClickListener(this);
		getHeaderOkBtn().setText(getString(R.string.upload_photo));
		setBackOnClickFinish();
		findViewById(R.id.btn_delete).setOnClickListener(this);
		
		gps = SpUtiles.BaseInfo.getGps();

		if (savedInstanceState != null) {// 代表有数据
			PHOTO_PATH = savedInstanceState.getString(Constants.PHOTO_PATH);
			// list
		} else {// 第一次加载
			loadDataDialog.open();
			DBFileState dbFileState = new DBFileState();
			dbFileState.setObj(false);
			mData.add(dbFileState);
			new mAnsyQuery().execute();
		}

		tv_dis_first = (TextView) findViewById(R.id.tv_dis_first);
		tv_dis_second = (TextView) findViewById(R.id.tv_dis_second);

		// 设置路径
		if (second != null) {
			Object[] userObjectPath = (Object[]) second.getUserObjectPath();
			if (userObjectPath != null) {
				try {
					tv_dis_first.setText("当前上传路径: " + userObjectPath[1].toString() + " --> ");
					tv_dis_second.setText(userObjectPath[2].toString() + " --> " + wonum);
				} catch (Exception e) {
				}
			}
		}

		mGrid.setOnItemClickListener(this);
		mGrid.setOnItemLongClickListener(this);
	}
	
	private int deleteFile(){
		boolean change = false;
		int state =-1;
		
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

			if (isChoose&&state2==Constants.READY_UPLOAD) {
				File file = new File(path);
				if (file.exists()) {
					boolean delete = file.delete();
					
					if(delete){
						deleteIndex.add(i);
					}
					
					change = true;
					state=2;
				}

			}
		}
		
		for (int i = deleteIndex.size()-1; i>=0; i--) {
			int index = deleteIndex.get(i);
			mData.remove(index);
		}
		
		if (change)
			refershDate();
		
		return state;
	}

	private void getIntentParmter() {
		boolean hasCategory = getIntent().hasExtra(Constants.INTENT_FLAG_PATMTER);
		table = getIntent().getStringExtra(Constants.INTENT_TABLE);
		wonum = getIntent().getStringExtra(Constants.INTENT_WONUM);
		type = getIntent().getStringExtra(Constants.INTENT_PARMTER);

		filterID = getIntent().getStringExtra(Constants.INTENT_SECONDID);
		if (hasCategory) {
			second = (ParmterMutableTreeNode) getIntent().getSerializableExtra(Constants.INTENT_FLAG_PATMTER);
			if (second != null) {
				Enumeration<ParmterMutableTreeNode> children = second.children();
				ArrayList<ParmterMutableTreeNode> itemList = Collections.list(children);
				if (itemList == null || itemList.size() < 1) {// 备注没有子孩子
					try {
						img = (String) second.getParmter()[1];
					} catch (Exception e) {
					}

				}
				diaLogTitle = new String[itemList.size()];
				parmeter = new ParmterMutableTreeNode[itemList.size()];
				for (int i = 0; i < itemList.size(); i++) {
					ParmterMutableTreeNode threeNode = itemList.get(i);
					String title = (String) threeNode.getUserObject();
					diaLogTitle[i] = title;
					parmeter[i] = threeNode;
				}
				if (parmeter.length == 1) {
					img = (String) parmeter[0].getParmter()[1];
					thirdTitle = (String) parmeter[0].getUserObject();
				}
			}
			return;
		}

		// firstid = getIntent().getStringExtra(Constants.INTENT_FIRSTID);
		img = getIntent().getStringExtra(Constants.INTENT_PARMTER_IMG);

	}

	@Override
	protected void onStart() {
		super.onStart();
		CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
	}

	class mAnsyQuery extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			imageAsynTask();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			loadDataDialog.close();
			refershDate();
		}

	}

	/**
	 * 从数据库中查找图片
	 * 
	 * @throws
	 */
	private void imageAsynTask() {
		FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
		if (TextUtils.isEmpty(ROOT_IMAGE)) {
			return;
		}
		List<DBFileState> find = fileStateDao.find(null, "message =?",
				new String[] { filterID }, null, null, null, null);
		
		for (int i = 0; i < find.size(); i++) {
			DBFileState dbFileState = find.get(i);
			String path = dbFileState.getPath();
			File file2 = new File(path);
			
			if(file2!=null&&file2.isFile()){
				mData.add(dbFileState);
			}else{
				fileStateDao.delete(dbFileState.getId()+"");
			}
		}
		
	}

	boolean flag = true;

	int count = 0;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_ok) {
			if (diaLogTitle != null && diaLogTitle.length > 1) {
				DiaLogUtil.showItemDiaLog(this, "提交位置", diaLogTitle, this);
			} else {
				onClick(null, -1);
			}
		} else if (R.id.iv_ok == id) {
			Intent intent = new Intent(this,PreviewDCIMAty.class);
			startActivityForResult(intent,REQUESTCAMERA);
		}else if(R.id.btn_delete == id){
			deleteFile();
		}
	}

	public void upLoad(String type, String img) {

		if (!NetUtil.hasConnectedNetwork(this)) {
			UToast.makeShortTxt(this, getString(R.string.error_http));
			return;
		}
		boolean flag = true;
		if (mData != null && mData.size() > 0) {// 代表有数据
			for (int i = 0; i < mData.size(); i++) {
				FileState fileState = mData.get(i);
				int other = fileState.getState();// 获取当前的状态 三个
				boolean choose = (Boolean) fileState.getObj();// 获取当前是否选中，如果选中为true
				if (choose && Constants.READY_UPLOAD == (other)) {// 获取当前的选中并且状态不是上传或则是上
					String path = fileState.getPath(); // 传中
					file = new File(path);
					if (file != null && file.exists()) {
						fileState.setState(Constants.UPLOADING);
						fileState.setObj(false);
						HashMap<String, String> mHash = new HashMap<String, String>();
						mHash.put(CoustomKey.WONUM, wonum);
						mHash.put("type", type);
						mHash.put("img", img);
						mHash.put("table", table);
						mHash.put("geox", gps[0] + "");
						mHash.put("secondid", filterID);
						mHash.put("geoy", gps[1] + "");
						FileRequest<String, String> mFile = new FileRequest<String, String>(mHash, file, this,
								fileState);

						String currDateFormat = DateUtil.getCurrDateFormat(DateUtil.style_sf);// 显示时间
						putFile(mFile, thirdTitle + "时间:" + currDateFormat);
						flag = false;
					} else {// 文件不存在可能存在数据库中，需要删除原来的数据库
					}
				}
			}
			if (flag) {
				UToast.makeShortTxt(this, getString(R.string.no_select_data));
			} else {
				refershDate();
			}
		} else {
			UToast.makeShortTxt(this, "没有需要上传到数据");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	String PHOTO_PATH = null;

	/**
	 * 打开相机进行照相
	 * 
	 * @author:zn
	 * @version:2015-1-29
	 * @return
	 */
	public void openCamera() {
		String MACCHA_PATH = CreatFileUtil.getFilePath(CreatFileUtil.getImage(this)) + File.separator;
		ImageUtil.getInstance().createMkdir(MACCHA_PATH);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		PHOTO_PATH = MACCHA_PATH + ImageUtil.getPhotoFilename(wonum, new Date());
		
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(PHOTO_PATH)));
		startActivityForResult(intent, REQUESTCAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCAMERA:
			if (resultCode != RESULT_CANCELED&&resultCode!=PreviewDCIMAty.RESULT_CODE) {
				if (!TextUtils.isEmpty(PHOTO_PATH)) {
					PHOTO_PATH=YaSuoUtil.ctrlCameraImage(PHOTO_PATH);
					DBFileState fileState = new DBFileState();
					fileState.setObj(false);
					fileState.setPath(PHOTO_PATH);
					fileState.setMessage(filterID);
					fileState.setState(Constants.READY_UPLOAD);
					FileStateDao mDBFile = (FileStateDao) DBFactory.getDBFileState(this);
					int insert = (int) mDBFile.insert(fileState);
					fileState.setId(insert);
					mData.add(fileState);
					refershDate();
				}
			}else if(PreviewDCIMAty.RESULT_CODE==resultCode){
				String path =data.getStringExtra(PreviewDCIMAty.RESULT_ADDRESS);
				
				if (!TextUtils.isEmpty(path)) {
					DBFileState fileState = new DBFileState();
					fileState.setObj(false);
					fileState.setPath(path);
					fileState.setMessage(filterID);
					fileState.setState(Constants.READY_UPLOAD);
					FileStateDao mDBFile = (FileStateDao) DBFactory.getDBFileState(this);
					int insert = (int) mDBFile.insert(fileState);
					fileState.setId(insert);
					mData.add(fileState);
					refershDate();
				}
			}
			
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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
	public void onClick(DialogInterface dialog, int which) {// dialog 弹出

		if (dialog != null && parmeter != null && which != -1) {// 获取那个条目的点击事件
			ParmterMutableTreeNode parmterMutableTreeNode = parmeter[which];
			Object[] parmter = parmterMutableTreeNode.getParmter();
			img = (String) parmter[1];
			thirdTitle = (String) parmterMutableTreeNode.getUserObject();
		}

		upLoad(type + "", img + "");

		if (dialog != null) {
			dialog.dismiss();
		}
	}

	private int number = -1;

	@Override
	public String onFileStart(HashMap mhash, File file, Object obj) {
		// super.onFileStart(mhash, file, obj);

		String secondid = (String) mhash.get("secondid");
		String wonum = (String) mhash.get(CoustomKey.WONUM);
		String type = (String) mhash.get("type");
		String img = (String) mhash.get("img");
		String table = (String) mhash.get("table");
		String geox = (String) mhash.get("geox");// 精度
		String geoy = (String) mhash.get("geoy");

		FileStateDao fileStateDao = (FileStateDao) DBFactory.getDBFileState(this);
		DBFileState mFileState = (DBFileState) obj;
		mFileState.setPath(file.getAbsolutePath());
		mFileState.setMessage(secondid);
		mFileState.setState(Constants.UPLOADING);
		fileStateDao.update(mFileState);

		if (number < 1) {
			number = 1;
			number = SpUtiles.BaseInfo.mbasepre.getInt(CoustomKey.SP_KEY_NUMBER, new Random().nextInt(1000));// 一个计数器
		}

		number++;

		String body = TrackAPI.uploadImageTestForLink(wonum, file.getAbsolutePath(), geox, geoy, GlobalApplication.attid,
				GlobalApplication.mBase64Lead, type, img, table, number + "");

		SpUtiles.BaseInfo.mbasepre.edit().putInt(CoustomKey.SP_KEY_NUMBER, number).commit();
		return body;
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
}
