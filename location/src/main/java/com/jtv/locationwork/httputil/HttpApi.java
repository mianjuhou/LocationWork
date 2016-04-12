
package com.jtv.locationwork.httputil;

import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;

public class HttpApi {

	public static String RootIP2 = null;

	static {

		String tempip = "";

		String defaultip = "118.26.65.36:8080";// 默认的ip

		try {
			tempip = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.SERVICES_IP2, defaultip);// 配置文件中的ip

			// ip错误
			if (TextUtil.isEmpty(tempip) || tempip.contains("http") || tempip.contains("//") || tempip.length() < 9) {
				throw new Exception();
			}

			RootIP2 = "http://" + tempip;

		} catch (Exception e) {

			RootIP2 = "http://" + defaultip;
		}

		// RootIP2 = "http://172.16.90.50:8080";
		// RootIP2 = "http://172.16.60.90:8080";
		// RootIP2 = "http://192.168.0.110:8081";

		// RootIP2 = "http://172.16.90.21:8080";
	}

	public static String TRACK_WORKORDER_LIST = RootIP2
			+ "/work/szhxl/workorder/Workorder.do?method=queryPersonWoInfoList";// 222.

	public static String TRACK_WORKORDER_INFO = RootIP2 + "/operation/success/plan/Plan.do";// 根据工单号获取工单详情(获取工单信息)222.

	public static String TRACK_UPLOAD_SERVLET = RootIP2 + "/operation/ServletPic ";// 上传图片222.

	public static String TRACK_UPLOAD_IMG = RootIP2 + "/operation/szhxl/picinfo/Picinfo.do";// 上传图片222.

	// 根据部门编号获取工班长信息海拉尔 需要 crewid=7765 get请求222.
	public static String Http_Interface_get_deptLead_hlr = RootIP2
			+ "/work/szhxl/person/Person.do?method=getPersonListJson&";

	// webservices 保存工机具 222.
	public static String Http_Interface_webservices_save_tool = RootIP2 + "/toolservice/services/IService";

	// "http://172.16.90.50"
	public static String uploadImageTest = RootIP2 + "/operation/szhxl/picinfo/Picinfo.do?method=savePicBase";

	public static String uploadImage = RootIP2 + "/operation/szhxl/picinfo/Picinfo.do?method=saveBigPicBase";

	// 这个是交接早班晚班上传图片
	public static String uploadImageWorkHandOver = RootIP2
			+ "/operation/operation/shiftinfo/Shiftinfo.do?method=saveInfoBase";

	// 上传经纬度
	public static String saveGpsinfoTest = RootIP2 + "/operation/szhxl/gpsinfo/Gpsinfo.do";

	// 上传录制的视频到服务器
	public static String Http_Interface_uploadvideo = RootIP2 + "/operation/mediaServlet";

	// 考勤的时候上传人脸到服务器
	// public static String Http_Interface_uploadFace = RootIP2 +
	// "/operation/operation/amsinfo/Amsinfo.do";

	// 考勤的时候上传人脸到服务器
	public static String Http_Interface_uploadFace = RootIP2
			+ "/operation/operation/facenotes/Facenotes.do?method=saveFace";

	// 保存工具
	public static String Http_Interface_SaveToolInfo = RootIP2
			+ "/operation/operation/toolnotes/Toolnotes.do?method=saveToolInfo";

	// 获取外来人员的脸部信息
	public static String Http_Interface_getfaceforoutPeople = RootIP2
			+ "/operation/operation/amspersonface/Amspersonface.do?method=getPersonFaceJson";

	// 上传文件到服务器
	public static String Http_Interface_uploadFile = RootIP2 + "/operation/depotServlet";

	// 请求一个人员的文件来自服务端
	public static String Http_Interface_requestDownloadFile = RootIP2
			+ "/operation/operation/depotfile/Depotfile.do?method=getDepotfile";

	// 请求的文件已经下载标示
	public static String Http_Interface_saveDownloadFileState = RootIP2
			+ "/operation/operation/depotfile/Depotfile.do?method=saveDepotfile";

	// post请求获取一个人的工单，需要参数是 personid=“此文”；
	// public static String Http_Interface_get_wonumlist = RootIP lgs改动
	// + "/work/szhxl/workorder/Workorder.do?method=queryPersonWoInfoList";

	// 根据部门编号获取工班长信息 需要 crewid=7765 get请求
	public static String Http_Interface_get_deptLead = RootIP2
			+ "/operation/operation/jtvperson/Jtvperson.do?method=getPersonListJson&";
			// queryPersonLeadJson 原来的是

	// 获取一个行车的工单编号 需要登陆人
	public static String Http_Interface_getRoadWonum = RootIP2
			+ "/operation/operation/runway/Runway.do?method=queryRunwayWonum&personid=";

	// 获取人脸数据来自服务端
	public static String Http_Interface_getFaceData = RootIP2
			+ "/operation/operation/amspersonface/Amspersonface.do?method=getFaceJson";

	// 保存gps信息到服务端
	public static String Http_Interfacee_save_gps = RootIP2 + "/operation/operation/amsinfo/Amsinfo.do";

	// webservices 版本更新 /appcheck/services/CertificatePDAService
	public static String Http_Interface_webservices_version_update = RootIP2
			+ "/appcheck/services/CertificatePDAService";

	/**
	 * 获取一个人的工单大阶段的app 需要参数 personid=池文 返回值：工单编号 lead 作业负责人 planname 作业项目 startmeasure 开始里程 endmeasure 结束里程 linename 线名
	 * linetype 线别 schedstartdate 日期 starttime 开始时间 endtime 结束时间
	 * 
	 */
	public static String Http_interface_get_wonumlist_bjd = RootIP2
			+ "/operation/operation/workorder/Workorder.do?method=queryPersonWoInfoList";

	// gps坐标采集
	public static String Http_Interface_Acquisition_circuit = RootIP2
			+ "/operation/operation/linegps/Linegps.do?method=saveLinegps";

	// 更新部门编号，不需要任何参数
	public static String Http_Interface_Update_DepartList = RootIP2
			+ "/operation/operation/alndomainancestor/Alndomainancestor.do?method=queryDepartmentList";

	// &personid=5a2Z5Yek5aWO&siteid=&attid=&deptid=
	public static String Http_Interface_save_DepartList = RootIP2
			+ "/operation/operation/handsetsys/Handsetsys.do?method=saveHandsetsys";

	// 获取手持机部门
	public static String Http_Interface_query_DepartList = RootIP2
			+ "/operation/operation/handsetsys/Handsetsys.do?method=getHandsetsysJson";

	// 获取手持机的配置文件
	public static String Http_Interface_pad_config = RootIP2
			+ "/operation/operation/sysconfig/Sysconfig.do?method=querySysConfig";

	// 获取工单的动态字段映射
	public static String Http_Interface_mapping_fieldwonum = RootIP2
			+ "/operation/operation/elementsys/Elementsys.do?method=getElementsysJson&type=1";

	// 获取一个树数据
	public static String Http_Interface_PhotoTree = RootIP2
			+ "/operation/operation/pictypesys/Pictypesys.do?method=getpicTypeJson";

	// 获取一个段数据
	public static String Http_Interface_queryDuanData = RootIP2
			+ "/operation/operation/alndomainancestor/Alndomainancestor.do?method=querySiteid";

	// 获取一个车间下所有车间数据 需要 siteid
	public static String Http_Interface_queryWorkShop = RootIP2
			+ "/operation/operation/alndomainancestor/Alndomainancestor.do?method=queryDepartmentList";

	// 获取一个工区下的工区数据 需要siteid 和 工区id ancestor
	public static String Http_Interface_queryArea = RootIP2
			+ "/operation/operation/alndomainancestor/Alndomainancestor.do?method=queryWorkarea";
	// 查询工单来自整个车间
	public static String Http_Interface_queryWonumForArea = RootIP2
			+ "/operation/operation/workorder/Workorder.do?method=querydeptWoInfoList";

	// 下载人脸的so文件到sd卡上面
	public static String Http_Interface_download_facsso = RootIP2 + "/operation/libfsdk.so";

	// 获取模块权限
	public static String Http_Interface_limitsofmenu = RootIP2
			+ "/operation/operation/workorder/Workorder.do?method=queryConfigList";

	// 获取里程
	public static String Http_Interface_getDistance = RootIP2
			+ "/operation/operation/linegps/Linegps.do?method=getGpsMeasure";

	// 问题检查上传
	public static String Http_Interface_Save_Question = RootIP2 + "/operation/troubleServlet";

	public static String Http_Interface_getSoundText = "http://61.167.137.39:9423/gbjc/upload/photoupload.do";

	// 下载人脸的信息路径
	public static String Http_Face_PATH = RootIP2 + "";

	public static String Http_DAOCHA = RootIP2 + "/operation/operation/asset/Asset.do?method=saveAssetInfo";

	// 获取线路检查的工单
	public static String HTTP_QUERYPERSONCHECKWOINFOLIST = RootIP2
			+ "/operation/operation/workorder/Workorder.do?method=queryPersonCheckWoInfoList";

	public static String HTTP_QUERYPERSONCHECKWOINFOCONFIG = RootIP2
			+ "/operation/operation/workorder/Workorder.do?method=queryPersonCheckWoInfoConfig";

	// 保存静态检查
	public static String HTTP_SAVECHECKWOINFOPROBLEM = RootIP2
			+ "/operation/operation/workorder/Workorder.do?method=saveCheckWoInfoProblem&data=";
	public static String HTTP_SAVECHECKWOINFOPROBLEM2 = RootIP2
			+ "/operation/operation/workorder/Workorder.do?method=saveCheckWoInfoProblem";

}
