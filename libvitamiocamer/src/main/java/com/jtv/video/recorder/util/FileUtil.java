package com.jtv.video.recorder.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

public class FileUtil {

	/**
	 * 删除文件（包括子文件）
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		if (file.isDirectory()) {
			File[] childs = file.listFiles();
			for (File file2 : childs)
				delete(file2);
		}
		file.delete();
	}

	/**
	 * 获得文件后缀名
	 * 
	 * @param myFile
	 * @return
	 */
	public static String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
	}

	/**
	 * 获取文件名.来自http
	 * 
	 * @param pathServer
	 * @return
	 */
	public static String getServerFileName(String path) {
		int start = path.lastIndexOf("/") + 1;
		return path.substring(start);
	}

	/**
	 * 获取sd卡路径
	 * 
	 * @return null 代表没有外置存储卡
	 */
	public static String getSDPath() {
		boolean sdCardExist = android.os.Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()); // 判断sd卡是否存在
		String filepath = null;
		if (sdCardExist) {
			filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return filepath;
	}

	/*
	 * 通过递归得到某一路径下所有的目录及其文件
	 */
	public static void forFiles(String filePath, FileCallBackListerner fileBack) {
		File root = new File(filePath);
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				forFiles(file.getAbsolutePath(), fileBack);
				if (fileBack != null) {
					fileBack.fileDirector(file);
				}
			} else {
				if (fileBack != null) {
					fileBack.file(file);
				}
			}
		}
	}

	// 复制文件
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	// 复制文件夹
	public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}

	public interface FileCallBackListerner {
		public void fileDirector(File file);

		public void file(File file);
	}
}
