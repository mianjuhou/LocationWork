package com.jtv.locationwork.httputil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.locationwork.util.Base64UtilCst;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.CreatFileUtil;
import com.jtv.locationwork.util.PDADeviceInfoService;
import com.jtv.locationwork.util.SpUtiles;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

/**
 * 访问网络的工具类
 * <p>
 * 
 * @author 更生
 * @version 2016年3月8日
 */
public class TrackAPI {

	public static TrackAPI trackerAPI = null;

	public static final String TAG = "TrackAPI";

	/**
	 * 上传文件的接口
	 * 
	 * @param attid
	 *            设备id
	 * @param personid
	 *            当前是谁
	 * @param upfile
	 *            上传的文件
	 * @return返回值 jsonObj.put("status", true); jsonObj.put("errmsg", "上传成功"); 或者
	 *            jsonObj.put("status", false); jsonObj.put("errmsg", "上传失败");
	 * @throws JSONException
	 * @throws AppError
	 */
	public static void uploadFile(ObserverCallBack back, int method, File upfile) {
		RequestParmter body = new RequestParmter();
		body.addBodyParmter("personid", GlobalApplication.mBase64Lead);
		body.addBodyParmter("attid", GlobalApplication.attid);
		body.addBodyParmter("type", "0");
		body.addBodyParmter("upfile", upfile);
		body.addBodyParmter("siteid", GlobalApplication.siteid);
		OkHttpUtil.post(HttpApi.Http_Interface_uploadFile, body, back, method, null);
	}

