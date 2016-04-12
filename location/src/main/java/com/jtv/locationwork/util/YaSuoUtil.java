package com.jtv.locationwork.util;

public class YaSuoUtil {
	public static String ctrlCameraImage(String mroot_image) {
		if (false) {
			return mroot_image;
		}
		try {
			int quil = SpUtiles.BaseInfo.mbasepre.getInt(CoustomKey.IMAGE_QUALITY, 50);
			int pei = SpUtiles.BaseInfo.mbasepre.getInt(CoustomKey.IMAGE_PIXEI, 480);
			BitmapUtils.compressImage(mroot_image, quil);// 对图片进行压缩处理
			// iv_photo.setImageBitmap(bitmap);
			mroot_image = BitmapUtils.getThumbUploadPath(mroot_image, pei, mroot_image);
			// saveResultFlag = true;
			return mroot_image;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return mroot_image;
	}

//	public static void transImage(String fromFile, String toFile, int width, int height, int quality) {
//		try {
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true;
//			Bitmap bitmap = BitmapFactory.decodeFile(fromFile, options);
////			Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
//			int bitmapWidth = options.outWidth;
//			int bitmapHeight = options.outHeight;
//			// 缩放图片的尺寸
//			int scaleWidth = (int) width / bitmapWidth;
//			int scaleHeight = (int) height / bitmapHeight;
//			Matrix matrix = new Matrix();
//			matrix.postScale(scaleWidth, scaleHeight);
//			options.inSampleSize=scaleWidth;
//			options.inJustDecodeBounds = false;
//			
//			  bitmap = BitmapFactory.decodeFile(fromFile, options);
//			// 产生缩放后的Bitmap对象
////			Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
//			// save file
//			File myCaptureFile = new File(toFile);
//			FileOutputStream out = new FileOutputStream(myCaptureFile);
//			if (bitmap.compress(Bitmap.CompressFormat.PNG, quality, out)) {
//				out.flush();
//				out.close();
//			}
//			if (!bitmap.isRecycled()) {
//				bitmap.recycle();// 记得释放资源，否则会内存溢出
//			}
//			if (!bitmap.isRecycled()) {
//				bitmap.recycle();
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}
}
