package com.jtv.locationwork.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

/**
 * @author: zn E-mail:zhangn@jtv.com.cn
 * @version:2015-2-12 类说明:bitmap操作类
 */

public class BitmapUtils {
	/**
	 * 获取图片文件的信息，是否旋转了90度，如果是则反转
	 * @param bitmap 需要旋转的图片
	 * @param path   图片的路径
	 */
	public static Bitmap reviewPicRotate(Bitmap bitmap,String path){
		int degree = getPicRotate(path);
		if(degree!=0){
			Matrix m = new Matrix();  
			int width = bitmap.getWidth();  
			int height = bitmap.getHeight();  
			m.setRotate(degree); // 旋转angle度   
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,m, true);// ä»Žæ–°ç”Ÿæˆ�å›¾ç‰‡  
		}
		return bitmap;
	}
	
	/**
	 * 读取图片文件旋转的角度
	 * @param path 图片绝对路径
	 * @return 图片旋转的角度
	 */
	public static int getPicRotate(String path) {
		int degree  = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	/**
	 * 获取缩略图路径
	 * 
	 * @author:zn
	 * @version:2015-2-16
	 * @param oldPath
	 * @param bitmapMaxWidth
	 * @param photoPath
	 * @return
	 * @throws Exception
	 */
	public static String getThumbUploadPath(String oldPath, int bitmapMaxWidth,
			String photoPath) throws Exception {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(oldPath, options);
		int height = options.outHeight;
		int width = options.outWidth;
		
		if(width==0){
			width=bitmapMaxWidth;
		}
		int reqHeight = 0;
		int reqWidth = bitmapMaxWidth;
		reqHeight = (reqWidth * height) / width;
		// 在内存中创建bitmap对象，这个对象按照缩放大小创建的
		options.inSampleSize = calculateInSampleSize(options, bitmapMaxWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
		// Log.e("asdasdas", "reqWidth->"+reqWidth+"---reqHeight->"+reqHeight);
		Bitmap bbb = compressImage(Bitmap.createScaledBitmap(bitmap,
				bitmapMaxWidth, reqHeight, false));
		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
		// Date());
		return BitmapUtils.saveImg(bbb, photoPath);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			options -= 10;// 每次都减少10
			if(options<=0){
				break;
			}
			baos.reset();// 重置baos即清空baos
			
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 
	 * @param b
	 *            Bitmap
	 * @return 图片存储的位置
	 * @throws FileNotFoundException
	 */
	public static String saveImg(Bitmap b, String photoPath) throws Exception {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "test/headImg/";
		File mediaFile = new File(photoPath);
		if (mediaFile.exists()) {
			mediaFile.delete();

		}
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		mediaFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(mediaFile);
		b.compress(Bitmap.CompressFormat.PNG, 100, fos);
		fos.flush();
		fos.close();
		b.recycle();
		b = null;
		System.gc();
		return mediaFile.getPath();
	}

	/**
	 * 有损压缩图片大小
	 * 
	 * @param filepath
	 * @param quality 0-100
	 * @return Bitmap
	 * @throws IOException
	 */
	public static Bitmap compressImage(String filepath, int quality)
			throws IOException {
		// 上传图片最大宽高
		int IMAGE_MAX_WIDTH = 600;
		int IMAGE_MAX_HEIGHT = 400;

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
//		压缩图片可以调用
//		bitMap = resizeBitmap(bitMap, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT);
		if (bitMap != null) {
			BufferedOutputStream bos = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(filepath));
				bitMap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
			} catch (IOException ioe) {
				Log.e("compress image", ioe.getMessage());
			}
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
			bitmap = Bitmap.createScaledBitmap(bitmap,newWidth,
					newHeight,true);
		}

		return bitmap;
	}
	
	/**
	 * 得到一个压缩后的图片
	 * @param srcPath
	 * @return
	 */
	public static Bitmap getimage(String srcPath)
	{
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww)
		{// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		}
		else if (w < h && h > hh)
		{// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = 1;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		if (bitmap == null)
		{
			return null;
		}
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}
	
	
}
