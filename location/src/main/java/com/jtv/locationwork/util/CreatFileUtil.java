package com.jtv.locationwork.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

public class CreatFileUtil {

	// 文件跟目录/mnt/sdcard/
	private static String root = "";

	// 项目更目录com.location.jtv
	private static String rootFile = "";

	// 项目完整路径/mnt/sdcard/com.location.jtv
	private static String buildPath = "";

	// 文件夹名字
	private static String mImageName = "jtv_image";

	private static String mVideoName = "jtv_video";

	private static String mImageMoring = "jtv_image_morning";

	public static String mImageNight = "jtv_image_night";

	public static String mVideoNight = "jtv_video_night";

	public static String mVideoMorning = "jtv_video_morning";

	public static String mDownLoad = "jtv_download";

	public static String mDCEIM = "jtv_dcim";

	private static String mAUDIO = "jtv_audio";

	private static String mDoc = "jtv_doc";

	public static File mRootFile = null;

	/**
	 * 获取项目路径跟目录
	 * 
	 * @param con
	 * @return
	 */
	public static File getRootFile(Context con) {

		if ("".equals(root) || root == null)
			root = getStorageRoot(con);

		if ("".equals(rootFile) || rootFile == null) {
			rootFile = con.getPackageName();
		}

		if ("".equals(buildPath) || buildPath == null)
			buildPath = root + File.separator + rootFile;

		if (mRootFile == null) {
			mRootFile = new File(buildPath);
			if (!mRootFile.exists()) {
				mRootFile.mkdirs();
			}
		}

		return mRootFile;
	}

	public static File getNightImage(Context con) {

		return getDirFile(con, mImageNight);
	}

	public static File getNightVideo(Context con) {

		return getDirFile(con, mVideoNight);
	}

	public static File getMorningVideo(Context con) {

		return getDirFile(con, mVideoMorning);
	}

	public static File getMorningImage(Context con) {

		return getDirFile(con, mImageMoring);
	}

	public static File getImage(Context con) {

		return getDirFile(con, mImageName);
	}

	public static File getVideo(Context con) {

		return getDirFile(con, mVideoName);
	}

	public static File getDownLoad(Context con) {
		return getDirFile(con, mDownLoad);
	}

	public static File getDCIM(Context con) {
		return getDirFile(con, mDCEIM);
	}

	/**
	 * 获取项目路径下的目录
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	public static File getDirFile(Context con, String name) {

		File mRoot = getRootFile(con);
		if (mRoot == null || "".equals(mRoot.getAbsoluteFile()))
			return null;

		String mPath = mRoot.getAbsolutePath() + File.separator + name;

		File mFile = new File(mPath);
		if (!mFile.exists()) {
			mFile.mkdirs();
		}

		return mFile;

	}

	public static String getFilePath(File file) {
		if (file != null) {
			return file.getAbsolutePath();
		}

		return null;
	}

	public static File getAudioFilePath(Context con) {
		return getDirFile(con, mAUDIO);
	}

	public static File makeFile(File file, String name) {
		return new File(file.getAbsolutePath() + File.separator + name);
	}

	public static File getDocFilePath(Context con) {
		return getDirFile(con, mDoc);
	}

	/**
	 * 
	 * @return sd卡路径
	 */
	public static String getStorageRoot(Context con) {
		String sdRoot = getSDRoot(con);
		if (TextUtils.isEmpty(sdRoot)) {
			sdRoot = getDataRoot(con);
		}
		return sdRoot;
	}

	public static String getSDRoot(Context con) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			File mRoot = Environment.getExternalStorageDirectory();
			return mRoot.getAbsolutePath();
		}
		return null;
	}

	// 获取系统自带的路径
	public static String getDataRoot(Context con) {
		File filesDir = con.getFilesDir();
		return filesDir.getAbsolutePath();
	}

	/**
	 * 去除跟路径 mnt/sdcard/
	 * 
	 * @param con
	 * @param name
	 * @return
	 */
	public static String getUnlessRoot(Context con, String name) {
		if ("".equals(rootFile) || rootFile == null) {
			rootFile = con.getPackageName();
		}
		return rootFile + File.separator + name;

	}

}
