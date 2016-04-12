package com.jtv.dbentity.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jtv.dbentity.annotation.Column;
import com.jtv.dbentity.annotation.Id;
import com.jtv.dbentity.annotation.PrimaryKey;
import com.jtv.dbentity.annotation.Table;
import com.jtv.dbentity.util.TableHelper;

public class BaseDaoImpl<T> implements BaseDao<T> {
	private String TAG = "AHibernate";
	private SQLiteOpenHelper dbHelper;
	private String tableName;
	private String idColumn;
	private Class<T> clazz;
	private List<Field> allFields;
	private static final int METHOD_INSERT = 0;
	private static final int METHOD_UPDATE = 1;

	private static final int TYPE_NOT_INCREMENT = 0;
	private static final int TYPE_INCREMENT = 1;

	public BaseDaoImpl(SQLiteOpenHelper dbHelper, Class<T> clazz) {
		this.dbHelper = dbHelper;
		if (clazz == null) {
			this.clazz = ((Class<T>) ((java.lang.reflect.ParameterizedType) super.getClass().getGenericSuperclass())
					.getActualTypeArguments()[0]);
		} else {
			this.clazz = clazz;
		}

		if (this.clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) this.clazz.getAnnotation(Table.class);
			this.tableName = table.name();
		}

		this.allFields = TableHelper.joinFields(this.clazz.getDeclaredFields(),
				this.clazz.getSuperclass().getDeclaredFields());

