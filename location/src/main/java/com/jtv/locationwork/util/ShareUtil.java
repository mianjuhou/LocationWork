package com.jtv.locationwork.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.jtv.base.util.FileUtil;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.entity.TreeJson;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.httputil.TrackAPI;
import com.spore.jni.FaceUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 共享的工具类方便复用包括数据库等
 * 
 * @author beyound
 *
 */
public class ShareUtil {

	// 是否有一个人脸数据有返回true
	public static boolean isFaceForLead(String lead) {
		lead = Base64UtilCst.encodeUrl(lead);

		Cursor queryCursor = DBFactory.getConnection().queryCursor(Constants.DB_NAME_FACE, null, null, null, null, null,
				null);

		if (queryCursor != null && queryCursor.moveToNext()) {
			queryCursor.close();
			return true;

		}

		return false;
	}

	/**
	 * 获取一个人脸数据
	 * 
	 * @param con
	 * @param back
	 * @param lead
	 */
	public static void downloadFace(Context con, ObserverCallBack back, String lead) {
		lead = Base64UtilCst.encodeUrl(lead);
		TrackAPI.downLoadFaceForService(con, back, lead);
	}

	// 插入一个人脸数据到数据库人脸数据和工区号
	public static void insertFace(String faceData, String deptid) {
		if (TextUtils.isEmpty(faceData)) {
			return;
		}
		JSONObject faceObjects;
		try {
			faceObjects = new JSONObject(faceData);
			JSONArray faceArray = faceObjects.getJSONArray("faces");
			JSONObject face;
			ContentValues values;
			for (int j = 0; j < faceArray.length(); j++) {
				face = faceArray.getJSONObject(j);
				values = new ContentValues();
				values.put("personid", face.optString("personid"));
				values.put("personname", face.optString("personname"));
				values.put("faceinfo", face.optString("faceinfo"));
				values.put("eyeinfo", face.optString("eyeinfo"));
				values.put("personfaceid", UUID.randomUUID().toString());
				values.put("crewid", deptid);
				values.put("updatetime", face.optString("currtentlastdateTimestamp"));
				DBFactory.getConnection().insert("ams_personface", values);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 同步树结构
	 * 
	 * @param data
	 * @param siteid
	 */
	public static synchronized void syncroizedTree(String data, String siteid) {
		SpeedTest test = new SpeedTest("syncroizedTree");
		test.start();
		// 获取到json数据{"tree":[],"currentTimestamp":2015}
		if ("-1".equals(data) || TextUtil.isEmpty(data)) {
			return;
		}
		com.alibaba.fastjson.JSONObject mJsonTree = null;
		try {
			mJsonTree = com.alibaba.fastjson.JSONObject.parseObject(data);
		} catch (Exception e) {
			e.printStackTrace();
			// JSONObject jsonObject = new JSONObject();
			//
			// try {
			// jsonObject.put("currentTimestamp", "1");
			// jsonObject.put("tree", data);
			// data = jsonObject.toString();
			// mJsonTree = com.alibaba.fastjson.JSONObject.parseObject(data);
			// } catch (JSONException e1) {
			// e1.printStackTrace();
			// return;
			// }

			// return;
		}

		if (mJsonTree.containsKey("currentTimestamp")) {// 返回了时间搓

			String lastTime = mJsonTree.getString("currentTimestamp");// 获取到服务端最后更新的时间搓

			Cursor queryCursor = DBFactory.getConnection().queryCursor(Constants.DB_PHOTO_TREE, null, "siteid  = ?",
					new String[] { siteid }, null, null, null);

			List<HashMap<String, String>> queryList = DateBaseUtil.CursorToList(queryCursor);

			// List<HashMap<String, String>> queryList =
			// DBFactory.getConnection().queryList(Constants.DB_PHOTO_TREE,null,
			// "siteid = ?", new String[] { siteid }, null, null, null);

			boolean isNeedUpdateTree = true;
			if (queryList.size() > 0) {// 查询到数据

				HashMap<String, String> hashMap = queryList.get(0);

				String time = hashMap.get("lasttimeupdate");

				if (TextUtils.equals(time, lastTime)) {// 时间搓一致，代表没有更新数据

					isNeedUpdateTree = false;

				} else {
					isNeedUpdateTree = true;
				}
			}

			if (isNeedUpdateTree) {// 需要更新树结构

				String mTreeJson = mJsonTree.getString("tree");

				List<TreeJson> mArrNode = JSON.parseArray(mTreeJson, TreeJson.class);

				// 没有数据
				if (mArrNode == null || mArrNode.size() < 1) {
					return;
				}

				ArrayList<ContentValues> mInsertValue = new ArrayList<ContentValues>();

				for (int i = 0; i < mArrNode.size(); i++) {
					TreeJson treeJson = mArrNode.get(i);
					String nodeid = treeJson.getNodeid();
					String parentid = treeJson.getParentid();
					String parmter = treeJson.getParmeter();
					String title = treeJson.getTitle();
					String siteid2 = treeJson.getSiteid();
					String table = treeJson.getTabletype();
					if (TextUtil.isEmpty(table)) {
						table = "";
					}
					if (TextUtil.isEmpty(siteid2)) {
						return;
					}
					if (TextUtil.isEmpty(nodeid)) {
						return;
					}
					if (TextUtil.isEmpty(parentid)) {
						return;
					}
					ContentValues contentValues = new ContentValues();

					contentValues.put("Node_id", nodeid);
					contentValues.put("Parent_id", parentid);
					contentValues.put("siteid", siteid);
					contentValues.put("lasttimeupdate", lastTime);
					contentValues.put("title", title);
					contentValues.put("parmeter", parmter);
					contentValues.put("tabletype", table);
					mInsertValue.add(contentValues);
				}

				SQLiteDatabase mDBTree;
				mDBTree = DBFactory.getConnection().getWritableDatabase();

				// mDBTree.beginTransaction();//同步

				// 需要更新数据先删除,在更新
				// for (int i = 0; i < queryList.size(); i++) {
				// HashMap<String, String> item = queryList.get(i);
				// String id = item.get("node_id");

				DBFactory.getConnection().delete(Constants.DB_PHOTO_TREE, "siteid = ?", new String[] { siteid });
				// }

				for (int i = 0; i < mInsertValue.size(); i++) {
					ContentValues contentValues = mInsertValue.get(i);
					long insert = mDBTree.insert(Constants.DB_PHOTO_TREE, null, contentValues);
				}
				// mDBTree.setTransactionSuccessful();
				// mDBTree.endTransaction();
				mInsertValue.clear();

			}
		}
		test.end();
		test.print();
	}

	/**
	 * 下载人脸识别动态库
	 * 
	 * 首先会判断当前有没有so，没有就从本地的包下com.jtv.hrl.location.work/download/判断有没有so
	 * 
	 * @param con
	 * @return true代表不需要下载
	 */
	public static boolean isFaceSo(Context con) {

		if (FaceUtil.isLoadDefaultSO()) {
			return true;
		}

		File dir2 = con.getFilesDir();
		File[] listFiles = dir2.listFiles();

		for (int i = 0; i < listFiles.length; i++) {
			File file = listFiles[i];
			String name = file.getName();
			if ("libfsdk.so".equals(name)) {// 代表有这个库文件直接更新完成
				long length = file.length();
				if (length > 10 && (length / 1024 / 1024) >= 14) {// 大于15兆
					return true;
				} else {
					// file.delete();
				}
			}
		}

		File downLoad = CreatFileUtil.getDownLoad(con);
		File file = new File(downLoad.getAbsolutePath() + File.separator + "libfsdk.so");
		long length = file.length();

		if (length != 0 && length / 1024 / 1024 >= 14) {
			File dir = con.getFilesDir();
			String path = dir.getAbsolutePath() + "/libfsdk.so";
			dir = new File(path);
			try {
				
				synchronized (ShareUtil.class) {
					FileUtil.copyFile(file, dir);
					return true;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// file.delete();
		}

		return false;
	}
	public static String getStatusText(int status) {
		String text="";
		switch (status) {
		case Constants.READY_UPLOAD:
			text = "没有上传";
			break;
		case Constants.UPLOADING:
			text="上传中";
			break;
		case Constants.UPLOAD_FINISH:
			text="上传完成";
			break;
		}
		return text;
	}
}
