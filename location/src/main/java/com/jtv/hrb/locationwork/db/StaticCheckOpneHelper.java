package com.jtv.hrb.locationwork.db;

import java.io.File;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StaticCheckOpneHelper extends SQLiteOpenHelper{
	public static int VERSION=1;
	public static String mDdName=SDBHelper.DB_DIR+File.separator+"staticcheck.db";
	public StaticCheckOpneHelper(Context context) {
		super(context,mDdName, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table zx_lc(_id INTEGER PRIMARY KEY AUTOINCREMENT,wonum INTEGER,startlc NUMERIC(16,5),endlc NUMERIC(16,5))");
		db.execSQL("create table zx_gh(_id INTEGER PRIMARY KEY AUTOINCREMENT,lc_id INTEGER,gh INTEGER,xvalue VARCHAR,yvalue VARCHAR,time VARCHAR");
		db.execSQL("create table zx_hh(_id INTEGER PRIMARY KEY AUTOINCREMENT,gh_id INTEGER,linenum INTEGER,value1 INTEGER,value2 INTEGER");
		db.execSQL("create table zx_wt(_id INTEGER PRIMARY KEY AUTOINCREMENT,gh_id INTEGER,questdesc VARCHAR");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