	/**
	 * 获取一个服务端的需要下载的文件
	 * 
	 * @param personid
	 *            当前是谁的人员
	 * @return [{"depotfileid":3,"src":
	 *         "http://118.26.65.36:8080/operation/depotupload/caad4811-680d-454d-a965-55b088f785ee.xml"
	 *         }]
	 * 
	 *         depotfileid 设备唯一码
	 */
	public static String requestFileForServer(String url, String personid) {
		Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		personid = Base64UtilCst.encodeUrl(personid);
		paramsMap.put("personid", personid);
		try {
			return OkHttpUtil.getStringFromServer(url, paramsMap);
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * 请求一个服务端
	 * 
	 * @param url
	 * @param paramsMap
	 * @return
	 */
	public static String requestServer(String url, Map<String, String> paramsMap) {
		try {
			return OkHttpUtil.getStringFromServer(url, paramsMap);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		// return Base64UtilCst.post(url, paramsMap, -1);
	}

	/**
	 * 上传工单号到服务器中
	 * 
	 * @param woNum
	 * @param imageUrl
	 * @param geox
	 * @param geoy
	 * @param context
	 * @param personid
	 * @return
	 * @throws JSONException
	 * @throws AppError
	 *             0 代表错误 1 代表成功
	 * 
	 *             送115 dajiu，daxiu
	 */
	public static String uploadImageTest(String woNum, String imageUrl, String geox, String geoy, Context context, String personid64, String number)
			throws JSONException, Exception {

		// Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		//
		// String image64 = Base64UtilCst.GetImageStr(imageUrl);
		//
		// paramsMap.put("wonum", woNum);
		// paramsMap.put("attid", GlobalApplication.attid);
		// paramsMap.put("picbase", image64);
		// paramsMap.put("personid", personid64);
		// paramsMap.put("geox", geox);
		// paramsMap.put("geoy", geoy);
		// paramsMap.put("number", number);
		// paramsMap.put("siteid", GlobalApplication.siteid);
		// return OkHttpUtil.post(HttpApi.uploadImageTest, paramsMap);

		RequestParmter paramsMap = uploadimage(woNum, GlobalApplication.attid, personid64, geox, geoy, GlobalApplication.siteid, imageUrl, number);

		return OkHttpUtil.post(HttpApi.uploadImage, paramsMap);

	}

	public static String uploadImageTestForLink(String woNum, String imageUrl, String geox, String geoy, String deviceid, String personid64,
			String type, String img, String table, String number) {
		RequestParmter paramsMap = uploadimage(woNum, GlobalApplication.attid, personid64, geox, geoy, GlobalApplication.siteid, imageUrl, number);
		paramsMap.put("table", table);
		paramsMap.put("type", type);
		paramsMap.put("img", img);
		return OkHttpUtil.post(HttpApi.uploadImage, paramsMap);
	}

	private static RequestParmter uploadimage(String wonum, String attid, String personid, String geox, String geoy, String siteid, String pic,
			String number) {
		RequestParmter paramsMap = new RequestParmter();
		paramsMap.put("wonum", wonum);
		paramsMap.put("attid", attid);
		paramsMap.put("personid", personid);
		paramsMap.put("geox", geox);
		paramsMap.put("geoy", geoy);
		paramsMap.put("number", number);
		paramsMap.put("siteid", siteid);
		paramsMap.put("pic", new File(pic));
		return paramsMap;
	}

	/**
	 * 上传图片分级上传
	 * 
	 * @param woNum
	 * @param imageUrl
	 * @param geox
	 * @param geoy
	 * @param context
	 * @param type
	 *            这个是更目录
	 * @param img
	 *            这个是图片的条目详情参考分级
	 * @param personid
	 * @param table
	 *            代表那张表
	 * @return 0 代表错误 1 代表成功
	 */
	// public static String uploadImageTestForLink(String woNum, String
	// imageUrl, String geox, String geoy,
	// String deviceid, String personid64, String type, String img, String
	// table, String number) {
	// Log.i("Parmters", "type:" + type + " img:" + img + " personid:" +
	// personid64 + " woNum:" + woNum
	// + " table:" + table);
	//
	// Map<String, String> paramsMap = new LinkedHashMap<String, String>();
	//
	// String image64 = Base64UtilCst.GetImageStr(imageUrl);
	//
	// paramsMap.put("wonum", woNum);
	// paramsMap.put("attid", deviceid);
	// paramsMap.put("picbase", image64);
	// paramsMap.put("personid", personid64);
	// paramsMap.put("geox", geox);
	// paramsMap.put("geoy", geoy);
	// paramsMap.put("table", table);
	// paramsMap.put("number", number);
	// paramsMap.put("siteid", GlobalApplication.siteid);
	// paramsMap.put("type", type);
	// paramsMap.put("img", img);
	//
	// try {
	// return OkHttpUtil.post(HttpApi.uploadImageTest, paramsMap);
	// } catch (IOException e) {
	// e.printStackTrace();
	// return 0 + "";
	// }
	// }

	/**
	 * 提交gps到服务器,连绵不断的请求
	 * 
	 * @param context
	 * @param wonum
	 *            工单号
	 * @param geox
	 *            纬度
	 * @param geoy
	 *            经度
	 * @param type
	 *            当前是上道还是下道上道 1下道2
	 * 
	 * @return
	 */
	public static void saveGpsinfoTest(Context context, String wonum, String geox, String geoy, String type, String lead) {
		String url = HttpApi.saveGpsinfoTest + "?method=saveGpsinfo&iswo=" + type + "&siteid=" + GlobalApplication.siteid + "&geox=" + geox
				+ "&geoy=" + geoy + "&wonum=" + wonum + "&attid=" + GlobalApplication.attid + "&personid=" + lead;
		AnsynHttpRequest.requestGet(url, null, 1, context, false);
	}

	/**
	 * 从服务器端获取人脸数据
	 * 
	 * @param crewid
	 * @return
	 */
	public static String getFaceInfos(String crewid) {
		Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		paramsMap.put("crewid", crewid);
		return requestServer(HttpApi.Http_Interface_getFaceData, paramsMap);
	}

	/**
	 * 从服务器端获取人脸数据
	 * 
	 * @param crewid
	 * @return
	 */
	public static String getFaceInfos(String crewid, String lastupdatetime) {
		Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		paramsMap.put("crewid", crewid);

		paramsMap.put("lastupdate", lastupdatetime);
		if (TextUtils.isEmpty(lastupdatetime)) {
			lastupdatetime = "0";
		}
		return requestServer(HttpApi.Http_Interface_getFaceData, paramsMap);
	}

	public static void uploadFace(Context con, ObserverCallBack back, HashMap<String, String> map) {
		AnsynHttpRequest.requestPost(HttpApi.Http_Interface_uploadFace, back, MethodApi.HTTP_UPLOAD_FACE, con, map, false);
	}

	/**
	 * 获取动态数据
	 * 
	 * @param con
	 *            &tablename=Runway
	 * @param callBack
	 */
	public static void getWolistField(Context con, ObserverCallBack callBack, String siteid) {
		String url = HttpApi.Http_Interface_mapping_fieldwonum + "&siteid=" + siteid;
		AnsynHttpRequest.requestGet(url, callBack, MethodApi.HTTP_REQUEST_WOLISTFAILED, con, false);
	}

	/**
	 * 获取所有的工单
	 * 
	 * @param con
	 * @param callBack
	 * @param lead
	 */
	public static void getALLWolist(Context con, ObserverCallBack callBack, String lead, String time, String siteid, Object obj) {

		String url = HttpApi.Http_interface_get_wonumlist_bjd + "&personid=" + lead + "&schedstartdate=" + time + "&siteid=" + siteid;
		AnsynHttpRequest.requestGetOrPostE(AnsynHttpRequest.GET, con, url, null, callBack, AnsynHttpRequest.getRequestQueue(con),
				MethodApi.HTTP_LOC_GET_WOLIST, obj, true);
	}

	/**
	 * 获取部门数据缓存
	 * 
	 * @param con
	 * @param back
	 */
	public static void requestDeparttlist(Context con, ObserverCallBack back) {
		// PDADeviceInfoService pdaDeviceInfoService = new
		// PDADeviceInfoService(con);
		// String url = HttpApi.Http_Interface_query_DepartList + "&attid=" +
		// pdaDeviceInfoService.getDeviceId();
		// AnsynHttpRequest.requestGet(url, back,
		// MethodApi.HTTP_REQUEST_DEPARTMENT, con, false);

		PDADeviceInfoService pdaDeviceInfoService = new PDADeviceInfoService(con);
		String version = "";
		try {
			version = pdaDeviceInfoService.getDeviceAppVersion(con);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String url = HttpApi.Http_Interface_query_DepartList + "&attid=" + pdaDeviceInfoService.getDeviceId() + "&version=" + version;
		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_REQUEST_DEPARTMENT, con, false);
	}

	/**
	 * 查询工班长人员信息
	 * 
	 * @author:zn
	 * @version:2015-2-10
	 * @param woNum
	 */
	public static void queryWoInfo(Context con, ObserverCallBack back, String dept, String test) {
		// String url = LocationApiConfig.Http_Interface_get_deptLead +
		// "crewid=" + dept;
		String url = HttpApi.Http_Interface_get_deptLead + "crewid=" + dept;

		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_CONSTANT, con, test, false);
	}

	/**
	 * 更新部门数据
	 */
	public static void updateDepartmentFromServices(Context con, ObserverCallBack back) {
		AnsynHttpRequest.requestGetOrPostE(AnsynHttpRequest.GET, con, HttpApi.Http_Interface_Update_DepartList, null, back,
				AnsynHttpRequest.getRequestQueue(con), MethodApi.HTTP_GET_REQUEST_DEPARTMENT, null, false);
	}

	/**
	 * 获取手机的配置文件
	 * 
	 * @param con
	 *            上下文
	 * @param back
	 *            返回
	 * @param siteid
	 *            状态ID
	 */
	public static void getPadConfig(Context con, ObserverCallBack back, String siteid) {
		String url = HttpApi.Http_Interface_pad_config + "&siteid=" + siteid;
		AnsynHttpRequest.requestGetOrPostE(AnsynHttpRequest.GET, con, url, null, back, AnsynHttpRequest.getRequestQueue(con),
				MethodApi.HTTP_PAD_CONFIG, null, false);
	}

	// 上传视频的接口
	public static void uploadvideo(Context con, ObserverCallBack back, RequestParmter params, Object obj) {

		OkHttpUtil.post(HttpApi.Http_Interface_uploadvideo, params, back, MethodApi.HTTP_UPLOAD_VIDEO_WONUM, obj);
	}

	// 下载人脸数据
	public static void downLoadFaceForService(Context con, ObserverCallBack back, String lead) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("lastupdate", "0");
		map.put("personid", lead);
		AnsynHttpRequest.requestPost(HttpApi.Http_Interface_getfaceforoutPeople, back, MethodApi.HTTP_DOWNLOAD_FACE, con, map, false);
	}

	/**
	 * 交接 上传图片
	 * 
	 * @param crewid
	 *            工号
	 * @param imageUrlPath
	 *            图片路径
	 * @param url
	 *            访问网络
	 * @param type
	 *            这个是早班夜班 返回1代表成功
	 * @param context
	 * @return
	 * @throws JSONException
	 * @throws AppError
	 */
	public static void uploadphotoWorkHandOver(Context con, ObserverCallBack back, RequestParmter params, Object obj, String imagePath) {
		// String image64 = Base64UtilCst.GetImageStr(imagePath);
		// params.addBodyParmter("picbase", image64);
		params.addBodyParmter("pic", new File(imagePath));

		OkHttpUtil.post(HttpApi.uploadImageWorkHandOver, params, back, MethodApi.HTTP_UPLOAD_PHOTO, obj);
	}

	public static void getTree(Context con, ObserverCallBack back, Object obj, String siteid) {
		String url = HttpApi.Http_Interface_PhotoTree + "&siteid=" + siteid;
		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_TREE, con, siteid, false);
	}

