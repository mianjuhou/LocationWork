package com.jtv.locationwork.dao;

import com.jtv.dbentity.dao.BaseDaoImpl;
import com.jtv.dbentity.util.SDSqliteDateBase;
import com.jtv.locationwork.entity.CheckEntity;
import com.jtv.locationwork.entity.DBFileState;
import com.jtv.locationwork.entity.LocalCache;

import android.content.Context;

public class DBFactory {

	private static BaseDaoImpl<LocalCache> localcache;
	private static BaseDaoImpl<CheckEntity> check;
	private static BaseDaoImpl<DBFileState> file;

	private DBFactory() {
	};

	private static SDSqliteDateBase factory;
	private static Context con;
	private static Object con1;

	public static void init(Context con) {
		DBFactory.con = con;
	}

	public static SDSqliteDateBase getConnection() {
		return getConnection(null);
	}

	public static SDSqliteDateBase getConnection(Context con) {
		if (con1 != null)
			init(con);
		if (factory == null)
			factory = new DBObjectHelper(con);

		return factory;
	}

	public static BaseDaoImpl<DBFileState> getDBFileState(Context con) {
		if (file == null) {
			file = creat(file, new FileStateDao(con));
		}
		return file;
	}

	public static BaseDaoImpl<LocalCache> getLocalCacheDao(Context con) {
		if (localcache == null) {
			localcache = creat(localcache, new LocalCacheImp(con));
		}
		return localcache;
	}

	public static BaseDaoImpl<CheckEntity> getCheckQuestionDao(Context con) {
		if (check == null) {
			check = creat(check, new CheckEntityImp(con));
		}
		return check;
	}

	private static BaseDaoImpl creat(BaseDaoImpl switchf, BaseDaoImpl creatOK) {
		if (switchf == null) {
			switchf = creatOK;
		}
		return switchf;
	}
}
