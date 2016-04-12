package com.jtv.dbentity.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jtv.locationwork.util.LogUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;

public class SDSqliteDateBase extends SQLiteOpenHelper {

	protected Context context;
	protected String databaseName;
	protected int databaseVersion;
	protected Class<?>[] modelClasses;
	private boolean showSql;
	private SQLiteDatabase db;

	public SDSqliteDateBase(Context context, String databaseName, SQLiteDatabase.CursorFactory factory,
			int databaseVersion) {
		super(context, databaseName, factory, databaseVersion);
		this.context = context;
		this.databaseName = databaseName;
		this.databaseVersion = databaseVersion;
		createInitDatabase();
	}

	public SDSqliteDateBase(Context context, String databaseName, CursorFactory factory, int databaseVersion,
			Class<?>[] modelClasses) {
		super(context, databaseName, factory, databaseVersion);
		this.context = context;
		this.databaseName = databaseName;
		this.databaseVersion = databaseVersion;
		this.modelClasses = modelClasses;
		createInitDatabase();
	}

	/**
	 * 查询表的所有字段数据
	 * 
	 * @param table
	 *            表名
	 * @param orderBy
	 *            排序函数
	 * @return String[][]查询的数据
	 */
	public String[][] queryAll(String table, String orderBy) {
		return this.query(table, null, null, null, null, null, orderBy);
	}

