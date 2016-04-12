package com.jtv.locationwork.imp;

import java.util.HashMap;
import java.util.Map;

import com.jtv.locationwork.entity.LocalCache;
import com.jtv.locationwork.listener.CacheHttp;

public class CacheFace implements CacheHttp{

	@Override
	public String commitGetData(LocalCache cache) {
		
		return null;
	}

	@Override
	public Map<String, String> commitPostData(LocalCache cache) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if(cache!=null){
			hashMap.put(cache.getKey(), cache.getValue());
			hashMap.put(cache.getKey1(), cache.getValue1());
			hashMap.put(cache.getKey2(), cache.getValue2());
			hashMap.put(cache.getKey3(), cache.getValue3());
			hashMap.put(cache.getKey4(), cache.getValue4());
			hashMap.put(cache.getKey5(), cache.getValue5());
			hashMap.put(cache.getKey6(), cache.getValue6());
//			hashMap.put(cache.getKey7(), cache.getValue7());
			return hashMap;
		}
		return null;
	}

}
