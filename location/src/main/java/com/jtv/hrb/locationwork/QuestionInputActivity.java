package com.jtv.hrb.locationwork;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.locationwork.activity.ClassSelectActivity;
import com.jtv.locationwork.adapter.LineNiceAdapter;
import com.jtv.locationwork.util.AudioRecordHelper;
import com.jtv.locationwork.util.CameraUtil;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.ImageUtil;
import com.jtv.locationwork.util.MediaPlayHelper;
import com.plutus.libraryui.spinner.NiceSpinner;
import com.yixia.weibo.sdk.util.ToastUtils;

public class QuestionInputActivity extends Activity {
	public static final int QT_SELECT_REQUEST = 100;
	private String linename;
	private String railnum;
	private int startkm;
	private int startm;
	private int endkm;
	private int endm;
	private TextView tvStartKm;
	private TextView tvStartM;
	private TextView tvEndKm;
	private TextView tvEndM;
	private TextView tvRailNum;
	private TextView tvLineName;
	private EditText etQtLocation;
	private TextView tvQtList;
	
	
//	private StaticCheckDsbService service=new StaticCheckDbService(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_input);
		initData();
		initView();
		initViewState();
	}

	private void initData() {
		Intent requestDestIntent = getIntent();
		String questsrcjson = requestDestIntent.getStringExtra("questsrcjson");
		if(!TextUtils.isEmpty(questsrcjson)){
			measureData = JSON.parseObject(questsrcjson, MeasureData.class);
			linename =requestDestIntent.getStringExtra("linename");
			startkm = getKM(measureData.getStartmeasure()+"");
			startm = getM(measureData.getStartmeasure()+"");
			endkm = getKM(measureData.getEndmeasure()+"");
			endm = getM(measureData.getEndmeasure()+"");
			railnum = measureData.getGh();
			qtDesc = measureData.getNametype();
			qtDescCustom = measureData.getCp1nanumcfgrownum();
			wonum = measureData.getWonum();
			assetnum = measureData.getAssetnum();
			linenum = measureData.getLinenum();
		}else{
			linename = requestDestIntent.getStringExtra("linename");
			startkm = requestDestIntent.getIntExtra("startkm", 0);
			startm = requestDestIntent.getIntExtra("startm", 0);
			endkm = requestDestIntent.getIntExtra("endkm", 0);
			endm = requestDestIntent.getIntExtra("endm", 0);
			railnum = requestDestIntent.getIntExtra("railnum", 0)+"";
			qtDesc="";
			qtDescCustom="";
			wonum = requestDestIntent.getStringExtra("wonum");
			assetnum = requestDestIntent.getStringExtra("assetnum");
			linenum = requestDestIntent.getIntExtra("linenum",0);
		}
	}
	
	private double getScopeByInt(int km, int m) {
		return m*1.0/1000+km*1.0;
	}
	
	private int getKM(String dkm) {
		BigDecimal bigDecimal = new BigDecimal(dkm);
		int km = bigDecimal.intValue();
		return km;
	}

	private int getM(String dkm) {
		BigDecimal bigDecimal = new BigDecimal(dkm);
		int km = bigDecimal.intValue();
		BigDecimal multiply = bigDecimal.subtract(new BigDecimal(km)).multiply(new BigDecimal(1000));
		return multiply.intValue();
	}
	
	private void initView() {
		tvLineName = (TextView) findViewById(R.id.tv_line_name);
		tvStartKm = (TextView) findViewById(R.id.tv_start_km);
		tvStartM = (TextView) findViewById(R.id.tv_start_m);
		tvEndKm = (TextView) findViewById(R.id.tv_end_km);
		tvEndM = (TextView) findViewById(R.id.tv_end_m);
		tvRailNum = (TextView) findViewById(R.id.tv_rail_num);
		etQtLocation = (EditText) findViewById(R.id.et_qt_location);
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
		tvLineName.setText(linename);
		tvStartKm.setText(startkm + "");
		tvStartM.setText(startm + "");
		tvEndKm.setText(endkm + "");
		tvEndM.setText(endm + "");
		tvRailNum.setText(railnum + "");
		tvQtList.setText(qtDesc);
		etQtDesc.setText(qtDescCustom);
		tvQtList.setOnClickListener(mOnClickListener);
		spQtGrade.setAdapterInternal(new LineNiceAdapter(this, new String[] { "A", "B", "C" }));
		ibTakePhoto.setOnClickListener(mOnClickListener);
		btnPic.setOnClickListener(mOnClickListener);
		ivRecord.setOnTouchListener(mOnTouchListener);
		btnPlay.setOnClickListener(mOnClickListener);
		btnCommit.setOnClickListener(mOnClickListener);
		btnCancel.setOnClickListener(mOnClickListener);
		if(measureData!=null){
			etQtLocation.setText(measureData.getValue1()+"");
			spQtGrade.setSelectedIndex(getSelectIndexByName(measureData.getDefectclass()));
			etFvalue.setText(measureData.getFvalue()+"");
			audioPath=measureData.getXvalue();
			mroot_image=measureData.getYvalue();
		}
	}

	private int getSelectIndexByName(String defectclass) {
		int positioin=0;
		if(defectclass.equals("A")){
			positioin=0;
		}else if(defectclass.equals("B")){
			positioin=1;
		}else if(defectclass.equals("C")){
			positioin=2;
		}
		return positioin;
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int resId = v.getId();
			if (resId == R.id.tv_qt_list) {
				Intent rsIntent = new Intent(QuestionInputActivity.this, ClassSelectActivity.class);
				QuestionInputActivity.this.startActivityForResult(rsIntent, QT_SELECT_REQUEST);
			} else if (resId == R.id.ibn_takephoto) {
				openCamera();
			} else if (resId == R.id.btn_pic) {
				if(!TextUtils.isEmpty(mroot_image)){
					CameraUtil.startEditor(QuestionInputActivity.this, mroot_image);
				}else{
					ToastUtils.showToast(QuestionInputActivity.this, "没有要查看的图片");
				}
			}else if(resId==R.id.btn_play){
				play();
			}else if(resId==R.id.btn_commit){
				commit();
			}else if(resId==R.id.btn_cancel){
				setResult(RESULT_CANCELED);
				QuestionInputActivity.this.finish();
			}
		}

		private void play() {
			if(!TextUtils.isEmpty(audioPath)){
				MediaPlayHelper mediaPlayHelper = MediaPlayHelper.getMediaPlayHelper();
				mediaPlayHelper.setPlayCompleListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						ToastUtils.showToast(QuestionInputActivity.this, "播放完毕");
					}
				});
				mediaPlayHelper.playMedia(QuestionInputActivity.this, MediaPlayHelper.SDFLAG, 0, audioPath);
			}else{
				ToastUtils.showToast(QuestionInputActivity.this, "请先录音再播放");
			}
		}
	};

	private OnTouchListener mOnTouchListener = new OnTouchListener() {
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
	};
	private NiceSpinner spQtGrade;
	private ImageButton ibTakePhoto;
	private Button btnPic;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == QT_SELECT_REQUEST) {
				String detailItem = data.getStringExtra("detailItem");
				tvQtList.setText(detailItem);
			}
		}
	}

	protected void commit() {
		String questSelect=tvQtList.getText().toString().trim();
		if(!TextUtils.isEmpty(questSelect)){
			Intent resultDestIntent=new Intent();
			resultDestIntent.putExtra("questdesc", questSelect);
			String json=JSON.toJSONString(getQuestDetail(questSelect));
			resultDestIntent.putExtra("josn", json);
			setResult(RESULT_OK, resultDestIntent);
			QuestionInputActivity.this.finish();
		}else{
			ToastUtils.showToast(QuestionInputActivity.this, "请在问题列表中选择问题后再提交");
		}
	}

	private MeasureData getQuestDetail(String questSelect) {
		MeasureData measureData=new MeasureData();
		measureData.setSiteid(GlobalApplication.siteid);
		measureData.setOrgid(GlobalApplication.orgid);
		measureData.setAssetnum(assetnum);
		measureData.setWonum(wonum);
		measureData.setStatus("-1");
		measureData.setFlag_v("2");
		measureData.setHasld(0);
		
		String qtDescCustom=etQtDesc.getText().toString().trim();
		measureData.setCp1nanumcfgrownum(qtDescCustom);
		measureData.setInspdate(DateUtil.getCurrDateFormat(DateUtil.style_nyrsfm));
		measureData.setSyncstate(0);
		measureData.setAssetattrid("12334");
		measureData.setTag("1-1");
		
		double startScope = getLcScope(tvStartKm, tvStartM);
		double endScope = getLcScope(tvEndKm, tvEndM);
		measureData.setStartmeasure(startScope);
		measureData.setEndmeasure(endScope);
		measureData.setGh(railnum+"");
		measureData.setValue1(getTextViewDoouble(etQtLocation));
		measureData.setNametype(questSelect);
		measureData.setDefectclass(getQtGrade());
		measureData.setFvalue(getDoubleValue(etFvalue.getText().toString().trim()));
		measureData.setXvalue(audioPath);
		measureData.setYvalue(mroot_image);
		measureData.setLinenum(linenum);
		return measureData;
	}

	private double getDoubleValue(String doubleStr){
		double dvalue=0;
		if(!TextUtils.isEmpty(doubleStr)){
			try {
				dvalue = Double.parseDouble(doubleStr);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return dvalue;
	}
	
	private String getQtGrade() {
		int position=spQtGrade.getSelectedIndex();
		String grade=null;
		if(position==0){
			grade="A";
		}else if(position==1){
			grade="B";
		}else if(position==2){
			grade="C";
		}
		return grade;
	}

	private double getTextViewDoouble(EditText et) {
		String strValue=et.getText().toString().trim();
		if(TextUtils.isEmpty(strValue)){
			return 0;
		}
		double value=0;
		try {
			value = Double.parseDouble(strValue);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return value;
	}

	private double getLcScope(TextView etStartLc, TextView etEndLc) {
		int startLc = getEdtiTextNum(etStartLc);
		int endLc = getEdtiTextNum(etEndLc);
		double lc = startLc * 1.0 + endLc * 1.0 / 1000;
		return lc;
	}
	
	private int getEdtiTextNum(TextView textView) {
		String railNumStr = textView.getText().toString().trim();
		if (!TextUtils.isEmpty(railNumStr)) {
			int railNum = Integer.parseInt(railNumStr);
			if (railNum <= 0) {
				return -1;
			}
			return railNum;
		} else {
			return -2;
		}
	}
	
	private String mroot_image="";
	private CameraUtil cameraUtil;
	private String wonum;
	private ImageView ivRecord;

	protected void openCamera() {
		String MACCHA_PATH = CreatFileUtil.getFilePath(CreatFileUtil.getImage(this)) + File.separator;// 图片的路径
		ImageUtil.getInstance().createMkdir(MACCHA_PATH);
		mroot_image = MACCHA_PATH + ImageUtil.getPhotoFilename(wonum, new Date());
		cameraUtil = new CameraUtil();
		cameraUtil.startCameraEditor(this, mroot_image);
	}

	private AudioRecordHelper mRecordUtil = null;
	private String audioPath = "";// 音频路径
	private Button btnPlay;
	private Button btnCommit;
	private Button btnCancel;
	private String assetnum;
	private int linenum;
	private MeasureData measureData;
	private String qtDesc;
	private EditText etQtDesc;
	private String qtDescCustom;
	private EditText etFvalue;
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

}
