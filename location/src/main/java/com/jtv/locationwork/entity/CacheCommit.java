package com.jtv.locationwork.entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jtv.hrb.locationwork.CacheCommit2;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.db.StaticCheckDbService;
import com.jtv.hrb.locationwork.domain.LineNumBean;
import com.jtv.hrb.locationwork.domain.RailNumBean;
import com.jtv.hrb.locationwork.domain.StraightLineBean;
import com.jtv.locationwork.httputil.HttpApi;
import com.jtv.locationwork.httputil.OkHttpUtil;
import com.jtv.locationwork.httputil.ParseJson;
import com.jtv.locationwork.httputil.TrackAPI;
import com.jtv.locationwork.util.Arrays;
import com.jtv.locationwork.util.Base64UtilCst;
import com.jtv.locationwork.util.JsonUtil;
import com.jtv.locationwork.util.LogUtil;
import com.jtv.locationwork.util.NetUtil;

import android.content.Context;
import android.hardware.Camera.Parameters;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 批量提交
 * <p>
 *
 * @author 更生
 * @version 2016年3月13日
 */
public class CacheCommit {

	private static CacheCommit cache = new CacheCommit();
	private static boolean run = false;

	private CacheCommit() {
	}

	public static CacheCommit getInstance() {
		return cache;
	}

	/**
	 * 执行提交请求
	 */
	public synchronized void start(Context con) {
		if (!NetUtil.hasConnectedNetwork(con)) {
			return;
		}
		if (isRun())
			return;
		run = true;
		new Thread(new CommitRunnable(con)).start();

	}

	private class CommitRunnable implements Runnable {

		Context con;

		public CommitRunnable(Context con) {
			this.con = con;
		}

		public void run() {
			LogUtil.i("执行提交");
			commit(con);
			run = false;
		}
	}
	