		for (Field field : this.allFields) {
			if (field.isAnnotationPresent(Id.class)) {
				Column column = (Column) field.getAnnotation(Column.class);
				this.idColumn = column.name();
				break;
			}
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				Column column = (Column) field.getAnnotation(Column.class);
				this.idColumn = column.name();
				break;
			}
		}

		Log.d(TAG, "clazz:" + this.clazz + " tableName:" + this.tableName + " idColumn:" + this.idColumn);
	}

	public BaseDaoImpl(SQLiteOpenHelper dbHelper) {
		this(dbHelper, null);
	}

	public SQLiteOpenHelper getDbHelper() {
		return dbHelper;
	}

	public T get(String id) {
		String selection = this.idColumn + " = ?";
		String[] selectionArgs = { id };
		Log.d(TAG, "[get]: select * from " + this.tableName + " where " + this.idColumn + " = '" + id + "'");
		List<T> list = find(null, selection, selectionArgs, null, null, null, null);
		if ((list != null) && (list.size() > 0)) {
			return (T) list.get(0);
		}
		return null;
	}

	public List<T> rawQuery(String sql, String[] selectionArgs) {
		Log.d(TAG, "[rawQuery]: " + getLogSql(sql, selectionArgs));

		List<T> list = new ArrayList<T>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			cursor = db.rawQuery(sql, selectionArgs);

			getListFromCursor(list, cursor);
		} catch (Exception e) {
			Log.e(this.TAG, "[rawQuery] from DB Exception.");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}

		return list;
	}

	public boolean isExist(String sql, String[] selectionArgs) {
		Log.d(TAG, "[isExist]: " + getLogSql(sql, selectionArgs));

		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			cursor = db.rawQuery(sql, selectionArgs);
			if (cursor.getCount() > 0) {
				return true;
			}
		} catch (Exception e) {
			Log.e(this.TAG, "[isExist] from DB Exception.");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return false;
	}

	public List<T> find() {
		return find(null, null, null, null, null, null, null);
	}

	public T find(String id) {
		List<T> find = find(null, this.idColumn + " = ?", new String[] { id }, null, null, null, null);
		if (find == null) {
			return null;
		}
		
		Log.i(TAG, "[find by id count ]"+find.size());
		for (T t : find) {
			return t;
		}
		
		return null;
	}

	public List<T> find(String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		Log.d(TAG, "[find]");

		List<T> list = new ArrayList<T>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			cursor = db.query(this.tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

			getListFromCursor(list, cursor);
		} catch (Exception e) {
			Log.e(this.TAG, "[find] from DB Exception");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}

		return list;
	}

	private void getListFromCursor(List<T> list, Cursor cursor) throws IllegalAccessException, InstantiationException {
		while (cursor.moveToNext()) {
			T entity = this.clazz.newInstance();

			for (Field field : this.allFields) {
				Column column = null;
				if (field.isAnnotationPresent(Column.class)) {
					column = (Column) field.getAnnotation(Column.class);

					field.setAccessible(true);
					Class<?> fieldType = field.getType();

					int c = cursor.getColumnIndex(column.name());
					if (c < 0) {
						continue;
					} else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
						field.set(entity, cursor.getInt(c));
					} else if (String.class == fieldType) {
						field.set(entity, cursor.getString(c));
					} else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
						field.set(entity, Long.valueOf(cursor.getLong(c)));
					} else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
						field.set(entity, Float.valueOf(cursor.getFloat(c)));
					} else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
						field.set(entity, Short.valueOf(cursor.getShort(c)));
					} else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
						field.set(entity, Double.valueOf(cursor.getDouble(c)));
					} else if (Date.class == fieldType) {
						Date date = new Date();
						date.setTime(cursor.getLong(c));
						field.set(entity, date);
					} else if (Blob.class == fieldType) {
						field.set(entity, cursor.getBlob(c));
					} else if (Character.TYPE == fieldType) {
						String fieldValue = cursor.getString(c);

						if ((fieldValue != null) && (fieldValue.length() > 0)) {
							field.set(entity, Character.valueOf(fieldValue.charAt(0)));
						}
					}
				}
			}

			list.add((T) entity);
		}
	}

	public long insert(T entity) {
		return insert(entity, true);
	}

	public void insert(List<T> entry) {
		if (entry != null)
			for (int i = 0; i < entry.size(); i++) {
				insert(entry.get(i));
			}
	}

	public SQLiteDatabase getWirtableDataBase() {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		return db;
	}

	public long beginTransactionInseart(List<T> list) {
		SQLiteDatabase db = getWirtableDataBase();
		db.beginTransaction();
		long insertReturn = -1;
		for (T t : list) {
			insertReturn = insert(db, t, true);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return insertReturn;
	}

	/**
	 * 可以事物提交，但是的插入后执行关闭close();
	 * 
	 * @param db
	 * @param entity
	 * @param flag
	 * @return
	 */
	public long insert(SQLiteDatabase db, T entity, boolean flag) {
		String sql = "";
		try {
			ContentValues cv = new ContentValues();
			if (flag) {
				sql = setContentValues(entity, cv, TYPE_INCREMENT, METHOD_INSERT);
			} else {
				sql = setContentValues(entity, cv, TYPE_NOT_INCREMENT, METHOD_INSERT);
			}
			Log.d(TAG, "[insert]: insert into " + this.tableName + " " + sql);
			long row = db.insert(this.tableName, null, cv);
			return row;
		} catch (Exception e) {
			Log.d(this.TAG, "[insert] into DB Exception.");
			e.printStackTrace();
		}

		return -1;
	}

	public long insert(T entity, boolean flag) {
		String sql = "";
		SQLiteDatabase db = null;
		try {
			db = getWirtableDataBase();
			ContentValues cv = new ContentValues();
			if (flag) {
				sql = setContentValues(entity, cv, TYPE_INCREMENT, METHOD_INSERT);// id����
			} else {
				sql = setContentValues(entity, cv, TYPE_NOT_INCREMENT, METHOD_INSERT);// id��ָ��
			}
			Log.d(TAG, "[insert]: insert into " + this.tableName + " " + sql);
			long row = db.insert(this.tableName, null, cv);
			return row;
		} catch (Exception e) {
			Log.d(this.TAG, "[insert] into DB Exception.");
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}

		return -1;
	}

	public void delete() {

		SQLiteDatabase db = getWirtableDataBase();

		Log.d(TAG, "[delete]: delelte from " + this.tableName);

		db.delete(this.tableName, null, null);
		db.close();
	}

	public void delete(String id) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		String where = this.idColumn + " = ?";
		String[] whereValue = { id };

		Log.d(TAG, "[delete]: delelte from " + this.tableName + " where " + where.replace("?", id));

		db.delete(this.tableName, where, whereValue);
		db.close();
	}

	public void delete(String... ids) {
		if (ids.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ids.length; i++) {
				sb.append('?').append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			String sql = "delete from " + this.tableName + " where " + this.idColumn + " in (" + sb + ")";

			Log.d(TAG, "[delete]: " + getLogSql(sql, ids));

			db.execSQL(sql, (Object[]) ids);
			db.close();
		}
	}

	public void update(T entity) {
		SQLiteDatabase db = null;
		try {
			db = this.dbHelper.getWritableDatabase();
			ContentValues cv = new ContentValues();

			String sql = setContentValues(entity, cv, TYPE_NOT_INCREMENT, METHOD_UPDATE);

			String where = this.idColumn + " = ?";
			String id = cv.get(this.idColumn).toString();
			// int id = Integer.parseInt(cv.get(this.idColumn).toString());
			cv.remove(this.idColumn);

			Log.d(TAG, "[update]: update " + this.tableName + " set " + sql + " where " + where.replace("?", id));

			String[] whereValue = { id };
			db.update(this.tableName, cv, where, whereValue);
		} catch (Exception e) {
			Log.d(this.TAG, "[update] DB Exception.");
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
	}

	private String setContentValues(T entity, ContentValues cv, int type, int method) throws IllegalAccessException {
		StringBuffer strField = new StringBuffer("(");
		StringBuffer strValue = new StringBuffer(" values(");
		StringBuffer strUpdate = new StringBuffer(" ");
		for (Field field : this.allFields) {
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			Column column = (Column) field.getAnnotation(Column.class);

			field.setAccessible(true);
			Object fieldValue = field.get(entity);
			if (fieldValue == null)
				continue;
			if ((type == TYPE_INCREMENT) && (field.isAnnotationPresent(Id.class))) {
				continue;
			}
			if (Date.class == field.getType()) {
				// 2012-06-10
				cv.put(column.name(), ((Date) fieldValue).getTime());
				continue;
			}
			String value = String.valueOf(fieldValue);
			cv.put(column.name(), value);
			if (method == METHOD_INSERT) {
				strField.append(column.name()).append(",");
				strValue.append("'").append(value).append("',");
			} else {
				strUpdate.append(column.name()).append("=").append("'").append(value).append("',");
			}

		}
		if (method == METHOD_INSERT) {
			strField.deleteCharAt(strField.length() - 1).append(")");
			strValue.deleteCharAt(strValue.length() - 1).append(")");
			return strField.toString() + strValue.toString();
		} else {
			return strUpdate.deleteCharAt(strUpdate.length() - 1).append(" ").toString();
		}
	}

	public List<Map<String, String>> query2MapList(String sql, String[] selectionArgs) {
		Log.d(TAG, "[query2MapList]: " + getLogSql(sql, selectionArgs));
		SQLiteDatabase db = null;
		Cursor cursor = null;
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		try {
			db = this.dbHelper.getReadableDatabase();
			cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				for (String columnName : cursor.getColumnNames()) {
					int c = cursor.getColumnIndex(columnName);
					if (c < 0) {
						continue; // ���������ѭ���¸�����ֵ
					} else {
						map.put(columnName.toLowerCase(), cursor.getString(c));
					}
				}
				retList.add(map);
			}
		} catch (Exception e) {
			Log.e(TAG, "[query2MapList] from DB exception");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}

		return retList;
	}

	public void execSql(String sql, Object[] selectionArgs) {
		SQLiteDatabase db = null;
		Log.d(TAG, "[execSql]: " + getLogSql(sql, selectionArgs));
		try {
			db = this.dbHelper.getWritableDatabase();
			if (selectionArgs == null) {
				db.execSQL(sql);
			} else {
				db.execSQL(sql, selectionArgs);
			}
		} catch (Exception e) {
			Log.e(TAG, "[execSql] DB exception.");
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private String getLogSql(String sql, Object[] args) {
		if (args == null || args.length == 0) {
			return sql;
		}
		for (int i = 0; i < args.length; i++) {
			sql = sql.replaceFirst("\\?", "'" + String.valueOf(args[i]) + "'");
		}
		return sql;
	}

	public String getTableName() {
		return tableName;
	}
}