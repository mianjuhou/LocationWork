package com.jtv.locationwork.activity;

import java.io.File;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jtv.base.activity.BaseFragmentActivity;
import com.jtv.base.ui.EditorPhotoView;
import com.jtv.base.ui.EditorPhotoView.MODEL;
import com.jtv.base.ui.EditorPhotoView.ModelViewListener;
import com.jtv.base.ui.PointDrawView;
import com.jtv.base.ui.colordialog.ColorPickerDialog;
import com.jtv.base.ui.colordialog.ColorPickerSwatch.OnColorSelectedListener;
import com.jtv.base.util.UToast;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.adapter.LineNiceAdapter;
import com.jtv.locationwork.httputil.MethodApi;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.AnimationUtils;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.DataConfig;
import com.jtv.locationwork.util.MediaUtil;
import com.jtv.locationwork.util.ScreenUtil;
import com.jtv.locationwork.util.TextUtil;
import com.plutus.libraryui.dialog.LoadDataDialog;
import com.plutus.libraryui.spinner.NiceSpinner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PainterAty extends BaseFragmentActivity
		implements OnClickListener, OnItemClickListener, ModelViewListener, ObserverCallBack {

	private NiceSpinner ns_module;
	private LineNiceAdapter adapter;
	private EditorPhotoView editor;
	private String path;
	private Button btn_record;
	private LoadDataDialog loadDataDialog = null;
	Paint paint = new Paint();
	private TextView tv_color;
	private ColorPickerDialog colorPickerDialog;
	private ImageView iv_record;
	private AnimationDrawable recordAnim;
	private Bitmap getimage;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:

			boolean saveImage = editor.saveImage(path);
			if (saveImage) {
				UToast.makeShortTxt(this, getString(R.string.dis_success));
				finish();
			} else {
				UToast.makeShortTxt(this, getString(R.string.dis_failed));
			}

			break;

		case R.id.tv_color:
			colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCreatInit(savedInstanceState);
	}

	protected void onCreatInit(Bundle savedInstanceState) {
		setContentView(R.layout.location_jtv_painter);
		setBackOnClickFinish();
		setHeaderTitleText(getString(R.string.title_editor));
		getHeaderOkBtn().setOnClickListener(this);
		getHeaderOkBtn().setVisibility(View.VISIBLE);
		getHeaderOkBtn().setText(getString(R.string.set_save));

		getIntentParmter();
		getimage = BitmapFactory.decodeFile(path);// 测试数据
		// getimage = BitmapUtils.compressImage(getimage);
		editor = (EditorPhotoView) findViewById(R.id.et);
		editor.setModule(MODEL.TEXT);
		editor.setImageBitmap(getimage);
		editor.setModelViewListener(this);

		findId();
		loadDataDialog = new LoadDataDialog(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (getimage != null) {
			getimage.recycle();
		}
	}

	private void getIntentParmter() {
		path = getIntent().getStringExtra("path");
		String name = new File(path).getName();
		name = name.substring(0, name.length() - 3);
		File audioFilePath = CreatFileUtil.getAudioFilePath(this);
		PCMMEDIA_PATH = audioFilePath.getAbsolutePath() + File.separator + name + "pcm";
	}

	private void findId() {
		ns_module = (NiceSpinner) findViewById(R.id.ns_module);
		tv_color = (TextView) findViewById(R.id.tv_color);
		iv_record = (ImageView) findViewById(R.id.iv_recode);
		recordAnim = AnimationUtils.recordAnim(iv_record);

		tv_color.setOnClickListener(this);
		adapter = new LineNiceAdapter(this, DataConfig.LIST_MODLE);
		ns_module.setAdapterInternal(adapter);
		ns_module.addOnItemClickListener(this);

		btn_record = (Button) findViewById(R.id.btn_record);

		listenterRecord();

		colorPickerDialog = new ColorPickerDialog();
		colorPickerDialog.initialize(R.string.dialog_color_title, new int[] { Color.CYAN, Color.LTGRAY, Color.BLACK,
				Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.GRAY, Color.YELLOW }, Color.YELLOW, 3, 2);
		colorPickerDialog.setOnColorSelectedListener(new OnColorSelectedListener() {

			@Override
			public void onColorSelected(int color) {
				tv_color.setBackgroundColor(color);
				editor.setColor(color);
			}
		});

	}

	private String PCMMEDIA_PATH;
	// private AudioRecordHelper mRecordUtil;
	private String words;
	private MODEL mode = MODEL.DEFAULT;

	private void start() {
		MediaUtil.getInstance().startRecord(PCMMEDIA_PATH);
	}

	private void stop() {
		MediaUtil.getInstance().stopRecord(false);
	}

	private void listenterRecord() {
		btn_record.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下
					start();
					btn_record.setPressed(true);
					iv_record.setVisibility(View.VISIBLE);
					recordAnim.start();
					break;
				case MotionEvent.ACTION_CANCEL:
					btn_record.setPressed(false);
					MediaUtil.getInstance().stopRecord(true);
					iv_record.setVisibility(View.GONE);
					recordAnim.stop();
					break;
				case MotionEvent.ACTION_UP:// 弹起
					stop();
					iv_record.setVisibility(View.GONE);
					recordAnim.stop();
					btn_record.setPressed(false);
					if (new File(PCMMEDIA_PATH).exists()) {
						final File file = new File(PCMMEDIA_PATH);
						try {
							Long size = file.length();
							if (size < 10) {
								UToast.makeShortTxt(PainterAty.this, "录音时间太短！");
								return false;
							} else {
								// 语音转文字
								loadDataDialog.open();
								TrackAPI.getWords(file, PainterAty.this, MethodApi.HTTP_GET_SOUNDTEXT);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						UToast.makeShortTxt(PainterAty.this, "录音时间太短！");
					}
					break;
				}
				return false;
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String mDepart = (String) parent.getItemAtPosition(position);
		mode = MODEL.TEXT;
		if (mDepart.equals(DataConfig.LIST_MODLE[0])) {
		} else if (mDepart.equals(DataConfig.LIST_MODLE[1])) {
			mode = MODEL.PAINTER;
		} else if (mDepart.equals(DataConfig.LIST_MODLE[2])) {
			mode = MODEL.EDITTEXT;
		}
		editor.setModule(mode);
	}

	@Override
	public View getView(MODEL model, Object object, int w, int h) {
		switch (model) {
		case PAINTER:
			PointDrawView pointDrawView = new PointDrawView(this, w, h);
			pointDrawView.setColor(editor.getColor());
			return pointDrawView;

		case EDITTEXT:
			EditText edit = new EditText(this);
			edit.setTextColor(editor.getColor());
			return edit;
		case TEXT:
			TextView textView = new TextView(this);
			if (!TextUtil.isEmpty(words)) {
				textView.setText(words);
			}

			textView.setTextColor(editor.getColor());
			textView.setTextColor(editor.getColor());
			return textView;
		}
		return null;
	}

	@Override
	public void setLayouot(MODEL model, View view) {
		switch (model) {
		case PAINTER:
			int dip2px = ScreenUtil.dip2px(this, 20);
			RelativeLayout.LayoutParams layout = (android.widget.RelativeLayout.LayoutParams) view.getLayoutParams();
			layout.setMargins(0, dip2px, 0, 0);
			layout.width = android.widget.RelativeLayout.LayoutParams.MATCH_PARENT;
			view.setLayoutParams(layout);
			break;
		case EDITTEXT:
			layout = (android.widget.RelativeLayout.LayoutParams) view.getLayoutParams();
			dip2px = ScreenUtil.dip2px(this, 10);
			layout.setMargins(0, dip2px, 0, 0);
			layout.width = android.widget.RelativeLayout.LayoutParams.MATCH_PARENT;
			view.setLayoutParams(layout);
			break;
		}
	}

	private int left = 10, top = 50, right = 50, bottom = 150;

	@Override
	public void back(String data, int method, Object obj) {
		loadDataDialog.close();
		switch (method) {
		case MethodApi.HTTP_GET_SOUNDTEXT:
			JSONObject obj2 = JSON.parseObject(data);
			if (obj2 == null) {
				return;
			}
			words = obj2.getString("words");
			if (TextUtil.isEmpty(words)) {
				UToast.makeShortTxt(this, getString(R.string.dis_word_failed));
			}

			TextView focusText = editor.getFocusText();
			if (focusText != null && focusText.getVisibility() == View.VISIBLE) {
				focusText.setText(words);
				words = "";
				return;
			}

			if (!TextUtil.isEmpty(words)) {
				Paint paint = new Paint();
				float measureText = paint.measureText(words);
				measureText = measureText * 2 + 20;
				positionChange();

				right = (int) (left + right + measureText);

				editor.setModule(MODEL.TEXT);
				editor.addView(left, top, (int) right, (words.length() > 5) ? bottom + 30 : bottom);
				editor.setModule(mode);
				right = 50;
			}

			words = "";
			break;
		}
	}

	@Override
	public void badBack(String error, int method, Object obj) {
		loadDataDialog.close();
		switch (method) {
		case MethodApi.HTTP_GET_SOUNDTEXT:
			UToast.makeShortTxt(this, getString(R.string.dis_word_failed));
			break;
		}
	}

	private void positionChange() {
		left += 15;
		top += 15;
		bottom += 15;

		if (top > 150) {
			top = 50;
			bottom = 150;
		}
		if (left > 100) {
			left = 10;
		}
	}

}
