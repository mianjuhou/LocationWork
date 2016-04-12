package com.jtv.dbentity.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 通用工具类
 * @author 殷小春
 *
 */
public class CommTool {
	/**
	 * 窗体宽度
	 */
	private static int window_width = 0;
	/**
	 * 窗体高度
	 */
	private static int window_height = 0;
	/**
	 * 是否全屏运行
	 */
	private static boolean fullScreen = false;
	/**
	 * 是否高亮显示未上传的数据
	 */
	private static boolean hightLightUnUploadData = true;
	
	/**
	 * 手机IMEI号
	 */
	private static String telPhoneDevice = "";
	/**
	 * 手机IMEI号
	 */
	private static String deviceIdOwener = "";
	
	/**
	 * 
	* @Title: getTelPhoneDevice 
	* @Description: TODO(获取手机device) 
	* @param @return 
	* @return String    返回类型 
	* @date 2014-3-20 上午11:07:04 
	* @throws
	 */
	public static String getTelPhoneDevice(){
		return telPhoneDevice;
	}
	/**
	 * 
	* @Title: getDeviceIdOwener 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @return 
	* @version 1.01
	* @return String    返回类型 
	* @date 2014-4-22 上午9:26:25 
	* @throws
	 */
	public static String getDeviceIdOwener() {
		return deviceIdOwener;
	}

	/**
	 * 设置窗体宽度
	 * @param width 宽度
	 */
	public static void setWindow_width(int width){
		window_width = width;
	}
	/**
	 * 得到窗体宽度
	 * @return int宽度
	 */
	public static int getWindow_width(){
		return window_width;
	}
	/**
	 * 设置窗体高度
	 * @param height 高度
	 */
	public static void setWindow_height(int height){
		window_height = height;
	}
	/**
	 * 得到窗体高度
	 * @return int高度
	 */
	public static int getWindow_height(){
		return window_height;
	}
	
	public static boolean isFullScreen() {
		return fullScreen;
	}
	
	private static void setFullScreen(boolean _fullScreen) {
		fullScreen = _fullScreen;
	}
	/**
	 * 得到R中资源的ID
	 * <p>因打包到jar中后，访问其中资源的方式不一样，导致jar中的R文件资源定义无法使用，故通过该方法获取正确的资源</p>
	 * @param res_name 资源名
	 * @param type 资源类型
	 * @param res 资源
	 * @param package_name 包名
	 * @return 资源ID
	 */
	public static int getRID(String res_name, String type, Resources res, String package_name){
		return res.getIdentifier(res_name,type,package_name);
	}
	/**
	 * 
	* @Title: getRID 
	* @Description: TODO(获取资源文件的内容) 
	* @param @param res_name
	* @param @param type
	* @param @param ctx
	* @param @return 
	* @return int    返回类型 
	* @date 2014-3-12 上午8:48:05 
	* @throws
	 */
	public static int getRID(String res_name, String type,Context ctx){
		Resources res = ctx.getResources();
		String package_name = ctx.getPackageName();
		return res.getIdentifier(res_name,type,package_name);
	}
	

	@SuppressWarnings("unchecked")
	public static <T extends View> T getViewByIdA(Activity aty,int id) {
		return (T) aty.findViewById(id);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends View> T getViewById(View view,int id) {
		return (T) view.findViewById(id);
	}
	
	
	public static <T extends View> T getViewByName(View view,String viewName,Context ctx){
		int id = getRID(viewName, "id",ctx);
		return getViewById(view,id);
	}
	
	public static void setFullScreen(Activity c){
		setFullScreen(c, fullScreen);
	}
	
	/**
	 * 设置页面全屏显示
	 * @param c
	 * @param _fullscreen
	 */
	public static void setFullScreen(Activity c, boolean _fullscreen){
		try{
			if (c!=null && _fullscreen){			
				c.requestWindowFeature(Window.FEATURE_NO_TITLE);
				c.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);			
			}else{				
				c.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);			
			}
		}catch(Exception e){}
	}
	
	public static boolean isHightLightUnUploadData() {
		return hightLightUnUploadData;
	}
	public static void setHightLightUnUploadData(boolean hightLightUnUploadData) {
		CommTool.hightLightUnUploadData = hightLightUnUploadData;
	}
	/**
	 * 
	* @Title: downLast 
	* @Description: 下載最新的系统
	* @param  
	* @version 1.01
	* @return void    返回类型 
	* @date 2014-4-2 下午3:24:15 
	* @throws
	 */
	public void downLast() {

//		boolean online = false;
//		online = CommDataAdapter.testConnection();
//
//		if ((online) && (CommonUpdater.needUpdate(this))) {
//			Intent intent = new Intent();
//			intent.setClass(this, CommonUpdater.class);
//			startActivity(intent);
//		} else {
//			Toast.makeText(this, "现在的版本是最新的！！", Toast.LENGTH_LONG).show();
//		}
	}

}
