//package com.jtv.locationwork.util;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Date;
//import java.util.List;
//
//import com.alibaba.fastjson.JSONException;
//import com.jtv.base.activity.BaseAty;
//import com.jtv.base.util.FileUtil;
//import com.jtv.base.util.UToast;
//import com.jtv.hrb.locationwork.R;
//import com.jtv.locationwork.httputil.AsyncRunner;
//import com.jtv.locationwork.httputil.BaseRequestListener;
//import com.plutus.libraryui.dialog.LoadDataDialog;
//import com.plutus.util.CreatFileUtil;
//
//import android.content.ContentResolver;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.PixelFormat;
//import android.graphics.drawable.AnimationDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.Display;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.MeasureSpec;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.RelativeLayout.LayoutParams;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class PhotoTouchAty extends BaseAty implements OnClickListener {
//	
//
//	public static final int PHOTO_PICKED_WITH_DATA = 232;//从相册返回
//	public static int TAKE_PHOTO_REQUEST = 230;//拍照请求
//	public static final int TAKE_PHOTO_OK = 1;//拍照ok
//	public static final int TAKE_PHOTO_CANCEL = -1;//拍照返回
//	
//	
//	
//	public static final String IMAGE_UNSPECIFIED = "image/*";
//	public String words;
//	/**
//	 * 完整的PCM音频路径
//	 */
//	private String PCMMEDIA_PATH;
//	private String PHOTO_PATH;// 照片路径
//	private String TAG = "PhotoTouchAty";
//	private String filepath; // 图片地址
//	private int mType = 0;// 0:为默认添加图片跳转;1:为从上传页面新增调用拍照
//	/** 检查或者整改类型，根据这个类型跳转不同的上传页面 */
//	private String list_type;
//	/** 整改-等待处理页面 */
//	private String zgParams;
//	private int mHeaderHei = 0;
//	private int mFooterHei = 0;
//	private AnimationDrawable animationDrawable = null;
//
//	private View mTouchView;
//	private int startX, startY, endX, endY;
//	private int moveStartX, moveStartY, moveEndX, moveEndY;
//	private RelativeLayout mBackgroupView;
//	private ImageView iv_photo, iv_photo1, iv_record;// 照片，录音时动画
//	private ImageView btn_clear;// 保存清除本地音频
//	private Button btn_record;// 录音
//	private Button btn_playmedia;
//	private boolean isPlay = false;// 是否播放音频
//	private ImageView iv_back;
//	private TextView tv_title;
//	private Button btn_save;
//	/** 头部 */
//	private LinearLayout linHeader;
//	/** 尾部 */
//	private RelativeLayout rlFooter;
//	private LoadDataDialog loadDataDialog;
//	public EditText edit;
//	public Handler mHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			edit.setVisibility(View.VISIBLE);
//			edit.setText(words);
//		};
//	};
//
//	public void onResume() {
//		super.onResume();
//		MobclickAgent.onResume(this);
//	}
//
//	public void onPause() {
//		super.onPause();
//		MobclickAgent.onPause(this);
//	}
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFormat(PixelFormat.TRANSLUCENT);
//		setContentView(R.layout.jtv_module_basetouch_main);
//		loadDataDialog = new LoadDataDialog(PhotoTouchAty.this);
//		init();
//		mHeaderHei = ViewUtil.getViewHeight(linHeader);
//		mFooterHei = ViewUtil.getViewHeight(rlFooter);
//		edit = (EditText) findViewById(R.id.edit);
//		Log.e(TAG, "mHeaderHei:" + mHeaderHei + ";mFooterHei" + mFooterHei);
//		getIntentData();
//		listenterRecord();
//	}
//
//	/**
//	 * 初始化
//	 **/
//	private void init() {
//		btn_save=getHeaderOkBtn();
//		btn_save.setVisibility(View.VISIBLE);
//		iv_photo = (ImageView) findViewById(R.id.iv_photo);
//		iv_photo1 = (ImageView) findViewById(R.id.iv_photo1);
//		iv_record = (ImageView) findViewById(R.id.iv_record);
//		btn_clear = (ImageView) findViewById(R.id.btn_clear);
//		btn_record = (Button) findViewById(R.id.btn_record);
//		btn_playmedia = (Button) findViewById(R.id.btn_playmedia);
//		linHeader = (LinearLayout) findViewById(R.id.linHeader);
//		rlFooter = (RelativeLayout) findViewById(R.id.rlFooter);
//		mBackgroupView = (RelativeLayout) findViewById(R.id.backgroup_view);
//
//		//
//		btn_clear.setOnClickListener(this);
//		btn_record.setOnClickListener(this);
//		btn_playmedia.setOnClickListener(this);
//		btn_save.setOnClickListener(this);
//		iv_back.setOnClickListener(this);
//
//		animationDrawable = (AnimationDrawable) getResources()
//				.getDrawable(R.id.animation_record);
//		iv_record.setBackgroundDrawable(animationDrawable);
//		File photoDir =CreatFileUtil.getImage(this);
//		if (!photoDir.exists()) {
//			photoDir.mkdirs();
//		}
//		String photo_filename = ImageUtil.getPhotoFilename("",new Date());
//		PHOTO_PATH = photoDir.getAbsolutePath()+File.separator+ photo_filename;
//
//		// add
//		String PCM_NAME = photo_filename.replace(".png", "") + ".pcm";
//		PCMMEDIA_PATH = photoDir.getAbsolutePath() + PCM_NAME;
//
//	}
//
//	/**
//	 * 获取前个页面传递过来的数据
//	 * 
//	 * @author:zn
//	 * @version:2015-2-26
//	 */
//	private void getIntentData() {
//		Intent intent = getIntent();
//		if (intent.getIntExtra("operate",230) == 230)// 调用相机
//			openImageCaptureMenu();
//		else
//			openPhotoLibraryMenu();// 调用相册
//		if (intent.hasExtra("type")) {
//			mType = intent.getIntExtra("type", 0);
//		}
//		if (intent.hasExtra("list_type")) {
//			list_type = intent.getStringExtra("list_type");
//		}
//		if (intent.hasExtra("zgparams")) {
//			zgParams = intent.getStringExtra("zgparams");
//		}
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			clearLocalData();
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	public void onClick(View v) {
//		if (v.getId() == R.id.back) {
//			MediaUtil.getInstance().stopPlayRecord();
//			clearLocalData();
//			finish();
//		} else if (v.getId() ==R.id.btn_ok) {
//			saveResource();
//			MediaUtil.getInstance().stopPlayRecord();
//			// TODO
//			if (StringUtil.isEmpty(PHOTO_PATH)) {
////				UToast.makeShortTxt(PhotoTouchAty.this, ResUtil.getResourceId("sinfo_error_photo", "string"));
//				return;
//			}
//			Intent intent = null;
//			if (!StringUtil.isEmpty(list_type)) {
//				// intent = new Intent(PhotoTouchAty.this,
//				// ZGWaitCtrlInfoAty.class);
//				// intent.putExtra("zgparams", zgParams);
//			} else {
//				intent = new Intent();
//			}
//			intent.putExtra("imgPath", PHOTO_PATH);
//			intent.putExtra("pcmPath", PCMMEDIA_PATH);
//			intent.putExtra("words", edit.getEditableText().toString());
//			if (mType == 1) {
//				setResult(111, intent);
//			} else {
//				startActivity(intent);
//			}
//			finish();
//		} else if (v.getId() == 1) {
////			if (FileUtil.getInstance().deleteFile(PCMMEDIA_PATH)) {
////				btn_playmedia.setBackgroundResource(ResUtil.getResourceId("icon_btn_play", "drawable"));
////			}
////			btn_record.setVisibility(View.VISIBLE);
////			btn_clear.setVisibility(View.INVISIBLE);
////			btn_playmedia.setVisibility(View.INVISIBLE);
//		} else if (v.getId() ==R.id.btn_playmedia) {
//			if (!isPlay) {// 播放
//				btn_playmedia.setBackgroundResource(R.drawable.icon_btn_stop);
//				MediaUtil.getInstance().playRecord(PCMMEDIA_PATH);
//				isPlay = true;
//			} else {// 正在播放，停止播放
//				isPlay = false;
//				btn_playmedia.setBackgroundResource(R.drawable.icon_btn_play);
//				MediaUtil.getInstance().stopPlayRecord();
//			}
//		}
//	}
//
//	private void clearLocalData() {
//		// 清除图片文件
//	}
//
//	/**
//	 * 监听录音
//	 * 
//	 * @author:zn
//	 * @version:2015-2-26
//	 */
//	private void listenterRecord() {
//		btn_record.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:// 按下
//					MediaUtil.getInstance().startRecord(PCMMEDIA_PATH);
//					iv_record.setVisibility(View.VISIBLE);
//					animationDrawable.start();
//					break;
//				case MotionEvent.ACTION_UP:// 弹起
//					MediaUtil.getInstance().stopRecord(false);
//					iv_record.setVisibility(View.GONE);
//					if (FileUtil.getInstance().isFileExist(PCMMEDIA_PATH)) {
//						final File file = new File(PCMMEDIA_PATH);
//						try {
//							Long size = FileUtil.getFileSizes(file);
//							if (size < 10) {
//								UToast.makeShortTxt(PhotoTouchAty.this, "录音时间太短！");
//								return false;
//							} else {
//								// 语音转文字
//								loadDataDialog.open("语音转换中...");
//								AsyncRunner.HttpGet(new BaseRequestListener() {
//									@Override
//									public void onRequesting() throws AppError, JSONException {
//										try {
//											super.onRequesting();
//										} catch (org.json.JSONException e) {
//											e.printStackTrace();
//										}
//										try {
//											org.json.JSONObject obj = PatrolAPI.getPatrolAPI()
//													.getWords(file.getAbsolutePath());
//											words = obj.getString("words");
//											mHandler.sendEmptyMessage(1);
//											loadDataDialog.close();
//										} catch (org.json.JSONException e) {
//											e.printStackTrace();
//										}
//									}
//
//									@Override
//									public void onAppError(AppError e) {
//										Toast.makeText(PhotoTouchAty.this, "转换失败", 0).show();
//										words = "";
//										loadDataDialog.close();
//									}
//								});
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						//
//						btn_record.setVisibility(View.INVISIBLE);
//						btn_clear.setVisibility(View.VISIBLE);
//						btn_playmedia.setVisibility(View.VISIBLE);
//					} else {
//						UToast.makeShortTxt(PhotoTouchAty.this, "录音时间太短！");
//					}
//					// btn_record.setBackgroundResource(R.drawable.icon_btn_record);
//					break;
//				default:
//					break;
//				}
//				return false;
//			}
//		});
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == 0) {// 取消拍照
//			this.finish();
//		} else if (requestCode == PhotoPopWindow.TAKE_PHOTO_REQUEST) {// 设置文件保存路径这里放在跟目录下(拍照)
//			showCameraImage();// 显示并保存拍下来的图片
//		} else if (requestCode == PhotoPopWindow.PHOTO_PICKED_WITH_DATA) {// 从相册返回
//			Uri originalUri = data.getData();
//			ContentResolver resolver = getContentResolver();
//			Bitmap bm;
//			try {
//				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);// 显得到bitmap图片
//				BitmapUtil.saveImg(bm, PHOTO_PATH);
//				showCameraImage();// 显示并保存拍下来的图片
//			} catch (FileNotFoundException e) {
//				UToast.makeShortTxt(this, "选择图片出错");
//				this.finish();
//			} catch (IOException e) {
//				UToast.makeShortTxt(this, "选择图片出错");
//				this.finish();
//			} catch (Exception e) {
//				UToast.makeShortTxt(this, "选择图片出错");
//				this.finish();
//			}
//		} else if (resultCode == PhotoPopWindow.TAKE_PHOTO_REQUEST) {
//			showCameraImage();// 显示并保存拍下来的图片
//		} else if (resultCode == PhotoPopWindow.TAKE_PHOTO_CANCEL) {
//			this.finish();
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	/**
//	 * 打开相机进行照相
//	 * 
//	 * @author:zn
//	 * @version:2015-2-26
//	 * @return
//	 */
//	public void openImageCaptureMenu() {
//		FileUtil.getInstance().createDir(PdaLoginManager.getPhotoSavePath());
//		FileUtil.getInstance().createDir(PdaLoginManager.getRecordSavePath());
//
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(PHOTO_PATH)));
//		startActivityForResult(intent, PhotoPopWindow.TAKE_PHOTO_REQUEST);
//	}
//
//	public void openPhotoLibraryMenu() {
//		Intent localIntent = new Intent();
//		localIntent.setType("image/*");
//		localIntent.setAction("android.intent.action.GET_CONTENT");
//		this.startActivityForResult(localIntent, PhotoPopWindow.PHOTO_PICKED_WITH_DATA);
//	}
//
//	/**
//	 * 显示拍照的图片
//	 */
//	private void showCameraImage() {
//		Bitmap bitmap = null;
//		try {
//			Display display = getWindowManager().getDefaultDisplay(); // 显示屏尺寸
//			bitmap = BitmapUtil.decodeBitmap(this, PHOTO_PATH, display.getWidth(),
//					display.getHeight() - mHeaderHei - mFooterHei);
//			// bitmap = BitmapUtil.compressImage(this,PHOTO_PATH, 40);//
//			// 对图片进行压缩处理
//			// bitmap =
//			// FileUtil.getInstance().getBitMapByPath(PHOTO_PATH);//原图会溢出
//
//			iv_photo.setImageBitmap(bitmap);
//
//			iv_photo.setDrawingCacheEnabled(true);
//			iv_photo1.setDrawingCacheEnabled(true);// iv_photo1显示画框轨迹，实际上不存放图片
//			iv_photo.setDrawingCacheBackgroundColor(getResources().getColor(ResUtil.getResourceId("white", "color")));
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			bitmap = null;
//			System.gc();
//		}
//	}
//
//	/**
//	 * 保存资源文件,点击保存图片时进行图片保存
//	 * 
//	 * @author:zn
//	 * @version:2015-3-17
//	 */
//	private void saveResource() {
//		Bitmap bitmap = null;
//		try {
//			bitmap = iv_photo.getDrawingCache();
//			// 获取画的框，重新画到iv_photo上，然后保存iv_photo的bitmap
//			List<View> views = ViewUtil.getAllChildViews(this, mBackgroupView);
//			for (View view : views) {
//				if (view.getId() == ResUtil.getResourceId("touch_view", "id")) {
//					String temp = "" + view.getTag();
//					int left = Integer.parseInt(temp.split(",")[0]);
//					int top = Integer.parseInt(temp.split(",")[1]);
//
//					Paint p = getRedRectPaint();
//					Canvas canvas = new Canvas(bitmap);
//					float x = left - iv_photo.getX() + view.getX();
//					float y = top - iv_photo.getY() + view.getY();
//					canvas.drawRect(x, y, x + view.getWidth(), y + view.getHeight(), p);// 长方形
//					canvas.save(Canvas.ALL_SAVE_FLAG);
//					canvas.restore();
//				}
//			}
//			// mBackgroupView.setDrawingCacheEnabled(true);
//			// //
//			// mBackgroupView.setDrawingCacheBackgroundColor(getResources().getColor(R.color.white));
//			//
//			// bitmap = mBackgroupView.getDrawingCache();
//			BitmapUtil.saveImg(bitmap, PHOTO_PATH);
//
//			// 先将原先的图片压缩后在进行删除,重新保存压缩后的图片
//			BitmapUtil.compressImage(this, PHOTO_PATH, 90);// 对图片进行压缩处理
//			// BitmapUtil.getThumbUploadPath(PHOTO_PATH,
//			// 480,PHOTO_PATH);//此方法压缩后太模糊
//
//			// mBackgroupView.setDrawingCacheEnabled(false);
//			bitmap.recycle();
//		} catch (Exception e) {
//			UToast.makeShortTxt(this, "保存图片失败，请重试");
//		} finally {
//			bitmap = null;
//			System.gc();
//		}
//	}
//
//	/**
//	 * 获取红框画笔
//	 * 
//	 * @Description: TODO(用一句话描述该方法做什么)
//	 * @author mouw DateTime 2015年5月22日 下午5:42:08
//	 * @return
//	 */
//	private Paint getRedRectPaint() {
//		Paint p = new Paint();// 创建画笔
//		p.setColor(Color.RED);// 设置红色
//		p.setStyle(Paint.Style.STROKE);// 设置空心
//		p.setStrokeWidth(2.5f);// 线条宽度
//		return p;
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			startX = (int) event.getRawX();
//			startY = (int) event.getRawY();
//			moveStartX = (int) event.getRawX();
//			moveStartY = (int) event.getRawY();
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if (mTouchView != null) {
//				moveEndX = (int) event.getRawX();
//				moveEndY = (int) event.getRawY();
//				moveView(moveEndX - moveStartX, moveEndY - moveStartY);
//				moveStartX = (int) event.getRawX();
//				moveStartY = (int) event.getRawY();
//			} else {
//				iv_photo1.setImageBitmap(null);
//				Bitmap bitmap = iv_photo1.getDrawingCache();
//				if (bitmap == null)
//					break;
//
//				Paint p = getRedRectPaint();
//
//				// 实际y(相对应屏幕顶点的坐标)应该减去上方的状态栏和标题栏高度,下面两行不能提取出来公共的计算等式
//				int top = startY - ViewUtil.getStatusHeight(PhotoTouchAty.this) - mHeaderHei;
//				int bottom = (int) event.getRawY() - ViewUtil.getStatusHeight(PhotoTouchAty.this) - mHeaderHei;
//
//				Canvas canvas = new Canvas(bitmap);
//				canvas.drawRect(startX, top, (int) event.getRawX(), bottom, p);// 长方形
//				iv_photo1.setImageBitmap(bitmap);
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			endX = (int) event.getRawX();
//			endY = (int) event.getRawY();
//			if (mTouchView != null) {
//				if (Math.abs(startX - endX) < 10 && Math.abs(startY - endY) < 10) {
//					if ((mTouchView.findViewById(R.id.delete_view))
//							.getVisibility() == View.VISIBLE) {
//						(mTouchView.findViewById(R.id.delete_view))
//								.setVisibility(View.INVISIBLE);
//					} else {
//						(mTouchView.findViewById(R.id.delete_view))
//								.setVisibility(View.VISIBLE);
//					}
//				}
//				RelativeLayout.LayoutParams params = (LayoutParams) mTouchView.getLayoutParams();
//				params.leftMargin = mTouchView.getLeft();
//				params.topMargin = mTouchView.getTop();
//				mTouchView.setLayoutParams(params);
//				mTouchView = null;
//			} else {
//				iv_photo1.setImageBitmap(null);
//				if (Math.abs(startX - endX) < 20 || Math.abs(startY - endY) < 20)
//					break;
//				if (startX < endX && startY < endY) {
//					addView(startX, startY, endX, endY);
//				} else if (startX > endX && startY < endY) {
//					addView(endX, startY, startX, endY);
//				} else if (startX < endX && startY > endY) {
//					addView(startX, endY, endX, startY);
//				} else if (startX > endX && startY > endY) {
//					addView(endX, endY, startX, startY);
//				}
//			}
//			break;
//		default:
//			break;
//		}
//		return super.onTouchEvent(event);
//	}
//
//	private void addView(int left, int top, int right, int bottom) {
//		final View bgview = LayoutInflater.from(this)
//				.inflate(R.layout.jtv_module_basetouch_layout);
//		final TextView deleteView = (TextView) bgview.findViewById(ResUtil.getResourceId("delete_view", "id"));
//		deleteView.setVisibility(View.INVISIBLE);
//		deleteView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mBackgroupView.removeView(bgview);
//				mTouchView = null;
//			}
//		});
//		TouchView touchView = (TouchView) bgview.findViewById(ResUtil.getResourceId("touch_view", "id"));
//		touchView.setListener(new SetViewListener() {
//			@Override
//			public void getView(TouchView view) {
//				mTouchView = bgview;
//			}
//
//		});
//		deleteView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//
//		RelativeLayout.LayoutParams params = new LayoutParams(right - left,
//				bottom - top + deleteView.getMeasuredHeight());
//		params.leftMargin = left;
//		params.topMargin = top - ViewUtil.getStatusHeight(PhotoTouchAty.this) - mHeaderHei
//				- deleteView.getMeasuredHeight();
//		touchView.setTag(params.leftMargin + "," + params.topMargin);
//		mBackgroupView.addView(bgview, params);
//	};
//
//	private void moveView(int moveX, int moveY) {
//		int l = mTouchView.getLeft() + moveX;
//		int t = mTouchView.getTop() + moveY;
//		int r = mTouchView.getRight() + moveX;
//		int b = mTouchView.getBottom() + moveY;
//		mTouchView.layout(l, t, r, b);
//		TouchView touchView = (TouchView) mTouchView.findViewById(ResUtil.getResourceId("touch_view", "id"));
//		touchView.setTag(l + "," + t);
//	}
//
//	@Override
//	protected void onCreatInit(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		
//	}
//}
