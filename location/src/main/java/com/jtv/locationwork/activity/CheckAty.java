package com.jtv.locationwork.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.IInterface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jtv.base.activity.BaseAty;
import com.jtv.base.util.UToast;
import com.jtv.dbentity.dao.BaseDaoImpl;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.adapter.AssetsAdapter;
import com.jtv.locationwork.adapter.LineNiceAdapter;
import com.jtv.locationwork.adapter.QuestionAdapter;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.entity.Assets;
import com.jtv.locationwork.entity.CheckEntity;
import com.jtv.locationwork.entity.ItemWoListAttribute;
import com.jtv.locationwork.entity.ItemWonum;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.ParseJson;
import com.jtv.locationwork.httputil.RequestParmter;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.imp.QuestionTree;
import com.jtv.locationwork.listener.DelayOntouchListener;
import com.jtv.locationwork.tree.ParmterMutableTreeNode;
import com.jtv.locationwork.util.AnimationUtils;
import com.jtv.locationwork.util.AudioRecordHelper;
import com.jtv.locationwork.util.Base64UtilCst;
import com.jtv.locationwork.util.CameraUtil;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.DataConfig;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.ImageUtil;
import com.jtv.locationwork.util.MediaPlayHelper;
import com.jtv.locationwork.util.MediaUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;
import com.plutus.libraryui.dialog.LoadDataDialog;
import com.plutus.libraryui.spinner.BaseNiceSpinnerAdapter;
import com.plutus.libraryui.spinner.NiceSpinner;
import com.yixia.camera.demo.log.Logger;
/**
 * 
 * @author 房德安  2016-2-26 修改
 *			
 */
public class CheckAty extends BaseAty implements ObserverCallBack, OnTouchListener {

	private AnimationDrawable recordAnim;
	private TextView[] tv_type = new TextView[4];
	private int indexSelector = 0;
	private String mroot_image;
	private long phoneid = new Date().getTime();
	private String PCMMEDIA_PATH;

	private int REQUESTCAMERA = 12;

	private String longitude;// 经度
	private String latitude;// 纬度

	private String linename = "";// 线别
	private String linetype = "";// 行别

	private Button btn_editor;

	private ImageButton ibn_takephoto;

	private TextView tv_linename;

	private TextView tv_linetype;

	private TextView tv_linedistance;

	private EditText et_data;

	private NiceSpinner ns_questionSocre;// 问题大概
	private NiceSpinner ns_questionItem;// 问题具体

	private ImageButton btn_recorder;

	private ImageView btn_play;

	private String audioPath = "";// 音频路径

	private BaseNiceSpinnerAdapter questionItemAdapter = null;// 问题类型适配器

	private BaseNiceSpinnerAdapter questionScoreAdapter = null;// 问题范围的适配器

	private BaseNiceSpinnerAdapter mAdapter3 = null;

	private BaseNiceSpinnerAdapter mAdapter4 = null;

	private LineNiceAdapter lineNameAdapter = null;
	private LineNiceAdapter lineTypeAdapter = null;

	private ArrayList<ParmterMutableTreeNode> mArrQuestionSocre = new ArrayList<ParmterMutableTreeNode>();

	private ArrayList<ParmterMutableTreeNode> mArrItemQuestion = new ArrayList<ParmterMutableTreeNode>();

	private String wonum = "";

	// 问题的详情
	private String questionType;

	// 具体的问题
	private String troublename;
	private AnimationDrawable playSoundAnim;

	private NiceSpinner ns_LineName;// 线名
	private NiceSpinner ns_Linetype;// 线别
	private NiceSpinner ns_3;
	private NiceSpinner ns_4;

	private LinearLayout ll_cha;// 道岔的父布局

	private EditText et_measure;// 输入测量的数值
	private EditText et_number;// 输入铁号
	private CameraUtil cameraUtil;
	private ImageView iv_record;
	private ImageView iv_say;
	private LoadDataDialog loadDataDialog;
	CheckEntity entity = new CheckEntity();
	private String daocha;
	private String daochaBufen;
	private LinearLayout ll_dcname;
	private NiceSpinner ns_dc_name;
	private AssetsAdapter mDaoChaName;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_editor:

