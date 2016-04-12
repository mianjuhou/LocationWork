package com.jtv.video.recorder.activity;

import com.yixia.weibo.sdk.util.StringUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public abstract class BaseRecordVideoAty extends Activity {

	/** 请求视频存入的地址 */
	protected static String addressPath = "";

	/** 录制最长时间 */
	protected static int RECORD_TIME_MAX = 10 * 1000;

	/** 录制最小时间 */
	protected static int RECORD_TIME_MIN = 3 * 1000;

	/** 请求视频放值的地址key */
	public static final String ADDRESS_PATH = "ADDRESSPATH";

	/** 录制成功的地址key */
	public static final String RESULT_PATH = "RESLUTPATH";

	/** 录制的最大时间key */
	public static final String MAX_TIME_RECORD = "max_time";

	/** 录制最小时间key */
	public static final String MIN_TIME_RECORD = "min_time";
	
	/** 录制状态key */
	public static final String RECORD_STATE = "state";

	/** 录制成功的结束码 */
	public static final int RESULT_CODE = 546;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentParmter();
	}

	protected void getIntentParmter() {

		RECORD_TIME_MAX = getIntent().getIntExtra(MAX_TIME_RECORD, RECORD_TIME_MAX);
		RECORD_TIME_MIN = getIntent().getIntExtra(MIN_TIME_RECORD, RECORD_TIME_MIN);

		addressPath = getIntent().getStringExtra(ADDRESS_PATH);

		if (RECORD_TIME_MIN < 1000) {
			RECORD_TIME_MIN = 1000;
		}

		if (RECORD_TIME_MAX < RECORD_TIME_MIN) {
			RECORD_TIME_MAX = RECORD_TIME_MIN;
		}

	}

	@Override
	public void onBackPressed() {

		Intent intent = new Intent();
		intent.putExtra(RESULT_PATH, addressPath);
		setResult(RESULT_CODE, intent);
		finish();
	}

	protected ProgressDialog mProgressDialog;

	public ProgressDialog showProgress(String title, String message) {
		return showProgress(title, message, -1);
	}

	public ProgressDialog showProgress(String title, String message, int theme) {
		if (mProgressDialog == null) {
			if (theme > 0)
				mProgressDialog = new ProgressDialog(this, theme);
			else
				mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
			mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
		}

		if (!StringUtils.isEmpty(title))
			mProgressDialog.setTitle(title);
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
		return mProgressDialog;
	}

	public void hideProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		hideProgress();
		mProgressDialog = null;
	}

	/** 反序列化对象 */
	// protected static MediaObject restoneMediaObject(String obj) {
	// try {
	// String str = FileUtils.readFile(new File(obj));
	// Gson gson = new Gson();
	// MediaObject result = gson.fromJson(str.toString(), MediaObject.class);
	// result.getCurrentPart();
	// preparedMediaObject(result);
	// return result;
	// } catch (Exception e) {
	// if (e != null)
	// Log.e("VCamera", "readFile", e);
	// }
	// return null;
	// }

	/** 预处理数据对象 */
	// public static void preparedMediaObject(MediaObject mMediaObject) {
	// if (mMediaObject != null && mMediaObject.getMedaParts() != null) {
	// int duration = 0;
	// for (com.yixia.weibo.sdk.model.MediaObject$MediaPart part :
	// (LinkedList<com.yixia.weibo.sdk.model.MediaObject$MediaPart>)mMediaObject.getMedaParts())
	// {
	// part.startTime = duration;
	// part.endTime = part.startTime + part.duration;
	// duration += part.duration;
	// }
	// }
	// }

	/** 序列号保存视频数据 */
	// public static boolean saveMediaObject(MediaObject mMediaObject) {
	// if (mMediaObject != null) {
	// try {
	// if (StringUtils.isNotEmpty(mMediaObject.getObjectFilePath())) {
	// FileOutputStream out = new
	// FileOutputStream(mMediaObject.getObjectFilePath());
	// Gson gson = new Gson();
	// out.write(gson.toJson(mMediaObject).getBytes());
	// out.flush();
	// out.close();
	// return true;
	// }
	// } catch (Exception e) {
	// Logger.e(e);
	// }
	// }
	// return false;
	// }
}
