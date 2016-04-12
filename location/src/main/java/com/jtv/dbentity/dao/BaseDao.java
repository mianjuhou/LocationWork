package com.jtv.dbentity.dao;

import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;
import java.util.Map;

public interface BaseDao<T> {

	public SQLiteOpenHelper getDbHelper();

	public abstract long insert(T entity);


	public abstract long insert(T entity, boolean flag);

	public abstract void delete(String id);

	public abstract void delete(String... ids);

	public abstract void update(T entity);

	public abstract T get(String id);

	public abstract List<T> rawQuery(String sql, String[] selectionArgs);

	public abstract List<T> find();

	public abstract List<T> find(String[] columns, String selection,
								 String[] selectionArgs, String groupBy, String having,
								 String orderBy, String limit);

	public abstract boolean isExist(String sql, String[] selectionArgs);


	public List<Map<String, String>> query2MapList(String sql,
												   String[] selectionArgs);

	public void execSql(String sql, Object[] selectionArgs);

}