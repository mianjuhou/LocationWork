package com.jtv.locationwork.imp;

import java.util.Map;

import com.jtv.locationwork.entity.LocalCache;
import com.jtv.locationwork.listener.CacheHttp;

/**
 * 提交缓存机具数据
 * @author beyound
 *
 */
public class CacheTool implements CacheHttp {

	@Override
	public String commitGetData(LocalCache cache) {
		return cache.getIp() + "&obj=" + cache.getKey();
	}

	@Override
	public Map<String, String> commitPostData(LocalCache cache) {

		return null;
	}

}
