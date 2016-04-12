package com.jtv.locationwork.dao;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;

/**
 * @author: zn E-mail:zhangn@jtv.com.cn
 * @version:2015-1-17 类说明:数据库操作类
 */

public class DBHelperDao {
	
	private String departTbl = "departtbl";// 段、车间、工区表

	/**
	 * 流水号
	 * 
	 * @author:zn
	 * @version:2015-1-19
	 * @return
	 */
	public String getGUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 更具部门获取到部门的数据比如车间获取的是车间
	 * 
	 * @param bumen
	 *            部门编号0-3
	 * @return
	 */
	public List<HashMap<String, String>> forDepartLevel(String bumen) {
		List<HashMap<String, String>> queryList = DBFactory.getConnection().queryList(departTbl, new String[] {},
				"departlevel=?", new String[] { bumen }, null, null, null);
		return queryList;
	}

	/**
	 * 更具部门和段别获取车间数据
	 * 
	 * @param departid
	 *            段
	 * @param bumen
	 *            部门
	 * @return
	 */
	public List<HashMap<String, String>> forDepartLevelAndDepartid(String departid, String bumen) {
		List<HashMap<String, String>> queryList = DBFactory.getConnection().queryList(departTbl, new String[] {},
				"departlevel=? and departid = ?", new String[] { bumen, departid }, null, null, null);
		return queryList;
	}

	/**
	 * 查询车间数据更具段别和部门号
	 * 
	 * @param id
	 * @param bumen
	 * @return
	 */
	public List<HashMap<String, String>> forSiteidAndDepartLevel(String id, String bumen) {
		List<HashMap<String, String>> queryList = DBFactory.getConnection().queryList(departTbl, null,
				"siteid=? and departlevel = ?", new String[] { id, bumen }, null, null, null);
		return queryList;
	}
	/**
	 * 通过siteid 和 departid 来确定修改那一行的departname 的名字
	 * @param name 修改的名字
	 * @param id siteid
	 * @param departid departid
	 * @return
	 */
	public int updateSiteidName(String name,String id, String departid){
		ContentValues contentValues = new ContentValues();
		contentValues.put("departname", name);
		return DBFactory.getConnection().update(departTbl,"siteid=? and departid = ?", new String[] { id, departid }, contentValues);
	}
	/**
	 * 查询车间数据更具段别和部门号
	 * 
	 * @param id
	 * @param bumen
	 * @return
	 */
	public List<HashMap<String, String>> forDepartidAndDepartLevel(String id, String bumen) {
		List<HashMap<String, String>> queryList = DBFactory.getConnection().queryList(departTbl, null,
				"departid=? and departlevel = ?", new String[] { id, bumen }, null, null, null);
		return queryList;
	}

	/**
	 * 更具部门和上级车间superdept获取数据
	 * 
	 * @param superdepart
	 * @param bumen
	 * @return
	 */
	public List<HashMap<String, String>> forDeaprtLevelAndSuperDepart(String superdepart, String bumen) {
		List<HashMap<String, String>> queryList = DBFactory.getConnection().queryList(departTbl, new String[] {},
				"departlevel=? and superdepart=?", new String[] { bumen, superdepart }, null, null, null);
		return queryList;
	}

	public String forDepartID(String departid) {
		try {
			List<HashMap<String, String>> queryList = DBFactory.getConnection().queryList(departTbl, new String[] {},
					"departid = ?", new String[] { departid }, null, null, null);
			for (int i = 0; i < queryList.size(); i++) {
				HashMap<String, String> hashMap = queryList.get(i);
				return hashMap.get("departlevel");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		return "2";
	}
}
