package com.jtv.locationwork.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jtv.dbentity.dao.BaseDaoImpl;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.httputil.AnsynHttpRequest;
import com.jtv.locationwork.httputil.ObserverCallBack;
import com.jtv.locationwork.listener.CacheHttp;

import android.content.Context;
import android.text.TextUtils;

/**
 * 从本地数据库提交到数据到服务器
 * 
 * @author beyound
 *
 */
public class CacheUtil {
	private static CacheUtil cache;
	private static Context con;
	private List<LocalCache> data;
	private static ObserverCallBack back;

	private CacheUtil() {
	};

	/**
	 * 只能获取一次
	 * 
	 * @param con
	 * @return
	 */
	public static CacheUtil getInstance(Context con) {

		if (cache == null) {
			cache = new CacheUtil();
		} else {
			return null;
		}
		setObserverCallBack(null);
		CacheUtil.con = con;
		return cache;

	}

	/**
	 * 获取到需要提交的数据
	 */
	public void get() {
		BaseDaoImpl<LocalCache> localCacheDao = DBFactory.getLocalCacheDao(con);
		data = localCacheDao.find();
	}

	public void start() {
		if(data==null){
			data= new ArrayList<LocalCache>();
		}
		for (int i = 0; i < data.size(); i++) {
			LocalCache localCache = data.get(i);
			
			String path = localCache.getClasspath();
			
			try {
				
				Class<CacheHttp> forName = (Class<CacheHttp>) Class.forName(path);
				
				CacheHttp newInstance = forName.newInstance();
				
				String get = newInstance.commitGetData(localCache);
				int cache_id = localCache.getCache_id();
				
				Map<String, String> post = newInstance.commitPostData(localCache);

				if (get != null&&!TextUtils.isEmpty(get)) {
					
					AnsynHttpRequest.requestGet(get, back, 1, con, cache_id,false);
					
				} else if (post != null) {
					
					AnsynHttpRequest.requestPost(localCache.getIp(), back, 1, con, cache_id, post,false);
					
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		data.clear();
	}

	private static void setObserverCallBack(ObserverCallBack back) {
		CacheUtil.back = back;
		
		if (CacheUtil.back == null) {
			CacheUtil.back = new CacheObserverCallBack();
		}
	}

	private static class CacheObserverCallBack implements ObserverCallBack {

		@Override
		public void back(String data, int method, Object obj) {
				
				BaseDaoImpl<LocalCache> localCacheDao = DBFactory.getLocalCacheDao(con);
				int id = (Integer) obj;
				
				localCacheDao.delete(id+"");
		}

		@Override
		public void badBack(String error, int method, Object obj) {
			
		}

	}

}
