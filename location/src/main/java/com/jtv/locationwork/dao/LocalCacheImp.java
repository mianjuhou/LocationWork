package com.jtv.locationwork.dao;

import com.jtv.dbentity.dao.BaseDaoImpl;
import com.jtv.locationwork.entity.LocalCache;

import android.content.Context;

public class LocalCacheImp extends BaseDaoImpl<LocalCache> {


	public LocalCacheImp(Context con) {
		super(new DBObjectHelper(con), LocalCache.class);
	}

}