	private void commit(Context con) {
		CacheCommit2 cacheCommit2 = new CacheCommit2(con);
		String submit=cacheCommit2.getCommitJson();
		if(TextUtils.isEmpty(submit)){
			return;
		}
		HashMap<String, String> parmter=new HashMap<String, String>();
		parmter.put("data", submit);
		writeToSDCard(submit);
		String status = post(HttpApi.HTTP_SAVECHECKWOINFOPROBLEM2, parmter);
		System.gc();
		if (ParseJson.parseOk(status)) {
			cacheCommit2.setSyncState(1);
			LogUtil.i("数据提交成功");
		} else {
			LogUtil.i("数据提交失败");
		}
	}
	
	
	private void writeToSDCard(String submit) {
		try {
			File jsonFile=new File(Environment.getExternalStorageDirectory(), "json.txt");
			BufferedWriter bw=new BufferedWriter(new FileWriter(jsonFile));
			bw.write(submit);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询数据库获取数据,根据数据筛选,提交到不同接口 李彦青 18:18:06 ------CP1RAW_ZXMAIN_R1 ASSETATTRID '轨型',R_VALUE '误差值',TRACKPART
	 * '道岔部位(转辙部位,导曲线部位,辙叉部位)'
	 * 
	 * assetnum '资产编号' ,wonum '工单号',gh '轨号',NAMETYPE（'水平'，'轨距','问题'）,value1 ,DEFECTCLASS '超限等级',STATUS '计算状态 0标识未计算
	 * 1,标识已计算 -1 不用计算',flag_v ' 标识 1曲线、2直线、0道岔',
	 * 
	 * -------CP1RAW_ZXMAIN INSPDATE '检测日期',XVALUE 'X轴坐标',YVALUE 'Y轴坐标' ,STRARTLC '开始里程',ENDLC '结束里程';
	 */
//	private void commit(Context con) {
//		// {table:***,data[{},{}]}
//		StaticCheckDbService staticCheckDbService = new StaticCheckDbService(con);
//		List<StraightLineBean> data = null;//staticCheckDbService.getAllStraightLine();
//		// ArrayList<Value> value = staticCheckDbService.getValue1();
//		String wonum = null;
//		if (!Arrays.isEmpty(data)) {
//			JSONArray root = new JSONArray();
//			JSONObject tab1 = new JSONObject();
//			JSONObject tab2 = new JSONObject();
//			JSONObject tab3 = new JSONObject();
//			JSONArray data1 = new JSONArray();
//			JSONArray data2 = new JSONArray();
//			JSONArray data3 = new JSONArray();
//			// JSONObject in = new JSONObject();
//			for (StraightLineBean item : data) {
//				List<RailNumBean> railNumBeans = item.getRailNumBeans();
//				JSONObject data1Item = null;
//				JSONObject data2Item = null;
//				String preWonum=item.getWonum();
//				if(TextUtils.isEmpty(preWonum)){
//					continue;
//				}
//				wonum = preWonum;
//				String assetnum = item.getAssetnum();
//				double startlc = item.getStartlc();
//				double endlc = item.getEndlc();
//
//				if (railNumBeans != null) {
//					for (RailNumBean railNumBean : railNumBeans) {
//
//						data2Item = new JSONObject();
//						String xvalue = railNumBean.getXvalue();
//						String yvalue = railNumBean.getYvalue();
//						String INSPDATE = railNumBean.getTime();
//						// List<LineNumBean> lineNumBeans = railNumBean.getLineNumBeans();
//						data2Item.put("xvalue", xvalue);
//						data2Item.put("yvalue", yvalue);
//						data2Item.put("inspdate", INSPDATE);
//						int gh = railNumBean.getGh();
//
//						List<LineNumBean> lineNumBeans = railNumBean.getLineNumBeans();
//						if (lineNumBeans != null) {
//							for (LineNumBean lineNumBean : lineNumBeans) {
//								data1Item = new JSONObject();
//								int value1 = lineNumBean.getValue1();
//								data1Item.put("gh", gh);
//								data1Item.put("assetnum", assetnum);
//								data1Item.put("value1", value1);
//								data1Item.put("nametype", getBase64("轨距"));
//								data1Item.put("wonum", wonum);
//								data1Item.put("flag_v", "2");// ',flag_v ' 标识 1曲线、2直线、0道岔',
//								data1Item.put("status", 0);// 0为测量数据 -1为问题
//								data1Item.put("orgid",GlobalApplication.orgid);
//								data1Item.put("siteid", GlobalApplication.siteid);
//								// if (data1Item != null) {
//
//								data1.add(data1Item);
//
//								data1Item = new JSONObject();
//								int value2 = lineNumBean.getValue2();
//								data1Item.put("gh", gh);
//								data1Item.put("assetnum", assetnum);
//								data1Item.put("value1", value2);
//								data1Item.put("nametype", getBase64("水平"));
//								data1Item.put("wonum", wonum);
//								data1Item.put("flag_v", "2");// ',flag_v ' 标识 1曲线、2直线、0道岔',
//								data1Item.put("status", 0);
//								data1Item.put("orgid",GlobalApplication.orgid);
//								data1Item.put("siteid", GlobalApplication.siteid);
//
//								data1.add(data1Item);
//								// }
//								// int value2 = lineNumBean.getValue2();
//
//							}
//						}
//						if (data2Item != null) {
//							data2Item.put("strartlc", startlc);
//							data2Item.put("endlc", endlc);
//							
//							data2Item.put("siteid", GlobalApplication.siteid);
//							data2Item.put("orgid", GlobalApplication.orgid);
//							data2Item.put("assetnum", assetnum);
//							data2Item.put("gh", gh);
//							data2Item.put("hasld", 0);
//							data2Item.put("wonum", wonum);
//							
//							data2.add(data2Item);
//						}
//					}
//
//				}
//
//			}
//
//			JSONObject data3Item = new JSONObject();
//			data3Item.put("enterby", GlobalApplication.mBase64Lead);
//			data3Item.put("wonum", wonum);
//			data3Item.put("orgid", GlobalApplication.orgid);
//			data3Item.put("siteid", GlobalApplication.siteid);
//			data3Item.put("hasld", 0);
//			data3.add(data3Item);
//			tab1.put("data", data1);
//			tab1.put("tablename", "Cp1raw_Zxmain_R1");
//			tab2.put("data", data2);
//			tab2.put("tablename", "Cp1raw_Zxmain");
//			tab3.put("data", data3);
//			tab3.put("tablename", "C_Insp1rawdata");
//			root.add(tab1);
//			root.add(tab2);
//			root.add(tab3);
//
////			String submit = TrackAPI.buildSaveCheckWoinfoProblemUrl(JsonUtil.parseString(root));
////			String status = get(submit);
//			HashMap<String, String> parmter=new HashMap<String, String>();
//			String submit=JsonUtil.parseString(root);
//			parmter.put("data", submit);
//			String status = post(HttpApi.HTTP_SAVECHECKWOINFOPROBLEM2, parmter);
//			root = null;
//			data.clear();
//			data = null;
//			data3Item = null;
//			System.gc();
//			staticCheckDbService = null;
//			if (ParseJson.parseOk(status)) {
//				LogUtil.i("数据提交成功");
//			} else {
//				LogUtil.i("数据提交失败");
//			}
//
//			LogUtil.i("执行提交数据:" + JsonUtil.parseString(root));
//			// String response = get(submit);
//
//		}
//
//	}

	
	public static String getBase64(String value) {
		return Base64UtilCst.encodeUrl(value);
	}

	public boolean isRun() {
		return run;
	}

	public String get(String url) {
		try {
			return OkHttpUtil.getStringFromServer(url);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		return null;
	}

	public String post(String url, HashMap<String, String> parmter) {
		try {
			return OkHttpUtil.post(url, parmter);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		return null;
	}

}