			if (!TextUtil.isEmpty(mroot_image)) {
				CameraUtil.startEditor(this, mroot_image);
			} else {
				UToast.makeShortTxt(this, getString(R.string.please_sel_photo));
			}

			break;
		case R.id.btn_select:
			Intent intent = new Intent(this, PreviewDCIMAty.class);
			startActivityForResult(intent, REQUESTCAMERA);
			break;
		case R.id.tv_line1:
			ll_cha.setVisibility(View.GONE);
			ll_dcname.setVisibility(View.GONE);
			split_line.setVisibility(View.GONE);
			ll_lc.setVisibility(View.VISIBLE);
			line_lc.setVisibility(View.VISIBLE);
			
			tv_type[indexSelector].setSelected(false);
			indexSelector = 0;
			tv_type[indexSelector].setSelected(true);
			break;
		case R.id.tv_line2:
			ll_cha.setVisibility(View.VISIBLE);
			ll_dcname.setVisibility(View.VISIBLE);
			split_line.setVisibility(View.VISIBLE);
			ll_lc.setVisibility(View.GONE);
			line_lc.setVisibility(View.GONE);
			
			tv_type[indexSelector].setSelected(false);
			indexSelector = 1;
			tv_type[indexSelector].setSelected(true);

			if (mDaoChaName == null) {
				TrackAPI.requestDaoChaAssets(GlobalApplication.siteid, GlobalApplication.getAreaIdOrShopId(), this);
			}

			break;
		case R.id.tv_line3:
			ll_cha.setVisibility(View.GONE);
			ll_dcname.setVisibility(View.GONE);
			split_line.setVisibility(View.GONE);
			ll_lc.setVisibility(View.VISIBLE);
			line_lc.setVisibility(View.VISIBLE);
			
			tv_type[indexSelector].setSelected(false);
			indexSelector = 2;
			tv_type[indexSelector].setSelected(true);
			break;
		case R.id.tv_line4:
			ll_dcname.setVisibility(View.GONE);
			ll_cha.setVisibility(View.GONE);
			split_line.setVisibility(View.GONE);
			ll_lc.setVisibility(View.VISIBLE);
			line_lc.setVisibility(View.VISIBLE);
			
			tv_type[indexSelector].setSelected(false);
			indexSelector = 3;
			tv_type[indexSelector].setSelected(true);
			break;
		case R.id.btn_ok:

			save();

			break;