	/**
	 * 按条件查询表的特定字段数据
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            要查询的列,null为所有列
	 * @param selection
	 *            查询条件 a=b and a2=c
	 * @param selectionArgs
	 *            查询条件对应的值，如果selection已经包括值，此处为null即可
	 * @param groupBy
	 *            分组函数
	 * @param having
	 *            having函数
	 * @param orderBy
	 *            排序函数
	 * @return String[][]查询的数据
	 */
	public String[][] query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		SQLiteDatabase db = null;
		String[][] ret = null;
		try {
			db = this.getReadableDatabase();
		} catch (Exception e) {
		}
		try {
			if (columns == null)
				columns = MetaTool.getTableCols(table);
			Cursor c = null;
			try {
				c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
				if (showSql)
					LogUtil.i("查询数组数据" + table + " 条件:" + selection + "->" + orderBy + "->返回行数:" + c.getCount());
				ret = MetaTool.CursorToArray(c);
			} catch (Exception e) {
				LogUtil.e("查询数据失败," + e.getMessage());
			}
			try {
				if (!c.isClosed())
					c.close();
			} catch (Exception e) {
			}
			try {
				db.close();
			} catch (Exception e) {
			}
			return ret;
		} catch (Exception e) {
			LogUtil.e("CommSqlHelper query error " + table + "<" + db, e);
			return null;
		} finally {
			try {
				db.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 查询表数据，并返回为List<Map>格式
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            要查询的字段
	 * @param selection
	 *            查询条件
	 * @param selectionArgs
	 *            查询条件的值
	 * @param groupBy
	 *            分组函数
	 * @param having
	 *            having函数
	 * @param orderBy
	 *            排序函数
	 * @return List<Map>数据
	 */
	public List<HashMap<String, String>> queryList(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy) {
		SQLiteDatabase db = null;
		List<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
		try {
			db = this.getReadableDatabase();
		} catch (Exception e) {
		}
		try {
			if (columns == null)
				columns = MetaTool.getTableCols(table);
			Cursor c = null;
			try {
				c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
				if (showSql)
					LogUtil.i("查询列表数据" + table + " 条件:" + selection + "->" + orderBy + "->返回行数：" + c.getCount());
				ret = MetaTool.CursorToList(c);
			} catch (Exception e) {
				LogUtil.e("查询数据失败," + e.getMessage());
			}
			try {
				c.close();
			} catch (Exception e) {
			}
			try {
				db.close();
			} catch (Exception e) {
			}
			return ret;
		} catch (Exception e) {
			LogUtil.e("CommSqlHelper query error " + table + "<" + db, e);
			return ret;
		} finally {
			try {
				db.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 以游标的形式查询数据
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            要查询的字段
	 * @param selection
	 *            查询条件
	 * @param selectionArgs
	 *            查询条件的值
	 * @param groupBy
	 *            分组函数
	 * @param having
	 *            having函数
	 * @param orderBy
	 *            排序函数
	 * @return Cursor游标，记得要及时关闭
	 */
	public Cursor queryCursor(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		try {
			db = this.getReadableDatabase();
		} catch (Exception e) {
			LogUtil.e("查询数据失败" + table, e);
		}
		try {
			Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
			if (showSql)
				LogUtil.i("查询游标数据" + table + " 条件:" + selection + "->" + orderBy + "->返回行数:" + c.getCount());
			return c;
		} catch (Exception e) {
			LogUtil.e("CommSqlHelper, query error " + table + "<" + e.getMessage());
			return null;
		} finally {
			// try{
			// db.close();
			// }catch(Exception e){}
		}

	}

	/**
	 * 插入数据
	 * 
	 * @param table_name
	 *            表名
	 * @param values
	 *            列-值对象
	 * @return 插入结果，1 成功 -1 失败
	 */
	public int insert(String table, ContentValues values) {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
		} catch (Exception e) {
		}
		int row = -1;
		try {
			row = new Long(db.insert(table, null, values)).intValue();
			if (row > 1)
				row = 1;
			if (showSql)
				LogUtil.i("插入数据" + table + " values:" + values + "->影响行数" + row);
		} catch (Exception e) {
			LogUtil.e("插入数据失败," + e.getMessage());
		}
		try {
			db.close();
		} catch (Exception e1) {
			// LogUtil.e("query close", e1);
		}
		return row;
	}

	/**
	 * 删除数据
	 * 
	 * @param table_name
	 *            表名
	 * @param where
	 *            where条件
	 * @param where_values
	 *            where条件中的参数值
	 * @return 删除数据行数
	 */
	public long delete(String table, String where, String[] where_values) {
		SQLiteDatabase db = null;
		long ret = -1;
		try {
			db = this.getWritableDatabase();
		} catch (Exception e) {
		}
		try {
			ret = db.delete(table, where, where_values);
			if (showSql)
				LogUtil.i("删除数据" + table + " 条件:" + where_values + " ->删除行数" + ret);
		} catch (Exception e) {
			LogUtil.e("删除数据失败," + e.getMessage());
		}
		try {
			db.close();
		} catch (Exception e1) {
			// LogUtil.e("query close", e1);
		}
		return ret;
	}

	/**
	 * 更新数据
	 * 
	 * @param table_name
	 *            表名
	 * @param where
	 *            where条件
	 * @param where_values
	 *            where条件的参数值
	 * @param columns
	 *            要更新的数据 列-值对应关系
	 */
	public int update(String table, String where, String[] where_values, ContentValues columns) {
		SQLiteDatabase db = null;
		int ret = -1;
		try {
			db = this.getWritableDatabase();
		} catch (Exception e) {
		}
		try {
			ret = db.update(table, columns, where, where_values);
			if (showSql)
				LogUtil.i("更新数据" + table + " 条件:" + where + " 数据:" + columns + "->更新行数" + ret);
		} catch (Exception e) {
			LogUtil.e("更新数据失败[" + table + "]," + e.getMessage());
		}
		try {
			db.close();
		} catch (Exception e1) {
			// LogUtil.e("query close", e1);
		}
		return ret;
	}

	/**
	 * 执行sql
	 * 
	 * @param sql
	 *            要执行的sql
	 */
	public void executeSql(String sql) {
		executeSql(sql, true);
	}

	public void executeSql(String sql, boolean log) {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
		} catch (Exception e) {
		}
		try {
			if (log && showSql)
				LogUtil.i("执行sql" + sql);
			db.execSQL(sql);
		} catch (Exception e) {
			if (log)
				LogUtil.e("执行SQL失败," + e.getMessage());
		}
		try {
			db.close();
		} catch (Exception e1) {
			// LogUtil.e("query close", e1);
		}
	}

	/**
	 * 特殊的rawQuery数据
	 * 
	 * @param sql
	 *            查询sql
	 * @return String[][]数组
	 */
	public String[][] rawQuery(String sql) {
		SQLiteDatabase db = null;
		try {
			if (showSql)
				LogUtil.i("执行sql" + sql);
			db = this.getReadableDatabase();
		} catch (Exception e) {
		}
		Cursor c = null;
		String[][] ret = null;
		try {
			c = db.rawQuery(sql, null);
			ret = MetaTool.CursorToArray(c);
		} catch (Exception e) {
			LogUtil.e("rawQuery失败，" + e.getMessage());
		}
		try {
			if (!c.isClosed())
				c.close();
		} catch (Exception e) {
		}
		try {
			db.close();
		} catch (Exception e) {
		}
		return ret;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * 得到数据库文件的存储路径
	 * 返回null时使用系统默认的路径"/data/data/"+context.getPackageName()+"/database"
	 * 
	 * @return
	 */
	public String getUserDatabaseFolder() {
		return getSDRoot() + File.separator + context.getPackageName();// "/jtvdb";
		// return "/data/data/"+context.getPackageName()+"/database";
	}

	public String getSDRoot() {
		// return android.os.Environment
		// .getExternalStorageDirectory().getAbsolutePath();
		String sys_root = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			/* 存在sdcard并且具有读写权限 根目录为sdcard根目录 */
			sys_root = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			/* 不存在sdcard或者sdcard不具有读写权限 根目录为android系统data/data目录 */
			sys_root = Environment.getDataDirectory().getAbsolutePath();
		}
		return sys_root;
	}

	/**
	 * desc 初始化传输表的字段和数据
	 * 
	 * version 1.0 add by wangde 10/02/2014
	 * 
	 */
	protected void createInitTransParams() {
		String transName = "transparams";
		// 判断数据文件是否存在，不存在的话使用打包的数据文件
		String databaseFilename = getUserDatabaseFolder() + File.separator + transName + ".xml";
		File f = new File(databaseFilename);
		if (f.exists())
			return;

		File dir = new File(getUserDatabaseFolder());
		if (!dir.exists())
			dir.mkdirs();

		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = context.getResources().openRawResource(
					CommTool.getRID(transName, "raw", context.getResources(), context.getPackageName()));
			// 创建输出流
			fos = new FileOutputStream(databaseFilename);
			// 将数据输出
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
		} catch (Exception ee) {
			LogUtil.i("初始化数据库失败！");
		} finally {
			try {
				fos.close();
			} catch (Exception ee) {
			}
			try {
				is.close();
			} catch (Exception ee) {
			}
		}
	}

	/**
	 * 设置是否使用预制的数据库文件。 数据库文件应该存放在/res/raw/initdb.db
	 * 
	 * @return
	 */
	public boolean useManualInitDB() {
		return false;
	}

	/**
	 * 初始化数据库数据，比如一些超大表，可以将数据集成到文件中，在第一次运行时创建。
	 */
	public void createInitDatabase() {
		if (TextUtils.isEmpty(getUserDatabaseFolder())) {
			return;
		}
		// 判断数据文件是否存在，不存在的话使用打包的数据文件
		String databaseFilename = getUserDatabaseFolder() + File.separator + databaseName;
		File f = new File(databaseFilename);
		if (f.exists())
			return;

		File dir = new File(getUserDatabaseFolder());
		if (!dir.exists())
			dir.mkdirs();

		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = context.getResources().openRawResource(
					CommTool.getRID("initdb", "raw", context.getResources(), context.getPackageName()));
			// 创建输出流
			fos = new FileOutputStream(databaseFilename);
			// 将数据输出
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
		} catch (Exception ee) {
			LogUtil.i("初始化数据库失败！" + ee.toString());
		} finally {
			try {
				fos.close();
			} catch (Exception ee) {
			}
			try {
				is.close();
			} catch (Exception ee) {
			}
		}
		try {
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
			this.initDatabase(database);
			database.close();
		} catch (Exception ee) {
			LogUtil.i("初始化数据库失败！" + ee.toString());
		}
	}

	protected void initDatabase(SQLiteDatabase database) {

	}

	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		if (useManualInitDB())
			return this.getWritableDatabase();
		else
			return super.getWritableDatabase();
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		if (getUserDatabaseFolder() == null)
			return super.getWritableDatabase();

		try {
			if (useManualInitDB()) {
				createInitTransParams();
				// 現在不需要了???
				createInitDatabase();
			}
			// 得到数据库的完整路径名
			String databaseFilename = getUserDatabaseFolder() + File.separator + databaseName;

			// 得到SQLDatabase对象
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
			int oldVer = database.getVersion();
			database.setVersion(databaseVersion);
			if (databaseVersion > oldVer) {
				onUpgrade(database, oldVer, databaseVersion);
			}
			return database;
		} catch (Exception e) {
			LogUtil.e(e.getMessage());
		}
		return super.getWritableDatabase();
	}

}