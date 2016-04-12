package com.jtv.locationwork.dao;

import com.jtv.dbentity.dao.BaseDaoImpl;
import com.jtv.locationwork.entity.CheckEntity;

import android.content.Context;

public class CheckEntityImp extends BaseDaoImpl<CheckEntity> {

	public CheckEntityImp(Context con) {
		super(new DBObjectHelper(con), CheckEntity.class);
	}

}