package com.jtv.locationwork.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;

public class DateBaseUtil {
	
	
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
					String value = cursor.getString(j);
					
					
					map.put(col[j], value == null ? "" : value);
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
