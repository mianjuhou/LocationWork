package com.jtv.locationwork.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jtv.locationwork.entity.ItemWoListAttribute;
import com.jtv.locationwork.entity.ItemWonum;
import com.jtv.locationwork.entity.WoListFiledJson;
/**
 * 查表得查表映射json
 *<p>
 *
 * @author 更生
 * @version 2016年4月11日
 */
public class WonumUtil {

	public static List<ItemWonum> swithcLists(JSONArray data) {

		List<ItemWonum> mlist = new ArrayList<ItemWonum>();

		if (data == null) {
			return mlist;
		}

		String filed = SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.WONUM_TABLE_FIELD, "");// 获取到对应的表字段

		List<WoListFiledJson> parseArray = com.alibaba.fastjson.JSON.parseArray(filed, WoListFiledJson.class);

		for (int i = 0; i < data.length(); i++) {

			JSONObject jsonObject = null;

			try {
				jsonObject = data.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				return mlist;
			}

			ItemWonum mItemWonum = new ItemWonum();

			ItemWoListAttribute mItemAttri = null;

			for (WoListFiledJson woListFiledBean : parseArray) {// 每个条目有很多属性

				String tabl = woListFiledBean.getTablename();

				tablename = jsonObject.optString(Constants.JSON_KEY_TABLENAME);

				if (TextUtil.isEmpty(tablename)) {// 查询的那张表
					tablename = Constants.WONUM_TABLE;
				}

				if (!tablename.equals(tabl)) {// 不是这张表的字段
					continue;
				}

				mItemAttri = new ItemWoListAttribute();

				String coln = woListFiledBean.getColn();

				String name = woListFiledBean.getName();

				String flag = woListFiledBean.getFlag();

				String datatype = woListFiledBean.getDatatype();

				String colnValue = jsonObject.optString(coln);

				mItemAttri.setDisPlayname(name);
				mItemAttri.setDisValue(colnValue);
				mItemAttri.setFlag(flag);
				mItemAttri.setColn(coln);

				mItemAttri.setDatatype(datatype);

				mItemWonum.addAttribute(mItemAttri);

			}

			String type = jsonObject.optString("type");//这两个字段后台做了隐藏

			if (!TextUtil.isEmpty(type)) {

				ItemWoListAttribute attr = new ItemWoListAttribute();

				attr.setColn("type");
				attr.setDisValue(type);

				mItemWonum.addAttribute(attr);

			}
			String isimportant = jsonObject.optString("isimportant");//这个字段后台做了隐藏,显示是否是重点行车
			
			if (!TextUtil.isEmpty(isimportant)) {
				
				ItemWoListAttribute attr = new ItemWoListAttribute();
				
				attr.setColn("isimportant");
				attr.setDisValue(isimportant);
				
				mItemWonum.addAttribute(attr);
				
			}
			
			if (!TextUtil.isEmpty(tablename)) {

				ItemWoListAttribute attr = new ItemWoListAttribute();

				attr.setColn(Constants.JSON_KEY_TABLENAME);
				attr.setDisValue(tablename);

				mItemWonum.addAttribute(attr);

			}

			mlist.add(mItemWonum);

		}

