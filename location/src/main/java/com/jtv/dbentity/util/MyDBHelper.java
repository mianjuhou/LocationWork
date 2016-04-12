package com.jtv.dbentity.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MyDBHelper extends SDSqliteDateBase {

	public MyDBHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion,
			Class<?>[] modelClasses) {
		super(context, databaseName, factory, databaseVersion, modelClasses);
	}

	@Override
	protected void initDatabase(SQLiteDatabase database) {
		TableHelper.createTablesByClasses(database, this.modelClasses);
	}

	public void onCreate(SQLiteDatabase db) {
		TableHelper.createTablesByClasses(db, this.modelClasses);
//		db.execSQL("create table IF NOT EXISTS zx_lc(_id INTEGER PRIMARY KEY AUTOINCREMENT,wonum VARCHAR,assetnum VARCHAR,startlc NUMERIC(16,5),endlc NUMERIC(16,5))");
//		db.execSQL("create table IF NOT EXISTS zx_gh(_id INTEGER PRIMARY KEY AUTOINCREMENT,lc_id INTEGER,gh INTEGER,xvalue VARCHAR,yvalue VARCHAR,time VARCHAR)");
//		db.execSQL("create table IF NOT EXISTS zx_hh(_id INTEGER PRIMARY KEY AUTOINCREMENT,gh_id INTEGER,linenum INTEGER,value1 INTEGER,value2 INTEGER)");
//		db.execSQL("create table IF NOT EXISTS zx_wt(_id INTEGER PRIMARY KEY AUTOINCREMENT,gh_id INTEGER,questdesc VARCHAR)");
//		//建立道岔表
//		db.execSQL("create table IF NOT EXISTS dc_zz(_id INTEGER PRIMARY KEY AUTOINCREMENT,assetnum varchar,a_jbgjt_1 INTEGER,a_jbgjt_2 INTEGER,a_jgjd_1 INTEGER,a_jgjd_2 INTEGER,a_jgz_1 INTEGER,a_jgz_2 INTEGER,a_jggd_1 INTEGER,a_jggd_2 INTEGER,a_jggd_3 INTEGER,a_jggd_4 INTEGER)");
//		db.execSQL("create table IF NOT EXISTS dc_dqx(_id INTEGER PRIMARY KEY AUTOINCREMENT,assetnum varchar,b_zx_q_1 INTEGER,b_zx_q_2 INTEGER,b_zx_z_1 INTEGER,b_zx_z_2 INTEGER,b_zx_h_1 INTEGER,b_zx_h_2 INTEGER,b_dqx_q_1 INTEGER,b_dqx_q_2 INTEGER,b_dqx_z_1 INTEGER,b_dqx_z_2 INTEGER,b_dqx_h_1 INTEGER,b_dqx_h_2 INTEGER)");
//		db.execSQL("create table IF NOT EXISTS dc_zc(_id INTEGER PRIMARY KEY AUTOINCREMENT,assetnum varchar,c_cxq_zhi_1 INTEGER,c_cxq_zhi_2 INTEGER,c_cxq_qu_1 INTEGER,c_cxq_qu_2 INTEGER,c_cxz_zhi_1 INTEGER,c_cxz_qu_1 INTEGER,c_cxh_zhi_1 INTEGER,c_cxh_zhi_2 INTEGER,c_cxh_qu_1 INTEGER,c_cxh_qu_2 INTEGER,c_czjg_zhi_1 INTEGER,c_czjg_qu_1 INTEGER,c_hbjl_zhi_1 INTEGER,c_hbjl_qu_1 INTEGER)");
//		db.execSQL("create table IF NOT EXISTS dc_zj(_id INTEGER PRIMARY KEY AUTOINCREMENT,assetnum varchar,linenum INTEGER,planvalue INTEGER,r_value INTEGER)");
//		db.execSQL("create table IF NOT EXISTS dc_quest(_id INTEGER PRIMARY KEY AUTOINCREMENT,turnoutid INTEGER,assetnum varchar,partType INTEGER,questDesc varchar)");
		db.execSQL("create table cp1raw_zxmain_r1(id INTEGER PRIMARY KEY AUTOINCREMENT,tag VARCHAR,siteid VARCHAR2(8),orgid VARCHAR2(8),gh INTEGER,startmeasure NUMBER(16,3),endmeasure NUMBER(16,3),linenum NUMBER,value1 NUMBER(16,2),value2 NUMBER(16,2),hasld NUMBER not null,assetattrid VARCHAR2(30),assetnum VARCHAR2(28),assetfeatureid NUMBER,wonum VARCHAR2(10),nametype VARCHAR2(50),status VARCHAR2(10),defectclass VARCHAR2(2),fvalue NUMBER(16,2),flag_v VARCHAR2(20),xvalue VARCHAR2(50),yvalue VARCHAR2(50),inspdate VARCHAR,cp1nanumcfgrownum  VARCHAR2(30),measuretype integer,syncstate INTEGER default -1)");
		db.execSQL("create table CP1RAW_ZXMAIN(id INTEGER PRIMARY KEY AUTOINCREMENT,siteid VARCHAR2(8), orgid  VARCHAR2(8), assetnum VARCHAR2(28), strartlc NUMBER(16,5), endlc  NUMBER(16,5), gh INTEGER, hasld  NUMBER not null, wonum  VARCHAR2(10), assetfeatureid  NUMBER, zx_zhd_value1 VARCHAR2(10), zx_zhd_value2 VARCHAR2(10), zx_hyd_value1 VARCHAR2(10), zx_hyd_value2 VARCHAR2(10), zx_yhd_value1 VARCHAR2(10), zx_yhd_value2 VARCHAR2(10), zx_hzd_value1 VARCHAR2(10), zx_hzd_value2 VARCHAR2(10),syncstate INTEGER default -1)");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		TableHelper.dropTablesByClasses(db, this.modelClasses);
		onCreate(db);
	}

}