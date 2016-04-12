package com.jtv.locationwork.util;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
/**
 * 保存了key的修改时间
 * 当不需要用到这个对象建议unregistlistener
 * @author beyound
 * @email  whatl@foxmail.com
 * @date   2015年7月8日
 */
public class LDateSharedPreferce implements SharedPreferences, SharedPreferences.OnSharedPreferenceChangeListener {
	private SharedPreferences sharedPreferences;
	private String config_name_time ="key_update_info_json_config";
	private String last_date_add ="last_date_add_0x0";
	private String last_key_add ="last_key_add_0x0";
	private String last_key_remove  ="last_key_remove_0x0";
	private String last_date_remove ="last_date_remove_0x0";
	private Context con;
	private String name;
	public LDateSharedPreferce(Context con,String name ,int mode) {
		this.con=con;
		this.name=name;
		sharedPreferences = con.getSharedPreferences(name, mode);
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public Map<String, ?> getAll() {
		return sharedPreferences.getAll();
	}

	@Override
	public String getString(String key, String defValue) {
		return sharedPreferences.getString(key, defValue);
	}

	@SuppressLint("NewApi")
	@Override
	public Set<String> getStringSet(String key, Set<String> defValues) {
		return sharedPreferences.getStringSet(key, defValues);
	}

	@Override
	public int getInt(String key, int defValue) {
		return sharedPreferences.getInt(key, defValue);
	}

	@Override
	public long getLong(String key, long defValue) {
		return sharedPreferences.getLong(key, defValue);
	}

	@Override
	public float getFloat(String key, float defValue) {
		return sharedPreferences.getFloat(key, defValue);
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		return sharedPreferences.getBoolean(key, defValue);
	}

	@Override
	public boolean contains(String key) {
		return sharedPreferences.contains(key);
	}

	@Override
	public Editor edit() {
		return sharedPreferences.edit();
	}
	/**
	 * 获取最后的一个key添加时间
	 * @return
	 */
	public ShparedBean getLastAddTime(){
		String string = sharedPreferences.getString(config_name_time, "");
		if(string==null||"".equals(string)){
			return null;
		}
		try {
			JSONObject jsonObject = new JSONObject(string);
			long date = jsonObject.optLong(last_date_add);
			String key = jsonObject.optString(last_key_add);
			return new ShparedBean(date,key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		 return null;
	}
	/**
	 * 获取最后的一个key添加时间
	 * @return
	 */
	public ShparedBean getLastRemoveTime(){
		String string = sharedPreferences.getString(config_name_time, "");
		if(string==null||"".equals(string)){
			return null;
		}
		try {
			JSONObject jsonObject = new JSONObject(string);
			long date = jsonObject.optLong(last_date_remove);
			String key = jsonObject.optString(last_key_remove);
			return new ShparedBean(date,key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		 return null;
	}
	/**
	 * 获取文件的最后的修改时间
	 * @return －1 代表获取失败 正常返回时间的正常毫秒值
	 */
	public long lastFileDate(){
		File file = getFile();
		
		if(file!=null&&file.exists()){
			return file.lastModified();
		}
		return -1;
	}
	/**
	 * 获取到xml文件
	 * @return
	 */
	public File getFile(){
		File file= new File(con.getFilesDir().getParent()+"/shared_prefs",name+".xml");
		return file;
	}
	@Override
	public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
		sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
	}

	public long getLastDate(String key, long time) {
		boolean contains = sharedPreferences.contains(config_name_time);
		if (!contains)
			return time;
		String value = sharedPreferences.getString(config_name_time, "");
		if (value==null||"".equals(value)) {
			return time;
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(value);
			boolean has = jsonObject.has(key);
			if (!has) {
				return time;
			}
			return jsonObject.getLong(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return time;
	}
	/**
	 * 当移除一个已经存在的key会调用此方法
	 * 当添加一个key,key的值和上一次的值发生变化才会调用 
	 * 当clear的时候不会调用
	 * 被getsharedpreference() 共享同一个
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(config_name_time.equalsIgnoreCase(key)){//如果是这个key更新就不触发事件
			return;
		}
		boolean contains = sharedPreferences.contains(key);// 代表是添加
		if (contains) {
			Editor edit = sharedPreferences.edit();
			String value = sharedPreferences.getString(config_name_time, "");
			JSONObject json = null;
			try {
				if (value==null||"".equals(value)) {// 为空就什么不做
					json = new JSONObject();
				} else {
					json = new JSONObject(value);
				}
				long time = new Date().getTime();
				json.put(last_date_add, time);
				json.put(last_key_add, key);
				json.put(key, time);
				edit.putString(config_name_time, json.toString());
				edit.commit();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{//代笔移除这个key
			Editor edit = sharedPreferences.edit();
			String value = sharedPreferences.getString(config_name_time, "");
			JSONObject json = null;
			try {
				if (value==null||"".equals(value)) {// 为空就什么不做
					return;
				} else {
					json = new JSONObject(value);
				}
				long time = new Date().getTime();
				
				json.put(last_date_remove, time);
				json.put(last_key_remove, key);
				boolean has = json.has(key);
				if(has){
					json.remove(key);
					edit.putString(config_name_time, json.toString());
					edit.commit();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
		}
	}
	public class ShparedBean{
		public ShparedBean(long time,String key){
			this.time=time;
			this.key=key;
		}
	private long time;
	private String key;
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	}
}
