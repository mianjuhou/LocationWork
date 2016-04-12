package com.jtv.locationwork.exception;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author MyEatMy Date: 2015-5-20下午2:59:56 Copyright (c) 2015,
 *         whatl@foxmail.com 处理信息到本地
 */
public class MyCrashHandler implements UncaughtExceptionHandler {
	// 需求是 整个应用程序 只有一个 MyCrash-Handler
	private static MyCrashHandler myCrashHandler;
	private Context context;
	private SimpleDateFormat dataFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	// 1.私有化构造方法
	private MyCrashHandler() {
	}

	public static synchronized MyCrashHandler getInstance() {
		if (myCrashHandler != null) {
			return myCrashHandler;
		} else {
			myCrashHandler = new MyCrashHandler();
			return myCrashHandler;
		}
	}

	public void init(Context context) {
		this.context = context;
	}
	// android获取sd卡路径方法：
		public String getSDPath() {
			boolean sdCardExist = android.os.Environment.MEDIA_MOUNTED
					.equals(Environment.getExternalStorageState()); // 判断sd卡是否存在
			String filepath = null;
			if (sdCardExist) {
				filepath = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
			}
			return filepath;

		}
	public void uncaughtException(Thread arg0, Throwable arg1) {
		arg1.printStackTrace();
		// 1.获取当前程序的版本号. 版本的id
		String versioninfo = getVersionInfo();

		// 2.获取手机的硬件信息.
		String mobileInfo = getMobileInfo();

		// 3.把错误的堆栈信息 获取出来
		String errorinfo = getErrorInfo(arg1);

		String sdPath = getSDPath();
		if (!TextUtils.isEmpty(sdPath)) {
			String packageName = context.getPackageName();
			sdPath = sdPath + "/"+packageName;
			File file = new File(sdPath);
			if (file.exists()) {
			} else {
				file.mkdirs();
			}
			sdPath = sdPath + "/log.txt";
			file = new File(sdPath);
			try {
				if(file.exists()){
					long lastModified = file.lastModified();
					Date date2 = new Date();
					long time = date2.getTime();
					if(time-lastModified>1*24*60*60*1000){//1天前的log删除
						file.delete();
					}
				}
				RandomAccessFile randomAccessFile = new RandomAccessFile(file,
						"rw");
				randomAccessFile.seek(randomAccessFile.length());
				String format = dataFormat.format(new Date());
				randomAccessFile.writeChars(format
						+ "----------------start----------------\r\n");
				versioninfo = "版本信息:" + versioninfo + "\r\n";
				byte[] versionArr = versioninfo.getBytes("utf-8");
//				mobileInfo = "手机信息:" + mobileInfo + "\r\n";
//				byte[] mobileArr = mobileInfo.getBytes("utf-8");

				String erro = "错误日志:" + errorinfo + "\r\n";
				byte[] erroArr = erro.getBytes("utf-8");
				randomAccessFile.write(versionArr);
//				randomAccessFile.write(mobileArr);
				randomAccessFile.write(erroArr);
				randomAccessFile
						.writeChars("-----------------------end--------------------\r\n");
				randomAccessFile.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// 4.把所有的信息 还有信息对应的时间 提交到服务器

		// 干掉当前的程序
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * 获取错误的信息
	 * 
	 * @param arg1
	 * @return
	 */
	private String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}

	/**
	 * 获取手机的硬件信息
	 * 
	 * @return
	 */
	private String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		// 通过反射获取系统的硬件信息
		try {

			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				// 暴力反射 ,获取私有的信息 S
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 获取手机的版本信息
	 * 
	 * @return
	 */
	private String getVersionInfo() {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

}
