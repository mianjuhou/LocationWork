package com.jtv.locationwork.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @author: zn E-mail:zhangn@jtv.com.cn
 * @version:2015-2-4 类说明：图片管理类
 */

public class ImageUtil {

	public static ImageUtil imageUtil = null;

	public static ImageUtil getInstance() {
		if (imageUtil == null) {
			imageUtil = new ImageUtil();
			return imageUtil;
		}
		return imageUtil;
	}

	/**
	 * 创建文件夹
	 * 
	 * @author:zn
	 * @version:2015-2-12
	 * @param path
	 */
	public void createMkdir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 根据时间命名图片的名称
	 * 
	 * @author:zn
	 * @version:2015-2-4
	 * @param date
	 * @return
	 */
	public static String getPhotoFilename(String workOrderId, Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		return String.format("%s-%s%s", workOrderId, dateFormat.format(date),
				".jpg");
	}
	
	/**
	 * 根据时间命名视频的名称
	 * 
	 * @author:zn
	 * @version:2015-2-4
	 * @param date
	 * @return
	 */
	public static String getVideoFilename(String workOrderId, Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		return String.format("%s-%s%s", workOrderId, dateFormat.format(date),
				".mp4");
	}

	/**
	 * 保存图片在SD卡中
	 * 
	 * @author:zn
	 * @version:2015-2-6
	 * @param bm
	 * @param filePath
	 * @param imagePath
	 * @throws IOException
	 */
	public static boolean saveImageInSD(Bitmap bm, String filePath,
			String imagePath) {
		boolean flag = false;
		try {
			File dirFile = new File(filePath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			File myCaptureFile = new File(imagePath);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取SDCard中某个目录下图片路径集合
	 * 
	 * @author:zn
	 * @version:2015-2-6
	 * @param strPath
	 *            (保存在本地图片的路径)
	 * @return(返回根目录下的所有图片地址)
	 */
	public List<String> getSDImagePathList(final String strPath) {
		List<String> list = new ArrayList<String>();
		File file = new File(strPath);
		File[] allfiles = file.listFiles();
		if (allfiles == null) {
			return null;
		}
		for (int k = 0; k < allfiles.length; k++) {
			final File fi = allfiles[k];
			if (fi.isFile()) {
				int idx = fi.getPath().lastIndexOf(".");
				if (idx <= 0) {
					continue;
				}
				String suffix = fi.getPath().substring(idx);
				if (suffix.toLowerCase().equals(".jpg")
						|| suffix.toLowerCase().equals(".jpeg")
						|| suffix.toLowerCase().equals(".bmp")
						|| suffix.toLowerCase().equals(".png")
						|| suffix.toLowerCase().equals(".gif")) {
					list.add(fi.getPath());
				}
			}
		}
		return list;
	}

	/**
	 * 获取SD卡中指定目录下的图片(模糊检索以fileName开头的所有图片)
	 * 
	 * @author:zn
	 * @version:2015-2-6
	 * @param strPath
	 *            (存放图片的根目录)
	 * @param fileName
	 *            (以filename开头的图片名称)
	 * @return(返回bitmap列表)
	 */
	public List<Bitmap> getSDImageBitmap(String strPath, String fileName) {
		List<String> imagePathList = getSDImagePathList(strPath);
		List<Bitmap> imageBitmapList = new ArrayList<Bitmap>();
		for (int i = 0; i < imagePathList.size(); i++) {
			String filepath = imagePathList.get(i);
			if (filepath.contains(fileName)) {
				File file = new File(filepath);
				if (file.exists()) {
					Bitmap bm = BitmapFactory.decodeFile(filepath);
					imageBitmapList.add(bm);
				}
			}
		}
		return imageBitmapList;
	}

	/**
	 * 获取该路径下所有图片
	 * 
	 * @author:zn
	 * @version:2015-2-16
	 * @param strPath
	 * @return
	 */
	public List<Bitmap> getSDPathAllImage(String strPath) {
		List<String> imagePathList = getSDImagePathList(strPath);
		List<Bitmap> imageBitmapList = new ArrayList<Bitmap>();
		for (int i = 0; i < imagePathList.size(); i++) {
			String filepath = imagePathList.get(i);
			File file = new File(filepath);
			if (file.exists()) {
				Bitmap bm = BitmapFactory.decodeFile(filepath);
				imageBitmapList.add(bm);
			}
		}
		return imageBitmapList;
	}

	/**
	 * 有损压缩图片大小
	 * 
	 * @param filepath
	 * @param quality
	 * @return Bitmap
	 * @throws IOException
	 */
	public static Bitmap compressImage(String filepath, int quality)
			throws IOException {
		// 上传图片最大宽高
		int IMAGE_MAX_WIDTH = 1000;
		int IMAGE_MAX_HEIGHT = 800;

		int scale = 1;
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		Bitmap bitMap = BitmapFactory.decodeFile(filepath, o);
		if (o.outWidth > IMAGE_MAX_WIDTH || o.outHeight > IMAGE_MAX_HEIGHT) {
			scale = (int) Math.pow(
					2.0,
					(int) Math.round(Math.log(IMAGE_MAX_WIDTH
							/ (double) Math.max(o.outHeight, o.outWidth))
							/ Math.log(0.5)));
		}
		Log.d("image-scale", scale + " scale");

		o.inJustDecodeBounds = false;
		o.inSampleSize = scale;
		bitMap = BitmapFactory.decodeFile(filepath, o);
		bitMap = resizeBitmap(bitMap, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT);

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filepath));
			bitMap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
		} catch (IOException ioe) {
			Log.e("compress image", ioe.getMessage());
		}
		return bitMap;
	}

