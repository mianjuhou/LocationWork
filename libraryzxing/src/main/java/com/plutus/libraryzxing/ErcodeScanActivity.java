package com.plutus.libraryzxing;

import java.io.IOException;
import java.util.Vector;

import com.framework.libraryzxing.camera.CameraManager;
import com.framework.libraryzxing.decoding.CaptureActivityHandler;
import com.framework.libraryzxing.decoding.InactivityTimer;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
/**
 * 
 * @author 		lgs 	
 * Project      Name:Location_android 
 * Package      Name:com.jtv.framework.zxing 
 * Date:        2015-5-7下午7:15:23 
 * Copyright    (c) 2015, whatl@foxmail.com All Rights Reserved. 
 * Desctiption  二维码扫描
 * Need			
 *
 */
public class ErcodeScanActivity extends Activity implements Callback {
	public final static int PURCHASEADD = 200;
	private Context mContext;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private SurfaceView surfaceView;
	private ImageView mBack;
	private Button mShowLight;
	private String resultString = "";
	private int screenWidth;
	public static final int SCANREQUESTCODE = 0x1;
	private RelativeLayout ll_title, rl_btn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_jtv_fragment_zxing_scan);
		mContext = this;
		CameraManager.init(getApplication());
//		setScreenAlpha(this, 0.1f);
		initControl();
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}
	/**
	 * 窗口背景变暗的效果0.0f-1.0f，透明度1.0完全不透明
	 * 
	 * @param activity
	 *            float f
	 */
	public void setScreenAlpha(Activity activity, float f) {
		// 产生背景变暗效果
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = f;
		activity.getWindow().setAttributes(lp);
	}
	private void initControl() {
		mShowLight = (Button) findViewById(R.id.scan_light);
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		ll_title = (RelativeLayout) findViewById(R.id.ll_title);
		rl_btn = (RelativeLayout) findViewById(R.id.rl_btn);
		ll_title.getBackground().setAlpha(100);
		rl_btn.getBackground().setAlpha(100);
		mBack = (ImageView) findViewById(R.id.back);

		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mShowLight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CameraManager.get().turnLightOnOrOff();
			}
		});

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;

	}

	@Override
	protected void onResume() {
		super.onResume();
		start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * @param result
	 * @param barcode
	 *当扫描成功或者失败的处理消息
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		resultString = result.getText();//扫描成功后的字符串
		Intent intent = new Intent();
		intent.putExtra("result", result.toString());
		setResult(SCANREQUESTCODE, intent);
		finish();
//		if (resultString.equals("")) {
//			UToast.makeShortTxt(this, "扫描失败"+resultString);
//		} else {
//			//扫描成功请求后台服务数据,
//			UToast.makeShortTxt(this, "扫描成功"+resultString);
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			handler.restartPreviewAndDecode();
//		}
	}
	/**
	 * 开始扫描
	 */
	private void start() {
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	/**
	 * 停止扫描
	 */
	private void stop() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	/**
	 * 扫描正确后的震动声音,如果感觉apk大了,可以删除
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}
	private static final long VIBRATE_DURATION = 200L;
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
}

