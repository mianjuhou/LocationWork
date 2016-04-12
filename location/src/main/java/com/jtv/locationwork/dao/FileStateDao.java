package com.jtv.locationwork.dao;

import com.jtv.dbentity.dao.BaseDaoImpl;
import com.jtv.locationwork.entity.DBFileState;

import android.content.Context;

public class FileStateDao extends BaseDaoImpl<DBFileState> {
	public FileStateDao(Context con) {
		super(new DBObjectHelper(con), DBFileState.class);
	}

}
