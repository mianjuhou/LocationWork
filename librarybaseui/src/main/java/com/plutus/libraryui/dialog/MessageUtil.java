package com.plutus.libraryui.dialog;

import java.util.Random;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

@SuppressLint("NewApi")
public class MessageUtil {
	/**
	 * 安卓自带的一个水平进度条，他会显示当前的进度，默认进度为0，最大进度为100 样式：上面一个标题，中间一个描述，下面一个水平进度条
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param desc
	 *            内容
	 * @return
	 */
	public static ProgressDialog showDialogProgressLoading(Context context, String title, String desc) {
		final ProgressDialog pro = new ProgressDialog(context);
		boolean empty = TextUtils.isEmpty(title);
		if (empty) {
		} else {
			pro.setTitle(title);
		}
		pro.setMessage(desc);
		pro.setMax(100);
		pro.setProgress(0);
		pro.setProgressStyle(1);
		pro.setCanceledOnTouchOutside(false);
		pro.show();
		return pro;
	}

	public static Notification showNotification(Context mContext, boolean isCancel, Intent intent, int fslg,
			int imageid, Bitmap mIcon, String title, String describe, String tirck) {
		// 高版本使用
		Notification notification = null;
		try {
			PendingIntent m_PendingIntent = PendingIntent.getActivity(mContext, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			notification = new Notification.Builder(mContext).setAutoCancel(isCancel).setContentTitle(title)
					.setContentText(describe).setContentIntent(m_PendingIntent).setWhen(System.currentTimeMillis())
					.setTicker(tirck).setSmallIcon(imageid).setLargeIcon(mIcon).build();

			// notification.notify

			NotificationManager mManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mManager.notify("" + imageid, imageid, notification);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return notification;
	}

	public static Notification showLownNotification(Context mContext, boolean isCancel, Intent intent, int flag,
			int imageid, String title, String describe, String tirck) {
		NotificationManager mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent m_PendingIntent = PendingIntent.getActivity(mContext, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setContentTitle(title)// 设置通知栏标题
				.setContentText(describe) // 设置通知栏显示内容
				.setContentIntent(m_PendingIntent) // 设置通知栏点击意图
				// .setNumber(number) //设置通知集合的数量
				.setTicker(tirck) // 通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
				.setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
				// .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
				// requires VIBRATE permission
				.setSmallIcon(imageid);// 设置通知小ICON
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_NO_CLEAR;
		mManager.notify(flag, notify);
		return notify;
	}
}