		return mlist;
	}

	private static String tablename;

	/**
	 * 
	 * @param filed
	 *            动态字段
	 * @param data
	 *            原数据
	 * @return 集合
	 * @throws JSONException
	 */
	// public static List<ItemWoList> swithcList(JSONArray data) {
	// List<ItemWoList> mlist = new ArrayList<ItemWoList>();
	// if (data == null) {
	// return mlist;
	// }
	// String filed =
	// SpUtiles.BaseInfo.mbasepre.getString(CoustomKey.WONUM_TABLE_FIELD, "");//
	// 获取到对应的表字段
	// List<WoListFiledJson> parseArray =
	// com.alibaba.fastjson.JSON.parseArray(filed, WoListFiledJson.class);
	// // 解析数据
	// for (int i = 0; i < data.length(); i++) {
	// JSONObject jsonObject = null;
	//
	// try {
	// jsonObject = data.getJSONObject(i);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// return mlist;
	// }
	//
	// // List<WoListFiledJson> unKnowParseDate =
	// // unKnowParseDate(jsonObject);
	//
	// // if (unKnowParseDate != null) {
	// // parseArray = unKnowParseDate;
	// // }
	// if (parseArray == null) {
	// continue;
	// }
	//
	// ItemWoList itemWoList = new ItemWoList();
	// ItemWoListAttribute itemAttribute = null;
	// HashMap<String, String> core = new HashMap<String, String>();
	//
	// for (WoListFiledJson woListFiledBean : parseArray) {// 每个条目有很多属性
	//
	// String tabl = woListFiledBean.getTablename();
	//
	// tablename = jsonObject.optString(Constants.JSON_KEY_TABLENAME);
	//
	// if (TextUtil.isEmpty(tablename)) {// 查询的那张表
	// tablename = Constants.WONUM_TABLE;
	// }
	//
	// if (!tablename.equals(tabl)) {// 不是这张表的字段
	// continue;
	// }
	// itemAttribute = new ItemWoListAttribute();
	// String coln = woListFiledBean.getColn();
	//
	// String name = woListFiledBean.getName();
	// String flag = woListFiledBean.getFlag();
	// String datatype = woListFiledBean.getDatatype();
	//
	// String colnValue = jsonObject.optString(coln);
	//
	// // name = jsonObject.optString(name);
	// // flag = jsonObject.optString(flag);
	// // datatype = jsonObject.optString(datatype);
	//
	// // itemWoList = new ItemWoList().ite;
	//
	// setField(flag, coln, colnValue, name, core, tablename);
	// itemAttribute.setDisPlayname(name);
	// itemAttribute.setDisValue(colnValue);
	// itemAttribute.setFlag(flag);
	//
	// itemAttribute.setDatatype(datatype);
	// itemWoList.addItem(itemAttribute);
	//
	// }
	//
	// String type = jsonObject.optString("type");
	//
	// core.put(Constants.JSON_KEY_TABLENAME, tablename);
	//
	// if (!"".equals(type) && type != null) {
	// core.put("type", type);
	// }
	//
	// itemWoList.setField(core);
	// mlist.add(itemWoList);
	// }
	// return mlist;
	// }

	/**
	 * 
	 * @param flag
	 *            时间字段标记
	 * @param coln
	 *            字段键
	 * @param colnValue
	 *            字段值
	 * @param disPlayName
	 *            显示字段名
	 * @param coreField
	 *            容器
	 * @return
	 */
	private static HashMap<String, String> setField(String flag, String coln, String colnValue, String disPlayName,
			HashMap<String, String> coreField, String tablename) {

		if ("1".equals(flag)) { // 开始时间

			coreField.put(Constants.START_TIME, colnValue);
			coreField.put(Constants.DIS_START_TIME, disPlayName);

		} else if ("2".equals(flag)) { // 结束时间

			coreField.put(Constants.END_TIME, colnValue);
			coreField.put(Constants.DIS_END_TIME, disPlayName);

		} else if (Constants.JSON_KEY_WONUM.equals(coln)) {

			coreField.put(Constants.JSON_KEY_WONUM, colnValue);
			coreField.put(Constants.DIS_JSON_KEY_WONUM, disPlayName);

		} else if (Constants.JSON_KEY_PLANNAME.equals(coln)) {

			coreField.put(Constants.WONUM_TYPE, colnValue);
			coreField.put(Constants.DIS_WONUM_TYPE, disPlayName);

		} else if (Constants.JSON_KEY_LEAD.equals(coln)) {

			coreField.put(Constants.JSON_KEY_WONUM, colnValue);
			coreField.put(Constants.DIS_JSON_KEY_WONUM, disPlayName);

		} else if (Constants.WONUM_TYPE.equals(coln)) {// 大中修施工,大阶段施工

			coreField.put(Constants.WONUM_TYPE, colnValue);
			coreField.put(Constants.DIS_WONUM_TYPE, disPlayName);

		} else {

			if (coreField.size() < 6 && !TextUtil.isEmpty(colnValue)) {// 缓存6个只用做显示
				coreField.put(colnValue, colnValue);
				coreField.put("dis_" + colnValue, disPlayName);
			}

		}

		return coreField;
	}

}