	/**
	 * 获取所有段数据
	 * 
	 * @param con
	 * @param back
	 */
	public static void queryDuanData(Context con, ObserverCallBack back) {
		String url = HttpApi.Http_Interface_queryDuanData;
		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_QUERY_DUAN, con, false);
	}

	/**
	 * 获取所有车间数据
	 * 
	 * @param con
	 * @param back
	 * @param siteid
	 */
	public static void queryWorKShopData(Context con, ObserverCallBack back, String siteid) {
		String url = HttpApi.Http_Interface_queryWorkShop + "&siteid=" + siteid;
		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_QUERY_WORK_SHOP, con, false);
	}

	/**
	 * 获取所有工区
	 * 
	 * @param con
	 * @param back
	 * @param siteid
	 * @param workshopid
	 *            车间id
	 */
	public static void queryAreaData(Context con, ObserverCallBack back, String siteid, String workshopid) {
		String url = HttpApi.Http_Interface_queryArea + "&siteid=" + siteid + "&ancestor=" + workshopid;

		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_QUERY_AREA, con, false);
	}

	/**
	 * 
	 * @param siteid
	 *            当前的站点
	 * @param depart
	 *            当前的工区或者车间
	 * @param type
	 *            当前是属于工区还是车间
	 */
	public static void queryWonumForAreaorShop(Context con, ObserverCallBack back, String siteid, String depart, String type) {
		String url = HttpApi.Http_Interface_queryWonumForArea + "&siteid=" + siteid + "&dept=" + depart + "&type=" + type;
		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_QUERY_STATION, con, true);

	}

	/**
	 * 线路静态检查工单
	 */
	public static void queryPersonCheckWoinfoList(Context con, ObserverCallBack back, String siteid, String personid, String date) {
		String url = HttpApi.HTTP_QUERYPERSONCHECKWOINFOLIST + "&siteid=" + siteid + "&personid=" + personid + "&schedstartdate=" + date;
		OkHttpUtil.get(url, back, MethodApi.HTTP_QUERY_CHECK_WONUM, HeadersUtil.defaultHead());
	}

	/**
	 * 线路静态检查配置数据
	 */
	public static void queryPersonCheckWoInfoConfig(Context con, ObserverCallBack back, String siteid) {
		String url = HttpApi.HTTP_QUERYPERSONCHECKWOINFOCONFIG + "&siteid=" + siteid;
		OkHttpUtil.get(url, back, MethodApi.HTTP_QUERY_CHECK_CONFIG, HeadersUtil.defaultHead());
	}

	/**
	 * 构建静态检查url
	 */
	public static String buildSaveCheckWoinfoProblemUrl(String json) {
		String url = HttpApi.HTTP_SAVECHECKWOINFOPROBLEM + json;
		return url;
	}

	public static void onSaveDepart(Context con, ObserverCallBack back, String lead, String siteid, String workshopid, String areid, String duanname,
			String workshopname, String arename, String attid, String orgid) {

		lead = Base64UtilCst.encodeUrl(lead);
		duanname = Base64UtilCst.encodeUrl(duanname);
		workshopname = Base64UtilCst.encodeUrl(workshopname);
		arename = Base64UtilCst.encodeUrl(arename);

		String url = HttpApi.Http_Interface_save_DepartList + "&personid=" + lead + "&siteid=" + siteid + "&attid=" + attid + "&deptid=" + workshopid
				+ "&workarea=" + areid + "&depname=" + workshopname + "&areaname=" + arename + "&orgid=" + orgid + "&sitename=" + duanname;

		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_SAVE_DEPARTMENT, con, false);

	}

	/**
	 * 获取功能权限
	 * 
	 * @param con
	 * @param back
	 * @param siteid
	 */
	public static void requestLimitMenu(Context con, ObserverCallBack back, String siteid) {
		String url = HttpApi.Http_Interface_limitsofmenu + "&siteid=" + siteid;
		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_GET_LIMIT_MENU, con, false);
	}

	/**
	 * 保存工具
	 * 
	 * @param con
	 * @param back
	 * @param obj
	 */
	public static void saveToolInfo(Context con, ObserverCallBack back, String obj, Object objp) {

		String url = HttpApi.Http_Interface_SaveToolInfo + "&obj=" + obj;

		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_SAVE_TOOLINFO, con, objp, false);

	}

	public static void getDistanceGps(Context con, ObserverCallBack back, String geox, String geoy, String linename, String linetype) {
		linename = Base64UtilCst.encodeUrl(linename);
		linetype = Base64UtilCst.encodeUrl(linetype);
		String url = HttpApi.Http_Interface_getDistance + "&geox=" + geox + "&geoy=" + geoy + "&linename=" + linename + "&linetype=" + linetype;

		AnsynHttpRequest.requestGet(url, back, MethodApi.HTTP_GET_DISTANCE, con, null, false);

	}

	/**
	 * 上传文件的接口
	 * 
	 * @param attid
	 *            设备id
	 * @param personid
	 *            当前是谁
	 * @param upfile
	 *            上传的文件
	 * @return返回值 jsonObj.put("status", true); jsonObj.put("errmsg", "上传成功"); 或者
	 *            jsonObj.put("status", false); jsonObj.put("errmsg", "上传失败");
	 * @throws JSONException
	 * @throws AppError
	 */
	public static void saveLineQuestion(Context con, ObserverCallBack back, RequestParmter body) {
		OkHttpUtil.post(HttpApi.Http_Interface_Save_Question, body, back, MethodApi.HTTP_UPLOAD_CHECK, null);
	}

	/****
	 * 语音转换 lvsd 2015年11月15日
	 * 
	 **/
	public static void getWords(File filePath, ObserverCallBack back, int method) {

		String url = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.k_word, HttpApi.Http_Interface_getSoundText);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("method", "getVoiceWords");
		HashMap<String, File> files = new HashMap<String, File>();
		files.put("photo", filePath);
		// 这里为什么不用okhttp呢？有个bug没解决，就是用okhttp有的时候传不上去会报
		// java.net.ProtocolException: expected 1364 bytes but received 2048
		new AnsyHttpRunner(url, files, params, back, method).execute();

		// RequestParmter body = new RequestParmter();
		// body.addBodyParmter("photo", filePath);
		// body.addBodyParmter("method", "getVoiceWords");
		// OkHttpUtil.postFile(HLocationApiConfig.Http_Interface_getSoundText+"?method=getVoiceWords&photo="+filePath.getName(),
		// filePath, back, method, null);

		// try {
		// OkHttpUtil.postFile(url, body, back, method, null);
		// } catch (Exception e) {
		// }
	}

	public static void requestApkUpdate(JSONObject json, ObserverCallBack back) {
		String nameSpace = "http://webservice.module.appcheck.jtv.com";
		String methodname = "checkApkUpdateInfo";
		String endPoint = HttpApi.Http_Interface_webservices_version_update;
		SoapObject defaultSoap = WebServiceUtil.defaultSoap(nameSpace, methodname);
		defaultSoap.addProperty("obj", json.toString());
		new WebAnsyHttp(null, defaultSoap, endPoint, back, MethodApi.HTTP_VERSION_UPDATE).execute();
	}

	public static void requestDownLoadFacePath(String siteid, ObserverCallBack back) {
		OkHttpUtil.get(HttpApi.Http_Face_PATH, back, MethodApi.HTTP_DOWNLOAD_FACE, null);
	}

	public static void requestDownloadFile(String path, ObserverCallBack back, Context con) {

		File file = new File(CreatFileUtil.getRootFile(con).getAbsolutePath() + File.separator + Constants.FACEDATEBASE);
		OkHttpUtil.download(path, file, back, MethodApi.HTTP_DOWNLOAD_FACE, null);

	}

	public static void requestDaoChaAssets(String siteid, String deptid, ObserverCallBack back) {
		String url = HttpApi.Http_DAOCHA + "&eq2=" + deptid + "&siteid=" + siteid;
		// String url = HttpApi.Http_DAOCHA+"&eq2="+11007+"&siteid=SITE1";
		// &eq2=11007&siteid=SITE1
		OkHttpUtil.get(url, back, MethodApi.HTTP_ASSETS, null);
	}
}
