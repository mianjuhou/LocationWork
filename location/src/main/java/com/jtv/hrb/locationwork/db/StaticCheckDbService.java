package com.jtv.hrb.locationwork.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jtv.hrb.locationwork.domain.MainMeasureData;
import com.jtv.hrb.locationwork.domain.MeasureData;
import com.jtv.hrb.locationwork.domain.TableLevelOne;
import com.jtv.locationwork.dao.DBObjectHelper;

import ct.cu;

public class StaticCheckDbService {

	private DBObjectHelper dbHelper;
	private SQLiteDatabase db;

	public StaticCheckDbService(Context context) {
		dbHelper = new DBObjectHelper(context);
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		db.close();
		dbHelper.close();
	}

	/**
	 * 轨号变化前更新当前页数据在在数据库中的条目状态标识为0
	 * 
	 * @param startScope
	 * @param endScope
	 * @param gh
	 * @return
	 */
	public boolean updateRailMeasureDataState(double startScope, double endScope, int gh) {
		ContentValues values = new ContentValues();
		values.put("syncstate", 0);
		int updateNum = db.update("cp1raw_zxmain_r1", values, "startmeasure=? and endmeasure=? and gh=?", new String[] { startScope + "",
				endScope + "", gh + "" });
		if (updateNum > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存曲线录入数据
	 * 
	 * @param data
	 * @param tag
	 * @param startScope
	 * @param endScope
	 * @param gh
	 * @return
	 */
	public boolean saveCurveMeasureData(MeasureData data) {
		Cursor cursor = db.rawQuery(//
				"select * from cp1raw_zxmain_r1 where  assetfeatureid=? and assetnum=? and flag_v=? and linenum=?",//
				new String[] { data.getAssetfeatureid() + "", data.getAssetnum(), data.getFlag_v(), data.getLinenum() + "" });
		if (cursor.getCount() > 0) {
			// 数据已存在，更新数据
			cursor.moveToFirst();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			return updateCurveMeasureData(data, id);
		} else {
			// 数据不存在，插入数据
			return insertMeasureData(data);
		}
	}

	private boolean updateCurveMeasureData(MeasureData data, int id) {
		ContentValues values = new ContentValues();
		values.put("value1", data.getValue1());
		values.put("value2", data.getValue2());
		values.put("inspdate", data.getInspdate());
		int num = db.update("cp1raw_zxmain_r1", values, "id=?", new String[] { id + "" });
		if (num > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存录入数据
	 * 
	 * @param data
	 * @param tag
	 * @param startScope
	 * @param endScope
	 * @param gh
	 * @return
	 */
	public boolean saveMeasureData(MeasureData data, String tag, double startScope, double endScope, int gh) {
		Cursor cursor = db.rawQuery("select * from cp1raw_zxmain_r1 where wonum=? and assetnum=? and tag=? and  startmeasure=? and endmeasure=? and gh=?", //
				new String[] { data.getWonum(),data.getAssetnum(),tag,startScope + "", endScope + "", gh + "" });
		if (cursor.getCount() > 0) {
			// 数据已存在，更新数据
			cursor.moveToFirst();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			return updateMeasureData(data, id);
		} else {
			// 数据不存在，插入数据
			return insertMeasureData(data);
		}
	}

	/**
	 * 更新一条录入数据
	 * 
	 * @param data
	 * @param id
	 * @return
	 */
	private boolean updateMeasureData(MeasureData data, int id) {
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put("value1", data.getValue1());
			values.put("inspdate", data.getInspdate());
			values.put("measuretype", data.getMeasuretype());
			values.put("nametype", data.getNametype());
			values.put("xvalue", data.getXvalue());
			values.put("yvalue", data.getYvalue());
			values.put("value1", data.getValue1());
			values.put("value2", data.getValue2());
			values.put("defectclass", data.getDefectclass());
			values.put("fvalue", data.getFvalue());
			values.put("cp1nanumcfgrownum", data.getCp1nanumcfgrownum());
			values.put("status", data.getStatus());
			int num = db.update("cp1raw_zxmain_r1", values, "id=?", new String[] { id + "" });
			db.setTransactionSuccessful();
			if (num > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			db.endTransaction();
		}

	}

	/**
	 * 插入一条录入数据
	 * 
	 * @param data
	 * @return
	 */
	private boolean insertMeasureData(MeasureData data) {
		// 插入数据
		ContentValues values = new ContentValues();
		values.put("siteid", data.getSiteid());
		// values.put("resid", data.getResid());
		values.put("tag", data.getTag());
		values.put("orgid", data.getOrgid());
		values.put("gh", data.getGh());
		values.put("startmeasure", data.getStartmeasure());
		values.put("endmeasure", data.getEndmeasure());
		values.put("linenum", data.getLinenum());
		values.put("value1", data.getValue1());
		values.put("value2", data.getValue2());
		values.put("hasld", data.getHasld());
		values.put("assetattrid", data.getAssetattrid());
		values.put("assetnum", data.getAssetnum());
		values.put("assetfeatureid", data.getAssetfeatureid());
		values.put("wonum", data.getWonum());
		values.put("nametype", data.getNametype());
		values.put("status", data.getStatus());
		values.put("defectclass", data.getDefectclass());
		values.put("fvalue", data.getFvalue());
		values.put("flag_v", data.getFlag_v());
		values.put("xvalue", data.getXvalue());
		values.put("yvalue", data.getYvalue());
		values.put("inspdate", data.getInspdate());
		values.put("cp1nanumcfgrownum", data.getCp1nanumcfgrownum());
		values.put("measuretype", data.getMeasuretype());
		long result = db.insert("cp1raw_zxmain_r1", null, values);
		if (result >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据
	 * @param assetnum 
	 */
	public ArrayList<MeasureData> getLastMeasureData(String wonum, String assetnum) {
		Cursor cursor = db.rawQuery("select * from cp1raw_zxmain_r1 where flag_v='2' and status='0' and  wonum=? and assetnum=? and syncstate=-1 and gh != '-2'",
				new String[] { wonum ,assetnum});
		int itemCount = cursor.getCount();
		ArrayList<MeasureData> datas = new ArrayList<MeasureData>();
		if (itemCount > 0) {
			while (cursor.moveToNext()) {
				MeasureData measureData = getMeasureDataByCurrentCursor(cursor);
				datas.add(measureData);
			}
		} else {
			Cursor cursorLastGh1 = db.rawQuery("select * from cp1raw_zxmain_r1 where flag_v='2' and status='0' and wonum = ? and assetnum=? and gh != '-2' order by startmeasure desc,endmeasure desc,gh desc",//
							new String[] { wonum ,assetnum});
			if (cursorLastGh1.getCount() > 0) {
				cursorLastGh1.moveToFirst();
				double startmeasure = cursorLastGh1.getDouble(cursorLastGh1.getColumnIndex("startmeasure"));
				double endmeasure = cursorLastGh1.getDouble(cursorLastGh1.getColumnIndex("endmeasure"));
				String gh = cursorLastGh1.getString(cursorLastGh1.getColumnIndex("gh"));

				datas = getMeasureDataByRailNum(wonum, assetnum,gh, startmeasure, endmeasure);
			}
		}
		return datas;
	}

	public MeasureData getMeasureDataByCurrentCursor(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex("id"));
		String tag = cursor.getString(cursor.getColumnIndex("tag"));
		String siteid = cursor.getString(cursor.getColumnIndex("siteid"));
		String orgid = cursor.getString(cursor.getColumnIndex("orgid"));
		String gh = cursor.getString(cursor.getColumnIndex("gh"));
		double startmeasure = cursor.getDouble(cursor.getColumnIndex("startmeasure"));
		double endmeasure = cursor.getDouble(cursor.getColumnIndex("endmeasure"));
		int linenum = cursor.getInt(cursor.getColumnIndex("linenum"));
		double value1 = cursor.getDouble(cursor.getColumnIndex("value1"));
		double value2 = cursor.getDouble(cursor.getColumnIndex("value2"));
		int hasld = cursor.getInt(cursor.getColumnIndex("hasld"));
		String assetattrid = cursor.getString(cursor.getColumnIndex("assetattrid"));
		String assetnum = cursor.getString(cursor.getColumnIndex("assetnum"));
		int assetfeatureid = cursor.getInt(cursor.getColumnIndex("assetfeatureid"));
		String wonum = cursor.getString(cursor.getColumnIndex("wonum"));
		String nametype = cursor.getString(cursor.getColumnIndex("nametype"));
		String status = cursor.getString(cursor.getColumnIndex("status"));
		String defectclass = cursor.getString(cursor.getColumnIndex("defectclass"));
		double fvalue=cursor.getDouble(cursor.getColumnIndex("fvalue"));
		String flag_v = cursor.getString(cursor.getColumnIndex("flag_v"));
		String xvalue = cursor.getString(cursor.getColumnIndex("xvalue"));
		String yvalue = cursor.getString(cursor.getColumnIndex("yvalue"));
		String inspdate = cursor.getString(cursor.getColumnIndex("inspdate"));
		String cp1nanumcfgrownum = cursor.getString(cursor.getColumnIndex("cp1nanumcfgrownum"));
		int measuretype = cursor.getInt(cursor.getColumnIndex("measuretype"));
		int syncstate = cursor.getInt(cursor.getColumnIndex("syncstate"));
		MeasureData measureData = new MeasureData();
		measureData.setId(id);
		measureData.setTag(tag);
		measureData.setSiteid(siteid);
		measureData.setOrgid(orgid);
		measureData.setGh(gh);
		measureData.setStartmeasure(startmeasure);
		measureData.setEndmeasure(endmeasure);
		measureData.setLinenum(linenum);
		measureData.setValue1(value1);
		measureData.setValue2(value2);
		measureData.setHasld(hasld);
		measureData.setAssetattrid(assetattrid);
		measureData.setAssetnum(assetnum);
		measureData.setAssetfeatureid(assetfeatureid);
		measureData.setWonum(wonum);
		measureData.setNametype(nametype);
		measureData.setStatus(status);
		if (defectclass != null) {
			measureData.setDefectclass(defectclass);
		}
		measureData.setFvalue(fvalue);
		measureData.setFlag_v(flag_v);
		measureData.setXvalue(xvalue);
		measureData.setYvalue(yvalue);
		measureData.setInspdate(inspdate);
		measureData.setCp1nanumcfgrownum(cp1nanumcfgrownum);
		measureData.setMeasuretype(measuretype);
		measureData.setSyncstate(syncstate);
		return measureData;
	}

	public ArrayList<MeasureData> getMeasureDataByRailNum(String wonum, String assetnum, String gh, double startMeasure, double endMeasure) {
		Cursor cursor = db.rawQuery(
				"select * from cp1raw_zxmain_r1 where flag_v='2' and status='0' and  wonum = ? and assetnum=? and startmeasure = ? and endmeasure = ?  and gh = ?",
				new String[] { wonum,assetnum, startMeasure + "", endMeasure + "", gh });
		int itemCount = cursor.getCount();
		ArrayList<MeasureData> datas = new ArrayList<MeasureData>();
		if (itemCount > 0) {
			while (cursor.moveToNext()) {
				MeasureData measureData = getMeasureDataByCurrentCursor(cursor);
				datas.add(measureData);
			}
		}
		return datas;
	}

	public MeasureData getLastCurveMeasureData(String wonum, int assetfeatureid) {
		Cursor cursor = db.rawQuery("select * from cp1raw_zxmain_r1 where wonum=? and assetfeatureid=? order by linenum desc", new String[] { wonum,
				assetfeatureid + "" });
		int itemCount = cursor.getCount();
		MeasureData measureData = null;
		if (itemCount > 0) {
			cursor.moveToFirst();
			measureData = getMeasureDataByCurrentCursor(cursor);
		}
		return measureData;
	}

	public MeasureData getCurveMeasueDataByPoint(String wonum, int assetfeatureid, int linenum) {
		Cursor cursor = db.rawQuery("select * from cp1raw_zxmain_r1 where wonum=? and assetfeatureid=? and linenum=?", new String[] { wonum,
				assetfeatureid + "", linenum + "" });
		int itemCount = cursor.getCount();
		MeasureData measureData = null;
		if (itemCount > 0) {
			cursor.moveToFirst();
			measureData = getMeasureDataByCurrentCursor(cursor);
		}
		return measureData;
	}

	/**
	 * 获取最后一次编辑的页面数据
	 */
	public MeasureData getLastTurnoutMeasureData() {

		return null;
	}

	public boolean savePage4Data(MeasureData data) {
		Cursor cursor = db.rawQuery(
						"select * from cp1raw_zxmain_r1 where flag_v =? and assetattrid =? and cp1nanumcfgrownum=? and assetnum=? and wonum=? and tag=? and gh=?",
						new String[] { data.getFlag_v(), data.getAssetattrid(), data.getCp1nanumcfgrownum(), data.getAssetnum(), data.getWonum(),data.getTag(), data.getGh() });
		if (cursor.getCount() > 0) {
			// 数据已存在，更新数据
			cursor.moveToFirst();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			return updateMeasureData(data, id);
		} else {
			// 数据不存在，插入数据
			return insertMeasureData(data);
		}
	}
	
	public boolean savePage4Data2(MeasureData data) {
		Cursor cursor = db.rawQuery(
						"select * from cp1raw_zxmain_r1 where flag_v =? and assetattrid =? and cp1nanumcfgrownum=? and assetnum=? and wonum=? and gh=? and linenum=?",
						new String[] { data.getFlag_v(), data.getAssetattrid(), data.getCp1nanumcfgrownum(), data.getAssetnum(), data.getWonum(), data.getGh() ,data.getLinenum()+""
								});
		if (cursor.getCount() > 0) {
			// 数据已存在，更新数据
			cursor.moveToFirst();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			return updateMeasureData2(data, id);
		} else {
			// 数据不存在，插入数据
			return insertMeasureData(data);
		}
	}

	private boolean updateMeasureData2(MeasureData data, int id) {
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put("nametype", data.getNametype());
			values.put("inspdate", data.getInspdate());
			values.put("xvalue", data.getXvalue());
			values.put("yvalue", data.getYvalue());
			
			String[] splits = data.getTag().split("_");
			String rowNumStr=splits[1];
			if("0".equals(rowNumStr)){
				values.put("value1", data.getValue1());
			}else{
				values.put("value2", data.getValue2());
			}
			
			values.put("cp1nanumcfgrownum", data.getCp1nanumcfgrownum());
			values.put("status", data.getStatus());
			int num = db.update("cp1raw_zxmain_r1", values, "id=?", new String[] { id + "" });
			db.setTransactionSuccessful();
			if (num > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			db.endTransaction();
		}

	}
	
	
	public boolean updateTurnoutMeasureDataState(String wonum, String assetnum, int gh) {
		ContentValues values = new ContentValues();
		values.put("syncstate", 0);
		int updateNum = db.update("cp1raw_zxmain_r1", values, "wonum=? and assetnum=? and gh=?", new String[] { wonum, assetnum, gh + "" });
		if (updateNum > 0) {
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<MeasureData> getTurnoutDataByGh(String wonum, String assetnum, int partOrder) {
		Cursor cursor = db.rawQuery("select * from cp1raw_zxmain_r1 where flag_v ='0' and status='0' and wonum=? and assetnum=? and gh=?", new String[] { wonum,
				assetnum, partOrder + "" });
		int itemCount = cursor.getCount();
		ArrayList<MeasureData> datas = new ArrayList<MeasureData>();
		if (itemCount > 0) {
			while (cursor.moveToNext()) {
				MeasureData measureData = getMeasureDataByCurrentCursor(cursor);
				datas.add(measureData);
			}
		}
		return datas;
	}

	/**
	 * 插入主表数据
	 * 
	 * @param mainData
	 */
	public boolean saveCP1RAW_ZXMAIN_Curve(MainMeasureData mainData) {
		Cursor cursor = db.rawQuery("select * from cp1raw_zxmain where wonum=? and assetnum=? and assetfeatureid=?",
				new String[] { mainData.getWonum(), mainData.getAssetnum(), mainData.getAssetfeatureid() + "" });
		if (cursor.getCount() > 0) {
			// 更新数据
			return updateCP1RAW_ZXMAIN_Curve(mainData);
		} else {
			// 插入数据
			return insertCP1RAW_ZXMAIN(mainData);
		}
	}

	private boolean updateCP1RAW_ZXMAIN_Curve(MainMeasureData mainData) {
		ContentValues values = new ContentValues();
		values.put("zx_zhd_value1", mainData.getZx_zhd_value1());
		values.put("zx_zhd_value2", mainData.getZx_zhd_value2());
		values.put("zx_hyd_value1", mainData.getZx_hyd_value1());
		values.put("zx_hyd_value2", mainData.getZx_hyd_value2());
		values.put("zx_yhd_value1", mainData.getZx_yhd_value1());
		values.put("zx_yhd_value2", mainData.getZx_yhd_value2());
		values.put("zx_hzd_value1", mainData.getZx_hzd_value1());
		values.put("zx_hzd_value2", mainData.getZx_hzd_value2());
		int result = db.update("cp1raw_zxmain", values, "wonum=? and assetnum=? and assetfeatureid=?",
				new String[] { mainData.getWonum(), mainData.getAssetnum(), mainData.getAssetfeatureid() + "" });
		if (result > 0) {
			return true;
		} else {
			return false;
		}
	}

	public MainMeasureData getCP1RAW_ZXMAIN_Curve(String wonum, String assetnum, int assetfeatureid) {
		Cursor cursor = db.rawQuery("select * from cp1raw_zxmain where wonum=? and assetnum=? and assetfeatureid=?", new String[] { wonum, assetnum,
				assetfeatureid + "" });
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return getCP1RAW_ZXMAINByCursor(cursor);
		} else {
			return null;
		}
	}

	public boolean insertCP1RAW_ZXMAIN(MainMeasureData mainData) {
		ContentValues values = new ContentValues();
		values.put("orgid", mainData.getOrgid());
		values.put("siteid", mainData.getSiteid());
		values.put("wonum", mainData.getWonum());
		values.put("assetnum", mainData.getAssetnum());
		values.put("strartlc", mainData.getStrartlc());
		values.put("endlc", mainData.getEndlc());
		values.put("gh", mainData.getGh());
		values.put("hasld", mainData.getHasld());
		values.put("assetfeatureid", mainData.getAssetfeatureid());
		values.put("zx_zhd_value1", mainData.getZx_zhd_value1());
		values.put("zx_zhd_value2", mainData.getZx_zhd_value2());
		values.put("zx_hyd_value1", mainData.getZx_hyd_value1());
		values.put("zx_hyd_value2", mainData.getZx_hyd_value2());
		values.put("zx_yhd_value1", mainData.getZx_yhd_value1());
		values.put("zx_yhd_value2", mainData.getZx_yhd_value2());
		values.put("zx_hzd_value1", mainData.getZx_hzd_value1());
		values.put("zx_hzd_value2", mainData.getZx_yhd_value2());
		values.put("syncstate", mainData.getSyncstate());
		long result = db.insert("cp1raw_zxmain", null, values);
		if (result > 0) {
			return true;
		} else {
			return false;
		}
	}

	public MainMeasureData getCP1RAW_ZXMAINByCursor(Cursor cursor) {
		if (cursor.getCount() > 0) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String orgid = cursor.getString(cursor.getColumnIndex("orgid"));
			String siteid = cursor.getString(cursor.getColumnIndex("siteid"));
			String wonum = cursor.getString(cursor.getColumnIndex("wonum"));
			String assetnum = cursor.getString(cursor.getColumnIndex("assetnum"));
			double strartlc = cursor.getDouble(cursor.getColumnIndex("strartlc"));
			double endlc = cursor.getDouble(cursor.getColumnIndex("endlc"));
			String gh = cursor.getString(cursor.getColumnIndex("gh"));
			int hasld = cursor.getInt(cursor.getColumnIndex("hasld"));
			int assetfeatureid = cursor.getInt(cursor.getColumnIndex("assetfeatureid"));

			String zx_zhd_value1 = cursor.getString(cursor.getColumnIndex("zx_zhd_value1"));
			String zx_zhd_value2 = cursor.getString(cursor.getColumnIndex("zx_zhd_value2"));
			String zx_hyd_value1 = cursor.getString(cursor.getColumnIndex("zx_hyd_value1"));
			String zx_hyd_value2 = cursor.getString(cursor.getColumnIndex("zx_hyd_value2"));
			String zx_yhd_value1 = cursor.getString(cursor.getColumnIndex("zx_yhd_value1"));
			String zx_yhd_value2 = cursor.getString(cursor.getColumnIndex("zx_yhd_value2"));
			String zx_hzd_value1 = cursor.getString(cursor.getColumnIndex("zx_hzd_value1"));
			String zx_hzd_value2 = cursor.getString(cursor.getColumnIndex("zx_hzd_value2"));

			int syncstate = cursor.getInt(cursor.getColumnIndex("syncstate"));
			// ////////////////////////////////////////
			MainMeasureData mainMeasureData = new MainMeasureData();
			mainMeasureData.setId(id);
			mainMeasureData.setOrgid(orgid);
			mainMeasureData.setSiteid(siteid);
			mainMeasureData.setWonum(wonum);
			mainMeasureData.setAssetnum(assetnum);
			mainMeasureData.setStrartlc(strartlc);
			mainMeasureData.setEndlc(endlc);
			mainMeasureData.setGh(gh);
			mainMeasureData.setHasld(hasld);
			mainMeasureData.setAssetfeatureid(assetfeatureid);
			mainMeasureData.setZx_zhd_value1(zx_zhd_value1);
			mainMeasureData.setZx_zhd_value2(zx_zhd_value2);
			mainMeasureData.setZx_hyd_value1(zx_hyd_value1);
			mainMeasureData.setZx_hyd_value2(zx_hyd_value2);
			mainMeasureData.setZx_yhd_value1(zx_yhd_value1);
			mainMeasureData.setZx_yhd_value2(zx_yhd_value2);
			mainMeasureData.setZx_hzd_value1(zx_hzd_value1);
			mainMeasureData.setZx_hzd_value2(zx_hzd_value2);
			mainMeasureData.setSyncstate(syncstate);
			return mainMeasureData;
		} else {
			return null;
		}
	}

	// 问题详细表
		public boolean saveTurnoutQuestion(MeasureData data) {
			Cursor cursor = db
					.rawQuery("select * from cp1raw_zxmain_r1 where status='-1' and siteid=? and wonum=? and assetnum=? and startmeasure=? and endmeasure=? and gh=? and linenum=?",
							new String[] { data.getSiteid(), data.getWonum(),data.getAssetnum(), data.getStartmeasure() + "", data.getEndmeasure() + "", data.getGh(),
									data.getLinenum() + "" });
			if (cursor.getCount() > 0) {
				// 数据已存在，更新数据
				cursor.moveToFirst();
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				return updateMeasureData(data, id);
			} else {
				// 数据不存在，插入数据
				return insertMeasureData(data);
			}
		}
	
	// 问题详细表
	public boolean saveQuestion(MeasureData data) {
		Cursor cursor = db
				.rawQuery("select * from cp1raw_zxmain_r1 where status='-1' and siteid=? and wonum=? and startmeasure=? and endmeasure=? and gh=? and linenum=?",
						new String[] { data.getSiteid(), data.getWonum(), data.getStartmeasure() + "", data.getEndmeasure() + "", data.getGh(),
								data.getLinenum() + "" });
		if (cursor.getCount() > 0) {
			// 数据已存在，更新数据
			cursor.moveToFirst();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			return updateMeasureData(data, id);
		} else {
			// 数据不存在，插入数据
			return insertMeasureData(data);
		}
	}

	/**
	 * 获取一个问题的详细数据
	 * 
	 * @param measureData
	 * @return
	 */
	public MeasureData getQuestion(MeasureData measureData) {
		Cursor cursor = db
				.rawQuery(
						"select * from cp1raw_zxmain_r1 where status='-1' and siteid=? and wonum=? and startmeasure=? and endmeasure=? and gh=? and linenum=?",
						new String[] { measureData.getSiteid(), measureData.getWonum(), measureData.getStartmeasure() + "",
								measureData.getEndmeasure() + "", measureData.getGh(), measureData.getLinenum() + "" });
		if (cursor.getCount() > 0) {
			// 数据已存在，更新数据
			cursor.moveToFirst();
			return getMeasureDataByCurrentCursor(cursor);
		} else {
			return null;
		}
	}

	/**
	 * 获取直线中指定轨号的所有问题
	 * 
	 * @param measureData
	 * @return
	 */
	public List<MeasureData> getQuestions(MeasureData measureData) {
		Cursor cursor = db.rawQuery(
				"select * from cp1raw_zxmain_r1 where status='-1' and siteid=? and wonum=? and assetnum=? and startmeasure=? and endmeasure=? and gh=? ",
				new String[] { measureData.getSiteid(), measureData.getWonum(),measureData.getAssetnum(), measureData.getStartmeasure() + "", measureData.getEndmeasure() + "",
						measureData.getGh() });
		if (cursor.getCount() > 0) {
			// 数据已存在，更新数据
			List<MeasureData> datas = new ArrayList<MeasureData>();
			while (cursor.moveToNext()) {
				MeasureData data = getMeasureDataByCurrentCursor(cursor);
				datas.add(data);
			}
			return datas;
		} else {
			return null;
		}
	}

	/**
	 * 获取指定道岔的所有问题
	 * 
	 * @param measureData
	 * @return
	 */
	public List<MeasureData> getTurnoutQuestions(MeasureData measureData) {
		// 获取：问题 道岔
		Cursor cursor = db.rawQuery("select * from cp1raw_zxmain_r1 where status='-1' and flag_v='0' and wonum=? and assetnum=? and gh=? ",//
				new String[] { measureData.getWonum(), measureData.getAssetnum(), measureData.getGh() });
		if (cursor.getCount() > 0) {
			// 数据已存在，更新数据
			List<MeasureData> datas = new ArrayList<MeasureData>();
			while (cursor.moveToNext()) {
				MeasureData data = getMeasureDataByCurrentCursor(cursor);
				datas.add(data);
			}
			return datas;
		} else {
			return null;
		}
	}
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
	public ArrayList<TableLevelOne> getAllTableLevelOne() {
		Cursor cursor = db.rawQuery("select distinct orgid,siteid,wonum,hasld from CP1RAW_ZXMAIN_R1 where syncstate <> 1 order by inspdate",null);
		if(cursor.getCount()>0){
			ArrayList<TableLevelOne> tableLevelOnes=new ArrayList<TableLevelOne>();
			while(cursor.moveToNext()){
				String orgid=cursor.getString(cursor.getColumnIndex("orgid"));
				String siteid=cursor.getString(cursor.getColumnIndex("siteid"));
				String wonum=cursor.getString(cursor.getColumnIndex("wonum"));
				int hasld=cursor.getInt(cursor.getColumnIndex("hasld"));
				
				TableLevelOne tableLevelOne=new TableLevelOne();
				tableLevelOne.setHasld(hasld);
				tableLevelOne.setOrgid(orgid);
				tableLevelOne.setSiteid(siteid);
				tableLevelOne.setWonum(wonum);
				
				tableLevelOnes.add(tableLevelOne);
			}
			
			return tableLevelOnes;
		}else{
			return null;
		}
	}
	
	public ArrayList<MainMeasureData> getTableLevelTwoInStraightByWonum(String wonum){
		Cursor cursor = db.rawQuery("select distinct startmeasure,endmeasure,gh,siteid,orgid,wonum,assetnum,hasld   from CP1RAW_ZXMAIN_R1 where wonum=? and flag_v=2 and syncstate <> 1 order by startmeasure,endmeasure,gh", new String[]{wonum});
		if(cursor.getCount()>0){
			ArrayList<MainMeasureData> datas=new ArrayList<MainMeasureData>();
			while(cursor.moveToNext()){
				double startmeasure = cursor.getDouble(cursor.getColumnIndex("startmeasure"));
				double endmeasure=cursor.getDouble(cursor.getColumnIndex("endmeasure"));
				String gh=cursor.getString(cursor.getColumnIndex("gh"));
				String siteid=cursor.getString(cursor.getColumnIndex("siteid"));
				String orgid=cursor.getString(cursor.getColumnIndex("orgid"));
				String assetnum=cursor.getString(cursor.getColumnIndex("assetnum"));
				int hasld=cursor.getInt(cursor.getColumnIndex("hasld"));
				
				MainMeasureData data=new MainMeasureData();
				data.setStrartlc(startmeasure);
				data.setEndlc(endmeasure);
				data.setGh(gh);
				data.setOrgid(orgid);
				data.setSiteid(siteid);
				data.setWonum(wonum);
				data.setAssetnum(assetnum);
				data.setHasld(hasld);
				
				datas.add(data);
			}
			return datas;
		}else{
			return null;
		}
	}

	
	/**
	 * 获取曲线的二级列表数据
	 * @param wonum
	 * @return
	 */
	public ArrayList<MainMeasureData> getTableLevelTwoInCurveByWonum(String wonum) {
		Cursor cursor = db.rawQuery("select * from CP1RAW_ZXMAIN where wonum=? ", new String[]{wonum});
		if(cursor.getCount()>0){
			ArrayList<MainMeasureData> datas=new ArrayList<MainMeasureData>();
			while(cursor.moveToNext()){
				datas.add(getCP1RAW_ZXMAINByCursor(cursor));
			}
			return datas;
		}else{
			return null;
		}
	}

	public ArrayList<MainMeasureData> getTableLevelTwoInTurnoutByWonum(String wonum) {
		Cursor cursor = db.rawQuery("select distinct assetnum  from CP1RAW_ZXMAIN_R1 where wonum=? and flag_v=0 and syncstate <> 1 ", new String[]{wonum});
		if(cursor.getCount()>0){
			ArrayList<MainMeasureData> datas=new ArrayList<MainMeasureData>();
			while(cursor.moveToNext()){
				String assetnum=cursor.getString(cursor.getColumnIndex("assetnum"));
				MainMeasureData data=new MainMeasureData();
				data.setAssetnum(assetnum);
				data.setWonum(wonum);
				datas.add(data);
			}
			return datas;
		}else{
			return null;
		}
	}
	
	public ArrayList<MeasureData> getTableLevelThreeInStraight(MainMeasureData mainMeasureData) {
		Cursor cursor = db.rawQuery("select * from CP1RAW_ZXMAIN_R1 where startmeasure=? and endmeasure=? and gh=? and flag_v=2 and syncstate <> 1 order by startmeasure,endmeasure,gh,linenum", new String[]{mainMeasureData.getStrartlc()+"",mainMeasureData.getEndlc()+"",mainMeasureData.getGh()});
		if(cursor.getCount()>0){
			ArrayList<MeasureData> datas=new ArrayList<MeasureData>();
			while(cursor.moveToNext()){
				datas.add(getMeasureDataByCurrentCursor(cursor));
			}
			return datas;
		}else{
			return null;
		}
	}

	public ArrayList<MeasureData> getTableLevelThreeInCurve(MainMeasureData mainMeasureData) {
		Cursor cursor = db.rawQuery("select * from CP1RAW_ZXMAIN_R1 where  flag_v=1 and syncstate <> 1  and wonum=? and assetnum=? and assetfeatureid=?  order by linenum", new String[]{mainMeasureData.getWonum(),mainMeasureData.getAssetnum(),mainMeasureData.getAssetfeatureid()+""});
		if(cursor.getCount()>0){
			ArrayList<MeasureData> datas=new ArrayList<MeasureData>();
			while(cursor.moveToNext()){
				datas.add(getMeasureDataByCurrentCursor(cursor));
			}
			return datas;
		}else{
			return null;
		}
	}

	public ArrayList<MeasureData> getTableLevelThreeInTurnout(MainMeasureData mainMeasureData) {
		Cursor cursor = db.rawQuery("select * from CP1RAW_ZXMAIN_R1 where wonum=? and assetnum=? and flag_v=0 and syncstate <> 1 order by gh,status,linenum", new String[]{mainMeasureData.getWonum(),mainMeasureData.getAssetnum()});
		if(cursor.getCount()>0){
			ArrayList<MeasureData> datas=new ArrayList<MeasureData>();
			while(cursor.moveToNext()){
				datas.add(getMeasureDataByCurrentCursor(cursor));
			}
			return datas;
		}else{
			return null;
		}
	}
	
	/**
	 * 上传成功则传入1
	 * @param syncstate
	 */
	public boolean setSyncState(int syncstate){
		ContentValues values=new ContentValues();
		values.put("syncstate", syncstate);
		int updateResult = db.update("CP1RAW_ZXMAIN_R1", values, null, null);
		if(updateResult>0){
			 updateResult = db.update("CP1RAW_ZXMAIN", values, null, null);
			 if(updateResult>0){
				 return true;
			 }else{
				 return false;
			 }
		}else{
			return false;
		}
	}
}
