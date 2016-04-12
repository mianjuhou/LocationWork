package com.jtv.locationwork.util;
import android.graphics.Bitmap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
public class UniversalImageLoadTool {

	private static ImageLoader imageLoader = ImageLoader.getInstance();
	
	public static ImageLoader getImageLoader(){
		return imageLoader;
	}
	public static boolean checkImageLoader(){
		return imageLoader.isInited();
	}
	
	public static void disPlay(String uri, ImageAware imageAware,int default_pic){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(default_pic)
		.showImageForEmptyUri(default_pic)
		.showImageOnFail(default_pic)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new SimpleBitmapDisplayer())
		.build();

		imageLoader.displayImage(uri, imageAware, options);
	}
	
	public static void clear(){
		imageLoader.clearMemoryCache();		
		imageLoader.clearDiscCache();
	}
	
	public static void resume(){
		imageLoader.resume();
	}
	/**
	 * æš‚å�œåŠ è½½
	 */
	public static void pause(){
		imageLoader.pause();
	}
	/**
	 * å�œæ­¢åŠ è½½
	 */
	public static void stop(){
		imageLoader.stop();
	}
	/**
	 * é”€æ¯�åŠ è½½
	 */
	public static void destroy() {
		imageLoader.destroy();
	}
}