	/**
	 * 保持长宽比缩小Bitmap
	 * 
	 * @param bitmap
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {

		int originWidth = bitmap.getWidth();
		int originHeight = bitmap.getHeight();

		// no need to resize
		if (originWidth < maxWidth && originHeight < maxHeight) {
			return bitmap;
		}

		int newWidth = originWidth;
		int newHeight = originHeight;

		// 若图片过宽, 则保持长宽比缩放图片
		if (originWidth > maxWidth) {
			newWidth = maxWidth;

			double i = originWidth * 1.0 / maxWidth;
			newHeight = (int) Math.floor(originHeight / i);

			bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight,
					true);
		}

		// 若图片过长, 则从中部截取
		if (newHeight > maxHeight) {
			newHeight = maxHeight;

			int half_diff = (int) ((originHeight - maxHeight) / 2.0);
			bitmap = Bitmap.createBitmap(bitmap, 0, half_diff, newWidth,
					newHeight);
		}

		return bitmap;
	}

	/**
	 * 删除文件
	 * 
	 * @author:zn
	 * @version:2015-2-11
	 * @param filePath
	 *            (文件路径)
	 * @return
	 */
	public boolean deleteImageFile(String filePath) {
		File delFile = new File(filePath);
		if (delFile.exists()) {
			delFile.delete();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取根目录下的所有图片，然后根据名称获取图片的路径
	 * 
	 * @author:zn
	 * @version:2015-2-12
	 * @param imgRootPath
	 *            (保存图片的路径)
	 * @param photoName
	 *            (以这个名称开头的图片名称)
	 * @return 返回过滤完成的图片路径
	 */
	public List<String> getPathListByName(final String imgRootPath,
			String photoName) {
		List<String> list = new ArrayList<String>();
		File file = new File(imgRootPath);
		File[] allfiles = file.listFiles();
		if (allfiles == null) {
			return null;
		}
		for (int k = 0; k < allfiles.length; k++) {
			final File fi = allfiles[k];
			if (fi.isFile()) {
				int idx = fi.getPath().lastIndexOf(".");
				if (idx <= 0) {
					continue;
				}
				String suffix = fi.getPath().substring(idx);
				boolean isImgFlag = suffix.toLowerCase().equals(".jpg")
						|| suffix.toLowerCase().equals(".jpeg")
						|| suffix.toLowerCase().equals(".bmp")
						|| suffix.toLowerCase().equals(".png")
						|| suffix.toLowerCase().equals(".gif");
				if (isImgFlag && fi.getPath().contains(photoName)) {
					list.add(fi.getPath());
				}
			}
		}
		return list;
	}

	/**
	 * 通过路径列表获取图片
	 * 
	 * @author:zn
	 * @version:2015-2-12
	 * @param list
	 *            (图片的路径)
	 * @return
	 */
	public List<Bitmap> getBitMapListByPath(List<String> list) {
		List<Bitmap> imageBitmapList = new ArrayList<Bitmap>();
		for (int i = 0; list != null && i < list.size(); i++) {
			String filepath = list.get(i);
			File file = new File(filepath);
			if (file.exists()) {
				Bitmap bm = BitmapFactory.decodeFile(filepath);
				imageBitmapList.add(bm);
			}
		}
		return imageBitmapList;
	}

	/**
	 * 获取某一个文件下的jpg图片,对应路径
	 * 
	 * @author:lgs
	 * @param path
	 * @return Map
	 */
	public static Map<String, Bitmap> getPathPicureMap(String path) {
		File file = new File(path);
		Bitmap decodeFile = null;
		Map<String, Bitmap> list = new HashMap<String, Bitmap>();
		if (file != null) {
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				for (int i = 0; i < listFiles.length; i++) {
					if (listFiles[i].isFile()) {// 是否是文件
						boolean endsWith = listFiles[i].getName().endsWith(
								"jpg");
						if (endsWith) {
							String pathPicktrue = listFiles[i]
									.getAbsolutePath();
							BitmapFactory.Options newOpts = new BitmapFactory.Options();
							newOpts.inJustDecodeBounds = true;
							decodeFile = BitmapFactory.decodeFile(pathPicktrue);
							newOpts.inJustDecodeBounds = false;
							int w = newOpts.outWidth;
							int h = newOpts.outHeight;
							float hh = 800f;//
							float ww = 480f;//
							int be = 1;
							if (w > h && w > ww) {
								be = (int) (newOpts.outWidth / ww);
							} else if (w < h && h > hh) {
								be = (int) (newOpts.outHeight / hh);
							}
							if (be <= 0)
								be = 1;
							newOpts.inSampleSize = be;// 设置采样率
							newOpts.inPurgeable = true;// 同时设置才会有效
							newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
							decodeFile = BitmapFactory.decodeFile(pathPicktrue,
									newOpts);
							if (decodeFile != null) {
								list.put(pathPicktrue, decodeFile);
							}
						}
					}
				}
			}
		}
		return list;

	}
	
}
