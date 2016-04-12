package com.jtv.locationwork.util;
import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
/**
 * @author: zn E-mail:zhangn@jtv.com.cn 
 * @version:2014-11-11
 * 类说明:添加震动(可自定义震动的时长模式等)
 */
public class VibrateVoice {
	static Vibrator vib = null;
	static AudioManager mAudioManager = null;
	static Uri notification = null;
	static Ringtone ringtone = null;

	public static void Vibrate(final Activity activity, long milliseconds) {
		vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);

	}
	/**
	 * 添加震动
	 * @author:zn
	 * @version:2014-11-12
	 * @param activity(当前上下文)
	 * @param pattern(long[] pattern:自定义震动模式 。数组中数字的含义依次是静止的时长，震动时长，静止时长，震动时长。。。时长的单位是毫秒)
	 * @param isRepeat(boolean isRepeat: 是否反复震动，如果是true，反复震动，如果是false，只震动一次)
	 */
	public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}

	/**
	 * 将震动取消
	 * @author:zn
	 * @version:2014-11-12
	 */
	public static void stopVibrate() {
		if (null != vib) {
			vib.cancel();
		}

	}

	/**
	 * 成功提示音
	 * @author:zn
	 * @version:2014-11-12
	 * @param activity
	 */
	@SuppressWarnings("static-access")
	public static void getSuccessRing(final Activity activity) {
		notification = RingtoneManager.getDefaultUri(mAudioManager.FLAG_ALLOW_RINGER_MODES);

		ringtone = RingtoneManager.getRingtone(activity, notification);
		if (ringtone != null) {
			ringtone.play();
		}
	}

	/**
	 * 失败提示音
	 * @author:zn
	 * @version:2014-11-12
	 * @param activity
	 */
	@SuppressWarnings("static-access")
	public static void getFailRing(final Activity activity) {
		notification = RingtoneManager.getDefaultUri(mAudioManager.FLAG_PLAY_SOUND);
		ringtone = RingtoneManager.getRingtone(activity, notification);
		if (ringtone != null) {
			ringtone.play();
		}
	}
	/**
	 * 将提示音关闭
	 * @author:zn
	 * @version:2014-11-12
	 */
	public static void getstopSuccessFail() {
		ringtone.stop();
	}

}
