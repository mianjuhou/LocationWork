package com.jtv.locationwork.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.LogUtil;
import com.jtv.locationwork.util.SpUtiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;

public class RtspVideoAty extends Activity implements RtspClient.Callback, Session.Callback, SurfaceHolder.Callback {
	// log tag
	public final static String TAG = RtspVideoAty.class.getSimpleName();

	private static String machineCode = "";

	// surfaceview
	private static SurfaceView mSurfaceView;

	// Rtsp session
	private Session mSession;

	private TelephonyManager tm;

	private static RtspClient mClient;

	private String deviceId;

	// 172.16.91.30
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();
		machineCode = deviceId;
		setContentView(R.layout.location_lay_rtspvideo);

		mSurfaceView = (SurfaceView) findViewById(R.id.surface);

		// mSurfaceView.getHolder().addCallback(this);

		// Initialize RTSP SpeakClient
		initRtspClient();

	}

	@Override
	protected void onResume() {
		super.onResume();

		// toggleStreaming();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// toggleStreaming();
	}

	private void initRtspClient() {
		// Configures the SessionBuilder
		// mSession = SessionBuilder.getInstance().setContext(getApplicationContext())
		// // .setAudioEncoder(SessionBuilder.AUDIO_NONE)
		// .setAudioEncoder(SessionBuilder.AUDIO_AAC).setAudioQuality(new AudioQuality(8000, 16000))
		//
		// .setVideoEncoder(SessionBuilder.VIDEO_H264).setSurfaceView(mSurfaceView).setPreviewOrientation(0)
		// .setCallback(this).build();

		mSession = SessionBuilder.getInstance().setCallback(this).setSurfaceView(mSurfaceView).setPreviewOrientation(90)
				.setContext(getApplicationContext()).setAudioEncoder(SessionBuilder.AUDIO_NONE)
				.setAudioQuality(new AudioQuality(16000, 32000)).setVideoEncoder(SessionBuilder.VIDEO_H264)
				// .setVideoQuality(new VideoQuality(320,240,20,500000))
				.build();

		// mSession.setPreviewOrientation(-90);
		// Configures the RTSP SpeakClient
		mClient = new RtspClient();
		mClient.setSession(mSession);
		mClient.setCallback(this);

		mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);
		String ip, port, path;

		// We parse the URI written in the Editext
		Pattern uri = Pattern.compile("rtsp://(.+):(\\d+)/(.+)");

		// machine;

		String SOCKET_HOST_IP = "";// 从配置文件中获取socketip
		int SOCKET_HOST_PORT = 1935;
		String ip_port = "";
		try {
			ip_port = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.SOCKET_RTSP_IP, Constants.RTSP_VIDEO_SOCKET);
			String[] split = ip_port.split(":");
			// String ip = split[0];
			// String port = split[1];

			// 263em
			SOCKET_HOST_IP = split[0];
			SOCKET_HOST_PORT = Integer.valueOf(split[1]);
		} catch (Exception e) {
			LogUtil.e(e);
			SpUtiles.BaseInfo.mbasepre.edit().putString(CoustomKey.SOCKET_RTSP_IP, Constants.RTSP_VIDEO_SOCKET).commit();
			ip_port = "118.26.65.50:1935";
			String[] split = ip_port.split(":");
			// String ip = split[0];
			// String port = split[1];
			// 263em
			SOCKET_HOST_IP = split[0];
			SOCKET_HOST_PORT = Integer.valueOf(split[1]);
		}

		String pathIp = "rtsp://" + SOCKET_HOST_IP + ":" + SOCKET_HOST_PORT + "/live/";
		Matcher m = uri.matcher(pathIp + machineCode);

		m.find();
		ip = m.group(1);
		port = m.group(2);
		path = m.group(3);

		mClient.setCredentials(Constants.PUBLISHER_USERNAME, Constants.PUBLISHER_PASSWORD);
		mClient.setServerAddress(ip, Integer.parseInt(port));
		mClient.setStreamPath("/" + path);
		mClient.startStream();// 这里调用会让照相机旋转正常
		mSurfaceView.getHolder().addCallback(this);
	}

	public void startStream() {
		if (mClient != null && !mClient.isStreaming() && mSession != null) {
			// Start camera preview
			mSession.startPreview();

			// Start video stream
			mClient.startStream();
		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mSession.stop();
		mSession.release();
		mClient.release();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// startStream();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// stopStreaning();
	}

	public void stopStreaning() {
		if (mClient != null && mClient.isStreaming() && mSession != null) {
			// already streaming, stop streaming
			// stop camera preview
			mSession.stopPreview();

			// stop streaming
			mClient.stopStream();
		}
	}

	private void toggleStreaming() {
		if (!mClient.isStreaming()) {
			// Start camera preview
			mSession.startPreview();
			// Start video stream
			mClient.startStream();
		} else {
			// already streaming, stop streaming
			// stop camera preview
			mSession.stopPreview();

			// stop streaming
			mClient.stopStream();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mClient.release();
		mSession.release();
		mSurfaceView.getHolder().removeCallback(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onSessionError(int reason, int streamType, Exception e) {
		Log.e(TAG, "An error occured", e);
		switch (reason) {
		case Session.ERROR_CAMERA_ALREADY_IN_USE:
			break;
		case Session.ERROR_CAMERA_HAS_NO_FLASH:
			break;
		case Session.ERROR_INVALID_SURFACE:
			break;
		case Session.ERROR_STORAGE_NOT_READY:
			break;
		case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
			break;
		case Session.ERROR_OTHER:
			break;
		}

		if (e != null) {
			alertError(e.getMessage());
			e.printStackTrace();
		}
	}

	private void alertError(final String msg) {
		final String error = (msg == null) ? "不知到错误: " : msg;
		AlertDialog.Builder builder = new AlertDialog.Builder(RtspVideoAty.this);
		builder.setMessage(error).setPositiveButton("好的", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		AlertDialog dialog = builder.create();

		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onRtspUpdate(int message, Exception exception) {
		switch (message) {
		case RtspClient.ERROR_CONNECTION_FAILED:
		case RtspClient.ERROR_WRONG_CREDENTIALS:
			alertError(exception.getMessage());
			exception.printStackTrace();
			break;
		}
	}

	@Override
	public void onPreviewStarted() {
	}

	@Override
	public void onSessionConfigured() {
		Log.d(TAG, "Preview configured.");
		// Once the stream is configured, you can get a SDP formated session description
		// that you can send to the receiver of the stream.
		// For example, to receive the stream in VLC, store the session description in a .sdp file
		// and open it with VLC while streming.

		Log.d(TAG, mSession.getSessionDescription());

		// 加上后后台收不到数据
		// mSession.start();
	}

	@Override
	public void onSessionStarted() {
		Log.d(TAG, "Streaming session started.");
	}

	@Override
	public void onSessionStopped() {
		Log.d(TAG, "Streaming session stopped.");
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// toggleStreaming();
		// Starts the preview of the Camera
		Log.d(TAG, "surfaceCreated  startPreview");
		// mSession.startPreview();
		startStream();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed  stop");
		// mSession.stop();
		stopStreaning();
	}

	@Override
	public void onBitrateUpdate(long bitrate) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Bitrate: " + bitrate);

	}

}
