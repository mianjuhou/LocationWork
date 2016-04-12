package com.jtv.locationwork.listener;

import java.util.Map;

import com.jtv.locationwork.entity.LocalCache;

/**
 * 离线提交数据
 * @author beyound
 *
 */
public interface CacheHttp {

	public String commitGetData(LocalCache cache);
	public Map<String, String>  commitPostData(LocalCache cache);
	
}
