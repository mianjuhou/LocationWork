package com.jtv.base.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

public class AplicationUtils {
	public static String path = null;
	public static List<Activity> stackActivity = new ArrayList<Activity>();

	public static void addActivity(Activity activity) {
		stackActivity.add(activity);
	}

	public static void closeActivity() {
		try {
			for (int i = 0; i < stackActivity.size(); i++) {
				if (!stackActivity.get(i).isFinishing()) {
					stackActivity.get(i).finish();
				}
			}
			stackActivity.clear();
		} catch (Exception e) {
		}
	}

	/**
	 *  * 用来判断服务是否运行.  * @param context  * @param className 判断的服务名字  * @return
	 * true 在运行 false 不在运行  
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 获取app的存储目录
	 * <p>
	 * 一般情况下是这样的/storage/emulated/0/Android/data/包名/
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppDir(Context context) {
		return (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? (Environment
				.getExternalStorageDirectory().getPath() + "/Android/data/")
				: (context.getCacheDir().getPath()))
				+ context.getPackageName();
	}

	public static Intent photo(Context context) {

		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			String filePath = getAppDir(context) + "/image/";
			File files = new File(filePath);
			if (!files.exists()) {
				files.mkdirs();
			}

			File file = new File(filePath, String.valueOf(System
					.currentTimeMillis()) + ".jpg");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			path = file.getPath();
			Uri imageUri = Uri.fromFile(file);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		} else {
			Toast.makeText(context, "请插入内存卡", Toast.LENGTH_SHORT);
		}
		return openCameraIntent;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取设备ID
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (!TextUtils.isEmpty(manager.getDeviceId())) {
			return manager.getDeviceId();
		} else {
			final String tmDevice, tmSerial, tmPhone, androidId;

			tmDevice = "" + manager.getDeviceId();

			tmSerial = "" + manager.getSimSerialNumber();

			androidId = ""
					+ android.provider.Settings.Secure.getString(
							context.getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);

			UUID deviceUuid = new UUID(androidId.hashCode(),
					((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

			String uniqueId = deviceUuid.toString();

			return uniqueId;
		}
	}
	/**
	 * 获取绝对路径
	 * @param activity
	 * @return
	 */
	public static String getMuLu(Activity activity) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				&& Environment.getExternalStorageDirectory().exists()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return activity.getApplication().getFilesDir().getAbsolutePath();
		}
	}

	/**
	 * 获取应用AppKey
	 * 
	 * @return
	 */
	public static String getAppKey(Context context) {
		ApplicationInfo applicationInfo = null;
		String appKey = null;
		try {
			applicationInfo = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			appKey = applicationInfo.metaData.getString("app_key");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return appKey;
	}
    
    // 取得版本号
    public static String GetVersion(Context context) {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return manager.versionName;
		} catch (NameNotFoundException e) {
			return "Unknown";
		}
	}
	

	public static String getAppSecret(Context context) {
		ApplicationInfo applicationInfo = null;
		String appSecret = null;
		try {
			applicationInfo = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			appSecret = applicationInfo.metaData.getString("app_secret");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return appSecret;
	}

	public interface IContactCallback {
		void callback(String string);
	}

	/**
	 * 获取手机上的联系人电话.
	 * 
	 * @return
	 */
	public static void getContacts(Context context,
			final IContactCallback callback) {
		final Context ctx = context;
		new Thread(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				Cursor cursor = ctx.getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null, null,
						null, null);
				if (cursor != null) {
					while (cursor.moveToNext()) {

						String contactId = cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts._ID));
						String displayName = cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						int numberCount = cursor.getInt(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
						if (numberCount > 0) {
							Cursor c = ctx
									.getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ "=" + contactId, null,
											null);
							if (c != null) {
								while (c.moveToNext()) {
									String number = c.getString(c
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									if (number.contains(" ")) {
										number = number.replace(" ", "");
									}
									if (number.contains("-")) {
										number = number.replace("-", "");
									}
									// 截取后11位.针对前面有+86前缀的.
									if (number.length() > 11) {
										number = number.subSequence(
												number.length() - 11,
												number.length()).toString();
									}

									sb.append(number);
									sb.append(",");
								}
								c.close();
								c = null;
							}
						}
					}
					cursor.close();
					cursor = null;
				}
				if (sb.toString().endsWith(",")) {
					sb.delete(sb.length() - 1, sb.length());
				}
				callback.callback(sb.toString());
			}

		}).start();
	}
}
