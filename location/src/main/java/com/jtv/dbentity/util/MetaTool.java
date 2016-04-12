package com.jtv.dbentity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.dao.DBObjectHelper;

import android.database.Cursor;

/**
 * 元数据操作工具类
 * 
 * @author 殷小春
 *
 */
public class MetaTool {

	/**
	 * 得到表的列定义
	 * 
	 * @param _context
	 *            Context引用
	 * @param tname
	 *            表名
	 * @return String数组
	 */
	public static String[] getTableCols(String tname) {
		String[] sb = null;

		DBObjectHelper conn = (DBObjectHelper) DBFactory.getConnection();
		Cursor c = null;
		try {
			c = conn.queryCursor(tname, null, " 1=2 ", null, null, null, null);
		} catch (Exception e) {
		}
		if (c == null)
			return null;

		sb = c.getColumnNames();
		try {
			c.close();
		} catch (Exception ee) {
		}

		try {
			conn.close();
		} catch (Exception ee) {
		}

		return sb;
	}

	/**
	 * Cursor转换成数组
	 * 
	 * @param cursor
	 *            Cursor对象
	 * @return String二维数组
	 * @throws Exception
	 */
	public static String[][] CursorToArray(Cursor cursor) throws Exception {
		if (cursor == null)
			return null;
		String[] col = cursor.getColumnNames();
		String[][] data = new String[cursor.getCount()][col.length];
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			for (int j = 0; j < col.length; j++) {
				try {
					data[i][j] = cursor.getString(j) == null ? "" : cursor.getString(j);
				} catch (Exception e) {
					if (e.getMessage().contains("BLOB")) {
						data[i][j] = "[BLOB]";
					} else {
						throw e;
					}
				}
			}
		}
		try {
			cursor.close();
		} catch (Exception ee) {
		}
		return data;
	}

	/**
	 * cursor转换成list结构
	 * 
	 * @param cursor
	 *            Cursor对象
	 * @return List<Map>对象
	 */
	public static List<HashMap<String, String>> CursorToList(Cursor cursor) {
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		if (cursor == null)
			return data;
		String[] col = cursor.getColumnNames();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			HashMap<String, String> map = new HashMap<String, String>();
			for (int j = 0; j < col.length; j++) {
				try {
					// map.put(col[j].toLowerCase(),
					// cursor.getString(j)==null?"":cursor.getString(j));
					map.put(col[j], cursor.getString(j) == null ? "" : cursor.getString(j));
				} catch (Exception e) {
				}
			}
			data.add(map);
		}
		try {
			cursor.close();
		} catch (Exception ee) {
		}
		return data;
	}
}
