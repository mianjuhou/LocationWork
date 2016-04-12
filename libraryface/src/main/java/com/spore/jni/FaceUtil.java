package com.spore.jni;

import java.io.File;

import android.content.Context;

public class FaceUtil {

	private static Context con;

	public static void initContext(Context con) {
		FaceUtil.con = con;
	}

	/**
	 * 是否加载默认的so
	 * 
	 * @return
	 */
	public static boolean isLoadDefaultSO() {

		File filesDir = con.getFilesDir();
		File parentFile = filesDir.getParentFile();

		File dir = new File(parentFile.getAbsolutePath() + File.separator + "lib");
		File[] listFiles = dir.listFiles();

		for (int i = 0; i < listFiles.length; i++) {

			File file = listFiles[i];

			String name = file.getName();
			if (name.equals("libfsdk.so")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 动态的加载so
	 * 
	 * @return
	 */
	public static String getDynamicSo() {
		File filesDir = con.getFilesDir();
		String path = filesDir.getAbsolutePath() + File.separator + "libfsdk.so";
		return path;
	}
}
