package com.jtv.video.recorder.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import com.jtv.video.recorder.R;
import com.jtv.video.recorder.util.FileUtil;
import com.jtv.video.recorder.view.ProgressView;
import com.yixia.camera.demo.log.Logger;
import com.yixia.videoeditor.adapter.UtilityAdapter;
import com.yixia.weibo.sdk.MediaRecorderBase;
import com.yixia.weibo.sdk.MediaRecorderNative;
import com.yixia.weibo.sdk.MediaRecorderSystem;
import com.yixia.weibo.sdk.VCamera;
import com.yixia.weibo.sdk.model.MediaObject;
import com.yixia.weibo.sdk.model.MediaObject$MediaPart;
import com.yixia.weibo.sdk.util.ConvertToUtils;
import com.yixia.weibo.sdk.util.DeviceUtils;
import com.yixia.weibo.sdk.util.FileUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecordVideoAty extends BaseRecordVideoAty implements com.yixia.weibo.sdk.MediaRecorderBase$OnErrorListener,
		OnClickListener, com.yixia.weibo.sdk.MediaRecorderBase$OnPreparedListener,
		com.yixia.weibo.sdk.MediaRecorderBase$OnEncodeListener {

	/** 刷新进度条 */
	private static final int HANDLE_INVALIDATE_PROGRESS = 0;
	/** 延迟拍摄停止 */
	private static final int HANDLE_STOP_RECORD = 1;
	/** 对焦 */
	private static final int HANDLE_HIDE_RECORD_FOCUS = 2;

	/** 下一步 */
	private ImageView mTitleNext;
	/** 对焦图标-带动画效果 */
	private ImageView mFocusImage;
	/** 前后摄像头切换 */
	private CheckBox mCameraSwitch;
	/** 回删按钮、延时按钮、滤镜按钮 */
	private CheckedTextView mRecordDelete;
	/** 闪光灯 */
	private CheckBox mRecordLed;
	/** 导入视频 */
	// private ImageView mImportVideo;
	/** 摄像头数据显示画布 */
	private SurfaceView mSurfaceView;
	/** 录制进度 */
	private ProgressView mProgressView;
	/** 对焦动画 */
	private Animation mFocusAnimation;

	/** SDK视频录制对象 */
	private MediaRecorderBase mMediaRecorder;
	/** 视频信息 */
	private MediaObject mMediaObject;

	/** 需要重新编译（拍摄新的或者回删） */
	private boolean mRebuild;
	/** on */
	private boolean mCreated;
	/** 是否是点击状态 */
	private volatile boolean mPressedStatus;
	/** 是否已经释放 */
	private volatile boolean mReleased;
	/** 对焦图片宽度 */
	private int mFocusWidth;
	/** 底部背景色 */
	private int mBackgroundColorNormal, mBackgroundColorPress;
	/** 屏幕宽度 */
	private int mWindowWidth;

	// 视频拍摄成功后的地址
	private String tempAddress = "";

	private CheckedTextView btn_over;// 保存按钮

	// 编码是否完成
	private boolean iscomple = false;
	private ImageView iv_touch;
	private CheckedTextView iv_status;
	private View saveButtom;
	private TextView tv_distime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mCreated = false;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		loadParmter();
		loadViews();
		mCreated = true;

	}

	/** 加载参数 */
	private void loadParmter() {

		mWindowWidth = DeviceUtils.getScreenWidth(this);

		mFocusWidth = ConvertToUtils.dipToPX(this, 64);
		mBackgroundColorNormal = getResources().getColor(R.color.black);// camera_bottom_bg
		mBackgroundColorPress = getResources().getColor(R.color.camera_bottom_press_bg);
	}

	/** 加载视图 */
	private void loadViews() {
		setContentView(R.layout.activity_media_recorder);
		// ~~~ 绑定控件
		mSurfaceView = (SurfaceView) findViewById(R.id.record_preview);
		mCameraSwitch = (CheckBox) findViewById(R.id.record_camera_switcher);
		mTitleNext = (ImageView) findViewById(R.id.title_next);
		mFocusImage = (ImageView) findViewById(R.id.record_focusing);
		mProgressView = (ProgressView) findViewById(R.id.record_progress);
		mRecordDelete = (CheckedTextView) findViewById(R.id.record_delete);
		mRecordLed = (CheckBox) findViewById(R.id.record_camera_led);
		btn_over = (CheckedTextView) findViewById(R.id.v_save);
		iv_touch = (ImageView) findViewById(R.id.iv_touch);
		iv_status = (CheckedTextView) findViewById(R.id.iv_status);
		saveButtom = findViewById(R.id.left);
		tv_distime = (TextView) findViewById(R.id.tv_distime);

		iv_touch.setOnTouchListener(mOnTouchRecord);
		btn_over.setOnClickListener(this);

		// mImportVideo = (ImageView) findViewById(R.id.importVideo_btn);
		// mImportVideo.setOnClickListener(this);
		// ~~~ 绑定事件

		mTitleNext.setOnClickListener(this);
		findViewById(R.id.title_back).setOnClickListener(this);
		mRecordDelete.setOnClickListener(this);

		// ~~~ 设置数据

		// 是否支持前置摄像头
		if (MediaRecorderBase.isSupportFrontCamera()) {
			mCameraSwitch.setOnClickListener(this);
		} else {
			mCameraSwitch.setVisibility(View.GONE);
		}

		// 是否支持闪光灯
		if (DeviceUtils.isSupportCameraLedFlash(getPackageManager())) {
			mRecordLed.setOnClickListener(this);
		} else {
			mRecordLed.setVisibility(View.GONE);
		}

		try {
			mFocusImage.setImageResource(R.drawable.video_focus);
			// mFocusImage.setVisibility(View.VISIBLE);
		} catch (OutOfMemoryError e) {
			Logger.e(e);
		}

		mProgressView.setMaxDuration(RECORD_TIME_MAX);

	}

	/** 初始化拍摄SDK */
	private void initMediaRecorder() {
		mMediaRecorder = new MediaRecorderNative();
		mRebuild = true;

		mMediaRecorder.setOnErrorListener(this);
		mMediaRecorder.setOnEncodeListener(this);
		File f = new File(VCamera.getVideoCachePath());

		if (!FileUtils.checkFile(f)) {
			f.mkdirs();
		}

		String key = String.valueOf(System.currentTimeMillis());
		mMediaObject = mMediaRecorder.setOutputDirectory(key, VCamera.getVideoCachePath() + key);
		mMediaRecorder.setOnSurfaveViewTouchListener(mSurfaceView);
		mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
		if (DeviceUtils.hasICS()) {
			mSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);
		}

		mMediaRecorder.prepare();
	}

	private View.OnTouchListener mOnTouchRecord = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startRecord();
				iv_status.setChecked(true);
				iv_status.setPressed(true);
				iv_touch.setPressed(true);
				saveButtom.setVisibility(View.GONE);
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				stopRecord();
				iv_status.setChecked(true);
				iv_status.setPressed(false);
				iv_touch.setPressed(false);
				saveButtom.setVisibility(View.VISIBLE);
				break;
			}

			return true;
		}
	};

	/** 点击屏幕录制 */
	private View.OnTouchListener mOnSurfaveViewTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mMediaRecorder == null || !mCreated) {
				return false;
			}

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				try {
					// 检测是否手动对焦
					if (checkCameraFocus(event)) {

						return true;
					} else {
						showFocusImage(event);
						mMediaRecorder.setAutoFocus();
						mHandler.sendEmptyMessageDelayed(HANDLE_HIDE_RECORD_FOCUS, 3500);// 最多3.5秒也要消失
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}
			return true;
		}

	};

	public void onResume() {
		super.onResume();

		if (mMediaRecorder == null) {
			UtilityAdapter.freeFilterParser();
			UtilityAdapter.initFilterParser();
			initMediaRecorder();
		} else {
			mRecordLed.setChecked(false);
			mMediaRecorder.prepare();
			mProgressView.setData(mMediaObject);
		}
	}

	// 释放后就不能从重新拍摄
	// public void onPause() {
	// super.onPause();
	// stopRecord();
	// UtilityAdapter.freeFilterParser();
	// if (!mReleased) {
	// if (mMediaRecorder != null)
	// mMediaRecorder.release();
	// }
	// mReleased = false;
	// }

	/** 手动对焦 */
	private boolean checkCameraFocus(MotionEvent event) {

		mFocusImage.setVisibility(View.GONE);
		int x = Math.round(event.getX());
		int y = Math.round(event.getY());
		float touchMajor = event.getTouchMajor();
		float touchMinor = event.getTouchMinor();
		int previewWidth = mSurfaceView.getWidth();
		int previewHeight = mSurfaceView.getHeight();
		mFocusWidth = mFocusImage.getWidth();
		Logger.e("touchMajor = " + touchMajor);
		Logger.e("touchMinor = " + touchMinor);

		Rect touchRect = new Rect((int) (x - touchMajor / 2), (int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
				(int) (y + touchMinor / 2));

		Logger.e("touchRect = " + touchRect);
		Logger.e("mWindowWidth = " + mWindowWidth);

		if (touchRect.right > 1000)
			touchRect.right = 1000;
		if (touchRect.bottom > 1000)
			touchRect.bottom = 1000;
		if (touchRect.left < 0)
			touchRect.left = 0;
		if (touchRect.right < 0)
			touchRect.right = 0;

		if (touchRect.left >= touchRect.right || touchRect.top >= touchRect.bottom)
			return false;

		ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
		focusAreas.add(new Camera.Area(touchRect, 1000));
		if (!mMediaRecorder.manualFocus(new Camera.AutoFocusCallback() {

			@Override
			public void onAutoFocus(boolean success, Camera camera) {
			}
		}, focusAreas)) {
		}

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFocusImage.getLayoutParams();
		int left = x - (mFocusWidth / 2);// (int) x -
											// (focusingImage.getWidth()
											// / 2);
		int top = y - (mFocusWidth / 2);// (int) y -
										// (focusingImage.getHeight()
										// / 2);
		if (left < 0)
			left = 0;
		else if (left + mFocusWidth / 2 >= previewWidth - mFocusWidth)
			left = previewWidth - mFocusWidth;
		if (top + mFocusWidth / 2 >= previewHeight - mFocusWidth)
			top = previewHeight - mFocusWidth;
		lp.leftMargin = left;
		lp.topMargin = top;
		mFocusImage.setLayoutParams(lp);
		mFocusImage.setVisibility(View.VISIBLE);

		if (mFocusAnimation == null)
			mFocusAnimation = AnimationUtils.loadAnimation(this, R.anim.record_focus);

		mFocusImage.startAnimation(mFocusAnimation);

		mHandler.sendEmptyMessageDelayed(HANDLE_HIDE_RECORD_FOCUS, 3500);// 最多3.5秒也要消失
		return true;
	}

	private void showFocusImage(MotionEvent e) {

		int x = Math.round(e.getX());
		int y = Math.round(e.getY());
		int previewWidth = mSurfaceView.getWidth();
		int previewHeight = mSurfaceView.getHeight();
		mFocusWidth = mFocusImage.getWidth();
		Rect touchRect = new Rect();

		mMediaRecorder.calculateTapArea(mFocusWidth, mFocusWidth, 1f, x, y, previewWidth, previewWidth, touchRect);

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFocusImage.getLayoutParams();
		int left = x - (mFocusWidth / 2);
		int top = y - (mFocusWidth / 2);
		if (left < 0)
			left = 0;
		else if (left + mFocusWidth / 2 >= previewWidth - mFocusWidth)
			left = mWindowWidth - mFocusWidth;
		if (top + mFocusWidth / 2 >= previewHeight - mFocusWidth)
			top = previewHeight - mFocusWidth;

		lp.leftMargin = left;
		lp.topMargin = top;

		Logger.e("left =  " + left);
		Logger.e("top =  " + top);

		mFocusImage.setLayoutParams(lp);
		mFocusImage.setVisibility(View.VISIBLE);

		if (mFocusAnimation == null)
			mFocusAnimation = AnimationUtils.loadAnimation(this, R.anim.record_focus);

		mFocusImage.startAnimation(mFocusAnimation);

		// mHandler.sendEmptyMessageDelayed(HANDLE_HIDE_RECORD_FOCUS, 3500);//
		// 最多3.5秒也要消失
	}

	/** 开始录制 */
	private void startRecord() {
		if (mMediaRecorder != null) {
			com.yixia.weibo.sdk.model.MediaObject$MediaPart part = mMediaRecorder.startRecord();
			if (part == null) {
				return;
			}

			// 如果使用MediaRecorderSystem，不能在中途切换前后摄像头，否则有问题
			if (mMediaRecorder instanceof MediaRecorderSystem) {
				mCameraSwitch.setVisibility(View.GONE);
			}
			mProgressView.setData(mMediaObject);
		}

		mRebuild = true;
		mPressedStatus = true;
		// mBottomLayout.setBackgroundColor(mBackgroundColorPress);

		if (mHandler != null) {
			mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
			mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

			mHandler.removeMessages(HANDLE_STOP_RECORD);
			mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD, RECORD_TIME_MAX - mMediaObject.getDuration());
		}
		mRecordDelete.setVisibility(View.GONE);
		mCameraSwitch.setEnabled(false);
		mRecordLed.setEnabled(false);
	}

	@Override
	public void onBackPressed() {
		if (mRecordDelete != null && mRecordDelete.isChecked()) {
			cancelDelete();
			return;
		}

		if (mMediaObject != null && mMediaObject.getDuration() > 1 && !iscomple) {// 没有录制完成
			// 未转码
			new AlertDialog.Builder(this).setTitle(R.string.hint).setMessage(R.string.record_camera_exit_dialog_message)
					.setNegativeButton(R.string.record_camera_cancel_dialog_yes, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mMediaObject.delete();
							// 停止消息推送轮询
							VCamera.stopPollingService();

							activityResult("");

							finish();
						}

					}).setPositiveButton(R.string.record_camera_cancel_dialog_no, null).setCancelable(false).show();
			return;
		}

		// 停止消息推送轮询
		VCamera.stopPollingService();

		String tempPath = tempAddress;

		if (mMediaObject != null && TextUtils.isEmpty(tempAddress)) {
			tempPath = mMediaObject.getOutputTempVideoPath();
		}

		activityResult(tempPath);

		finish();
	}

	private void activityResult(String responseAddress) {

		Intent intent = new Intent();

		intent.putExtra(RECORD_STATE, iscomple);// 是否录制成功

		intent.putExtra(RESULT_PATH, responseAddress);

		setResult(RESULT_CODE, intent);
	}

	/**
	 * 把视频的数据复制到其他地址
	 * 
	 * @param mMediaObject
	 */
	private void source2TargetFile(MediaObject mMediaObject) {

		if (mMediaObject == null || TextUtils.isEmpty(addressPath)) {
			return;
		}

		File sourceFile = new File(mMediaObject.getOutputTempVideoPath());

		if (sourceFile.exists() && sourceFile.length() > 10) {
			File targetFile = new File(addressPath);

			try {
				FileUtil.copyFile(sourceFile, targetFile);
				tempAddress = addressPath;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** 停止录制 */
	private void stopRecord() {
		mPressedStatus = false;

		if (mMediaRecorder != null) {
			mMediaRecorder.stopRecord();
		}

		mRecordDelete.setVisibility(View.VISIBLE);
		mCameraSwitch.setEnabled(true);
		mRecordLed.setEnabled(true);

		mHandler.removeMessages(HANDLE_STOP_RECORD);
		checkStatus();
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (mHandler.hasMessages(HANDLE_STOP_RECORD)) {
			mHandler.removeMessages(HANDLE_STOP_RECORD);
		}
		// 处理开启回删后其他点击操作
		if (id != R.id.record_delete) {
			if (mMediaObject != null) {
				com.yixia.weibo.sdk.model.MediaObject$MediaPart part = mMediaObject.getCurrentPart();
				if (part != null) {
					if (part.remove) {
						part.remove = false;
						mRecordDelete.setChecked(false);
						if (mProgressView != null)
							mProgressView.invalidate();
					}
				}
			}
		}

		if (id == R.id.v_save) {// 录制完成
			mMediaRecorder.startEncoding();
		} else if (id == R.id.title_back) {
			onBackPressed();
		} else if (id == R.id.record_camera_switcher) {
			if (mRecordLed.isChecked()) {
				if (mMediaRecorder != null) {
					mMediaRecorder.toggleFlashMode();
				}
				mRecordLed.setChecked(false);
			}
			if (mMediaRecorder != null) {
				mMediaRecorder.switchCamera();
			}
			if (mMediaRecorder.isFrontCamera()) {
				mRecordLed.setEnabled(false);
			} else {
				mRecordLed.setEnabled(true);
			}
		} else if (id == R.id.record_camera_led) {
			// 开启前置摄像头以后不支持开启闪光灯
			if (mMediaRecorder != null) {
				if (mMediaRecorder.isFrontCamera()) {
					return;
				}
			}
			if (mMediaRecorder != null) {
				mMediaRecorder.toggleFlashMode();
			}
		} else if (id == R.id.title_next) {// 自动录制完成
			mMediaRecorder.startEncoding();
		} else if (id == R.id.record_delete) {
			// 取消回删
			if (mMediaObject != null) {
				com.yixia.weibo.sdk.model.MediaObject$MediaPart part = mMediaObject.getCurrentPart();
				if (part != null) {
					if (part.remove) {
						mRebuild = true;
						part.remove = false;
						backRemove();
						mRecordDelete.setChecked(false);
					} else {
						part.remove = true;
						mRecordDelete.setChecked(true);
					}
				}
				if (mProgressView != null)
					mProgressView.invalidate();
				// 检测按钮状态
				checkStatus();
			}
		}
	}

	/** 回删 */
	public boolean backRemove() {

		if (mMediaObject != null && mMediaObject.mediaList != null) {
			int size = mMediaObject.mediaList.size();
			if (size > 0) {
				com.yixia.weibo.sdk.model.MediaObject$MediaPart part = (MediaObject$MediaPart) mMediaObject.mediaList
						.get(size - 1);
				mMediaObject.removePart(part, true);

				if (mMediaObject.mediaList.size() > 0)
					mMediaObject.mCurrentPart = (MediaObject$MediaPart) mMediaObject.mediaList
							.get(mMediaObject.mediaList.size() - 1);
				else
					mMediaObject.mCurrentPart = null;
				return true;
			}
		}
		return false;
	}

	/** 取消回删 */
	private boolean cancelDelete() {
		if (mMediaObject != null) {
			MediaObject$MediaPart part = mMediaObject.getCurrentPart();
			if (part != null && part.remove) {
				part.remove = false;
				mRecordDelete.setChecked(false);

				if (mProgressView != null)
					mProgressView.invalidate();

				return true;
			}
		}
		return false;
	}

	/** 检查录制时间，显示/隐藏下一步按钮 */
	private int checkStatus() {
		int duration = 0;
		if (!isFinishing() && mMediaObject != null) {
			duration = mMediaObject.getDuration();
			if (duration < RECORD_TIME_MIN) {
				if (duration == 0) {
					mCameraSwitch.setVisibility(View.VISIBLE);
					mRecordDelete.setVisibility(View.GONE);
				}
				// 视频必须大于3秒
				if (mTitleNext.getVisibility() != View.INVISIBLE)
					mTitleNext.setVisibility(View.INVISIBLE);
			} else {
				// 下一步
				if (mTitleNext.getVisibility() != View.VISIBLE) {
					mTitleNext.setVisibility(View.INVISIBLE);
				}
			}
		}
		return duration;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_STOP_RECORD:
				stopRecord();
				mTitleNext.performClick();
				break;
			case HANDLE_INVALIDATE_PROGRESS:
				if (mMediaRecorder != null && !isFinishing()) {
					if (mProgressView != null)
						mProgressView.invalidate();
					if (mPressedStatus)
						sendEmptyMessageDelayed(0, 30);
				}
				// if (mPressedStatus)
				// titleText.setText(String.format("%.1f",
				// mMediaRecorder.getDuration() / 1000F));
				if (tv_distime != null) {
					String currMiao = String.format("%.1f", mMediaObject.getDuration() / 1000F);
					tv_distime.setText("最大录制时间" + RECORD_TIME_MAX / 1000f + "\r\n当前录制时间:" + currMiao);
				}
				break;
			case HANDLE_HIDE_RECORD_FOCUS:

				if (mFocusImage != null) {
					mFocusImage.setVisibility(View.GONE);
				}
				break;
			}
		}
	};

	@Override
	public void onEncodeStart() {
		System.out.println("编码开始==");
		showProgress("", getString(R.string.record_camera_progress_message));
	}

	@Override
	public void onEncodeProgress(int progress) {
		System.out.println("编码进度==" + progress);
		// Logger.e("[MediaRecorderActivity]onEncodeProgress..." + progress);
	}

	/** 转码完成 */
	@Override
	public void onEncodeComplete() {

		source2TargetFile(mMediaObject);

		hideProgress();

		mRebuild = false;
		iscomple = true;
		Toast.makeText(this, getString(R.string.ercode_ok), Toast.LENGTH_SHORT).show();

	}

	/**
	 * 转码失败 检查sdcard是否可用，检查分块是否存在
	 */
	@Override
	public void onEncodeError() {
		hideProgress();
		Toast.makeText(this, getString(R.string.ercode_failed), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onVideoError(int what, int extra) {

	}

	@Override
	public void onAudioError(int what, String message) {

	}

	@Override
	public void onPrepared() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMediaRecorder != null)
			mMediaRecorder.release();
	}
}