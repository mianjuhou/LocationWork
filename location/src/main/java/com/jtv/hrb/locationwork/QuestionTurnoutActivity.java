package com.jtv.hrb.locationwork;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.jtv.hrb.locationwork.domain.TurnoutUiData;
import com.jtv.locationwork.activity.ClassSelectActivity;
import com.jtv.locationwork.adapter.LineNiceAdapter;
import com.jtv.locationwork.util.AudioRecordHelper;
import com.jtv.locationwork.util.CameraUtil;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.ImageUtil;
import com.jtv.locationwork.util.MediaPlayHelper;
import com.plutus.libraryui.spinner.NiceSpinner;
import com.yixia.weibo.sdk.util.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class QuestionTurnoutActivity extends Activity {
	public static final int QT_SELECT_REQUEST = 200;
	private boolean isAdd;
	private String linename;
	private int gh;
	private int linenum;
	private double value1;
	private String nametype;
	private String defectclass;
	private String fvalue;
	private String xvalue;
	private String yvalue;
	private String wonum;
	private String cp1nanumcfgrownum;
	
	private TextView tvLineName;
	private TextView tvGh;
	private NiceSpinner spQtLocation;
	private TextView tvQtList;
	private NiceSpinner spQtGrade;
	private EditText etFvalue;
	private EditText etQtDesc;
	private ImageView ivRecord;
	private Button btnPlay;
	private ImageButton ibTakePhoto;
	private Button btnPic;
	private Button btnCommit;
	private Button btnCancel;
	private TurnoutUiData uiData;
	
	private String audioPath ="";// 音频路径
	private String imagePath="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_turnout);
		initData();
		initView();
		initViewState();
	}

	private void initData() {
		Intent srcIntent = getIntent();
		
		isAdd = srcIntent.getBooleanExtra("isAdd", true);
		String uiDataJson = srcIntent.getStringExtra("uiData");
		uiData = JSON.parseObject(uiDataJson, TurnoutUiData.class);
		if(isAdd){
			wonum = srcIntent.getStringExtra("wonum");
			linename = srcIntent.getStringExtra("linename");
			gh = srcIntent.getIntExtra("gh", Integer.MIN_VALUE);
			linenum = srcIntent.getIntExtra("linenum", Integer.MIN_VALUE);
		}else{
			wonum = srcIntent.getStringExtra("wonum");
			linename=srcIntent.getStringExtra("linename");
			gh=srcIntent.getIntExtra("gh", Integer.MIN_VALUE);
			value1 = srcIntent.getDoubleExtra("value1", Double.MIN_VALUE);
			nametype = srcIntent.getStringExtra("nametype");
			defectclass = srcIntent.getStringExtra("defectclass");
			fvalue=srcIntent.getStringExtra("fvalue");
			xvalue = srcIntent.getStringExtra("xvalue");
			yvalue = srcIntent.getStringExtra("yvalue");
			linenum=srcIntent.getIntExtra("linenum", Integer.MIN_VALUE);
			cp1nanumcfgrownum = srcIntent.getStringExtra("cp1nanumcfgrownum");
		}
	}

	private void initView() {
		tvLineName = (TextView) findViewById(R.id.tv_line_name);
		tvGh = (TextView) findViewById(R.id.tv_rail_num);
		spQtLocation = (NiceSpinner) findViewById(R.id.sp_qt_location);
		tvQtList = (TextView) findViewById(R.id.tv_qt_list);
		spQtGrade = (NiceSpinner) findViewById(R.id.sp_qt_grade);
		etFvalue = (EditText)findViewById(R.id.et_fvalue);
		
		etQtDesc = (EditText) findViewById(R.id.et_qt_desc);
		ivRecord = (ImageView) findViewById(R.id.iv_record);
		btnPlay = (Button) findViewById(R.id.btn_play);

		ibTakePhoto = (ImageButton) findViewById(R.id.ibn_takephoto);
		btnPic = (Button) findViewById(R.id.btn_pic);
		
		btnCommit = (Button) findViewById(R.id.btn_commit);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
	}

	private void initViewState() {
		if(isAdd){
			tvLineName.setText(linename);
			tvGh.setText(uiData.partName);
			spQtLocation.setAdapterInternal(new LineNiceAdapter(this,getStringArrayByList(uiData.rowNames)));
			spQtGrade.setAdapterInternal(new LineNiceAdapter(this, new String[]{"A","B","C"}));
		}else{
			tvLineName.setText(linename);
			tvGh.setText(uiData.partName);
			spQtLocation.setAdapterInternal(new LineNiceAdapter(this,getStringArrayByList(uiData.rowNames)));
			spQtLocation.setSelectedIndex((int)value1);
			tvQtList.setText(nametype);
			spQtGrade.setAdapterInternal(new LineNiceAdapter(this, new String[]{"A","B","C"}));
			spQtGrade.setSelectedIndex(getSelectionIndex(defectclass));
			etQtDesc.setText(cp1nanumcfgrownum);
			etFvalue.setText(fvalue+"");
			audioPath=xvalue;
			imagePath=yvalue;
		}
		tvQtList.setOnClickListener(mOnClickListener);
		ivRecord.setOnTouchListener(mOnTouchListener);
		btnPlay.setOnClickListener(mOnClickListener);
		ibTakePhoto.setOnClickListener(mOnClickListener);
		btnPic.setOnClickListener(mOnClickListener);
		btnCommit.setOnClickListener(mOnClickListener);
		btnCancel.setOnClickListener(mOnClickListener);
	}
	
	private String[] getStringArrayByList(ArrayList<String> rowNames) {
		String[] strs=new String[rowNames.size()];
		for (int i = 0; i < rowNames.size(); i++) {
			strs[i]=rowNames.get(i);
		}
		return strs;
	}

	private int getSelectionIndex(String defectclass) {
		if("A".equals(defectclass)){
			return 0;
		}else if("B".equals(defectclass)){
			return 1;
		}else{
			return 2;
		}
	}

	private String getSelectionName(int index){
		if(index==0){
			return "A";
		}else if(index==1){
			return "B";
		}else{
			return "C";
		}
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		private CameraUtil cameraUtil;
		@Override
		public void onClick(View v) {
			int resId = v.getId();
			if (resId == R.id.tv_qt_list) {
				Intent rsIntent = new Intent(QuestionTurnoutActivity.this, ClassSelectActivity.class);
				QuestionTurnoutActivity.this.startActivityForResult(rsIntent, QT_SELECT_REQUEST);
			} else if (resId == R.id.ibn_takephoto) {
				openCamera();
			} else if (resId == R.id.btn_pic) {
				if(!TextUtils.isEmpty(imagePath)){
					CameraUtil.startEditor(QuestionTurnoutActivity.this, imagePath);
				}else{
					ToastUtils.showToast(QuestionTurnoutActivity.this, "没有要查看的图片");
				}
			}else if(resId==R.id.btn_play){
				play();
			}else if(resId==R.id.btn_commit){
				commit();
			}else if(resId==R.id.btn_cancel){
				setResult(RESULT_CANCELED);
				QuestionTurnoutActivity.this.finish();
			}
		}

		private void openCamera() {
			String MACCHA_PATH = CreatFileUtil.getFilePath(CreatFileUtil.getImage(QuestionTurnoutActivity.this)) + File.separator;// 图片的路径
			ImageUtil.getInstance().createMkdir(MACCHA_PATH);
			imagePath = MACCHA_PATH + ImageUtil.getPhotoFilename(wonum, new Date());
			cameraUtil = new CameraUtil();
			cameraUtil.startCameraEditor(QuestionTurnoutActivity.this, imagePath);
		}

		private void commit() {
			String questSelect=tvQtList.getText().toString().trim();
			String qtDesc=etQtDesc.getText().toString().trim();
			String fvalue=etFvalue.getText().toString().trim();
			if(!TextUtils.isEmpty(questSelect)){
				Intent resultDestIntent=new Intent();
				resultDestIntent.putExtra("isAdd", isAdd);
				resultDestIntent.putExtra("gh", gh);
				resultDestIntent.putExtra("linenum", linenum);
				resultDestIntent.putExtra("value1", spQtLocation.getSelectedIndex());
				resultDestIntent.putExtra("defectclass", getSelectionName(spQtGrade.getSelectedIndex()));
				resultDestIntent.putExtra("fvalue", fvalue);
				resultDestIntent.putExtra("nametype", questSelect);
				if(TextUtils.isEmpty(qtDesc)){
					qtDesc="";
				}
				resultDestIntent.putExtra("cp1nanumcfgrownum", qtDesc);
				resultDestIntent.putExtra("xvalue", audioPath);
				resultDestIntent.putExtra("yvalue", imagePath);
				setResult(RESULT_OK, resultDestIntent);
				QuestionTurnoutActivity.this.finish();
			}else{
				ToastUtils.showToast(QuestionTurnoutActivity.this, "请在问题列表中选择问题后再提交");
			}
		}

		private void play() {
			if(!TextUtils.isEmpty(audioPath)){
				MediaPlayHelper mediaPlayHelper = MediaPlayHelper.getMediaPlayHelper();
				mediaPlayHelper.setPlayCompleListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						ToastUtils.showToast(QuestionTurnoutActivity.this, "播放完毕");
					}
				});
				mediaPlayHelper.playMedia(QuestionTurnoutActivity.this, MediaPlayHelper.SDFLAG, 0, audioPath);
			}else{
				ToastUtils.showToast(QuestionTurnoutActivity.this, "请先录音再播放");
			}
		}
	};
	
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		private AudioRecordHelper mRecordUtil;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v.getId() == R.id.iv_record) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						ivRecord.setPressed(true);
						start();
						break;
					case MotionEvent.ACTION_UP:
						ivRecord.setPressed(false);
						stop();
						break;
				}
				return true;
			}
			return true;
		}
		
		private void start() {
			if(TextUtils.isEmpty(audioPath)){
				File dir = getDir("audio", MODE_PRIVATE);
				String path = dir.getAbsolutePath() + File.separator + System.currentTimeMillis()+"question.mp3";
				File file = new File(path);
				if (file.exists()) {
					file.delete();
				}
				audioPath = path;
			}
			if (mRecordUtil != null) {
				mRecordUtil.stop();
				mRecordUtil.release();
			}
			mRecordUtil = new AudioRecordHelper();
			mRecordUtil.initial(audioPath);
			try {
				mRecordUtil.prepare();
				mRecordUtil.start();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		private void stop() {
			if (mRecordUtil != null) {
				mRecordUtil.stop();
				mRecordUtil.release();
				mRecordUtil = null;
			}
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == QT_SELECT_REQUEST) {
				String detailItem = data.getStringExtra("detailItem");
				tvQtList.setText(detailItem);
			}
		}
	};
}