		case R.id.ibn_takephoto:
			openCamera();
			break;
		case R.id.btn_play:
			if (playSoundAnim != null && playSoundAnim.isRunning()) {
				return;
			}
			playSoundAnim = AnimationUtils.playSoundAnim(btn_play);
			MediaPlayHelper mediaPlayHelper = MediaPlayHelper.getMediaPlayHelper();
			mediaPlayHelper.setPlayCompleListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					playSoundAnim.stop();
					btn_play.setBackgroundResource(R.anim.anim_playsound);
				}
			});
			mediaPlayHelper.playMedia(this, MediaPlayHelper.SDFLAG, 0, audioPath);
			break;
		case R.id.btn_getdistance:
			linename = ns_LineName.getSelectedTitle();
			linetype = ns_Linetype.getSelectedTitle();
			TrackAPI.getDistanceGps(this, this, longitude, latitude, linename, linetype);
			break;

		}
	}

	private void save() {
		linename = ns_LineName.getSelectedTitle();
		linetype = ns_Linetype.getSelectedTitle();

		RequestParmter requestParams = new RequestParmter();

		if (!TextUtil.isEmpty(audioPath)) {
			File record = new File(audioPath);
			if (record.exists()) {
				requestParams.addBodyParmter("say", record);
			}
		}

		try {
			int index = ns_questionSocre.getSelectedIndex();
			ParmterMutableTreeNode mParmterqutestion = (ParmterMutableTreeNode) questionScoreAdapter.getItem(index);
			questionType = (String) mParmterqutestion.getUserObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (ll_cha.getVisibility() == View.VISIBLE && mAdapter4 != null) {
			daocha = (String) mAdapter4.getItem(ns_4.getSelectedIndex());
			requestParams.addBodyParmter("trackpart", Base64UtilCst.encodeUrl(daocha));
		}

		if (ll_dcname.getVisibility() == View.VISIBLE && mDaoChaName != null) {
			Assets daochaName = (Assets) mDaoChaName.getItem(ns_dc_name.getSelectedIndex());
			String assetnum = daochaName.getAssetnum();
			requestParams.addBodyParmter("assetnum", Base64UtilCst.encodeUrl(assetnum));
		}

//		String item = (String) mAdapter3.getItem(ns_3.getSelectedIndex());
//		requestParams.addBodyParmter("RULER", item);
		
		//fda修改 2016.2.26 
		String item=et_3.getText().toString().trim();
		if(TextUtil.isEmpty(item)){
			requestParams.addBodyParmter("RULER","");
		}else{
			requestParams.addBodyParmter("RULER",item);
		}

//		try {
//			int index2 = ns_questionItem.getSelectedIndex();
//			ParmterMutableTreeNode mParmterqutestionThrouble = (ParmterMutableTreeNode) questionItemAdapter
//					.getItem(index2);
//			troublename = (String) mParmterqutestionThrouble.getUserObject();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		troublename=tv_question.getText().toString().trim();
		if(troublename.isEmpty()){
			troublename="";
		}
		
		if (!TextUtil.isEmpty(mroot_image)) {
			File picFile = new File(mroot_image);

			if (picFile.exists()) {
				requestParams.addBodyParmter("pic", picFile);
			}
		}

		requestParams.addBodyParmter("wonum", wonum);
		requestParams.addBodyParmter("tracknum", Base64UtilCst.encodeUrl(et_number.getText().toString()));
		requestParams.addBodyParmter("phoneid", phoneid + "");
		requestParams.addBodyParmter("fvalue", et_measure.getText().toString());

		// requestParams.addBodyParmter("type",
		// tv_type[indexSelector].getText().toString());
		requestParams.addBodyParmter("problem", Base64UtilCst.encodeUrl(troublename));
		
		String kmStr=tv_linedistance.getText().toString();
		String mStr=tv_linemile.getText().toString();
		Double kmNumber=0D;
		try {
			kmNumber=Double.parseDouble(kmStr);
			Double mNumber=Double.parseDouble(mStr);
			mNumber=mNumber/1000;
			kmNumber=mNumber+kmNumber;
		} catch (NumberFormatException e) {
			Logger.e("里程输入格式有误，无法转为数值");
			kmNumber=0D;
			throw new RuntimeException(e);
		}
		
		requestParams.addBodyParmter("km", Base64UtilCst.encodeUrl(kmNumber.toString()));
		requestParams.addBodyParmter("linetype", Base64UtilCst.encodeUrl(linetype));
		requestParams.addBodyParmter("linename", Base64UtilCst.encodeUrl(linename));
		String descript = et_data.getText().toString();
		requestParams.addBodyParmter("description", Base64UtilCst.encodeUrl(descript));
		requestParams.addBodyParmter("personid", GlobalApplication.mBase64Lead);
		requestParams.addBodyParmter("attid", GlobalApplication.attid);
		requestParams.addBodyParmter("siteid", GlobalApplication.siteid);
		requestParams.addBodyParmter("orgid", GlobalApplication.orgid);

		TrackAPI.saveLineQuestion(this, this, requestParams);

		UToast.makeShortTxt(this, getString(R.string.tos_uploading));

		entity.setLineSelector(indexSelector + "");
		entity.setLine(linename);
		entity.setType(linetype);
		entity.setRule(item);
		entity.setCha(daocha);
		entity.setTie(et_number.getText().toString());
		entity.setMeasure(et_measure.getText().toString());
		entity.setKm(tv_linedistance.getText().toString());
		entity.setQuestion(questionType);
		entity.setQuestiontype(troublename);
		entity.setDescription(descript);
		entity.setPath(mroot_image);
		entity.setTime(DateUtil.getCurrTime());
		entity.setAudiopath(audioPath);
		entity.setWonum(wonum);
		if (entity.getCheck_id() > 0) {
			DBFactory.getCheckQuestionDao(this).update(entity);
		} else {
			long check_id = DBFactory.getCheckQuestionDao(this).insert(entity);
			entity.setCheck_id((int) check_id);
		}
	}

	@Override
	protected void onCreatInit(Bundle arg0) {
		setContentView(R.layout.location_jtv_check_lay);
		setHeaderTitleText(getString(R.string.dis_title_check));
		setBackOnClickFinish();
		getIntentParmter();
		find();
		value();
	}

	private void value() {
		loadDataDialog = new LoadDataDialog(this);
		PCMMEDIA_PATH = CreatFileUtil.getFilePath(CreatFileUtil.getAudioFilePath(this)) + File.separator + "temp.pcm";
		QuestionTree instance = QuestionTree.getInstance();
		ParmterMutableTreeNode creatTree = instance.creatTree(null);

		mArrQuestionSocre = (ArrayList<ParmterMutableTreeNode>) creatTree.getChilds();

		questionScoreAdapter = new QuestionAdapter(this, mArrQuestionSocre);
		ns_questionSocre.setAdapterInternal(questionScoreAdapter);

		ns_questionSocre.addOnItemClickListener(new OnItemClickListener() {
			String question = null;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ParmterMutableTreeNode mParent = (ParmterMutableTreeNode) arg0.getItemAtPosition(arg2);

				List<ParmterMutableTreeNode> childs = mParent.getChilds();

				mArrItemQuestion = (ArrayList<ParmterMutableTreeNode>) childs;

				questionItemAdapter = new QuestionAdapter(CheckAty.this, mArrItemQuestion);

				ns_questionItem.setAdapterInternal(questionItemAdapter);

				if (question == null) {// 默认选中
					question = entity.getQuestiontype();
					if (TextUtil.isEmpty(question)) {
					} else {
						int selectedIndex = ns_questionItem.getSelectedIndex(question);
						if (selectedIndex >= 0) {
							ns_questionItem.setSelectedIndex(selectedIndex);
						}
					}
				}

			}
		});

		// Arrays.sort(DataConfig.LIST_LINE_NAME);
		// Arrays.sort(DataConfig.LIST_LINE_TYPE);

		lineNameAdapter = new LineNiceAdapter(this, DataConfig.LIST_LINE_NAME);
		lineTypeAdapter = new LineNiceAdapter(this, DataConfig.LIST_LINE_TYPE);

		ns_LineName.setAdapterInternal(lineNameAdapter);
		ns_Linetype.setAdapterInternal(lineTypeAdapter);

		mAdapter3 = new LineNiceAdapter(this, new String[] { "1", "2", "3", "4" });
		ns_3.setAdapterInternal(mAdapter3);

		mAdapter4 = new LineNiceAdapter(this, new DataConfig().LIST_DAOCHA);
		ns_4.setAdapterInternal(mAdapter4);

		recordAnim = AnimationUtils.recordAnim(iv_say);

		// 设置默认选中文本
		linetype = entity.getType();
		linename = entity.getLine();

		// 设置默认选中的文本
		if (!TextUtil.isEmpty(linename) && !TextUtil.isEmpty(linetype)) {

			int selectedIndex = ns_LineName.getSelectedIndex(linename);
			if (selectedIndex >= 0) {
				ns_LineName.setSelectedIndex(selectedIndex);
			}
			selectedIndex = ns_Linetype.getSelectedIndex(linetype);
			if (selectedIndex >= 0) {
				ns_Linetype.setSelectedIndex(selectedIndex);
			}
		}

		String question = entity.getQuestion();
		if (TextUtil.isEmpty(question)) {
			ns_questionSocre.perfromOnItemClick(0);
		} else {
			int selectedIndex = ns_questionSocre.getSelectedIndex(question);
			if (selectedIndex >= 0) {
				ns_questionSocre.setSelectedIndex(selectedIndex);
				ns_questionSocre.perfromOnItemClick(selectedIndex);
			}
		}

		question = entity.getRule();
		if (TextUtil.isEmpty(question)) {
		} else {
			int selectedIndex = ns_3.getSelectedIndex(question);
			if (selectedIndex >= 0) {
				ns_3.setSelectedIndex(selectedIndex);
			}
		}

		daochaBufen = entity.getCha();
		if (TextUtil.isEmpty(daochaBufen)) {
		} else {
			int selectedIndex = ns_4.getSelectedIndex(daochaBufen);
			if (selectedIndex >= 0) {
				ns_4.setSelectedIndex(selectedIndex);
			}
		}

		String km = entity.getKm();

		String tie = entity.getTie();
		String measure = entity.getMeasure();
		String wonum = entity.getWonum();

		if (!TextUtil.isEmpty(wonum)) {
			this.wonum = wonum;
		}

		String description = entity.getDescription();
		setText(et_number, tie);
		setText(et_measure, measure);
		setText(tv_linedistance, km);
		setText(et_data, description);

		String path = entity.getPath();

		if (!TextUtil.isEmpty(path)) {
			File file = new File(path);
			if (file != null && file.exists() && file.isFile()) {
				mroot_image = path;

			}
		}

		if (TextUtil.isEmpty(km)) {
			TrackAPI.getDistanceGps(this, this, longitude, latitude, linename, linetype);
		}
		btn_editor.setOnClickListener(this);
		String lineSelector = entity.getLineSelector();

		if (!TextUtil.isEmpty(lineSelector)) {
			int parseInt = Integer.parseInt(lineSelector);
			if (parseInt > -1 && parseInt < tv_type.length) {
				onClick(tv_type[parseInt]);
			}

		}
		TrackAPI.requestDaoChaAssets(GlobalApplication.siteid, GlobalApplication.getAreaIdOrShopId(), this);
	}

	private void setText(TextView tv, String text) {
		tv.setText(text);
	}

	private void find() {
		tv_type[0] = (TextView) findViewById(R.id.tv_line1);
		tv_type[1] = (TextView) findViewById(R.id.tv_line2);
		tv_type[2] = (TextView) findViewById(R.id.tv_line3);
		tv_type[3] = (TextView) findViewById(R.id.tv_line4);

		tv_type[0].setOnClickListener(this);
		tv_type[1].setOnClickListener(this);
		tv_type[2].setOnClickListener(this);
		tv_type[3].setOnClickListener(this);

		tv_type[indexSelector].setSelected(true);

		btn_editor = (Button) findViewById(R.id.btn_editor);

		ll_dcname = (LinearLayout) findViewById(R.id.ll_dcname);
		ll_cha = (LinearLayout) findViewById(R.id.ll_cha);
		ll_lc = (LinearLayout) findViewById(R.id.ll_lc);
		
		

		ibn_takephoto = (ImageButton) findViewById(R.id.ibn_takephoto);
		ibn_takephoto.setOnClickListener(this);

		tv_linename = (TextView) findViewById(R.id.tv_linename);
		tv_linetype = (TextView) findViewById(R.id.tv_linetype);
		tv_linedistance = (TextView) findViewById(R.id.tv_linedistance);
		tv_linemile = (TextView) findViewById(R.id.tv_linemile);
		split_line = findViewById(R.id.split_line);
		line_lc = findViewById(R.id.line_lc);
		
		et_data = (EditText) findViewById(R.id.et_text);

		ns_questionSocre = (NiceSpinner) findViewById(R.id.ns_questiontype);
		ns_questionItem = (NiceSpinner) findViewById(R.id.ns_question);
		tv_question = (TextView) findViewById(R.id.tv_question);
		tv_question.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(CheckAty.this, ClassSelectActivity.class),100);
			}
		});
		
		ns_dc_name = (NiceSpinner) findViewById(R.id.ns_dc_name);

		btn_recorder = (ImageButton) findViewById(R.id.img_recorder);
		btn_recorder.setOnTouchListener(new DelayOntouchListener(this, this));
		btn_play = (ImageView) findViewById(R.id.btn_play);
		iv_record = (ImageView) findViewById(R.id.iv_record);
		iv_say = (ImageView) findViewById(R.id.iv_say);
		iv_record.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					MediaUtil.getInstance().startRecord(PCMMEDIA_PATH);
					iv_say.setVisibility(View.VISIBLE);
					recordAnim.start();
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					MediaUtil.getInstance().stopRecord(false);
					iv_say.setVisibility(View.GONE);
					recordAnim.stop();
					if (new File(PCMMEDIA_PATH).exists()) {
						final File file = new File(PCMMEDIA_PATH);
						try {
							Long size = file.length();
							if (size < 10) {
								UToast.makeShortTxt(CheckAty.this, "录音时间太短！");
								return false;
							} else {
								// 语音转文字
								loadDataDialog.open();
								TrackAPI.getWords(file, CheckAty.this, MethodApi.HTTP_GET_SOUNDTEXT);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						UToast.makeShortTxt(CheckAty.this, "录音时间太短！");
					}
					break;
				}
				return false;
			}
		});
		btn_play.setBackgroundResource(R.drawable.ic_play1);

		getHeaderOkBtn().setVisibility(View.VISIBLE);
		getHeaderOkBtn().setText(R.string.photo_submit);
		getHeaderOkBtn().setOnClickListener(this);

		findViewById(R.id.btn_getdistance).setOnClickListener(this);
		findViewById(R.id.btn_select).setOnClickListener(this);

		ns_LineName = (NiceSpinner) findViewById(R.id.ns_linename);
		ns_Linetype = (NiceSpinner) findViewById(R.id.ns_linetype);

		ns_3 = (NiceSpinner) findViewById(R.id.ns_3);
		ns_4 = (NiceSpinner) findViewById(R.id.ns_4);

		et_3 = (EditText) findViewById(R.id.et_3);
		
		
		et_measure = (EditText) findViewById(R.id.et_measure);
		et_number = (EditText) findViewById(R.id.et_number);

	}

	private void getIntentParmter() {
		try {

			if (getIntent().hasExtra("id")) {
				int id = getIntent().getIntExtra("id", -1);
				if (id != -1) {
					BaseDaoImpl<CheckEntity> checkQuestionDao = DBFactory.getCheckQuestionDao(this);
					entity = checkQuestionDao.get(id + "");
				}
			}

			if (getIntent().hasExtra(Constants.INTENT_KEY_WONUM)) {
				ItemWonum itemWonum = getIntent().getParcelableExtra(Constants.INTENT_KEY_WONUM);

				ItemWoListAttribute attribute = itemWonum.getAttribute("linetype");
				linetype = attribute.getDisValue();

				if (linetype.length() >= 2) {
					linetype = linetype.substring(0, 1);
				}

				ItemWoListAttribute attribute2 = itemWonum.getAttribute("linename");
				linename = attribute2.getDisValue();

				ItemWoListAttribute wonumAttr = itemWonum.getAttribute("wonum");
				wonum = wonumAttr.getDisValue();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		File dir = getDir("audio", MODE_PRIVATE);
		String path = dir.getAbsolutePath() + File.separator + System.currentTimeMillis()+"question.mp3";
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		audioPath = path;

		double[] gps = SpUtiles.BaseInfo.getGps();
		longitude = gps[0] + "";
		latitude = gps[1] + "";

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode){
			if(100==requestCode){
				String detailItem=data.getStringExtra("detailItem");
				tv_question.setText(detailItem);
			}
		}else if(RESULT_CANCELED==resultCode){
			
		}else if (PreviewDCIMAty.RESULT_CODE == resultCode) {
			String mroot_image = data.getStringExtra(PreviewDCIMAty.RESULT_ADDRESS);

			if (!TextUtil.isEmpty(mroot_image)) {
				this.mroot_image = mroot_image;
			}

		}  else if (cameraUtil != null) {
			cameraUtil.onActivityResult(requestCode, resultCode, data);
		}

	}

	protected void openCamera() {
		String MACCHA_PATH = CreatFileUtil.getFilePath(CreatFileUtil.getImage(this)) + File.separator;// 图片的路径
		ImageUtil.getInstance().createMkdir(MACCHA_PATH);
		mroot_image = MACCHA_PATH + ImageUtil.getPhotoFilename(wonum, new Date());
		cameraUtil = new CameraUtil();
		cameraUtil.startCameraEditor(this, mroot_image);
	}

	@Override
	public void back(String data, int method, Object obj) {
		loadDataDialog.close();
		switch (method) {
		case MethodApi.HTTP_GET_DISTANCE:// 获取里程
			if (TextUtil.isEmpty(data)) {
				data = "";
				UToast.makeShortTxt(this, getString(R.string.tos_no_distance));
				return;
			}
			if (data.contains(".")) {
				int indexOf = data.indexOf(".");
				if (indexOf > 1 && indexOf < data.length() - 6) {
					data = data.substring(0, indexOf + 4);
				}
			}

			tv_linedistance.setText(data);
			break;
		case MethodApi.HTTP_ASSETS:// 获取道岔名字
			List<Assets> parseArray = null;

			try {
				parseArray = JSON.parseArray(data, Assets.class);
			} catch (Exception e) {
			}

			if (parseArray == null || parseArray.size() < 1) {
				return;
			}

			// Assets assets = new Assets();
			// assets.setShortname("获取更多道岔部分");
			// parseArray.add(0, assets);
			mDaoChaName = new AssetsAdapter(this, parseArray);
			ns_dc_name.setAdapterInternal(mDaoChaName);

			break;
		case MethodApi.HTTP_UPLOAD_CHECK:

			boolean parseJsonArraySuccess = ParseJson.parseStatusSuccessful(data);

			if (parseJsonArraySuccess) {
				UToast.makeShortTxt(this, getString(R.string.tos_upload_finish));
				entity.setStatu(Constants.UPLOAD_FINISH);
				DBFactory.getCheckQuestionDao(this).update(entity);
			} else {
				UToast.makeShortTxt(this, getString(R.string.tos_upload_failed));
				entity.setStatu(Constants.READY_UPLOAD);
				DBFactory.getCheckQuestionDao(this).update(entity);
			}

			break;
		case MethodApi.HTTP_GET_SOUNDTEXT:
			JSONObject obj2 = JSON.parseObject(data);
			if (obj2 == null) {
				return;
			}
			String words = obj2.getString("words");
			if (TextUtil.isEmpty(words)) {
				UToast.makeShortTxt(this, getString(R.string.dis_word_failed));
			} else {
				et_data.setText(words);
			}

			break;
		}

	}

	public void badBack(String error, int method, Object obj) {
		loadDataDialog.close();
		switch (method) {
		case MethodApi.HTTP_UPLOAD_CHECK:
			UToast.makeShortTxt(this, getString(R.string.tos_error_upload));
			entity.setStatu(Constants.READY_UPLOAD);
			DBFactory.getCheckQuestionDao(this).update(entity);
			break;

		case MethodApi.HTTP_GET_SOUNDTEXT:
			UToast.makeShortTxt(this, getString(R.string.dis_word_failed));
			break;
		}
	};

	AudioRecordHelper mRecordUtil = null;
	private View split_line;
	private TextView tv_question;
	private EditText et_3;
	private TextView tv_linemile;
	private LinearLayout ll_lc;
	private View line_lc;

	private void start() {

		btn_recorder.setPressed(true);

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
		btn_recorder.setPressed(false);

		if (mRecordUtil != null) {
			mRecordUtil.stop();
			mRecordUtil.release();
			mRecordUtil = null;
		}
		btn_play.setOnClickListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (v.getId() == R.id.img_recorder) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				start();
				break;
			case MotionEvent.ACTION_UP:
				stop();
				break;
			}

			return true;
		}

		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mRecordUtil != null) {
			mRecordUtil.stop();
			mRecordUtil.release();
		}
	}

}
